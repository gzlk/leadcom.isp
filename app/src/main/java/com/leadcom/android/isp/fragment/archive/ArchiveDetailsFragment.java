package com.leadcom.android.isp.fragment.archive;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.hlk.hlklib.layoutmanager.CustomLinearLayoutManager;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredButton;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.activity.MainActivity;
import com.leadcom.android.isp.activity.WelcomeActivity;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.archive.ArchivePermissionRequest;
import com.leadcom.android.isp.api.archive.ArchiveRequest;
import com.leadcom.android.isp.api.archive.ClassifyRequest;
import com.leadcom.android.isp.api.common.ShareRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.org.ConcernRequest;
import com.leadcom.android.isp.api.org.MemberRequest;
import com.leadcom.android.isp.api.org.SquadRequest;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.etc.NetworkUtil;
import com.leadcom.android.isp.etc.SysInfoUtil;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseCmtLikeColFragment;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.common.ImageViewerFragment;
import com.leadcom.android.isp.fragment.common.LabelPickFragment;
import com.leadcom.android.isp.fragment.organization.ActivityCollectionFragment;
import com.leadcom.android.isp.fragment.organization.GroupAllPickerFragment;
import com.leadcom.android.isp.fragment.organization.GroupPickerFragment;
import com.leadcom.android.isp.fragment.organization.SquadPickerFragment;
import com.leadcom.android.isp.fragment.organization.SquadsFragment;
import com.leadcom.android.isp.helper.DownloadingHelper;
import com.leadcom.android.isp.helper.FilePreviewHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.helper.popup.ArchiveDetailsHelper;
import com.leadcom.android.isp.helper.popup.DateTimeHelper;
import com.leadcom.android.isp.helper.popup.DeleteDialogHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.helper.publishable.Collectable;
import com.leadcom.android.isp.holder.organization.GroupInterestViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.listener.OnTaskCompleteListener;
import com.leadcom.android.isp.listener.OnTaskFailureListener;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.archive.ArchivePushTarget;
import com.leadcom.android.isp.model.archive.Classify;
import com.leadcom.android.isp.model.common.ArchivePermission;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.common.Seclusion;
import com.leadcom.android.isp.model.common.ShareInfo;
import com.leadcom.android.isp.model.operation.GRPOperation;
import com.leadcom.android.isp.model.organization.Concern;
import com.leadcom.android.isp.model.organization.Member;
import com.leadcom.android.isp.model.organization.RelateGroup;
import com.leadcom.android.isp.model.organization.Role;
import com.leadcom.android.isp.model.organization.Squad;
import com.leadcom.android.isp.model.organization.SubMember;
import com.leadcom.android.isp.model.user.Collection;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    private static final String PARAM_ARCHIVE = "adwvf_archive";
    private static final String PARAM_DRAFT = "adwvf_draft";
    private static final String PARAM_INNER_OPEN = "adwvf_inner_open";
    private static final String PARAM_CLASSIFY_TYPE = "adwvf_classify_type";
    private static final String PARAM_REPORT_STATUS = "adwvf_act_report_status";
    private static boolean isCollected = false;
    private static boolean isLoaded = false;
    public static boolean isIndividual = false;

    public static ArchiveDetailsFragment newInstance(Bundle bundle) {
        ArchiveDetailsFragment adwvf = new ArchiveDetailsFragment();
        adwvf.setArguments(bundle);
        return adwvf;
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
        open(fragment, archive, false);
    }

    // 打开详情页并指定一个档案，收藏时用
    public static void open(BaseFragment fragment, Archive archive, boolean isDraft) {
        if (!isEmpty(archive.getDocId())) {
            archive.setId(archive.getDocId());
        }
        isLoaded = false;
        archive.setOwnType(isEmpty(archive.getGroupId()) ? Archive.Type.USER : Archive.Type.GROUP);
        Bundle bundle = getBundle(archive, archive.getId(), isDraft, true);
        fragment.openActivity(ArchiveDetailsFragment.class.getName(), bundle, REQUEST_DELETE, true, false);
    }

    public static void open(Context context, Archive archive, boolean innerOpen) {
        isLoaded = false;
        Bundle bundle = getBundle(archive, archive.getId(), false, innerOpen);
        BaseActivity.openActivity(context, ArchiveDetailsFragment.class.getName(), bundle, REQUEST_DELETE, true, false);
    }

    public static void open(Context context, String groupId, String archiveId, boolean isDraft, boolean innerOpen, String authorId) {
        Archive archive = new Archive();
        archive.setId(archiveId);
        archive.setGroupId(groupId);
        archive.setUserId(authorId);
        isLoaded = false;
        Bundle bundle = getBundle(archive, archiveId, isDraft, innerOpen);
        BaseActivity.openActivity(context, ArchiveDetailsFragment.class.getName(), bundle, REQUEST_DELETE, true, false);
    }

    private static Bundle getBundle(Archive archive, String archiveId, boolean isDraft, boolean innerOpen) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAM_ARCHIVE, archive);
        bundle.putString(PARAM_QUERY_ID, archiveId);
        bundle.putBoolean(PARAM_DRAFT, isDraft);
        bundle.putBoolean(PARAM_INNER_OPEN, innerOpen);
        return bundle;
    }

    private static String getUrl(String archiveId, int archiveType, boolean isDraft, String h5) {
        if (isH5(h5)) {
            // http://113.108.144.2:8038/quesinfo.html              ??
            // http://113.108.144.2:8038/quesinfo.html?id=xxxa      ??
            return h5 + (h5.contains("?") ? "&" : "?") + "accesstoken=" + Cache.cache().accessToken;
        }
        // http://113.108.144.2:8038/html/h5file.html?docid=&doctype=&accesstoken=
        // https://www.chacx.cn/html/h5file.html?docid=&doctype=&accesstoken=
        return StringHelper.format("%s/html/h5file.html?docid=%s&owntype=%d&isdraft=%s&accesstoken=%s",
                (Cache.isReleasable() ? "https://www.chacx.cn" : "http://113.108.144.2:8038"),
                archiveId, (archiveType > 0 ? archiveType : Archive.Type.GROUP), isDraft, Cache.cache().accessToken);
    }

    private static boolean isH5(String h5) {
        // 问答或活动
        return !isEmpty(h5) && (h5.contains("quesinfo.html") || h5.contains("activedetail.html"));
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mArchive = (Archive) bundle.getSerializable(PARAM_ARCHIVE);
        assert mArchive != null;
        archiveType = isEmpty(mArchive.getGroupId()) ? Archive.Type.USER : Archive.Type.GROUP;
        isDraft = bundle.getBoolean(PARAM_DRAFT, false);
        innerOpen = bundle.getBoolean(PARAM_INNER_OPEN, false);
        groupId = mArchive.getGroupId();
        authorId = mArchive.getUserId();
        pushingType = bundle.getInt(PARAM_CLASSIFY_TYPE, 0);
        activityStatus = bundle.getInt(PARAM_REPORT_STATUS, 0);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putSerializable(PARAM_ARCHIVE, mArchive);
        bundle.putBoolean(PARAM_DRAFT, isDraft);
        bundle.putBoolean(PARAM_INNER_OPEN, innerOpen);
        bundle.putInt(PARAM_CLASSIFY_TYPE, pushingType);
        bundle.putInt(PARAM_REPORT_STATUS, activityStatus);
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
        setLeftText(R.string.ui_base_text_back);
        setLeftTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                if (!webView.canGoBack()) {
                    finish();
                } else {
                    webView.goBack();
                }
            }
        });
        initializeActivityControlPosition();
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
        if (null != webView) {
            webView.destroy();
        }
        isIndividual = false;
        isCollected = false;
        super.onDestroy();
    }

    private int archiveType, activityStatus;
    private boolean isDraft;
    private String groupId, authorId;
    private int pushingType = 0;
    private long archiveLoadingStart;
    /**
     * 分类到别的组织或当前组织的栏目
     */
    private static final int PUSH_GROUPS = 1, PUSH_CLASSIFY = 2, PUSH_TRANSFORM = 3;
    /**
     * 标记是否是app内部打开的详情页
     */
    private boolean innerOpen;
    private Archive mArchive;
    private ArrayList<Squad> squads = new ArrayList<>();

    @ViewId(R.id.ui_archive_details_content)
    private WebView webView;
    @ViewId(R.id.ui_archive_details_activity_control)
    private View activityControl;
    @ViewId(R.id.ui_archive_details_activity_deliver)
    private CorneredButton deliverButton;
    @ViewId(R.id.ui_archive_details_activity_sign_in)
    private CorneredButton signButton;
    @ViewId(R.id.ui_archive_details_activity_leave)
    private CorneredButton leaveButton;
    @ViewId(R.id.ui_archive_details_activity_report)
    private CorneredButton reportButton;
    @ViewId(R.id.ui_archive_details_activity_limited)
    private View limitedText;

    @Click({R.id.ui_archive_details_activity_deliver,
            R.id.ui_archive_details_activity_sign_in,
            R.id.ui_archive_details_activity_leave,
            R.id.ui_archive_details_activity_report})
    private void viewClick(View view) {
        switch (view.getId()) {
            case R.id.ui_archive_details_activity_sign_in:
                view.setEnabled(false);
                warningSingInOrLeave(true);
                break;
            case R.id.ui_archive_details_activity_leave:
                view.setEnabled(false);
                warningSingInOrLeave(false);
                break;
            case R.id.ui_archive_details_activity_report:
                ActivityCollectionFragment.open(ArchiveDetailsFragment.this, mArchive);
                break;
            case R.id.ui_archive_details_activity_deliver:
                GroupAllPickerFragment.IS_FOR_DELIVER = true;
                GroupAllPickerFragment.open(ArchiveDetailsFragment.this, mArchive.getGroupId(), mArchive.getGroupName(), null, null);
                //openActivityDeliverDialog(true);
                break;
        }
    }

    private void warningSingInOrLeave(final boolean signIn) {
        int title;
        if (signIn) {
            title = activityStatus == Member.ActivityStatus.LEAVE ? R.string.ui_group_activity_details_sign_up_warning_title_leaved : R.string.ui_group_activity_details_sign_up_warning_title;
        } else {
            title = activityStatus == Member.ActivityStatus.JOINED ? R.string.ui_group_activity_details_leave_warning_title_joined : R.string.ui_group_activity_details_leave_warning_title;
        }
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                tryJoinInActivity(signIn);
                return true;
            }
        }).setOnDialogCancelListener(new DialogHelper.OnDialogCancelListener() {
            @Override
            public void onCancel() {
                if (signIn) {
                    signButton.setEnabled(true);
                } else {
                    leaveButton.setEnabled(true);
                }
            }
        }).setTitleText(title).setConfirmText(R.string.ui_base_text_confirm).show();
    }

    private void tryJoinInActivity(final boolean signIn) {
        MemberRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Member>() {
            @Override
            public void onResponse(Member member, boolean success, String message) {
                super.onResponse(member, success, message);
                if (success) {
                    ToastHelper.make().showMsg(signIn ? R.string.ui_group_activity_details_sign_up_succeed : R.string.ui_group_activity_details_leave_succeed);
                    member.setStatus(String.valueOf(signIn ? Member.ActivityStatus.JOINED : Member.ActivityStatus.LEAVE));
                    refreshReportButtons(member);
                } else {
                    if (signIn) {
                        signButton.setEnabled(true);
                    } else {
                        leaveButton.setEnabled(true);
                    }
                }
            }
        }).joinActivity(mArchive.getGroupId(), mArchive.getGroActivityId(), signIn ? Member.ActivityStatus.JOINED : Member.ActivityStatus.LEAVE);
    }

    @Override
    protected void onDelayRefreshComplete(int type) {

    }

    @Override
    public void doingInResume() {
        displayArchive();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
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
        //finish();
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

    private void resetRightIconEvent(int icon, int text) {
        setRightText(text);
        setRightIcon(icon);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                rightIconClick();
            }
        });
    }

    private void rightIconClick() {
        if (isDraft) {
            ArchiveEditorFragment.open(ArchiveDetailsFragment.this, mQueryId, mArchive.getDocType());
            finish();
        } else {
            if (mArchive.isActivity()) {
                openActivityDeliverDialog(false);
            } else {
                loadingArchivePermission();
            }
        }
    }

    private void openActivityDeliverDialog(boolean direct) {
        enableShareWX = false;
        enableShareTimeLine = false;
        enableShareQQ = false;
        enableShareQZone = false;
        enableTransform = Role.isManager(mArchive.getGroupId());
        if (direct) {
            transform();
        } else {
            openShareDialog();
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
                    mArchive.setAwardable(data.isAwarded() ? Archive.AwardType.AWARDED : Archive.AwardType.NONE);
                    enableAward = data.isAwardable() && !data.isAwarded();
                    enableAwarded = data.isAwardable() && data.isAwarded();
                    enableReplay = data.isRepliable();
                    enableClassify = data.isClassifiable();
                    enableProperty = data.isDeletable();
                    fetchingShareInfo();
                }
            }
        }).permission(mQueryId);
    }

    private void displayArchive(Archive archive) {

        setCustomTitle(archive.getTitle());

        if (isDraft) {
            // 草稿档案只能查看
            if (archive.isAuthor()) {
                resetRightIconEvent(0, R.string.ui_base_text_edit);
            }
        } else if (mArchive.isActivity()) {
            if (!mArchive.isStopped() && Role.hasOperation(mArchive.getGroupId(), GRPOperation.ACTIVITY_DELIVER)) {
                // 有下发活动的权限时才显示更多按钮
                resetRightIconEvent(R.string.ui_icon_more, 0);
            }
        } else if (!isCollected && !isH5(archive.getH5())) {
            // 不是收藏过来的内容
            // 非草稿档案，可以分享等等
            resetRightIconEvent(R.string.ui_icon_more, 0);
        }
        // 设置收藏的参数为档案
        if (!isCollected) {
            Collectable.resetArchiveCollectionParams(archive);
        }

        String url = getUrl(mQueryId, mArchive.getOwnType(), isDraft, archive.getH5());
        log(url);
        webView.loadUrl(url);
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
        } else if (!isLoaded) {
            isLoaded = true;
            displayArchive(mArchive);
        }
    }

    private void prepareShareDialogElement(Archive archive) {
        String groupId = archive.getGroupId();
        // 档案管理员/组织管理员/档案作者可以删除档案
        if (isEmpty(groupId)) {
            // 个人档案且当前用户是作者时，允许删除
            enableShareDelete = archive.isAuthor();
        } else {
            // 组织档案
            // 是否可以删除档案
            enableShareDelete = archive.isAuthor() || Role.hasOperation(groupId, GRPOperation.ARCHIVE_DELETE);
            enableShareForward = Role.hasOperation(groupId, GRPOperation.ARCHIVE_FORWARD);
            enableShareRecommend = archive.isPublic() && !archive.isRecommend() && Role.hasOperation(groupId, GRPOperation.ARCHIVE_RECOMMEND);
            enableShareRecommended = archive.isRecommend() && Role.hasOperation(groupId, GRPOperation.ARCHIVE_RECOMMEND);

        }
    }

    private void initializeActivityControlPosition() {
        Handler().post(new Runnable() {
            @Override
            public void run() {
                displayActivityControl(false);
            }
        });
    }

    private void displayActivityControl(final boolean shown) {
        activityControl.animate().alpha(shown ? 1.0f : 0.0f)
                .translationY(shown ? 0 : activityControl.getMeasuredHeight() * 1.1f)
                .setDuration(duration())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (shown) {
                            activityControl.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!shown) {
                            activityControl.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();
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
            } else if (message.startsWith("href=")) {
                String msg = message.replace("href=", "");
                startingDownload(msg);
                result.confirm();
                return true;
            }
            return super.onJsAlert(view, url, message, result);
        }
    }

    private class DetailsWebViewClient extends WebViewClient {

        //private final String[] segments = new String[]{"bootstrap.min.css", "bootstrap.min.js", "jquery.cookie.js", "jquery.min.js", "template-web.js"};

        private ArrayList<String> segments = new ArrayList<String>() {{
            add("bootstrap.min.css");
            add("bootstrap.min.js");
            add("jquery.cookie.js");
            add("jquery.min.js");
            add("template-web.js");
        }};

        private void checkSchema(WebView view, String url) {
            if (url.startsWith("leadcom://")) {
                WelcomeActivity.open(Activity(), url);
            } else {
                view.loadUrl(url);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            checkSchema(view, url);
            return true;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (Build.VERSION.SDK_INT >= 21) {
                checkSchema(view, request.getUrl().toString());
            }
            return true;
        }

        private WebResourceResponse checkRequest(Uri uri) {
            String fileName = uri.getLastPathSegment();
            if (segments.contains(fileName)) {
                WebResourceResponse response;
                try {
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
                log(format("request request: %s", request.getUrl().toString()));
                WebResourceResponse response = checkRequest(request.getUrl());
                return null != response ? response : super.shouldInterceptRequest(view, request);
            } else {
                return super.shouldInterceptRequest(view, request);
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            archiveLoadingStart = System.currentTimeMillis();
            displayLoading(true);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            displayLoading(false);
            super.onPageFinished(view, url);
            log(format("loading archive content used: %dms", System.currentTimeMillis() - archiveLoadingStart));
            // 活动时显示报名按钮
            if (mArchive.isActivity()) {
                displayActivityControl(true);
                // 拉取当前用户是否已报名
                checkActivityReported();
            }
            setJsEvents(view);
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
    }

    /**
     * 查询当前用户在活动中的报名情况
     */
    private void checkActivityReported() {
        MemberRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Member>() {
            @Override
            public void onResponse(Member member, boolean success, String message) {
                super.onResponse(member, success, message);
                if (success) {
                    if (mArchive.isStopped()) {
                        limitedText.setVisibility(View.VISIBLE);
                    } else {
                        signButton.setVisibility(null == member ? View.GONE : View.VISIBLE);
                        leaveButton.setVisibility(null == member ? View.GONE : View.VISIBLE);
                    }
                    // 是否可以查看统计
                    reportButton.setVisibility(Role.hasOperation(mArchive.getGroupId(), GRPOperation.ACTIVITY_REPORT_COLLECT) || Cache.cache().isMe() ? View.VISIBLE : View.GONE);
                    if (null == member) {
                        // 返回的member为空则说明不能报名，此时如果当前用户是组织管理员的话，提醒其下发活动
                        //reportButton.setVisibility(View.VISIBLE);
                        // 当前用户是组织管理员时，且有下发活动的权限时显示下发按钮
                        deliverButton.setVisibility(Role.hasOperation(mArchive.getGroupId(), GRPOperation.ACTIVITY_DELIVER) && !mArchive.isStopped() ? View.VISIBLE : View.GONE);
                    } else {
                        refreshReportButtons(member);
                    }
                }
            }
        }).findActivityStatus(mArchive.getGroupId(), mArchive.getGroActivityId());
    }

    private void refreshReportButtons(Member member) {
        deliverButton.setVisibility(View.GONE);
        activityStatus = Integer.valueOf(member.getStatus());
        // 未报名或请假状态下可以继续报名（请假后也可以报名）
        signButton.setEnabled(activityStatus >= Member.ActivityStatus.LEAVE);
        signButton.setText(activityStatus >= Member.ActivityStatus.LEAVE ? R.string.ui_group_activity_details_sign_up : R.string.ui_group_activity_details_signed_up);
        // 未报名或已报名状态下可以请假
        leaveButton.setEnabled(activityStatus != Member.ActivityStatus.LEAVE);
        leaveButton.setText(activityStatus != Member.ActivityStatus.LEAVE ? R.string.ui_group_activity_details_leave : R.string.ui_group_activity_details_leaved);
    }

    private String local, extension, name, url;

    private void startingDownload(String url) {
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
        extension = name.substring(name.lastIndexOf('.') + 1);
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

    private class DetailsDownloadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            if (!NetworkUtil.isNetAvailable(App.app())) {
                ToastHelper.make().showMsg(R.string.ui_base_text_network_invalid);
                return;
            }
            startingDownload(url);
        }
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (!isChooseGroup && requestCode == REQUEST_SELECT) {
            // 选择了下发的成员
            String result = getResultedData(data);
            if (isEmpty(result)) {
                ToastHelper.make().showMsg(R.string.ui_group_activity_details_transform_dialog_member_select_empty);
            } else {
                final ArrayList<SubMember> members = SubMember.fromJson(result);
                if (null == members || members.size() < 1) {
                    ToastHelper.make().showMsg(R.string.ui_group_activity_details_transform_dialog_member_select_empty);
                } else {
                    String title = StringHelper.getString(R.string.ui_group_activity_details_transform_member_selected, SubMember.getMemberNames(members), members.size());
                    DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
                        @Override
                        public boolean onConfirm() {
                            tryTransferActivity(members);
                            return true;
                        }
                    }).setTitleText(title).setConfirmText(R.string.ui_base_text_confirm).setCancelText(R.string.ui_base_text_cancel).show();
                }
            }
            //finish();
            // 需要跳转到会话页面并且关闭档案详情页
            //String teamId = getResultedData(data);
            //NimUIKit.startTeamSession(Activity(), teamId);
        } else if (!isChooseGroup && requestCode == REQUEST_GROUP) {
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
        if (null != _helper) {
            _helper.handleActivityResult(requestCode, data);
        }
        isChooseGroup = false;
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
    private TextView pushTitleText, nothingText;
    private RecyclerView concerned;
    private View nothingView;
    private ConcernAdapter cAdapter;

    private void openPushDialog() {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == pushDialog) {
                    pushDialog = View.inflate(Activity(), R.layout.popup_dialog_archive_push, null);
                    pushTitleText = pushDialog.findViewById(R.id.ui_dialog_archive_push_title);
                    nothingView = pushDialog.findViewById(R.id.ui_tool_nothing_container);
                    nothingText = pushDialog.findViewById(R.id.ui_tool_nothing_text);
                    concerned = pushDialog.findViewById(R.id.ui_tool_swipe_refreshable_recycler_view);
                    concerned.setLayoutManager(new CustomLinearLayoutManager(concerned.getContext()));
                    cAdapter = new ConcernAdapter();
                    concerned.setAdapter(cAdapter);
                }
                return pushDialog;
            }

            private void showConcernedGroups() {
                // 查询关注我的组织列表并推送
                nothingText.setText(R.string.ui_text_archive_details_push_dialog_nothing);
                ConcernRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Concern>() {
                    @Override
                    public void onResponse(List<Concern> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                        super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                        if (success && null != list) {
                            for (Concern concern : list) {
                                concern.setId(concern.getGroupId());
                                cAdapter.add(concern);
                            }
                        }
                        nothingView.setVisibility(null == list || list.size() <= 0 ? View.VISIBLE : View.GONE);
                    }
                }).listTransfer(mArchive.getGroupId());
            }

            private void showSelfDefined() {
                nothingText.setText(R.string.ui_text_archive_details_classify_nothing);
                ClassifyRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Classify>() {
                    @Override
                    public void onResponse(List<Classify> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                        super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                        if (success && null != list) {
                            for (Classify classify : list) {
                                cAdapter.add(classify);
                            }
                        }
                        nothingView.setVisibility(null == list || list.size() <= 0 ? View.VISIBLE : View.GONE);
                    }
                }).list(mArchive.getGroupId());
            }

            // 显示组织的支部和成员列表
            private void showSquadsMember() {
                nothingText.setText(R.string.ui_group_activity_details_transform_dialog_no_squad);
                if (squads.size() > 0) {
                    for (Squad squad : squads) {
                        cAdapter.add(squad);
                    }
                    return;
                }
                SquadRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Squad>() {
                    @Override
                    public void onResponse(List<Squad> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                        super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                        if (success && null != list) {
                            for (Squad squad : list) {
                                // 设置可选
                                squad.setSelectable(true);
                                squad.setLocalDeleted(true);
                                cAdapter.add(squad);
                            }
                            squads.addAll(list);
                        }
                        nothingView.setVisibility(null == list || list.size() <= 0 ? View.VISIBLE : View.GONE);
                    }
                }).list(mArchive.getGroupId(), 1);
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {
                cAdapter.clear();
                switch (pushingType) {
                    case PUSH_GROUPS:
                        pushTitleText.setText(R.string.ui_text_archive_details_push_dialog_title);
                        showConcernedGroups();
                        break;
                    case PUSH_CLASSIFY:
                        pushTitleText.setText(R.string.ui_text_archive_details_push_dialog_title_classify);
                        showSelfDefined();
                        break;
                    case PUSH_TRANSFORM:
                        pushTitleText.setText(R.string.ui_group_activity_details_transform_dialog_title);
                        showSquadsMember();
                        break;
                }
            }
        }).addOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
//                ArrayList<String> groupIds = new ArrayList<>();
//                Iterator<Concern> iterator = cAdapter.iterator();
//                while (iterator.hasNext()) {
//                    Concern concern = iterator.next();
//                    if (concern.isSelected()) {
//                        groupIds.add(concern.getId());
//                    }
//                }
//                tryPushArchive(groupIds);
                switch (pushingType) {
                    case PUSH_GROUPS:
                        preparePushGroups();
                        break;
                    case PUSH_CLASSIFY:
                        prepareClassify();
                        break;
                    case PUSH_TRANSFORM:
                        prepareMembers();
                        break;
                }
                return true;
            }

            private void preparePushGroups() {
                ArrayList<ArchivePushTarget> targets = new ArrayList<>();
                Iterator<Model> iterator = cAdapter.iterator();
                while (iterator.hasNext()) {
                    Model model = iterator.next();
                    if (model.isSelected()) {
                        if (model instanceof Concern) {
                            Concern concern = (Concern) model;
                            ArchivePushTarget target = new ArchivePushTarget();
                            target.setTargertGroupId(concern.getGroupId());
                            if (concern.getDocClassifyList().size() > 0) {
                                for (Classify classify : concern.getDocClassifyList()) {
                                    if (classify.isSelected()) {
                                        if (!classify.getId().contains("classify")) {
                                            target.setDocClassifyId(classify.getId());
                                        }
                                    }
                                }
                            }
                            targets.add(target);
                        }
                    }
                }
                pushArchive(targets);
            }

            private void prepareClassify() {
                Iterator<Model> iterator = cAdapter.iterator();
                String classifyId = "", classifyName = "";
                while (iterator.hasNext()) {
                    Classify classify = (Classify) iterator.next();
                    if (classify.isSelected()) {
                        classifyId = classify.getId();
                        classifyName = classify.getName();
                        break;
                    }
                }
                if (!isEmpty(classifyId)) {
                    classifyArchive(classifyId, classifyName);
                }
            }

            private void prepareMembers() {
                ArrayList<SubMember> members = new ArrayList<>();
                Iterator<Model> iterator = cAdapter.iterator();
                while (iterator.hasNext()) {
                    Model model = iterator.next();
                    if (model instanceof Squad) {
                        Squad squad = (Squad) model;
                        for (Member member : squad.getGroSquMemberList()) {
                            if (member.isSelected()) {
                                SubMember sub = new SubMember(member);
                                members.add(sub);
                            }
                        }
                    }
                }
                if (members.size() > 0) {
                    tryTransferActivity(members);
                } else {
                    ToastHelper.make().showMsg(R.string.ui_group_activity_details_transform_dialog_member_select_empty);
                }
            }
        }).setConfirmText(pushingType == PUSH_GROUPS ? R.string.ui_base_text_forward : (pushingType == PUSH_CLASSIFY ? R.string.ui_base_text_classify : R.string.ui_base_text_confirm)).setPopupType(DialogHelper.SLID_IN_RIGHT).show();
    }

    private void tryTransferActivity(ArrayList<SubMember> members) {
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    ToastHelper.make().showMsg(R.string.ui_group_activity_details_transform_success);
                    checkActivityReported();
                }
            }
        }).transferActivity(mArchive.getGroupId(), mArchive.getFromGroupId(), mArchive.getGroActivityId(), members);
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

    private void pushArchive(ArrayList<ArchivePushTarget> targets) {
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    ToastHelper.make().showMsg(message);
                }
            }
        }).push(mQueryId, targets);
    }

    private void classifyArchive(String classifyId, final String classifyName) {
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    ToastHelper.make().showMsg(getString(R.string.ui_text_archive_details_classify_success, classifyName));
                }
            }
        }).classify(mQueryId, classifyId);
    }

    @Override
    protected void onLikeComplete(boolean success, Model model) {

    }

    @Override
    protected void onCollectComplete(boolean success, Model model) {

    }

    private OnViewHolderClickListener clickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Model model = cAdapter.get(index);
            if (model instanceof Concern) {
                Concern concern = (Concern) model;
                concern.setSelectable(!concern.isSelectable());
                int cnt = 0;
                boolean selected = concern.isSelectable();
                for (Classify classify : concern.getDocClassifyList()) {
                    if (selected) {
                        cnt++;
                        if (isEmpty(classify.getId())) {
                            classify.setId(concern.getId() + "classify");
                            //classify.setSelected(true);
                        }
                        cAdapter.add(classify, cnt + index);
                    } else {
                        cAdapter.remove(classify);
                    }
                }
            } else if (model instanceof Classify) {
                Classify classify = (Classify) model;
                classify.setSelected(!classify.isSelected());
                cAdapter.update(classify);
                Model upper = cAdapter.get(classify.getGroupId());
                if (null != upper) {
                    Concern concern = (Concern) upper;
                    boolean hasSelected = false;
                    for (Classify clazz : concern.getDocClassifyList()) {
                        if (clazz.isSelected()) {
                            hasSelected = true;
                        }
                    }
                    concern.setSelected(hasSelected);
                    cAdapter.update(concern);
                }
                Iterator<Model> iterator = cAdapter.iterator();
                while (iterator.hasNext()) {
                    Model m = iterator.next();
                    if (m instanceof Classify) {
                        Classify clazz = (Classify) m;
                        if (clazz.getGroupId().equals(classify.getGroupId())) {
                            // 同一个组织里的
                            if (!clazz.getId().equals(classify.getId()) && clazz.isSelected()) {
                                clazz.setSelected(false);
                                cAdapter.update(clazz);
                            }
                        }
                    }
                }
            } else if (model instanceof Squad) {
                // 显示或隐藏小组的成员列表
                if (hasSquadMembers(model.getId())) {
                    model.setRead(false);
                    clearSquadMember((Squad) model);
                } else {
                    model.setRead(true);
                    appendSquadMembers((Squad) model);
                }
                cAdapter.update(model);
            } else if (model instanceof Member) {
                model.setSelected(!model.isSelected());
                cAdapter.update(model);
                checkSquadMemberAllSelected(((Member) model).getSquadId());
            }
        }

        private void checkSquadMemberAllSelected(String squadId) {
            Squad squad = (Squad) cAdapter.get(squadId);
            int count = 0;
            for (Member member : squad.getGroSquMemberList()) {
                count += member.isSelected() ? 1 : 0;
            }
            squad.setSelected(count == squad.getGroSquMemberList().size());
            cAdapter.update(squad);
        }

        private void clearSquadMember(Squad squad) {
            for (Member member : squad.getGroSquMemberList()) {
                cAdapter.remove(member);
            }
        }

        private void appendSquadMembers(Squad squad) {
            int index = cAdapter.indexOf(squad);
            int cnt = 0;
            for (Member member : squad.getGroSquMemberList()) {
                cnt++;
                cAdapter.add(member, index + cnt);
            }
        }

        private boolean hasSquadMembers(String squadId) {
            Iterator<Model> iterator = cAdapter.iterator();
            while (iterator.hasNext()) {
                Model model = iterator.next();
                if (model instanceof Member) {
                    Member member = (Member) model;
                    if (member.getSquadId().equals(squadId)) {
                        return true;
                    }
                }
            }
            return false;
        }
    };

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            switch (view.getId()) {
                case R.id.ui_holder_view_group_interest_root:
                    clickListener.onClick(index);
                    break;
                case R.id.ui_holder_view_group_interest_select:
                    // 小组的全选和取消全选
                    Squad squad = (Squad) cAdapter.get(index);
                    squad.setSelected(!squad.isSelected());
                    for (Member member : squad.getGroSquMemberList()) {
                        member.setSelected(squad.isSelected());
                        if (cAdapter.exist(member)) {
                            cAdapter.update(member);
                        }
                    }
                    cAdapter.update(squad);
                    break;
            }
        }
    };

    private class ConcernAdapter extends RecyclerViewAdapter<GroupInterestViewHolder, Model> {

        @Override
        public GroupInterestViewHolder onCreateViewHolder(View itemView, int viewType) {
            GroupInterestViewHolder holder = new GroupInterestViewHolder(itemView, ArchiveDetailsFragment.this);
            holder.setSelectable(true);
            holder.setOnViewHolderElementClickListener(elementClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_group_interesting_item;
        }

        @Override
        public void onBindHolderOfView(GroupInterestViewHolder holder, int position, @Nullable Model item) {
            if (item instanceof Concern) {
                holder.showContent((Concern) item);
            } else if (item instanceof Classify) {
                holder.showContent((Classify) item);
            } else if (item instanceof Squad) {
                holder.showContent((Squad) item);
            } else if (item instanceof Member) {
                holder.showContent((Member) item);
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
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

    ArchiveDetailsHelper _helper;
    private boolean isChooseGroup = false;

    @Override
    protected void archiveSetting() {
        if (null == _helper) {
            _helper = ArchiveDetailsHelper.helper().init(this).setArchive(mArchive).setOnElementClickListener(new ArchiveDetailsHelper.OnElementClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.ui_popup_rich_editor_setting_type_user:
                            break;
                        case R.id.ui_popup_rich_editor_setting_type_group:

                            break;
                        case R.id.ui_popup_rich_editor_setting_group_picker:
                            // 所属组织
                            isChooseGroup = true;
                            GroupPickerFragment.open(ArchiveDetailsFragment.this, mArchive.getGroupId(), false);
                            break;
                        case R.id.ui_popup_rich_editor_setting_branch_picker:
                            // 所属支部
                            if (isEmpty(mArchive.getGroupId())) {
                                ToastHelper.make().showMsg(R.string.ui_text_archive_details_editor_setting_group_empty);
                            } else {
                                //isOpenOther = true;
                                SquadPickerFragment.open(ArchiveDetailsFragment.this, mArchive.getGroupId(), mArchive.getBranch());
                            }
                            break;
                        case R.id.ui_popup_rich_editor_setting_time:
                            // 发生时间
                            openDateTimePicker();
                            break;
                        case R.id.ui_popup_rich_editor_setting_property:
                            // 档案性质
                            if (isEmpty(mArchive.getGroupId())) {
                                ToastHelper.make().showMsg(R.string.ui_text_archive_details_editor_setting_group_empty);
                            } else {
                                //isOpenOther = true;
                                LabelPickFragment.open(ArchiveDetailsFragment.this, LabelPickFragment.TYPE_PROPERTY, mArchive.getGroupId(), mArchive.getDocClassifyId());
                            }
                            break;
                        case R.id.ui_popup_rich_editor_setting_category:
                            // 档案类型
                            //isOpenOther = true;
                            LabelPickFragment.open(ArchiveDetailsFragment.this, LabelPickFragment.TYPE_CATEGORY, mArchive.getGroupId(), mArchive.getCategory());
                            break;
                        case R.id.ui_popup_rich_editor_setting_participant:
                            // 参与者
                            SquadsFragment.isOpenable = true;
                            SquadsFragment.open(ArchiveDetailsFragment.this, mArchive.getGroupId(), "", true, null);
                            //GroupSquadContactPickerFragment.open(ArchiveDetailsFragment.this, mArchive.getGroupId(), "", "[]");
                            //GroupContactPickFragment.open(ArchiveDetailsFragment.this, REQUEST_SELECT, "", true, false, "[]");
                            break;
                        case R.id.ui_popup_rich_editor_setting_public:
                            break;
                        case R.id.ui_popup_rich_editor_setting_public_public:
                            // 设为公开
                            mArchive.setAuthPublic(Seclusion.Type.Public);
                            resetPublicStatus();
                            break;
                        case R.id.ui_popup_rich_editor_setting_public_private:
                            // 设为私密
                            mArchive.setAuthPublic(isEmpty(mArchive.getGroupId()) ? Seclusion.Type.Private : Seclusion.Type.Group);
                            resetPublicStatus();
                            break;
                        case R.id.ui_popup_rich_editor_setting_label:
                            // 档案标签
                            LabelPickFragment.open(ArchiveDetailsFragment.this, LabelPickFragment.TYPE_LABEL, mArchive.getGroupId(), mArchive.getLabel());
                            break;
                        case R.id.ui_popup_rich_editor_setting_share:
                        case R.id.ui_popup_rich_editor_setting_share_draft:
                            break;
                        case R.id.ui_popup_rich_editor_setting_commit:
                            break;
                    }
                }

                private void resetPublicStatus() {
                    _helper.setArchive(mArchive).resetPublicStatus();
                }
            }).setShowCommit(false);
        }
        _helper.show();
    }

    private void openDateTimePicker() {
        // 发生时间
        DateTimeHelper.helper().setOnDateTimePickListener(new DateTimeHelper.OnDateTimePickListener() {
            @Override
            public void onPicked(Date date) {
                if (mArchive.isActivity()) {
                    Calendar calendar = Calendar.getInstance();
                    if (date.getTime() < calendar.getTime().getTime()) {
                        ToastHelper.make().showMsg(R.string.ui_group_activity_editor_time_limit_less_than_now);
                        return;
                    }
                    calendar.add(Calendar.HOUR, 24);
                    if (date.getTime() < calendar.getTime().getTime()) {
                        ToastHelper.make().showMsg(R.string.ui_group_activity_editor_time_limit_less_than_24h_after_now);
                        return;
                    }
                }
                String fullTime = Utils.format(StringHelper.getString(R.string.ui_base_text_date_time_format), date);
                mArchive.setHappenDate(fullTime);
                String time = mArchive.isActivity() ? formatDateTime(fullTime) : formatDate(fullTime);
                _helper.setArchive(mArchive).showHappenDate(time);
            }
        }).show(ArchiveDetailsFragment.this, true, true, true, mArchive.isActivity(), mArchive.isActivity(), false, !mArchive.isTemplateArchive(), mArchive.getHappenDate());
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
        pushingType = PUSH_GROUPS;
        openPushDialog();
    }

    @Override
    protected void shareToClassify() {
        pushingType = PUSH_CLASSIFY;
        openPushDialog();
    }

    @Override
    protected void shareToReply() {
        ArchiveReplyFragment.open(this, mQueryId, mArchive.getTitle(), mArchive.getGroupName(), mArchive.getCreateDate(), mArchive.getContent());
    }

    @Override
    protected void transform() {
        pushingType = PUSH_TRANSFORM;
        GroupAllPickerFragment.IS_FOR_DELIVER = true;
        GroupAllPickerFragment.open(this, mArchive.getGroupId(), mArchive.getGroupName(), null, null);
        //openPushDialog();
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
