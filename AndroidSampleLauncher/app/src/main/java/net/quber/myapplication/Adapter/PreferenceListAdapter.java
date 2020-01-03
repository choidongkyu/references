package net.quber.myapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.quber.myapplication.R;
import net.quber.myapplication.data.Picture;
import net.quber.myapplication.ui.fragment.PictureFragment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

public class PreferenceListAdapter extends RecyclerView.Adapter<PreferenceListAdapter.ViewHolder> {

    private List<Picture> mItemList;
    private int mItemLayout;
    private PictureFragment mPictureFragment;

    public PreferenceListAdapter(List<Picture> items, int itemLayout, PictureFragment fragment) {
        this.mItemList = items;
        this.mItemLayout = itemLayout;
        this.mPictureFragment = fragment;
    }

    @NonNull
    @Override
    public PreferenceListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mItemLayout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceListAdapter.ViewHolder holder, int position) {
        Picture item = mItemList.get(position);
        holder.mBackgroundView.setBackground(item.getPictureListImage());
        holder.mTextTitle.setText(item.getPictureListtitle());

         if (position == getItemCount() - 1) {
            holder.mCardView.setNextFocusRightId(holder.mCardView.getId());
            holder.mCardView.setPadding(0, 0, 65, 0);
        }
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnFocusChangeListener {

        private LinearLayout mCardView;
        private ImageView mBackgroundView;
        private TextView mTextTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mCardView = (LinearLayout) itemView.findViewById(R.id.picturelist_container);
            mBackgroundView = (ImageView) itemView.findViewById(R.id.img_picture_list_background_view);
            mTextTitle = (TextView) itemView.findViewById(R.id.txt_picture_list_background_view_title);
            itemView.setOnFocusChangeListener(this);
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            ScaleAnimation anim;
            if (hasFocus) {
                ViewCompat.setElevation(v, 1);
                anim = new ScaleAnimation(1.0f, 1.15f, 1.0f, 1.15f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            } else {
                ViewCompat.setElevation(v, 0);
                anim = new ScaleAnimation(1.15f, 1.0f, 1.15f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            }
            anim.setDuration(100);
            anim.setFillAfter(true);
            v.startAnimation(anim);
        }
    }
}
