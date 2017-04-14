package com.gzlk.android.isp.fragment.base;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.lib.layoutmanager.CustomLinearLayoutManager;
import com.gzlk.android.isp.lib.view.LoadingMoreSupportedRecyclerView;
import com.hlk.hlklib.lib.inject.ViewId;

/**
 * <b>功能描述：</b>支持下拉刷新功能的fragment基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/13 15:08 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/13 15:08 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class BaseSwipeRefreshSupportFragment extends BaseDelayRefreshSupportFragment {

    @ViewId(R.id.ui_tool_swipe_refreshable_swipe_refresh_layout)
    public SwipeRefreshLayout mSwipeRefreshLayout;
    @ViewId(R.id.ui_tool_swipe_refreshable_recycler_view)
    public LoadingMoreSupportedRecyclerView mRecyclerView;

    /**
     * 是否自动初始化RecyclerView的LayoutManager
     */
    protected boolean isAutoLayoutManager = true;

    /**
     * 设置是否允许下拉刷新
     */
    protected void enableSwipe(boolean enabled) {
        if (null != mSwipeRefreshLayout) {
            mSwipeRefreshLayout.setEnabled(enabled);
        }
    }

    @Override
    public int getLayout() {
        return R.layout.tool_view_swipe_refreshable_recycler_view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (null != mRecyclerView && isAutoLayoutManager) {
            mRecyclerView.setLayoutManager(new CustomLinearLayoutManager(mRecyclerView.getContext()));
        }
        initRefreshableItems();
    }

    protected void initRefreshableItems() {
        if (null != mSwipeRefreshLayout) {
            mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                    android.R.color.holo_red_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_green_light);
            //mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
            mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.addOnLoadingMoreListener(loadingMoreListener);
            registerForContextMenu(mRecyclerView);
        }
    }

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {

        @Override
        public void onRefresh() {
            onSwipeRefreshing();
        }
    };

    private LoadingMoreSupportedRecyclerView.OnLoadingMoreListener loadingMoreListener = new LoadingMoreSupportedRecyclerView.OnLoadingMoreListener() {
        @Override
        public void onLoadingMore() {
            BaseSwipeRefreshSupportFragment.this.onLoadingMore();
        }
    };

    /**
     * 设置数据是否已加载完毕
     */
    protected void isLoadingComplete(boolean complete) {
        mRecyclerView.isLoadingComplete(complete);
    }

    /**
     * 下拉刷新
     */
    protected abstract void onSwipeRefreshing();

    /**
     * 加载更多
     */
    protected abstract void onLoadingMore();

    /**
     * 退出刷新状态
     */
    protected void stopRefreshing() {
        if (null != mSwipeRefreshLayout) {
            Handler().post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    /**
     * 设置刷新状态
     */
    protected void refreshing() {
        if (null != mSwipeRefreshLayout) {
            Handler().post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
        }
    }
}
