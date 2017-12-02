package com.gzlk.android.isp.fragment.archive;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.activity.MainActivity;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.archive.ArchiveRequest;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.crash.system.SysInfoUtil;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.base.BaseCmtLikeColFragment;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.individual.UserPropertyFragment;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.SimpleDialogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.helper.publishable.Collectable;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.archive.ArchiveAttachmentViewHolder;
import com.gzlk.android.isp.holder.archive.ArchiveDetailsAdditionalViewHolder;
import com.gzlk.android.isp.holder.archive.ArchiveDetailsCommentViewHolder;
import com.gzlk.android.isp.holder.archive.ArchiveDetailsViewHolder;
import com.gzlk.android.isp.holder.common.NothingMoreViewHolder;
import com.gzlk.android.isp.listener.OnKeyboardChangeListener;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.listener.OnViewHolderElementClickListener;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.archive.Additional;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.archive.Comment;
import com.gzlk.android.isp.model.common.Attachment;
import com.gzlk.android.isp.nim.file.FilePreviewHelper;
import com.gzlk.android.isp.share.ShareToQQ;
import com.gzlk.android.isp.share.ShareToWeiBo;
import com.gzlk.android.isp.share.ShareToWeiXin;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredButton;
import com.hlk.hlklib.lib.view.CorneredEditText;
import com.hlk.hlklib.lib.view.CustomTextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <b>功能描述：</b><br />
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
    private static boolean deletable = false;
    private static int selectedIndex = 0;

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
        String params = format("%s,%d", archiveId, archiveType);
        fragment.openActivity(ArchiveDetailsWebViewFragment.class.getName(), params, REQUEST_DELETE, true, false);
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        archiveType = bundle.getInt(PARAM_DOC_TYPE, Archive.Type.GROUP);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_DOC_TYPE, archiveType);
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
            // 如果不是堆栈恢复的app则打开主页面，否则直接关闭即可
            MainActivity.start(Activity());
        }
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
    private int archiveType;
    private DetailsAdapter mAdapter;
    private ArchiveDetailsViewHolder detailsViewHolder;
    private OnKeyboardChangeListener mOnKeyboardChangeListener;

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
        return parent;
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
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                displayLoading(false);
                if (success && null != archive) {
                    // 设置收藏的参数为档案
                    Collectable.resetArchiveCollectionParams(archive);
                    // 档案管理员/组织管理员/档案作者可以删除档案
                    if (archive.isAuthor()) {
                        //resetRightIconEvent();
                    }
                    // 档案创建者可以删除评论
                    deletable = archive.isAuthor();
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
                    displayAdditional(archive);
                    loadingComments(archive);
                }
            }

        }).find(archiveType, mQueryId, false);
    }

    private void displayAdditional(Archive archive) {

        archive.getAddition().setId(format("additional_%s", archive.getId()));
        if (archive.getAddition().isVisible()) {
            int index = mAdapter.indexOf(archive.getAddition());
            if (index > 0) {
                mAdapter.update(archive.getAddition());
            } else {
                mAdapter.add(archive.getAddition(), 1 + archive.getAttach().size());
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
            setRightIcon(R.string.ui_icon_more);
            setRightTitleClickListener(new OnTitleButtonClickListener() {
                @Override
                public void onClick() {
                    openShareDialog();
                }
            });
            mAdapter = new DetailsAdapter();
            mRecyclerView.setAdapter(mAdapter);
            loadingArchive();
        }
    }

    private String targetUrl = "http://120.25.124.199:8008/group1/M00/00/21/cErYIVohcxCAXdqZAAAIENZw73g7..html";

    @Override
    protected void shareToQQ() {
        Archive archive = (Archive) mAdapter.get(mQueryId);
        ShareToQQ.shareToQQ(ShareToQQ.TO_QQ, Activity(), archive.getTitle(), archive.getSharableSummary(), targetUrl, archive.getCover(), null);
    }

    @Override
    protected void shareToQZone() {
        Archive archive = (Archive) mAdapter.get(mQueryId);
        ArrayList<String> images = new ArrayList<>();
        images.add(archive.getCover());
        ShareToQQ.shareToQQ(ShareToQQ.TO_QZONE, Activity(), archive.getTitle(), archive.getSharableSummary(), targetUrl, archive.getCover(), images);
    }

    @Override
    protected void shareToWeiXinSession() {
        Archive archive = (Archive) mAdapter.get(mQueryId);
        ShareToWeiXin.shareToWeiXin(Activity(), ShareToWeiXin.TO_WX_SESSION, archive.getTitle(), archive.getSharableSummary(), targetUrl);
    }

    @Override
    protected void shareToWeiXinTimeline() {
        Archive archive = (Archive) mAdapter.get(mQueryId);
        ShareToWeiXin.shareToWeiXin(Activity(), ShareToWeiXin.TO_WX_TIMELINE, archive.getTitle(), archive.getSharableSummary(), targetUrl);
    }

    @Override
    protected void shareToWeiBo() {
        //ShareToWeiBo.init(Activity()).share(mMoment.getContent(), mMoment.getImage());
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
                mAdapter.update(comment);
            }
        }
        mAdapter.update(nothingMore);
    }

    @Override
    protected void onCommentComplete(boolean success, Comment comment, Model model) {
        if (success) {
            inputContent.setText("");
            // 重置选择的评论id
            selectedIndex = mAdapter.getItemCount() - 1;
            if (null != comment && !isEmpty(comment.getId())) {
                mAdapter.add(comment, mAdapter.getItemCount() - 1);
                mAdapter.update(model);
                displayAdditional((Archive) model);
                //smoothScrollToBottom(mAdapter.getItemCount() - 1);
                restoreInputStatus();
            } else {
                restoreInputStatus();
            }
        }
    }

    @Override
    protected void onCommentDeleteDialogCanceled() {
        selectedIndex = mAdapter.getItemCount() - 1;
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
            selectedIndex = mAdapter.getItemCount() - 1;
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
            return 0;
        }
    }
}
