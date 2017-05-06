package com.bignerdranch.android.sunset;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created by 我 on 2017/4/15.
 */
public class SunsetFragment extends Fragment {

    public static String TAG="SunsetFragment";
    private View mSceneView;
    private View mSunView;
    private View mSkyView;
    private View mSunStoneView;
    private View mSunShadowView;

    private int mBlueSkyColor;
    private int mSunsetSkyColor;
    private int mNightSkyColor;

    private int mState;
    //-1表示黑夜0表示变化态1表示白天

    private float mScaleBig;
    private float mScaleSmall;
    public static SunsetFragment newInstance(){
        return new SunsetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_sunset,container,false);

        mSceneView=view;
        mSkyView=view.findViewById(R.id.sky);
        mSunView=view.findViewById(R.id.sun);
        mSunStoneView=view.findViewById(R.id.sun_stone);
        mSunShadowView=view.findViewById(R.id.sun_shadow);

        Resources resources=getResources();
        mBlueSkyColor=resources.getColor(R.color.blue_sky);
        mSunsetSkyColor=resources.getColor(R.color.sunset_sky);
        mNightSkyColor=resources.getColor(R.color.night_sky);

        mScaleBig=(float) 1.05;
        mScaleSmall=(float)0.95;
        mState=1;
        mSceneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimation(mState);
            }
        });
        startSunAnimation();
        return view;
    }

    private void startAnimation(final int state){
        float sunYStart=mSunView.getTop();
        float sunYEnd=mSkyView.getHeight();
        float shadowYStart=mSunShadowView.getTop();
        float shadowYEnd=0-mSunShadowView.getHeight()-10;
        Log.i(TAG,sunYEnd+" "+shadowYStart);
        ObjectAnimator heightAnimator=ObjectAnimator
                .ofFloat(mSunView,"y",sunYStart,sunYEnd)
                .setDuration(3000);
        heightAnimator.setInterpolator(new AccelerateInterpolator());
        ObjectAnimator shadowHeight=ObjectAnimator
                .ofFloat(mSunShadowView,"y",shadowYStart,shadowYEnd)
                .setDuration(3000);
        shadowHeight.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator sunsetSkyAnimator=ObjectAnimator
                .ofInt(mSkyView,"backgroundColor",mBlueSkyColor,mSunsetSkyColor)
                .setDuration(3000);
        sunsetSkyAnimator.setEvaluator(new ArgbEvaluator());

        ObjectAnimator nightSkyAnimator=ObjectAnimator
                .ofInt(mSkyView,"backgroundColor",mSunsetSkyColor,mNightSkyColor)
                .setDuration(1500);
        nightSkyAnimator.setEvaluator(new ArgbEvaluator());

        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet
                .play(heightAnimator)
                .with(sunsetSkyAnimator)
                .before(nightSkyAnimator);

        ObjectAnimator heightUpAnimator=ObjectAnimator
                .ofFloat(mSunView,"y",sunYEnd,sunYStart)
                .setDuration(3000);
        heightUpAnimator.setInterpolator(new AccelerateInterpolator());
        ObjectAnimator shadowDownAnimator=ObjectAnimator
                .ofFloat(mSunShadowView,"y",shadowYEnd,shadowYStart)
                .setDuration(3000);
        shadowDownAnimator.setStartDelay(1500);
        shadowDownAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator sunsetSkyUpAnimator=ObjectAnimator
                .ofInt(mSkyView,"backgroundColor",mSunsetSkyColor,mBlueSkyColor)
                .setDuration(3000);
        sunsetSkyUpAnimator.setEvaluator(new ArgbEvaluator());

        ObjectAnimator nightSkyUpAnimator=ObjectAnimator
                .ofInt(mSkyView,"backgroundColor",mNightSkyColor,mSunsetSkyColor)
                .setDuration(1500);
        nightSkyUpAnimator.setEvaluator(new ArgbEvaluator());
        AnimatorSet animatorSetUp=new AnimatorSet();
        animatorSetUp.play(heightUpAnimator)
                .with(sunsetSkyUpAnimator)
                .after(nightSkyUpAnimator);

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mState=-1;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSetUp.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mState=1;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        if (state==-1){
            animatorSetUp.start();
            shadowDownAnimator.start();
            mState=0;
        }else if(state==1){
            animatorSet.start();
            shadowHeight.start();
            mState=0;
        }
    }
    private void startSunAnimation(){

        ObjectAnimator sunScaleXAnimator=ObjectAnimator
                .ofFloat(mSunStoneView,"scaleX",(float) 1.0,mScaleBig,(float)1.0,mScaleSmall,(float)1.0)
                .setDuration(1500);
        sunScaleXAnimator.setInterpolator(new AccelerateInterpolator());
        ObjectAnimator sunScaleYAnimator=ObjectAnimator
                .ofFloat(mSunStoneView,"scaleY",(float) 1.0,mScaleBig,(float)1.0,mScaleSmall,(float)1.0)
                .setDuration(1500);
        sunScaleYAnimator.setInterpolator(new AccelerateInterpolator());
        ObjectAnimator shadowScaleXAnimator=ObjectAnimator
                .ofFloat(mSunShadowView,"scaleX",(float)1.0,mScaleBig,(float)1.0,mScaleSmall,(float)1.0)
                .setDuration(1500);
        shadowScaleXAnimator.setInterpolator(new AccelerateInterpolator());
        ObjectAnimator shadowScaleYAnimator=ObjectAnimator
                .ofFloat(mSunShadowView,"scaleY",(float)1.0,mScaleBig,(float)1.0,mScaleSmall,(float)1.0)
                .setDuration(1500);
        shadowScaleYAnimator.setInterpolator(new AccelerateInterpolator());
        sunScaleXAnimator.setRepeatCount(-1);
        sunScaleYAnimator.setRepeatCount(-1);
        shadowScaleXAnimator.setRepeatCount(-1);
        shadowScaleYAnimator.setRepeatCount(-1);
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.play(sunScaleXAnimator)
                .with(sunScaleYAnimator)
                .with(shadowScaleXAnimator)
                .with(shadowScaleYAnimator);
        animatorSet.start();

    }
}
