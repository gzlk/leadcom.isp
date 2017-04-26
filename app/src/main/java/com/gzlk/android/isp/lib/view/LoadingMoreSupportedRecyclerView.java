package com.gzlk.android.isp.lib.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.helper.LogHelper;
import com.gzlk.android.isp.holder.FooterViewHolder;
import com.gzlk.android.isp.model.Footer;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.multitype.adapter.BaseMultiTypeAdapter;
import com.gzlk.android.isp.multitype.binder.FooterViewBinder;
import com.hlk.hlklib.layoutmanager.CustomLinearLayoutManager;
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

public class LoadingMoreSupportedRecyclerView extends RecyclerView {

    public LoadingMoreSupportedRecyclerView(Context context) {
        super(context);
        initialize();
    }

    public LoadingMoreSupportedRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public LoadingMoreSupportedRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private boolean forceToLoadingMore = false;
    private boolean supportLoadingMore = true;
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
            // 如果用户设置了不需要加载更多，则此时直接不用处理后续的判断
            if (!supportLoadingMore) {
                return;
            }
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
     * 设置是否支持滚动到底部的时候加载更多
     */
    public void setSupportLoadingMore(boolean loadingMore) {
        supportLoadingMore = loadingMore;
        if (null != loadingMoreAdapter) {
            loadingMoreAdapter.setSupportLoadingMore(supportLoadingMore);
        }
    }

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
        loadingTextView = (TextView) footerView.findViewById(R.id.ui_tool_view_loading_more_text);
        loadingProgress = (CircleProgressBar) footerView.findViewById(R.id.ui_tool_view_loading_more_progress);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        footerView = LayoutInflater.from(getContext()).inflate(R.layout.tool_view_loading_more_item, this, false);
        findFooterViews();
        footerView.setVisibility(GONE);
        if (!(adapter instanceof LoadingMoreAdapter)) {
            LogHelper.log("LoadingMore", "You should extends your adapter of LoadMoreRecyclerView.LoadingMoreAdapter");
        }
        super.setAdapter(adapter);
    }

    private LoadingMoreAdapter loadingMoreAdapter;

    public abstract static class LoadingMoreAdapter<VH extends ViewHolder, T extends Model> extends BaseMultiTypeAdapter<T> {

        private int VIEW_TYPE_FOOTER = 999;
        private View footView;
        private boolean supportLoadingMore = true;
        private FooterViewBinder footerViewBinder = new FooterViewBinder();

        public LoadingMoreAdapter() {
            super();
            register(Footer.class, footerViewBinder);
        }

        //        final void setFooterView(View view) {
//            footView = view;
//        }
//
        final void setSupportLoadingMore(boolean support) {
            supportLoadingMore = support;
        }

//        @Override
//        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
//            if (viewType == VIEW_TYPE_FOOTER) {
//                VH holder = footerViewHolder(footView);
//                if (null == holder) {
//                    throw new IllegalArgumentException("no footer view holder presented.");
//                }
//                return holder;
//            } else {
//                return super.onCreateViewHolder(parent, viewType);
//            }
//        }

//        @Override
//        public void onBindViewHolder(VH holder, int position) {
//            if (!supportLoadingMore) {
//                // 不支持加载更多时，直接绑定holder
//                onBindHolderOfView(holder, position, null);
//            } else {
//                //if (position < getItemCount() - 1) {
//                //    if (position < super.getItemCount()) {
//                super.onBindViewHolder(holder, position);
//                //    } else {
//                //        onBindHolderOfView(holder, position, null);
//                //    }
//                //}
//            }
//        }

//        @Override
//        public int getItemCount() {
//            return super.getItemCount() + (supportLoadingMore ? 1 : 0);
//        }
//
//        @Override
//        public int getItemViewType(int position) {
//            if (supportLoadingMore) {
//                if (position == getItemCount() - 1) {
//                    return VIEW_TYPE_FOOTER;
//                }
//            }
//            return gotItemViewType(position);
//        }

        /**
         * view type
         */
//        public abstract int gotItemViewType(int position);

        /**
         * 创建foot view holder
         */
//        public abstract VH footerViewHolder(View itemView);

        /**
         * 创建ViewHolder
         */
//        public abstract VH onCreateViewHolder(View itemView, int viewType);
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
