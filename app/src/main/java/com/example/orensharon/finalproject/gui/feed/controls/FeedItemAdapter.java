package com.example.orensharon.finalproject.gui.feed.controls;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.orensharon.finalproject.R;
import com.squareup.picasso.Picasso;

/**
 * Created by orensharon on 5/3/15.
 */
public class FeedItemAdapter extends ArrayAdapter<FeedItem> {

    private Context mContext;
    private int mLayoutInflater;
    private FeedItem data[] = null;

    public FeedItemAdapter(Context context, int layoutResId, FeedItem[] data) {
        super(context, layoutResId, data);
        this.mLayoutInflater = layoutResId;
        this.mContext = context;
        this.data = data;
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
            //holder.textTitle = (TextView)convertView.findViewById(R.id.gameType);
            //holder.textScore = (TextView)convertView.findViewById(R.id.score);

            convertView.setTag(holder);
        }
        else
        {
            holder = (FeedItemHolder)convertView.getTag();
        }

        FeedItem feedItem = data[position];
        //holder.textScore.setText(feedItem.score);
        //holder.textTitle.setText(feedItem.gametype);
        //holder.imageIcon.setImageResource(feedItem.getPhoto());
        Picasso.with(this.mContext).load(feedItem.getPhoto()).into(holder.imageIcon);

        return convertView;
    }

    static class FeedItemHolder
    {
        ImageView imageIcon;
        //TextView textTitle;
        //TextView textScore;
    }
}
