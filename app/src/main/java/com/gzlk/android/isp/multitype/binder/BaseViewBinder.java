package com.gzlk.android.isp.multitype.binder;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.holder.BaseViewHolder;

import java.lang.ref.SoftReference;

import me.drakeet.multitype.ItemViewBinder;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/20 22:38 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/20 22:38 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class BaseViewBinder<T, VH extends BaseViewHolder> extends ItemViewBinder<T, VH> {

    protected SoftReference<BaseFragment> fragment;

    /**
     * 设置宿主fragment，弱引用
     */
    public BaseViewBinder<T, VH> setFragment(BaseFragment fragment) {
        this.fragment = new SoftReference<BaseFragment>(fragment);
        return this;
    }

    @NonNull
    @Override
    protected VH onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(itemLayout(), parent, false);
        return onCreateViewHolder(view);
    }

    /**
     * 当前ViewHolder的layout
     */
    protected abstract int itemLayout();

    public abstract VH onCreateViewHolder(@NonNull View itemView);
}
