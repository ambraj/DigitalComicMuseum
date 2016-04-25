package com.quarkstar.goldencomics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.quarkstar.goldencomics.database.DatabaseHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;

public class ComicDetailActivity extends AppCompatActivity {

    ImageView comicThumbnail;
    ImageView comicThumbnailBackground;
    Intent intent;
    String comicId;
    String comicIndex;
    String comicUrl;
    String thumbUrl;
    String comicName;
    int pageCount;
    Button startReadingButton;
    Button addToLibraryButton;
    DatabaseHelper dbHelper;
    String addToLibraryStatus;
    /**
     * used to record screen views.
     */
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_detail);

        dbHelper = new DatabaseHelper(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");

        startReadingButton = (Button) findViewById(R.id.button_start_reading);
        addToLibraryButton = (Button) findViewById(R.id.button_addToLibrary);

        registerClickHandlers();

        intent = getIntent();
        comicId = intent.getExtras().get("comicId").toString();
        comicIndex = intent.getExtras().get("clickedIndex").toString();
        comicUrl = intent.getExtras().getString("comicUrl").toString();
        thumbUrl = intent.getExtras().getString("thumbUrl").toString();
        comicName = intent.getExtras().getString("comicName").toString();
        pageCount = intent.getExtras().getInt("comicPageCount");

        comicThumbnailBackground = (ImageView) findViewById(R.id.imageView_comic_preview_background);
        comicThumbnail = (ImageView) findViewById(R.id.imageView_comic_preview_thumb);

        Bitmap icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.thumb_not_available);
        Bitmap blurred = blurRenderScript(ComicDetailActivity.this, icon, 25);
                comicThumbnailBackground.setImageBitmap(blurred);

//        Picasso.with(this).load(thumbUrl).networkPolicy(NetworkPolicy.OFFLINE).into(comicThumbnail);
        PicassoCache.getPicassoInstance(this).load(thumbUrl).networkPolicy(NetworkPolicy.OFFLINE)
            .into(comicThumbnail, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    //Try again online if cache failed
                    PicassoCache.getPicassoInstance(ComicDetailActivity.this)
                        .load(thumbUrl)
                        .into(comicThumbnail, new Callback() {
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

//        Picasso.with(this).load(thumbUrl).into(comicThumbnailBackground, new Callback() {
//            @Override
//            public void onSuccess() {
//                Bitmap blurred = blurRenderScript(ComicDetailActivity.this, ((BitmapDrawable)comicThumbnailBackground.getBackground()).getBitmap(), 25);
//                comicThumbnailBackground.setImageBitmap(blurred);
//            }
//
//            @Override
//            public void onError() {
//
//            }
//        });

        Cursor cursorComic = dbHelper.fetchComicData(DatabaseHelper.TABLE_COMIC, DatabaseHelper.COLUMN_ID+"="+comicId);

        while (cursorComic.moveToNext()) {
            addToLibraryStatus = cursorComic.getString(cursorComic.getColumnIndex("favorite"));
//            TextView seriesTextView = (TextView) holder.recyclerView.getRootView().findViewById(R.id.series_textView);
            if(addToLibraryStatus.equals("y")) {
                addToLibraryButton.setText("Remove from library");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        // Send initial screen screen view hit.
        sendScreenImageName();

    }

    /**
     * Record a screen view hit for the visible one
     */
    private void sendScreenImageName() {
        String name = "Comic detail page";

        // [START screen_view_hit]
        Log.i("Activity", "Setting screen name: " + name);
        mTracker.setScreenName("Image~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void registerClickHandlers(){
        startReadingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ComicDetailActivity.this, ViewComicActivity.class);
                intent.putExtra("comicId", comicId);
                intent.putExtra("comicUrl", comicUrl);
                intent.putExtra("comicName", comicName);
                intent.putExtra("comicPageCount", pageCount);
                ComicDetailActivity.this.startActivity(intent);
            }
        });

        addToLibraryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addToLibraryStatus.equals("n")) {
//                    Toast.makeText(ComicDetailActivity.this, "Added to library!", Toast.LENGTH_LONG).show();
                    addToLibraryButton.setText("Remove from library");
                } else {
//                    Toast.makeText(ComicDetailActivity.this, "Removed from library!", Toast.LENGTH_LONG).show();
                    addToLibraryButton.setText("Add to library");
                }
                dbHelper.addComicToLibrary(comicId, addToLibraryStatus.equals("y") ? "n":"y");

                addToLibraryStatus = addToLibraryStatus.equals("y") ? "n":"y";
            }
        });
    }



    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * to blur background image
     */
    @SuppressLint("NewApi")
    public static Bitmap blurRenderScript(Context context, Bitmap smallBitmap, int radius) {
        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bitmap = Bitmap.createBitmap(
            smallBitmap.getWidth(), smallBitmap.getHeight(),
            Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(context);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
            Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(radius); // radius must be 0 < r <= 25
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;

    }

    private static Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }

}
