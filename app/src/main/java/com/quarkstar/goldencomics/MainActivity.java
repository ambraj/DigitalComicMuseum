package com.quarkstar.goldencomics;

import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.quarkstar.goldencomics.adapter.AllGamesAdapter;
import com.quarkstar.goldencomics.adapter.ComicData;
import com.quarkstar.goldencomics.model.DatabaseHelper;

import java.util.*;

public class MainActivity extends AppCompatActivity {

    String thumbImageUrl;
    private RecyclerView mRecyclerView;
    private List<ComicData> comicList = new ArrayList<>();
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing_card);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setHasFixedSize(true);

        setupDatabase();

        setUpGameList();
    }

    private void setupDatabase() {
        try {
            dbHelper = new DatabaseHelper(this);
            dbHelper.createDataBase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpGameList() {
        if (comicList == null) {
            comicList = new ArrayList();
        }

        //https://dl.dropboxusercontent.com/u/21785336/adventures_into_the_unknown/1/c/1.jpg

        Cursor cursorComic = dbHelper.fetchComicData(DatabaseHelper.TABLE_COMIC, DatabaseHelper.CONDITION_TRUE);

        while (cursorComic.moveToNext()){
            String seriesUrl = "";
            String comicId = cursorComic.getString(cursorComic.getColumnIndex(DatabaseHelper.COLUMN_ID));
            String comicName = cursorComic.getString(cursorComic.getColumnIndex(DatabaseHelper.COLUMN_NAME));
            String comicPageCount = cursorComic.getString(cursorComic.getColumnIndex(DatabaseHelper.COLUMN_PAGE_COUNT));
            String seriesId = cursorComic.getString(cursorComic.getColumnIndex(DatabaseHelper.COLUMN_SERIES_ID));

            Cursor cursorSeries = dbHelper.fetchComicData(DatabaseHelper.TABLE_SERIES, "_id="+seriesId);
            while (cursorSeries.moveToNext()){
                seriesUrl = cursorSeries.getString(cursorSeries.getColumnIndex(DatabaseHelper.COLUMN_SERIES_URL));
            }

            String comicUrl = getResources().getString(R.string.base_url) + seriesUrl+"/"+comicName+"/t/";

//            for (int i = 1; i < cursorComic.getCount(); i++) {
                String thumbImageUrl = comicUrl + "1.jpg";

                ComicData comic = new ComicData();
                comic.setSeriesName(seriesUrl);
                comic.setImageUrl(thumbImageUrl);
                comicList.add(comic);
//            }
        }

//        Map<Integer, List<String>> comicDetail = new HashMap<>();
//        comicDetail.put(0, Arrays.asList("Adventures_into_the_Unknown", "Adventures into the Unknown", "51"));
//        comicDetail.put(1, Arrays.asList("Big_Shot_Comics", "Big Shot Comics", "67"));
//        comicDetail.put(2, Arrays.asList("Space_Detective", "Space Detective", "36"));
//        comicDetail.put(3, Arrays.asList("Wings", "Wings", "67"));
//        comicDetail.put(4, Arrays.asList("Santa_Claus_Funnies", "Santa Claus Funnies", "67"));
//
//        for (int i = 0; i < 4; i++) {
//            //https://dl.dropboxusercontent.com/u/21785336/Adventures_into_the_Unknown_001/t/001.jpg
//            thumbImageUrl = getResources().getString(R.string.base_url) + comicDetail.get(i).get(0) + "/t/001.jpg";
//
//            ComicData game = new ComicData();
//            game.setSeriesName("Big Shot Comics " + i);
//            game.setImageUrl(thumbImageUrl);
//            comicList.add(game);
//        }
        AllGamesAdapter mAdapter = new AllGamesAdapter(MainActivity.this, comicList, dbHelper);
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