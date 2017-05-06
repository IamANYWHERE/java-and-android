package com.bignerdranch.android.photogallery;

import android.net.Uri;

/**
 * Created by 我 on 2017/3/22.
 */
public class GalleryItem {
    private String mCaption;
    private String mId;
    private String mUrl;
    private String mOwner;

    public String getUrl() {
        return mUrl;
    }

    public String getOwner(){
        return mOwner;
    }
    public void setOwner(String Owner){
        mOwner=Owner;
    }
    public Uri getPhotoPageUri(){
        return Uri.parse("http://www.flickr.com/photos/")
                .buildUpon()
                .appendPath(mOwner)
                .appendPath(mId)
                .build();
    }
    public void setUrl(String url) {
        mUrl = url;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String toString(){
        return mCaption;
    }
}
