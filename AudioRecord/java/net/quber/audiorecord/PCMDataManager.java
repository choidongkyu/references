package net.quber.audiorecord;

import android.os.IHwBinder;
import android.os.Message;
import android.os.RemoteException;

import java.util.ArrayList;

import android.util.Log;
import android.os.Handler;

import vendor.quber.hardware.aecanalysis.V1_0.IAECAnalysisService;
import vendor.quber.hardware.aecanalysis.V1_0.IAECAnalysisListener;
import vendor.quber.hardware.aecanalysis.V1_0.AECAnalysisdData;


public class PCMDataManager {
    private final String TAG = "AudioRecord";
    private Handler mPcmWaveFormHandler;
    private Handler mFileDumpHandler;
    private static PCMDataManager mPCMDataManager;
    private static IAECAnalysisService mAECAnalysisService;
    private IHwBinder.DeathRecipient mDeathNotifiier = new IHwBinder.DeathRecipient() {
        @Override
        public void serviceDied(long cookie) {
            getAECAnalysisService();
        }
    };

    private IAECAnalysisListener mAECAnalysisListener = new IAECAnalysisListener.Stub() {
        @Override
        public void onPCMdataReceived(ArrayList<AECAnalysisdData> dataList) throws RemoteException {
            Message msg = mPcmWaveFormHandler.obtainMessage(0, dataList);
            mPcmWaveFormHandler.sendMessage(msg);
            if (mFileDumpHandler != null) {
                msg = mFileDumpHandler.obtainMessage(0, dataList);
                mFileDumpHandler.sendMessage(msg);
            }
        }

        @Override
        public void onEventReceived(int event, int arg) throws android.os.RemoteException {
            Log.d(TAG, "onEventReceived");
        }
    };

    private PCMDataManager() {
        getAECAnalysisService();
    }

    public void setPCMDataHandler(Handler handler) {
        if (mPcmWaveFormHandler == null)
            mPcmWaveFormHandler = handler;
    }

    public void setFileDumpHandler(Handler handler) {
        if (mFileDumpHandler == null) {
            mFileDumpHandler = handler;
        }
    }

    public void destroyFileDumpHandler() {
        if (mFileDumpHandler != null)
            mFileDumpHandler = null;
    }

    public static PCMDataManager getInstance() {
        if (mPCMDataManager == null) {
            mPCMDataManager = new PCMDataManager();
        }
        return mPCMDataManager;
    }


    public void disConnectService() {
        try {
            mAECAnalysisService.registerAECAnalysisListener(null);
            mPCMDataManager = null;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void getAECAnalysisService() {
        try {
            mAECAnalysisService = IAECAnalysisService.getService(true);
            mAECAnalysisService.linkToDeath(mDeathNotifiier, 0);
            mAECAnalysisService.registerAECAnalysisListener(mAECAnalysisListener);
            Log.d(TAG, "getNativeService");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}