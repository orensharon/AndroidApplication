package com.example.orensharon.finalproject.gui.settings.controls;

public class Content {
    private String mTitle;
    private boolean mChecked;

    public Content (String title, boolean checked) {
        mTitle = title;
        mChecked = checked;
    }

    public String getTitle() {
        return mTitle;
    }

    public boolean getChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }
}
