package com.gzlk.android.isp.holder.archive;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.fragment.archive.ArchiveDetailsFragment;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.common.SimpleClickableViewHolder;
import com.gzlk.android.isp.lib.view.ExpandableTextView;
import com.gzlk.android.isp.model.archive.Archive;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;

/**
 * <b>功能描述：</b>文档详情头部文档基本信息<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/27 07:58 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/27 07:58 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveDetailsHeaderViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_tool_view_document_details_header_title)
    private View titleView;
    @ViewId(R.id.ui_tool_view_document_details_header_source)
    private View sourceView;
    @ViewId(R.id.ui_tool_view_document_details_header_time)
    private View timeView;
    @ViewId(R.id.ui_tool_view_document_details_header_privacy)
    private View privacyView;
    @ViewId(R.id.ui_tool_view_document_details_header_desc)
    private ExpandableTextView descView;
    @ViewId(R.id.ui_holder_view_document_details_content)
    private WebView contentView;

    // holder
    private SimpleClickableViewHolder titleHolder;
    private SimpleClickableViewHolder sourceHolder;
    private SimpleClickableViewHolder timeHolder;
    private SimpleClickableViewHolder privacyHolder;

    // items
    private String[] items;
    private static boolean firstEnter = true;

    @SuppressLint("SetJavaScriptEnabled")
    public ArchiveDetailsHeaderViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        firstEnter = true;
        contentView.getSettings().setUseWideViewPort(true);
        contentView.getSettings().setLoadWithOverviewMode(true);
        contentView.getSettings().setDomStorageEnabled(true);
        contentView.getSettings().setJavaScriptEnabled(true);
        contentView.setWebViewClient(new MyWebViewClient());
        contentView.setWebChromeClient(new WebChromeClient());
        initializeHolders();
    }

    private void initializeHolders() {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_document_details_item);
        }
        if (null == titleHolder) {
            titleHolder = new SimpleClickableViewHolder(titleView, fragment());
        }
        if (null == sourceHolder) {
            sourceHolder = new SimpleClickableViewHolder(sourceView, fragment());
        }
        if (null == timeHolder) {
            timeHolder = new SimpleClickableViewHolder(timeView, fragment());
        }
        if (null == privacyHolder) {
            privacyHolder = new SimpleClickableViewHolder(privacyView, fragment());
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void showContent(final Archive archive) {
        titleHolder.showContent(format(items[0], archive.getTitle()));
        sourceHolder.showContent(format(items[1], archive.getUserName()));
        timeHolder.showContent(format(items[2], fragment().formatDate(archive.getHappenDate())));
        privacyHolder.showContent(format(items[3], ""));
        descView.setText(StringHelper.getString(R.string.ui_text_document_details_intro, archive.getIntro()));
        descView.makeExpandable();
        contentView.setVisibility(StringHelper.isEmpty(archive.getMarkdown()) ? View.GONE : View.VISIBLE);
        if (!isEmpty(archive.getMarkdown())) {
            int type = Cache.isReleasable() ? 1 : 0;
            int mType = isEmpty(archive.getGroupId()) ? Archive.Type.USER : Archive.Type.GROUP;
            String url = format("%s?test=%d&%sDocId=%s", BASE_URL, type, (mType == Archive.Type.GROUP ? "gro" : "user"), archive.getId());
            contentView.loadUrl(url);
        }
    }

    private static final String BASE_URL = "http://113.108.144.2:8045/lcbase-manage/editor/md_view.html";

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
            if (firstEnter) {
                firstEnter = false;
                if (fragment() instanceof ArchiveDetailsFragment) {
                    ((ArchiveDetailsFragment) fragment()).showLoadingContent(true);
                }
            }
        }

        /**
         * 在页面加载结束时调用
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            if (fragment() instanceof ArchiveDetailsFragment) {
                ((ArchiveDetailsFragment) fragment()).showLoadingContent(false);
            }
            super.onPageFinished(view, url);
        }
    }
}
