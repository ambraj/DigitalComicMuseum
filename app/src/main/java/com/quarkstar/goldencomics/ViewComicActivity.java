/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.quarkstar.goldencomics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import com.quarkstar.goldencomics.database.DatabaseHelper;
import com.quarkstar.goldencomics.ui.HackyViewPager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import uk.co.senab.photoview.PhotoView;

public class ViewComicActivity extends Activity {

    private static final String ISLOCKED_ARG = "isLocked";
    int picPosition;
    Intent intent;
    String comicId;
    String comicUrl;
    String comicName;
    int pageCount;
    //    Map<Integer, List<String>> comicDetail = new HashMap<>();
    DatabaseHelper dbHelper;
    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_comic);
        mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
        setContentView(mViewPager);

        dbHelper = new DatabaseHelper(this);

        intent = getIntent();
        comicId = intent.getExtras().get("comicId").toString();
        comicUrl = intent.getExtras().getString("comicUrl").toString();
        comicName = intent.getExtras().getString("comicName").toString();
        pageCount = intent.getExtras().getInt("comicPageCount");

        SamplePagerAdapter pagerAdapter = new SamplePagerAdapter();
        mViewPager.setAdapter(pagerAdapter);

        if (savedInstanceState != null) {
            boolean isLocked = savedInstanceState.getBoolean(ISLOCKED_ARG, false);
            ((HackyViewPager) mViewPager).setLocked(isLocked);
        }

        // hide the status bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private boolean isViewPagerActive() {
        return (mViewPager != null && mViewPager instanceof HackyViewPager);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (isViewPagerActive()) {
            outState.putBoolean(ISLOCKED_ARG, ((HackyViewPager) mViewPager).isLocked());
        }
        super.onSaveInstanceState(outState);
    }

    private String getImageUrl(String comic_link, String file_name) {
        String imageUrl = getResources().getString(R.string.base_url) + comicUrl + "/" + comicName + "/" + comic_link + "/" + file_name;
        Log.e("getImageUrl: ", imageUrl);
        return imageUrl;
    }

    class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.valueOf(pageCount);
        }

        @Override
        public View instantiateItem(final ViewGroup container, int position) {
            picPosition = position;
            final String comic_link = getResources().getString(R.string.url_comic);
            final String file_name = (position + 1) + ".webp";

//            dbHelper.updateLastReadPageNo(comicId, (position+1));

            final ImageView mImageView = new PhotoView(container.getContext());
//            Picasso.with(container.getContext()).load(getImageUrl(comic_link, file_name)).networkPolicy(NetworkPolicy.OFFLINE).into(mImageView);

            PicassoCache.getPicassoInstance(container.getContext())
                .load(getImageUrl(comic_link, file_name))
                .placeholder( R.drawable.progress_animation )
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(mImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed
                        PicassoCache.getPicassoInstance(container.getContext())
                            .load(getImageUrl(comic_link, file_name))
                            .placeholder( R.drawable.progress_animation )
                            .into(mImageView, new Callback() {
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

            container.addView(mImageView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

            return mImageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

}
