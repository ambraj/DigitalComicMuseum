package com.quarkstar.digitalcomicmusium.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.quarkstar.digitalcomicmusium.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class AllGamesAdapter extends RecyclerView.Adapter<AllGamesAdapter.AllGamesGridHolder> {

    private List<ComicData> allGamesList;

    private Context mContext;

    public AllGamesAdapter(List<ComicData> allGamesList) {
        this.allGamesList = allGamesList;
    }

    @Override
    public AllGamesGridHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comic_grid_item, null);
        mContext = viewGroup.getContext();
        AllGamesGridHolder ml = new AllGamesGridHolder(v);
        return ml;
    }

    @Override
    public void onBindViewHolder(AllGamesGridHolder allGamesGridHolder, int i) {
        ComicData allGamesItem = allGamesList.get(i);

//        allGamesGridHolder.icon.setImageResource(allGamesItem.getIcon());
//        allGamesGridHolder.icon.setImageURI();
        allGamesGridHolder.game.setText(allGamesItem.getGameName());

        class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

            private String url;
            private ImageView imageView;

            public ImageLoadTask(String url, ImageView imageView) {
                this.url = url;
                this.imageView = imageView;
            }

            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    URL urlConnection = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    return myBitmap;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                imageView.setImageBitmap(result);
            }

        }

        new ImageLoadTask(allGamesItem.getImageUrl(), allGamesGridHolder.icon).execute();

    }

    @Override
    public int getItemCount() {
        return (null != allGamesList ? allGamesList.size() : 0);
    }

    public class AllGamesGridHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ImageView icon;
        protected TextView game;

        public AllGamesGridHolder(View view) {
            super(view);
            this.icon = (ImageView) view.findViewById(R.id.icon);
            this.game = (TextView) view.findViewById(R.id.comic);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            Intent intent = new Intent(mContext, ComicActivity.class);
//            startActivity(intent);

        }

    }


}

