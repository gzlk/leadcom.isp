package com.gzlk.android.isp.fragment.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.helper.PreferenceHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.hlk.hlklib.etc.Cryptography;
import com.hlk.hlklib.layoutmanager.CustomGridLayoutManager;
import com.hlk.hlklib.layoutmanager.CustomLinearLayoutManager;
import com.hlk.hlklib.layoutmanager.CustomStaggeredGridLayoutManager;
import com.hlk.hlklib.lib.inject.ViewId;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

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
    public RecyclerView mRecyclerView;

    @ViewId(R.id.ui_tool_view_loading_more_layout)
    public View mLoadingMoreLayout;
    @ViewId(R.id.ui_tool_view_loading_more_progress)
    public CircleProgressBar mLoadingMoreProgress;
    @ViewId(R.id.ui_tool_view_loading_more_text)
    public TextView mLoadingMoreTextView;

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
    protected int gridOrientation = StaggeredGridLayoutManager.HORIZONTAL;

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
        remotePageNumber = bundle.getInt(PARAM_REMOTE_PAGE_NUM, 1);
        remoteTotalPages = bundle.getInt(PARAM_REMOTE_PAGE_TOTAL, 0);
        remotePageSize = bundle.getInt(PARAM_REMOTE_PAGE_SIZE, 0);
        remoteTotalCount = bundle.getInt(PARAM_REMOTE_TOTAL_COUNT, 0);
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
        bundle.putInt(PARAM_REMOTE_PAGE_SIZE, remotePageSize);
        bundle.putInt(PARAM_REMOTE_TOTAL_COUNT, remoteTotalCount);
        super.saveParamsToBundle(bundle);
    }

    protected void initRefreshableItems() {
        if (null != mSwipeRefreshLayout) {
            mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                    android.R.color.holo_red_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_green_light);
            mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        }
        if (null != mRecyclerView) {
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.addOnScrollListener(mOnScrollListener);
            registerForContextMenu(mRecyclerView);
        }
    }

    private boolean forceToLoadingMore = false;
    private boolean supportLoadingMore = true;

    /**
     * 设置是否支持加载更多
     */
    protected void setSupportLoadingMore(boolean support) {
        supportLoadingMore = support;
    }

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            // 如果用户设置了不需要加载更多，则此时直接不用处理后续的判断
            if (!supportLoadingMore) {
                return;
            }
            // 停止滚动时
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
                int totalItemCount = lm.getItemCount();
                int lastVisibleItem;

                // 获取最后一个完全显示 Item 的 position
                if (lm instanceof StaggeredGridLayoutManager) {
                    lastVisibleItem = ((StaggeredGridLayoutManager) lm).findLastCompletelyVisibleItemPositions(null)[0];
                } else {
                    lastVisibleItem = ((LinearLayoutManager) lm).findLastCompletelyVisibleItemPosition();
                }

                // 判断是否滚动到底部，并且不在加载状态
                if (lastVisibleItem == (totalItemCount - 1) && !forceToLoadingMore) {
                    forceToLoadingMore = true;
                    loadingText(R.string.hlklib_text_refreshable_recycler_view_loading_more);
                    showView(mLoadingMoreProgress, true);
                    showLoadingMoreLayout(true);
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };

    private void loadingText(int res) {
        if (null != mLoadingMoreTextView) {
            mLoadingMoreTextView.setText(res);
        }
    }

    /**
     * 是否还需要继续加载更多
     *
     * @param complete true=已经加载完毕，false=还需要继续加载
     */

    public void isLoadingComplete(boolean complete) {
        if (!complete) {
            // 未加载完毕时暂时先隐藏加载UI，等下次在滚动到底部的时候显示
            showLoadingMoreLayout(false);
            delayToHideLoadingMoreLayout();
        } else {
            showView(mLoadingMoreProgress, false);
            loadingText(R.string.hlklib_text_refreshable_recycler_view_loading_complete);
            supportLoadingMore = false;
            delayToHideLoadingMoreLayout();
        }
        forceToLoadingMore = false;
    }

    private void delayToHideLoadingMoreLayout() {
        Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showLoadingMoreLayout(false);
            }
        }, 3000);
    }

    protected void showLoadingMoreLayout(final boolean shown) {
        if (null == mLoadingMoreLayout) {
            return;
        }
        mLoadingMoreLayout.animate()
                .alpha(shown ? 1 : 0)
                .setDuration(duration())
                .translationY(shown ? 0 : mLoadingMoreLayout.getHeight())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (!shown) {
                            mLoadingMoreLayout.setVisibility(View.GONE);
                        } else {
                            // 显示UI动画完毕时触发加载更多事件
                            onLoadingMore();
                        }
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        if (shown) {
                            mLoadingMoreLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }).start();
    }

    private void showView(View view, boolean show) {
        if (null != view) {
            view.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {

        @Override
        public void onRefresh() {
            // 下拉刷新的时候再次允许加载更多
            //supportLoadingMore = true;
            onSwipeRefreshing();
        }
    };

    /**
     * 列表滚动到最后一条记录
     */
    public void smoothScrollToBottom(final int position) {
        if (position < 0) return;
        if (null != mRecyclerView) {
            Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRecyclerView.smoothScrollToPosition(position);
                }
            }, 100);
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
            if (!mSwipeRefreshLayout.isRefreshing()) {
                Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                        //onSwipeRefreshing();
                    }
                });
            }
        }
    }

    private static final String PARAM_LOCAL_PAGE_NUM = "bsrsf_local_page_nul";
    private static final String PARAM_LOCAL_PAGE_TOTAL = "bsrsf_local_total_pages";
    private static final String PARAM_REMOTE_PAGE_NUM = "bsrsf_remote_page_num";
    private static final String PARAM_REMOTE_PAGE_TOTAL = "bsrsf_remote_total_pages";
    private static final String PARAM_REMOTE_PAGE_SIZE = "bsrsf_remote_page_size";
    private static final String PARAM_REMOTE_TOTAL_COUNT = "bsrsf_remote_total_count";
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
    protected int remotePageNumber = 1;
    /**
     * 远程页总数
     */
    protected int remoteTotalPages = 1;
    /**
     * 远程页大小
     */
    protected int remotePageSize = 0;
    /**
     * 远程总条数
     */
    protected int remoteTotalCount = 0;
    /**
     * 每页大小
     */
    protected static int PAGE_SIZE = 10;

    /**
     * 默认刷新时间间隔，10分钟
     */
    protected static final int REFRESHING_INTERVAL = 10 * 60 * 1000;

    /**
     * 本地页码缓存标记
     */
    protected abstract String getLocalPageTag();

    /**
     * 查看缓存中指定的页面是否可以自动刷新
     */
    protected boolean isNeedRefresh() {
        if (StringHelper.isEmpty(getLocalPageTag())) {
            throw new IllegalArgumentException("no page refresh tag exists.");
        }
        String md5 = Cryptography.md5(getLocalPageTag());
        long timestamp = Utils.timestamp();
        long value = Long.valueOf(PreferenceHelper.get(md5, "0"));
        if ((value + REFRESHING_INTERVAL) < timestamp) {
            PreferenceHelper.save(md5, String.valueOf(timestamp));
            return true;
        }
        return false;
    }
}
