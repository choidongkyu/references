package net.quber.audiorecord;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import net.quber.waveformview.WaveFormView;

import static net.quber.audiorecord.MainActivity.DB10;
import static net.quber.audiorecord.MainActivity.DB20;
import static net.quber.audiorecord.MainActivity.DB30;

public class SettingDialog extends Dialog {
    private static final String TAG = "SettingDialog";
    private boolean[] mChecks = {true, true, true, true, true, true};
    private Context mContext;
    private WaveFormView[] mWaveFormViews;
    private LinearLayout[] mLinearLayouts;
    private WaveFormView[] mMICWaveView;
    private TextView[] mTextView;
    private CheckBox[] mCheckBoxArray;
    private Button mPositiveButton;
    private Button mNegativeButton;
    private RadioButton[] mRadioButtonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"setting Dialog onCreate");
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.setting_dialog);
        initUI();


        mPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mCheckBoxArray.length; i++) {
                    mChecks[i] = mCheckBoxArray[i].isChecked();
                }
                checkMicWave();
                ChangeUILayout();
                dismiss();
            }
        });
        mNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public SettingDialog(Context context) {
        super(context);
        this.mContext = context;
        this.mWaveFormViews = ((MainActivity) context).mWaveFormViews;
        this.mLinearLayouts = ((MainActivity) context).mLinearLayouts;
        this.mTextView = ((MainActivity) context).mTextViews;
        this.mMICWaveView = ((MainActivity) context).mMICWaveView;
    }

    public SettingDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected SettingDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private void ChangeUILayout() {
        for (int j = 0; j < mChecks.length; j++) {
            if (!mChecks[j]) {
                mTextView[j].setVisibility(View.GONE);
                mWaveFormViews[j].setVisibility(View.GONE);
                setMarginBottom(mLinearLayouts[j], 0);
            } else {
                mTextView[j].setVisibility(View.VISIBLE);
                mWaveFormViews[j].setVisibility(View.VISIBLE);
                setMarginBottom(mLinearLayouts[j], 10);
            }
        }
    }

    private void setMarginBottom(View v, int bottom) {
        ViewGroup.MarginLayoutParams params =
                (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        params.setMargins(params.leftMargin, params.topMargin,
                params.rightMargin, bottom);
    }

    private void initUI() {
        mPositiveButton = (Button) findViewById(R.id.positiveButton);
        mNegativeButton = (Button) findViewById(R.id.negativeButton);
        mRadioButtonArray = new RadioButton[]{findViewById(R.id.dB10),findViewById(R.id.dB20),findViewById(R.id.dB30)};
        mCheckBoxArray = new CheckBox[]{findViewById(R.id.mic1check), findViewById(R.id.mic2check), findViewById(R.id.mic3check),
                findViewById(R.id.mic4check), findViewById(R.id.ref1check), findViewById(R.id.ref2check)};
        for (CheckBox checkBox : mCheckBoxArray) checkBox.setChecked(true);
        mRadioButtonArray[0].setChecked(true);
    }

    private void checkMicWave(){
        if(mRadioButtonArray[0].isChecked()){
            for (WaveFormView view : mMICWaveView)
                view.setMICWave(DB10);
        }else if(mRadioButtonArray[1].isChecked()){ //20dB
            for (WaveFormView view : mMICWaveView)
                view.setMICWave(DB20);
        }else if(mRadioButtonArray[2].isChecked()){ //30dB
            for (WaveFormView view : mMICWaveView)
                view.setMICWave(DB30);
        }else{
            for (WaveFormView view : mMICWaveView)
                view.setMICWave(1); //base
        }
    }
}
