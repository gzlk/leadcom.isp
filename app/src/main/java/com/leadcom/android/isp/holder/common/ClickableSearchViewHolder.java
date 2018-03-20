package com.leadcom.android.isp.holder.common;

import android.view.View;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.BaseViewHolder;

/**
 * <b>功能描述：</b>只可点击的搜索框<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/19 15:27 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/03/19 15:27 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ClickableSearchViewHolder extends BaseViewHolder {

    public ClickableSearchViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    @Click({R.id.ui_holder_view_searchable_icon_container})
    private void viewClick(View view) {
        if (null != mOnViewHolderClickListener) {
            mOnViewHolderClickListener.onClick(getAdapterPosition());
        }
    }
}
