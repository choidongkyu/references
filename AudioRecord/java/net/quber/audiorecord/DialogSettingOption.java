package net.quber.audiorecord;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.quber.waveformview.WaveFormView;


public class DialogSettingOption {
    private boolean[] mChecks = {true, true, true, true, true, true};
    private Context mContext;
    private WaveFormView[] mWaveFormViews;
    private LinearLayout[] mLinearLayouts;
    private TextView[] mTextView;
    private final String TAG = "AudioRecord";

    public DialogSettingOption(Context context) {
        this.mContext = context;
        this.mWaveFormViews = ((MainActivity) context).mWaveFormViews;
        this.mLinearLayouts = ((MainActivity) context).mLinearLayouts;
        this.mTextView = ((MainActivity) context).mTextViews;

    }

    public void openAlertDialog() {
        final String[] items = {"MIC1", "MIC2", "MIC3", "MIC4", "Ref1", "Ref2"};
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
        alertBuilder.setTitle("Setting Option");
        alertBuilder.setMultiChoiceItems(items, mChecks, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which, boolean checked) {
                mChecks[which] = checked;
            }
        });

        alertBuilder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "alretDialog onclick!");
                dialogInterface.dismiss();
            }
        });

        alertBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ChangeUILayout();
            }
        });
        alertBuilder.show();
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
}
