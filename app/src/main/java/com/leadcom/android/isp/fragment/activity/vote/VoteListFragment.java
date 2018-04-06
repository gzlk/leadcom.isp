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
import com.leadcom.android.isp.holder.activity.VoteViewHolder;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.activity.vote.AppVote;
import com.leadcom.android.isp.view.SwipeItemLayout;

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

public class VoteListFragment extends BaseVoteFragment {

    public static VoteListFragment newInstance(Bundle bundle) {
        VoteListFragment vlf = new VoteListFragment();
        vlf.setArguments(bundle);
        return vlf;
    }

    public static void open(BaseFragment fragment, String tid) {
        fragment.openActivity(VoteListFragment.class.getName(), getBundle(tid), true, false);
    }

    public static void open(Context context, int requestCode, String tid) {
        BaseActivity.openActivity(context, VoteListFragment.class.getName(), getBundle(tid), requestCode, true, false);
    }

    private VoteAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRootView.setBackgroundColor(getColor(R.color.windowBackground));
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
    protected void onSwipeRefreshing() {
        remotePageNumber = 1;
        loadingVote();
    }

    @Override
    protected void onLoadingMore() {
        loadingVote();
    }

    private void loadingVote() {
        displayLoading(true);
        displayNothing(false);
        AppVoteRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<AppVote>() {
            @Override
            public void onResponse(List<AppVote> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (remotePageNumber <= 1) {
                    mAdapter.clear();
                }
                int size = null == list ? 0 : list.size();
                isLoadingComplete(size < pageSize);
                remotePageNumber += size < pageSize ? 0 : 1;
                if (success && null != list) {
                    mAdapter.update(list, false);
                }
                stopRefreshing();
                displayLoading(false);
                displayNothing(mAdapter.getItemCount() < 1);
            }
        }).listTeamVotes(mQueryId, AppVoteRequest.LIST_ALL, remotePageNumber);
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            setLoadingText(R.string.ui_activity_vote_list_loading_vote);
            setNothingText(R.string.ui_activity_vote_list_no_vote);
            mAdapter = new VoteAdapter();
            mRecyclerView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(Activity()));
            mRecyclerView.setAdapter(mAdapter);
            loadingVote();
        }
    }

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            switch (view.getId()) {
                case R.id.ui_holder_view_activity_vote_item:
                    VoteDetailsFragment.open(VoteListFragment.this, mQueryId, mAdapter.get(index).getId());
                    break;
                case R.id.ui_tool_view_contact_button2:
                    warningDelete(mAdapter.get(index).getId());
                    break;
            }
        }
    };

    private class VoteAdapter extends RecyclerViewAdapter<VoteViewHolder, AppVote> {

        @Override
        public VoteViewHolder onCreateViewHolder(View itemView, int viewType) {
            VoteViewHolder holder = new VoteViewHolder(itemView, VoteListFragment.this);
            holder.setOnViewHolderElementClickListener(elementClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return isSelfOwner ? R.layout.holder_view_activity_vote_item_deletable : R.layout.holder_view_activity_vote_item;
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
