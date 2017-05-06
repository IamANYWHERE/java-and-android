package com.bignerdranch.android.beatbox;

import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

/**
 * Created by 我 on 2017/3/13.
 */
public class BeatBoxFragment extends Fragment {

    public static final String TAG="fragment";
    private BeatBox mBeatBox;
    private LinearLayout mReveal;
    public static BeatBoxFragment newInstance(){
        return new BeatBoxFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mBeatBox=new BeatBox(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_beat_box_1,container,false);

        mReveal=(LinearLayout) view.findViewById(R.id.reveal);

        RecyclerView mRecyclerView=(RecyclerView)view
                .findViewById(R.id.fragment_beat_box_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        mRecyclerView.setAdapter(new SoundAdapter(mBeatBox.getSounds()));

        return view;
    }

    private class SoundHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener{
        private Button mButton;
        private Sound mSound;
        public SoundHolder(LayoutInflater inflater,ViewGroup container){
            super(inflater.inflate(R.layout.list_item_sound,container,false));
            mButton=(Button)itemView.findViewById(R.id.list_item_sound_button);
            mButton.setOnClickListener(this);
        }
        public void bindSound(Sound sound){
            mSound=sound;
            mButton.setText(mSound.getName());
        }

        @Override
        public void onClick(View v) {
            int[] clickCoords=new int[2];
            v.getLocationOnScreen(clickCoords);
            clickCoords[0]+=v.getWidth()/2;
            clickCoords[1]+=v.getHeight()/2;
            performRevealAnimation(mReveal,clickCoords[0],clickCoords[1]);
            mBeatBox.play(mSound);
        }
    }
    private void performRevealAnimation(View view,int screenCenterX,int screenCenterY){
        Point size=new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        int maxRadius=size.y;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            view.setBackgroundColor(getResources().getColor(R.color.dark_blue));
            ViewAnimationUtils.createCircularReveal(view,screenCenterX,screenCenterY,0,maxRadius)
                    .start();
        }
    }

    private class SoundAdapter extends RecyclerView.Adapter<SoundHolder>{

        private List<Sound> mSounds;
        public SoundAdapter(List<Sound> sounds){
            mSounds=sounds;
        }
        @Override
        public SoundHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater=LayoutInflater.from(getActivity());
            return new SoundHolder(inflater,parent);
        }

        @Override
        public void onBindViewHolder(SoundHolder holder, int position) {
            Sound sound=mSounds.get(position);
            holder.bindSound(sound);
        }

        @Override
        public int getItemCount() {
            return mSounds.size();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBeatBox.release();
    }
}
