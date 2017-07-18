package com.bignerdranch.android.voice;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.data.ByteArrayFetcher;
import com.github.clans.fab.FloatingActionButton;
import com.haozhang.lib.AnimatedRecordingView;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by 我 on 2017/5/31.
 */
public class DiaryContentActivity extends AppCompatActivity {

    private static final int REQUEST_CHANGE_PICTURE=0;
    private static final String TAG="content";

    public static final String DIARY_UUID="diary_uuid";
    private static final String LANGUAGE_SELECT="language";

    private ImageView mDiaryImageView;
    private Diary mDiary;
    private EditText mDiaryContentText;
    private EditText mDiaryContentTitle;
    private NestedScrollView mNestedScrollView;
    private CardView mCardContent;
    private float mPosX;
    private float mPosY;
    private float mCurPosX;
    private float mCurPosY;
    private List<String> mSentences;
    private  List<String> mSentencesTwo;
    private FloatingActionButton mFabPlay;
    private TextView mTouchText;
    private SpeechSynthesizer mSynthesizer;
    private int mMyChoice;
    private boolean isPlay;
    private boolean mEnd;
    private TextWatcher textWatcher;
    private Handler mHandler=new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mDiaryContentText.removeTextChangedListener(textWatcher);
                    String temp=(String)msg.obj;
                    mDiaryContentText.setText(temp);
                    mDiary.setContent(temp);
                    DiaryLab.get(DiaryContentActivity.this)
                            .updateDiary(mDiary);
                    mDiaryContentText.addTextChangedListener(textWatcher);
                    break;
                case 2:
                    mDiaryContentText.removeTextChangedListener(textWatcher);
                    String temp1=(String)msg.obj;
                    mDiaryContentText.append(temp1);
                    mDiary.setContent(mDiaryContentText.getText().toString());
                    DiaryLab.get(DiaryContentActivity.this)
                            .updateDiary(mDiary);
                    mDiaryContentText.addTextChangedListener(textWatcher);
                    break;
            }
            super.handleMessage(msg);
        }

    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        Intent intent=getIntent();
        UUID uuid=(UUID) intent.getSerializableExtra(DIARY_UUID);
        mDiary=DiaryLab.get(this).getDiary(uuid);
        if (mDiary==null){
            Log.i(TAG,"mdiary=null");
            finish();
        }
        String diaryTitle=mDiary.getTitle();
        String diaryImageId=mDiary.getImageId();
        Date date=mDiary.getDate();
        String diaryContent=mDiary.getContent();
        Toolbar toolbar=(Toolbar)findViewById(R.id.coll_toolbar);
        CollapsingToolbarLayout collapsing=(CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        mDiaryImageView=(ImageView)findViewById(R.id.diary_image_view);
        mDiaryContentText=(EditText)findViewById(R.id.diary_content_text);
        mDiaryContentTitle=(EditText) findViewById(R.id.diary_content_title);
        FloatingActionButton fabChange=(FloatingActionButton)findViewById(R.id.fab_menu_item_1);
        FloatingActionButton fabDelete=(FloatingActionButton)findViewById(R.id.fab_menu_item_2);
        mFabPlay=(FloatingActionButton)findViewById(R.id.fab_menu_item_3);
        android.support.design.widget.FloatingActionButton fabSelect=
                (android.support.design.widget.FloatingActionButton)findViewById(R.id.fab_select);
        TextView timeText=(TextView)findViewById(R.id.diary_content_time);
        mNestedScrollView=(NestedScrollView)findViewById(R.id.nest_view);
        mCardContent=(CardView)findViewById(R.id.card_content);
        mTouchText=(TextView)findViewById(R.id.touch);
        setGestureListener();
        mMyChoice=getLanguage();
        mEnd=false;
        isPlay=false;
        mSentences=new LinkedList<>();
        mSentencesTwo=new LinkedList<>();
        final AnimatedRecordingView arv=(AnimatedRecordingView)findViewById(R.id.speech_view);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        String dateFormat="yyyy年MM月dd日 HH：mm";
        String dateString=(String) DateFormat.format(dateFormat,date);
        timeText.setText(dateString);
        collapsing.setTitle(diaryTitle);
        mDiaryContentTitle.setText(diaryTitle);
        if (diaryImageId.length()<=3){
            int id=this.getResources().getIdentifier(diaryImageId,"drawable",getPackageName());
            Glide.with(this)
                    .load(id)
                    .into(mDiaryImageView);
        }else {
            Glide.with(this).load(diaryImageId).into(mDiaryImageView);
        }
        mDiaryContentText.setText(diaryContent);
        mDiaryContentTitle.addTextChangedListener(new TextWatcher() {
            private String mTitle;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTitle=s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                mDiary.setTitle(mTitle);
                DiaryLab.get(DiaryContentActivity.this)
                        .updateDiary(mDiary);
            }
        });
         textWatcher= new TextWatcher() {
            private String mContent;
            private String mNewSentence;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mContent=s.toString();
                mNewSentence="";
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNewSentence=s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                mDiary.setContent(mNewSentence);
                if(mNewSentence.length()>mContent.length())
                     mSentences.add(mNewSentence.substring(mContent.length()));
                DiaryLab.get(DiaryContentActivity.this)
                        .updateDiary(mDiary);
            }
        };
        mDiaryContentText.addTextChangedListener( textWatcher);
        fabChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePicture();
            }
        });
        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,"are you sure?",Snackbar.LENGTH_SHORT)
                        .setAction("YES", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DiaryLab.get(DiaryContentActivity.this)
                                        .deleteCrime(mDiary.getUUID());
                                backHome();
                            }
                        }).show();
            }
        });
        mFabPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play));
        mFabPlay.setLabelText("play");
        mFabPlay.setLabelVisibility(FloatingActionButton.VISIBLE);
        mFabPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });
        fabSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSingleChoiceDialog();
            }
        });
        arv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Parse.getPermission(DiaryContentActivity.this);
                writeSpeech(DiaryContentActivity.this,arv,mMyChoice);
            }
        });
    }

    private void setLanguage(int language){
        SharedPreferences sharedPreferences=
                DiaryContentActivity.this.getSharedPreferences(getString(R.string.preference_file_key),
                        Context.MODE_PRIVATE);

        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt(LANGUAGE_SELECT,language);
        editor.apply();
    }
    private int getLanguage(){
        SharedPreferences sharedPreferences=
                DiaryContentActivity.this.getSharedPreferences(getString(R.string.preference_file_key),
                        Context.MODE_PRIVATE);
        int language=sharedPreferences.getInt(LANGUAGE_SELECT,0);
        if (language==0){
            Log.i(TAG,"language=0");
        }
        return language;
    }
    private void showSingleChoiceDialog(){

        final String[] items={"普通话","四川话","粤语"};
        AlertDialog.Builder singleChoiceDialog=
                new AlertDialog.Builder(DiaryContentActivity.this);
        singleChoiceDialog.setTitle("选择语言");
        final int language=mMyChoice;
        singleChoiceDialog.setSingleChoiceItems(items, language,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMyChoice=which;
                    }
                });

        singleChoiceDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mMyChoice!=-1){
                    setLanguage(mMyChoice);
                    Toast.makeText(DiaryContentActivity.this,items[mMyChoice],Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(DiaryContentActivity.this, "MyChoice=-1", Toast.LENGTH_SHORT).show();
                }
            }
        });
        singleChoiceDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mMyChoice=language;
            }
        });
        singleChoiceDialog.show();
    }

    private void changePicture(){
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,REQUEST_CHANGE_PICTURE);
    }

    public static Intent newIntent(Context context, UUID uuid){
        Intent intent=new Intent(context, DiaryContentActivity.class);
        intent.putExtra(DIARY_UUID,uuid);
        return intent;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                backHome();
                break;
        }
        return true;
    }

    private void backHome(){
        Intent intent=new Intent(this,DiaryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CHANGE_PICTURE&&
                resultCode==RESULT_OK&&
                null!=data){
            Uri selectedImage=data.getData();
            String str=selectedImage.toString();
            mDiary.setImageId(str);
            DiaryLab.get(this).updateDiary(mDiary);
            Glide.with(this).load(str).into(mDiaryImageView);
        }
    }
    public void writeSpeech(final Context context, final AnimatedRecordingView arv,int language){
        SpeechRecognizer mIat=SpeechRecognizer.createRecognizer(context,null);
        mIat.setParameter(SpeechConstant.DOMAIN,"iat");
        mIat.setParameter(SpeechConstant.LANGUAGE,"zh_cn");
        final String[] items={"mandarin","lmz","cantonese"};
        mIat.setParameter(SpeechConstant.ACCENT,items[language]);

        mIat.startListening(new RecognizerListener() {
            @Override
            public void onVolumeChanged(int i, byte[] bytes) {
                arv.setVolume(i*5);
            }

            @Override
            public void onBeginOfSpeech() {
                arv.start();

            }

            @Override
            public void onEndOfSpeech() {
                arv.stop();
            }

            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {
                if (!mEnd) {
                    Log.i(TAG, "result:" + recognizerResult.getResultString());
                    String result = Parse.parseVoice(recognizerResult.getResultString());
                    mDiaryContentText.setText(mDiaryContentText.getText()+result);
                }else{
                    Log.i(TAG, "result:" + recognizerResult.getResultString());
                    String result = Parse.parseVoice(recognizerResult.getResultString());
                    mDiaryContentText.setText(mDiaryContentText.getText()+result);
                    mEnd=false;
                }
                if (b){
                    mEnd=true;
                }
            }

            @Override
            public void onError(SpeechError speechError) {
                Log.e(TAG,"error:"+speechError.toString());
            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        });
    }
    private void speak(){
        if (mSynthesizer==null) {
            mSynthesizer = SpeechSynthesizer.createSynthesizer(DiaryContentActivity.this, null);
            mSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "vils");
            mSynthesizer.setParameter(SpeechConstant.SPEED, "50");
            mSynthesizer.setParameter(SpeechConstant.VOLUME, "100");
            mSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        }
        if (isPlay==false){
            mSynthesizer.startSpeaking(mDiary.getContent(), new SynthesizerListener() {
                @Override
                public void onSpeakBegin() {
                    isPlay=true;
                    mFabPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_stop));
                    mFabPlay.setLabelText("stop");
                }

                @Override
                public void onBufferProgress(int i, int i1, int i2, String s) {

                }

                @Override
                public void onSpeakPaused() {

                }

                @Override
                public void onSpeakResumed() {

                }

                @Override
                public void onSpeakProgress(int i, int i1, int i2) {

                }

                @Override
                public void onCompleted(SpeechError speechError) {
                    mFabPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play));
                    mFabPlay.setLabelText("play");
                }

                @Override
                public void onEvent(int i, int i1, int i2, Bundle bundle) {

                }
            });
        }else {
            mSynthesizer.stopSpeaking();
            isPlay=false;
            mFabPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play));
            mFabPlay.setLabelText("play");
        }
    }
    private void setGestureListener(){
        mTouchText.setOnTouchListener(new View.OnTouchListener() {
            boolean isaAction;
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mPosX = event.getX();
                        mPosY = event.getY();
                        Log.i(TAG, "ACTION_DOWN");
                        isaAction = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mCurPosX = event.getX();
                        mCurPosY = event.getY();
                        Log.i(TAG, "ACTION_MOVE");
                        if (isaAction == false){
                            if ((Math.abs(mCurPosX - mPosX) > 25) && mCurPosX - mPosX > 0) {
                                Log.i(TAG,"de="+Math.abs(mCurPosX - mPosX));
                                if (mSentencesTwo.size() != 0) {
                                    Log.i(TAG,"MStwo="+mSentencesTwo.size());
                                    Log.i(TAG, "右划成功1");
                                    Message msg=new Message();
                                    msg.what=2;
                                    msg.obj=mSentencesTwo.get(mSentencesTwo.size()-1);
                                    mHandler.sendMessage(msg);
                                    Log.i(TAG, "右划成功2");
                                    mSentences.add(mSentencesTwo.get(mSentencesTwo.size()-1));
                                    Log.i(TAG, "右划成功3");
                                    mSentencesTwo.remove(mSentencesTwo.size()-1);
                                    mDiary.setContent(mDiaryContentText.getText().toString());
                                    DiaryLab.get(DiaryContentActivity.this).updateDiary(mDiary);
                                    Log.i(TAG, "右划成功4");
                                    isaAction=true;
                                } else {
                                    Log.i(TAG, "右划");
                                }
                            } else if ((Math.abs(mCurPosX - mPosX) > 25) && mCurPosX - mPosX < 0) {
                                Log.i(TAG,"de="+Math.abs(mCurPosX - mPosX));
                                if (mSentences.size() != 0) {
                                    Log.i(TAG,"ms="+mSentences.size());
                                    Log.e(TAG, "左划成功6");
                                    String init=mDiaryContentText.getText().toString();
                                    Log.e(TAG, init);
                                    Log.e(TAG, mSentences.get(mSentences.size()-1));
                                    init=init.replaceAll(mSentences.get(mSentences.size()-1),"");
                                    Log.e(TAG, init);
                                    Message msg=new Message();
                                    msg.what=1;
                                    msg.obj=init;
                                   mHandler.sendMessage(msg);
                                   /* mDiaryContentText.getText()
                                            .delete(mDiaryContentText.getText().toString().length() - mSentences.get(mSentences.size()).length()
                                                    , mDiaryContentText.getText().toString().length());*/
                                    Log.e(TAG, "左划成功1");
                                    mSentencesTwo.add(mSentences.get(mSentences.size()-1));
                                    Log.e(TAG, "左划成功2");
                                    mSentences.remove(mSentences.size()-1);
                                    Log.e(TAG, "左划成功3");
                                    mDiary.setContent(mDiaryContentText.getText().toString());
                                    Log.e(TAG, "左划成功4");
                                    DiaryLab.get(DiaryContentActivity.this).updateDiary(mDiary);
                                    Log.e(TAG, "左划成功");
                                    isaAction=true;
                                } else {
                                    Log.e(TAG, "左划");
                                }
                            }
                }
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.i(TAG,"ACTION_UP");

                        break;
                }

                return true;
            }
        });
    }
}
