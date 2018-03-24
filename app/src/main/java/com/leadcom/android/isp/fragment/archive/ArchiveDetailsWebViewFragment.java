package com.leadcom.android.isp.fragment.archive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.layoutmanager.CustomLinearLayoutManager;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredButton;
import com.hlk.hlklib.lib.view.CorneredEditText;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.MainActivity;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.archive.ArchiveRequest;
import com.leadcom.android.isp.api.common.ShareRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.crash.system.SysInfoUtil;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseCmtLikeColFragment;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.individual.UserPropertyFragment;
import com.leadcom.android.isp.fragment.organization.StructureFragment;
import com.leadcom.android.isp.helper.DeleteDialogHelper;
import com.leadcom.android.isp.helper.DialogHelper;
import com.leadcom.android.isp.helper.SimpleDialogHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.helper.publishable.Collectable;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.archive.ArchiveAttachmentViewHolder;
import com.leadcom.android.isp.holder.archive.ArchiveDetailsAdditionalViewHolder;
import com.leadcom.android.isp.holder.archive.ArchiveDetailsCommentViewHolder;
import com.leadcom.android.isp.holder.archive.ArchiveDetailsViewHolder;
import com.leadcom.android.isp.holder.common.NothingMoreViewHolder;
import com.leadcom.android.isp.holder.common.TextViewHolder;
import com.leadcom.android.isp.holder.organization.GroupInterestViewHolder;
import com.leadcom.android.isp.listener.OnKeyboardChangeListener;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.Additional;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.archive.Comment;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.common.ShareInfo;
import com.leadcom.android.isp.model.operation.GRPOperation;
import com.leadcom.android.isp.model.organization.Concern;
import com.leadcom.android.isp.model.organization.Organization;
import com.leadcom.android.isp.model.organization.Role;
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
    private static boolean deletable = false;
    /**
     * 标记是否是app内部打开的详情页
     */
    private static boolean innerOpen = false;
    /**
     * 是否可推送
     */
    public static boolean pushable = false;

    public static ArchiveDetailsWebViewFragment newInstance(String params) {
        ArchiveDetailsWebViewFragment adwvf = new ArchiveDetailsWebViewFragment();
        Bundle bundle = new Bundle();
        String[] strings = splitParameters(params);
        // 档案id
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        // 档案类型：组织档案或个人档案
        bundle.putInt(PARAM_DOC_TYPE, Integer.valueOf(strings[1]));
        adwvf.setArguments(bundle);
        return adwvf;
    }

    public static void open(BaseFragment fragment, String archiveId, int archiveType) {
        innerOpen = true;
        String params = format("%s,%d", archiveId, archiveType);
        fragment.openActivity(ArchiveDetailsWebViewFragment.class.getName(), params, REQUEST_DELETE, true, false);
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        archiveType = bundle.getInt(PARAM_DOC_TYPE, Archive.Type.GROUP);
        isDraft = archiveType >= 3;
        selectedIndex = bundle.getInt(PARAM_CMT_INDEX, 0);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_DOC_TYPE, archiveType);
        bundle.putInt(PARAM_CMT_INDEX, selectedIndex);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nothingMore = Model.getNoMore();
        inputContent.addTextChangedListener(inputTextWatcher);
        mOnKeyboardChangeListener = new OnKeyboardChangeListener(Activity());
        mOnKeyboardChangeListener.setKeyboardListener(keyboardListener);
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
        innerOpen = false;
        pushable = false;
        super.onDestroy();
    }

    @ViewId(R.id.ui_tool_view_archive_additional_comment_number)
    private TextView commentNumber;
    @ViewId(R.id.ui_tool_view_archive_additional_like_icon)
    private CustomTextView likeIcon;
    @ViewId(R.id.ui_tool_view_archive_additional_like_number)
    private TextView likeNumber;
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
    private DetailsAdapter mAdapter;
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
        loadingArchive();
    }

    @Override
    protected void onLoadingMore() {
        mAdapter.remove(nothingMore);
        loadingComments(mAdapter.get(mQueryId));
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
            R.id.ui_tool_view_simple_inputable_send})
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
        Intent intent = new Intent();
        intent.putExtra(RESULT_ARCHIVE, archive);
        intent.putExtra(RESULT_STRING, archive.getId());
        Activity().setResult(Activity.RESULT_OK, intent);
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

    private void openEditSelector() {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                return View.inflate(Activity(), R.layout.popup_dialog_edit_selector, null);
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {

            }
        }).addOnEventHandlerListener(new DialogHelper.OnEventHandlerListener() {
            @Override
            public int[] clickEventHandleIds() {
                return new int[]{R.id.ui_dialog_button_editor_to_change, R.id.ui_dialog_button_editor_to_delete};
            }

            @Override
            public boolean onClick(View view) {
                int id = view.getId();
                switch (id) {
                    case R.id.ui_dialog_button_editor_to_change:
                        break;
                    case R.id.ui_dialog_button_editor_to_delete:
                        Archive archive = (Archive) mAdapter.get(mQueryId);
                        if (archive.isAuthor()) {
                            warningDeleteDocument();
                        } else {
                            ToastHelper.make().showMsg(R.string.ui_text_document_details_delete_no_permission);
                        }
                        break;
                }
                return true;
            }
        }).setPopupType(DialogHelper.SLID_IN_BOTTOM).setAdjustScreenWidth(true).show();
    }

    private void warningDeleteDocument() {
        SimpleDialogHelper.init(Activity()).show(R.string.ui_text_document_details_delete, R.string.ui_base_text_yes, R.string.ui_base_text_no_need, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                deleteDocument();
                return true;
            }
        }, null);
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
        setRightText(R.string.ui_base_text_edit);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                openEditSelector();
            }
        });
    }

    private void loadingArchive() {
        setLoadingText(R.string.ui_text_archive_details_loading);
        displayLoading(true);
        if (isDraft) {
            loadingSharedArchive();
        } else {
            ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
                @Override
                public void onResponse(Archive archive, boolean success, String message) {
                    super.onResponse(archive, success, message);
                    displayLoading(false);
                    if (success && null != archive) {
                        displayArchive(archive);
                    }
                }

            }).find(archiveType, mQueryId, false);
        }
    }

    private void loadingSharedArchive() {
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                displayLoading(false);
                if (success && null != archive) {
                    displayArchive(archive);
                }
            }
        }).findShare(mQueryId, archiveType + 1);
    }

    /**
     * 当前角色是否具有某项权限
     */
    private boolean hasOperation(String operation) {
        return null != myRole && myRole.hasOperation(operation);
    }

    private void displayArchive(Archive archive) {
        setCustomTitle(archive.getTitle());
        myRole = Cache.cache().getGroupRole(archive.getGroupId());
        // 设置收藏的参数为档案
        Collectable.resetArchiveCollectionParams(archive);
        // 档案管理员/组织管理员/档案作者可以删除档案
        if (isEmpty(archive.getGroupId())) {
            //resetRightIconEvent();
            // 个人档案且当前用户是作者时，允许删除
            enableShareDelete = archive.isAuthor();
        } else {
            // 组织档案
            if (null != myRole && (myRole.isManager() || myRole.isArchiveManager())) {
                // 是否可以删除档案
                enableShareDelete = true;
                enableShareForward = true;
                enableShareRecommend = !archive.isRecommend();
                enableShareRecommended = archive.isRecommend();
            }
        }
        // 档案创建者可以删除评论
        deletable = enableShareDelete;
        mAdapter.update(archive);
        for (Attachment attachment : archive.getImage()) {
            mAdapter.update(attachment);
        }
        for (Attachment attachment : archive.getVideo()) {
            mAdapter.update(attachment);
        }
        for (Attachment attachment : archive.getOffice()) {
            mAdapter.update(attachment);
        }
        for (Attachment attachment : archive.getAttach()) {
            mAdapter.update(attachment);
        }
        if (!isDraft) {
            displayAdditional(archive);
            loadingComments(archive);
        }
    }

    private int getAdditionalPosition(Archive archive) {
        return archive.getOffice().size() + archive.getImage().size() + archive.getAttach().size() + archive.getVideo().size();
    }

    private void displayAdditional(Archive archive) {

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

        liked = archive.getCollection() == Archive.CollectionType.COLLECTED;
        collectIcon.setText(liked ? R.string.ui_icon_pentagon_corner_solid : R.string.ui_icon_pentagon_corner_hollow);
        collectIcon.setTextColor(getColor(liked ? R.color.colorCaution : R.color.textColorHint));
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            setCustomTitle(R.string.ui_text_archive_details_fragment_title);
            if (isDraft) {
                // 草稿档案只能查看
                additionalLayout.setVisibility(View.GONE);
            }
            if (!isDraft) {
                // 非草稿档案，可以分享等等
                setRightIcon(pushable ? 0 : R.string.ui_icon_more);
                setRightText(pushable ? R.string.ui_base_text_push : 0);
                setRightTitleClickListener(new OnTitleButtonClickListener() {
                    @Override
                    public void onClick() {
                        if (pushable) {
                            // 打开推送页面
                            openPushDialog();
                        } else {
                            fetchingShareInfo();
                        }
                    }
                });
            }
            mAdapter = new DetailsAdapter();
            mRecyclerView.setAdapter(mAdapter);

            loadingArchive();
        }
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == REQUEST_SELECT) {
            finish();
            // 需要跳转到会话页面并且关闭档案详情页
            String teamId = getResultedData(data);
            NimUIKit.startTeamSession(Activity(), teamId);
        }
        super.onActivityResult(requestCode, data);
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
                Model supper = new Model();
                supper.setId("supper");
                supper.setAccessToken("上级组织");
                Model sub = new Model();
                sub.setId("subgroup");
                sub.setAccessToken("下级组织");
                ArrayList<Concern> concerns = StructureFragment.selectedOrganization.getConGroup();
                for (Concern concern : concerns) {
                    if (concern.getType() == Concern.Type.UPPER) {
                        if (!cAdapter.exist(supper)) {
                            cAdapter.add(supper);
                        }
                        cAdapter.update(concern);
                    }
                }
                for (Concern concern : concerns) {
                    if (concern.getType() == Concern.Type.SUBGROUP) {
                        if (!cAdapter.exist(sub)) {
                            cAdapter.add(sub);
                        }
                        cAdapter.update(concern);
                    }
                }
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {
            }
        }).addOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                tryPushArchive();
                return true;
            }
        }).setPopupType(DialogHelper.SLID_IN_RIGHT).show();
    }

    private void tryPushArchive() {
        ArrayList<String> groupIds = new ArrayList<>();
        for (int i = 0; i < cAdapter.getItemCount(); i++) {
            Model model = cAdapter.get(i);
            if (model.isSelected()) {
                groupIds.add(model.getId());
            }
        }
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
            Concern concern = (Concern) cAdapter.get(index);
            concern.setSelected(!concern.isSelected());
            cAdapter.update(concern);
        }
    };

    private class ConcernAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {

        private static final int TP_TITLE = 0, TP_GROUP = 1;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            if (viewType == TP_GROUP) {
                GroupInterestViewHolder holder = new GroupInterestViewHolder(itemView, ArchiveDetailsWebViewFragment.this);
                holder.setSelectable(true);
                holder.addOnViewHolderClickListener(clickListener);
                return holder;
            } else {
                TextViewHolder tvh = new TextViewHolder(itemView, ArchiveDetailsWebViewFragment.this);
                tvh.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                return tvh;
            }
        }

        @Override
        public int itemLayout(int viewType) {
            return viewType == TP_TITLE ? R.layout.holder_view_text_olny : R.layout.holder_view_group_interesting_item;
        }

        @Override
        public int getItemViewType(int position) {
            Model model = get(position);
            if (model.getId().equals("supper") || model.getId().equals("subgroup")) {
                return TP_TITLE;
            }
            return TP_GROUP;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof GroupInterestViewHolder) {
                ((GroupInterestViewHolder) holder).showContent((Organization) item);
            } else if (holder instanceof TextViewHolder) {
                assert item != null;
                ((TextViewHolder) holder).showContent(item.getAccessToken());
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }

    private void fetchingShareInfo() {
        if (null == mShareInfo) {
            ShareRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<ShareInfo>() {
                @Override
                public void onResponse(ShareInfo shareInfo, boolean success, String message) {
                    super.onResponse(shareInfo, success, message);
                    if (success && null != shareInfo) {
                        mShareInfo = shareInfo;
                        openShareDialog();
                    }
                }
            }).getShareInfo(mQueryId, 1, archiveType + 1);
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
    protected void shareToRecommend() {

    }

    @Override
    protected void shareToRecommended() {

    }

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            switch (view.getId()) {
                case R.id.ui_holder_view_archive_details_comment_header:
                    // 点击了评论里的头像
                    UserPropertyFragment.open(ArchiveDetailsWebViewFragment.this, ((Comment) mAdapter.get(index)).getUserId());
                    break;
                case R.id.ui_holder_view_archive_details_comment_layout:
                    // 回复评论或自己
                    Comment comment = (Comment) mAdapter.get(index);
                    if (!comment.isMine()) {
                        selectedIndex = index;
                        // 要回复别人的评论
                        replyView.setText(StringHelper.getString(R.string.ui_text_archive_details_comment_hint_to, comment.getUserName()));
                        replyView.setVisibility(View.VISIBLE);
                        showInputBoard(true);
                    }
                    break;
                case R.id.ui_holder_view_archive_details_comment_delete:
                    selectedIndex = index;
                    openCommentDeleteDialog();
                    break;
                case R.id.ui_tool_view_archive_additional_like_layout:
                    // 点赞
                    like(mAdapter.get(mQueryId));
                    break;
                case R.id.ui_tool_view_archive_additional_collection_layout:
                    // 收藏
                    collect(mAdapter.get(mQueryId));
                    break;
                case R.id.ui_holder_view_archive_attachment_layout:
                    // 点击打开附件
                    Attachment attachment = (Attachment) mAdapter.get(index);
                    FilePreviewHelper.previewFile(Activity(), attachment.getUrl(), attachment.getName(), attachment.getExt());
                    break;
            }
        }
    };

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
