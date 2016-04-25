package com.quarkstar.goldencomics;

import android.content.Context;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class PicassoCache {

    /**
     * Static Picasso Instance
     */
    private static Picasso picassoInstance = null;

    /**
     * PicassoCache Constructor
     *
     * @param context application Context
     */
    private PicassoCache(Context context) {

        Downloader downloader = new OkHttpDownloader(context, Integer.MAX_VALUE);
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(downloader);

        picassoInstance = builder.build();
//        picassoInstance.setIndicatorsEnabled(true);
//        picassoInstance.setLoggingEnabled(true);
        Picasso.setSingletonInstance(picassoInstance);

    }

    /**
     * Get Singleton Picasso Instance
     *
     * @param context application Context
     * @return Picasso instance
     */
    public static Picasso getPicassoInstance(Context context) {
        if (picassoInstance == null) {
            new PicassoCache(context);
            return picassoInstance;
        }

        return picassoInstance;
    }

} 