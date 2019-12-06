package net.quber.myapplication.ui.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import net.quber.myapplication.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class AppGameFragment extends Fragment {

    private Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TEST CODE --> start
        mPM = getActivity().getPackageManager();
        getResolveInfo();
        dumpResolveInfo();
        //TEST CODE <-- end
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.fragment_appsgame, null, false);

        //TEST CODE --> start
        View view = inflater.inflate(R.layout.fragment_appsgame2, null, false);
        mGridView = view.findViewById(R.id.gridView1);

        MyAdapter adapter = new MyAdapter (
                getContext(),
                R.layout.icon,
                mResolveInfo);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.d(TAG, "onItemClick Pos : " + position);
                startApp(mResolveInfo.get(position));
            }
        });
        //TEST CODE <-- end

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    //TEST CODE --> start
    private static final String TAG = "AppGameFragment";
    private PackageManager mPM;
    private GridView mGridView;
    public List<ResolveInfo> mResolveInfo;

    private void getResolveInfo(){
        if(mPM != null){
            Intent i = new Intent(Intent.ACTION_MAIN, null);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            mResolveInfo = mPM.queryIntentActivities(i, 0);
        }
    }

    private void startApp(ResolveInfo ri){
        ComponentName componentName = new ComponentName(ri.activityInfo.packageName, ri.activityInfo.name);
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        i.setComponent(componentName);

        getContext().startActivity(i);
    }
    private void dumpResolveInfo(){
        if(mResolveInfo != null){
            for(ResolveInfo ri : mResolveInfo){
                Log.d(TAG, ri.toString());
            }
        }
    }

    class MyAdapter extends BaseAdapter {
        Context context;
        int layout;
        List<ResolveInfo> info;
        LayoutInflater inf;

        public MyAdapter(Context context, int layout, List<ResolveInfo> info) {
            this.context = context;
            this.layout = layout;
            this.info = info;
            inf = (LayoutInflater) context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return info.size();
        }

        @Override
        public Object getItem(int position) {
            return info.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inf.inflate(layout, null);
            ImageView iv = convertView.findViewById(R.id.id_app_icon);
            TextView tv = convertView.findViewById(R.id.id_app_label);
            iv.setImageDrawable(info.get(position).loadIcon(getContext().getPackageManager()));
            tv.setText(info.get(position).loadLabel(getContext().getPackageManager()));
            return convertView;
        }
    }
    //TEST CODE <-- end


}
