package com.bignerdranch.android.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER_IS_TRUE=
            "com.bignerdranch.android.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN=
            "com.bignerdranch.android.geoquiz.anwer_shown";
    private static  final String KEY_JUDGE="judge";
    private static final String TAG="CheatActivity";
    private boolean mAnswerIsTrue;
    private boolean mIAmCheat=false;
    private Button mShowAnswer;
    private TextView mAnswerTextView;
    private TextView mAPILevelTextView;
    public static Intent newIntent(Context packageContext,boolean answerIsTrue){
        Intent i=new Intent(packageContext,CheatActivity.class);
        i.putExtra(EXTRA_ANSWER_IS_TRUE,answerIsTrue);
        return i;
    }
    public static boolean wasAnswerShown(Intent result){
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN,false);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);
        if(savedInstanceState!=null){
            mIAmCheat=savedInstanceState.getBoolean(KEY_JUDGE,false);
            if(mIAmCheat){
                Log.d(TAG, "onCreate: mIAmCheat="+mIAmCheat);
                setAnswerShownResult(true);
            }
        }
        else{
            Log.d(TAG, "onCreate: savedInstanceState is null");
        }
        mAnswerIsTrue=getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE,false);
        mAnswerTextView=(TextView)findViewById(R.id.answerTextView);
        mShowAnswer=(Button)findViewById(R.id.showAnswerButton);
        mShowAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAnswerIsTrue){
                    mAnswerTextView.setText(R.string.true_button);
                }
                else {
                    mAnswerTextView.setText(R.string.false_button);
                }
                mIAmCheat=true;
                setAnswerShownResult(true);

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                    int cx = mShowAnswer.getWidth() / 2;
                    int cy = mShowAnswer.getHeight() / 2;
                    float radius = mShowAnswer.getWidth();
                    Animator anim = ViewAnimationUtils.createCircularReveal(mShowAnswer, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mShowAnswer.setVisibility(View.INVISIBLE);
                        }
                    });
                    anim.start();
                }else{
                    mShowAnswer.setVisibility(View.INVISIBLE);
                }
            }
        });
        mAPILevelTextView=(TextView)findViewById(R.id.APILevel);
        mAPILevelTextView.append("API level "+Build.VERSION.SDK_INT);
    }
    private void setAnswerShownResult(boolean isAnswerShown){
        Intent data=new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN,isAnswerShown);
        setResult(RESULT_OK,data);
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(KEY_JUDGE,mIAmCheat);
        Log.d(TAG, "Cheat onSaveInstanceState: called"+mIAmCheat);
    }
}
