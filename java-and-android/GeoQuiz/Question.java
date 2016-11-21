package com.bignerdranch.android.geoquiz;

/**
 * Created by 我 on 2016/10/30.
 */
public class Question {
    private int mTextResId;
    private boolean mAnswerTrue;
    private boolean mIsCheat;
    public Question(int textResId ,boolean AnswerTrue){
        mTextResId=textResId;
        mAnswerTrue=AnswerTrue;
        mIsCheat=false;
    }
    public int getTextResId(){
        return mTextResId;
    }
    public boolean isAnswerTrue(){
        return mAnswerTrue;
    }
    public boolean isCheat(){return mIsCheat;}
    public void setTextResId(int textResId){
        mTextResId=textResId;
    }
    public void setAnswerTrue(boolean answerTrue){
        mAnswerTrue=answerTrue;
    }
    public void setCheat(boolean isCheat){mIsCheat=isCheat;}
}
