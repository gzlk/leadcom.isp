package com.gzlk.android.isp.multitype.binder;

import android.support.annotation.NonNull;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.holder.FooterViewHolder;
import com.gzlk.android.isp.model.Footer;

/**
 * <b>功能描述：</b>加载更多的ViewHolder<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/26 22:12 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/26 22:12 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class FooterViewBinder extends BaseViewBinder<Footer, FooterViewHolder> {

    @Override
    protected int itemLayout() {
        return R.layout.tool_view_loading_more_item;
    }

    @Override
    public FooterViewHolder onCreateViewHolder(@NonNull View itemView) {
        return new FooterViewHolder(itemView, null);
    }

    @Override
    protected void onBindViewHolder(@NonNull FooterViewHolder holder, @NonNull Footer item) {

    }
}
