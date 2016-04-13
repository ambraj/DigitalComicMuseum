package com.quarkstar.goldencomics.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.quarkstar.goldencomics.ComicDetailActivity;
import com.quarkstar.goldencomics.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by belladunovska on 17/07/15.
 */
public class SeriesWiseHorizontalAdapter extends RecyclerView.Adapter<SeriesWiseHorizontalAdapter.ViewHolder> {
    private List<ComicData> dataset;
    public Context context;

    public SeriesWiseHorizontalAdapter(Context context, List<ComicData> dataset) {
        this.context = context;
        this.dataset = dataset;
    }


    @Override
    public SeriesWiseHorizontalAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.series_wise_horizontal_item, parent, false);
        view.setTag(dataset);

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.horizontalItemTextView.setText(dataset.get(position).getSeriesName());
        Picasso.with(context).load(dataset.get(position).getImageUrl()).networkPolicy(NetworkPolicy.OFFLINE)
            .into(holder.randomImageView, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError() {
                //Try again online if cache failed
                Picasso.with(context)
                    .load(dataset.get(position).getImageUrl())
                    .error(R.drawable.profile)
                    .into(holder.randomImageView, new Callback() {
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
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView horizontalItemTextView;
        public ImageView randomImageView;

        public ViewHolder(View v) {
            super(v);
            horizontalItemTextView = (TextView) v.findViewById(R.id.comic_title);
            randomImageView = (ImageView) v.findViewById(R.id.comic_thumbnail);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            String comicUrl = horizontalItemTextView.getText().toString();
            List<ComicData> comicList = (ArrayList<ComicData>) v.getTag();
            String comicName = comicList.get(position).getComicName();
            String thumbUrl = comicList.get(position).getImageUrl();
            int pageCount = comicList.get(position).getPageCount();
            int comicId = comicList.get(position).getComicId();

            Intent intent = new Intent(context, ComicDetailActivity.class);
            intent.putExtra("clickedIndex", this.getLayoutPosition());
            intent.putExtra("comicId", comicId);
            intent.putExtra("comicUrl", comicUrl);
            intent.putExtra("thumbUrl", thumbUrl);
            intent.putExtra("comicName", comicName);
            intent.putExtra("comicPageCount", pageCount);

            context.startActivity(intent);
        }

    }
}