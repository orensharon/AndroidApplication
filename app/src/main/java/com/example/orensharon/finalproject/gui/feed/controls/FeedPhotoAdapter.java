package com.example.orensharon.finalproject.gui.feed.controls;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.orensharon.finalproject.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import org.ocpsoft.prettytime.PrettyTime;
import java.util.Date;

/**
 * Created by orensharon on 5/3/15.
 * The Photo feed adapter
 */
public class FeedPhotoAdapter extends ArrayAdapter<FeedPhotoItem> {

    private Context mContext;
    private int mLayoutInflater;
    private LayoutInflater mInflater;
    private FeedPhotoItem data[] = null;



    public FeedPhotoAdapter(Context context, int layoutResId, FeedPhotoItem[] data) {
        super(context, layoutResId, data);
        this.mLayoutInflater = layoutResId;
        this.mContext = context;
        this.mInflater = ((Activity)mContext).getLayoutInflater();
        this.data = data;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        FeedItemHolder holder;

        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();

        //float dpHeight = displayMetrics.heightPixels; // / displayMetrics.density;
        final float dpWidth = displayMetrics.widthPixels; // / displayMetrics.density;


        if(convertView == null)
        {
            convertView = mInflater.inflate(mLayoutInflater, parent, false);

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
        ProgressBar progressBar;


        container = (RelativeLayout)convertView.findViewById(R.id.feed_image_container);
        container.getLayoutParams().width = (int) dpWidth;
        container.getLayoutParams().height = (int) dpWidth;

        holder.imageIcon.getLayoutParams().width = (int) dpWidth;
        holder.imageIcon.getLayoutParams().height = (int) dpWidth;
        progressBar = (ProgressBar) convertView.findViewById(R.id.feed_item_progress_Bar);
        progressBar.setVisibility(View.VISIBLE);


        FeedPhotoItem feedItem = getItem(position);

        // Get the date and parse it
        String friendlyDate;
        PrettyTime prettyTime = new PrettyTime();
        friendlyDate = prettyTime.format(new Date(feedItem.getDateCreated()));

        // Put the geo location
        String geoLocation = feedItem.getGeoLocation();
        if (geoLocation.equals("null") || geoLocation.equals("")) {
            geoLocation = mContext.getString(R.string.photo_location_not_availavle);
        }

        //holder.textCreatedDate.setText("ID" + feedItem.getRealPhotoId());
        holder.textCreatedDate.setText(friendlyDate);
        holder.textGeoLocation.setText(geoLocation);

        final ProgressBar finalProgressBar = progressBar;

        // Put the Image
        ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance



        //holder.imageIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageLoader.displayImage(feedItem.getPhoto(), holder.imageIcon, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                finalProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });



        return convertView;
    }


    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public FeedPhotoItem getItem(int position) {
        return data[position];
    }

    static class FeedItemHolder
    {
        ImageView imageIcon;
        TextView textCreatedDate;
        TextView textGeoLocation;
    }


}
