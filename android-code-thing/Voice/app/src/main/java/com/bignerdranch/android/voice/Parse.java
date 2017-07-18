package com.bignerdranch.android.voice;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import java.util.ArrayList;

/**
 * Created by 我 on 2017/5/10.
 */
public class Parse {

    public static final String TAG="Parse";
    public static String getService(String resultString){
        Gson gson=new Gson();
        Service service=gson.fromJson(resultString,Service.class);
        return service.service;
    }

    public static String parseVoice(String resultString){
        Gson gson=new Gson();
        Voice voiceBean=gson.fromJson(resultString,Voice.class);

        StringBuffer sb=new StringBuffer();
        ArrayList<Voice.WSBean> ws=voiceBean.ws;
        for (Voice.WSBean wsBean:ws){
            String word=wsBean.cw.get(0).w;
            sb.append(word);
        }

        return  sb.toString();
    }
    public static String getWeather(String resultString){
        Gson gson=new Gson();
        Weather weatherBean=gson.fromJson(resultString,Weather.class);

        StringBuffer sb=new StringBuffer();
        Weather.DATABean data=weatherBean.data;
        Weather.Semantic semantic=weatherBean.semantic;
        String dateOrig=semantic.slots.datetime.dateOrig;
        ArrayList<Weather.Bean> result=data.result;
        if (dateOrig!=null) {
            sb.append(dateOrig);
        }else {
            sb.append("今天");
        }
        int i=0;
        if (dateOrig.equals("今天")){
            i=0;
        }else if (dateOrig.equals("明天")){
            i=1;
        }else if (dateOrig.equals("后天")){
            i=2;
        }else if (dateOrig.equals("外天")){
            i=3;
        }

        int j=-1;
        for (Weather.Bean bean:result){

            j++;
            if (i==j) {
                sb.append("的");
                sb.append(bean.city);
                sb.append("\n");
                sb.append(bean.weather);

                sb.append("\n");
                sb.append(bean.tempRange);
                sb.append("\n");
                sb.append(bean.wind);
            }
        }
        return sb.toString();
    }
    public static String getAnswer(String resultString){
        Gson gson=new Gson();
        Answer answer=gson.fromJson(resultString,Answer.class);
        Answer.Bean bean=answer.answer;
        String result=bean.text;

        return result;

    }
    public static String getMySpeech(String resultString){
        Gson gson=new Gson();
        MySpeech mySpeech=gson.fromJson(resultString,MySpeech.class);
        String text=mySpeech.text;
        return text;
    }
    public static String parseInstructor(String speech){
        if (speech.contains("电话")){
            return "tel";
        }else if (speech.contains("百度")){
            return "http";
        }else if (speech.contains("地图")){
            return "geo";
        }else if (speech.contains("微博")){
            return "weibo";
        }else if (speech.contains("短信")){
            return "message";
        }else if (speech.contains("视频")){
            return "video";
        }
        return null;
    }

    public static void getPermission(Activity context){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)!=
                PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (context,Manifest.permission.RECORD_AUDIO)){
                Toast.makeText(context,"需要权限才能使用",Toast.LENGTH_SHORT);
            }else{
                ActivityCompat.requestPermissions(context,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        1);
            }
        }
    }
    public class Voice {


        public ArrayList<WSBean> ws;

        public class WSBean{
            public ArrayList<CWBean> cw;
        }

        public class CWBean{
            public String w;
        }
    }
    public class Service {
        public String service;
    }
    public class Weather {

        public String text;
        public DATABean data;
        public Semantic semantic;


        public class Semantic{
            public Slots slots;
        }
        public class Slots{
            public DateTime datetime;
        }
        public class DateTime{
            public String dateOrig;
        }

        public class DATABean{
            public ArrayList<Bean> result;
        }

        public class Bean{
            public String city;
            public String weather;
            public String tempRange;
            public String wind;
        }

    }
    public class Answer {

        public Bean answer;
        public String text;

        public class Bean{
            public String text;
        }

    }
    public class MySpeech{
        public String text;
    }
    public static void speakWords(Context context, String sentence){
        SpeechSynthesizer mTts=SpeechSynthesizer.createSynthesizer(context,null);
        mTts.setParameter(SpeechConstant.VOICE_NAME,"vils");
        mTts.setParameter(SpeechConstant.SPEED,"50");
        mTts.setParameter(SpeechConstant.VOLUME,"100");
        mTts.setParameter(SpeechConstant.ENGINE_TYPE,SpeechConstant.TYPE_CLOUD);

        mTts.startSpeaking(sentence, new SynthesizerListener() {
            @Override
            public void onSpeakBegin() {

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

            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        });
    }
}
