package com.leadcom.android.isp.fragment.activity.vote;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.activity.AppVoteRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.holder.activity.VoteViewHolder;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.activity.Activity;
import com.leadcom.android.isp.model.activity.vote.AppVote;

import java.util.List;

/**
 * <b>功能描述：</b>投票列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/29 00:21 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/29 00:21 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class VoteListFragment extends BaseSwipeRefreshSupportFragment {

    public static VoteListFragment newInstance(String params) {
        VoteListFragment vlf = new VoteListFragment();
        Bundle bundle = new Bundle();
        // tid
        bundle.putString(PARAM_QUERY_ID, params);
        vlf.setArguments(bundle);
        return vlf;
    }

    public static void open(BaseFragment fragment, String tid) {
        fragment.openActivity(VoteListFragment.class.getName(), tid, true, false);
    }

    public static void open(Context context, int requestCode, String tid) {
        BaseActivity.openActivity(context, VoteListFragment.class.getName(), tid, requestCode, true, false);
    }

    private String activityId = "";
    private VoteAdapter mAdapter;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_nim_action_vote);
        setRightText(R.string.ui_activity_vote_publish_text);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                resultSucceededActivity();
            }
        });
        initializeAdapter();
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_top_paddingable_swipe_recycler_view;
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
        loadingVote();
    }

    @Override
    protected void onLoadingMore() {
        loadingVote();
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    private void fetchingActivity() {
        if (isEmpty(activityId)) {
            Activity act = Activity.getByTid(mQueryId);
            if (null != act) {
                activityId = act.getId();
            }
        }
    }

    private void loadingVote() {
        displayLoading(true);
        displayNothing(false);
        AppVoteRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<AppVote>() {
            @Override
            public void onResponse(List<AppVote> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    if (remotePageNumber <= 1) {
                        mAdapter.clear();
                    }
                    if (null != list) {
                        if (list.size() >= pageSize) {
                            remotePageNumber++;
                            isLoadingComplete(false);
                        } else {
                            isLoadingComplete(true);
                        }
                        mAdapter.update(list, false);
                    } else {
                        isLoadingComplete(true);
                    }
                } else {
                    isLoadingComplete(true);
                }
                stopRefreshing();
                displayLoading(false);
                displayNothing(mAdapter.getItemCount() < 1);
            }
        }).list(activityId, AppVoteRequest.LIST_ALL, remotePageNumber);
    }

    private void initializeAdapter() {
        fetchingActivity();
        if (null == mAdapter) {
            setLoadingText(R.string.ui_activity_vote_list_loading_vote);
            setNothingText(R.string.ui_activity_vote_list_no_vote);
            mAdapter = new VoteAdapter();
            mRecyclerView.setAdapter(mAdapter);
            loadingVote();
        }
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            VoteDetailsFragment.open(VoteListFragment.this, mQueryId, mAdapter.get(index).getId());
        }
    };

    private class VoteAdapter extends RecyclerViewAdapter<VoteViewHolder, AppVote> {

        @Override
        public VoteViewHolder onCreateViewHolder(View itemView, int viewType) {
            VoteViewHolder holder = new VoteViewHolder(itemView, VoteListFragment.this);
            holder.addOnViewHolderClickListener(onViewHolderClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_activity_vote_item;
        }

        @Override
        public void onBindHolderOfView(VoteViewHolder holder, int position, @Nullable AppVote item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(AppVote item1, AppVote item2) {
            return 0;
        }
    }
}
