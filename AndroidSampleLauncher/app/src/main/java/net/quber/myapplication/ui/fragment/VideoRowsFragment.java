package net.quber.myapplication.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.quber.myapplication.data.Movie;
import net.quber.myapplication.data.MovieList;
import net.quber.myapplication.Utils;
import net.quber.myapplication.task.GetVideolistTask;
import net.quber.myapplication.ui.presenter.CardPresenter;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.app.RowsFragment;
import androidx.leanback.app.RowsSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

public class VideoRowsFragment extends RowsSupportFragment implements LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final String TAG = "VideoRowsFragment";

    private static final int NUM_ROWS = 2;
    private static final int NUM_COLS = 8;
    private static final int MEDIA_STORE_LOADER = 0;

    private ArrayObjectAdapter mRowsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        Log.d(TAG, "mRowsAdapter == null");
        LoaderManager.getInstance(this).initLoader(MEDIA_STORE_LOADER, null, this);
        //getLoaderManager().initLoader(MEDIA_STORE_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "video fragment onActivCreate");
        //loadRows();
        setCustomPadding();
    }


    private void setCustomPadding() {
        getView().setPadding(Utils.convertDpToPixel(getActivity(), -24), Utils.convertDpToPixel(getActivity(), 128), Utils.convertDpToPixel(getActivity(), 48), 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        mRowsAdapter.clear();
        mRowsAdapter = null;
        LoaderManager.getInstance(this).destroyLoader(MEDIA_STORE_LOADER);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }

    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG, "onCreateLoader");
        return new GetVideolistTask(getActivity());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> data) {
        Log.d(TAG, "onLoadFinished");
        loadRows();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {
        //none
    }

    private void loadRows() {
        List<Movie> list = MovieList.getList();
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        CardPresenter cardPresenter = new CardPresenter();

        int i;
        for (i = 0; i < NUM_ROWS; i++) {
            if (i != 0) {
                Collections.shuffle(list);
            }
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
            for (int j = 0; j < NUM_COLS; j++) {
                listRowAdapter.add(list.get(j % 5));
            }
            HeaderItem header = new HeaderItem(i, MovieList.MOVIE_CATEGORY[i]);
            mRowsAdapter.add(new ListRow(header, listRowAdapter));
        }
        //setAdapter(mRowsAdapter);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setAdapter(mRowsAdapter);
            }
        },CustomHeadersFragment.TRANSACTION_DELAY); // Fragment 이동시마다 버벅임이 있어 강제로 250ms delay를 줌, 좋은 방법이 있다면 로직 수정할 예정
    }
}
