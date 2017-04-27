package com.gzlk.android.isp.multitype.binder.user;

import android.support.annotation.NonNull;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.DocumentViewHolder;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.user.document.Document;
import com.gzlk.android.isp.multitype.binder.BaseViewBinder;

import java.lang.ref.SoftReference;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/27 00:25 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/27 00:25 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class DocumentViewBinder extends BaseViewBinder<Document, DocumentViewHolder> {

    private SoftReference<BaseViewHolder.OnHandlerBoundDataListener<Model>> click;

    public DocumentViewBinder(BaseViewHolder.OnHandlerBoundDataListener<Model> l) {
        super();
        click = new SoftReference<>(l);
    }

    @Override
    protected int itemLayout() {
        return R.layout.holder_view_document;
    }

    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull View itemView) {
        DocumentViewHolder holder=new DocumentViewHolder(itemView,fragment.get());
        holder.addOnHandlerBoundDataListener(click.get());
        return holder;
    }

    @Override
    protected void onBindViewHolder(@NonNull DocumentViewHolder holder, @NonNull Document item) {
        holder.showContent(item);
    }
}
