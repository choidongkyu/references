package net.quber.myapplication.task;

import android.content.Context;
import android.util.Log;

import net.quber.myapplication.data.Movie;
import net.quber.myapplication.data.MovieList;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.loader.content.AsyncTaskLoader;

public class GetVideolistTask extends AsyncTaskLoader <List<Movie>> {

    private static final String TAG = "GetVideolistTask";

    public GetVideolistTask(@NonNull Context context) {
        super(context);
    }

    @Override
    public List<Movie> loadInBackground() {
        Log.d(TAG,"loadInBackground");
        return MovieList.setupMovies();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        Log.d(TAG,"onStartLoading");
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        Log.d(TAG,"onStopLoading");
        cancelLoad();
    }
}
