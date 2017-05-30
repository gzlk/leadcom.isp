package com.gzlk.android.isp.multitype.binder;

import android.support.annotation.NonNull;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.holder.common.SimpleClickableViewHolder;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.Model;

import java.lang.ref.SoftReference;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/02 10:31 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/02 10:31 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SimpleClickableViewBinder extends BaseViewBinder<Model, SimpleClickableViewHolder> {

    private SoftReference<OnViewHolderClickListener> click;

    public SimpleClickableViewBinder(OnViewHolderClickListener l) {
        super();
        click = new SoftReference<>(l);
    }

    @Override
    protected int itemLayout() {
        return R.layout.holder_view_simple_clickable;
    }

    @Override
    public SimpleClickableViewHolder onCreateViewHolder(@NonNull View itemView) {
        SimpleClickableViewHolder holder = new SimpleClickableViewHolder(itemView, fragment.get());
        holder.addOnViewHolderClickListener(click.get());
        return holder;
    }

    @Override
    protected void onBindViewHolder(@NonNull SimpleClickableViewHolder holder, @NonNull Model item) {
        holder.showContent(item);
    }
}
