package com.quarkstar.goldencomics.fragment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.quarkstar.goldencomics.MainActivity;
import com.quarkstar.goldencomics.R;
import com.quarkstar.goldencomics.adapter.AllGamesAdapter;
import com.quarkstar.goldencomics.adapter.ComicData;
import com.quarkstar.goldencomics.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;


public class ComicGridFragment extends Fragment {

    private static final String DATABASE_HELPER = "database_helper";

    private RecyclerView mRecyclerView;
    private List<ComicData> comicList = new ArrayList<>();
    private DatabaseHelper dbHelper;

    private OnFragmentInteractionListener mListener;

    public ComicGridFragment() {
        // Required empty public constructor
    }

    public static ComicGridFragment newInstance(DatabaseHelper dbHelper) {
        ComicGridFragment fragment = new ComicGridFragment();
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

        mRecyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing_card);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mRecyclerView.setHasFixedSize(true);

        setUpGameList();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comic_grid, container, false);
    }

    private void setUpGameList() {
        if (comicList == null) {
            comicList = new ArrayList();
        }

        //https://dl.dropboxusercontent.com/u/21785336/adventures_into_the_unknown/1/c/1.jpg

        Cursor cursorComic = dbHelper.fetchComicData(DatabaseHelper.TABLE_COMIC, "favorite='y'");

        while (cursorComic.moveToNext()) {
            String seriesUrl = "";
            int comicId = cursorComic.getInt(cursorComic.getColumnIndex(DatabaseHelper.COLUMN_ID));
            String comicName = cursorComic.getString(cursorComic.getColumnIndex(DatabaseHelper.COLUMN_NAME));
            Log.e("comic name = ", comicName);
            String comicPageCount = cursorComic.getString(cursorComic.getColumnIndex(DatabaseHelper.COLUMN_PAGE_COUNT));
            String seriesId = cursorComic.getString(cursorComic.getColumnIndex(DatabaseHelper.COLUMN_SERIES_ID));

            Cursor cursorSeries = dbHelper.fetchComicData(DatabaseHelper.TABLE_SERIES, "_id=" + seriesId);
            while (cursorSeries.moveToNext()) {
                seriesUrl = cursorSeries.getString(cursorSeries.getColumnIndex(DatabaseHelper.COLUMN_SERIES_URL));
            }

            String comicUrl = getResources().getString(R.string.base_url) + seriesUrl + "/" + comicName + "/t/";
            String thumbImageUrl = comicUrl + "1.webp";

            ComicData comic = new ComicData();
            comic.setComicId(comicId);
            comic.setComicName(comicName);
            comic.setSeriesName(seriesUrl);
            comic.setImageUrl(thumbImageUrl);
            comic.setPageCount(Integer.parseInt(comicPageCount));

            comicList.add(comic);
        }

        AllGamesAdapter mAdapter = new AllGamesAdapter(getActivity(), comicList, dbHelper);
        mRecyclerView.setAdapter(mAdapter);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onMainFragmentInteraction(Uri uri);
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private final int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
        }
    }
}
