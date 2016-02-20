package com.quarkstar.comicmuseum.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.quarkstar.comicmuseum.ComicActivity;
import com.quarkstar.comicmuseum.R;
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

        public CustomViewHolder(View view) {
            super(view);
            this.icon = (ImageView) view.findViewById(R.id.comic_thumbnail);
            this.game = (TextView) view.findViewById(R.id.comic_title);
            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, ComicActivity.class);
            intent.putExtra("clickedIndex", this.getLayoutPosition());
            mContext.startActivity(intent);
        }

    }

}

