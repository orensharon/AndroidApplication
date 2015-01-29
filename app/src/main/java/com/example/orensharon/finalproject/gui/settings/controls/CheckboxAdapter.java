package com.example.orensharon.finalproject.gui.settings.controls;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.sessions.SettingsSession;

import java.util.ArrayList;

/**
 * Created by orensharon on 11/30/14.
 */
public class CheckboxAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<Content> mObjects;

    private SettingsSession mSettingsSession;


    public CheckboxAdapter(Context context, ArrayList<Content> contents) {

        mContext = context;
        mObjects = contents;
        mSettingsSession = new SettingsSession(context);

        mLayoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public Object getItem(int position) {
        return mObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        //if we are at the first position then return 0, last 1, other 2
        if (position == this.getCount() - 1) {
            return 1;
        } else if (position == 0) {
            return 0;
        }
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // For each item in the list - add it

        View view;

        view = convertView;

        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.control_list_view_row, parent, false);
        }

        //AddRoundedStyleToList(position, view);
        AddContentItem(position, view);

        return view;
    }



    private void AddContentItem(int position, View view) {

        Content c = getContent(position);

        // Fill the content title
        ((TextView) view.findViewById(R.id.list_view_title)).setText(c.getTitle());

        // Setting the details icon
        ImageView detailsIcon;
        detailsIcon = (ImageView)view.findViewById(R.id.list_view_details_icon);

        // Adding details icon
        detailsIcon.setImageResource(R.drawable.arrow);


        // Set the checkbox and its listener
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.list_view_check_box);
        checkBox.setOnCheckedChangeListener(myCheckChangList);

        checkBox.setTag(position);
        checkBox.setChecked(mSettingsSession.getUserContentItem(c.getTitle()));

        /*view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkBox.setChecked(!checkBox.isChecked());
            }

        });*/
    }

    private void AddRoundedStyleToList(int position, View view) {
        int rowType;
        rowType = getItemViewType(position);

        // This will add a rounded styles to the top and bottom items
        if (rowType == 0) {
            view.setBackgroundResource(R.drawable.list_view_row_bg_top);
        } else if (rowType == 1) {
            view.setBackgroundResource(R.drawable.list_view_row_bg_bottom);
        }
    }

    Content getContent(int position) {
        return ((Content) getItem(position));
    }

    public ArrayList<Content> getBox() {
        ArrayList<Content> box = new ArrayList<Content>();
        for (Content c : mObjects) {
            //if (c.box)
                box.add(c);
        }
        return box;
    }

    AdapterView.OnItemClickListener itemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Content content;

            content = getContent(position);

            // Toggle the selection of the content item
            //content.setChecked(!content.getChecked());

        }
    };

    CompoundButton.OnCheckedChangeListener myCheckChangList = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {

            Content content;


            content = getContent((Integer) buttonView.getTag());
            content.setChecked(isChecked);

            mSettingsSession.setUserContentItem(content.getTitle(), content.getChecked());
        }
    };
}


