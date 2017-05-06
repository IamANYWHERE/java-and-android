package com.bignerdranch.android.beatbox;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by 我 on 2016/11/17.
 */
public abstract class SingleFragmentActivity2 extends AppCompatActivity{


    protected abstract Fragment createFragment();
    @LayoutRes
    protected int getLayoutResId(){
        return R.layout.activity_fragment_2;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        FragmentManager fm=getSupportFragmentManager();
        Fragment fragment=fm.findFragmentById(R.id.fragment_container_2);
        if (fragment==null){
            fragment=createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container_2,fragment)
                    .commit();
        }
    }
}
