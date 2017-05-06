package com.bignerdranch.android.photogallery;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.net.URL;
import java.net.URLDecoder;

/**
 * Created by 我 on 2017/4/10.
 */
public class PhotoPageFragment extends VisibleFragment {

    private static final String TAG="PhotoPageFragment";
    private static final String ARG_URI="photo_page_url";
    private static final String ARG_ACTIVITY="activity";
    private Uri mUri;
    private WebView mWebView;
    private ProgressBar mProgressBar;

    public static PhotoPageFragment newInstance(Uri uri){
        Bundle args=new Bundle();
        args.putParcelable(ARG_URI,uri);

        PhotoPageFragment fragment=new PhotoPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUri=getArguments().getParcelable(ARG_URI);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_photo_page,container,false);

        mProgressBar=(ProgressBar)view.findViewById(R.id.fragment_photo_page_progress_bar);
        mProgressBar.setMax(100);
        mWebView=(WebView) view.findViewById(R.id.fragment_photo_page_web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress==100){
                    mProgressBar.setVisibility(View.GONE);
                }else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                AppCompatActivity activity=(AppCompatActivity)getActivity();
                activity.getSupportActionBar().setSubtitle(title);
            }
        });
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri=Uri.parse(url);
                if(uri.getScheme().equals("https")||uri.getScheme().equals("http")){
                    Log.i(TAG,"scheme is https or http");
                    return false;
                }else {
                    Log.i(TAG,"scheme is "+uri.getScheme());
                    try {

                        view.stopLoading();
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }catch (Exception e){
                        e.printStackTrace();
                        if (uri.getScheme().equals("market")){
                            Toast.makeText(view.getContext()
                                    , "make sure you have intall appstore"
                                    , Toast.LENGTH_SHORT)
                                    .show();
                        }else {
                            Toast.makeText(view.getContext()
                                    ,uri.getScheme()+"is not stall"
                                    ,Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }finally {
                        return true;
                    }
                }
            }

        });
        mWebView.loadUrl(mUri.toString());
        ((PhotoPageActivity)getActivity()).getWebViewFromFragment(mWebView);
        return view;
    }


}
