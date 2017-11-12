package com.gzlk.android.isp.fragment.individual;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.TooltipHelper;
import com.gzlk.android.isp.helper.publishable.Collectable;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.common.NothingMoreViewHolder;
import com.gzlk.android.isp.holder.individual.MomentCommentHeaderViewHolder;
import com.gzlk.android.isp.holder.individual.MomentDetailsViewHolder;
import com.gzlk.android.isp.holder.individual.MomentPraiseViewHolder;
import com.gzlk.android.isp.listener.OnHandleBoundDataListener;
import com.gzlk.android.isp.listener.OnKeyboardChangeListener;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.listener.OnViewHolderElementClickListener;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.archive.ArchiveLike;
import com.gzlk.android.isp.model.archive.Comment;
import com.gzlk.android.isp.model.user.Moment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <b>功能描述：</b>说说详情页<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/07/23 22:55 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/07/23 22:55 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MomentDetailsFragment extends BaseMomentFragment {

    public static MomentDetailsFragment newInstance(String params) {
        MomentDetailsFragment mdf = new MomentDetailsFragment();
        Bundle bundle = new Bundle();
        // 单个说说的id
        bundle.putString(PARAM_QUERY_ID, params);
        mdf.setArguments(bundle);
        return mdf;
    }

    public static void open(BaseFragment fragment, String momentId) {
        fragment.openActivity(MomentDetailsFragment.class.getName(), momentId, true, false);
    }

    private static boolean deletable = false;
    private static int selectedComment = 0;
    private static final String PARAM_INDEX = "mdf_selected_index";
    private Model noMore = Model.getNoMore();

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mSelectedIndex = bundle.getInt(PARAM_INDEX, -1);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_INDEX, mSelectedIndex);
    }

    @Override
    protected void destroyView() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mOnKeyboardChangeListener = new OnKeyboardChangeListener(Activity());
        mOnKeyboardChangeListener.setKeyboardListener(new OnKeyboardChangeListener.KeyboardListener() {
            @Override
            public void onKeyboardChange(boolean isShow, int keyboardHeight) {
                log(format("keyboard changed, show: %s, keyboard height: %d", isShow, keyboardHeight));
                if (!isShow) {
                    replyName.setVisibility(View.GONE);
                }
            }
        });
        setInputHint(R.string.ui_text_archive_details_comment_hint);
    }

    @Override
    public void onDestroy() {
        if (null != mOnKeyboardChangeListener) {
            mOnKeyboardChangeListener.destroy();
        }
        super.onDestroy();
    }

    private MomentDetailsAdapter mAdapter;
    private MomentPraiseViewHolder praiseViewHolder;
    private int mSelectedIndex = -1;
    private OnKeyboardChangeListener mOnKeyboardChangeListener;

    @Override
    public void doingInResume() {
        showRecorder = false;
        showAppend = false;
        setSendText(R.string.ui_base_text_publish);
        super.doingInResume();
        setCustomTitle(R.string.ui_base_text_details);
        initializeAdapter();
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_normal_chatable;
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void onSwipeRefreshing() {
        if (null != praiseViewHolder) {
            praiseViewHolder.setHasShown(false);
        }
        setSupportLoadingMore(true);
        loadingLike(mAdapter.get(mQueryId));
        loadingComments(mAdapter.get(mQueryId));
    }

    @Override
    protected void onLoadingMore() {
        mAdapter.remove(noMore);
        loadingComments(mAdapter.get(mQueryId));
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    private ArrayList<ArchiveLike> likes = new ArrayList<>();

    @Override
    protected void onLoadingLikeComplete(boolean success, List<ArchiveLike> list) {
        if (success) {
            if (null != list) {
                likes.clear();
                likes.addAll(list);
            }
            checkPraises(likes.size() <= 0);
        }
    }

    private void removeComments() {
        Iterator<Model> iterator = mAdapter.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            // 移除旧列表里不在list中的记录
            Model model = iterator.next();
            if (model instanceof Comment) {
                iterator.remove();
                mAdapter.notifyItemRemoved(index);
            }
            index++;
        }
    }

    @Override
    protected void onLoadingCommentComplete(boolean success, List<Comment> list) {
        if (success) {
            if (null != list) {
                if (remotePageNumber <= 1) {
                    // 第一页时清空评论列表
                    removeComments();
                }
                for (Comment comment : list) {
                    mAdapter.update(comment);
                }
                mAdapter.update(noMore);
                smoothScrollToBottom(mAdapter.getItemCount() - 1);
            }
        }
    }

    @Override
    protected void onFetchingMomentComplete(Moment moment, boolean success) {
        if (success) {
            mMoment = moment;
            Collectable.resetMomentCollectionParams(moment);
            // 我发布的动态可以删除全部评论
            deletable = mMoment.isMine();
            // 拉取回来之后立即显示
            if (!mAdapter.exist(moment)) {
                mAdapter.add(moment, 0);
            } else {
                mAdapter.update(moment);
            }
            onSwipeRefreshing();
        }
    }

    private void checkPraises(boolean removable) {
        Model model = new Model();
        model.setId("praises");
        if (removable) {
            if (mAdapter.exist(model)) {
                mAdapter.remove(model);
            }
        } else {
            if (null != praiseViewHolder) {
                praiseViewHolder.setHasShown(false);
            }
            if (!mAdapter.exist(model)) {
                mAdapter.add(model, 1);
            } else {
                mAdapter.notifyItemChanged(mAdapter.indexOf(model));
            }
        }
    }

    private OnInputCompleteListener onInputCompleteListener = new OnInputCompleteListener() {
        @Override
        public void onInputComplete(String text, int length, int type) {
            if (!isEmpty(text)) {
                Model model = mAdapter.get(selectedComment);
                if (model instanceof Comment) {
                    // 回复评论或点击评论
                    Comment comment = (Comment) model;
                    comment(mMoment, text, comment.isMine() ? "" : comment.getUserId());
                } else {
                    // 直接发布评论
                    comment(mMoment, text, "");
                }
            }
        }
    };

    private void initializeAdapter() {
        if (null == mAdapter) {
            mRootView.setBackgroundColor(Color.WHITE);
            addOnInputCompleteListener(onInputCompleteListener);
            mAdapter = new MomentDetailsAdapter();
            mRecyclerView.setAdapter(mAdapter);
            fetchingMoment();
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ui_tooltip_menu_moment_comment:
                case R.id.ui_tooltip_menu_moment_comment1:
                    // 发表评论(不是回复别人)
                    selectedComment = 0;
                    replyName.setVisibility(View.GONE);
                    openKeyboard();
                    break;
                case R.id.ui_tooltip_menu_moment_praise:
                    praiseViewHolder.setHasShown(false);
                    like(mMoment);
                    break;
                case R.id.ui_tooltip_menu_moment_praised:
                    praiseViewHolder.setHasShown(false);
                    like(mMoment);
                    break;
            }
        }
    };

    private void openKeyboard() {
        _inputText.setFocusable(true);
        _inputText.setFocusableInTouchMode(true);
        _inputText.requestFocus();
        Utils.showInputBoard(_inputText);
    }

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            switch (view.getId()) {
                case R.id.ui_holder_view_moment_details_container:
                    // 这里已经是详情页，不再需要打开详情页了
                    break;
                case R.id.ui_holder_view_moment_details_more:
                    // 打开快捷赞、评论菜单
                    mSelectedIndex = index;
                    // 已赞和未赞
                    int layout = mMoment.isLiked() ? R.id.ui_tooltip_moment_comment_praised : R.id.ui_tooltip_moment_comment;
                    showTooltip(view, layout, true, TooltipHelper.TYPE_RIGHT, onClickListener);
                    break;
                case R.id.ui_holder_view_moment_comment_container:
                    // 回复别人的评论或删除自己的评论
                    selectedComment = index;
                    Comment comment = (Comment) mAdapter.get(index);
                    replyName.setVisibility(comment.isMine() ? View.GONE : View.VISIBLE);
                    replyName.setText(StringHelper.getString(R.string.ui_text_archive_details_comment_hint_to, comment.getUserName()));
                    openKeyboard();
                    break;
                case R.id.ui_holder_view_moment_comment_delete:
                    selectedComment = index;
                    openCommentDeleteDialog();
                    break;
            }
        }
    };

    private OnHandleBoundDataListener<Model> boundDataListener = new OnHandleBoundDataListener<Model>() {
        @Override
        public Model onHandlerBoundData(BaseViewHolder holder) {
            return mAdapter.get(holder.getAdapterPosition());
        }
    };

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Model model = mAdapter.get(index);
            if (model instanceof Comment) {
                UserPropertyFragment.open(MomentDetailsFragment.this, ((Comment) model).getUserId());
            }
        }
    };

    @Override
    protected void onDelayRefreshComplete(int type) {

    }

    @Override
    protected void onCommentDeleteDialogCanceled() {
        selectedComment = 0;
    }

    @Override
    protected void onCommentDeleteDialogConfirmed() {
        deleteComment(mMoment, mAdapter.get(selectedComment).getId());
    }

    @Override
    protected void onDeleteCommentComplete(boolean success, Model model) {
        if (success) {
            if (mAdapter.get(selectedComment) instanceof Comment) {
                mAdapter.remove(selectedComment);
            }
            selectedComment = 0;
        }
    }

    @Override
    protected void onLikeComplete(boolean success, Model model) {
        if (success) {
            mAdapter.update(model);
            likes.clear();
            likes.addAll(((Moment) model).getUserMmtLikeList());
            checkPraises(likes.size() <= 0);
        }
    }

    @Override
    protected void onCollectComplete(boolean success, Model model) {

    }

    @Override
    protected void onCommentComplete(boolean success, Comment comment, Model model) {
        if (success) {
            selectedComment = 0;
            // 需要加到“后面没有了“前面
            mAdapter.add(comment, mAdapter.getItemCount() - 1);
            smoothScrollToBottom(mAdapter.getItemCount() - 1);
            replyName.setVisibility(View.GONE);
            _inputText.setText("");
        }
    }

    private class MomentDetailsAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {

        private static final int VT_MOMENT = 0, VT_PRAISE = 1, VT_COMMENT = 2, VT_NOMORE = 3;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            switch (viewType) {
                case VT_MOMENT:
                    MomentDetailsViewHolder holder = new MomentDetailsViewHolder(itemView, MomentDetailsFragment.this);
                    holder.setOnViewHolderElementClickListener(elementClickListener);
                    holder.addOnHandlerBoundDataListener(boundDataListener);
                    holder.isShowLike(false);
                    return holder;
                case VT_PRAISE:
                    if (null == praiseViewHolder) {
                        praiseViewHolder = new MomentPraiseViewHolder(itemView, MomentDetailsFragment.this);
                        praiseViewHolder.showContent(likes);
                    }
                    return praiseViewHolder;
                case VT_NOMORE:
                    return new NothingMoreViewHolder(itemView, MomentDetailsFragment.this);
                default:
                    MomentCommentHeaderViewHolder mcv = new MomentCommentHeaderViewHolder(itemView, MomentDetailsFragment.this);
                    mcv.addOnViewHolderClickListener(onViewHolderClickListener);
                    mcv.setOnViewHolderElementClickListener(elementClickListener);
                    mcv.setDeletable(deletable);
                    return mcv;
            }
        }

        @Override
        public int getItemViewType(int position) {
            Model model = get(position);
            if (model instanceof Moment) {
                return VT_MOMENT;
            } else if (model instanceof Comment) {
                return VT_COMMENT;
            } else if (model.getId().equals(noMore.getId())) {
                return VT_NOMORE;
            }
            return VT_PRAISE;
        }

        @Override
        public int itemLayout(int viewType) {
            switch (viewType) {
                case VT_MOMENT:
                    return R.layout.holder_view_individual_moment_details;
                case VT_PRAISE:
                    return R.layout.holder_view_individual_moment_like_header;
                case VT_NOMORE:
                    return R.layout.holder_view_nothing_more;
                default:
                    return R.layout.holder_view_individual_moment_comment_header;
            }
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof MomentDetailsViewHolder) {
                ((MomentDetailsViewHolder) holder).showContent((Moment) item);
            } else if (holder instanceof MomentCommentHeaderViewHolder) {
                MomentCommentHeaderViewHolder hld = (MomentCommentHeaderViewHolder) holder;
                hld.showIcon(position == (likes.size() > 0 ? 2 : 1));
                hld.showContent((Comment) item);
            } else if (holder instanceof MomentPraiseViewHolder) {
                MomentPraiseViewHolder hd = (MomentPraiseViewHolder) holder;
                if (!hd.hasShown()) {
                    hd.showContent(likes);
                }
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
