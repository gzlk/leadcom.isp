package com.gzlk.android.isp.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseDownloadingUploadingSupportFragment;
import com.hlk.hlklib.lib.inject.ViewId;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * <b>功能描述：</b>带WebView的fragment基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/06 21:27 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/06 21:27 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class WebViewFragment extends BaseDownloadingUploadingSupportFragment {

    private static final String PARAM_LOADED = "wvf_loaded";

    public WebView webView;
    private boolean isLoaded = false;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        isLoaded = bundle.getBoolean(PARAM_LOADED, false);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putBoolean(PARAM_LOADED, isLoaded);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_web_view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void doingInResume() {
        if (null == webView) {
            if (!isLoaded) {
                isLoaded = true;
                webView = (WebView) mRootView;
                webView.getSettings().setUseWideViewPort(true);
                webView.getSettings().setLoadWithOverviewMode(true);
                webView.getSettings().setDomStorageEnabled(true);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setWebViewClient(new MyWebViewClient());
                webView.setWebChromeClient(new MyWebChromeClient());
                webView.loadUrl(loadingUrl());
            }
        }
    }

    /**
     * 需要加载的页面地址
     */
    protected abstract String loadingUrl();

    @Override
    protected void destroyView() {

    }

    /**
     * WebViewClient主要帮助WebView处理各种通知、请求事件的
     */
    private class MyWebViewClient extends WebViewClient {

        /**
         * 在点击请求的是链接时才会调用，重写此方法返回true表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        /**
         * 在页面加载开始时调用
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            showImageHandlingDialog(R.string.ui_base_text_loading_web_page);
        }

        /**
         * 在页面加载结束时调用
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            hideImageHandlingDialog();
            super.onPageFinished(view, url);
        }

//        @Override
//        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//            handler.proceed();// Ignore SSL certificate errors
//        }
    }

    /**
     * 辅助WebView处理Javascript的对话框，网站图标，网站title，加载进度等
     */
    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            setCustomTitle(title);
        }
    }
}
