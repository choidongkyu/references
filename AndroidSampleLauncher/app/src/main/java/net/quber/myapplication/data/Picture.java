package net.quber.myapplication.data;

import android.graphics.drawable.Drawable;

public class Picture {
    Drawable mPictureListImage;
    String mPictureListtitle;

    public Picture(Drawable mPreferenceListImage, String mPreferenceListtitle) {
        this.mPictureListImage = mPreferenceListImage;
        this.mPictureListtitle = mPreferenceListtitle;
    }

    public Drawable getPictureListImage() {
        return mPictureListImage;
    }

    public String getPictureListtitle() {
        return mPictureListtitle;
    }
}
