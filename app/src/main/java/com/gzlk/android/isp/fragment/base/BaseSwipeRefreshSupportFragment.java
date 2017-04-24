package com.gzlk.android.isp.fragment.base;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.lib.view.LoadingMoreSupportedRecyclerView;
import com.hlk.hlklib.layoutmanager.CustomGridLayoutManager;
import com.hlk.hlklib.layoutmanager.CustomLinearLayoutManager;
import com.hlk.hlklib.layoutmanager.CustomStaggeredGridLayoutManager;
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

    private static final String PARAM_TYPE = "_bsrsf_layout_type";
    private static final String PARAM_SPAN_COUNT = "bsrsf_layout_span_count";
    private static final String PARAM_ORIENTATION = "bsrsf_layout_orientation";
    /**
     * 普通顺序列表
     */
    public static final int TYPE_LINEAR = 0;
    /**
     * 网格列表
     */
    public static final int TYPE_GRID = 1;
    /**
     * 混合列表
     */
    public static final int TYPE_SGRID = 2;

    /**
     * RecyclerView的列表方式
     */
    @IntDef({TYPE_LINEAR, TYPE_GRID, TYPE_SGRID})
    public @interface LayoutType {
    }

    /**
     * 默认显示列数
     */
    private static final int DFT_SPAN_COUNT = 4;
    /**
     * 默认RecyclerView为直接排列列表方式
     */
    protected int layoutType = TYPE_LINEAR;
    /**
     * 网格列表默认每行4列
     */
    protected int gridSpanCount = DFT_SPAN_COUNT;
    /**
     * 横向排列
     */
    protected int gridOrientation = 0;

    @Override
    public int getLayout() {
        return R.layout.tool_view_recycler_view_swipe_refreshable;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (null != mRecyclerView && isAutoLayoutManager) {
            mRecyclerView.setLayoutManager(getLayoutManager());
        }
        initRefreshableItems();
    }

    private RecyclerView.LayoutManager getLayoutManager() {
        switch (layoutType) {
            case TYPE_GRID:
                return new CustomGridLayoutManager(mRecyclerView.getContext(), gridSpanCount);
            case TYPE_SGRID:
                return new CustomStaggeredGridLayoutManager(gridSpanCount, gridOrientation);
            default:
                return new CustomLinearLayoutManager(mRecyclerView.getContext());
        }
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        layoutType = bundle.getInt(PARAM_TYPE, TYPE_LINEAR);
        gridSpanCount = bundle.getInt(PARAM_SPAN_COUNT, DFT_SPAN_COUNT);
        gridOrientation = bundle.getInt(PARAM_ORIENTATION, 0);
        localPageNumber = bundle.getInt(PARAM_LOCAL_PAGE_NUM, 0);
        localPageCount = bundle.getInt(PARAM_LOCAL_PAGE_TOTAL, 0);
        remotePageNumber = bundle.getInt(PARAM_REMOTE_PAGE_NUM, 0);
        remoteTotalPages = bundle.getInt(PARAM_REMOTE_PAGE_TOTAL, 0);
        super.getParamsFromBundle(bundle);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        bundle.putInt(PARAM_TYPE, layoutType);
        bundle.putInt(PARAM_ORIENTATION, gridOrientation);
        bundle.putInt(PARAM_SPAN_COUNT, gridSpanCount);
        bundle.putInt(PARAM_LOCAL_PAGE_NUM, localPageNumber);
        bundle.putInt(PARAM_LOCAL_PAGE_TOTAL, localPageCount);
        bundle.putInt(PARAM_REMOTE_PAGE_NUM, remotePageNumber);
        bundle.putInt(PARAM_REMOTE_PAGE_TOTAL, remoteTotalPages);
        super.saveParamsToBundle(bundle);
    }

    protected void initRefreshableItems() {
        if (null != mSwipeRefreshLayout) {
            mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                    android.R.color.holo_red_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_green_light);
            //mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
            mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        }
        if (null != mRecyclerView) {
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
        if (null != mRecyclerView) {
            mRecyclerView.isLoadingComplete(complete);
        }
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

    private static final String PARAM_LOCAL_PAGE_NUM = "bsrsf_local_page_nul";
    private static final String PARAM_LOCAL_PAGE_TOTAL = "bsrsf_local_total_pages";
    private static final String PARAM_REMOTE_PAGE_NUM = "bsrsf_remote_page_num";
    private static final String PARAM_REMOTE_PAGE_TOTAL = "bsrsf_remote_total_pages";
    /**
     * 本地显示的页码
     */
    protected int localPageNumber = 0;
    /**
     * 本地页码数量
     */
    protected int localPageCount = 0;
    /**
     * 远程获取的页码
     */
    protected int remotePageNumber = 0;
    /**
     * 远程页码总数
     */
    protected int remoteTotalPages = 0;
    /**
     * 每页大小
     */
    protected static int PAGE_SIZE = 10;
}
