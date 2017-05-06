package com.bignerdranch.android.photogallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

/**
 * Created by 我 on 2017/4/9.
 */
public class StartupReceiver extends BroadcastReceiver {
    private static final String TAG="StartupReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"Received broadcast intent: "+intent.getAction());

        boolean isOn=QueryPreferences.isServiceOn(context);
        if (Build.VERSION.SDK_INT<21){
            PollService.setServiceAlarm(context,isOn);
        }else {
            PollServiceTwo.setServiceJob(context,isOn);
        }
    }
}
