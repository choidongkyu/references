package net.quber.waveformview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import net.quber.audiorecord.R;

import java.util.ArrayList;
import java.util.Collections;

public class WaveFormView extends View {
    private static final String TAG = "WaveFormView";
    int mSampleRate = 16000;
    float mSecond = 30f;
    int mSamplePerPixel = ((int) mSecond * mSampleRate) / 1920;
    int mDataRange = 65536;
    float mMagnification = 1f;
    short mShortMaxValue = 32767;

    ArrayList<Short> mData = new ArrayList<>();

    Bitmap mBmp = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888);
    Bitmap mTempBmp = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888);

    private Paint mWaveFormPaint;
    private float mScale = 1f;
    private float mAxisX = 0;


    public WaveFormView(Context context) {
        super(context);
        init(context, null);
    }

    public WaveFormView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WaveFormView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public WaveFormView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        int waveformColor = Color.parseColor("#009FF7");
        mWaveFormPaint = new Paint();
        mWaveFormPaint.setColor(waveformColor);
        mWaveFormPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mWaveFormPaint.setStrokeWidth(0);

        Canvas canvas = new Canvas(mBmp);
        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorWaveBackground));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBmp, 0, 0, mWaveFormPaint);
    }

    public synchronized void updateAudioData(ArrayList<Short> buffer) {
        ArrayList<Short> newBuffer = new ArrayList<>(buffer);
        drawWave(newBuffer);
        invalidate();
    }

    public void setMICWave(float magnification){
        mMagnification = magnification;
    }


    private void drawWave(ArrayList<Short> buffer) {
        int height = getMeasuredHeight();
        int originWidth = mBmp.getWidth();
        int originHeight = mBmp.getHeight();

        int dataLength = buffer.size();

        float axisX = 0;
        int finalAxisX = -1;
        short low;
        short high;
        int lowY;
        int highY;


        if (mAxisX > getMeasuredWidth()) {
            Canvas secondCanvas = new Canvas(mTempBmp);
            secondCanvas.drawBitmap(mBmp,new Rect(dataLength/mSamplePerPixel,0,originWidth,originHeight),
                    new Rect(0,0,originWidth-(dataLength/mSamplePerPixel),originHeight),mWaveFormPaint);
            mBmp = mTempBmp;
            mAxisX -= (dataLength / mSamplePerPixel);
        }

        Canvas cashCanvas = new Canvas(mBmp);

        while (axisX < dataLength / mSamplePerPixel) {
            mData.addAll(buffer.subList(0, mSamplePerPixel));
            int nearestAxisX = (int) mAxisX;
            if (nearestAxisX != finalAxisX) {
                finalAxisX = nearestAxisX;
                low = (short) (Collections.min(mData)*mMagnification);
                high = (short) (Collections.max(mData)*mMagnification);
                if(low == 0 && high == 0){
                    low = -1;
                    high = 1;
                }
                lowY = height - (low + mShortMaxValue) * height / mDataRange;
                highY = height - (high + mShortMaxValue) * height / mDataRange;
                cashCanvas.drawLine(finalAxisX, lowY, finalAxisX, highY, mWaveFormPaint);
            }
            mAxisX += mScale;
            axisX += mScale;
            mData.clear();
            buffer.subList(0, mSamplePerPixel).clear();
        }
    }

    public void changeWaveFormColor(int color) {
        Log.d(TAG, "changewaveform color = " + color);
        mWaveFormPaint.setColor(color);
    }
}
