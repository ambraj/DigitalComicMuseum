package com.quarkstar.goldencomics;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import com.quarkstar.goldencomics.adapter.AllGamesAdapter;
import com.quarkstar.goldencomics.adapter.ComicData;
import com.quarkstar.goldencomics.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class AllComicsFromSeriesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private List<ComicData> comicList = new ArrayList<>();
    private DatabaseHelper dbHelper;
    Intent intent;
    String seriesName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_comics_from_series);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        seriesName = intent.getExtras().get("seriesName").toString();

        String titleName = new String(seriesName);
        setTitle(titleName.replace("_", " "));

        dbHelper = new DatabaseHelper(this);

        int width = this.getResources().getDisplayMetrics().widthPixels;
        int height = this.getResources().getDisplayMetrics().heightPixels;

        mRecyclerView = (RecyclerView) findViewById(R.id.grid_recycler_view);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing_card);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setHasFixedSize(true);

        setUpGameList();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpGameList() {
        if (comicList == null) {
            comicList = new ArrayList();
        }

        String seriesId = "";
        Cursor cursorSeries = dbHelper.fetchComicData(DatabaseHelper.TABLE_SERIES, "url='" + seriesName+"'");
        while (cursorSeries.moveToNext()) {
            seriesId = cursorSeries.getString(cursorSeries.getColumnIndex(DatabaseHelper.COLUMN_ID));
        }

        Cursor cursorComic = dbHelper.fetchComicData(DatabaseHelper.TABLE_COMIC, "series_id="+seriesId);

        while (cursorComic.moveToNext()) {
            int comicId = cursorComic.getInt(cursorComic.getColumnIndex(DatabaseHelper.COLUMN_ID));
            String comicName = cursorComic.getString(cursorComic.getColumnIndex(DatabaseHelper.COLUMN_NAME));
            String comicPageCount = cursorComic.getString(cursorComic.getColumnIndex(DatabaseHelper.COLUMN_PAGE_COUNT));
            String comicUrl = getResources().getString(R.string.base_url) + seriesName + "/" + comicName + "/t/";
            String thumbImageUrl = comicUrl + "1.webp";

            ComicData comic = new ComicData();
            comic.setComicId(comicId);
            comic.setComicName(comicName);
            comic.setSeriesName(seriesName);
            comic.setImageUrl(thumbImageUrl);
            comic.setPageCount(Integer.parseInt(comicPageCount));

            comicList.add(comic);
        }

        AllGamesAdapter mAdapter = new AllGamesAdapter(this, comicList, dbHelper);
        mRecyclerView.setAdapter(mAdapter);
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
