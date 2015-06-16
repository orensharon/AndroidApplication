package com.example.orensharon.finalproject.gui.feed.controls;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.orensharon.finalproject.R;

import com.example.orensharon.finalproject.logic.CustomPicasso;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by orensharon on 5/3/15.
 */
public class FeedPhotoAdapter extends ArrayAdapter<FeedPhotoItem> {

    private Context mContext;
    private int mLayoutInflater;
    private FeedPhotoItem data[] = null;
    String mToken;


    public FeedPhotoAdapter(Context context, int layoutResId, FeedPhotoItem[] data, String token) {
        super(context, layoutResId, data);
        this.mLayoutInflater = layoutResId;
        this.mContext = context;
        this.data = data;
        mToken = token;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        FeedItemHolder holder = null;

        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();

        float dpHeight = displayMetrics.heightPixels; // / displayMetrics.density;
        final float dpWidth = displayMetrics.widthPixels; // / displayMetrics.density;


        if(convertView == null)
        {
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            convertView = inflater.inflate(mLayoutInflater, parent, false);

            holder = new FeedItemHolder();
            holder.imageIcon = (ImageView)convertView.findViewById(R.id.icon);
            holder.textCreatedDate = (TextView)convertView.findViewById(R.id.feed_time_text_view);
            holder.textGeoLocation = (TextView)convertView.findViewById(R.id.feed_location_text_view);

            convertView.setTag(holder);
        }
        else
        {
            holder = (FeedItemHolder)convertView.getTag();
        }

        RelativeLayout container;
        ProgressBar progressBar = null;
        if (convertView != null) {

            container = (RelativeLayout)convertView.findViewById(R.id.feed_image_container);
            container.getLayoutParams().width = (int) dpWidth;
            container.getLayoutParams().height = (int) dpWidth;

            holder.imageIcon.getLayoutParams().width = (int) dpWidth;
            holder.imageIcon.getLayoutParams().height = (int) dpWidth;
            progressBar = (ProgressBar) convertView.findViewById(R.id.feed_item_progress_Bar);
            progressBar.setVisibility(View.VISIBLE);
        }

        FeedPhotoItem feedItem = data[position];

        SimpleDateFormat df = new SimpleDateFormat("HH:mm MM/dd/yyyy");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        String result = df.format(feedItem.getDateCreated());

        String geoLocation = feedItem.getGeoLocation();
        if (geoLocation.equals("null") || geoLocation.equals("")) {
            geoLocation = "Not available";
        }
        holder.textCreatedDate.setText(result);
        holder.textGeoLocation.setText(geoLocation);


        final FeedItemHolder finalHolder = holder;
        CustomPicasso.getImageLoader(mContext, mToken)
                .load(feedItem.getPhoto())
                .fit()
                .centerCrop()
                .into(holder.imageIcon, new ImageLoadedCallback(progressBar) {
                    @Override
                    public void onSuccess() {
                        if (this.progressBar != null) {
                            this.progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError() {
                        if (this.progressBar != null) {
                            this.progressBar.setVisibility(View.GONE);
                        }
                        CustomPicasso.getImageLoader(mContext, mToken)
                                .load(R.drawable.image_not_found)
                                .resize((int) dpWidth, (int) dpWidth)
                                .into(finalHolder.imageIcon);
                    }
                });



        return convertView;
    }

    static class FeedItemHolder
    {
        ImageView imageIcon;
        TextView textCreatedDate;
        TextView textGeoLocation;
    }

    private class ImageLoadedCallback implements Callback {
        ProgressBar progressBar;

        public  ImageLoadedCallback(ProgressBar progBar){
            progressBar = progBar;
        }

        @Override
        public void onSuccess() {

        }

        @Override
        public void onError() {

        }
    }


}
