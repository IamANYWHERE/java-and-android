package com.bignerdranch.android.voice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.haozhang.lib.AnimatedRecordingView;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by 我 on 2017/5/31.
 */
public class DiaryActivity extends AppCompatActivity {

    private static final String TAG="diary";

    private static final int REQUEST_DIARY=1;

    private DrawerLayout mDrawerLayout;
    private RecyclerView mRecyclerView;
    private List<Diary> mDiaryList=new ArrayList<>();
    private DiaryAdapter mAdapter;
    private boolean mEnd;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView navView=(NavigationView)findViewById(R.id.nav_view);
        mEnd=false;
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_question:
                        Intent i= QuestionActivity.newIntent(DiaryActivity.this);
                        startActivity(i);
                        break;
                    default:
                }
                return true;
            }
        });

        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v,"new diary",Snackbar.LENGTH_SHORT)
                        .setAction("Do", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Diary diary=new Diary();
                                Random random=new Random();
                                int index=random.nextInt(9)+1;
                                diary.setImageId("q"+index);
                                DiaryLab.get(DiaryActivity.this).addDiary(diary);
                                updateUI();
                                Intent intent=DiaryContentActivity.newIntent(DiaryActivity.this,diary.getUUID());
                                startActivityForResult(intent,REQUEST_DIARY);
                                Toast.makeText(DiaryActivity.this,"new diary",Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }).show();
            }
        });

        initDiaries();
        mRecyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager=new GridLayoutManager(this,1);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter=new DiaryAdapter(mDiaryList);
        mRecyclerView.setAdapter(mAdapter);
        Log.i(TAG,"create");
        final AnimatedRecordingView arv=(AnimatedRecordingView)findViewById(R.id.arv_view);
        arv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Parse.getPermission(DiaryActivity.this);
                /*rEnd=!rEnd;
                if(!rEnd) {
                    writeSpeech(DiaryActivity.this, arv);

                }*/
                writeSpeech(DiaryActivity.this, arv);
            }
        });
    }

    private void initDiaries(){
        mDiaryList=DiaryLab.get(this).getDiaries();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }
    private void updateUI(){
        mDiaryList=DiaryLab.get(this).getDiaries();
        if (mAdapter==null){
            mAdapter=new DiaryAdapter(mDiaryList);
            mRecyclerView.setAdapter(mAdapter);
        }else {
            mAdapter.setDiaries(mDiaryList);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
    public void writeSpeech(final Context context, final AnimatedRecordingView arv){
        final SpeechRecognizer mIat=SpeechRecognizer.createRecognizer(context,null);
        mIat.setParameter(SpeechConstant.DOMAIN,"iat");
        mIat.setParameter(SpeechConstant.LANGUAGE,"zh_cn");
        final String[] items={"mandarin","lmz","cantonese"};
        mIat.setParameter(SpeechConstant.ACCENT,items[0]);

        RecognizerListener temp= new RecognizerListener() {
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
              /*  if(!rEnd)
                    mIat.startListening(this);*/
                //  arv.stop();

            }

            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {
                if (!mEnd) {
                    Log.i(TAG+1, "result:" + recognizerResult.getResultString());
                    String result = Parse.parseVoice(recognizerResult.getResultString());
                    Log.e("asdf","result="+result);
                    if (result.equals("新建"))
                    {
                        Diary diary=new Diary();
                        Random random=new Random();
                        int index=random.nextInt(9)+1;
                        diary.setImageId("q"+index);
                        DiaryLab.get(DiaryActivity.this).addDiary(diary);
                        updateUI();
                        Intent intent=DiaryContentActivity.newIntent(DiaryActivity.this,diary.getUUID());
                        startActivityForResult(intent,REQUEST_DIARY);
                        Toast.makeText(DiaryActivity.this,"new diary",Toast.LENGTH_SHORT)
                                .show();
                        Toast.makeText(DiaryActivity.this,"Data restored",
                                Toast.LENGTH_SHORT).show();
                    }
                    else if(result.equals("机器人"))
                    {
                        Intent i= QuestionActivity.newIntent(DiaryActivity.this);
                        startActivity(i);
                    }
                    else
                    {
                        for(int i=0;i<mDiaryList.size();i++)
                        {
                            if(result.equals(mDiaryList.get(i).getTitle()))
                            {
                                Intent intent=DiaryContentActivity.newIntent(DiaryActivity.this,mDiaryList.get(i).getUUID());
                                startActivity(intent);
                            }
                        }
                    }

                }
                else
                {
                    Log.e("asdf", "result:" + recognizerResult.getResultString());
                    //String result = Parse.parseVoice(recognizerResult.getResultString());
                    mEnd=false;
                }
                if (b){
                    mEnd=false
                    ;
                }
            }

            @Override
            public void onError(SpeechError speechError) {
                Log.e(TAG,"error:"+speechError.toString());
            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        };
        mIat.startListening(temp);
    }

}
