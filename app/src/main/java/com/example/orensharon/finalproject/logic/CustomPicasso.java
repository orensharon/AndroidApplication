package com.example.orensharon.finalproject.logic;

import android.content.Context;
import android.net.Uri;

import com.example.orensharon.finalproject.ApplicationConstants;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

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

    public static Picasso getImageLoader(final Context context, String token) {

        mToken = token;
        if (sPicasso == null) {
            Picasso.Builder builder = new Picasso.Builder(context);
            builder.downloader(new CustomOkHttpDownloader(context));
            sPicasso = builder.build();
        }
        return sPicasso;
    }

    private static class CustomOkHttpDownloader extends OkHttpDownloader {

        public CustomOkHttpDownloader(Context context) {

            // Max value of int to get cache
            super(context, Integer.MAX_VALUE);
        }



        @Override
        protected HttpURLConnection openConnection(final Uri uri) throws IOException {
            HttpURLConnection connection = super.openConnection(uri);
            connection.setRequestProperty(ApplicationConstants.HEADER_AUTHORIZATION, mToken);
            return connection;
        }
    }

}
