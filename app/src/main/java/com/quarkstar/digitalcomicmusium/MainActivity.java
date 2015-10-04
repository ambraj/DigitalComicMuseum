package com.quarkstar.digitalcomicmusium;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    ImageView mImageView;
    String murl;

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

        // Uses AsyncTask to create a task away from the main UI thread. This task takes a
        // URL string and uses it to create an HttpUrlConnection. Once the connection
        // has been established, the AsyncTask downloads the contents of the webpage as
        // an InputStream. Finally, the InputStream is converted into a string, which is
        // displayed in the UI by the AsyncTask's onPostExecute method.
        class DownloadWebpageTask extends AsyncTask<String, Void, String> {

            // Given a URL, establishes an HttpUrlConnection and retrieves
            // the web page content as a InputStream, which it returns as
            // a string.
            private String downloadUrl(String myurl) throws IOException {
                // Build and set timeout values for the request.
                URLConnection connection = (new URL(myurl)).openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                // Read and store the result line by line then return the entire string.
                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder html = new StringBuilder();
                for (String line; (line = reader.readLine()) != null; ) {
                    html.append(line);
                }
                in.close();

                return html.toString();
            }
            @Override
            protected String doInBackground(String... urls) {

                // params comes from the execute() call: params[0] is the url.
                try {
                    return downloadUrl(urls[0]);
                } catch (IOException e) {
                    return "Unable to retrieve web page. URL may be invalid.";
                }
            }
            // onPostExecute displays the results of the AsyncTask.
            @Override
            protected void onPostExecute(String result) {
                int index = result.indexOf("img src='cache");
                int lindex = result.indexOf("' width", index);

                result = result.substring(index + 9, lindex);

                murl = "http://digitalcomicmuseum.com/preview/" + result;

                new Thread(new Runnable() {

                    private Bitmap loadImageFromNetwork(String str){
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(str).getContent());

                            File mediaFile;
                            String mImageName="1" + ".jpg";
                            mediaFile = new File(getFilesDir() + File.separator + mImageName);

                            FileOutputStream fos = new FileOutputStream(mediaFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            fos.close();

                            String[] st = fileList();

                            //FileInputStream fis = new FileInputStream(getFilesDir() + fileList()[0]);
                            Bitmap btmp = BitmapFactory.decodeFile(getFilesDir() + File.separator + fileList()[0]);

                            //Log.e("FILE:", st[0]);

                            return btmp;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    public void run() {
                        final Bitmap b = loadImageFromNetwork(murl);

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

        new DownloadWebpageTask().execute("http://digitalcomicmuseum.com/preview/index.php?did=20137");
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
