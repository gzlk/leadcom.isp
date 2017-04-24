package com.gzlk.android.isp.fragment.main;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.listener.OnRequestListListener;
import com.gzlk.android.isp.api.user.MomentRequest;
import com.gzlk.android.isp.application.App;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.fragment.individual.MomentDetailsFragment;
import com.gzlk.android.isp.fragment.individual.MomentNewFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.IndividualFunctionViewHolder;
import com.gzlk.android.isp.holder.IndividualHeaderViewHolder;
import com.gzlk.android.isp.holder.MomentViewHolder;
import com.gzlk.android.isp.holder.TextViewHolder;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.lib.view.LoadingMoreSupportedRecyclerView;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.listener.RecycleAdapter;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.user.moment.Moment;
import com.litesuits.orm.db.assit.QueryBuilder;

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
    private static final String PARAM_SELECTED = "function_selected";

    private boolean isTitleBarShown = false;
    private int selectedFunction = 0;

    private List<Moment> moments = new ArrayList<>();

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
        refreshingComment();
    }

    @Override
    protected void onLoadingMore() {
        refreshingMore();
    }

    /**
     * 拉取我的最新说说列表
     */
    @SuppressWarnings("ConstantConditions")
    private void refreshingComment() {
        MomentRequest.request().setOnRequestListListener(new OnRequestListListener<Moment>() {

            @SuppressWarnings("unchecked")
            @Override
            public void onResponse(List<Moment> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    if (list.size() > 0) {
                        new Dao<>(Moment.class).save(list);
                        // 下拉刷新的时候需要清空已显示的记录列表
                        //mAdapter.update((List<Model>) (Object) list);
                        //appendListHeader();
                    }
                }
                stopRefreshing();
            }
        }).list(App.app().UserId());
    }

    /**
     * 加载更多
     */
    @SuppressWarnings("ConstantConditions")
    private void refreshingMore() {
        QueryBuilder<Moment> builder = new QueryBuilder<>(Moment.class);
        builder.whereEquals(Model.Field.UserId, App.app().UserId())
                .appendOrderDescBy(Model.Field.CreateDate)
                .limit(localPageNumber, PAGE_SIZE);
        List<Moment> more = new Dao<>(Moment.class).query(builder);
        if (null != more) {
            if (more.size() >= PAGE_SIZE) {
                // 取出了一整页则对应的本地页码增1，下次会获取下一页
                localPageNumber++;
                // 还未加载完，下次还要继续加载下一页
                isLoadingComplete(false);
            } else {
                // 没有更多了
                isLoadingComplete(true);
            }
            mAdapter.add(more, false);
        }
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        isTitleBarShown = bundle.getBoolean(PARAM_SHOWN, false);
        selectedFunction = bundle.getInt(PARAM_SELECTED, 0);
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
        bundle.putInt(PARAM_SELECTED, selectedFunction);
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
            mAdapter = new TestAdapter();
            mRecyclerView.addOnScrollListener(scrollListener);
            mRecyclerView.setAdapter(mAdapter);
            resetData();
        }
    }

    private void resetData() {
        mAdapter.update(moments);
    }

    private TestAdapter mAdapter;

    private class TestAdapter extends LoadingMoreSupportedRecyclerView.LoadingMoreAdapter<BaseViewHolder, Moment>
            implements RecycleAdapter<Moment> {

        private static final int VT_HEADER = 0, VT_FUNCTIONS = 1, VT_MOMENT = 2, VT_DOCUMENT = 3, VT_FAVORITE = 4;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            switch (viewType) {
                case VT_HEADER:
                    tryPaddingContent(itemView, true);
                    return new IndividualHeaderViewHolder(itemView, IndividualFragment.this);
                case VT_FUNCTIONS:
                    IndividualFunctionViewHolder holder = new IndividualFunctionViewHolder(itemView, IndividualFragment.this);
                    holder.addOnFunctionChangeListener(functionChangeListener);
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
                    return R.layout.holder_view_individual_main_functions;
                default:
                    return R.layout.holder_view_moment;
            }
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, Moment item) {
            if (holder instanceof IndividualFunctionViewHolder) {
                ((IndividualFunctionViewHolder) holder).setSelected(selectedFunction);
            } else if (holder instanceof MomentViewHolder) {
                MomentViewHolder moment = (MomentViewHolder) holder;
                //moment.setAsToday(position == 2);
            }
        }

        @Override
        protected int comparator(Moment item1, Moment item2) {
            // 按照创建时间倒序排序
            int compared = item1.getCreateDate().compareTo(item2.getCreateDate());
            return compared == 0 ? 0 : -compared;
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

    private IndividualFunctionViewHolder.OnFunctionChangeListener functionChangeListener = new IndividualFunctionViewHolder.OnFunctionChangeListener() {
        @Override
        public void onChange(int index) {
            if (selectedFunction != index) {
                selectedFunction = index;
                // 拉取不同类型的数据并显示
                ToastHelper.make().showMsg(StringHelper.getStringArray(R.array.ui_individual_functions)[selectedFunction].replaceAll("\\d\\|", ""));
            }
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
        public void onImageSelected(ArrayList<String> selected) {
            // 打开新建动态页面
            openActivity(MomentNewFragment.class.getName(), Json.gson().toJson(selected), true, true);
        }
    };
}
