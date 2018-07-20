package com.leadcom.android.isp.fragment.archive;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.hlk.hlklib.layoutmanager.CustomLinearLayoutManager;
import com.hlk.hlklib.lib.inject.ViewId;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.activity.MainActivity;
import com.leadcom.android.isp.activity.WelcomeActivity;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.archive.ArchivePermissionRequest;
import com.leadcom.android.isp.api.archive.ArchiveQueryRequest;
import com.leadcom.android.isp.api.archive.ArchiveRequest;
import com.leadcom.android.isp.api.common.ShareRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.org.ConcernRequest;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.etc.NetworkUtil;
import com.leadcom.android.isp.etc.SysInfoUtil;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseCmtLikeColFragment;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.common.ImageViewerFragment;
import com.leadcom.android.isp.helper.DownloadingHelper;
import com.leadcom.android.isp.helper.FilePreviewHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.helper.popup.DeleteDialogHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.helper.publishable.Collectable;
import com.leadcom.android.isp.holder.organization.GroupInterestViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.listener.OnTaskCompleteListener;
import com.leadcom.android.isp.listener.OnTaskFailureListener;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.archive.ArchiveQuery;
import com.leadcom.android.isp.model.common.ArchivePermission;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.common.ShareInfo;
import com.leadcom.android.isp.model.operation.GRPOperation;
import com.leadcom.android.isp.model.organization.Concern;
import com.leadcom.android.isp.model.organization.RelateGroup;
import com.leadcom.android.isp.model.organization.Role;
import com.leadcom.android.isp.model.user.Collection;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <b>功能描述：</b>档案详情页，webview展现档案内容<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/27 14:10 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/27 14:10 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveDetailsFragment extends BaseCmtLikeColFragment {

    private static final String PARAM_DOC_TYPE = "adwvf_archive_type";
    private static final String PARAM_ARCHIVE = "adwvf_archive";
    private static final String PARAM_DRAFT = "adwvf_draft";
    private static final String PARAM_INNER_OPEN = "adwvf_inner_open";
    private static final String PARAM_GROUP_ID = "adwvf_group_id";
    private static final String PARAM_AUTHOR_ID = "adwvf_author_id";
    private static final String PARAM_COVER_URL = "adwvf_cover_url";
    private static boolean isCollected = false;

    public static ArchiveDetailsFragment newInstance(Bundle bundle) {
        ArchiveDetailsFragment adwvf = new ArchiveDetailsFragment();
        adwvf.setArguments(bundle);
        return adwvf;
    }

    private static Bundle getBundle(String archiveId, String groupId, String coverUrl, int archiveType, boolean innerOpen, boolean isDraft, String authorId) {
        Bundle bundle = new Bundle();
        // 档案id
        bundle.putString(PARAM_QUERY_ID, archiveId);
        // 档案所属的组织id
        bundle.putString(PARAM_GROUP_ID, groupId);
        // 档案类型：组织档案或个人档案
        bundle.putInt(PARAM_DOC_TYPE, archiveType);
        // 是否app内部打开的详情页
        bundle.putBoolean(PARAM_INNER_OPEN, innerOpen);
        // 是否是草稿档案
        bundle.putBoolean(PARAM_DRAFT, isDraft);
        // 档案作者id
        bundle.putString(PARAM_AUTHOR_ID, authorId);
        // 档案封面
        bundle.putString(PARAM_COVER_URL, coverUrl);
        return bundle;
    }

    public static void open(BaseFragment fragment, Collection collection) {
        isCollected = true;
        if (collection.getType() == Collection.Type.GROUP_ARCHIVE) {
            open(fragment, collection.getGroDoc());
        } else if (collection.getType() == Collection.Type.USER_ARCHIVE) {
            open(fragment, collection.getUserDoc());
        }
    }

    // 打开详情页并指定一个档案，收藏时用
    public static void open(BaseFragment fragment, Archive archive) {
        open(fragment, archive.getGroupId(), archive.getCover(), (isEmpty(archive.getGroupId()) ? Archive.Type.USER : Archive.Type.GROUP),
                (!isEmpty(archive.getDocId()) ? archive.getDocId() : archive.getId()), false, archive.getUserId());
    }

    // 打开详情页并指定一个档案，收藏时用
    public static void open(BaseFragment fragment, Archive archive, boolean isDraft) {
        open(fragment, archive.getGroupId(), archive.getCover(), (isEmpty(archive.getGroupId()) ? Archive.Type.USER : Archive.Type.GROUP),
                (!isEmpty(archive.getDocId()) ? archive.getDocId() : archive.getId()), isDraft, archive.getUserId());
    }

    public static void open(Context context, String groupId, String cover, String archiveId, int archiveType, boolean isDraft, boolean innerOpen, String authorId) {
        BaseActivity.openActivity(context, ArchiveDetailsFragment.class.getName(),
                getBundle(archiveId, groupId, cover, archiveType, innerOpen, isDraft, authorId), true, false);
    }

    private static void open(BaseFragment fragment, String groupId, String cover, int archiveType, String archiveId, boolean isDraft, String authorId) {
        fragment.openActivity(ArchiveDetailsFragment.class.getName(),
                getBundle(archiveId, groupId, cover, archiveType, true, isDraft, authorId), true, false);
    }

    static String getUrl(String archiveId, int archiveType, boolean isDraft, boolean forShare) {
        // http://113.108.144.2:8038/html/h5file.html?docid=&doctype=&accesstoken=
        // https://www.chacx.cn/html/h5file.html?docid=&doctype=&accesstoken=
        return StringHelper.format("%s/html/h5file.html?docid=%s&owntype=%d&isdraft=%s&accesstoken=%s",
                (Cache.isReleasable() ? "https://www.chacx.cn" : "http://113.108.144.2:8038"),
                archiveId, (archiveType > 0 ? archiveType : Archive.Type.GROUP), isDraft, (forShare ? "" : Cache.cache().accessToken));
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        archiveType = bundle.getInt(PARAM_DOC_TYPE, Archive.Type.GROUP);
        isDraft = bundle.getBoolean(PARAM_DRAFT, false);
        mArchive = (Archive) bundle.getSerializable(PARAM_ARCHIVE);
        innerOpen = bundle.getBoolean(PARAM_INNER_OPEN, false);
        groupId = bundle.getString(PARAM_GROUP_ID, "");
        authorId = bundle.getString(PARAM_AUTHOR_ID, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_DOC_TYPE, archiveType);
        bundle.putSerializable(PARAM_ARCHIVE, mArchive);
        bundle.putBoolean(PARAM_DRAFT, isDraft);
        bundle.putBoolean(PARAM_INNER_OPEN, innerOpen);
        bundle.putString(PARAM_GROUP_ID, groupId);
        bundle.putString(PARAM_AUTHOR_ID, authorId);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        INTERNAL_SHAREABLE = false;
        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.setWebChromeClient(new DetailsChromeClient());
        webView.setWebViewClient(new DetailsWebViewClient());
        webView.setDownloadListener(new DetailsDownloadListener());
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_archive_details_new;
    }

    @Override
    public void onDestroy() {
        if (!SysInfoUtil.stackResumed(Activity())) {
            if (!innerOpen) {
                // 如果不是堆栈恢复的app则打开主页面，否则直接关闭即可
                MainActivity.start(Activity());
            }
        }
        isCollected = false;
        super.onDestroy();
    }

    private int archiveType;
    private boolean isDraft;
    private String groupId, authorId;
    /**
     * 标记是否是app内部打开的详情页
     */
    private boolean innerOpen;
    private Archive mArchive;
    private Role myRole;
    @ViewId(R.id.ui_archive_details_content)
    private WebView webView;

    @Override
    protected void onDelayRefreshComplete(int type) {

    }

    @Override
    public void doingInResume() {
        displayArchive();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    protected void onSwipeRefreshing() {
    }

    @Override
    protected void onLoadingMore() {
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    @Override
    protected boolean onBackKeyPressed() {
        resetResultData();
        return super.onBackKeyPressed();
    }

    private void resetResultData() {
        if (null != mArchive) {
            Intent intent = new Intent();
            intent.putExtra(RESULT_ARCHIVE, mArchive);
            intent.putExtra(RESULT_STRING, mArchive.getId());
            Activity().setResult(Activity.RESULT_OK, intent);
        }
    }

    private void deleteDocument() {
        //setLoadingText(R.string.ui_text_document_details_deleting_document);
        displayLoading(true);
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                displayLoading(false);
                ToastHelper.make().showMsg(message);
                if (success) {
                    new Dao<>(Archive.class).delete(mQueryId);
                    // 返回成功
                    resultData(mQueryId);
                }
            }
        }).delete(archiveType, mQueryId);
    }

    private void resetRightIconEvent() {
        setRightText(R.string.ui_base_text_edit);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                rightIconClick();
            }
        });
    }

    private void rightIconClick() {
        if (isDraft) {
            String type = mArchive.isAttachmentArchive() ? ArchiveEditorFragment.ATTACHABLE :
                    (mArchive.isMultimediaArchive() ? ArchiveEditorFragment.MULTIMEDIA : ArchiveEditorFragment.TEMPLATE);
            ArchiveEditorFragment.open(ArchiveDetailsFragment.this, mQueryId, type);
            finish();
        } else {
            loadingArchivePermission();
        }
    }

    private void loadingArchivePermission() {
        ArchivePermissionRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<ArchivePermission>() {
            @Override
            public void onResponse(ArchivePermission data, boolean success, String message) {
                super.onResponse(data, success, message);
                if (success) {
                    enableShareDelete = data.isDeletable();
                    enableShareForward = data.isFlowable();
                    enableShareRecommend = data.isRecommendable() && !data.isRecommended();
                    enableShareRecommended = data.isRecommendable() && data.isRecommended();
                    mArchive.setRecommend(data.isRecommended() ? Archive.RecommendType.RECOMMENDED : Archive.RecommendType.UN_RECOMMEND);
                    fetchingShareInfo();
                }
            }
        }).permission(mQueryId);
    }

    /**
     * 当前角色是否具有某项权限
     */
    private boolean hasOperation(String operation) {
        return null != myRole && myRole.hasOperation(operation);
    }

    private void displayArchive(Archive archive) {

        setCustomTitle(archive.getTitle());

        if (isDraft) {
            // 草稿档案只能查看
            if (archive.isAuthor()) {
                resetRightIconEvent();
            }
        } else if (!isCollected) {
            // 不是收藏过来的内容
            // 非草稿档案，可以分享等等
            setRightIcon(R.string.ui_icon_more);
            setRightTitleClickListener(new OnTitleButtonClickListener() {
                @Override
                public void onClick() {
                    rightIconClick();
                }
            });
        }
        myRole = Cache.cache().getGroupRole(archive.getGroupId());
        // 设置收藏的参数为档案
        if (!isCollected) {
            Collectable.resetArchiveCollectionParams(archive);
        }

        String url = getUrl(archive.getId(), archive.getOwnType(), isDraft, false);
        log(url);
        webView.loadUrl(url);
        loadingArchiveDetails();
    }

    private void displayArchive() {
        if (null == mArchive) {
            mArchive = new Archive();
            // 档案id
            mArchive.setId(mQueryId);
            // 组织id
            mArchive.setGroupId(groupId);
            // 档案id
            mArchive.setDocId(mQueryId);
            // 档案类型：1=组织、2=个人
            mArchive.setOwnType(archiveType);
            // 档案作者id
            mArchive.setUserId(authorId);

            displayArchive(mArchive);
        }
    }

    private void loadingArchiveDetails() {
        ArchiveQueryRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<ArchiveQuery>() {
            @Override
            public void onResponse(ArchiveQuery archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success && null != archive) {
                    if (null != archive.getGroDoc()) {
                        mArchive = archive.getGroDoc();
                    } else if (null != archive.getUserDoc()) {
                        mArchive = archive.getUserDoc();
                    } else if (null != archive.getDocDraft()) {
                        mArchive = archive.getDocDraft();
                    }
                }
            }
        }).find(archiveType, mQueryId);
    }

    private void prepareShareDialogElement(Archive archive) {
        // 档案管理员/组织管理员/档案作者可以删除档案
        if (isEmpty(archive.getGroupId())) {
            // 个人档案且当前用户是作者时，允许删除
            enableShareDelete = archive.isAuthor();
        } else {
            // 组织档案
            // 是否可以删除档案
            enableShareDelete = archive.isAuthor() || hasOperation(GRPOperation.ARCHIVE_DELETE);
            enableShareForward = hasOperation(GRPOperation.ARCHIVE_FORWARD);
            enableShareRecommend = archive.isPublic() && !archive.isRecommend() && hasOperation(GRPOperation.ARCHIVE_RECOMMEND);
            enableShareRecommended = archive.isRecommend() && hasOperation(GRPOperation.ARCHIVE_RECOMMEND);

        }
    }

    private class DetailsChromeClient extends WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            setCustomTitle(title);
            super.onReceivedTitle(view, title);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            if (message.contains("未找到") || message.contains("删除")) {
                ToastHelper.make().showMsg(message);
                finish();
                result.confirm();
                return true;
            } else if (Utils.isUrl(message)) {
                if (!message.contains("images/dz1.png") && !message.contains("images/dz6.png")
                        && !message.contains("images/sc1.png") && !message.contains("images/sc4.png")
                        && !message.contains("images/xx3.png")
                        && !message.contains("images/fj.png")) {
                    // 打开图片浏览器
                    ImageViewerFragment.open(ArchiveDetailsFragment.this, message);
                }
                result.confirm();
                return true;
            }
            return super.onJsAlert(view, url, message, result);
        }
    }

    private class DetailsWebViewClient extends WebViewClient {

        private boolean checkSchema(String url) {
            if (url.startsWith("leadcom://")) {
                WelcomeActivity.open(Activity(), url);
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
            displayLoading(true);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            displayLoading(false);
            super.onPageFinished(view, url);
            setImageClick(view);
        }

        private void setImageClick(WebView view) {
            String jsCode = "javascript:(function() {" +
                    "   var imgs = document.getElementsByTagName(\"img\");" +
                    "   for(var i = 0; i < imgs.length; i++) {" +
                    "       imgs[i].onclick = function() {" +
                    "           alert(this.src);" +
                    "       }" +
                    "   }" +
                    "   var videos = document.getElementsByTagName(\"video\");" +
                    "   for(var i = 0; i < videos.length; i++) {" +
                    "       videos[i].setAttribute(\"style\", \"width: 100%;\");" +
                    "   }" +
                    "})()";
            view.loadUrl(jsCode);
        }
    }

    private class DetailsDownloadListener implements DownloadListener {
        private String local, extension, name, url;

        // 通过下载链接获取附件的文件名
        private String getAttachmentName(String url) {
            for (Attachment attachment : mArchive.getImage()) {
                if (url.equals(attachment.getUrl())) {
                    return attachment.getName();
                }
            }
            for (Attachment attachment : mArchive.getVideo()) {
                if (url.equals(attachment.getUrl())) {
                    return attachment.getName();
                }
            }
            for (Attachment attachment : mArchive.getOffice()) {
                if (url.equals(attachment.getUrl())) {
                    return attachment.getName();
                }
            }
            for (Attachment attachment : mArchive.getAttach()) {
                if (url.equals(attachment.getUrl())) {
                    return attachment.getName();
                }
            }
            return null;
        }

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            if (!NetworkUtil.isNetAvailable(App.app())) {
                ToastHelper.make().showMsg(R.string.ui_base_text_network_invalid);
                return;
            }
            String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
            if (!downloadPath.endsWith("/")) {
                downloadPath += "/";
            }
            this.url = url;
            name = getAttachmentName(url);
            if (isEmpty(name)) {
                name = url.substring(url.lastIndexOf('/') + 1);
            }
            local = downloadPath + name;
            extension = isEmpty(mimetype) ? name.substring(name.lastIndexOf('.') + 1) : mimetype;
            File file = new File(local);
            boolean exists = file.exists();
            log(format("download file, url: %s, local: %s, extension: %s, exists: %s", url, local, extension, exists));
            if (exists) {
                FilePreviewHelper.previewFile(Activity(), local, name, extension);
                return;
            }
            if (!NetworkUtil.isWifi(App.app())) {
                // 如果不是wifi环境则提醒用户需要消耗流量下载
                warningDownload();
            } else {
                tryDownload();
            }
        }

        private void warningDownload() {
            DeleteDialogHelper.helper().init(ArchiveDetailsFragment.this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
                @Override
                public boolean onConfirm() {
                    tryDownload();
                    return true;
                }
            }).setTitleText(R.string.ui_base_text_network_not_wifi).setConfirmText(R.string.ui_base_text_confirm).show();
        }

        private void tryDownload() {
            DownloadingHelper.helper().init(Activity()).setShowNotification(true).setOnTaskFailureListener(new OnTaskFailureListener() {
                @Override
                public void onFailure() {
                    ToastHelper.make().showMsg(R.string.ui_system_updating_failure);
                }
            }).setOnTaskCompleteListener(new OnTaskCompleteListener() {
                @Override
                public void onComplete() {
                    //String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                    //ToastHelper.make().showMsg(format("已下载到%s", downloadPath));
                    log(format("downloaded, url: %s, local: %s, ext: %s, name: %s", url, local, extension, name));
                    FilePreviewHelper.previewFile(Activity(), local, name, extension);
                }
            }).setRemoveNotificationWhenComplete(true).download(url, local, extension, "", "");
        }
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == REQUEST_SELECT) {
            finish();
            // 需要跳转到会话页面并且关闭档案详情页
            //String teamId = getResultedData(data);
            //NimUIKit.startTeamSession(Activity(), teamId);
        } else if (requestCode == REQUEST_GROUP) {
            ArrayList<RelateGroup> groups = RelateGroup.from(getResultedData(data));
            // 转发到指定的组织
            if (null != groups && groups.size() > 0) {
                ArrayList<String> ids = new ArrayList<>();
                String name = "";
                for (RelateGroup group : groups) {
                    if (ids.size() < 1) {
                        name = group.getGroupName();
                    }
                    ids.add(group.getId());
                }
                if (ids.size() > 1) {
                    name += "等";
                }
                openForwardDialog(ids, name);
            }
        }
        super.onActivityResult(requestCode, data);
    }

    private View forwardDialog;
    private TextView dialogTitle, shareTitle, shareSummary;
    private ImageDisplayer shareImage;

    private void openForwardDialog(final ArrayList<String> groupIds, final String groupName) {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == forwardDialog) {
                    forwardDialog = View.inflate(Activity(), R.layout.popup_dialog_share_in_app, null);

                    dialogTitle = forwardDialog.findViewById(R.id.ui_dialog_share_in_app_title);
                    shareTitle = forwardDialog.findViewById(R.id.ui_dialog_share_in_app_title_label);
                    shareSummary = forwardDialog.findViewById(R.id.ui_dialog_share_in_app_summary_label);
                    shareImage = forwardDialog.findViewById(R.id.ui_dialog_share_in_app_image);
                }
                return forwardDialog;
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {
                dialogTitle.setText(Html.fromHtml(getString(R.string.ui_base_share_to_forward_dialog_title, groupName)));
                shareTitle.setText(mArchive.getTitle());
                shareSummary.setText(isEmpty(mArchive.getContent()) ? "" : Html.fromHtml(mArchive.getContent()));
                shareImage.setVisibility(isEmpty(mArchive.getCover()) ? View.GONE : View.VISIBLE);
                shareImage.displayImage(mArchive.getCover(), getDimension(R.dimen.ui_static_dp_50), false, false);
            }
        }).addOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                tryPushArchive(groupIds);
                return true;
            }
        }).setPopupType(DialogHelper.SLID_IN_BOTTOM).show();
    }

    private View pushDialog;
    private RecyclerView concerned;
    private ConcernAdapter cAdapter;

    private void openPushDialog() {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == pushDialog) {
                    pushDialog = View.inflate(Activity(), R.layout.popup_dialog_archive_push, null);
                    concerned = pushDialog.findViewById(R.id.ui_tool_swipe_refreshable_recycler_view);
                    concerned.setLayoutManager(new CustomLinearLayoutManager(concerned.getContext()));
                    cAdapter = new ConcernAdapter();
                    concerned.setAdapter(cAdapter);
                    showConcernedGroups();
                }
                return pushDialog;
            }

            private void showConcernedGroups() {
                // 查询关注我的组织列表并推送
                ConcernRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Concern>() {
                    @Override
                    public void onResponse(List<Concern> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                        super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                        if (success && null != list) {
                            cAdapter.update(list);
                        }
                    }
                }).list(mArchive.getGroupId(), ConcernRequest.CONCERN_FROM, 1, "");
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {
            }
        }).addOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                ArrayList<String> groupIds = new ArrayList<>();
                Iterator<Concern> iterator = cAdapter.iterator();
                while (iterator.hasNext()) {
                    Concern concern = iterator.next();
                    if (concern.isSelected()) {
                        groupIds.add(concern.getId());
                    }
                }
                tryPushArchive(groupIds);
                return true;
            }
        }).setPopupType(DialogHelper.SLID_IN_RIGHT).show();
    }

    private void tryPushArchive(ArrayList<String> groupIds) {
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    ToastHelper.make().showMsg(message);
                }
            }
        }).push(groupIds, mQueryId);
    }

    private OnViewHolderClickListener clickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Concern concern = cAdapter.get(index);
            concern.setSelected(!concern.isSelected());
            cAdapter.update(concern);
        }
    };

    @Override
    protected void onLikeComplete(boolean success, Model model) {

    }

    @Override
    protected void onCollectComplete(boolean success, Model model) {

    }

    private class ConcernAdapter extends RecyclerViewAdapter<GroupInterestViewHolder, Concern> {

        @Override
        public GroupInterestViewHolder onCreateViewHolder(View itemView, int viewType) {
            GroupInterestViewHolder holder = new GroupInterestViewHolder(itemView, ArchiveDetailsFragment.this);
            holder.setSelectable(true);
            holder.addOnViewHolderClickListener(clickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_group_interesting_item;
        }

        @Override
        public void onBindHolderOfView(GroupInterestViewHolder holder, int position, @Nullable Concern item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(Concern item1, Concern item2) {
            return 0;
        }
    }

    private void fetchingShareInfo() {
        if (null == mShareInfo) {
            int type = archiveType == Archive.Type.GROUP ? ShareRequest.ARCHIVE_GROUP : ShareRequest.ARCHIVE_USER;
            ShareRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<ShareInfo>() {
                @Override
                public void onResponse(ShareInfo shareInfo, boolean success, String message) {
                    super.onResponse(shareInfo, success, message);
                    if (success && null != shareInfo) {
                        mShareInfo = shareInfo;
                        openShareDialog();
                    }
                }
            }).getShareInfo(mQueryId, 1, type);
        } else {
            openShareDialog();
        }
    }

    @Override
    protected void shareToDelete() {
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                deleteDocument();
                return true;
            }
        }).setTitleText(R.string.ui_text_archive_details_delete).show();
    }

    @Override
    protected void shareToForward() {
        openPushDialog();
    }

    @Override
    protected void shareToRecommend() {
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                tryRecommendArchive();
                return true;
            }
        }).setTitleText(R.string.ui_text_archive_details_recommend).setConfirmText(R.string.ui_base_text_recommend).show();
    }

    @Override
    protected void shareToRecommended() {
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                tryRecommendArchive();
                return true;
            }
        }).setTitleText(R.string.ui_text_archive_details_recommended).setConfirmText(R.string.ui_base_text_confirm).show();
    }

    private void tryRecommendArchive() {
        if (null != mArchive) {
            if (isEmpty(mArchive.getId()) || mArchive.getId().equals("null")) {
                ToastHelper.make().showMsg(R.string.ui_archive_recommend_archive_id_null);
            } else {
                // 没有推荐则推荐，有推荐则取消推荐
                if (!mArchive.isRecommend()) {
                    // 推荐
                    recommendArchive();
                } else {
                    unRecommendArchive();
                }
            }
        } else {
            ToastHelper.make().showMsg(R.string.ui_archive_recommend_archive_null);
        }
    }

    // 推荐档案
    private void recommendArchive() {
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    mArchive.setRecommend(Archive.RecommendType.RECOMMENDED);
                    prepareShareDialogElement(mArchive);
                    ToastHelper.make().showMsg(R.string.ui_text_archive_details_recommend_ok);
                }
            }
        }).recommend(mQueryId);
    }

    // 取消推荐档案
    private void unRecommendArchive() {
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    mArchive.setRecommend(Archive.RecommendType.UN_RECOMMEND);
                    prepareShareDialogElement(mArchive);
                    ToastHelper.make().showMsg(R.string.ui_text_archive_details_recommended_ok);
                }
                displayLoading(false);
            }
        }).unRecommend(mQueryId);
    }

    @Override
    protected void shareToAward() {
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                tryAwardArchive();
                return true;
            }
        }).setTitleText(R.string.ui_text_archive_details_award).setConfirmText(R.string.ui_base_text_confirm).show();
    }

    @Override
    protected void shareToAwarded() {
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                tryAwardArchive();
                return true;
            }
        }).setTitleText(R.string.ui_text_archive_details_awarded).setConfirmText(R.string.ui_base_text_confirm).show();
    }

    private void tryAwardArchive() {
        if (null != mArchive) {
            if (isEmpty(mArchive.getId()) || mArchive.getId().equals("null")) {
                ToastHelper.make().showMsg(R.string.ui_archive_recommend_archive_id_null);
            } else {
                // 没有推荐则推荐，有推荐则取消推荐
                if (!mArchive.awarded()) {
                    // 设置获奖标记
                    awardArchive();
                } else {
                    unAwardArchive();
                }
            }
        } else {
            ToastHelper.make().showMsg(R.string.ui_archive_recommend_archive_null);
        }
    }

    private void awardArchive() {
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    mArchive.setAwardable(Archive.AwardType.AWARDED);
                    prepareShareDialogElement(mArchive);
                    ToastHelper.make().showMsg(R.string.ui_text_archive_details_award_ok);
                }
                displayLoading(false);
            }
        }).award(mQueryId);
    }

    private void unAwardArchive() {
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    mArchive.setAwardable(Archive.AwardType.NONE);
                    prepareShareDialogElement(mArchive);
                    ToastHelper.make().showMsg(R.string.ui_text_archive_details_awarded_ok);
                }
                displayLoading(false);
            }
        }).unaward(mQueryId);
    }
}
