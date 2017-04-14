package com.gzlk.android.isp.fragment.main;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.IndividualHeaderViewHolder;
import com.gzlk.android.isp.holder.TextViewHolder;
import com.gzlk.android.isp.lib.view.LoadMoreRecyclerView;
import com.gzlk.android.isp.listener.RecycleAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能描述：</b>个人<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/13 10:03 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/13 10:03 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class IndividualFragment extends BaseSwipeRefreshSupportFragment {

    private String[] test = new String[]{"", "测试1", "测试2", "测试3", "测试4", "测试5", "测试6", "测试7",
            "测试8", "测试9", "测试10", "测试11", "测试12", "测试13", "测试14", "测试15", "测试16", "测试17", "测试18"};

    private List<String> data = new ArrayList<>();

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public int getLayout() {
        return R.layout.fragment_main_individual;
    }

    @Override
    protected void onSwipeRefreshing() {
        Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.add("下拉刷新的" + (data.size() + 1), 1);
                stopRefreshing();
            }
        }, 1000);
    }

    @Override
    protected void onLoadingMore() {
        Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (data.size() > 30) {
                    isLoadingComplete(true);
                } else {
                    mAdapter.add("测试" + (data.size() + 1));
                    isLoadingComplete(false);
                }
            }
        }, 1000);
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {

    }

    @Override
    public void doingInResume() {
        initializeTest();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {

    }

    @Override
    protected void destroyView() {

    }

    private View toolBarView;

    public void setToolBar(View view) {
        if (null == toolBarView) {
            toolBarView = view;
        }
    }

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        private int scrolledY = 0;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (!isViewPagerDisplayedCurrent()) {
                return;
            }
            scrolledY += dy;
            if (scrolledY >= 0 && scrolledY <= 255) {
                if (null != toolBarView) {
                    toolBarView.setAlpha(scrolledY * 0.005f);
                }
            }
            log(format("on scrolled dx: %d, dy: %d, scrolled: %d", dx, dy, scrolledY));
        }
    };

    private void initializeTest() {
        if (null == mAdapter) {
            mAdapter = new TestAdapter();
            mRecyclerView.addOnScrollListener(scrollListener);

            LoadMoreRecyclerView lmrv = (LoadMoreRecyclerView) mRecyclerView;
            lmrv.setAdapter(mAdapter);
            resetData();
        }
    }

    private void resetData() {
        mAdapter.clear();
        for (String string : test) {
            mAdapter.add(string);
        }
    }

    private TestAdapter mAdapter;

    private class TestAdapter extends LoadMoreRecyclerView.LoadingMoreAdapter<BaseViewHolder> implements RecycleAdapter<String> {

        int VT_HEADER = 0, VT_NORMAL = 1;

        @Override
        public void clear() {
            int size = data.size();
            while (size > 0) {
                remove(size - 1);
                size = data.size();
            }
        }

        @Override
        public void remove(int position) {
            data.remove(position);
            notifyItemRemoved(position);
        }

        @Override
        public void add(String object) {
            data.add(object);
            notifyItemInserted(data.size() - 1);
        }

        @Override
        public void add(String object, int position) {
            data.add(position, object);
            notifyItemInserted(position);
        }

        @Override
        public boolean exist(String value) {
            return false;
        }

        @Override
        public boolean isFirstItemFullLine() {
            return false;
        }

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            if (viewType == VT_NORMAL) {
                return new TextViewHolder(itemView, IndividualFragment.this);
            } else {
                tryPaddingContent(itemView, true);
                return new IndividualHeaderViewHolder(itemView, IndividualFragment.this);
            }
        }

        @Override
        public int itemLayout(int viewType) {
            return viewType == VT_HEADER ? R.layout.tool_view_individual_header : R.layout.holder_view_text_olny;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position) {
            if (holder instanceof TextViewHolder) {
                ((TextViewHolder) holder).showContent(data.get(position));
            }
        }

        @Override
        public int gotItemCount() {
            return data.size();
        }

        @Override
        public int gotItemViewType(int position) {
            if (position == 0) {
                return VT_HEADER;
            }
            return VT_NORMAL;
        }

        @Override
        public BaseViewHolder footerViewHolder(View itemView) {
            return new TextViewHolder(itemView, IndividualFragment.this);
        }
    }
}
