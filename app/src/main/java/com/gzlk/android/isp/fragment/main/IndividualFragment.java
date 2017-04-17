package com.gzlk.android.isp.fragment.main;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.fragment.individual.MomentDetailsFragment;
import com.gzlk.android.isp.fragment.individual.MomentNewFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.HorizontalRecyclerViewHolder;
import com.gzlk.android.isp.holder.IndividualHeaderViewHolder;
import com.gzlk.android.isp.holder.MomentViewHolder;
import com.gzlk.android.isp.holder.TextViewHolder;
import com.gzlk.android.isp.lib.view.LoadingMoreSupportedRecyclerView;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.listener.RecycleAdapter;

import java.lang.ref.SoftReference;
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

    private static final String PARAM_SHOWN = "title_bar_shown";

    private String[] functions;
    private String[] test = new String[]{"", "", "测试2", "测试3", "测试4", "测试5", "测试6", "测试7",
            "测试8", "测试9", "测试10", "测试11", "测试12", "测试13", "测试14", "测试15", "测试16", "测试17", "测试18"};

    private List<String> data = new ArrayList<>();

    private boolean isTitleBarShown = false;

    /**
     * 标题栏是否已经显示了
     */
    public boolean isTitleBarShown() {
        return isTitleBarShown;
    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    protected void onSwipeRefreshing() {
        Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.add("下拉刷新的" + (data.size() + 1), 2);
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
        super.getParamsFromBundle(bundle);
        isTitleBarShown = bundle.getBoolean(PARAM_SHOWN, false);
    }

    @Override
    public void doingInResume() {
        // 这里不缓存选择了的图片，选择了一张图片之后就立即打开新发布窗口
        isSupportCacheSelected = false;
        initializeTest();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        bundle.putBoolean(PARAM_SHOWN, isTitleBarShown);
        super.saveParamsToBundle(bundle);
    }

    @Override
    protected void destroyView() {

    }

    private SoftReference<View> toolBarView;

    public IndividualFragment setToolBar(View view) {
        if (null == toolBarView || null == toolBarView.get()) {
            toolBarView = new SoftReference<>(view);
        }
        return this;
    }

    private SoftReference<View> textView;

    public void setToolBarTextView(View view) {
        if (null == textView || null == textView.get()) {
            textView = new SoftReference<>(view);
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
            if (scrolledY >= 0 && scrolledY <= 500) {
                float alpha = scrolledY * 0.005f;
                if (null != toolBarView && null != toolBarView.get()) {
                    toolBarView.get().setAlpha(alpha);
                    isTitleBarShown = toolBarView.get().getAlpha() >= 1;
                }
                if (null != textView && null != textView.get()) {
                    textView.get().setAlpha(alpha);
                }
            }
        }
    };

    private void initializeTest() {
        if (null == mAdapter) {
            // 这里不需要直接上传，只需要把选择的图片传递给新建动态页面即可，上传在那里实现
            isSupportDirectlyUpload = false;
            // 添加图片选择
            addOnImageSelectedListener(imageSelectedListener);
            functions = Activity().getResources().getStringArray(R.array.ui_individual_functions);
            mAdapter = new TestAdapter();
            mRecyclerView.addOnScrollListener(scrollListener);
            mRecyclerView.setAdapter(mAdapter);
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

    private class TestAdapter extends LoadingMoreSupportedRecyclerView.LoadingMoreAdapter<BaseViewHolder> implements RecycleAdapter<String> {

        private static final int VT_HEADER = 0, VT_FUNCTIONS = 1, VT_MOMENT = 2;

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
        public void remove(String item) {
            remove(data.indexOf(item));
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
            switch (viewType) {
                case VT_HEADER:
                    tryPaddingContent(itemView, true);
                    return new IndividualHeaderViewHolder(itemView, IndividualFragment.this);
                case VT_FUNCTIONS:
                    HorizontalRecyclerViewHolder holder = new HorizontalRecyclerViewHolder(itemView, IndividualFragment.this);
                    holder.addOnViewHolderClickListener(horizontalHolderClickListener);
                    // 默认选中动态选项
                    holder.setSelectedIndex(0);
                    holder.displaySelectedEffect(true);
                    holder.setDataSources(functions);
                    return holder;
                default:
                    MomentViewHolder mvh = new MomentViewHolder(itemView, IndividualFragment.this);
                    mvh.addOnViewHolderClickListener(momentHolderClickListener);
                    return mvh;
            }
        }

        @Override
        public int itemLayout(int viewType) {
            switch (viewType) {
                case VT_HEADER:
                    return R.layout.holder_view_individual_header;
                case VT_FUNCTIONS:
                    return R.layout.holder_view_individual_functions;
                default:
                    return R.layout.holder_view_moment;
            }
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position) {
            if (holder instanceof HorizontalRecyclerViewHolder) {
                ((HorizontalRecyclerViewHolder) holder).displayItems();
            } else if (holder instanceof MomentViewHolder) {
                MomentViewHolder moment = (MomentViewHolder) holder;
                moment.setAsToday(position == 2);
                moment.showContent();
            }
        }

        @Override
        public int gotItemCount() {
            return data.size();
        }

        @Override
        public int gotItemViewType(int position) {
            switch (position) {
                case 0:
                    return VT_HEADER;
                case 1:
                    return VT_FUNCTIONS;
                default:
                    return VT_MOMENT;
            }
        }

        @Override
        public BaseViewHolder footerViewHolder(View itemView) {
            return new TextViewHolder(itemView, IndividualFragment.this);
        }
    }

    private OnViewHolderClickListener horizontalHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            ToastHelper.make(Activity()).showMsg(functions[index].substring(2));
            openActivity(MomentNewFragment.class.getName(), "", true, true);
        }
    };

    private OnViewHolderClickListener momentHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            switch (index) {
                case 0:
                case 1:
                    break;
                case 2:
                    openImageSelector();
                    break;
                default:
                    openActivity(MomentDetailsFragment.class.getName(), "", false, false, true);
                    break;
            }
        }
    };

    private OnImageSelectedListener imageSelectedListener = new OnImageSelectedListener() {
        @Override
        public void onImageSelected(String compressed) {
            if (!StringHelper.isEmpty(compressed)) {
                // 打开新建动态页面
                openActivity(MomentNewFragment.class.getName(), compressed, true, true);
            }
        }
    };
}
