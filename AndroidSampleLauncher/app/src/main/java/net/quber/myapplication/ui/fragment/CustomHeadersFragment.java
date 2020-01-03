package net.quber.myapplication.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.quber.myapplication.IconHeaderItem;
import net.quber.myapplication.MainActivity;
import net.quber.myapplication.R;
import net.quber.myapplication.ui.presenter.IconHeaderItemPresenter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.app.HeadersSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.OnChildSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.PresenterSelector;
import androidx.leanback.widget.VerticalGridView;

public class CustomHeadersFragment extends HeadersSupportFragment {

    private static final String TAG = "CustomHeadersFragment";

    private ArrayObjectAdapter mHeaderAdapter;
    private ArrayList<String> mHeaderTitle;
    private HashMap<String, Integer> mHeaderItem;

    private VideoRowsFragment mVideoRowsFragment;
    private AppGameFragment mAppGameFragment;
    private PictureFragment mPictureFragment;

    public static final int TRANSACTION_DELAY = 300;

    public static final String MENU_CATEGORY[] = {
            "Video",
            "Apps",
            "Picture & Image",
            "Setting",
    };

    private final int ICON_CATEGORY[] = {
            R.drawable.img_menu_icon_01,
            R.drawable.img_menu_icon_02,
            R.drawable.img_menu_icon_03,
            R.drawable.img_menu_icon_04,
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHeaderTitle = new ArrayList<String>();
        mHeaderItem = new HashMap<String, Integer>();
        mVideoRowsFragment = new VideoRowsFragment();
        mAppGameFragment = new AppGameFragment();
        mPictureFragment = new PictureFragment();

        for (int i = 0; i < MENU_CATEGORY.length; i++) {
            mHeaderTitle.add(MENU_CATEGORY[i]);
            mHeaderItem.put(MENU_CATEGORY[i], ICON_CATEGORY[i]);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        customSetBackground(R.color.transparent_background);
        setHeaderAdapter();
        getView().setPadding(0, 320, 0, 0); // set top margin

        setPresenterSelector(new PresenterSelector() {
            @Override
            public Presenter getPresenter(Object item) {
                return new IconHeaderItemPresenter();
            }
        });

        VerticalGridView gridView = getVerticalGridView();
        gridView.setOnChildSelectedListener(new OnChildSelectedListener() {
            @Override
            public void onChildSelected(ViewGroup parent, View view, int position, long id) {
                Object obj = ((ListRow) getAdapter().get(position)).getAdapter().get(0);
                transFragment((String) obj);
            }
        });
    }

    public void setHeaderAdapter() {
        mHeaderAdapter = new ArrayObjectAdapter();

        //ArrayList<String> headerTitle = ((MainActivity) getActivity()).getHeaderTitle();
        //HashMap<String, Integer> headerItem = ((MainActivity) getActivity()).getHeaderItem();
        IconHeaderItem gridItemPresenterHeader;

        for (int i = 0; i < mHeaderTitle.size(); i++) {
            gridItemPresenterHeader = new IconHeaderItem(i, mHeaderTitle.get(i), mHeaderItem.get(mHeaderTitle.get(i)));
            Log.d(TAG, "new IconHeaderItem(" + mHeaderTitle.get(i) + "," + mHeaderItem.get(mHeaderTitle.get(i)) + ")");
            ArrayObjectAdapter innerAdapter = new ArrayObjectAdapter();
            innerAdapter.add(mHeaderTitle.get(i));
            mHeaderAdapter.add(i, new ListRow(gridItemPresenterHeader, innerAdapter));
        }

        setAdapter(mHeaderAdapter);
    }


    /**
     * Since the original setBackgroundColor is private, we need to
     * access it via reflection
     *
     * @param Color The colour resource
     */
    private void customSetBackground(int Color) {
        try {
            Class clazz = HeadersSupportFragment.class;
            //Log.d(TAG,"replection = "+HeadersSupportFragment.class);
            Method m = clazz.getDeclaredMethod("setBackgroundColor", Integer.TYPE);
            m.setAccessible(true);
            m.invoke(this, getResources().getColor(Color));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    private void transFragment(String obj) {
        switch (obj) {
            case "Video" :
                ((MainActivity) getActivity()).getfragmentManager().beginTransaction().replace(R.id.rows_container, mVideoRowsFragment).commit();
                break;
            case "Apps":
                ((MainActivity) getActivity()).getfragmentManager().beginTransaction().replace(R.id.rows_container, mAppGameFragment).commit();
                break;
            case "Picture & Image" :
                ((MainActivity) getActivity()).getfragmentManager().beginTransaction().replace(R.id.rows_container, mPictureFragment).commit();
                break;
            default:
                Log.d(TAG, "not exist fragment");
        }
    }
}
