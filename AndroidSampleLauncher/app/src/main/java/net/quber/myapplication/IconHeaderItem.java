package net.quber.myapplication;

import androidx.leanback.widget.HeaderItem;

public class IconHeaderItem extends HeaderItem {

    public static final int ICON_NONE = -1;

    private int mIconResId = ICON_NONE;

    public IconHeaderItem(long id, String name, int iconResId) {
        super(id, name);
        mIconResId = iconResId;
    }

    public IconHeaderItem(long id, String name) {
        this(id, name, ICON_NONE);
    }

    public int getIconResId() {
        return mIconResId;
    }
}
