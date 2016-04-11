package com.quarkstar.goldencomics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

public class ComicDetailActivity extends AppCompatActivity {

    ImageView comicThumbnail;
    ImageView comicThumbnailBackground;
    Intent intent;
    String comicIndex;
    String comicUrl;
    String thumbUrl;
    String comicName;
    int pageCount;
    Button startReadingButton;
    Button addToLibraryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        startReadingButton = (Button) findViewById(R.id.button_start_reading);
        addToLibraryButton = (Button) findViewById(R.id.button_addToLibrary);

        registerClickHandlers();

        intent = getIntent();
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

        Picasso.with(this).load(thumbUrl).into(comicThumbnail);
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
    }

    private void registerClickHandlers(){
        startReadingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ComicDetailActivity.this, ViewComicActivity.class);
//                intent.putExtra("clickedIndex", this.getLayoutPosition());
                intent.putExtra("comicUrl", comicUrl);
                intent.putExtra("comicName", comicName);
                intent.putExtra("comicPageCount", pageCount);
                ComicDetailActivity.this.startActivity(intent);
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
