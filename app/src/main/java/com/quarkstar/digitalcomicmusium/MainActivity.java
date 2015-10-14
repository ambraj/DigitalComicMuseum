package com.quarkstar.digitalcomicmusium;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import com.quarkstar.digitalcomicmusium.adapter.AllGamesAdapter;
import com.quarkstar.digitalcomicmusium.adapter.ComicData;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView mImageView;
    private RecyclerView mRecyclerView;
    private AllGamesAdapter mAdapter;
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

        mImageView = (ImageView) findViewById(R.id.icon);
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

//        List<Integer> thumbList = Arrays.asList(R.drawable.thumb1, R.drawable.thumb2, R.drawable.thumb3,
//            R.drawable.thumb4, R.drawable.thumb5, R.drawable.thumb6, R.drawable.thumb7, R.drawable.thumb8, R.drawable.thumb9);

        for (int i = 1; i < 30; i++) {
            ComicData game = new ComicData();
            game.setgameName("Big Shot Comics " + i);
//            game.setIcon(thumbList.get(i / 2));
            String thumbImageUrl;
            if(i<10)
                thumbImageUrl = "https://dl.dropboxusercontent.com/u/21785336/Big_Shot_Comics_005/t/00"+i+".jpg";
            else
                thumbImageUrl = "https://dl.dropboxusercontent.com/u/21785336/Big_Shot_Comics_005/t/0"+i+".jpg";

            game.setImageUrl(thumbImageUrl);

            Log.e("Ambuj ", thumbImageUrl);

            comicList.add(game);
        }
        mAdapter = new AllGamesAdapter(comicList);
        mRecyclerView.setAdapter(mAdapter);
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

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
