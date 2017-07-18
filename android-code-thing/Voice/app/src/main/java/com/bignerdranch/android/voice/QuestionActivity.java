package com.bignerdranch.android.voice;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.icu.util.Calendar;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.EventLog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.haozhang.lib.AnimatedRecordingView;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.UnderstanderResult;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QuestionActivity extends AppCompatActivity {

    public static final String TAG = "Main";
    private static final int RECODE_REQUEST = 1;

    private List<Msg> mMsgList;

    private RecyclerView mRecyclerView;
    private AnimatedRecordingView mRecordingView;
    private MsgAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_chat);

        initMsgs();
        mRecyclerView = (RecyclerView) findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MsgAdapter(mMsgList);
        mRecyclerView.setAdapter(mAdapter);

        mRecordingView = (AnimatedRecordingView) findViewById(R.id.recording);
        ;
        mRecordingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Parse.getPermission(QuestionActivity.this);
                writeSpeech(QuestionActivity.this);
            }
        });


    }

    private void initMsgs() {

        mMsgList = new ArrayList<>();
        Msg msg = new Msg("你好啊，有什么需要帮助的吗？", Msg.TYPE_RECEIVED);
        mMsgList.add(msg);
    }

    private void updateMsg(String content, int type) {
        Msg msg = new Msg(content, type);
        mMsgList.add(msg);
        int size = mMsgList.size();
        mAdapter.notifyItemChanged(size - 1);
        mRecyclerView.scrollToPosition(size - 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RECODE_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "权限已获取", Toast.LENGTH_SHORT);

            } else {
                Toast.makeText(this, "权限获取失败", Toast.LENGTH_SHORT);
            }
            return;
        } else if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "权限已获取", Toast.LENGTH_SHORT);
            } else {
                Toast.makeText(this, "权限获取失败", Toast.LENGTH_SHORT);
            }
        }
    }


    public void writeSpeech(final Context context) {
        SpeechUnderstander understander = SpeechUnderstander.createUnderstander(context, null);
        understander.setParameter(SpeechConstant.DOMAIN, "iat");
        understander.setParameter(SpeechConstant.RESULT_TYPE, "json");
        understander.setParameter(SpeechConstant.NLP_VERSION, "2.0");

        understander.startUnderstanding(new SpeechUnderstanderListener() {
            @Override
            public void onVolumeChanged(int i, byte[] bytes) {
                mRecordingView.setVolume(i * 5);
            }

            @Override
            public void onBeginOfSpeech() {
                mRecordingView.start();
            }

            @Override
            public void onEndOfSpeech() {
                mRecordingView.stop();
            }

            @Override
            public void onResult(UnderstanderResult understanderResult) {
                String result = understanderResult.getResultString();
                Log.i(TAG, "question=" + result);
                Log.i(TAG, "ssss");
                String mySpeech = Parse.getMySpeech(result);
                updateMsg(mySpeech, Msg.TYPE_SENT);
                String service = Parse.getService(result);
                Log.i(TAG,"service="+service);
                if (service != null) {
                    if (service.equals("weather")) {
                        Log.i(TAG, "question=" + result);
                        String weather = Parse.getWeather(result);
                        Parse.speakWords(QuestionActivity.this,weather);
                        updateMsg(weather, Msg.TYPE_RECEIVED);
                    } else if (service.equals("openQA") || service.equals("datetime") || service.equals("calc") ||
                            service.equals("baike") || service.equals("faq") || service.equals("chat")) {
                        Log.i(TAG, "question=" + result);
                        String answer = Parse.getAnswer(result);
                        Parse.speakWords(QuestionActivity.this,answer);
                        updateMsg(answer, Msg.TYPE_RECEIVED);
                    } else if (service.equals("telephone")||service.equals("message")) {
                        Intent intent = newIntent(service);
                        startActivity(intent);
                    }
                } else {
                    String instructor = Parse.parseInstructor(mySpeech);
                    if (instructor != null) {
                        Intent i = newIntent(instructor);
                        startActivity(i);
                    } else {
                        updateMsg("你还是人吗？人家要哭了~~>_<~~", Msg.TYPE_RECEIVED);
                        Parse.speakWords(QuestionActivity.this,"你还是人吗？人家要哭了");
                    }
                }
            }

            @Override
            public void onError(SpeechError speechError) {
                Log.e(TAG, "error:" + speechError.toString());
            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        });

    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, QuestionActivity.class);
        return intent;
    }

    private Intent newIntent(String instructor) {
        Intent intent;
        switch (instructor) {
            case "telephone":
                Uri number = Uri.parse("tel:10086");
                intent = new Intent(Intent.ACTION_DIAL);
                break;
            case "geo":
                Location loc=getLocation();
                if (loc!=null) {
                    Log.i(TAG, "loc=" + loc.toString());
                    Uri location = Uri.parse("geo:" + loc.getLatitude() + "," + loc.getLongitude() + "?z=14");
                    intent = new Intent(Intent.ACTION_VIEW, location);
                }else {
                    intent=null;
                }
                break;
            case "http":
                Uri webPage = Uri.parse("http://www.baidu.com");
                intent = new Intent(Intent.ACTION_VIEW, webPage);
                break;
            case "weibo":
                intent=new Intent();
                intent.setClassName("com.sina.weibo", "com.sina.weibo.SplashActivity");
                break;
            case "message":
                intent=new Intent(Intent.ACTION_VIEW);
                intent.putExtra("sms_body","TheSMS text");
                intent.setType("vnd.android-dir/mms-sms");
                break;
            case "video":
                intent=new Intent(Intent.ACTION_VIEW);
                intent.setType("video/*");
                break;
            default:
                intent = null;
        }
        if (intent!=null&&queryEnableActivities(intent)) {
            return intent;
        }
        return null;
    }

    private Location getLocation() {
        getLocationPermission();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        LocationManager locationManager;
        String serviceName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) QuestionActivity.this.getSystemService(serviceName);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);
        Location location= locationManager.getLastKnownLocation(provider);

        return location;
    }
    private void getLocationPermission(){
        if (ActivityCompat.checkSelfPermission(QuestionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(QuestionActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(QuestionActivity.this,"没有权限",Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(QuestionActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},2);

        }

    }
    private boolean queryEnableActivities(Intent i){
        PackageManager packageManager=getPackageManager();
        List<ResolveInfo> activities=packageManager.queryIntentActivities(i,0);
        boolean isIntentSafe=activities.size()>0;
        return isIntentSafe;
    }

}
