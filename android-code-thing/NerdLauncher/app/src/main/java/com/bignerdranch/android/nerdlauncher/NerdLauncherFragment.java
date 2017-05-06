package com.bignerdranch.android.nerdlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by 我 on 2017/3/20.
 */
public class NerdLauncherFragment extends Fragment {

    private static  final String TAG="NerdLauncherFragment";

    RecyclerView mRecyclerView;


    public static Fragment newInstance(){
        return new NerdLauncherFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_nerd_launcher,container,false);

        mRecyclerView=(RecyclerView) view.findViewById(R.id.fragment_nerd_launcher_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setupAdapter();
        return view;
    }
    private void setupAdapter(){
        Intent startupIntent=new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm=getActivity().getPackageManager();
        List<ResolveInfo> activities=pm.queryIntentActivities(startupIntent,0);
        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo lhs, ResolveInfo rhs) {
                PackageManager pm=getActivity().getPackageManager();
                return String.CASE_INSENSITIVE_ORDER.compare(
                        lhs.loadLabel(pm).toString(),
                        rhs.loadLabel(pm).toString()
                );
            }
        });
        Log.i(TAG,"Found "+activities.size()+" activities.");
        mRecyclerView.setAdapter(new ActivityAdapter(activities));
    }
    private class ActivityHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener{
        private ResolveInfo mResolveInfo;
        private TextView mNameTextView;
        private ImageView mImageView;

        public ActivityHolder(View itemView){
            super(itemView);
            mNameTextView=(TextView)itemView.findViewById(R.id.activity_name);
            mNameTextView.setOnClickListener(this);
            mImageView=(ImageView)itemView.findViewById(R.id.activity_image);

        }

        public void bindActivity(ResolveInfo resolveInfo){
            mResolveInfo=resolveInfo;
            PackageManager pm=getActivity().getPackageManager();
            String appName=mResolveInfo.loadLabel(pm).toString();
            mNameTextView.setText(appName);
            Drawable drawable=mResolveInfo.loadIcon(pm);
            mImageView.setImageDrawable(drawable);
        }

        @Override
        public void onClick(View v) {
            ActivityInfo activityInfo=mResolveInfo.activityInfo;
            Intent i=new Intent(Intent.ACTION_MAIN)
                    .setClassName(activityInfo.applicationInfo.packageName
                            ,activityInfo.name)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }
    private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder>{
        private final List<ResolveInfo> mActivities;

        public ActivityAdapter(List<ResolveInfo> activities){
            mActivities=activities;
        }
        @Override
        public ActivityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater=LayoutInflater.from(getActivity());
            View view=layoutInflater.inflate(R.layout.activities_selected,parent,false);
            return new ActivityHolder(view);
        }

        @Override
        public void onBindViewHolder(ActivityHolder holder, int position) {
            ResolveInfo resolveInfo=mActivities.get(position);
            holder.bindActivity(resolveInfo);
        }

        @Override
        public int getItemCount() {
            return mActivities.size();
        }
    }
}
