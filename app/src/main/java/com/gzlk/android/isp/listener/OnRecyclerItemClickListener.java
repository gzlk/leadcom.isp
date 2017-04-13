package com.gzlk.android.isp.listener;

import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * <b>功能描述：</b>RecyclerView item 的点击处理<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/12 23:08 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/12 23:08 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class OnRecyclerItemClickListener<VH extends RecyclerView.ViewHolder> implements RecyclerView.OnItemTouchListener {

    private GestureDetectorCompat mGestureDetectorCompat;
    private RecyclerView mRecyclerView;

    public OnRecyclerItemClickListener(@NonNull RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        mGestureDetectorCompat = new GestureDetectorCompat(recyclerView.getContext(), new ItemTouchHelperGestureListener());
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetectorCompat.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetectorCompat.onTouchEvent(e);
    }

    /**
     * RecyclerView item 的点击事件
     */
    public abstract void onItemClick(VH holder);

    /**
     * RecyclerView item 的长按事件
     */
    public abstract void onItemLongPress(VH holder);

    private class ItemTouchHelperGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            View child = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            if (null != child) {
                RecyclerView.ViewHolder vh = mRecyclerView.getChildViewHolder(child);
                onItemClick((VH) vh);
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            View child = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            if (null != child) {
                RecyclerView.ViewHolder vh = mRecyclerView.getChildViewHolder(child);
                onItemLongPress((VH) vh);
            }
        }
    }
}
