package com.gzlk.android.isp.lib.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.lib.layoutmanager.CustomLinearLayoutManager;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

/**
 * <b>功能描述：</b>实现上拉加载更多功能的RecyclerView<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/13 15:28 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/13 15:28 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class LoadMoreRecyclerView extends RecyclerView {

    public LoadMoreRecyclerView(Context context) {
        super(context);
        initialize();
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private boolean forceToLoadingMore = false;
    private View footerView;
    private CircleProgressBar loadingProgress;
    private TextView loadingTextView;

    private void initialize() {
        super.addOnScrollListener(mOnScrollListener);
    }

    private OnScrollListener mOnScrollListener = new OnScrollListener() {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            CustomLinearLayoutManager manager = (CustomLinearLayoutManager) recyclerView.getLayoutManager();
            // 停止滚动时
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                // 获取最后一个完全显示 Item 的 position
                int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                int totalItemCount = manager.getItemCount();
                // 判断是否滚动到底部，并且不在加载状态
                if (lastVisibleItem == (totalItemCount - 1) && !forceToLoadingMore) {
                    forceToLoadingMore = true;
                    loadingText(R.string.hlklib_text_refreshable_recycler_view_loading_more);
                    showView(loadingProgress, true);
                    showView(footerView, true);
                    if (null != mOnLoadingMoreListener) {
                        mOnLoadingMoreListener.onLoadingMore();
                    }
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };

    /**
     * 是否还需要继续加载更多
     *
     * @param complete true=已经加载完毕，false=还需要继续加载
     */

    public void isLoadingComplete(boolean complete) {
        if (!complete) {
            footerView.setVisibility(GONE);
        } else {
            loadingProgress.setVisibility(GONE);
            loadingText(R.string.hlklib_text_refreshable_recycler_view_loading_complete);
        }
        forceToLoadingMore = false;
    }

    private void showView(View view, boolean show) {
        if (null != view) {
            view.setVisibility(show ? VISIBLE : GONE);
        }
    }

    private void loadingText(int res) {
        if (null != loadingTextView) {
            loadingTextView.setText(res);
        }
    }

    private void findFooterViews() {
        loadingTextView = (TextView) footerView.findViewById(R.id.ui_refreshable_recycler_view_footer_text);
        loadingProgress = (CircleProgressBar) footerView.findViewById(R.id.ui_refreshable_recycler_view_footer_progress);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (!(adapter instanceof LoadingMoreAdapter)) {
            throw new IllegalArgumentException("You should extends your adapter of LoadMoreRecyclerView.LoadingMoreAdapter");
        }
        footerView = LayoutInflater.from(getContext()).inflate(R.layout.hlklib_refreshable_recycler_view_loading_more_item, this, false);
        findFooterViews();
        footerView.setVisibility(GONE);
        ((LoadingMoreAdapter) adapter).setFooterView(footerView);
        super.setAdapter(adapter);
    }

    public static abstract class LoadingMoreAdapter<VH extends ViewHolder> extends Adapter<VH> {

        private int VT_FOOTER = 999;
        private View footView;

        final void setFooterView(View view) {
            footView = view;
        }

        @Override
        public void onViewAttachedToWindow(VH holder) {
            super.onViewAttachedToWindow(holder);
            if (isFirstItemFullLine()) {
                // 第一行占满全屏
                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                if (params != null && params instanceof StaggeredGridLayoutManager.LayoutParams) {
                    StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) params;
                    p.setFullSpan(holder.getLayoutPosition() == 0);
                }
            }
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            if (isFirstItemFullLine()) {
                RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
                if (manager instanceof GridLayoutManager) {
                    final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                    gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            // 第一个item占满整行
                            return (position == 0) ? gridManager.getSpanCount() : 1;
                        }
                    });
                }
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VT_FOOTER) {
                return footerViewHolder(footView);
            }
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(itemLayout(viewType), parent, false);
            return onCreateViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            if (position < getItemCount() - 1) {
                onBindHolderOfView(holder, position);
            }
        }

        @Override
        public int getItemCount() {
            return gotItemCount() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getItemCount() - 1) {
                return VT_FOOTER;
            }
            return gotItemViewType(position);
        }

        /**
         * 数据集中数据个数
         */
        public abstract int gotItemCount();

        /**
         * 绑定数据
         */
        public abstract void onBindHolderOfView(VH holder, int position);

        /**
         * view type
         */
        public abstract int gotItemViewType(int position);

        /**
         * 创建foot view holder
         */
        public abstract VH footerViewHolder(View itemView);

        /**
         * 第一个item是否占满整行
         */
        public abstract boolean isFirstItemFullLine();

        /**
         * 创建ViewHolder
         */
        public abstract VH onCreateViewHolder(View itemView, int viewType);

        /**
         * 返回单个item的layout布局
         */
        public abstract int itemLayout(int viewType);

    }

    private OnLoadingMoreListener mOnLoadingMoreListener;

    /**
     * 添加加载更多事件处理回调
     */
    public void addOnLoadingMoreListener(OnLoadingMoreListener l) {
        mOnLoadingMoreListener = l;
    }

    /**
     * 加载更多的接口
     */
    public interface OnLoadingMoreListener {
        /**
         * 加载更多
         */
        void onLoadingMore();
    }
}
