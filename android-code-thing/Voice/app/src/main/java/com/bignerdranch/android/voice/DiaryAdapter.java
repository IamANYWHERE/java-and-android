package com.bignerdranch.android.voice;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DecorContentParent;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by 我 on 2017/5/31.
 */
public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {

    private static final String TAG="adapter";
    private Context mContext;

    private List<Diary> mDiaryList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView mCardView;
        ImageView mDiaryImage;
        TextView mDiaryTitle;
        TextView mDiaryTime;

        public ViewHolder(View itemView) {
            super(itemView);
            mCardView=(CardView)itemView;
            mDiaryImage=(ImageView)itemView.findViewById(R.id.diary_image);
            mDiaryTitle=(TextView)itemView.findViewById(R.id.diary_title);
            mDiaryTime=(TextView)itemView.findViewById(R.id.diary_time);
        }

    }

    public DiaryAdapter(List<Diary> diaryList){
        mDiaryList=diaryList;
    }

    public void setDiaries(List<Diary> diaries){}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext==null){
            mContext=parent.getContext();
        }
        View view= LayoutInflater.from(mContext).inflate(R.layout.diary_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                Log.i(TAG,"POSITION="+position);
                Diary diary=mDiaryList.get(position);
                Intent intent=DiaryContentActivity.newIntent(mContext,diary.getUUID());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Diary diary=mDiaryList.get(position);
        holder.mDiaryTitle.setText(diary.getTitle());
        String dateFormat="yyyy年MM月dd日 HH：mm";
        String dateString=(String) DateFormat.format(dateFormat,diary.getDate());
        holder.mDiaryTime.setText(dateString);
        String imageId=diary.getImageId();
        if (imageId.length()<=3){
            int id=mContext.getResources().getIdentifier(imageId,"drawable",mContext.getPackageName());
            Glide.with(mContext)
                    .load(id)
                    .into(holder.mDiaryImage);
        }else {
            Glide.with(mContext).load(imageId).into(holder.mDiaryImage);
        }
    }

    @Override
    public int getItemCount() {
        return mDiaryList.size();
    }

}
