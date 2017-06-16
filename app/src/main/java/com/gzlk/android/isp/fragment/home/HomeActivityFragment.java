package com.gzlk.android.isp.fragment.home;

import android.support.annotation.Nullable;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.activity.ActRequest;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.fragment.activity.ActivityEntranceFragment;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.holder.home.ActivityHomeViewHolder;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.activity.Activity;
import com.gzlk.android.isp.nim.session.NimSessionHelper;

import java.util.List;

/**
 * <b>功能描述：</b>首页活动<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/05 15:07 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/05 15:07 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class HomeActivityFragment extends BaseSwipeRefreshSupportFragment {

    private ActivityAdapter mAdapter;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        setNothingText(R.string.ui_text_home_activity_nothing);
        initializeAdapter();
    }

    @Override
    protected void onViewPagerDisplayedChanged(boolean visible) {
        super.onViewPagerDisplayedChanged(visible);
        if (visible) {
            fetchingPublicActivity();
        }
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
        fetchingPublicActivity();
    }

    @Override
    protected void onLoadingMore() {
        fetchingPublicActivity();
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    private void fetchingPublicActivity() {
        displayLoading(true);
        displayNothing(false);
        ActRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Activity>() {
            @Override
            public void onResponse(List<Activity> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    if (null != list) {
                        if (list.size() >= pageSize) {
                            remotePageNumber++;
                            isLoadingComplete(false);
                        } else {
                            isLoadingComplete(true);
                        }
                        initializeAdapter();
                        mAdapter.update(list, false);
                        //mAdapter.sort();
                    } else {
                        isLoadingComplete(true);
                    }
                } else {
                    isLoadingComplete(true);
                }
                displayNothing(mAdapter.getItemCount() < 1);
                displayLoading(false);
                stopRefreshing();
            }
        }).allOpenActivities(remotePageNumber);
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            // 到活动详情报名页
            Activity act = mAdapter.get(index);
            isJoinedPublicAct(act.getId(), act.getTid());
        }
    };

    private void isJoinedPublicAct(final String actId, final String tid) {
        ActRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Activity>() {
            @Override
            public void onResponse(Activity activity, boolean success, String message) {
                super.onResponse(activity, success, message);
                if (success) {
                    NimSessionHelper.startTeamSession(Activity(), tid);
                } else {
                    // 如果不在该群则打开报名页面
                    openActivity(ActivityEntranceFragment.class.getName(), format("%s,%s", actId, tid), true, false);
                }
            }
        }).isJoinPublicAct(actId);
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new ActivityAdapter();
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private class ActivityAdapter extends RecyclerViewAdapter<ActivityHomeViewHolder, Activity> {

        @Override
        public ActivityHomeViewHolder onCreateViewHolder(View itemView, int viewType) {
            ActivityHomeViewHolder holder = new ActivityHomeViewHolder(itemView, HomeActivityFragment.this);
            holder.addOnViewHolderClickListener(onViewHolderClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_home_seminar;
        }

        @Override
        public void onBindHolderOfView(ActivityHomeViewHolder holder, int position, @Nullable Activity item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(Activity item1, Activity item2) {
            return -item1.getCreateDate().compareTo(item2.getCreateDate());
        }
    }
}
