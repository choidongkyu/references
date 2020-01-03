package net.quber.myapplication.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.quber.myapplication.Adapter.PreferenceListAdapter;
import net.quber.myapplication.R;
import net.quber.myapplication.data.Picture;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PictureFragment extends Fragment {
    private Context mContext;
    private RecyclerView mRecyclerViewPreferenceList;
    private RecyclerView.Adapter mAdapterPreferenceList;
    private RecyclerView mRecyclerViewWatchList;
    private RecyclerView.Adapter mAdapterWatchList;
    private LinearLayoutManager mLayoutManagerPreferenceList;
    private LinearLayoutManager mLayoutManagerWatchList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture, null, false);
        mContext = getContext();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerViewPreferenceList = (RecyclerView) getActivity().findViewById(R.id.preference_list_recycler_view);
        mRecyclerViewWatchList = (RecyclerView) getActivity().findViewById(R.id.watch_list_recycler_view);
        updatePictureListRow();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void updatePictureListRow() {
        //-------------- Preference List ROW -----------------------------//
        ArrayList mPreferenceListitems = new ArrayList<>();
        mPreferenceListitems.add(new Picture(
                getActivity().getDrawable(R.drawable.m_0001),
                "2016 sing"
        ));

        mPreferenceListitems.add(new Picture(
                getActivity().getDrawable(R.drawable.m_0002),
                "Eat Pray Love"
        ));

        mPreferenceListitems.add(new Picture(
                getActivity().getDrawable(R.drawable.m_0003),
                "Beauty and the Beast"
        ));

        mPreferenceListitems.add(new Picture(
                getActivity().getDrawable(R.drawable.m_0004),
                "Avatar 2"
        ));

        mPreferenceListitems.add(new Picture(
                getActivity().getDrawable(R.drawable.m_0005),
                "Pirates"
        ));

        mPreferenceListitems.add(new Picture(
                getActivity().getDrawable(R.drawable.m_0006),
                "2017 sing"
        ));

        mPreferenceListitems.add(new Picture(
                getActivity().getDrawable(R.drawable.m_0007),
                "2018 sing"
        ));

        //-------------- Watch List ROW -----------------------------//

        ArrayList mWatchListitems = new ArrayList<>();
        mWatchListitems.add(new Picture(
                getActivity().getDrawable(R.drawable.m_0008),
                "The Fury"
        ));

        mWatchListitems.add(new Picture(
                getActivity().getDrawable(R.drawable.m_0009),
                "Wonder Woman"
        ));

        mWatchListitems.add(new Picture(
                getActivity().getDrawable(R.drawable.m_0010),
                "HIDDEN FIGURES"
        ));

        mWatchListitems.add(new Picture(
                getActivity().getDrawable(R.drawable.m_0011),
                "DUNKIRK"
        ));

        mWatchListitems.add(new Picture(
                getActivity().getDrawable(R.drawable.m_0012),
                "Super Man"
        ));

        mWatchListitems.add(new Picture(
                getActivity().getDrawable(R.drawable.m_0013),
                "BAT Man"
        ));

        mWatchListitems.add(new Picture(
                getActivity().getDrawable(R.drawable.m_0014),
                "2017 sing"
        ));

        mLayoutManagerPreferenceList = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mAdapterPreferenceList = new PreferenceListAdapter(mPreferenceListitems, R.layout.item_picture_list, this);
        mRecyclerViewPreferenceList.setAdapter(mAdapterPreferenceList);
        mRecyclerViewPreferenceList.setLayoutManager(mLayoutManagerPreferenceList);
        mRecyclerViewPreferenceList.setItemAnimator(null);


        mLayoutManagerWatchList = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mAdapterWatchList = new PreferenceListAdapter(mWatchListitems, R.layout.item_picture_list, this);
        mRecyclerViewWatchList.setAdapter(mAdapterWatchList);
        mRecyclerViewWatchList.setLayoutManager(mLayoutManagerWatchList);
        mRecyclerViewWatchList.setItemAnimator(null);
    }



}
