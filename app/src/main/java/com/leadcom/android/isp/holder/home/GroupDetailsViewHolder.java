package com.leadcom.android.isp.holder.home;

import android.view.View;

import com.hlk.hlklib.lib.inject.Click;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.common.SimpleClickableViewHolder;
import com.leadcom.android.isp.model.common.SimpleClickableItem;


/**
 * <b>功能描述：</b>组织的详细统计信息<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/16 20:44 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class GroupDetailsViewHolder extends SimpleClickableViewHolder {

    public GroupDetailsViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
    }

    @Override
    public void showContent(SimpleClickableItem item) {
        super.showContent(item);
        Integer i = Integer.decode(item.getIcon());
        valueIcon.setText(String.valueOf((char) i.intValue()));
        valueIcon.setVisibility(View.VISIBLE);
    }

    @Click({R.id.ui_holder_view_simple_clickable})
    @Override
    public void click(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, index);
        }
    }
}
