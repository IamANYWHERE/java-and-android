package com.bignerdranch.android.voice;

import java.util.Date;
import java.util.UUID;

/**
 * Created by 我 on 2017/5/31.
 */
public class Diary {

    private String mTitle;

    private String mImageId;

    private Date mDate;

    private UUID mUUID;

    private String mContent;

    public Diary(String title,String imageId,UUID uuid){
        this.mTitle=title;
        this.mImageId=imageId;
        mDate=new Date();
        mUUID=uuid;
        mContent="";
    }

    public Diary(String title,String imageId){
        this(title,imageId,UUID.randomUUID());
    }

    public Diary(){
        this("","",UUID.randomUUID());
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getImageId() {
        return mImageId;
    }

    public void setImageId(String imageId) {
        mImageId = imageId;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public UUID getUUID() {
        return mUUID;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }
}
