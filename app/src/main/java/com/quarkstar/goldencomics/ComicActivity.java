/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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
import com.squareup.picasso.Picasso;
import uk.co.senab.photoview.PhotoView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComicActivity extends Activity {

    private static final String ISLOCKED_ARG = "isLocked";
    int picPosition;
    Intent intent;
    String comicIndex;
    Map<Integer, List<String>> comicDetail = new HashMap<>();
    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic);
        mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
        setContentView(mViewPager);

        intent = getIntent();
        comicIndex = intent.getExtras().get("clickedIndex").toString();

        comicDetail.put(0, Arrays.asList("Adventures_into_the_Unknown_001", "Adventures into the Unknown", "51"));
        comicDetail.put(1, Arrays.asList("Big_Shot_Comics_005", "Big Shot Comics", "67"));
        comicDetail.put(2, Arrays.asList("Space_Detective_001", "Space Detective", "36"));
        comicDetail.put(3, Arrays.asList("Wings_013", "Wings", "67"));

        mViewPager.setAdapter(new SamplePagerAdapter());

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

    private String getImageUrl(String comic_name, String comic_link, String file_name) {
        String imageUrl = getResources().getString(R.string.base_url) + comic_name + "/" + comic_link + "/" + file_name;
        Log.e("getImageUrl: ", imageUrl);
        return imageUrl;
    }

    class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.valueOf(comicDetail.get(Integer.valueOf(comicIndex)).get(2));
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            picPosition = position;
            String comic_name = comicDetail.get(Integer.valueOf(comicIndex)).get(0);
            String comic_link = getResources().getString(R.string.url_comic);
            String file_name = String.format("%03d", (position + 1)) + ".jpg";

            ImageView mImageView = new PhotoView(container.getContext());
            Picasso.with(container.getContext()).load(getImageUrl(comic_name, comic_link, file_name)).into(mImageView);

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
