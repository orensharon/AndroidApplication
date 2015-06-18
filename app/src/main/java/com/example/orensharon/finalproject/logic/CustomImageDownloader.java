package com.example.orensharon.finalproject.logic;

import android.content.Context;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;

/**
 * Created by orensharon on 6/17/15.
 */
public class CustomImageDownloader extends BaseImageDownloader {

    public CustomImageDownloader(Context context) {
        super(context);
    }

    public CustomImageDownloader(Context context, int connectTimeout, int readTimeout) {
        super(context, connectTimeout, readTimeout);
    }

    @Override
    protected HttpURLConnection createConnection(String url, Object extra) throws IOException {
        HttpURLConnection conn = super.createConnection(url, extra);
        Map<String, String> headers = (Map<String, String>) extra;
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                conn.setRequestProperty(header.getKey(), header.getValue());
            }
        }
        return conn;
    }
}