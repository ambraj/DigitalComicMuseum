package com.quarkstar.comicmuseum;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.quarkstar.comicmuseum.adapter.AllGamesAdapter;
import com.quarkstar.comicmuseum.adapter.ComicData;

import java.util.*;

public class MainActivity extends AppCompatActivity {

    String thumbImageUrl;
    private RecyclerView mRecyclerView;
    private List<ComicData> comicList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing_card);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setHasFixedSize(true);

        setUpGameList();
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

        Map<Integer, List<String>> comicDetail = new HashMap<>();
        comicDetail.put(0, Arrays.asList("Adventures_into_the_Unknown_001", "Adventures into the Unknown"));
        comicDetail.put(1, Arrays.asList("Big_Shot_Comics_005", "Big Shot Comics"));
        comicDetail.put(2, Arrays.asList("Space_Detective_001", "Space Detective"));
        comicDetail.put(3, Arrays.asList("Wings_013", "Wings"));

        for (int i = 0; i < 4; i++) {
            //https://dl.dropboxusercontent.com/u/21785336/Adventures_into_the_Unknown_001/t/001.jpg
            thumbImageUrl = "https://dl.dropboxusercontent.com/u/21785336/"+ comicDetail.get(i).get(0) +"/t/001.jpg";

            ComicData game = new ComicData();
            game.setgameName("Big Shot Comics " + i);
            game.setImageUrl(thumbImageUrl);
            comicList.add(game);
        }
        AllGamesAdapter mAdapter = new AllGamesAdapter(MainActivity.this, comicList, comicDetail);
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