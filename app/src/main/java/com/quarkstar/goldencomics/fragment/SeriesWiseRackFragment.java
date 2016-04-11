package com.quarkstar.goldencomics.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.quarkstar.goldencomics.MainActivity;
import com.quarkstar.goldencomics.R;
import com.quarkstar.goldencomics.adapter.SeriesWiseVerticalAdapter;
import com.quarkstar.goldencomics.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class SeriesWiseRackFragment extends Fragment {

    private static final String DATABASE_HELPER = "database_helper";

    private OnFragmentInteractionListener mListener;
    private RecyclerView.Adapter verticalAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView myRecyclerView;
    private RecyclerView mRecyclerView;
    private DatabaseHelper dbHelper;
    private List<String> seriesIdList;

    public SeriesWiseRackFragment() {
        // Required empty public constructor
    }

    public static SeriesWiseRackFragment newInstance(DatabaseHelper dbHelper) {
        SeriesWiseRackFragment fragment = new SeriesWiseRackFragment();
        Bundle bundle = new Bundle();
//        bundle.putSerializable(DATABASE_HELPER, dbHelper);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dbHelper = ((MainActivity)getActivity()).getDatabaseHelper();

        seriesIdList = setUpGameList();

        myRecyclerView = (RecyclerView) getView().findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());

        myRecyclerView.setLayoutManager(mLayoutManager);
        verticalAdapter = new SeriesWiseVerticalAdapter(getActivity(), seriesIdList, dbHelper);
        myRecyclerView.setAdapter(verticalAdapter);
    }

    private List<String> setUpGameList() {
        if (seriesIdList == null) {
            seriesIdList = new ArrayList();
        }

        //https://dl.dropboxusercontent.com/u/21785336/adventures_into_the_unknown/1/c/1.jpg

        Cursor cursorComic = dbHelper.fetchComicData(DatabaseHelper.TABLE_SERIES, DatabaseHelper.CONDITION_TRUE);

        while (cursorComic.moveToNext()) {
            String seriesId = cursorComic.getString(cursorComic.getColumnIndex(DatabaseHelper.COLUMN_ID));

            seriesIdList.add(seriesId);
        }

        return seriesIdList;

//        AllGamesAdapter mAdapter = new AllGamesAdapter(getActivity(), comicList, dbHelper);
//        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_series_wise_rack, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
