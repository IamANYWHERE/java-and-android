package com.bignerdranch.android.photogallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by 我 on 2017/3/21.
 */
public class PhotoGalleryFragment extends VisibleFragment {

    private static final String DIALOG_LOAD="loading";
    private static final String TAG="PhotoGalleryFragment";
    private RecyclerView mPhotoRecyclerView;
    private Integer mPage;
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;
    private LruCache<String,Bitmap> mLruCache;
    private List<GalleryItem> mItems=new ArrayList<>();
    public static PhotoGalleryFragment newInstance(){
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        mPage=0;
        updateItems();

        int maxCache=(int)Runtime.getRuntime().maxMemory();
        int cacheSize=maxCache/8;
        mLruCache=new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                if(value!=null){
                    return value.getByteCount();
                }
                return 0;
            }
        };

        Handler responseHandler=new Handler();
        mThumbnailDownloader=new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
            @Override
            public void onThumbnailDownloaded(PhotoHolder target, Bitmap thumbnail) {
                    Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
                    target.bindDrawable(drawable);
                    mLruCache.put(target.getGalleryItem().getId()
                            ,thumbnail);
            }

            @Override
            public void onThumbnailPreload(String id, Bitmap bitmap) {;
                mLruCache.put(id,bitmap);
            }
        });
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG,"Background thread started");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_photo_gallery,container,false);


        mPhotoRecyclerView=(RecyclerView) view.findViewById(R.id.fragment_photo_gallery_recycler_view);
        final FragmentActivity activity=getActivity();
        setupAdapter();
        mPhotoRecyclerView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        final int column=(int)Math.floor(mPhotoRecyclerView.getWidth()/360.0);
                        setLayoutManager(activity,column);
                        mPhotoRecyclerView.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    }
                });
        mPhotoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState==recyclerView.SCROLL_STATE_IDLE){
                    RecyclerView.LayoutManager layoutManager=recyclerView.getLayoutManager();
                    int lastVisibleItem=((GridLayoutManager)layoutManager).findLastVisibleItemPosition();
                    GalleryItem galleryItem;
                    for (int i=1;i<=10;i++){
                        try{
                            galleryItem=mItems.get(lastVisibleItem+i);
                            if (galleryItem!=null&&mLruCache.get(galleryItem.getId())==null){
                                mThumbnailDownloader.queuePreLoad(galleryItem.getId(),galleryItem.getUrl());
                            }
                        }catch (IndexOutOfBoundsException iob){
                            return;
                        }
                    }
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);
                if (QueryPreferences.getStoreQuery(activity) == null) {
                    if (recyclerView.computeVerticalScrollOffset() + recyclerView.computeVerticalScrollExtent()
                            >= recyclerView.computeVerticalScrollRange()) {
                        Log.d(TAG, "onScrolled: set");
                        if (mPage <= 10) {
                            mPage++;
                            new FetchItemsTask(null).execute(mPage.toString());
                        }
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG,"Background thread destroyed");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery,menu);

        final FragmentActivity activity=getActivity();
        final MenuItem searchItem=menu.findItem(R.id.menu_item_search);
        final SearchView searchView=(SearchView)searchItem.getActionView();

        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG,"QueryTextSubmit: "+query);
                QueryPreferences.setStoreQuery(getActivity(),query);
                mItems.clear();
                setupAdapter();
                updateItems();
                searchView.clearFocus();
                searchView.onActionViewCollapsed();

                LoadingDialog();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG,"QueryTextChange: "+newText);
                return false;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query=QueryPreferences.getStoreQuery(activity);
                searchView.setQuery(query,false);
                Log.d(TAG,"click ");
            }
        });

        MenuItem toggleItem=menu.findItem(R.id.menu_item_toggle_polling);
        if (Build.VERSION.SDK_INT<21) {
            if (PollService.isServiceAlarmOn(getActivity())) {
                toggleItem.setTitle(R.string.stop_polling);
            } else {
                toggleItem.setTitle(R.string.start_polling);
            }
        }else{
            if (PollServiceTwo.isServiceJobOn(getActivity())){
                toggleItem.setTitle(R.string.stop_polling);
            }else{
                toggleItem.setTitle(R.string.start_polling);
            }
        }
    }
    private void LoadingDialog(){
        FragmentManager fragmentManager=getFragmentManager();
        LoadingFragment dialog=new LoadingFragment();
        dialog.show(fragmentManager,DIALOG_LOAD);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_clear:
                QueryPreferences.setStoreQuery(getActivity(),null);
                mItems.clear();
                setupAdapter();
                updateItems();
                LoadingDialog();
                return true;
            case R.id.menu_item_toggle_polling:
                if (Build.VERSION.SDK_INT<21) {
                    boolean shouldStartAlarm = !PollService.isServiceAlarmOn(getActivity());
                    PollService.setServiceAlarm(getActivity(), shouldStartAlarm);
                }else {
                    boolean shouldStartJob=!PollServiceTwo.isServiceJobOn(getActivity());
                    PollServiceTwo.setServiceJob(getActivity(),shouldStartJob);
                }
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateItems(){
        String query=QueryPreferences.getStoreQuery(getActivity());
        new FetchItemsTask(query).execute(mPage.toString());
    }

    private void setLayoutManager(FragmentActivity activity, int column){
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(activity,column));
    }
    private void setupAdapter(){
        if(isAdded()){
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }
    private void updateAdapter(List<GalleryItem> Items){
        PhotoAdapter adapter=(PhotoAdapter)mPhotoRecyclerView.getAdapter();
        adapter.updateItems(Items);
        adapter.notifyDataSetChanged();
    }
    private class PhotoHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        private ImageView mItemImageView;
        private GalleryItem mGalleryItem;

        public PhotoHolder(View itemView){
            super(itemView);
            mItemImageView=(ImageView)itemView
                    .findViewById(R.id.fragment_photo_gallery_image_view);
            itemView.setOnClickListener(this);
        }
        public GalleryItem getGalleryItem(){
            return mGalleryItem;
        }
        public void bindDrawable(Drawable drawable){
            mItemImageView.setImageDrawable(drawable);
        }
        public void bindGalleryItem(Drawable drawable,GalleryItem galleryItem){
            bindDrawable(drawable);
            mGalleryItem=galleryItem;
        }

        @Override
        public void onClick(View v) {
            Intent intent=PhotoPageActivity.newIntent(getActivity(),mGalleryItem.getPhotoPageUri());
            startActivity(intent);
        }
    }
    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>{
        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems){
            mGalleryItems=galleryItems;
        }

        public void updateItems(List<GalleryItem> galleryItems){
            if(galleryItems!=null)
                mGalleryItems.addAll(galleryItems);
        }
        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater=LayoutInflater.from(getActivity());
            View view=inflater.inflate(R.layout.gallery_item,parent,false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem galleryItem=mGalleryItems.get(position);
            Drawable placeholder=getResources().getDrawable(R.drawable.timg_new);
            holder.bindGalleryItem(placeholder,galleryItem);
            Bitmap bitmap=mLruCache.get(galleryItem.getId());
            if(bitmap==null) {
                mThumbnailDownloader.queueThumbnail(holder, galleryItem.getUrl());
            }else{
                Drawable drawable=new BitmapDrawable(getResources(),bitmap);
                holder.bindDrawable(drawable);
            }
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }
    private class FetchItemsTask extends AsyncTask<String,Void,List<GalleryItem>>{

        private String mQuery;
        public FetchItemsTask(String query){
            mQuery=query;
        }
        @Override
        protected List<GalleryItem> doInBackground(String... params) {
            if(mQuery==null){
                if (params[0]!=null)
                    return new FlickrFetchr().fetchRecentPhotos(params[0]);
                return null;
            }else{
                return new FlickrFetchr().searchPhotos(mQuery);
            }
        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            updateAdapter(galleryItems);
            try {
                DialogFragment dialog = (DialogFragment) getFragmentManager().findFragmentByTag(DIALOG_LOAD);
                dialog.dismiss();
            }catch (NullPointerException npe){
            }

        }
    }
}
