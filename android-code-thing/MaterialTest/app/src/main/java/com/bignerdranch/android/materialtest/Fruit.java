package com.bignerdranch.android.materialtest;

/**
 * Created by 我 on 2017/4/26.
 */
public class Fruit {
    private String name;

    private int imageId;

    public Fruit(String name,int imageId){
        this.name=name;
        this.imageId=imageId;
    }

    public String getName(){
        return name;
    }

    public int getImageId(){
        return imageId;
    }
}
