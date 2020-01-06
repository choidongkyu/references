package net.quber.audiorecord;

import android.os.StatFs;
import android.util.Log;

import static net.quber.audiorecord.MainActivity.mPCMDataPath;

public class StorageManager {
    private static final String TAG = "AudioRecord";
    private static StorageManager mStorageManager;
    private StorageNotificationListener mListener;


    private boolean mAvailableRecord = true;

    private Thread mCheckStorageThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (mAvailableRecord) {
                try {
                    checkStorage();
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    private StorageManager() {
        startCheckStorage();
    }

    public static StorageManager getInstance() {
        if (mStorageManager == null) {
            mStorageManager = new StorageManager();
            Log.d(TAG, "StorageManager create");
        }
        return mStorageManager;
    }

    public void setStorageManagerListener(StorageNotificationListener listener) {
        if (mListener == null)
            mListener = listener;
    }

    public void startCheckStorage() {
        mAvailableRecord = true;
        if (mCheckStorageThread != null) {
            mCheckStorageThread.start();
        }
    }

    public void stopCheckStorage() {
        mAvailableRecord = false;
        mStorageManager = null;
        mListener = null;
        Log.d(TAG, "stopCheckStorage mAvailableRecord = " + mAvailableRecord);
    }

    public boolean checkStorage() {
        StatFs mstat = new StatFs(mPCMDataPath);
        long BlockSize = mstat.getBlockSizeLong();
        long availableSize = mstat.getAvailableBlocksLong() * BlockSize;
        int twentyMB = 20 * 1024 * 1024;
        Log.d(TAG, "availableSize, twentyMB " + availableSize + ", " + twentyMB);
        if ((availableSize) < twentyMB) {
            if (mListener != null) mListener.noticeLackOfStorage();
            mListener = null;
            Log.d(TAG, "폴더 이용 :" + String.valueOf(mAvailableRecord));
            return false;
        }
        return true;
    }
}