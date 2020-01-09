package net.quber.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

public class CustomFrameLayout extends FrameLayout {
    private OnFocusListener mOnChildFocusListener;

    public interface OnFocusListener {
        void onRequestFocus(View child, View focused);
    }

    public CustomFrameLayout(Context context) {
        this(context, null, 0);
    }

    public CustomFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnFocusListener(OnFocusListener listener) {
        mOnChildFocusListener = listener;
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        if (mOnChildFocusListener != null) {
            mOnChildFocusListener.onRequestFocus(child, focused);
        }
    }
}
