package com.kk.android.dayonejournal;

public class GalleryItem {
    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    private String mCaption;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    private String mId;

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    private String mUrl;

    @Override
    public String toString() {
        return mCaption;
    }
}
