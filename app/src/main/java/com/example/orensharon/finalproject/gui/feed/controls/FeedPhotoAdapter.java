package com.example.orensharon.finalproject.gui.feed.controls;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.orensharon.finalproject.R;

import com.example.orensharon.finalproject.logic.CustomPicasso;
import com.squareup.picasso.Callback;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        FeedItemHolder holder = null;


        if(convertView == null)
        {
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            convertView = inflater.inflate(mLayoutInflater, parent, false);

            holder = new FeedItemHolder();
            holder.imageIcon = (ImageView)convertView.findViewById(R.id.icon);
            holder.textCreatedDate = (TextView)convertView.findViewById(R.id.feed_time_text_view);
            //holder.textScore = (TextView)convertView.findViewById(R.id.score);

            convertView.setTag(holder);
        }
        else
        {
            holder = (FeedItemHolder)convertView.getTag();
        }

        ProgressBar progressBar = null;
        if (convertView != null) {
            progressBar = (ProgressBar) convertView.findViewById(R.id.feed_item_progress_Bar);
            progressBar.setVisibility(View.VISIBLE);
        }

        FeedPhotoItem feedItem = data[position];

        SimpleDateFormat df = new SimpleDateFormat("hh:ss MM/dd/yyyy");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        String result = df.format(Long.parseLong(feedItem.getDateCreated()));

        holder.textCreatedDate.setText(result);
        //holder.textTitle.setText(feedItem.gametype);
        //holder.imageIcon.setImageResource(feedItem.getPhoto());

        final FeedItemHolder finalHolder = holder;
        Transformation transformation = new Transformation() {

            @Override public Bitmap transform(Bitmap source) {
                int targetWidth = finalHolder.imageIcon.getWidth();

                double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                int targetHeight = (int) (targetWidth * aspectRatio);
                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                if (result != source) {
                    // Same bitmap is returned if sizes are the same
                    source.recycle();
                }
                return result;
            }

            @Override public String key() {
                return "transformation" + " desiredWidth";
            }
        };

        CustomPicasso.getImageLoader(mContext, mToken)
                .load(feedItem.getPhoto())
                .error(R.drawable.action_bar)
                .transform(transformation)
                .into(holder.imageIcon, new ImageLoadedCallback(progressBar) {
                    @Override
                    public void onSuccess() {
                        if (this.progressBar != null) {
                            this.progressBar.setVisibility(View.GONE);
                        }
                    }
                });



        return convertView;
    }

    static class FeedItemHolder
    {
        ImageView imageIcon;
        TextView textCreatedDate;
        //TextView textScore;
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
