package com.gzlk.android.isp.holder;

import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewUtility;

/**
 * <b>功能描述：</b>组织架构里添加小组<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/07 23:00 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/07 23:00 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SquadAddViewHolder extends BaseViewHolder {

    public SquadAddViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    @Click(R.id.ui_holder_view_squad_add_container)
    private void click(View view) {
        if (null != mOnViewHolderClickListener) {
            mOnViewHolderClickListener.onClick(-2);
        }
    }
}
