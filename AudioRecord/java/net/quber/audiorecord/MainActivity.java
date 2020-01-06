package net.quber.audiorecord;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteOrder;
import java.util.Date;

import vendor.quber.hardware.aecanalysis.V1_0.AECAnalysisdData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import net.quber.waveformview.WaveFormView;


public class MainActivity extends AppCompatActivity implements StorageNotificationListener {
    private final String TAG = "AudioRecord";
    public WaveFormView[] mMICWaveView;
    private WaveFormView[] mRefWaveView;
    public WaveFormView[] mWaveFormViews;
    public LinearLayout[] mLinearLayouts;
    public TextView[] mTextViews;
    private WaveFormView mOutWaveView;
    private Button mBt_recorder;
    private Button mBt_stop;
    private Button mBt_setting;
    private Button mBt_play;
    private ImageView mRecord;
    private ImageView mStop;
    public static float DB10 = 3.1623f;
    public static float DB20 = 10f;
    public static float DB30 = 31.6228f;

    private ArrayList<Short> mMIC1Data = new ArrayList<>();
    private ArrayList<Short> mMIC2Data = new ArrayList<>();
    private ArrayList<Short> mMIC3Data = new ArrayList<>();
    private ArrayList<Short> mMIC4Data = new ArrayList<>();
    private ArrayList[] mMICData = new ArrayList[]{mMIC1Data, mMIC2Data, mMIC3Data, mMIC4Data};
    private ArrayList<Short> mRef1Data = new ArrayList<>();
    private ArrayList<Short> mRef2Data = new ArrayList<>();
    private ArrayList[] mRefData = new ArrayList[]{mRef1Data, mRef2Data};
    private ArrayList<Short> mOutData = new ArrayList<>();

    public static int MICROPHONE = 1;
    public static int REFERENCE = 2;
    public static int ECHO_CANCELED = 3;

    public static final String mPCMDataPath = "/data/vendor/aec/";


    private static final int REQUEST_RECORD_AUDIO = 13;

    private Handler mPcmDataHandler;
    private Handler mFileDumpHandler;
    private HandlerThread mFileDumpThread = new HandlerThread("filedumpthread");
    private SimpleDateFormat mFormat;

    private DialogSettingOption mDialog;
    private FileOutputStream mDumpFileOutPutStream;
    private FileOutputStream mDumpFileMICPutStream;
    private FileOutputStream mDumpFileRefPutStream;
    private PcmDataPlayer mPcmplayer;
    private SettingDialog mSettingDialog;


    @Override
    public void noticeLackOfStorage() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplication().getApplicationContext(), "저장용량이 부족합니다.", Toast.LENGTH_SHORT).show();
                mRecord.setImageResource(R.drawable.img_record_default);
                mStop.setImageResource(R.drawable.img_stop_activation);
                changeColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWaveFormDefault),
                        ContextCompat.getColor(getApplicationContext(), R.color.colorWaveFormOutDefault));
                PCMDataManager.getInstance().destroyFileDumpHandler(); // stop filedump
                fileClose();
            }
        });
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.ui);
        initUI();
        mPcmplayer = new PcmDataPlayer(this);
        mFormat = new SimpleDateFormat("MMddHHmmss");
        mFileDumpThread.start();
        mFileDumpHandler = new Handler(mFileDumpThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                ArrayList<AECAnalysisdData> dataList = (ArrayList<AECAnalysisdData>) msg.obj;
                dumpPcmData(dataList);
            }
        };
        mPcmDataHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ArrayList<AECAnalysisdData> dataList = (ArrayList<AECAnalysisdData>) msg.obj;
                parsingPcmData(dataList);
            }
        };

        startAudioRecordingSafe();
        mBt_recorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StorageManager.getInstance().setStorageManagerListener(MainActivity.this);
                if (StorageManager.getInstance().checkStorage()) {
                    if (mDumpFileOutPutStream != null && mDumpFileMICPutStream != null
                            && mDumpFileRefPutStream != null) {
                        return;
                    }
                    mRecord.setImageResource(R.drawable.img_record_activation);
                    mStop.setImageResource(R.drawable.img_stop_default);
                    changeColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWaveFormRecording),
                            ContextCompat.getColor(getApplicationContext(), R.color.colorWaveFormOutRecording));
                    long now = System.currentTimeMillis();
                    Date time = new Date(now);
                    String folderName = mFormat.format(time);
                    mkdir(mPCMDataPath + folderName);
                    try {
                        mDumpFileOutPutStream = new FileOutputStream(mPCMDataPath + folderName + "/out.pcm");
                        mDumpFileMICPutStream = new FileOutputStream(mPCMDataPath + folderName + "/mic.pcm");
                        mDumpFileRefPutStream = new FileOutputStream(mPCMDataPath + folderName + "/ref.pcm");
                    } catch (FileNotFoundException e) {
                        Toast.makeText(getApplication().getApplicationContext(), "파일을 저장할 수 없습니다.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    PCMDataManager.getInstance().setFileDumpHandler(mFileDumpHandler); // start filedump
                }
            }
        });

        mBt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecord.setImageResource(R.drawable.img_record_default);
                mStop.setImageResource(R.drawable.img_stop_activation);
                changeColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWaveFormDefault),
                        ContextCompat.getColor(getApplicationContext(), R.color.colorWaveFormOutDefault));
                PCMDataManager.getInstance().destroyFileDumpHandler(); // stop filedump
                fileClose();
            }
        });

        mBt_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSettingDialog.show();
            }
        });

        mBt_setting.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mBt_setting.setTextColor(Color.parseColor("#25e48b"));
                } else {
                    mBt_setting.setTextColor(Color.parseColor("#4e4c4e"));
                }
            }
        });

        mBt_play.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mBt_play.setTextColor(Color.parseColor("#1d9ffc"));
                } else {
                    mBt_play.setTextColor(Color.parseColor("#4e4c4e"));
                }
            }
        });

        mBt_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBt_play.getText().equals("PLAY")) {
                    mBt_play.setText("PLAYING");
                } else {
                    mBt_play.setText("PLAY");
                    Toast.makeText(getApplication().getApplicationContext(), "REPEAT(" + mPcmplayer.isRepeat + ")", Toast.LENGTH_SHORT).show();
                }
                mPcmplayer.pcmDataPlay();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        StorageManager.getInstance().setStorageManagerListener(this);
        PCMDataManager.getInstance().setPCMDataHandler(mPcmDataHandler);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
        StorageManager.getInstance().stopCheckStorage();
        PCMDataManager.getInstance().disConnectService();
        fileClose();
        changeColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWaveFormDefault),
                ContextCompat.getColor(getApplicationContext(), R.color.colorWaveFormOutDefault));
        mRecord.setImageResource(R.drawable.img_record_default);
        mStop.setImageResource(R.drawable.img_stop_activation);
        if (mPcmplayer.isPlaying) {
            mPcmplayer.pcmDataPlay();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        if (mPcmplayer.isPlaying) {
            mPcmplayer.pcmDataPlay();
        }
    }

    private void requestMicrophonePermission() {
        Log.d(TAG, "requestMicrophonePermission resquestPermission");
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                android.Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
    }

    private void startAudioRecordingSafe() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
        } else {
            requestMicrophonePermission();
        }
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_1:
                mPcmplayer.pcmDataPlay();
                break;
            case KeyEvent.KEYCODE_0:
                mPcmplayer.isRepeat = !mPcmplayer.isRepeat;
                Toast.makeText(getApplication().getApplicationContext(), "REPEAT(" + mPcmplayer.isRepeat + ")", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void mkdir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
            Log.d(TAG, "make forder succes, path = " + path);
        } else {
            Log.d(TAG, "make forder fail");
        }
    }

    private void dumpPcmData(ArrayList<AECAnalysisdData> dataList) {
        for (int i = 0; i < dataList.size(); i++) {
            try {
                for (int k = 0; k < 3; k++) {
                    if (mDumpFileRefPutStream != null && mDumpFileOutPutStream != null && mDumpFileMICPutStream != null) {
                        ByteBuffer buffer = ByteBuffer.allocate(2 * dataList.get(i).pcmDatas.get(k).channels
                                * dataList.get(i).pcmDatas.get(k).frames).order(ByteOrder.LITTLE_ENDIAN);
                        for (int j = 0; j < dataList.get(i).pcmDatas.get(k).channels * dataList.get(i).pcmDatas.get(k).frames; j++) {
                            buffer.putShort(dataList.get(i).pcmDatas.get(k).data.get(j));
                        }
                        if (k + 1 == MICROPHONE) {
                            mDumpFileMICPutStream.write(buffer.array());
                        } else if (k + 1 == REFERENCE) {
                            mDumpFileRefPutStream.write(buffer.array());
                        } else if (k + 1 == ECHO_CANCELED) {
                            mDumpFileOutPutStream.write(buffer.array());
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                Toast.makeText(getApplication().getApplicationContext(), "pcm 데이타를 생성할 수 없습니다.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "file not found");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initUI() {
        mMICWaveView = new WaveFormView[]{findViewById(R.id.wave_form_view), findViewById(R.id.wave_form_view2),
                findViewById(R.id.wave_form_view3), findViewById(R.id.wave_form_view4)};
        mRefWaveView = new WaveFormView[]{findViewById(R.id.wave_form_view5), findViewById(R.id.wave_form_view6)};
        mLinearLayouts = new LinearLayout[]{findViewById(R.id.linear1), findViewById(R.id.linear2),
                findViewById(R.id.linear3), findViewById(R.id.linear4),
                findViewById(R.id.linear5), findViewById(R.id.linear6)};
        mTextViews = new TextView[]{findViewById(R.id.Mic1), findViewById(R.id.Mic2), findViewById(R.id.Mic3),
                findViewById(R.id.Mic4), findViewById(R.id.Ref1), findViewById(R.id.Ref2)};
        mWaveFormViews = new WaveFormView[]{findViewById(R.id.wave_form_view), findViewById(R.id.wave_form_view2),
                findViewById(R.id.wave_form_view3), findViewById(R.id.wave_form_view4),
                findViewById(R.id.wave_form_view5), findViewById(R.id.wave_form_view6)};
        mOutWaveView = findViewById(R.id.wave_form_view7);
        mOutWaveView.changeWaveFormColor(ContextCompat.getColor(getApplicationContext(),R.color.colorWaveFormOutDefault));
        for (WaveFormView view : mMICWaveView)
            view.setMICWave(DB10);
        mBt_play = findViewById(R.id.btn_play);
        mBt_recorder = findViewById(R.id.btn_record);
        mBt_stop = findViewById(R.id.btn_stop);
        mBt_setting = findViewById(R.id.btn_setting);
        mRecord = findViewById(R.id.Record);
        mStop = findViewById(R.id.Stop);
        mSettingDialog = new SettingDialog(this);
        mBt_recorder.setFocusableInTouchMode(true);
        mBt_stop.setFocusableInTouchMode(true);
        mBt_recorder.requestFocus();
    }

    private void changeColor(int color, int outcolor) {
        for (WaveFormView view : mMICWaveView) {
            view.changeWaveFormColor(color);
        }
        for (WaveFormView view : mRefWaveView) {
            view.changeWaveFormColor(color);
        }
        mOutWaveView.changeWaveFormColor(outcolor);
    }

    private void fileClose() {
        if (mDumpFileOutPutStream != null) {
            try {
                mDumpFileOutPutStream.close();
                mDumpFileOutPutStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mDumpFileMICPutStream != null) {
            try {
                mDumpFileMICPutStream.close();
                mDumpFileMICPutStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mDumpFileOutPutStream != null) {
            try {
                mDumpFileOutPutStream.close();
                mDumpFileOutPutStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void parsingPcmData(ArrayList<AECAnalysisdData> dataList) {
        int bufferSize = (dataList.get(0).pcmDatas.get(2).data.size()) * dataList.size();
        for (int i = 0; i < dataList.size(); i++) {
            for (int j = 0; j < dataList.get(i).pcmDatas.size(); j++) {
                int channels = dataList.get(i).pcmDatas.get(j).channels;
                for (int k = 0; k < channels; k++) {
                    for (int mic = k; mic < dataList.get(i).pcmDatas.get(j).data.size(); mic += channels) {
                        if (j + 1 == MICROPHONE)
                            mMICData[k].add(dataList.get(i).pcmDatas.get(j).data.get(mic));
                        else if (j + 1 == REFERENCE)
                            mRefData[k].add(dataList.get(i).pcmDatas.get(j).data.get(mic));
                        else if (j + 1 == ECHO_CANCELED)
                            mOutData.add(dataList.get(i).pcmDatas.get(j).data.get(mic));
                    }
                    if (j + 1 == MICROPHONE && mMICData[k].size() >= bufferSize) {
                        mMICWaveView[k].updateAudioData(mMICData[k]);
                        mMICData[k].clear();
                    }
                    if (j + 1 == REFERENCE && mRefData[k].size() >= bufferSize) {
                        mRefWaveView[k].updateAudioData(mRefData[k]);
                        mRefData[k].clear();
                    }
                    if (j + 1 == ECHO_CANCELED && mOutData.size() >= bufferSize) {
                        mOutWaveView.updateAudioData(mOutData);
                        mOutData.clear();
                    }
                }
            }
        }
    }
}