package com.quarkstar.digitalcomicmusium;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.*;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    ImageView mImageView;
    String html;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int mUIFlag = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

        getWindow().getDecorView().setSystemUiVisibility(mUIFlag);

        mImageView = (ImageView)findViewById(R.id.imageView);

        class DownloadImage extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                return null;
            }

            // onPostExecute displays the results of the AsyncTask.
            @Override
            protected void onPostExecute(String result) {

                html = result;

                new Thread(new Runnable() {

                    private String getMediaStorageDir() {
                        // To be safe, you should check that the SDCard is mounted
                        // using Environment.getExternalStorageState() before doing this.
                        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
//                            + "/Android/data/"
//                            + getApplicationContext().getPackageName()
//                            + "/Files");
                              + "/Download");

                        // This location works best if you want the created images to be shared
                        // between applications and persist after your app has been uninstalled.

                        // Create the storage directory if it does not exist
                        if (! mediaStorageDir.exists()){
                            if (! mediaStorageDir.mkdirs()){
                                return null;
                            }
                        }
                        return mediaStorageDir.getPath();
                    }

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

                    private Bitmap loadImageFromStorage(String image_name)
                    {
                        try {
                            File f = new File(getApplicationContext().getFilesDir(), image_name);
                            return BitmapFactory.decodeStream(new FileInputStream(f));
                        }
                        catch (FileNotFoundException e)
                        {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    private Bitmap loadImageFromNetwork(String url, String image_name){
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
                        return getResources().getString(R.string.base_url) + "/" + comic_name + "/" + comic_link + "/" + file_name;
                    }

                    private String getImageNameLocal(String comic_name, String comic_link, String file_name) {
                        return comic_name + "_" + comic_link + "_" + file_name;
                    }

                    private Bitmap loadImage() {
                        String comic_name = getResources().getString(R.string.comic_1);
                        String comic_link = getResources().getString(R.string.url_comic);
                        String file_name  = "001.jpg";
                        Bitmap bitmap = loadImageFromStorage(getImageNameLocal(comic_name, comic_link, file_name));
                        if (bitmap == null)
                        {
                            bitmap = loadImageFromNetwork(getImageUrl(comic_name, comic_link, file_name), getImageNameLocal(comic_name, comic_link, file_name));
                        }

                        return bitmap;
                    }

                    public void run() {
                        final Bitmap b = loadImage();

                        runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              mImageView.setImageBitmap(b);
                                          }
                                      }
                        );
                    }
                }).start();
            }
        }

        new DownloadImage().execute();
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
}
