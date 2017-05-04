package com.gzlk.android.isp.multitype.binder.user;

import android.support.annotation.NonNull;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.CollectionItemViewHolder;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.user.Collection;
import com.gzlk.android.isp.multitype.binder.BaseViewBinder;

import java.lang.ref.SoftReference;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/05 00:44 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/05 00:44 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class CollectionViewBinder extends BaseViewBinder<Collection, CollectionItemViewHolder> {

    private SoftReference<BaseViewHolder.OnHandlerBoundDataListener<Model>> click;

    public CollectionViewBinder(BaseViewHolder.OnHandlerBoundDataListener<Model> l) {
        super();
        click = new SoftReference<>(l);
    }

    @Override
    protected int itemLayout() {
        return R.layout.holder_view_collection;
    }

    @Override
    public CollectionItemViewHolder onCreateViewHolder(@NonNull View itemView) {
        CollectionItemViewHolder holder = new CollectionItemViewHolder(itemView, fragment.get());
        holder.addOnHandlerBoundDataListener(click.get());
        return holder;
    }

    @Override
    protected void onBindViewHolder(@NonNull CollectionItemViewHolder holder, @NonNull Collection item) {
        holder.showContent(item);
    }
}
