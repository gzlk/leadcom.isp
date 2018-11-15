package com.leadcom.android.isp.fragment.archive;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredEditText;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.archive.ArchiveRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.chorme.ChromeWebViewClient;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseTransparentSupportFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.common.SimpleClickableViewHolder;
import com.leadcom.android.isp.holder.common.SimpleInputableViewHolder;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.model.archive.Archive;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>回复流转档案<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/07/23 10:40 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/07/23 10:40 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ArchiveReplyFragment extends BaseTransparentSupportFragment {

    public static ArchiveReplyFragment newInstance(Bundle bundle) {
        ArchiveReplyFragment arf = new ArchiveReplyFragment();
        arf.setArguments(bundle);
        return arf;
    }

    public static void open(BaseFragment fragment, Archive archive) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAM_JSON, archive);
        fragment.openActivity(ArchiveReplyFragment.class.getName(), bundle, true, true);
    }

    @ViewId(R.id.ui_archive_reply_subject)
    private View titleView;
    @ViewId(R.id.ui_archive_reply_recipient)
    private View recipientView;
    @ViewId(R.id.ui_archive_reply_content)
    private CorneredEditText contentView;
    @ViewId(R.id.ui_archive_reply_details)
    private WebView webView;
    private SimpleInputableViewHolder titleHolder;
    private SimpleClickableViewHolder recipientHolder;
    private String[] items;
    private Archive mArchive;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                result.confirm();
                return true;
            }
        });
        webView.setWebViewClient(new ChromeWebViewClient().setWebEventListener(eventListener));
        setCustomTitle(R.string.ui_base_text_reply);
        setRightText(R.string.ui_base_text_finish);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                // 提交档案的回复
                tryReplyArchive();
            }
        });
    }

    private ChromeWebViewClient.WebEventListener eventListener = new ChromeWebViewClient.WebEventListener() {
        @Override
        public void onPageStarted() {
            displayLoading(true);
        }

        @Override
        public void onPageFinished() {
            displayLoading(false);
        }

        @Override
        public boolean onOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("leadcom://")) {
                return true;
            }
            view.loadUrl(url);
            return true;
        }
    };

    @Override
    public int getLayout() {
        return R.layout.fragment_archive_reply;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mArchive = (Archive) bundle.getSerializable(PARAM_JSON);
    }

    @Override
    public void doingInResume() {
        //if (isEmpty(mArchive.getFromGroupName())) {
        //    loadingArchiveDetails();
        //} else {
        initializeHolders();
        //}
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putSerializable(PARAM_JSON, mArchive);
    }

    @Override
    protected void destroyView() {

    }

    @Override
    protected boolean checkStillEditing() {
        return super.checkStillEditing();
    }

    private void loadingArchiveDetails() {
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    mArchive = archive;
                    initializeHolders();
                }
            }
        }).find(mArchive.getDocType(), mArchive.getId());
    }

    private void initializeHolders() {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_text_archive_reply_items);
        }
        if (null == recipientHolder) {
            recipientHolder = new SimpleClickableViewHolder(recipientView, this);
            recipientHolder.showContent(format(items[0], mArchive.getFromGroupName()));
        }
        if (null == titleHolder) {
            titleHolder = new SimpleInputableViewHolder(titleView, this);
            titleHolder.showContent(format(items[1], mArchive.getTitle()));
        }
        webView.loadUrl(ArchiveDetailsFragment.getUrl(mArchive.getId(), mArchive.getOwnType(), false, mArchive.getH5(), false));
    }

    private void tryReplyArchive() {
        String title = titleHolder.getValue();
        if (isEmpty(title)) {
            ToastHelper.make().showMsg(R.string.ui_text_archive_reply_title_blank);
            return;
        }
        String content = contentView.getValue();
        if (isEmpty(content)) {
            ToastHelper.make().showMsg(R.string.ui_text_archive_reply_content_blank);
            return;
        }
        replyArchive(title, content);
    }

    private void replyArchive(String title, String content) {
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    ToastHelper.make().showMsg(R.string.ui_text_archive_reply_success);
                    finish();
                }
            }
        }).reply(mArchive.getId(), title, content);
    }
}
