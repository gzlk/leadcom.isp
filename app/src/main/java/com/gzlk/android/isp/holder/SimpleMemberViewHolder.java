package com.gzlk.android.isp.holder;

import android.view.View;

import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.model.ListItem;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/08 10:47 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/08 10:47 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SimpleMemberViewHolder extends SimpleClickableViewHolder {

    public SimpleMemberViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
    }

    @Override
    public void showContent(ListItem item) {
        super.showContent(item);
    }
}
