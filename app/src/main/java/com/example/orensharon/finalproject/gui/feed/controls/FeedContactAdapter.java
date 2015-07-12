package com.example.orensharon.finalproject.gui.feed.controls;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.orensharon.finalproject.R;

public class FeedContactAdapter extends ArrayAdapter<FeedContactItem> {

    private Context mContext;
    private int mLayoutInflater;
    private FeedContactItem data[] = null;
    String mToken;


    public FeedContactAdapter(Context context, int layoutResId, FeedContactItem[] data, String token) {
        super(context, layoutResId, data);
        this.mLayoutInflater = layoutResId;
        this.mContext = context;
        this.data = data;
        mToken = token;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FeedItemHolder holder;


        if(convertView == null)
        {
            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
            convertView = inflater.inflate(mLayoutInflater, parent, false);

            holder = new FeedItemHolder();
            holder.imageIcon = (ImageView)convertView.findViewById(R.id.icon);
            holder.textTitle = (TextView)convertView.findViewById(R.id.contact_name_text_view);

            convertView.setTag(holder);
        }
        else
        {
            holder = (FeedItemHolder)convertView.getTag();
        }

        FeedContactItem feedItem = data[position];
        holder.textTitle.setText(feedItem.getDisplayName());

        return convertView;
    }

    public FeedContactItem getData(int i) {
        return data[i];
    }

    static class FeedItemHolder
    {
        ImageView imageIcon;
        TextView textTitle;

    }


}
