package net.quber.audiocapturetest;

import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    private Button mButtonRecord;
    private Button mButtonPlay;
    private TextView mTextViewTitle;

    //    private int mBufferSize = 256*4*2;
//    private int mBufferSize = 4096;
//    private int mBufferSize = 2048;
    private int mBufferSize = AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

    //    public Thread mRecordThread = null;
    public AudioRecord mAudioRecord = null;
    public boolean isRecording = false;

    //    public Thread mPlayThread = null;
    public AudioTrack mAudioTrack = null;
    public boolean isPlaying = false;
    public boolean isRepeat = false;

    public String mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/record.pcm";
//    public String mFilePath = "/mnt/media_rw/EA18-AC3B/record.pcm";
//    public String mPlayFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/music.pcm";
    public String mPlayFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/music.pcm";
//    public String mPlayFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/record.pcm";

    private MediaPlayer mMediaPlayer;

    private AssetFileDescriptor mAssetFileDescriptor;

    private int mStreamType = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewTitle = findViewById(R.id.text_title);
        mButtonRecord = findViewById(R.id.record);
        mButtonPlay = findViewById(R.id.play);

        ToggleButton tb = findViewById(R.id.toggleButton3);

        Switch sw = findViewById(R.id.DUMP);
        String value = getSystemProperties("vendor.audio.aec.dump_start", "0");
        Log.d(TAG, "value = " + value);
        if (value.equals("1")) {
            // on
            sw.setChecked(true);
        } else {
            // off
            sw.setChecked(false);
        }

        boolean checked = tb.isChecked();

        if (checked) {
            // spk
            mStreamType = 12;
        } else {
            // hdmi
            mStreamType = 3;
        }

        addChangeCallbackSystemProperties(new Runnable() {
            @Override
            public void run() {
                String value = getSystemProperties("vendor.audio.aec.dump_start", "0");
                Log.d(TAG, "getprop vendor.audio.aec.dump_start =  " + value);
            }
        });
    }

    @Override
    protected void onResume() {
//        startBGM();
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        isPlaying = false;
        mButtonPlay.setText("Play");

        isRecording = false;
        mButtonRecord.setText("Record");
        mTextViewTitle.setText("Recording Point:");
        stopBGM();
        setSystemProperties("vendor.audio.aec.dump_start", "0");
        super.onPause();
    }

    public void startBGM() {
        if (mMediaPlayer == null) {
            try {
                mMediaPlayer = new MediaPlayer();
                if (false) {
                    mAssetFileDescriptor = getAssets().openFd("ms.mp3");
//                mAssetFileDescriptor = getAssets().openFd("test.mp3");
//                mAssetFileDescriptor = getAssets().openFd("pink_repeat_16.wav");
//                mAssetFileDescriptor = getAssets().openFd("sweep_16_a.wav");
//                mAssetFileDescriptor = getAssets().openFd("white_16_a.wav");
                    mMediaPlayer.setDataSource(mAssetFileDescriptor.getFileDescriptor(), mAssetFileDescriptor.getStartOffset(), mAssetFileDescriptor.getLength());
                } else {
                    mMediaPlayer.setDataSource("/mnt/sdcard/ms.mp3");
                }
                mMediaPlayer.prepare();

                mMediaPlayer.setLooping(true);
//            mMediaPlayer.setVolume(0.08f, 0.08f);
                mMediaPlayer.setVolume(0.12f, 0.12f);
                mMediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopBGM() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void setSystemProperties(String key, String value) {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method set = c.getMethod("set", String.class, String.class);
            set.invoke(c, key, value);
        } catch (Exception e) {
            Log.d(TAG, "Exception (" + e.toString() + ") on setSystemProperties(), Msg[" + e.getMessage() + "]");
        }
    }


    private String getSystemProperties(String key, String def) {
        String value = "";

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String) get.invoke(c, key, def);
        } catch (Exception e) {
            Log.d(TAG, "Exception (" + e.toString() + ") on getSystemProperties(), Msg[" + e.getMessage() + "]");
        }

        return value;
    }

    private void addChangeCallbackSystemProperties(Runnable runnable) {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method set = c.getMethod("addChangeCallback", Runnable.class);
            set.invoke(c, runnable);
        } catch (Exception e) {
            Log.d(TAG, "Exception (" + e.toString() + ") on setSystemProperties(), Msg[" + e.getMessage() + "]");
        }
    }


    public void onDump(View view) {
        Switch sw = (Switch) view;
        if (sw.isChecked()) {
            // on
            setSystemProperties("vendor.audio.aec.dump_start", "1");
        } else {
            // off
            setSystemProperties("vendor.audio.aec.dump_start", "0");
        }
    }

    public void onOutput(View view) {

        ToggleButton tb = (ToggleButton) view;

        boolean checked = tb.isChecked();

        if (checked) {
            // spk
            mStreamType = 12;
        } else {
            // hdmi
            mStreamType = 3;
        }

//        mStreamType = mStreamType == 12 ? 3 : 12;
        Toast.makeText(getApplication().getApplicationContext(), "STREAM TYPE(" + mStreamType + ")", Toast.LENGTH_SHORT).show();
    }

    public void onRecord(View view) {
        if (isRecording == true) {
            isRecording = false;
            mButtonRecord.setText("Record");
            mTextViewTitle.setText("Recording Point:");
        } else {
            isRecording = true;
            mButtonRecord.setText("Stop");
            mTextViewTitle.setText("Recording Point: Recording");

            if (mAudioRecord == null) {
                mAudioRecord = new AudioRecord.Builder()
                        .setAudioSource(MediaRecorder.AudioSource.MIC)
                        .setAudioFormat(new AudioFormat.Builder()
                                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                .setSampleRate(16000)
                                .setChannelMask(AudioFormat.CHANNEL_IN_MONO /* CHANNEL_IN_MONO CHANNEL_IN_STEREO */)
//                                .setChannelIndexMask(AudioFormat.CHANNEL_IN_LEFT | AudioFormat.CHANNEL_IN_RIGHT
//                                        | AudioFormat.CHANNEL_IN_FRONT | AudioFormat.CHANNEL_IN_BACK)
                                .build())
//                        .setBufferSizeInBytes(AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT))
                        .setBufferSizeInBytes(mBufferSize)
                        .build();


                mAudioRecord.startRecording();
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    byte[] readData = new byte[mBufferSize];
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(mFilePath);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    while (isRecording) {
                        int ret = mAudioRecord.read(readData, 0, mBufferSize);
                        Log.d(TAG, "read bytes is " + ret);

                        try {
                            fos.write(readData, 0, mBufferSize);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    mAudioRecord.stop();
                    mAudioRecord.release();
                    mAudioRecord = null;

                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }

    public void onPlay(View view) {
        if (isPlaying == true) {
            isPlaying = false;
            mButtonPlay.setText("Play");
        } else {
            File file = new File(mPlayFilePath);
            if (file.exists() == false) {
                Toast.makeText(getApplication().getApplicationContext(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/music.pcm File not exist!!!!", Toast.LENGTH_SHORT).show();
                return;
            }

            isPlaying = true;
            mButtonPlay.setText("Stop");


            if (mAudioTrack == null) {
                if (false) {
                    mAudioTrack = new AudioTrack.Builder()
                            .setAudioAttributes(new AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_ASSISTANT)
                                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                    .build())
                            .setAudioFormat(new AudioFormat.Builder()
                                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                    .setSampleRate(16000)
                                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO/*CHANNEL_OUT_STEREO CHANNEL_OUT_MONO*/)
                                    .build())
                            .setBufferSizeInBytes(mBufferSize)
                            .build();
                } else {
                    mAudioTrack = new AudioTrack(mStreamType/*12*/, 16000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                            mBufferSize, AudioTrack.MODE_STREAM);
                }
            }
            Toast.makeText(getApplication().getApplicationContext(), "REPEAT(" + isRepeat + ")", Toast.LENGTH_SHORT).show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    byte[] writeData = new byte[mBufferSize];
                    FileInputStream fis = null;
                    try {
//                        fis = new FileInputStream(mFilePath);
                        fis = new FileInputStream(mPlayFilePath);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    DataInputStream dis = new DataInputStream(fis);
                    mAudioTrack.play();

                    while (isPlaying) {
                        try {
                            int ret = dis.read(writeData, 0, mBufferSize);
                            if (ret <= 0) {
                                fis = new FileInputStream(mPlayFilePath);
                                dis = new DataInputStream(fis);
                                if (isRepeat) {
                                    Thread.sleep(300);
                                    continue;
                                }

                                (MainActivity.this).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        isPlaying = false;
                                        mButtonPlay.setText("Play");
                                        isRepeat = false;
                                    }
                                });

                                break;
                            }
                            mAudioTrack.write(writeData, 0, ret);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                    mAudioTrack.stop();
                    mAudioTrack.release();
                    mAudioTrack = null;

                    try {
                        dis.close();
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        Log.d(TAG, "keyCode = " + keyCode + " event.getKeyCode() = " + event.getKeyCode());
        switch (keyCode) {
            case KeyEvent.KEYCODE_1:
                stopBGM();
                break;
            case KeyEvent.KEYCODE_2:
                startBGM();
                break;
            case KeyEvent.KEYCODE_3:
//                mStreamType = mStreamType == 12 ? 3 : 12;
                Toast.makeText(getApplication().getApplicationContext(), "STREAM TYPE(" + mStreamType + ")", Toast.LENGTH_SHORT).show();
                break;

            case KeyEvent.KEYCODE_0:
                isRepeat = !isRepeat;
                Toast.makeText(getApplication().getApplicationContext(), "REPEAT(" + isRepeat + ")", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }
}