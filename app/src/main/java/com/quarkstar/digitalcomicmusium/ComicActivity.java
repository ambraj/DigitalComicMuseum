/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.quarkstar.digitalcomicmusium;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import uk.co.senab.photoview.PhotoView;

import java.io.*;
import java.net.URL;

/**
 * Lock/Unlock button is added to the ActionBar.
 * Use it to temporarily disable ViewPager navigation in order to correctly interact with ImageView by gestures.
 * Lock/Unlock state of ViewPager is saved and restored on configuration changes.
 *
 * Julia Zudikova
 */

public class ComicActivity extends Activity {

    private static final String ISLOCKED_ARG = "isLocked";
    private ImageView mImageView;
    int picPosition;
    Bitmap bitmapComic;

    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic);
        mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
        setContentView(mViewPager);

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

     class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 27;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            picPosition = position;
            new DownloadImage().execute();
            PhotoView photoView = new PhotoView(container.getContext());
            photoView.setImageBitmap(bitmapComic);

            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

            return photoView;
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

    private class DownloadImage extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return null;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            new Thread(new Runnable() {

                private void storeImage(Bitmap image, String image_name) {
                    FileOutputStream outputStream;

                    try {
                        outputStream = openFileOutput(image_name, Context.MODE_PRIVATE);
                        image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                private Bitmap loadImageFromStorage(String image_name) {
                    try {
                        File f = new File(getApplicationContext().getFilesDir(), image_name);
                        return BitmapFactory.decodeStream(new FileInputStream(f));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                private Bitmap loadImageFromNetwork(String url, String image_name) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
                        storeImage(bitmap, image_name);
                        return bitmap;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                private String getImageUrl(String comic_name, String comic_link, String file_name) {
                    String imageUrl = getResources().getString(R.string.base_url) + "/" + comic_name + "/" + comic_link + "/" + file_name;
                    Log.e("getImageUrl: ", imageUrl);
                    return imageUrl;
                }

                private String getImageNameLocal(String comic_name, String comic_link, String file_name) {
                    return comic_name + "_" + comic_link + "_" + file_name;
                }

                private Bitmap loadImage() {
                    String comic_name = getResources().getString(R.string.comic_3);
                    String comic_link = getResources().getString(R.string.url_comic);
                    String file_name = String.format("%03d", (picPosition)) + ".jpg";
                    Log.e("filename: ", file_name);
                    Bitmap bitmap = loadImageFromStorage(getImageNameLocal(comic_name, comic_link, file_name));
                    if (bitmap == null) {
                        bitmap = loadImageFromNetwork(getImageUrl(comic_name, comic_link, file_name), getImageNameLocal(comic_name, comic_link, file_name));
                    }

                    return bitmap;
                }

                public void run() {
                    final Bitmap b = loadImage();

                    runOnUiThread(new Runnable() {
                                      @Override

                                      public void run() {
//                                          if (b == null) {
//                                              int id = getResources().getIdentifier("res:drawable/thumb_not_available.jpg", null, null);
//                                              mImageView.setImageResource(id);
//                                          } else {
                                          bitmapComic = b;
//                                          }
                                      }
                                  }
                    );
                }
            }).start();
        }
    }

}
