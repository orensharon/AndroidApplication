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
import com.example.orensharon.finalproject.gui.feed.sections.containers.BaseContainerFragment;
import com.example.orensharon.finalproject.gui.feed.sections.containers.ContainerContactsFragment;
import com.example.orensharon.finalproject.gui.feed.sections.containers.ContainerPhotosFragment;

/**
 * Created by orensharon on 5/23/15.
 */
public class Message extends Fragment {

    private TextView mMessageTextView, mRetryTextView;
    private ImageView mMessageImageView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view;


        view = inflater.inflate(R.layout.fragment_feed_error, container, false);

        // Init message components
        mMessageTextView = (TextView) view.findViewById(R.id.feed_section_message_text_view);
        mRetryTextView = (TextView) view.findViewById(R.id.retry_load_content_text_view);
        mMessageImageView = (ImageView) view.findViewById(R.id.feed_section_message_image_view);

        // Get message args.
        Bundle bundle = getArguments();

        // Set the message
        mMessageTextView.setText(bundle.getString("message"));
        mMessageImageView.setImageResource(bundle.getInt("icon"));

        // Init retry link
        initRetryOnClick(bundle.getString("section"));

        return view;
    }


    private void initRetryOnClick(final String section)
    {

        // create click listener

        final View.OnClickListener mRetryTextView_onClick = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String toReload;
                Fragment fragment = null;


                if (section.equals("photos")) {
                    fragment = new ContainerPhotosFragment();
                } else if (section.equals("contacts")) {
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
