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
import com.quarkstar.goldencomics.ComicDetailActivity;
import com.quarkstar.goldencomics.R;
import com.quarkstar.goldencomics.database.DatabaseHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AllGamesAdapter extends RecyclerView.Adapter<AllGamesAdapter.CustomViewHolder> {

    private List<ComicData> feedItemList;
    private Context mContext;
    private DatabaseHelper dbHelper;

    public AllGamesAdapter(Context context, List<ComicData> feedItemList, DatabaseHelper dbHelper) {
        this.feedItemList = feedItemList;
        this.mContext = context;
        this.dbHelper = dbHelper;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comic_card_layout, null);
        view.setTag(feedItemList);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, int i) {
        final ComicData feedItem = feedItemList.get(i);

        Log.e("imageURL", feedItem.getImageUrl());

        Log.e("thumbImageUrl = ", feedItem.getSeriesName());

        Picasso.with(mContext).load(feedItem.getImageUrl()).networkPolicy(NetworkPolicy.OFFLINE).into(customViewHolder.icon);

        Picasso.with(mContext).load(feedItem.getImageUrl()).networkPolicy(NetworkPolicy.OFFLINE)
            .into(customViewHolder.icon, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    //Try again online if cache failed
                    Picasso.with(mContext)
                        .load(feedItem.getImageUrl())
                        .error(R.drawable.profile)
                        .into(customViewHolder.icon, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError() {
                                Log.v("Picasso","Could not fetch image");
                            }
                        });
                }
            });

        customViewHolder.seriesName.setText(feedItem.getSeriesName());
        customViewHolder.seriesNameView.setText(feedItem.getSeriesName().replace("_", " "));
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView icon;
        final TextView seriesName;
        final TextView seriesNameView;
        final RelativeLayout downloadIconLayout;
        ImageView downloadIcon;

        public CustomViewHolder(final View view) {
            super(view);
            this.icon = (ImageView) view.findViewById(R.id.comic_thumbnail);
            this.seriesName = (TextView) view.findViewById(R.id.comic_title_temp);
            this.seriesNameView = (TextView) view.findViewById(R.id.comic_title);
            this.downloadIconLayout = (RelativeLayout) view.findViewById(R.id.download_icon_div);
            this.downloadIcon = (ImageView) view.findViewById(R.id.download_icon);

            view.setOnClickListener(this);

//            downloadIconLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast toast = Toast.makeText(mContext, "downloading...", Toast.LENGTH_LONG);
//                    toast.show();
//
//                    int comicIndex = CustomViewHolder.this.getLayoutPosition();
//                    final int pageCountInComic = Integer.valueOf(comicDetail.get(Integer.valueOf(CustomViewHolder.this.getLayoutPosition())).get(2));
//                    for (int i = 0; i < pageCountInComic; i++) {
//                        String comic_name = comicDetail.get(Integer.valueOf(comicIndex)).get(0);
//                        String comic_link = mContext.getResources().getString(R.string.url_comic);
//                        String file_name = String.format("%03d", (i + 1)) + ".jpg";
//
//                        final int pageCount = i;
//
//                        final String comicUrl = getImageUrl(comic_name, comic_link, file_name);
//                        Picasso.with(mContext).load(comicUrl).fetch(new Callback() {
//                            @Override
//                            public void onSuccess() {
//                                Log.e("Downloaded", comicUrl);
//                                if (pageCount == pageCountInComic - 1) {
//                                    downloadIcon.setImageResource(R.drawable.ic_cloud_done_white);
//                                }
//                            }
//
//                            @Override
//                            public void onError() {
//                                Log.e("ERROR", comicUrl);
//                            }
//                        });
//                    }
//                }
//            });
        }

//        private String getImageUrl(String comic_name, String comic_link, String file_name) {
//            String imageUrl = mContext.getResources().getString(R.string.base_url) + "/" + comic_name + "/" + comic_link + "/" + file_name;
//            Log.e("getImageUrl: ", imageUrl);
//            return imageUrl;
//        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            String comicUrl = seriesName.getText().toString();
            List<ComicData> comicList = (ArrayList<ComicData>) v.getTag();
            int comicId = comicList.get(position).getComicId();
            String comicName = comicList.get(position).getComicName();
            String thumbUrl = comicList.get(position).getImageUrl();
            int pageCount = comicList.get(position).getPageCount();

            Log.e("AllGamesAdapter", "comic name = " + comicName);

            Intent intent = new Intent(mContext, ComicDetailActivity.class);
            intent.putExtra("clickedIndex", this.getLayoutPosition());
            intent.putExtra("comicId", comicId);
            intent.putExtra("comicUrl", comicUrl);
            intent.putExtra("comicName", comicName);
            intent.putExtra("thumbUrl", thumbUrl);
            intent.putExtra("comicPageCount", pageCount);
            mContext.startActivity(intent);
        }

    }

}

