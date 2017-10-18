package com.gzlk.android.isp.holder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.LogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.listener.OnHandleBoundDataListener;
import com.gzlk.android.isp.listener.OnViewHolderElementClickListener;
import com.hlk.hlklib.etc.Utility;

import java.lang.ref.SoftReference;

/**
 * <b>功能：</b>RecyclerViewHolder基类<br />
 * <b>作者：</b>Hsiang Leekwok <br />
 * <b>时间：</b>2015/12/30 14:39 <br />
 * <b>邮箱：</b>xiang.l.g@gmail.com <br />
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {

    private SoftReference<BaseFragment> fragment;

    /**
     * 当前 holder 的 tag 对象
     */
    private Object mTag;

    public BaseViewHolder(View itemView, BaseFragment fragment) {
        super(itemView);
        if (null != fragment) {
            this.fragment = new SoftReference<>(fragment);
        }
    }

    protected boolean isEmpty(String text) {
        return StringHelper.isEmpty(text);
    }

    protected boolean multiSelectable = false;

    /**
     * 设置是否可多选
     */
    public void setMultiSelectable(boolean selectable) {
        multiSelectable = selectable;
    }

    /**
     * attach
     */
    public void attachedFromWindow() {
    }

    /**
     * detach
     */
    public void detachedFromWindow() {
    }

    protected void log(String text) {
        LogHelper.log(this.getClass().getSimpleName(), text);
    }

    protected String format(String format, Object... args) {
        return StringHelper.format(format, args);
    }

    protected int getColor(int res) {
        return fragment.get().getColor(res);
    }

    protected int getDimension(int res) {
        return fragment.get().getDimension(res);
    }

    protected void openActivity(String fullClassName, String params, boolean supportToolbar, boolean supportBackKey) {
        openActivity(fullClassName, params, BaseFragment.ACTIVITY_BASE_REQUEST, supportToolbar, supportBackKey);
    }

    protected void openActivity(String fullClassName, String params, int requestCode, boolean supportToolbar, boolean supportBackKey) {
        openActivity(fullClassName, params, requestCode, supportToolbar, supportBackKey, false);
    }

    protected void openActivity(String fullClassName, String params, boolean supportToolbar, boolean supportBackKey, boolean transparentStatusBar) {
        openActivity(fullClassName, params, BaseFragment.ACTIVITY_BASE_REQUEST, supportToolbar, supportBackKey, transparentStatusBar);
    }

    protected void openActivity(String fullClassName, String params, int requestCode, boolean supportToolbar, boolean supportBackKey, boolean transparentStatusBar) {
        fragment.get().openActivity(fullClassName, params, requestCode, supportToolbar, supportBackKey, transparentStatusBar);
    }

    protected BaseFragment fragment() {
        return this.fragment.get();
    }

    protected Handler Handler() {
        return this.fragment.get().Handler();
    }

    protected Context getContext() {
        return fragment.get().Activity();
    }

    public void setTag(Object tag) {
        mTag = tag;
    }

    public Object getTag() {
        return mTag;
    }

    protected String getSearchingText(String text, String searchingText) {
        if (!StringHelper.isEmpty(searchingText)) {
            if (!isEmpty(text)) {
                assert text != null;
                return Utility.addColor(text, searchingText, getColor(R.color.colorAccent));
            }
        }
        return text;
    }

    /**
     * 为CardView当作跟View的UI布局演示点击动画
     */
    protected void startRootViewClickEffect() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(itemView, "translationZ", 20, 0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                onRootViewClickEffectComplete();
            }
        });
        animator.start();
    }

    /**
     * 根CardView的点击动画执行完毕
     */
    protected void onRootViewClickEffectComplete() {
    }

    /**
     * 添加长按处理回调
     */
    public void addOnLongClickListener(View.OnLongClickListener l) {
    }

    /**
     * 保存暂存数据到bundle中
     */
    public void saveParamsToBundle(Bundle bundle) {
    }

    /**
     * 从bundle中恢复数据
     */
    public void getParamsFromBundle(Bundle bundle) {
    }

    protected OnViewHolderClickListener mOnViewHolderClickListener;

    /**
     * 添加ViewHolder点击处理回调
     */
    public void addOnViewHolderClickListener(OnViewHolderClickListener l) {
        mOnViewHolderClickListener = l;
    }

    /**
     * 获取当前正在处理的数据对象的回调
     */
    protected OnHandleBoundDataListener mOnHandlerBoundDataListener;

    /**
     * 添加数据处理回调
     */
    public void addOnHandlerBoundDataListener(OnHandleBoundDataListener l) {
        mOnHandlerBoundDataListener = l;
    }

    protected OnViewHolderElementClickListener mOnViewHolderElementClickListener;

    /**
     * 添加元素点击事件处理回调
     */
    public void setOnViewHolderElementClickListener(OnViewHolderElementClickListener l) {
        mOnViewHolderElementClickListener = l;
    }
}
