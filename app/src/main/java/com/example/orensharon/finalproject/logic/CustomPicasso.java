package com.example.orensharon.finalproject.logic;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.example.orensharon.finalproject.ApplicationConstants;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by orensharon on 5/12/15.
 */
public class CustomPicasso {

    private static Picasso sPicasso;
    private static String mToken;

    private CustomPicasso() {
    }

    public static Picasso getImageLoader(final Context context, final String token) {

        mToken = token;

        OkHttpClient picassoClient = new OkHttpClient();

       /* File folder = new File(Environment.getExternalStorageDirectory() + "/feed_cache");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (success) {
            // Do something on success

            File httpCacheDir = new File(Environment.getExternalStorageDirectory() + "/feed_cache");
            long httpCacheSize = 50 * 1024 * 1024; // 10 MiB
            try {
                Cache cache = new Cache(httpCacheDir, httpCacheSize);
                picassoClient.setCache(cache);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            // Do something else on failure
        }*/


        picassoClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader(ApplicationConstants.HEADER_AUTHORIZATION, token)
                        .build();
                return chain.proceed(newRequest);
            }
        });

        if (sPicasso == null) {
            sPicasso = new Picasso
                    .Builder(context)
                    .downloader(new OkHttpDownloader(picassoClient))
                    .memoryCache(new LruCache(240000))
                    .build();
        }

        return sPicasso;
    }
}
