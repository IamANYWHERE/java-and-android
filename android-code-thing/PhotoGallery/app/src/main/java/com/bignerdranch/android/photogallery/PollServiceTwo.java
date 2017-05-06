package com.bignerdranch.android.photogallery;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.List;

/**
 * Created by 我 on 2017/4/8.
 */
public class PollServiceTwo extends JobService {

    public static String TAG="PollServiceTwo";
    private static final int JOB_ID=1;
    private PollTask mCurrentTask;

    @Override
    public boolean onStartJob(JobParameters params) {
        String query=QueryPreferences.getStoreQuery(this);
        mCurrentTask=new PollTask();
        mCurrentTask.execute(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (mCurrentTask!=null){
            mCurrentTask.cancel(true);
        }
        return true;
    }

    public static void setServiceJob(Context context,Boolean isOn){

        JobScheduler scheduler=(JobScheduler)
                context.getSystemService(context.JOB_SCHEDULER_SERVICE);

        if(isOn==true) {
            JobInfo jobInfo = new JobInfo.Builder(JOB_ID, new ComponentName(context,PollServiceTwo.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                    .setPeriodic(1000 * 60)
                    .build();
            scheduler.schedule(jobInfo);
        }else if (isOn==false){
            scheduler.cancel(JOB_ID);
        }
        QueryPreferences.setServiceOn(context,isOn);
    }
    public static boolean isServiceJobOn(Context context){
        boolean hasBeenScheduled=false;
        JobScheduler scheduler=(JobScheduler)
                context.getSystemService(context.JOB_SCHEDULER_SERVICE);

        for (JobInfo jobInfo:scheduler.getAllPendingJobs()){
            if (jobInfo.getId()==JOB_ID){
                hasBeenScheduled=true;
            }
        }
        return hasBeenScheduled;
    }
    private Context getContext(){
        return this;
    }
    private class PollTask extends AsyncTask<JobParameters,Void,Void>{

        @Override
        protected Void doInBackground(JobParameters... params) {
            JobParameters jobParams=params[0];
            String query=QueryPreferences.getStoreQuery(getContext());
            String lastResultId=QueryPreferences.getLastResultId(getContext());
            List<GalleryItem> items;
            if(query==null){
                items=new FlickrFetchr().fetchRecentPhotos("0");
            }else{
                items=new FlickrFetchr().searchPhotos(query);
            }

            if(items.size()!=0){

                String resultId=items.get(0).getId();
                if(resultId.equals(lastResultId)){
                    Log.i(TAG,"Got an old result: "+resultId);
                }else {
                    Log.i(TAG,"Got an new result: "+resultId);

                    Resources resources=getResources();
                    Intent intent=PhotoGalleryActivity.newIntent(getContext());
                    PendingIntent pi=PendingIntent.getActivity(getContext(),0,intent,0);

                    Notification notification=new NotificationCompat.Builder(getContext())
                            .setTicker(resources.getString(R.string.new_pictures_title))
                            .setContentTitle(resources.getString(R.string.new_pictures_title))
                            .setContentText(resources.getString(R.string.new_pictures_text))
                            .setSmallIcon(android.R.drawable.ic_menu_report_image)
                            .setContentIntent(pi)
                            .setAutoCancel(true)
                            .build();

                    showBackgroundNotification(0,notification);
                }
                QueryPreferences.setLastResultId(getContext(),resultId);
            }

            jobFinished(jobParams,false);
            return null;
        }

    }

    private void showBackgroundNotification(int requestCode,Notification notification){
        Intent intent=new Intent(PollService.ACTION_SHOW_NOTIFICATION);
        intent.putExtra(PollService.REQUEST_CODE,requestCode);
        intent.putExtra(PollService.NOTIFICATION,notification);
        sendOrderedBroadcast(intent,PollService.PERM_PRIVATE,null,null,
                Activity.RESULT_OK,null,null);
    }
}
