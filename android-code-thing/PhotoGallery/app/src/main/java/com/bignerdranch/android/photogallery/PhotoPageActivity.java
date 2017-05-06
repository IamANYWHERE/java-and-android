package com.bignerdranch.android.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.webkit.WebView;

import java.net.URL;

/**
 * Created by 我 on 2017/4/10.
 */
public class PhotoPageActivity extends SingleFragmentActivity {

    private WebView mWebView;
    @Override
    protected Fragment createFragment() {
        return PhotoPageFragment.newInstance(getIntent().getData());
    }
    public static Intent newIntent(Context context, Uri photoPageUri){
        Intent intent=new Intent(context,PhotoPageActivity.class);
        intent.setData(photoPageUri);
        return intent;
    }

    public void getWebViewFromFragment(WebView webView){
        mWebView=webView;
    }

    @Override
    public void onBackPressed() {
        if (mWebView!=null&&mWebView.canGoBack()){
            mWebView.goBack();
        }else {
            super.onBackPressed();
        }
    }
}
