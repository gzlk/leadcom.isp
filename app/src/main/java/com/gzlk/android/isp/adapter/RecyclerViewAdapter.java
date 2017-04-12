package com.gzlk.android.isp.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gzlk.android.isp.holder.BaseViewHolder;

/**
 * <b>功能描述：</b>RecyclerView的适配器，提供添加删除功能<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2016/12/22 13:19 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2016/12/22 13:19 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public abstract class RecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    /**
     * 清除列表
     */
    public abstract void clear();

    /**
     * 删除指定位置的item
     */
    public abstract void remove(int position);

    /**
     * 在列表末尾添加一个item
     */
    public abstract void add(Object object);

    /**
     * 在指定位置添加一个item
     */
    public abstract void add(Object object, int position);

    /**
     * 当前列表中是否存在指定值的item(value可以指定为任意字段的值)
     */
    public abstract boolean exist(Object value);

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

    @Override
    public void onViewAttachedToWindow(VH holder) {
        super.onViewAttachedToWindow((VH) holder);
        if (isFirstItemFullLine()) {
            // 第一行占满全屏
            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
            if (params != null && params instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) params;
                p.setFullSpan(holder.getLayoutPosition() == 0);
            }
        }
        if (holder instanceof BaseViewHolder) {
            ((BaseViewHolder) holder).attachedFromWindow();
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

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(itemLayout(viewType), parent, false);
        return onCreateViewHolder(view, viewType);
    }

    @Override
    public void onViewDetachedFromWindow(VH holder) {
        if (holder instanceof BaseViewHolder) {
            ((BaseViewHolder) holder).detachedFromWindow();
        }
        super.onViewDetachedFromWindow(holder);
    }
}
