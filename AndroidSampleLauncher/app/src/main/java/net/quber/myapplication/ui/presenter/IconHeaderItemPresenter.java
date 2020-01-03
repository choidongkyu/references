package net.quber.myapplication.ui.presenter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import net.quber.myapplication.IconHeaderItem;
import net.quber.myapplication.R;
import net.quber.myapplication.Utils;

import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.RowHeaderPresenter;

import static net.quber.myapplication.ui.fragment.CustomHeadersFragment.MENU_CATEGORY;


public class IconHeaderItemPresenter extends RowHeaderPresenter {

    private static final String TAG = "IconHeaderItemPresenter";
    Context mContext;

    @Override
    public Presenter.ViewHolder onCreateViewHolder(ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.icon_header_item, null);
        mContext = view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, final Object item) {
        IconHeaderItem iconHeaderItem = (IconHeaderItem) ((ListRow) item).getHeaderItem();
        View rootView = viewHolder.view;
        rootView.setFocusable(true);

        final ImageView iconView = (ImageView) rootView.findViewById(R.id.header_icon);
        int iconResId = iconHeaderItem.getIconResId();
        if (iconResId != IconHeaderItem.ICON_NONE) { // Show icon only when it is set.
            Drawable icon = rootView.getResources().getDrawable(iconResId, null);
            iconView.setImageDrawable(icon);
        }

        TextView label = (TextView) rootView.findViewById(R.id.header_label);
        label.setText(iconHeaderItem.getName());
        label.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        label.setSingleLine(true);
        label.setMarqueeRepeatLimit(-1);

        //ViewGroup.LayoutParams params = iconView.getLayoutParams();
        /*params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        iconView.setLayoutParams(params);*/
        iconView.setPadding(0, Utils.convertDpToPixel(viewHolder.view.getContext(), 10), 0, Utils.convertDpToPixel(viewHolder.view.getContext(), 10));
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Object obj = ((ListRow) getAdapter().get(position)).getAdapter().get(0);
                Log.d(TAG, "" + ((ListRow) item).getAdapter().get(0));
                if (((ListRow) item).getAdapter().get(0).equals(MENU_CATEGORY[MENU_CATEGORY.length - 1])) {
                    Intent intent = Intent.makeMainActivity(new ComponentName("com.android.tv.settings",
                            "com.android.tv.settings.MainSettings"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mContext.startActivity(intent); // tv용 setting이라 tablet용 설정으로 추후 수정해야 합니다.
                }
            }
        });
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        //super.onUnbindViewHolder(viewHolder);
    }

    @Override
    protected void onSelectLevelChanged(ViewHolder holder) {
        super.onSelectLevelChanged(holder);
    }

}
