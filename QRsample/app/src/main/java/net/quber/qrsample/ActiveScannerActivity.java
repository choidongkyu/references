package net.quber.qrsample;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.zebra.scannercontrol.DCSSDKDefs;
import com.zebra.scannercontrol.DCSScannerInfo;
import com.zebra.scannercontrol.FirmwareUpdateEvent;

import net.quber.external.zebra.helpers.Barcode;
import net.quber.external.zebra.helpers.Constants;
import net.quber.external.zebra.helpers.ScannerAppEngine;
import net.quber.external.zebra.receivers.NotificationsReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ActiveScannerActivity extends BaseActivity implements SurfaceHolder.Callback, ScannerAppEngine.IScannerAppEngineDevEventsDelegate,
        ScannerAppEngine.IScannerAppEngineDevConnectionsDelegate {

    JSONObject mJsonObject;
    JSONArray mJsonArray;
    ArrayList<String> mMediaUrlList;
    ArrayList<Integer> mChannelNumList;
    ArrayList<String> mChannelNameList;

    //String mUrl = "http://221.165.27.104:8023/api/channel_list";


    MediaPlayer mMediaPlayer;
    SurfaceView mSurfaceView;
    SurfaceHolder mSurfaceHolder;
    String mLocalFilePath = Environment.getExternalStorageDirectory()+"/Movies";
    //HashMap<String, String> mParams;

    private int mScannerID;
    int mBarcodeCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ActiveScannerActivity onCreate");
        setContentView(R.layout.activity_active_scanner);


        /**
         *  It's not necessary yet,
         *  but it will be needed later when html request.
         */

//        mMediaUrlList = new ArrayList<>();
//        mChannelNameList = new ArrayList<>();
//        mChannelNumList = new ArrayList<>();
//        mParams = new HashMap<>();
//        mParams.put("serial_id", 2222 + "");
//        mParams.put("last_update", 0 + "");
//        NetworkTask networkTask = new NetworkTask(mUrl, mParams);
//        networkTask.execute();

        registerReceiver(mUsbDeviceReceiver, new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED));
        registerReceiver(mUsbDeviceReceiver, new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED));

        ListView listView = (ListView) findViewById(R.id.video_list);

        List localVideoList = new ArrayList();
        File videoFile = new File(mLocalFilePath);
        Log.d(TAG,"mLocalFilepath = "+mLocalFilePath);
        File videoFileList[] = videoFile.listFiles();
        if(videoFileList != null){
            for(int i=0; i<videoFileList.length; ++i){
                localVideoList.add(videoFileList[i].getName());
            }
        }else {
            Log.d(TAG,"not found videolist");
        }


        mMediaPlayer = new MediaPlayer();
        mSurfaceView = (SurfaceView) findViewById(R.id.mediaplayer);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);

        //ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.row, R.id.textItem, videoList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.row, R.id.textItem, localVideoList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "selected item = " + parent.getItemAtPosition(position) + ", position = " + position);
                localMediaPlay(mLocalFilePath+"/"+parent.getItemAtPosition(position));
            }
        });


        addDevConnectionsDelegate(this);
        mScannerID = getIntent().getIntExtra(Constants.SCANNER_ID, -1);
        BaseActivity.lastConnectedScannerID = mScannerID;
        String scannerName = getIntent().getStringExtra(Constants.SCANNER_NAME);
        String address = getIntent().getStringExtra(Constants.SCANNER_ADDRESS);


        Application.CurScannerId = mScannerID;
        Application.CurScannerName = scannerName;
        Application.CurScannerAddress = address;

        mBarcodeCount = 0;

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) this.getSystemService(ns);
        if (nMgr != null) {
            nMgr.cancel(NotificationsReceiver.DEFAULT_NOTIFICATION_ID);
        }


        initialize();

//        Intent intent = new Intent(ActiveScannerActivity.this, FindCabledScanner.class);
//        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "ActiveScannerActivity onResume");
        addDevEventsDelegate(this);
        addDevConnectionsDelegate(this);
        //addMissedBarcodes();
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) this.getSystemService(ns);
        if (nMgr != null) {
            nMgr.cancel(NotificationsReceiver.DEFAULT_NOTIFICATION_ID);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        removeDevEventsDelegate(this);
        removeDevConnectiosDelegate(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        removeDevEventsDelegate(this);
        removeDevConnectiosDelegate(this);
        unregisterReceiver(mUsbDeviceReceiver);
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated begin");
//        String path = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
//        try {
//            mMediaPlayer.setDataSource(path);
//            mMediaPlayer.setDisplay(mSurfaceHolder);
//            mMediaPlayer.prepare();
//            mMediaPlayer.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfacechanges");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.setDisplay(null);
        }
    }

    @Override
    public boolean scannerHasAppeared(int scannerID) {
        return false;
    }

    @Override
    public boolean scannerHasDisappeared(int scannerID) {
        return false;
    }

    @Override
    public boolean scannerHasConnected(int scannerID) {
        return false;
    }

    @Override
    public boolean scannerHasDisconnected(int scannerID) {
        return false;
    }

    @Override
    public void scannerBarcodeEvent(byte[] barcodeData, int barcodeType, int scannerID) {
        Barcode barcode = new Barcode(barcodeData, barcodeType, scannerID);
        String path = new String(barcode.getBarcodeData());
        Log.d(TAG, "scannerBarcode Event recevied = " + path);
        urlMediaPlay(path);
    }

    @Override
    public void scannerFirmwareUpdateEvent(FirmwareUpdateEvent firmwareUpdateEvent) {

    }

    @Override
    public void scannerImageEvent(byte[] imageData) {

    }

    @Override
    public void scannerVideoEvent(byte[] videoData) {

    }

    private void addMissedBarcodes() {
        if (Application.barcodeData.size() != mBarcodeCount) {
            for (int i = mBarcodeCount; i < Application.barcodeData.size(); i++) {
                scannerBarcodeEvent(Application.barcodeData.get(i).getBarcodeData(), Application.barcodeData.get(i).getBarcodeType(), Application.barcodeData.get(i).getFromScannerID());
            }
        }
    }

    private void initialize() {
        Log.d(TAG, "initialize begin");
        initializeDcsSdk();
        addDevConnectionsDelegate(this);
        //setTitle("Pair New Scanner");
        broadcastSCAisListening();
    }

    private void broadcastSCAisListening() {
        Log.d(TAG, "broadcastSCAisListiening begin");
        Intent intent = new Intent();
        intent.setAction("com.zebra.scannercontrol.LISTENING_STARTED");
        sendBroadcast(intent);
    }

    private void initializeDcsSdk() {
        Application.sdkHandler.dcssdkEnableAvailableScannersDetection(true);
        Application.sdkHandler.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_BT_NORMAL);
        Application.sdkHandler.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_SNAPI);
        Application.sdkHandler.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_BT_LE);
    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private String url;
        private HashMap<String, String> value;

        public NetworkTask(String url, HashMap<String, String> params) {
            this.url = url;
            this.value = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, value); // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                mJsonObject = new JSONObject(s);
                Log.d(TAG, "data = " + mJsonObject.get("data"));
                mJsonObject = new JSONObject(mJsonObject.getString("data"));
                Log.d(TAG, "second data = " + mJsonObject.getString("data"));
                mJsonArray = mJsonObject.getJSONArray("data");
                mJsonArray = mJsonArray.getJSONObject(0).getJSONArray("tps");
                mJsonArray = mJsonArray.getJSONObject(0).getJSONArray("zpre_channels");
                Log.d(TAG, "zpre_channels = " + mJsonArray.length());
                for (int i = 0; i < mJsonArray.length(); i++) {
                    JSONObject jsonObject = mJsonArray.getJSONObject(i);
                    String mediaURL = jsonObject.getString("media_url");
                    String chName = jsonObject.getString("ch_name");
                    Integer chNum = jsonObject.getInt("ch_number");
                    mMediaUrlList.add(mediaURL);
                    mChannelNameList.add(chName);
                    mChannelNumList.add(chNum);
                }
                for (int i = 0; i < mMediaUrlList.size(); i++) {
                    Log.d(TAG, "mMediaURL = " + mMediaUrlList.get(i));
                }
                mSurfaceHolder.addCallback(ActiveScannerActivity.this);
            } catch (JSONException e) {
                Log.d(TAG, "JSONException");
                e.printStackTrace();
            }
        }
    }

    private final BroadcastReceiver mUsbDeviceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                Log.d(TAG, "ACTION_USB_DEVICE_ATTACHED");
//                Intent scannerintent = new Intent(ActiveScannerActivity.this, FindCabledScanner.class);
//                startActivity(scannerintent);

            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                Log.d(TAG, "ACTION_USB_DEVICE_DETACHED");
            }
        }
    };


    private void urlMediaPlay(String path) {
        try {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void localMediaPlay(String path) {
        File file = new File(path);
        try {
            FileInputStream inputStream = new FileInputStream(file);
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.setDataSource(inputStream.getFD());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
