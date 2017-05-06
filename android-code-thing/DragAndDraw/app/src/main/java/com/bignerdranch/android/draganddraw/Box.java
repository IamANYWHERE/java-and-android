package com.bignerdranch.android.draganddraw;

import android.graphics.PointF;

import java.io.Serializable;

/**
 * Created by 我 on 2017/4/11.
 */
public class Box{
    private PointF mOrigin;
    private PointF mCurrent;
    private float mDegrees;
    public Box(){

    }
    public Box(PointF origin){
        mOrigin=origin;
        mCurrent=origin;
        mDegrees=0;
    }
    public void setOrigin(PointF origin){
        mOrigin=origin;
    }
    public PointF getOrigin(){
        return mOrigin;
    }
    public void setCurrent(PointF current){
        mCurrent=current;
    }
    public PointF getCurrent(){
        return mCurrent;
    }
    public void changeDegrees(float degrees){
        mDegrees+=degrees;
    }
    public float getDegrees(){
        return mDegrees;
    }
}
