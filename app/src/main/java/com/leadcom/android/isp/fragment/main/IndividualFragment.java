package com.leadcom.android.isp.fragment.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.archive.ArchiveRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.user.CollectionRequest;
import com.leadcom.android.isp.api.user.PublicMomentRequest;
import com.leadcom.android.isp.application.NimApplication;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.archive.ArchiveDetailsWebViewFragment;
import com.leadcom.android.isp.fragment.archive.ArchiveEditorFragment;
import com.leadcom.android.isp.fragment.base.BaseCmtLikeColFragment;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.individual.CollectionDetailsFragment;
import com.leadcom.android.isp.fragment.individual.moment.MomentCreatorFragment;
import com.leadcom.android.isp.fragment.individual.moment.MomentDetailsFragment;
import com.leadcom.android.isp.fragment.individual.moment.MomentImagesFragment;
import com.leadcom.android.isp.fragment.individual.UserMessageFragment;
import com.leadcom.android.isp.fragment.individual.UserPropertyFragment;
import com.leadcom.android.isp.fragment.organization.StructureFragment;
import com.leadcom.android.isp.helper.DialogHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.TooltipHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.common.NothingMoreViewHolder;
import com.leadcom.android.isp.holder.home.ArchiveHomeRecommendedViewHolder;
import com.leadcom.android.isp.holder.individual.CollectionItemViewHolder;
import com.leadcom.android.isp.holder.individual.IndividualFunctionViewHolder;
import com.leadcom.android.isp.holder.individual.IndividualHeaderViewHolder;
import com.leadcom.android.isp.holder.individual.MomentCommentTextViewHolder;
import com.leadcom.android.isp.holder.individual.MomentDetailsViewHolder;
import com.leadcom.android.isp.holder.individual.MomentHomeCameraViewHolder;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.listener.OnHandleBoundDataListener;
import com.leadcom.android.isp.listener.OnNimMessageEvent;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.archive.Comment;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.user.Collection;
import com.leadcom.android.isp.model.user.Moment;
import com.leadcom.android.isp.model.user.MomentPublic;
import com.leadcom.android.isp.model.user.User;
import com.leadcom.android.isp.nim.activity.VideoPlayerActivity;
import com.leadcom.android.isp.nim.file.FilePreviewHelper;
import com.leadcom.android.isp.nim.model.notification.NimMessage;
import com.hlk.hlklib.lib.view.CorneredButton;
import com.hlk.hlklib.lib.view.CorneredEditText;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <b>功能描述：</b>首页 - 个人<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/20 22:15 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/20 22:15 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class IndividualFragment extends BaseCmtLikeColFragment {

    private static final String PARAM_SHOWN = "title_bar_shown";
    private static final String PARAM_SELECTED = "function_selected";
    private static final String PAGE_TAG = "ifmt_page_%d_";
    private static final String PARAM_SELECTED_MMT = "mmt_selected";
    private static final String PARAM_SELECTED_CMT = "ifmt_selected_cmt";
    private boolean isTitleBarShown = false;
    private int selectedFunction = 0, selectedMoment = 0, selectedComment = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NimApplication.addNimMessageEvent(messageEvent);
        nothingMore = Model.getNoMore();
    }

    @Override
    public void onDestroy() {
        NimApplication.removeNimMessageEvent(messageEvent);
        super.onDestroy();
    }

    private OnNimMessageEvent messageEvent = new OnNimMessageEvent() {
        @Override
        public void onMessageEvent(NimMessage message) {
            if (!message.isSavable()) {
                // 拉取消息列表
                performRefresh();
            }
        }
    };

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        isTitleBarShown = bundle.getBoolean(PARAM_SHOWN, false);
        selectedFunction = bundle.getInt(PARAM_SELECTED, 0);
        selectedMoment = bundle.getInt(PARAM_SELECTED_MMT, 0);
        selectedComment = bundle.getInt(PARAM_SELECTED_CMT, 0);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        bundle.putBoolean(PARAM_SHOWN, isTitleBarShown);
        bundle.putInt(PARAM_SELECTED, selectedFunction);
        bundle.putInt(PARAM_SELECTED_MMT, selectedMoment);
        bundle.putInt(PARAM_SELECTED_CMT, selectedComment);
        super.saveParamsToBundle(bundle);
    }

    /**
     * 标题栏是否已经显示了
     */
    public boolean isTitleBarShown() {
        return isTitleBarShown;
    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        initializeAdapter();
        //autoRefreshing();
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
        remotePageNumber = 1;
        resetList();
        performRefresh();
    }

    @Override
    protected void onLoadingMore() {
        performRefresh();
    }

    @Override
    protected String getLocalPageTag() {
        return format(PAGE_TAG, selectedFunction);
    }

    /**
     * 尝试自动刷新
     */
//    private void autoRefreshing() {
//        if (!isNeedRefresh()) {
//            return;
//        }
//        // 远程刷新的页码小于总页码时才刷新
//        refreshing();
//        performRefresh();
//    }
    private void performRefresh() {
        mAdapter.remove(nothingMore);
        displayLoading(true);
        switch (selectedFunction) {
            case 0:
                // 刷新动态列表
                refreshingRemoteMoments();
                break;
            case 1:
                // 刷新档案列表
                refreshingRemoteDocuments();
                break;
            case 2:
                // 刷新收藏列表
                refreshingFavorites();
                break;
        }
    }

    /**
     * 拉取我的最新说说列表
     */
    private void refreshingRemoteMoments() {
        //setLoadingText(R.string.ui_individual_moment_list_loading);
        //displayLoading(true);
        PublicMomentRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<MomentPublic>() {
            @Override
            public void onResponse(List<MomentPublic> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                int count = null == list ? 0 : list.size();
                adjustRemotePages(count, pageSize);
                if (success) {
                    if (null != list) {
                        if (selectedFunction == 0) {
                            Moment today = (Moment) mAdapter.get(2);
                            today.setAuthPublic(userInfoNum);
                            today.setContent(lastHeadPhoto);
                            mAdapter.notifyItemChanged(2);
                            for (MomentPublic moment : list) {
                                appendMoment(moment.getUserMmt());
                            }
                        }
                    }
                }
                if (count < pageSize) {
                    mAdapter.add(nothingMore);
                }
            }
        }).list(StructureFragment.selectedGroupId, remotePageNumber);
    }

    private void appendMoment(Moment moment) {
        moment.resetAdditional(moment.getAddition());
        mAdapter.update(moment);
        clearMomentComments(moment);
        int index = mAdapter.indexOf(moment);
        int size = moment.getUserMmtCmtList().size();
        for (Comment comment : moment.getUserMmtCmtList()) {
            index++;
            comment.setLast(false);
            mAdapter.add(comment, index);
        }
        if (size > 0) {
            // 设置最后一个评论
            Comment cmt = (Comment) mAdapter.get(index);
            cmt.setLast(true);
            mAdapter.update(cmt);
        }
    }

    private void clearMomentComments(Moment moment) {
        Iterator<Model> iterator = mAdapter.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            Model model = iterator.next();
            if (model instanceof Comment) {
                Comment comment = (Comment) model;
                if (comment.getMomentId().equals(moment.getId())) {
                    iterator.remove();
                    mAdapter.notifyItemRemoved(index);
                }
            }
            index++;
        }
    }

    /**
     * 拉取我的档案列表
     */
    private void refreshingRemoteDocuments() {
        //mAdapter.remove(noMore());
        //setLoadingText(R.string.ui_individual_archive_list_loading);
        //displayLoading(true);
        ArchiveRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Archive>() {
            @SuppressWarnings("unchecked")
            @Override
            public void onResponse(List<Archive> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                int count = null == list ? 0 : list.size();
                adjustRemotePages(count, pageSize);
                if (success) {
                    if (null != list) {
                        if (selectedFunction == 1) {
                            for (Archive archive : list) {
                                mAdapter.update(archive);
                            }
                        }
                    }
                }
                if (count < pageSize) {
                    mAdapter.add(nothingMore);
                }
            }
        }).list(remotePageNumber, Cache.cache().userId);
    }

    private void refreshingFavorites() {
        //mAdapter.remove(noMore());
        //setLoadingText(R.string.ui_individual_collection_list_loading);
        //displayLoading(true);
        CollectionRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Collection>() {
            @SuppressWarnings("unchecked")
            @Override
            public void onResponse(List<Collection> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                int count = null == list ? 0 : list.size();
                adjustRemotePages(count, pageSize);
                if (success) {
                    if (null != list) {
                        if (selectedFunction == 2) {
                            for (Collection collection : list) {
                                mAdapter.update(collection);
                            }
                        }
                    }
                }
                if (count < pageSize) {
                    mAdapter.add(nothingMore);
                }
            }
        }).list(Collection.Type.ALL_ARCHIVE, CollectionRequest.OPE_MONTH, remotePageNumber);
    }

    private void adjustRemotePages(int fetchedCount, int pageSize) {
        // 如果当前拉取的是满页数据，则下次再拉取的时候拉取下一页
        remotePageNumber += (fetchedCount >= pageSize ? 1 : 0);
        isLoadingComplete(fetchedCount < pageSize);
        displayLoading(false);
        stopRefreshing();
    }

    private IndividualAdapter mAdapter;
    private Model functions, nothingMore;
    private Moment cameraMoment;

    // 功能列表
    private Model functions() {
        if (null == functions) {
            functions = new Model();
            functions.setId("only for functions");
        }
        return functions;
    }

    // 今天
    private Moment today() {
        if (null == cameraMoment) {
            cameraMoment = new Moment();
            cameraMoment.setId(getString(R.string.ui_text_moment_item_default_today));
        }
        // 设置时间为今天最后一秒，在排序时会一直排在最前面
        cameraMoment.setCreateDate(Utils.formatDateOfNow("yyyy-MM-dd 23:59:59"));
        return cameraMoment;
    }

    private void appendListHeader(boolean needToday) {
        mAdapter.add(Cache.cache().me, 0);
        mAdapter.add(functions(), 1);
        if (needToday) {
            mAdapter.add(today(), 2);
        }
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            // 这里不需要直接上传，只需要把选择的图片传递给新建动态页面即可，上传在那里实现
            isSupportDirectlyUpload = false;
            // 添加图片选择
            addOnImageSelectedListener(imageSelectedListener);
            mRecyclerView.addOnScrollListener(scrollListener);
            mAdapter = new IndividualAdapter();
            mRecyclerView.setAdapter(mAdapter);
            appendListHeader(selectedFunction == 0);
            // 自动加载本地缓存中的记录
            performRefresh();
        } else {
            // 更新我的信息
            mAdapter.update(Cache.cache().me);
        }
    }

    private OnImageSelectedListener imageSelectedListener = new OnImageSelectedListener() {
        @Override
        public void onImageSelected(ArrayList<String> selected) {
            // 打开新建动态页面
            MomentCreatorFragment.open(IndividualFragment.this, Json.gson().toJson(selected));
            getWaitingForUploadFiles().clear();
            //openActivity(MomentCreatorFragment.class.getName(), Json.gson().toJson(selected), REQUEST_CREATE, true, true);
        }
    };

    private SoftReference<View> toolBarView;

    public IndividualFragment setToolBar(View view) {
        if (null == toolBarView || null == toolBarView.get()) {
            toolBarView = new SoftReference<>(view);
        }
        return this;
    }

    private SoftReference<View> textView;

    public void setToolBarTextView(View view) {
        if (null == textView || null == textView.get()) {
            textView = new SoftReference<>(view);
        }
    }

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        private int scrolledY = 0;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (!isViewPagerDisplayedCurrent()) {
                return;
            }
            scrolledY += dy;
            if (scrolledY >= 0 && scrolledY <= 500) {
                float alpha = scrolledY * 0.005f;
                if (null != toolBarView && null != toolBarView.get()) {
                    toolBarView.get().setAlpha(alpha);
                    isTitleBarShown = toolBarView.get().getAlpha() >= 1;
                }
                if (null != textView && null != textView.get()) {
                    textView.get().setAlpha(alpha);
                }
            }
        }
    };

    private void resetList() {
        int size = mAdapter.getItemCount();
        while (size > 2) {
            mAdapter.remove(size - 1);
            size = mAdapter.getItemCount();
        }
        if (selectedFunction == 0) {
            mAdapter.add(today());
        }
        setSupportLoadingMore(true);
    }

    public void onActivityResult(int requestCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SELECT:
                // 个人档案不需要groupId
                ArchiveEditorFragment.open(IndividualFragment.this, "", getResultedData(data));
                break;
            case REQUEST_CREATE:
                onSwipeRefreshing();
                break;
            case REQUEST_DELETE:
                // 上层返回的有更改的或删除的
                Model model = new Model();
                model.setId(getResultedData(data));
                mAdapter.remove(model);
                break;
        }
        super.onActivityResult(requestCode, data);
    }

    private IndividualFunctionViewHolder.OnFunctionChangeListener functionChangeListener = new IndividualFunctionViewHolder.OnFunctionChangeListener() {
        @Override
        public void onChange(int index) {
            if (selectedFunction != index) {
                setSupportLoadingMore(true);
                // 重置本地页码
                remotePageNumber = 1;
                selectedFunction = index;
                resetList();
                // 尝试从服务器上拉取新纪录
                performRefresh();
            }
        }
    };

    private View openUserMsgDialogView;

    public void openUserMessageList() {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == openUserMsgDialogView) {
                    openUserMsgDialogView = View.inflate(Activity(), R.layout.popup_dialog_individual_user_message, null);
                }
                return openUserMsgDialogView;
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {

            }
        }).addOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                UserMessageFragment.open(IndividualFragment.this);
                return true;
            }
        }).setAdjustScreenWidth(true).setPopupType(DialogHelper.SLID_IN_BOTTOM).show();
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Model model = mAdapter.get(index);
            if (model instanceof Moment) {
                momentClick((Moment) model);
            } else if (model instanceof Archive) {
                archiveClick((Archive) model);
            } else if (model instanceof Collection) {
                collectionClick((Collection) model);
            }
        }
    };

    private void collectionClick(Collection collection) {
        switch (collection.getType()) {
            case Collection.Type.TEXT:
            case Collection.Type.IMAGE:
                openActivity(CollectionDetailsFragment.class.getName(), collection.getId(), true, false);
                break;
            case Collection.Type.GROUP_ARCHIVE:
                ArchiveDetailsWebViewFragment.open(this, collection.getSourceId(), Archive.Type.GROUP);
                break;
            case Collection.Type.USER_ARCHIVE:
                ArchiveDetailsWebViewFragment.open(this, collection.getSourceId(), Archive.Type.USER);
                break;
            case Collection.Type.ATTACHMENT:
            case Collection.Type.ARCHIVE:
                String name = collection.getContent().substring(collection.getContent().lastIndexOf('/') + 1);
                Attachment att = new Attachment();
                att.setUrl(collection.getContent());
                att.setName(name);
                att.resetInformation();
                FilePreviewHelper.previewFile(Activity(), att.getUrl(), att.getName(), att.getExt());
                break;
            case Collection.Type.USER_MOMENT:
                if (collection.getUserMmt().getImage().size() > 0) {
                    MomentImagesFragment.open(this, collection.getSourceId(), 0);
                } else {
                    MomentDetailsFragment.open(this, collection.getSourceId());
                }
                break;
            case Collection.Type.VIDEO:
                VideoPlayerActivity.start(Activity(), collection.getContent());
                break;
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ui_tooltip_menu_moment_comment:
                case R.id.ui_tooltip_menu_moment_comment1:
                    // 发表评论，打开详情页评论
                    selectedComment = 0;
                    openCommentReplyDialog();
                    //MomentDetailsFragment.open(IndividualFragment.this, mAdapter.get(selectedMoment).getId());
                    break;
                case R.id.ui_tooltip_menu_moment_praise:
                    // 点赞说说
                    like(mAdapter.get(selectedMoment));
                    break;
                case R.id.ui_tooltip_menu_moment_praised:
                    // 取消赞说说
                    like(mAdapter.get(selectedMoment));
                    break;
            }
        }
    };

    private OnViewHolderElementClickListener onViewHolderElementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            switch (view.getId()) {
                case R.id.ui_holder_view_moment_camera_icon:
                    // 选择照片
                    openImageSelector(true);
                    break;
                case R.id.ui_holder_view_moment_camera_message_layer:
                    // 打开消息列表
                    UserMessageFragment.open(IndividualFragment.this);
                    break;
                case R.id.ui_holder_view_moment_details_header:
                    Moment m = (Moment) mAdapter.get(index);
                    UserPropertyFragment.open(IndividualFragment.this, m.getUserId());
                    break;
                case R.id.ui_holder_view_moment_details_container:
                    // 打开详情页了
                    Moment moment = (Moment) mAdapter.get(index);
                    if (moment.getImage().size() < 1) {
                        // 没有图片，直接打开说说详情页
                        MomentDetailsFragment.open(IndividualFragment.this, moment.getId());
                    } else {
                        // 默认打开第一个图片
                        MomentImagesFragment.open(IndividualFragment.this, moment.getId(), 0);
                    }
                    break;
                case R.id.ui_holder_view_moment_details_more:
                    // 打开快捷赞、评论菜单
                    selectedMoment = index;
                    Model model = mAdapter.get(index);
                    if (model instanceof Moment) {
                        Moment mmt = (Moment) model;
                        // 已赞和未赞
                        int layout = mmt.isLiked() ? R.id.ui_tooltip_moment_comment_praised : R.id.ui_tooltip_moment_comment;
                        showTooltip(view, layout, true, TooltipHelper.TYPE_RIGHT, onClickListener);
                    }
                    break;
                case R.id.ui_holder_view_individual_moment_comment_name_container:
                    selectedComment = index;
                    Comment comment = (Comment) mAdapter.get(selectedComment);
                    if (comment.isMine()) {
                        openCommentDeleteDialog();
                    } else {
                        openCommentReplyDialog();
                    }
                    break;
                case R.id.ui_tool_view_archive_additional_comment_layout:
                    // 个人档案评论
                    ArchiveDetailsWebViewFragment.open(IndividualFragment.this, mAdapter.get(index).getId(), Archive.Type.USER);
                    break;
                case R.id.ui_tool_view_archive_additional_like_layout:
                    // 个人档案点赞
                    like(mAdapter.get(index));
                    break;
                case R.id.ui_tool_view_archive_additional_collection_layout:
                    // 个人档案收藏
                    collect(mAdapter.get(index));
                    break;
                case R.id.ui_tool_view_document_user_header_image:
                    Archive archive = (Archive) mAdapter.get(index);
                    UserPropertyFragment.open(IndividualFragment.this, archive.getUserId());
                    break;
                case R.id.ui_holder_view_collection_header:
                    Collection collection = (Collection) mAdapter.get(index);
                    UserPropertyFragment.open(IndividualFragment.this, collection.getCreatorId());
                    break;
                case R.id.ui_holder_view_collection_content_cover:
                    collectionClick((Collection) mAdapter.get(index));
                    break;
            }
        }
    };

    @Override
    protected void onCommentDeleteDialogCanceled() {
        selectedComment = 0;
    }

    @Override
    protected void onCommentDeleteDialogConfirmed() {
        Comment comment = (Comment) mAdapter.get(selectedComment);
        deleteComment(mAdapter.get(comment.getMomentId()), comment.getId());
    }

    @Override
    protected void onDeleteCommentComplete(boolean success, Model model) {
        if (success) {
            selectedComment = 0;
            appendMoment((Moment) model);
        }
    }

    private View replyDialogView, inputableView;
    private TextView replyName;
    private CorneredEditText replyContent;
    private CorneredButton replyButton;

    private void openCommentReplyDialog() {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == replyDialogView) {
                    replyDialogView = View.inflate(Activity(), R.layout.popup_dialog_comment_input, null);
                    inputableView = replyDialogView.findViewById(R.id.ui_tool_view_simple_inputable_layout);
                    inputableView.setVisibility(View.VISIBLE);
                    replyName = replyDialogView.findViewById(R.id.ui_tool_view_simple_inputable_reply);
                    replyContent = replyDialogView.findViewById(R.id.ui_tool_view_simple_inputable_text);
                    replyContent.setHint(R.string.ui_text_archive_details_comment_hint);
                    replyContent.addTextChangedListener(inputTextWatcher);
                    replyButton = replyDialogView.findViewById(R.id.ui_tool_view_simple_inputable_send);
                }
                return replyDialogView;
            }

            @Override
            public void onBindData(View dialogView, final DialogHelper helper) {
                replyContent.setText("");
                Model model = mAdapter.get(selectedComment);
                if (model instanceof Comment) {
                    Comment comment = (Comment) model;
                    if (comment.isMine()) {
                        // 直接发布评论
                        replyName.setVisibility(View.GONE);
                    } else {
                        replyName.setText(StringHelper.getString(R.string.ui_text_archive_details_comment_hint_to, comment.getUserName()));
                        replyName.setVisibility(View.VISIBLE);
                    }
                } else {
                    // 直接发布评论
                    replyName.setVisibility(View.GONE);
                }
                replyContent.setOnImeBackKeyListener(new CorneredEditText.OnImeBackKeyListener() {
                    @Override
                    public void onBackKey(EditText editText) {
                        Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                helper.dismiss();
                            }
                        }, 100);
                    }
                });
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
                    replyButton.setVisibility(size > 0 ? View.VISIBLE : View.GONE);
                }
            };

        }).addOnEventHandlerListener(new DialogHelper.OnEventHandlerListener() {
            @Override
            public int[] clickEventHandleIds() {
                return new int[]{R.id.ui_tool_view_simple_inputable_send};
            }

            @Override
            public boolean onClick(View view) {
                if (view.getId() == R.id.ui_tool_view_simple_inputable_send) {
                    // 发布评论
                    final String content = replyContent.getValue();
                    if (!isEmpty(content)) {
                        comment(content);
                        return true;
                    }
                    return false;
                }
                return true;
            }
        }).addOnDialogCancelListener(new DialogHelper.OnDialogCancelListener() {
            @Override
            public void onCancel() {
                Utils.hidingInputBoard(replyContent);
            }
        }).addOnDialogDismissListener(new DialogHelper.OnDialogDismissListener() {
            @Override
            public void onDismiss() {
                View view = Activity().getCurrentFocus();
                if (null != view) {
                    Utils.hidingInputBoard(view);
                }
            }
        }).setPopupType(DialogHelper.SLID_IN_BOTTOM).setAdjustScreenWidth(true).show();
        Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                replyContent.setFocusable(true);
                replyContent.setFocusableInTouchMode(true);
                replyContent.requestFocus();
                Utils.showInputBoard(replyContent);
            }
        }, 100);
    }

    private void comment(String content) {
        if (selectedComment > 0) {
            Model model = mAdapter.get(selectedComment);
            if (model instanceof Comment) {
                Comment comment = (Comment) model;
                if (comment.isMine()) {
                    comment(mAdapter.get(comment.getMomentId()), content, "");
                } else {
                    comment(mAdapter.get(comment.getMomentId()), content, comment.getUserId());
                }
            }
        } else {
            // 直接评论
            comment(mAdapter.get(selectedMoment), content, "");
        }
    }

    @Override
    protected void onCommentComplete(boolean success, Comment comment, Model model) {
        if (success) {
            appendMoment((Moment) model);
        }
    }

    private OnHandleBoundDataListener<Model> momentBoundDataListener = new OnHandleBoundDataListener<Model>() {
        @Override
        public Model onHandlerBoundData(BaseViewHolder holder) {
            return mAdapter.get(holder.getAdapterPosition());
        }
    };

    private void momentClick(Moment moment) {
        if (null != moment) {
            // 点击打开新窗口查看详情
            if (moment.getId().contains("cameraMoment's")) {
                openImageSelector(true);
            } else {
                // 默认显示第一张图片
                MomentImagesFragment.open(IndividualFragment.this, moment.getId(), 0);
            }
        }
    }

    private void archiveClick(Archive archive) {
        if (null != archive) {
            int type = isEmpty(archive.getGroupId()) ? Archive.Type.USER : Archive.Type.GROUP;
            ArchiveDetailsWebViewFragment.open(IndividualFragment.this, archive.getId(), type);
        }
    }

    @Override
    protected void onLikeComplete(boolean success, Model model) {
        if (success) {
            mAdapter.update(model);
        }
    }

    @Override
    protected void onCollectComplete(boolean success, Model model) {
        if (success) {
            mAdapter.update(model);
        }
    }

    private class IndividualAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {

        private static final int VT_HEADER = 0, VT_FUNCTION = 1, VT_MOMENT = 2,
                VT_ARCHIVE = 3, VT_COLLECTION = 4, VT_CAMERA = 5, VT_NO_MORE = 6,
                VT_COMMENT = 7;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            BaseFragment fragment = IndividualFragment.this;
            switch (viewType) {
                case VT_HEADER:
                    return new IndividualHeaderViewHolder(itemView, fragment);
                case VT_FUNCTION:
                    IndividualFunctionViewHolder ifvh = new IndividualFunctionViewHolder(itemView, fragment);
                    // 初始化选中第一个
                    ifvh.setSelected(0);
                    ifvh.addOnFunctionChangeListener(functionChangeListener);
                    return ifvh;
                case VT_MOMENT:
                    MomentDetailsViewHolder mdvh = new MomentDetailsViewHolder(itemView, fragment);
                    mdvh.setOnViewHolderElementClickListener(onViewHolderElementClickListener);
                    mdvh.addOnHandlerBoundDataListener(momentBoundDataListener);
                    mdvh.setToDetails(true);
                    mdvh.isShowLike(true);
                    return mdvh;
                case VT_CAMERA:
                    MomentHomeCameraViewHolder mhcvh = new MomentHomeCameraViewHolder(itemView, fragment);
                    mhcvh.setOnViewHolderElementClickListener(onViewHolderElementClickListener);
                    return mhcvh;
                case VT_ARCHIVE:
                    ArchiveHomeRecommendedViewHolder holder = new ArchiveHomeRecommendedViewHolder(itemView, fragment);
                    holder.addOnViewHolderClickListener(onViewHolderClickListener);
                    holder.setOnViewHolderElementClickListener(onViewHolderElementClickListener);
                    holder.setHeaderShaoable(true);
                    return holder;
                case VT_COLLECTION:
                    CollectionItemViewHolder civh = new CollectionItemViewHolder(itemView, fragment);
                    civh.setOnViewHolderElementClickListener(onViewHolderElementClickListener);
                    return civh;
                case VT_NO_MORE:
                    return new NothingMoreViewHolder(itemView, fragment);
                case VT_COMMENT:
                    MomentCommentTextViewHolder mctvh = new MomentCommentTextViewHolder(itemView, fragment);
                    mctvh.setOnViewHolderElementClickListener(onViewHolderElementClickListener);
                    return mctvh;
            }
            return null;
        }

        @Override
        public int itemLayout(int viewType) {
            switch (viewType) {
                case VT_HEADER:
                    return R.layout.holder_view_individual_header;
                case VT_FUNCTION:
                    return R.layout.holder_view_individual_main_functions;
                case VT_MOMENT:
                    return R.layout.holder_view_individual_moment_details;
                case VT_ARCHIVE:
                    return R.layout.holder_view_archive_home_feature;
                case VT_CAMERA:
                    return R.layout.holder_view_individual_moment_camera;
                case VT_NO_MORE:
                    return R.layout.holder_view_nothing_more;
                case VT_COMMENT:
                    return R.layout.holder_view_individual_moment_comment_name;
                default:
                    return R.layout.holder_view_collection;
            }
        }

        @Override
        public int getItemViewType(int position) {
            Model model = get(position);
            if (model instanceof User) {
                return VT_HEADER;
            } else if (model.getId().contains("moment")) {
                return VT_CAMERA;
            } else if (model.getId().contains("no_more_any_things")) {
                return VT_NO_MORE;
            } else if (model instanceof Moment) {
                return VT_MOMENT;
            } else if (model instanceof Archive) {
                return VT_ARCHIVE;
            } else if (model instanceof Collection) {
                return VT_COLLECTION;
            } else if (model instanceof Comment) {
                return VT_COMMENT;
            } else return VT_FUNCTION;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof IndividualHeaderViewHolder) {
                ((IndividualHeaderViewHolder) holder).showContent((User) item);
            } else if (holder instanceof MomentDetailsViewHolder) {
                ((MomentDetailsViewHolder) holder).showContent((Moment) item);
            } else if (holder instanceof ArchiveHomeRecommendedViewHolder) {
                ((ArchiveHomeRecommendedViewHolder) holder).showContent((Archive) item);
            } else if (holder instanceof CollectionItemViewHolder) {
                ((CollectionItemViewHolder) holder).showContent((Collection) item);
            } else if (holder instanceof MomentHomeCameraViewHolder) {
                ((MomentHomeCameraViewHolder) holder).showContent((Moment) item);
            } else if (holder instanceof NothingMoreViewHolder) {
                ((NothingMoreViewHolder) holder).showContent(item);
            } else if (holder instanceof MomentCommentTextViewHolder) {
                ((MomentCommentTextViewHolder) holder).showContent((Comment) item);
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
            if (item1 instanceof Moment && item2 instanceof Moment) {
                Moment m1 = (Moment) item1;
                Moment m2 = (Moment) item2;
                return -m1.getCreateDate().compareTo(m2.getCreateDate());
            }
            return 0;
        }
    }
}