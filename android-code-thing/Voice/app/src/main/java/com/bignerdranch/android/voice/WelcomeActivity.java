package com.bignerdranch.android.voice;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import java.util.Random;

/**
 * Created by 我 on 2017/6/3.
 */
public class WelcomeActivity extends AppCompatActivity {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.start);
        SpeechUtility.createUtility(this, SpeechConstant.APPID+"=58fd9ca8");


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(WelcomeActivity.this,DiaryActivity.class);
                startActivity(intent);
                WelcomeActivity.this.finish();
            }
        },1000);
    }
    private void setImage(ImageView image){
        Random random=new Random();
        int index=random.nextInt(9)+1;
        int id=getResources().getIdentifier("q"+index,"drawable",getPackageName());
        Glide.with(this).load(id).into(image);
    }
}
