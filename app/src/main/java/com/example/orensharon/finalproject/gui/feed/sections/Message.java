package com.example.orensharon.finalproject.gui.feed.sections;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.orensharon.finalproject.R;
import com.example.orensharon.finalproject.gui.feed.FeedActivity;
import com.example.orensharon.finalproject.gui.feed.sections.containers.BaseContainerFragment;
import com.example.orensharon.finalproject.gui.feed.sections.containers.ContainerContactsFragment;
import com.example.orensharon.finalproject.gui.feed.sections.containers.ContainerPhotosFragment;

/**
 * Created by orensharon on 5/23/15.
 */
public class Message extends Fragment {

    private TextView mMessageTextView, mRetryTextView;
    private ImageView mMessageImageView;

    public final static String MESSAGE_KEY = "message";
    public final static String MESSAGE_ICON_KEY = "icon";
    public final static String MESSAGE_SECTION_KEY = "section";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view;
        view = inflater.inflate(R.layout.fragment_feed_error, container, false);

        initView(view);

        return view;
    }

    // Init message components
    private void initView(View view) {


        mMessageTextView = (TextView) view.findViewById(R.id.feed_section_message_text_view);
        mRetryTextView = (TextView) view.findViewById(R.id.retry_load_content_text_view);
        mMessageImageView = (ImageView) view.findViewById(R.id.feed_section_message_image_view);

        // Get message args.
        Bundle bundle = getArguments();

        // Set the message
        mMessageTextView.setText(bundle.getString(MESSAGE_KEY));
        mMessageImageView.setImageResource(bundle.getInt(MESSAGE_ICON_KEY));

        // Init retry link
        initRetryOnClick(bundle.getString(MESSAGE_SECTION_KEY));
    }


    // Create message retry click listener
    private void initRetryOnClick(final String section)
    {
        final View.OnClickListener mRetryTextView_onClick = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String toReload;
                Fragment fragment = null;


                if (section.equals(FeedActivity.FEED_PHOTOS)) {
                    fragment = new ContainerPhotosFragment();
                } else if (section.equals(FeedActivity.FEED_CONTACTS)) {
                    fragment = new ContainerContactsFragment();
                }

                if (fragment != null) {
                    ((BaseContainerFragment) getParentFragment()).replaceFragment(fragment, section, false);
                }
            }
        };

        mRetryTextView.setOnClickListener(mRetryTextView_onClick);

    }

}
