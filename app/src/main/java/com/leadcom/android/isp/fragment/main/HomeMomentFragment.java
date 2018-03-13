package com.leadcom.android.isp.fragment.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.fragment.base.BaseCmtLikeColFragment;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.common.NothingMoreViewHolder;
import com.leadcom.android.isp.holder.individual.MomentCommentTextViewHolder;
import com.leadcom.android.isp.holder.individual.MomentDetailsViewHolder;
import com.leadcom.android.isp.holder.individual.MomentHomeCameraViewHolder;
import com.leadcom.android.isp.listener.OnHandleBoundDataListener;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.Comment;
import com.leadcom.android.isp.model.user.Moment;

import java.util.Iterator;


/**
 * <b>功能描述：</b>首页 - 动态（已关注组织内的个人公开动态列表）<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/13 20:45 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class HomeMomentFragment extends BaseCmtLikeColFragment {

    private Model nothingMore;
    private MomentAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nothingMore = Model.getNoMore();
    }

    @Override
    protected void onDelayRefreshComplete(int type) {

    }

    @Override
    public void doingInResume() {

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

    private OnViewHolderElementClickListener onViewHolderElementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {

        }
    };

    private OnHandleBoundDataListener<Model> momentBoundDataListener = new OnHandleBoundDataListener<Model>() {
        @Override
        public Model onHandlerBoundData(BaseViewHolder holder) {
            return mAdapter.get(holder.getAdapterPosition());
        }
    };

    @Override
    protected void onCommentComplete(boolean success, Comment comment, Model model) {
        if (success) {
            appendMoment((Moment) model);
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

    private class MomentAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {

        private static final int VT_MOMENT = 0, VT_COMMENT = 1, VT_NO_MORE = 2, VT_CAMERA = 3;

        @Override
        public int getItemViewType(int position) {
            Model model = get(position);
            if (model.getId().contains("no_more_any_things")) {
                return VT_NO_MORE;
            } else if (model.getId().contains("moment")) {
                return VT_CAMERA;
            }
            return (model instanceof Comment ? VT_COMMENT : VT_MOMENT);
        }

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            BaseFragment fragment = HomeMomentFragment.this;
            switch (viewType) {
                case VT_MOMENT:
                    MomentDetailsViewHolder mdvh = new MomentDetailsViewHolder(itemView, fragment);
                    mdvh.setOnViewHolderElementClickListener(onViewHolderElementClickListener);
                    mdvh.addOnHandlerBoundDataListener(momentBoundDataListener);
                    mdvh.setToDetails(true);
                    mdvh.isShowLike(true);
                    return mdvh;
                case VT_COMMENT:
                    MomentCommentTextViewHolder mctvh = new MomentCommentTextViewHolder(itemView, fragment);
                    mctvh.setOnViewHolderElementClickListener(onViewHolderElementClickListener);
                    return mctvh;
                case VT_NO_MORE:
                    return new NothingMoreViewHolder(itemView, fragment);
                case VT_CAMERA:
                    MomentHomeCameraViewHolder mhcvh = new MomentHomeCameraViewHolder(itemView, fragment);
                    mhcvh.setOnViewHolderElementClickListener(onViewHolderElementClickListener);
                    return mhcvh;
            }
            return null;
        }

        @Override
        public int itemLayout(int viewType) {
            switch (viewType) {
                case VT_MOMENT:
                    return R.layout.holder_view_individual_moment_details;
                case VT_COMMENT:
                    return R.layout.holder_view_individual_moment_comment_name;
                case VT_CAMERA:
                    return R.layout.holder_view_individual_moment_camera;
                default:
                    return R.layout.holder_view_nothing_more;
            }
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {

        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
