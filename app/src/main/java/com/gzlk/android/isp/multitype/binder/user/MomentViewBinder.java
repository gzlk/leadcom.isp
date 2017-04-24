package com.gzlk.android.isp.multitype.binder.user;

import android.support.annotation.NonNull;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.MomentViewHolder;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.user.moment.Moment;
import com.gzlk.android.isp.multitype.binder.BaseViewBinder;

import java.lang.ref.SoftReference;

/**
 * <b>功能描述：</b>说说的内容框架<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/21 01:07 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/21 01:07 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MomentViewBinder extends BaseViewBinder<Moment, MomentViewHolder> {

    private SoftReference<BaseViewHolder.OnHandlerBoundDataListener<Model>> click;

    public MomentViewBinder(BaseViewHolder.OnHandlerBoundDataListener<Model> l) {
        super();
        click = new SoftReference<>(l);
    }

    private SoftReference<MomentViewHolder.OnGotPositionListener> gotPosition;

    public MomentViewBinder addOnGotPositionListener(MomentViewHolder.OnGotPositionListener l) {
        gotPosition = new SoftReference<>(l);
        return this;
    }

    @Override
    protected int itemLayout() {
        return R.layout.holder_view_moment;
    }

    @Override
    public MomentViewHolder onCreateViewHolder(@NonNull View itemView) {
        MomentViewHolder holder = new MomentViewHolder(itemView, fragment.get());
        holder.addOnHandlerBoundDataListener(click.get());
        holder.addOnGotPositionListener(gotPosition.get());
        return holder;
    }

    @Override
    protected void onBindViewHolder(@NonNull MomentViewHolder holder, @NonNull Moment item) {
        holder.showContent(item);
    }
}
