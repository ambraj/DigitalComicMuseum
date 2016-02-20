package com.quarkstar.goldencomics.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.quarkstar.goldencomics.ComicActivity;
import com.quarkstar.goldencomics.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllGamesAdapter extends RecyclerView.Adapter<AllGamesAdapter.CustomViewHolder> {

    private List<ComicData> feedItemList;
    private Context mContext;
    Map<Integer, List<String>> comicDetail = new HashMap<>();

    public AllGamesAdapter(Context context, List<ComicData> feedItemList, Map<Integer, List<String>> comicDetail) {
        this.feedItemList = feedItemList;
        this.mContext = context;
        this.comicDetail = comicDetail;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comic_grid_item, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        ComicData feedItem = feedItemList.get(i);

        Log.e("imageURL", feedItem.getImageUrl());
        //Download image using picasso library

        String thumbImageUrl = "https://dl.dropboxusercontent.com/u/21785336/"+comicDetail.get(i).get(0)+"/t/001.jpg";

        Picasso.with(mContext).load(thumbImageUrl).into(customViewHolder.icon);

        customViewHolder.game.setText(comicDetail.get(i).get(1));
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView icon;
        final TextView game;
        final RelativeLayout downloadIconLayout;
        ImageView downloadIcon;

        public CustomViewHolder(final View view) {
            super(view);
            this.icon = (ImageView) view.findViewById(R.id.comic_thumbnail);
            this.game = (TextView) view.findViewById(R.id.comic_title);
            this.downloadIconLayout = (RelativeLayout) view.findViewById(R.id.download_icon_div);
            this.downloadIcon = (ImageView) view.findViewById(R.id.download_icon);

            view.setOnClickListener(this);

            downloadIconLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast toast = Toast.makeText(mContext, "downloading...", Toast.LENGTH_LONG);
                    toast.show();

                    int comicIndex = CustomViewHolder.this.getLayoutPosition();
                    final int pageCountInComic = Integer.valueOf(comicDetail.get(Integer.valueOf(CustomViewHolder.this.getLayoutPosition())).get(2));
                    for (int i = 0; i < pageCountInComic; i++) {
                        String comic_name = comicDetail.get(Integer.valueOf(comicIndex)).get(0);
                        String comic_link = mContext.getResources().getString(R.string.url_comic);
                        String file_name = String.format("%03d", (i+1)) + ".jpg";

                        final int pageCount = i;

                        final String comicUrl = getImageUrl(comic_name, comic_link, file_name);
                        Picasso.with(mContext).load(comicUrl).fetch(new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.e("Downloaded",comicUrl);
                                if(pageCount == pageCountInComic-1){
                                    downloadIcon.setImageResource(R.drawable.ic_cloud_done_white);
                                }
                            }

                            @Override
                            public void onError() {
                                Log.e("ERROR",comicUrl);
                            }
                        });
                    }
                }
            });
        }

        private String getImageUrl(String comic_name, String comic_link, String file_name) {
            String imageUrl = mContext.getResources().getString(R.string.base_url) + "/" + comic_name + "/" + comic_link + "/" + file_name;
            Log.e("getImageUrl: ", imageUrl);
            return imageUrl;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, ComicActivity.class);
            intent.putExtra("clickedIndex", this.getLayoutPosition());
            mContext.startActivity(intent);
        }

    }

}

