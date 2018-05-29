package com.leadcom.android.isp.fragment.archive;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.layoutmanager.CustomLinearLayoutManager;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredButton;
import com.hlk.hlklib.lib.view.CorneredEditText;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.hlk.hlklib.lib.view.ToggleButton;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.activity.MainActivity;
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
import com.leadcom.android.isp.etc.SysInfoUtil;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseCmtLikeColFragment;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.helper.popup.DeleteDialogHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.helper.publishable.Collectable;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.archive.ArchiveAttachmentViewHolder;
import com.leadcom.android.isp.holder.archive.ArchiveDetailsAdditionalViewHolder;
import com.leadcom.android.isp.holder.archive.ArchiveDetailsCommentViewHolder;
import com.leadcom.android.isp.holder.archive.ArchiveDetailsViewHolder;
import com.leadcom.android.isp.holder.common.NothingMoreViewHolder;
import com.leadcom.android.isp.holder.organization.GroupInterestViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.listener.OnKeyboardChangeListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.Additional;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.archive.ArchiveInfo;
import com.leadcom.android.isp.model.archive.ArchiveQuery;
import com.leadcom.android.isp.model.archive.Comment;
import com.leadcom.android.isp.model.common.ArchivePermission;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.common.Seclusion;
import com.leadcom.android.isp.model.common.ShareInfo;
import com.leadcom.android.isp.model.operation.GRPOperation;
import com.leadcom.android.isp.model.organization.Concern;
import com.leadcom.android.isp.model.organization.RelateGroup;
import com.leadcom.android.isp.model.organization.Role;
import com.leadcom.android.isp.model.user.Collection;
import com.leadcom.android.isp.nim.file.FilePreviewHelper;
import com.netease.nim.uikit.api.NimUIKit;

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

public class ArchiveDetailsWebViewFragment extends BaseCmtLikeColFragment {

    private static final String PARAM_DOC_TYPE = "adwvf_archive_type";
    private static final String PARAM_CMT_INDEX = "adwvf_archive_cmt_index";
    private static final String PARAM_ARCHIVE = "adwvf_archive";
    private static final String PARAM_DRAFT = "adwvf_draft";
    private static final String PARAM_INNER_OPEN = "adwvf_inner_open";
    private static final String PARAM_GROUP_ID = "adwvf_group_id";
    private static final String PARAM_AUTHOR_ID = "adwvf_author_id";
    private static final String PARAM_COVER_URL = "adwvf_cover_url";
    private static boolean deletable = false;
    private static boolean isCollected = false;

    public static ArchiveDetailsWebViewFragment newInstance(Bundle bundle) {
        ArchiveDetailsWebViewFragment adwvf = new ArchiveDetailsWebViewFragment();
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
        //int type = isEmpty(archive.getGroupId()) ? Archive.Type.USER : Archive.Type.GROUP;
        //Bundle bundle = getBundle(archive.getId(), type, true);
        //bundle.putSerializable(PARAM_ARCHIVE, archive);
        //fragment.openActivity(ArchiveDetailsWebViewFragment.class.getName(), bundle, REQUEST_DELETE, true, false);
    }

    // 打开详情页并指定一个档案，收藏时用
    public static void open(BaseFragment fragment, Archive archive, boolean isDraft) {
        open(fragment, archive.getGroupId(), archive.getCover(), (isEmpty(archive.getGroupId()) ? Archive.Type.USER : Archive.Type.GROUP),
                (!isEmpty(archive.getDocId()) ? archive.getDocId() : archive.getId()), isDraft, archive.getUserId());
    }

//    public static void open(BaseFragment fragment, String archiveId, int archiveType) {
//        fragment.openActivity(ArchiveDetailsWebViewFragment.class.getName(),
//                getBundle(archiveId, archiveType, true), REQUEST_DELETE, true, false);
//    }

//    public static void openDraft(BaseFragment fragment, String archiveId, int archiveType) {
//        Bundle bundle = getBundle(archiveId, archiveType, true);
//        bundle.putBoolean(PARAM_DRAFT, true);
//        fragment.openActivity(ArchiveDetailsWebViewFragment.class.getName(), bundle, REQUEST_DELETE, true, false);
//    }

//    public static void open(Context context, String archiveId, int archiveType, boolean innerOpen) {
//        BaseActivity.openActivity(context, ArchiveDetailsWebViewFragment.class.getName(),
//                getBundle(archiveId, archiveType, innerOpen), REQUEST_DELETE, true, false);
//    }

//    public static void openDraft(Context context, String archiveId, int archiveType, boolean innerOpen) {
//        Bundle bundle = getBundle(archiveId, archiveType, innerOpen);
//        bundle.putBoolean(PARAM_DRAFT, true);
//        BaseActivity.openActivity(context, ArchiveDetailsWebViewFragment.class.getName(), bundle, REQUEST_DELETE, true, false);
//    }

    public static void open(Context context, String groupId, String cover, String archiveId, int archiveType, boolean isDraft, boolean innerOpen, String authorId) {
        //InnerWebViewFragment.open(context, title, getUrl(archiveId, archiveType, isDraft));
        BaseActivity.openActivity(context, ArchiveDetailsWebViewFragment.class.getName(),
                getBundle(archiveId, groupId, cover, archiveType, innerOpen, isDraft, authorId), false, false);
    }

    public static void open(BaseFragment fragment, String groupId, String cover, int archiveType, String archiveId, boolean isDraft, String authorId) {
        fragment.openActivity(ArchiveDetailsWebViewFragment.class.getName(),
                getBundle(archiveId, groupId, cover, archiveType, true, isDraft, authorId), false, false);
        //InnerWebViewFragment.open(fragment, title, getUrl(archiveId, archiveType, isDraft));
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        archiveType = bundle.getInt(PARAM_DOC_TYPE, Archive.Type.GROUP);
        isDraft = bundle.getBoolean(PARAM_DRAFT, false);
        selectedIndex = bundle.getInt(PARAM_CMT_INDEX, 0);
        mArchive = (Archive) bundle.getSerializable(PARAM_ARCHIVE);
        innerOpen = bundle.getBoolean(PARAM_INNER_OPEN, false);
        groupId = bundle.getString(PARAM_GROUP_ID, "");
        authorId = bundle.getString(PARAM_AUTHOR_ID, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_DOC_TYPE, archiveType);
        bundle.putInt(PARAM_CMT_INDEX, selectedIndex);
        bundle.putSerializable(PARAM_ARCHIVE, mArchive);
        bundle.putBoolean(PARAM_DRAFT, isDraft);
        bundle.putBoolean(PARAM_INNER_OPEN, innerOpen);
        bundle.putString(PARAM_GROUP_ID, groupId);
        bundle.putString(PARAM_AUTHOR_ID, authorId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        INTERNAL_SHAREABLE = false;
        nothingMore = Model.getNoMore();
        inputContent.addTextChangedListener(inputTextWatcher);
        mOnKeyboardChangeListener = new OnKeyboardChangeListener(Activity());
        mOnKeyboardChangeListener.setKeyboardListener(keyboardListener);

        // WebView 显示档案详情时的UI处理
        additionalLayout.setVisibility(View.GONE);
        enableSwipe(false);
        isLoadingComplete(true);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_archive_details_new;
    }

    @Override
    public void onDestroy() {
        if (null != detailsViewHolder) {
            // 停止播放视频或声音
            detailsViewHolder.onFragmentDestroy();
        }
        if (null != mOnKeyboardChangeListener) {
            mOnKeyboardChangeListener.destroy();
        }
        if (!SysInfoUtil.stackResumed(Activity())) {
            if (!innerOpen) {
                // 如果不是堆栈恢复的app则打开主页面，否则直接关闭即可
                MainActivity.start(Activity());
            }
        }
        isCollected = false;
        super.onDestroy();
    }

    @ViewId(R.id.ui_main_tool_bar_background)
    private View titleBackground;
    @ViewId(R.id.ui_ui_custom_title_right_icon)
    private CustomTextView rightIcon;
    @ViewId(R.id.ui_ui_custom_title_right_text)
    private TextView rightText;
    @ViewId(R.id.ui_main_archive_details_title_text)
    private TextView titleText;

    @ViewId(R.id.ui_tool_view_archive_additional_comment_number)
    private TextView commentNumber;
    @ViewId(R.id.ui_tool_view_archive_additional_like_layout)
    private View likeLayout;
    @ViewId(R.id.ui_tool_view_archive_additional_like_icon)
    private CustomTextView likeIcon;
    @ViewId(R.id.ui_tool_view_archive_additional_like_number)
    private TextView likeNumber;
    @ViewId(R.id.ui_tool_view_archive_additional_collection_layout)
    private View collectLayout;
    @ViewId(R.id.ui_tool_view_archive_additional_collection_icon)
    private CustomTextView collectIcon;
    @ViewId(R.id.ui_tool_view_archive_additional_collection_number)
    private TextView collectNumber;

    @ViewId(R.id.ui_tool_view_simple_inputable_layout)
    private View inputLayout;
    @ViewId(R.id.ui_tool_view_simple_inputable_reply)
    private TextView replyView;
    @ViewId(R.id.ui_tool_view_simple_inputable_text)
    private CorneredEditText inputContent;
    @ViewId(R.id.ui_tool_view_simple_inputable_send)
    private CorneredButton inputSend;
    @ViewId(R.id.ui_tool_view_archive_additional_layout)
    private View additionalLayout;

    private Model nothingMore;
    private int archiveType, selectedIndex;
    private boolean isDraft;
    private String groupId, authorId;
    /**
     * 标记是否是app内部打开的详情页
     */
    private boolean innerOpen;
    private DetailsAdapter mAdapter;
    private Archive mArchive;
    private ArchiveDetailsViewHolder detailsViewHolder;
    private OnKeyboardChangeListener mOnKeyboardChangeListener;
    private Role myRole;

    @Override
    protected void onDelayRefreshComplete(int type) {

    }

    @Override
    public void doingInResume() {
        initializeAdapter();
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
        remotePageNumber = 1;
        setSupportLoadingMore(true);
        //loadingArchive();
    }

    @Override
    protected void onLoadingMore() {
        //mAdapter.remove(nothingMore);
        //loadingComments(mAdapter.get(mQueryId));
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    private TextWatcher inputTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            int size = null == s ? 0 : s.length();
            inputSend.setVisibility(size > 0 ? View.VISIBLE : View.GONE);
        }
    };

    @Click({R.id.ui_tool_view_archive_additional_comment_layout,
            R.id.ui_tool_view_archive_additional_like_layout,
            R.id.ui_tool_view_archive_additional_collection_layout,
            R.id.ui_tool_view_simple_inputable_send,
            R.id.ui_ui_custom_title_left_container,
            R.id.ui_ui_custom_title_right_container})
    private void elementClick(View view) {
        switch (view.getId()) {
            case R.id.ui_tool_view_simple_inputable_send:
                // 发送评论
                trySendComment();
                break;
            case R.id.ui_tool_view_archive_additional_comment_layout:
                // 显示评论输入框并隐藏附加信息，默认打开并滚动到最后
                restoreInputStatus();
                showInputBoard(true);
                break;
            case R.id.ui_tool_view_archive_additional_like_layout:
                like(mAdapter.get(mQueryId));
                break;
            case R.id.ui_tool_view_archive_additional_collection_layout:
                collect(mAdapter.get(mQueryId));
                break;
            case R.id.ui_ui_custom_title_left_container:
                finish();
                break;
            case R.id.ui_ui_custom_title_right_container:
                rightIconClick(view);
                break;
        }
    }

    @Override
    protected boolean onBackKeyPressed() {
        boolean parent = super.onBackKeyPressed();
        if (!parent) {
            if (inputLayout.getVisibility() == View.VISIBLE) {
                showInputBoard(false);
                //return true;
            }
        }
        resetResultData();
        return parent;
    }

    private void resetResultData() {
        Archive archive = (Archive) mAdapter.get(mQueryId);
        if (null != archive) {
            Intent intent = new Intent();
            intent.putExtra(RESULT_ARCHIVE, archive);
            intent.putExtra(RESULT_STRING, archive.getId());
            Activity().setResult(Activity.RESULT_OK, intent);
        }
    }

    private OnKeyboardChangeListener.KeyboardListener keyboardListener = new OnKeyboardChangeListener.KeyboardListener() {
        @Override
        public void onKeyboardChange(boolean isShow, int keyboardHeight) {
            log(format("keyboard changed, show: %s, keyboard height: %d", isShow, keyboardHeight));
            if (!isShow) {
                // 键盘隐藏时，设置评论状态为普通评论
                restoreInputStatus();
            }
        }
    };

    // 评论发表完毕之后重置评论发布状态
    private void restoreInputStatus() {
        replyView.setVisibility(View.GONE);
        inputContent.setHint(R.string.ui_text_archive_details_comment_hint);
    }

    private void trySendComment() {
        String content = inputContent.getValue();
        if (!isEmpty(content)) {
            Model model = mAdapter.get(selectedIndex);
            if (model instanceof Comment) {
                Comment comment = (Comment) model;
                comment(mAdapter.get(mQueryId), content, comment.isMine() ? "" : comment.getUserId());
            } else {
                // 直接评论
                comment(mAdapter.get(mQueryId), content, "");
            }
        }
    }

    private void deleteDocument() {
        setLoadingText(R.string.ui_text_document_details_deleting_document);
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
        rightIcon.setText(null);
        rightText.setText(R.string.ui_base_text_edit);
//        setRightText(R.string.ui_base_text_edit);
//        setRightTitleClickListener(new OnTitleButtonClickListener() {
//            @Override
//            public void onClick() {
//                //openEditSelector();
//                Archive archive = (Archive) mAdapter.get(mQueryId);
//                String type = archive.isAttachmentArchive() ? ArchiveEditorFragment.ATTACHABLE : ArchiveEditorFragment.MULTIMEDIA;
//                ArchiveEditorFragment.open(ArchiveDetailsWebViewFragment.this, mQueryId, type);
//                finish();
//            }
//        });
    }

    private void rightIconClick(View view) {
        view.startAnimation(App.clickAnimation());
        if (isDraft) {
            Archive archive = (Archive) mAdapter.get(mQueryId);
            String type = archive.isAttachmentArchive() ? ArchiveEditorFragment.ATTACHABLE :
                    (archive.isMultimediaArchive() ? ArchiveEditorFragment.MULTIMEDIA : ArchiveEditorFragment.TEMPLATE);
            ArchiveEditorFragment.open(ArchiveDetailsWebViewFragment.this, mQueryId, type);
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
                    // 档案创建者可以删除评论
                    deletable = enableShareDelete;
                    fetchingShareInfo();
                }
            }
        }).permission(mQueryId);
    }

    private void loadingArchiveInfo() {
        ArchiveQueryRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<ArchiveQuery>() {
            @Override
            public void onResponse(ArchiveQuery archiveQuery, boolean success, String message) {
                super.onResponse(archiveQuery, success, message);
                if (success) {
                    ArchiveInfo info = archiveQuery.getAdditionResult();
                    boolean isUser = null == archiveQuery.getGroDoc();
                    Archive archive = isDraft ? archiveQuery.getDocDraft() : isUser ? archiveQuery.getUserDoc() : archiveQuery.getGroDoc();
                    if (null == archive) {
                        ToastHelper.make().showMsg(R.string.ui_text_archive_details_invalid_archive);
                        finish();
                    } else {
                        archive.resetInfo(info);
                        mArchive.resetInfo(info);
                        //archive.resetAdditional(archive.getAddition());
                        //displayArchive(archive);
                        prepareShareDialogElement(archive);
                        fetchingShareInfo();
                    }
                }
            }
        }).find(isDraft ? Archive.Type.DRAFT : archiveType, mQueryId);
    }

    private void loadingArchive() {
        if (null != mArchive && !isEmpty(mArchive.getId())) {
            displayArchive(mArchive);
        } else {
            setLoadingText(R.string.ui_text_archive_details_loading);
            displayLoading(true);
            ArchiveQueryRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<ArchiveQuery>() {
                @Override
                public void onResponse(ArchiveQuery archiveQuery, boolean success, String message) {
                    super.onResponse(archiveQuery, success, message);
                    displayLoading(false);
                    if (success && null != archiveQuery) {
                        mAdapter.remove(nothingMore);
                        ArchiveInfo info = archiveQuery.getAdditionResult();
                        boolean isUser = null == archiveQuery.getGroDoc();
                        Archive archive = isDraft ? archiveQuery.getDocDraft() : isUser ? archiveQuery.getUserDoc() : archiveQuery.getGroDoc();
                        if (null == archive) {
                            ToastHelper.make().showMsg(R.string.ui_text_archive_details_invalid_archive);
                            finish();
                        } else {
                            archive.resetInfo(info);
                            archive.resetAdditional(archive.getAddition());
                            prepareShareDialogElement(archive);
                        }
                        onLoadingCommentComplete(true, isUser ? archiveQuery.getUserDocComment() : archiveQuery.getGroDocCmtList());
                    }
                    stopRefreshing();
                    isLoadingComplete(true);
                }

            }).find(isDraft ? Archive.Type.DRAFT : archiveType, mQueryId);
        }
    }

    /**
     * 当前角色是否具有某项权限
     */
    private boolean hasOperation(String operation) {
        return null != myRole && myRole.hasOperation(operation);
    }

    private void displayArchive(Archive archive) {
        likeLayout.setVisibility(isDraft ? View.GONE : View.VISIBLE);
        collectLayout.setVisibility(isDraft ? View.GONE : View.VISIBLE);
        setCustomTitle(archive.getTitle());

        if (isDraft) {
            // 草稿档案只能查看
            //additionalLayout.setVisibility(View.GONE);
            if (archive.isAuthor()) {
                resetRightIconEvent();
            }
        } else if (!isCollected) {
            // 不是收藏过来的内容
            // 非草稿档案，可以分享等等
            rightIcon.setText(R.string.ui_icon_more);
            //setRightIcon(R.string.ui_icon_more);
            //setRightTitleClickListener(new OnTitleButtonClickListener() {
            //    @Override
            //    public void onClick() {
            //        fetchingShareInfo();
            //    }
            //});
        }
        myRole = Cache.cache().getGroupRole(archive.getGroupId());
        // 设置收藏的参数为档案
        if (!isCollected) {
            Collectable.resetArchiveCollectionParams(archive);
        }
        //prepareShareDialogElement(archive);
        mAdapter.update(archive);
//        for (Attachment attachment : archive.getImage()) {
//            mAdapter.update(attachment);
//        }
//        for (Attachment attachment : archive.getVideo()) {
//            mAdapter.update(attachment);
//        }
//        for (Attachment attachment : archive.getOffice()) {
//            mAdapter.update(attachment);
//        }
//        for (Attachment attachment : archive.getAttach()) {
//            mAdapter.update(attachment);
//        }
        //if (!isDraft) {
        // 草稿也可以有评论和赞什么的
        //displayAdditional(archive);
        //loadingComments(archive);
        //}
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
        // 档案创建者可以删除评论
        deletable = enableShareDelete;
    }

    private int getAdditionalPosition(Archive archive) {
        return archive.getOffice().size() + archive.getImage().size() + archive.getAttach().size() + archive.getVideo().size();
    }

    private void displayAdditional(Archive archive) {
        if (null == archive) return;
        archive.getAddition().setId(format("additional_%s", archive.getId()));
        if (archive.getAddition().isVisible()) {
            int index = mAdapter.indexOf(archive.getAddition());
            if (index > 0) {
                mAdapter.update(archive.getAddition());
            } else {
                mAdapter.add(archive.getAddition(), 1 + getAdditionalPosition(archive));
            }
        } else {
            mAdapter.remove(archive.getAddition());
        }

        commentNumber.setText(String.valueOf(archive.getCmtNum()));
        likeNumber.setText(String.valueOf(archive.getLikeNum()));
        collectNumber.setText(String.valueOf(archive.getColNum()));

        boolean liked = archive.getLike() == Archive.LikeType.LIKED;
        likeIcon.setText(liked ? R.string.ui_icon_like_solid : R.string.ui_icon_like_hollow);
        likeIcon.setTextColor(getColor(liked ? R.color.colorCaution : R.color.textColorHint));

        liked = archive.getCollection() == Collection.CollectionType.COLLECTED;
        collectIcon.setText(liked ? R.string.ui_icon_pentagon_corner_solid : R.string.ui_icon_pentagon_corner_hollow);
        collectIcon.setTextColor(getColor(liked ? R.color.colorCaution : R.color.textColorHint));

        additionalLayout.setVisibility(isCollected ? View.GONE : View.VISIBLE);
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            //setCustomTitle(R.string.ui_text_archive_details_fragment_title);
            mAdapter = new DetailsAdapter();
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.addOnScrollListener(scrollListener);

            displayArchive();
            //loadingArchive();
        }
    }

    private int mDistance = 0;
    private static final int MAX_ALPHA = 255;
    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            mDistance += dy;
            float percentage = mDistance * 1.0f / MAX_ALPHA;
            titleBackground.setAlpha(percentage);
            titleText.setAlpha(percentage);
        }
    };

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == REQUEST_SELECT) {
            finish();
            // 需要跳转到会话页面并且关闭档案详情页
            String teamId = getResultedData(data);
            NimUIKit.startTeamSession(Activity(), teamId);
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
                Archive archive = (Archive) mAdapter.get(mQueryId);
                shareTitle.setText(archive.getTitle());
                shareSummary.setText(isEmpty(archive.getContent()) ? "" : Html.fromHtml(archive.getContent()));
                shareImage.setVisibility(isEmpty(archive.getCover()) ? View.GONE : View.VISIBLE);
                shareImage.displayImage(archive.getCover(), getDimension(R.dimen.ui_static_dp_50), false, false);
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
                ConcernRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Concern>() {
                    @Override
                    public void onResponse(List<Concern> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                        super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                        if (success && null != list) {
                            cAdapter.update(list);
                        }
                    }
                }).list(((Archive) mAdapter.get(mQueryId)).getGroupId(), 1, "");
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

    private class ConcernAdapter extends RecyclerViewAdapter<GroupInterestViewHolder, Concern> {

        @Override
        public GroupInterestViewHolder onCreateViewHolder(View itemView, int viewType) {
            GroupInterestViewHolder holder = new GroupInterestViewHolder(itemView, ArchiveDetailsWebViewFragment.this);
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
                        //mShareInfo.setTargetPath(ArchiveDetailsViewHolder.getUrl(mQueryId, archiveType, isDraft, true));
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
        Archive archive = (Archive) mAdapter.get(mQueryId);
        if (null != archive) {
            if (isEmpty(archive.getId()) || archive.getId().equals("null")) {
                ToastHelper.make().showMsg(R.string.ui_archive_recommend_archive_id_null);
            } else {
                // 没有推荐则推荐，有推荐则取消推荐
                if (!archive.isRecommend()) {
//                    if (!archive.isRecommendable()) {
//                        // 无图无视频
//                        ToastHelper.make().showMsg(R.string.ui_archive_recommend_archive_content_no_image_video);
//                    } else {
//                        // 有图或者视频，可以推荐
//                        if (Utils.hasImage(archive.getContent()) || Utils.hasVideo(archive.getContent())) {
//                            recommendArchive(archive);
//                        } else {
//                            long len = archive.getHtmlClearedLength();
//                            if (len < 70) {
//                                ToastHelper.make().showMsg(R.string.ui_archive_recommend_archive_content_too_short);
//                            } else {
                    // 推荐
                    recommendArchive(archive);
//                            }
//                        }
//                    }
                } else {
                    unRecommendArchive();
                }
            }
        } else {
            ToastHelper.make().showMsg(R.string.ui_archive_recommend_archive_null);
        }
    }

    // 推荐档案
    private void recommendArchive(Archive archive) {
        final int index = mAdapter.indexOf(archive);
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    Archive doc = (Archive) mAdapter.get(index);
                    doc.setRecommend(Archive.RecommendType.RECOMMENDED);
                    mAdapter.notifyItemChanged(index);
                    prepareShareDialogElement(doc);
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
                    Archive doc = (Archive) mAdapter.get(mQueryId);
                    doc.setRecommend(Archive.RecommendType.UN_RECOMMEND);
                    mAdapter.notifyItemChanged(0);
                    prepareShareDialogElement(doc);
                    ToastHelper.make().showMsg(R.string.ui_text_archive_details_recommended_ok);
                }
                displayLoading(false);
            }
        }).unRecommend(mQueryId);
    }

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            switch (view.getId()) {
                case R.id.ui_holder_view_archive_details_comment_header:
                    // 点击了评论里的头像
                    App.openUserInfo(ArchiveDetailsWebViewFragment.this, ((Comment) mAdapter.get(index)).getUserId());
                    break;
                case R.id.ui_holder_view_archive_details_comment_layout:
                    if (!isCollected) {
                        // 回复评论或自己
                        Comment comment = (Comment) mAdapter.get(index);
                        if (!comment.isMine()) {
                            selectedIndex = index;
                            // 要回复别人的评论
                            replyView.setText(StringHelper.getString(R.string.ui_text_archive_details_comment_hint_to, comment.getUserName()));
                            replyView.setVisibility(View.VISIBLE);
                            showInputBoard(true);
                        }
                    }
                    break;
                case R.id.ui_holder_view_archive_details_comment_delete:
                    selectedIndex = index;
                    openCommentDeleteDialog();
                    break;
                case R.id.ui_tool_view_archive_additional_like_layout:
                    // 点赞
                    if (!isCollected) {
                        like(mAdapter.get(mQueryId));
                    }
                    break;
                case R.id.ui_tool_view_archive_additional_collection_layout:
                    // 收藏
                    if (!isCollected) {
                        collect(mAdapter.get(mQueryId));
                    }
                    break;
                case R.id.ui_holder_view_archive_attachment_layout:
                    // 点击打开附件
                    Attachment attachment = (Attachment) mAdapter.get(index);
                    FilePreviewHelper.previewFile(Activity(), attachment.getUrl(), attachment.getName(), attachment.getExt());
                    break;
                case R.id.ui_holder_view_archive_details_public_toggle:
                    ToggleButton toggleButton = (ToggleButton) view;
                    updateArchivePublic(toggleButton.isToggleOn());
                    break;
            }
        }
    };

    private void updateArchivePublic(final boolean isPublic) {
        Archive archive = (Archive) mAdapter.get(mQueryId);
        final boolean isGroup = !isEmpty(archive.getGroupId());
        archive.setAuthPublic(isPublic ? Seclusion.Type.Public : (!isGroup ? Seclusion.Type.Private : Seclusion.Type.Group));
        //final int auth = archive.getAuthPublic();
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    Archive doc = (Archive) mAdapter.get(mQueryId);
                    //doc.setAuthPublic(auth);
                    if (!isPublic) {
                        // 如果是设为私密，则一同撤销组织档案的推荐状态
                        if (isGroup) {
                            doc.setRecommend(Archive.RecommendType.UN_RECOMMEND);
                        }
                    }
                    prepareShareDialogElement(doc);
                    mAdapter.notifyItemChanged(0);
                    ToastHelper.make().showMsg(isPublic ? (isGroup ? R.string.ui_text_archive_details_public_group : R.string.ui_text_archive_details_public_individual) : R.string.ui_text_archive_details_publicable);
                }
            }
        }).update((Archive) mAdapter.get(mQueryId), ArchiveRequest.TYPE_AUTH);
    }

    private void showInputBoard(boolean showInput) {
        additionalLayout.setVisibility(showInput ? View.GONE : View.VISIBLE);
        inputLayout.setVisibility(showInput ? View.VISIBLE : View.GONE);
        if (showInput) {
            inputContent.setFocusable(true);
            inputContent.setFocusableInTouchMode(true);
            inputContent.requestFocus();
            Utils.showInputBoard(inputContent);
        } else {
            restoreInputStatus();
        }
//        Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                smoothScrollToBottom(selectedIndex);
//            }
//        }, 100);
    }

    @Override
    protected void onLikeComplete(boolean success, Model model) {
        if (success) {
            mAdapter.update(model);
            displayAdditional((Archive) model);
        }
    }

    @Override
    protected void onCollectComplete(boolean success, Model model) {
        if (success) {
            mAdapter.update(model);
            displayAdditional((Archive) model);
        }
    }

    @Override
    protected void onLoadingCommentComplete(boolean success, List<Comment> list) {
        if (success && null != list) {
            for (Comment comment : list) {
                int index = mAdapter.indexOf(comment);
                if (index >= 0) {
                    mAdapter.update(comment);
                } else {
                    mAdapter.add(comment);
                }
            }
            mAdapter.sort();
        }
        mAdapter.update(nothingMore);
    }

    private int getLastIndex() {
        return mAdapter.indexOf(nothingMore);
    }

    @Override
    protected void onCommentComplete(boolean success, Comment comment, Model model) {
        if (success) {
            inputContent.setText("");
            // 重置选择的评论id
            selectedIndex = 0;
            if (null != comment && !isEmpty(comment.getId())) {
                mAdapter.add(comment, getLastIndex());
                mAdapter.update(model);
                displayAdditional((Archive) model);
                //smoothScrollToBottom(mAdapter.getItemCount() - 1);
                restoreInputStatus();
            } else {
                restoreInputStatus();
            }
            Utils.hidingInputBoard(inputContent);
        }
    }

    @Override
    protected void onCommentDeleteDialogCanceled() {
        selectedIndex = 0;
    }

    @Override
    protected void onCommentDeleteDialogConfirmed() {
        deleteComment(mAdapter.get(mQueryId), mAdapter.get(selectedIndex).getId());
    }

    @Override
    protected void onDeleteCommentComplete(boolean success, Model model) {
        if (success) {
            if (mAdapter.get(selectedIndex) instanceof Comment) {
                mAdapter.remove(selectedIndex);
            }
            // 删除成功之后index清零
            selectedIndex = 0;
            mAdapter.update(model);
            displayAdditional((Archive) model);
        }
    }

    private ArchiveDetailsViewHolder.OnReceivedTitleListener receivedTitleListener = new ArchiveDetailsViewHolder.OnReceivedTitleListener() {
        @Override
        public void onReceivedTitle(String title) {
            titleText.setText(title);
            setCustomTitle(title);
        }
    };

    private class DetailsAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {
        private static final int VT_ARCHIVE = 0, VT_COMMENT = 1, VT_NOTHING = 2, VT_ATTACHMENT = 3, VT_ADDITIONAL = 4;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            switch (viewType) {
                case VT_ARCHIVE:
                    if (null == detailsViewHolder) {
                        detailsViewHolder = new ArchiveDetailsViewHolder(itemView, ArchiveDetailsWebViewFragment.this);
                        detailsViewHolder.setOnViewHolderElementClickListener(elementClickListener);
                        detailsViewHolder.setIsManager(enableShareDelete);
                        detailsViewHolder.setIsCollected(isCollected);
                        detailsViewHolder.setIsDraft(isDraft);
                        detailsViewHolder.setOnReceivedTitleListener(receivedTitleListener);
                    }
                    return detailsViewHolder;
                case VT_COMMENT:
                    ArchiveDetailsCommentViewHolder adcvh = new ArchiveDetailsCommentViewHolder(itemView, ArchiveDetailsWebViewFragment.this);
                    adcvh.setDeletable(deletable);
                    adcvh.setOnViewHolderElementClickListener(elementClickListener);
                    return adcvh;
                case VT_ATTACHMENT:
                    ArchiveAttachmentViewHolder aavh = new ArchiveAttachmentViewHolder(itemView, ArchiveDetailsWebViewFragment.this);
                    aavh.setOnViewHolderElementClickListener(elementClickListener);
                    return aavh;
                case VT_ADDITIONAL:
                    ArchiveDetailsAdditionalViewHolder adavh = new ArchiveDetailsAdditionalViewHolder(itemView, ArchiveDetailsWebViewFragment.this);
                    adavh.setOnViewHolderElementClickListener(elementClickListener);
                    adavh.setIsDraft(isDraft);
                    return adavh;
                default:
                    return new NothingMoreViewHolder(itemView, ArchiveDetailsWebViewFragment.this);
            }
        }

        @Override
        public int itemLayout(int viewType) {
            switch (viewType) {
                case VT_ARCHIVE:
                    return R.layout.holder_view_archive_details;
                case VT_COMMENT:
                    return R.layout.holder_view_archive_details_comment;
                case VT_ATTACHMENT:
                    return R.layout.holder_view_archive_attachment;
                case VT_ADDITIONAL:
                    return R.layout.holder_view_archive_additional;
            }
            return R.layout.holder_view_archive_details_comment_nothing_more;
        }

        @Override
        public int getItemViewType(int position) {
            Model model = get(position);
            if (model instanceof Archive) {
                return VT_ARCHIVE;
            } else if (model instanceof Comment) {
                return VT_COMMENT;
            } else if (model instanceof Attachment) {
                return VT_ATTACHMENT;
            } else if (model instanceof Additional) {
                return VT_ADDITIONAL;
            }
            return VT_NOTHING;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof ArchiveDetailsViewHolder) {
                ((ArchiveDetailsViewHolder) holder).showContent((Archive) item);
            } else if (holder instanceof ArchiveDetailsCommentViewHolder) {
                ((ArchiveDetailsCommentViewHolder) holder).showContent((Comment) item);
            } else if (holder instanceof ArchiveAttachmentViewHolder) {
                ((ArchiveAttachmentViewHolder) holder).showContent((Attachment) item);
            } else if (holder instanceof ArchiveDetailsAdditionalViewHolder) {
                ((ArchiveDetailsAdditionalViewHolder) holder).showContent((Additional) item);
            }
        }

        /**
         * 查找指定id的节点
         */
        public Model get(String queryId) {
            Iterator<Model> iterable = iterator();
            while (iterable.hasNext()) {
                Model model = iterable.next();
                if (!isEmpty(model.getId()) && model.getId().equals(queryId)) {
                    return model;
                }
            }
            return null;
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            if (item1 instanceof Comment && item2 instanceof Comment) {
                return ((Comment) item1).getCreateDate().compareTo(((Comment) item2).getCreateDate());
            }
            return 0;
        }
    }
}
