package com.quarkstar.goldencomics.adapter;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import com.quarkstar.goldencomics.MainActivity;
import com.quarkstar.goldencomics.R;
import com.quarkstar.goldencomics.database.DatabaseHelper;
import com.quarkstar.goldencomics.databinding.SeriesWiseVerticalItemBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by belladunovska on 17/07/15.
 */
public class SeriesWiseVerticalAdapter extends RecyclerView.Adapter<SeriesWiseVerticalAdapter.ViewHolder> {
    private final List<String> dataset;
    private Context context;
    private Map<Integer, Parcelable> scrollStatePositionsMap = new HashMap<>();
    private DatabaseHelper dbHelper;

    public SeriesWiseVerticalAdapter(Context context, List<String> dataset, DatabaseHelper dbHelper) {
        this.context = context;
        this.dataset = dataset;
        this.dbHelper = dbHelper;
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }


    @Override
    public SeriesWiseVerticalAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        SeriesWiseVerticalItemBinding binding = DataBindingUtil.setContentView((MainActivity)context, R.layout.series_wise_vertical_item);
        binding.setUser("****************&&&&&&&&&&&&&&&************(***");

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.series_wise_vertical_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        List<ComicData> comicList = new ArrayList<>();
        Cursor cursorComic = dbHelper.fetchComicData(DatabaseHelper.TABLE_COMIC, DatabaseHelper.COLUMN_SERIES_ID+"="+dataset.get(position));

        while (cursorComic.moveToNext()) {
            String seriesUrl = "";
            int comicId= cursorComic.getInt(cursorComic.getColumnIndex(DatabaseHelper.COLUMN_ID));
            String comicName = cursorComic.getString(cursorComic.getColumnIndex(DatabaseHelper.COLUMN_NAME));
            String comicPageCount = cursorComic.getString(cursorComic.getColumnIndex(DatabaseHelper.COLUMN_PAGE_COUNT));
            String seriesId = cursorComic.getString(cursorComic.getColumnIndex(DatabaseHelper.COLUMN_SERIES_ID));

            Cursor cursorSeries = dbHelper.fetchComicData(DatabaseHelper.TABLE_SERIES, "_id=" + seriesId);
            while (cursorSeries.moveToNext()) {
                seriesUrl = cursorSeries.getString(cursorSeries.getColumnIndex(DatabaseHelper.COLUMN_SERIES_URL));
            }

            String comicUrl = context.getResources().getString(R.string.base_url) + seriesUrl + "/" + comicName + "/t/";
            String thumbImageUrl = comicUrl + "1.webp";

            ComicData comic = new ComicData();
            comic.setComicId(comicId);
            comic.setComicName(comicName);
            comic.setSeriesName(seriesUrl);
            comic.setImageUrl(thumbImageUrl);
            comic.setPageCount(Integer.parseInt(comicPageCount));

            comicList.add(comic);
        }
        if(comicList.size() < 3){
            final TextView moreTextView = (TextView) holder.recyclerView.getRootView().findViewById(R.id.more_textView);
            moreTextView.setVisibility(View.GONE);



//            moreTextView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ComicGridFragment fragment = new ComicGridFragment();
//                    FragmentTransaction fragmentTransaction = ((FragmentActivity)((FragmentActivity)context).getParent()).getSupportFragmentManager().beginTransaction();
//                    fragmentTransaction.replace(R.id.fragment_container, fragment);
//                    fragmentTransaction.commit();
//                }
//            });
        }

        cursorComic = dbHelper.fetchComicData(DatabaseHelper.TABLE_SERIES, DatabaseHelper.COLUMN_ID+"="+dataset.get(position));

        while (cursorComic.moveToNext()) {
            String seriesName = cursorComic.getString(cursorComic.getColumnIndex(DatabaseHelper.COLUMN_NAME));
            TextView seriesTextView = (TextView) holder.recyclerView.getRootView().findViewById(R.id.series_textView);
            seriesTextView.setText(seriesName.replace("_", " "));
        }

        holder.recyclerView.setLayoutManager(linearLayoutManager);
        holder.recyclerView.setAdapter(new SeriesWiseHorizontalAdapter(context, comicList));
        holder.setPosition(position);
        if (scrollStatePositionsMap.containsKey(position)) {
            holder.recyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    holder.recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                    holder.recyclerView.getLayoutManager().onRestoreInstanceState(scrollStatePositionsMap.get(position));
                    return false;
                }
            });
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public RecyclerView recyclerView;
        public int position;

        public ViewHolder(final View itemView) {
            super(itemView);

            recyclerView = (RecyclerView) itemView.findViewById(R.id.horizontal_grid_view);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        scrollStatePositionsMap.put(position, recyclerView.getLayoutManager().onSaveInstanceState());
                    }
                }
            });
        }

        public void setPosition(int position) {
            this.position = position;
        }

    }
}