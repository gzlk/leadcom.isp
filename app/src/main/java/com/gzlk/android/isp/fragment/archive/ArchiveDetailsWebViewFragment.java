package com.gzlk.android.isp.fragment.archive;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.archive.ArchiveRequest;
import com.gzlk.android.isp.api.archive.CommentRequest;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.fragment.individual.UserPropertyFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.publishable.CommentHelper;
import com.gzlk.android.isp.helper.publishable.listener.OnCommentAddListener;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.archive.ArchiveDetailsCommentViewHolder;
import com.gzlk.android.isp.holder.archive.ArchiveDetailsViewHolder;
import com.gzlk.android.isp.holder.common.NothingMoreViewHolder;
import com.gzlk.android.isp.listener.OnKeyboardChangeListener;
import com.gzlk.android.isp.listener.OnViewHolderElementClickListener;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.archive.Comment;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredButton;
import com.hlk.hlklib.lib.view.CorneredEditText;
import com.hlk.hlklib.lib.view.CustomTextView;

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

public class ArchiveDetailsWebViewFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_DOC_TYPE = "adwvf_archive_type";
    private static final String PARAM_SELECTED = "adwvf_selected_comment";
    private static boolean deletable = false;

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
        selectedIndex = bundle.getInt(PARAM_SELECTED, 0);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_DOC_TYPE, archiveType);
        bundle.putInt(PARAM_SELECTED, selectedIndex);
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
    private DetailsAdapter mAdapter;
    private ArchiveDetailsViewHolder detailsViewHolder;
    private OnKeyboardChangeListener mOnKeyboardChangeListener;
    private CommentHelper commentHelper;

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

    @Click({R.id.ui_tool_view_archive_additional_comment_layout, R.id.ui_tool_view_simple_inputable_send})
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
        }
    }

    @Override
    protected boolean onBackKeyPressed() {
        boolean parent = super.onBackKeyPressed();
        if (!parent) {
            if (inputLayout.getVisibility() == View.VISIBLE) {
                showInputBoard(false);
                return true;
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
        selectedIndex = mAdapter.getItemCount() - 1;
        replyView.setVisibility(View.GONE);
        inputContent.setHint(R.string.ui_text_archive_details_comment_hint);
    }

    private void trySendComment() {
        String content = inputContent.getValue();
        if (!isEmpty(content)) {
            Model model = mAdapter.get(selectedIndex);
            if (model instanceof Comment) {
                Comment comment = (Comment) model;
                if (!comment.getUserId().equals(Cache.cache().userId)) {
                    // 评论别人
                    sendComment(content, comment.getUserId());
                }
            } else {
                sendComment(content, "");
            }
        }
    }

    private void sendComment(String content, String toUserId) {
        setLoadingText(R.string.ui_text_archive_details_comment_sending);
        displayLoading(true);
        commentHelper.setCommentAddListener(new OnCommentAddListener() {
            @Override
            public void onComplete(boolean success, Comment comment, Model model) {
                displayLoading(false);
                if (success) {
                    inputContent.setText("");
                    if (null != comment && !isEmpty(comment.getId())) {
                        mAdapter.add(comment, mAdapter.getItemCount() - 1);
                        mAdapter.update(model);
                        displayAdditional((Archive) model);
                        smoothScrollToBottom(mAdapter.getItemCount() - 1);
                        restoreInputStatus();
                    } else {
                        loadingComments();
                        restoreInputStatus();
                    }
                }
            }
        }).comment(commentType(), content, toUserId);
    }

    private int commentType() {
        return archiveType == Archive.Type.GROUP ? Comment.Type.GROUP : Comment.Type.USER;
    }

    private void loadingComments() {
        mAdapter.remove(nothingMore);
        setLoadingText(R.string.ui_text_document_details_loading_comments);
        displayLoading(true);
        CommentRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Comment>() {
            @Override
            public void onResponse(List<Comment> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                displayLoading(false);
                isLoadingComplete(null == list || list.size() < pageSize);
                if (success && null != list) {
                    for (Comment comment : list) {
                        mAdapter.update(comment);
                    }
                }
                stopRefreshing();
                mAdapter.update(nothingMore);
            }
        }).list(commentType(), mQueryId, remotePageNumber);
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
                    // 档案创建者可以删除评论
                    deletable = archive.getUserId().equals(Cache.cache().userId);
                    mAdapter.update(archive);
                    displayAdditional(archive);
                    if (null == commentHelper) {
                        commentHelper = CommentHelper.helper().setArchive(archive);
                    }
                    loadingComments();
                }
            }

        }).find(archiveType, mQueryId, false);
    }

    private void displayAdditional(Archive archive) {
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
            mAdapter = new DetailsAdapter();
            mRecyclerView.setAdapter(mAdapter);
            loadingArchive();
        }
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
                    if (!comment.getUserId().equals(Cache.cache().userId)) {
                        selectedIndex = index;
                        // 要回复别人的评论
                        replyView.setText(StringHelper.getString(R.string.ui_text_archive_details_comment_hint_to, comment.getUserName()));
                        replyView.setVisibility(View.VISIBLE);
                        showInputBoard(true);
                    }
                    break;
                case R.id.ui_holder_view_archive_details_comment_delete:
                    // 删除评论
                    deleteComment(index, mAdapter.get(index).getId());
                    break;
            }
        }
    };

    private void deleteComment(final int index, String commentId) {
        setLoadingText(R.string.ui_text_archive_details_comment_deleting);
        displayLoading(true);
        CommentRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Comment>() {
            @Override
            public void onResponse(Comment comment, boolean success, String message) {
                super.onResponse(comment, success, message);
                displayLoading(false);
                if (success) {
                    mAdapter.remove(index);
                    Archive archive = (Archive) mAdapter.get(mQueryId);
                    int cmtNum = archive.getCmtNum() - 1;
                    if (cmtNum <= 0) {
                        cmtNum = 0;
                    }
                    archive.setCmtNum(cmtNum);
                    mAdapter.update(archive);
                    displayAdditional(archive);
                }
            }
        }).delete(commentType(), mQueryId, commentId);
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
        Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                smoothScrollToBottom(selectedIndex);
            }
        }, 100);
    }

    private class DetailsAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {
        private static final int VT_ARCHIVE = 0, VT_COMMENT = 1, VT_NOTHING = 2;

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
            }
            return VT_NOTHING;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof ArchiveDetailsViewHolder) {
                ((ArchiveDetailsViewHolder) holder).showContent((Archive) item);
            } else if (holder instanceof ArchiveDetailsCommentViewHolder) {
                ((ArchiveDetailsCommentViewHolder) holder).showContent((Comment) item);
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
