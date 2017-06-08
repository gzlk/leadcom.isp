package com.gzlk.android.isp.multitype.binder;

import android.support.annotation.NonNull;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.holder.individual.IndividualFunctionViewHolder;
import com.gzlk.android.isp.model.Model;

import java.lang.ref.SoftReference;

/**
 * <b>功能描述：</b>主页个人中功能选项<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/20 23:09 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/20 23:09 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class IndividualFunctionalViewBinder extends BaseViewBinder<Model, IndividualFunctionViewHolder> {

    private SoftReference<IndividualFunctionViewHolder.OnFunctionChangeListener> listener;

    public IndividualFunctionalViewBinder(IndividualFunctionViewHolder.OnFunctionChangeListener l) {
        super();
        listener = new SoftReference<>(l);
    }

    @Override
    protected void onBindViewHolder(@NonNull IndividualFunctionViewHolder holder, @NonNull Model item) {
        //holder.setSelected(0);
    }

    @Override
    protected int itemLayout() {
        return R.layout.holder_view_individual_main_functions;
    }

    @Override
    public IndividualFunctionViewHolder onCreateViewHolder(@NonNull View itemView) {
        IndividualFunctionViewHolder holder = new IndividualFunctionViewHolder(itemView, fragment.get());
        // 初始化选中第一个
        holder.setSelected(0);
        holder.addOnFunctionChangeListener(listener.get());
        return holder;
    }
}
