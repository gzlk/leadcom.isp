package com.leadcom.android.isp.chorme;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.helper.LogHelper;
import com.leadcom.android.isp.helper.StringHelper;

import java.util.ArrayList;

public class ChromeWebViewClient extends WebViewClient {

    private static final String TAG = "ChromeClient";
    private long pageLoadingStart;

    private void log(String string) {
        LogHelper.log(TAG, string);
    }

    private String format(String fmt, Object... args) {
        return StringHelper.format(fmt, args);
    }

    private ArrayList<String> segments = new ArrayList<String>() {{
        add("bootstrap.min.css");
        add("bootstrap.min.js");
        add("jquery.cookie.js");
        add("jquery.min.js");
        add("template-web.js");
    }};

    private WebResourceResponse checkRequest(Uri uri) {
        String fileName = uri.getLastPathSegment();
        if (segments.contains(fileName)) {
            WebResourceResponse response;
            try {
                assert fileName != null;
                if (fileName.endsWith(".js")) {
                    response = new WebResourceResponse("application/javascript", "UTF-8", App.app().getAssets().open("js/" + fileName));
                } else {
                    response = new WebResourceResponse("text/css", "UTF-8", App.app().getAssets().open("css/" + fileName));
                }
                log(format("create local resource: %s", fileName));
            } catch (Exception e) {
                response = null;
            }
            return response;
        } else {
            return null;
        }
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if (Build.VERSION.SDK_INT < 21) {
            log(format("request url: %s", url));
            WebResourceResponse response = checkRequest(Uri.parse(url));
            return null != response ? response : super.shouldInterceptRequest(view, url);
        } else {
            return super.shouldInterceptRequest(view, url);
        }
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        if (Build.VERSION.SDK_INT >= 21) {
            log(format("need request: %s", request.getUrl().toString()));
            WebResourceResponse response = checkRequest(request.getUrl());
            return null != response ? response : super.shouldInterceptRequest(view, request);
        } else {
            return super.shouldInterceptRequest(view, request);
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (null != listener) {
            return listener.onOverrideUrlLoading(view, url);
        }
        return true;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        if (Build.VERSION.SDK_INT >= 21) {
            if (null != listener) {
                return listener.onOverrideUrlLoading(view, request.getUrl().toString());
            }
        }
        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        pageLoadingStart = System.currentTimeMillis();
        if (null != listener) {
            listener.onPageStarted();
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        log(format("loading archive content used: %dms", System.currentTimeMillis() - pageLoadingStart));
        setJsEvents(view);
        if (null != listener) {
            listener.onPageFinished();
        }
    }

    private void setJsEvents(WebView view) {
        String jsCode = "javascript:(function() {" +
                "   $(\".cont img[src^='http']\").on(\"click\", function() {" +
                "       alert($(this).attr(\"src\"));" +
                "   });" +
                "   $(\".cont1 img[src^='http']\").on(\"click\", function() {" +
                "       alert($(this).attr(\"src\"));" +
                "   });" +
                "   $(\".fjlist a\").on(\"click\", function(evt) {" +
                "       var href = $(this).attr(\"href\").toLowerCase();" +
                "       if((href.indexOf(\".gif\") >=0) || (href.indexOf(\".jpg\") >=0) || " +
                "           (href.indexOf(\".jpeg\") >=0) || (href.indexOf(\".png\") >= 0) || " +
                "           (href.indexOf(\".txt\") >= 0) || (href.indexOf(\".mp4\") >= 0) || " +
                "           (href.indexOf(\".mp3\") >= 0)) {" +
                "           evt.preventDefault();" +
                "           alert(\"href=\" + $(this).attr(\"href\"));" +
                "       }" +
                "   }).each(function(){" +
                "       $(this).removeAttr(\"download\");" +
                "   });" +
                "   $(\"video\").each(function() {" +
                "       $(this).css(\"width\", \"100%\");" +
                "   });" +
                "})()";
        view.loadUrl(jsCode);
    }

    private WebEventListener listener;

    public ChromeWebViewClient setWebEventListener(WebEventListener l) {
        listener = l;
        return this;
    }

    public interface WebEventListener {

        void onPageStarted();

        void onPageFinished();

        boolean onOverrideUrlLoading(WebView view, String url);
    }
}
