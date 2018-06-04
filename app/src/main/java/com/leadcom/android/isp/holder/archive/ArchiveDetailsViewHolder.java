package com.leadcom.android.isp.holder.archive;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.ToggleButton;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.WelcomeActivity;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.model.archive.Archive;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/27 14:42 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/27 14:42 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveDetailsViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_archive_details_title)
    private TextView titleView;
    @ViewId(R.id.ui_holder_view_archive_details_time)
    private TextView timeView;
    @ViewId(R.id.ui_holder_view_archive_details_author)
    private TextView authorView;
    @ViewId(R.id.ui_holder_view_archive_details_cover)
    private ImageDisplayer coverView;
    @ViewId(R.id.ui_holder_view_archive_details_content)
    private WebView contentView;
    @ViewId(R.id.ui_holder_view_archive_details_public_layout)
    private View publicView;
    @ViewId(R.id.ui_holder_view_archive_details_author_layout)
    private View authorLayout;
    @ViewId(R.id.ui_holder_view_archive_details_public_toggle)
    private ToggleButton publicToggle;
    @ViewId(R.id.ui_holder_view_archive_details_labels)
    private FlexboxLayout labelsLayout;
    @ViewId(R.id.ui_holder_view_archive_details_divider)
    private View divider;

    @ViewId(R.id.ui_tool_loading_container)
    private View loadingView;

    private boolean isOnlyWebView = true;
    private int width, height, margin;
    private boolean isManager = false, isDraft = false;

    public ArchiveDetailsViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        WebSettings settings = contentView.getSettings();
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            contentView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        contentView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (null != titleListener) {
                    titleListener.onReceivedTitle(title);
                }
                super.onReceivedTitle(view, title);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                if (message.contains("未找到") || message.contains("删除")) {
                    ToastHelper.make().showMsg(message);
                    fragment().finish();
                    result.confirm();
                    return true;
                }
                return super.onJsAlert(view, url, message, result);
            }
        });
        contentView.setWebViewClient(new WebViewClient() {

            private boolean checkSchema(String url) {
                if (url.startsWith("leadcom://")) {
                    WelcomeActivity.open(getContext(), url);
                    return true;
                }
                return false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!checkSchema(url)) {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= 21) {
                    if (!checkSchema(request.getUrl().toString())) {
                        view.loadUrl(request.getUrl().toString());
                    }
                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                loadingView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                loadingView.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }
        });
        margin = getDimension(R.dimen.ui_static_dp_5);
        resetCoverSize();
        publicToggle.addOnToggleChangedListener(new ToggleButton.OnToggleChangedListener() {
            @Override
            public void onToggle(ToggleButton button, boolean on) {
                if (null != mOnViewHolderElementClickListener) {
                    mOnViewHolderElementClickListener.onClick(button, getAdapterPosition());
                }
            }
        });
    }

    /**
     * 设置是否是管理员或者档案发布者
     */
    public void setIsManager(boolean isManager) {
        this.isManager = isManager;
        publicView.setVisibility(this.isManager ? View.VISIBLE : View.GONE);
    }

    public void setIsCollected(boolean isCollect) {
        if (isCollect) {
            publicView.setVisibility(View.GONE);
        }
    }

    public void setIsDraft(boolean isDraft) {
        this.isDraft = isDraft;
        if (isDraft) {
            publicView.setVisibility(View.GONE);
        }
    }

    public void onFragmentDestroy() {
        contentView.destroy();
    }

    private void resetCoverSize() {
        width = fragment().getScreenWidth() - getDimension(R.dimen.ui_base_dimen_margin_padding) * 2;
        height = width / 2;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) coverView.getLayoutParams();
        params.height = height;
        coverView.setLayoutParams(params);
    }

    public static String getUrl(String archiveId, int archiveType, boolean isDraft, boolean forShare) {
        // http://113.108.144.2:8038/html/h5file.html?docid=&doctype=&accesstoken=
        // https://www.chacx.cn/html/h5file.html?docid=&doctype=&accesstoken=
        return StringHelper.format("%s/html/h5file.html?docid=%s&owntype=%d&isdraft=%s&accesstoken=%s",
                (Cache.isReleasable() ? "https://www.chacx.cn" : "http://113.108.144.2:8038"),
                archiveId, (archiveType > 0 ? archiveType : Archive.Type.GROUP), isDraft, (forShare ? "" : Cache.cache().accessToken));
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void showContent(Archive archive) {
        if (isOnlyWebView) {
            titleView.setVisibility(View.GONE);
            authorLayout.setVisibility(View.GONE);
            publicView.setVisibility(View.GONE);
            labelsLayout.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);

            contentView.getSettings().setUseWideViewPort(true);
            contentView.getSettings().setLoadWithOverviewMode(true);
            contentView.getSettings().setDomStorageEnabled(true);
            contentView.getSettings().setJavaScriptEnabled(true);

            String url = getUrl(archive.getId(), archive.getOwnType(), isDraft, false);
            log(url);
            contentView.loadUrl(url);
            return;
        }
        titleView.setText(archive.getTitle());
        timeView.setText(fragment().formatDate(archive.getCreateDate(), R.string.ui_base_text_date_time_format));
        String source = (isEmpty(archive.getSource()) || archive.getSource().equals(archive.getUserName())) ? "" : StringHelper.getString(R.string.ui_text_archive_details_source_text, archive.getSource());
        authorView.setText(StringHelper.getString(R.string.ui_text_archive_details_author_text, archive.getUserName(), source));
        String cover = archive.getCover();
//        if (isEmpty(cover)) {
//            Random random = new Random(10);
//            cover = "drawable://" + (random.nextInt() % 2 == 0 ? R.drawable.img_activity_cover_1 : R.drawable.img_activity_cover_2);
//        }
        //coverView.displayImage(cover, width, height, false, false);
        //coverView.setVisibility(isEmpty(cover) ? View.GONE : View.VISIBLE);
        String content = archive.getContent();
        contentView.setVisibility((isEmpty(content) || content.equals("null")) ? View.GONE : View.VISIBLE);
        content = isEmpty(content) ? "" : Utils.clearStyleEqualsXXX(content);
        contentView.loadData(StringHelper.getString(R.string.ui_text_archive_details_content_html, content).replace("width: 100per", "width: 100%"), "text/html; charset=UTF-8", null);
        //bottomLine.setVisibility(archive.getCmtNum() > 0 ? View.VISIBLE : View.GONE);
        //if (archive.isDraft()) {
        // 草稿不需要公开
        //    publicView.setVisibility(View.GONE);
        //}
        if (archive.getAuthPublic() == 1) {
            // 公开
            publicToggle.setToggleOn(true);
        } else {
            publicToggle.setToggleOff(true);
        }
        labelsLayout.removeAllViews();
        if (!isEmpty(archive.getGroupId())) {
            archive.getLabel().clear();
            if (!isEmpty(archive.getProperty())) {
                archive.getLabel().add(archive.getProperty());
            }
            if (!isEmpty(archive.getCategory())) {
                archive.getLabel().add(archive.getCategory());
            }
        }
        for (String string : archive.getLabel()) {
            TextView textView = (TextView) View.inflate(labelsLayout.getContext(), R.layout.holder_view_archive_label, null);
            textView.setText(string);
            labelsLayout.addView(textView);
            int lines = labelsLayout.getFlexLines().size();
            FlexboxLayout.LayoutParams params = (FlexboxLayout.LayoutParams) textView.getLayoutParams();
            params.rightMargin = margin;
            params.topMargin = lines > 0 ? margin : 0;
            textView.setLayoutParams(params);
        }
    }

    private OnReceivedTitleListener titleListener;

    public void setOnReceivedTitleListener(OnReceivedTitleListener l) {
        titleListener = l;
    }

    public interface OnReceivedTitleListener {
        void onReceivedTitle(String title);
    }
}
