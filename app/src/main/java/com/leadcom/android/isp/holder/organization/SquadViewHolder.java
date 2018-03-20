package com.leadcom.android.isp.holder.organization;

import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.model.organization.Squad;

/**
 * <b>功能描述：</b>小组的ViewHolder<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/19 15:47 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/03/19 15:47 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SquadViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_group_squad_name)
    private TextView nameView;
    @ViewId(R.id.ui_holder_view_group_squad_members)
    private TextView numberView;

    public SquadViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(Squad squad) {
        nameView.setText(squad.getName());
        numberView.setText(format("%d人", squad.isSelectable() ? Integer.valueOf(squad.getAccessToken()) : squad.getMemberNum()));
    }

    @Click({R.id.ui_holder_view_group_squad_container})
    private void viewClick(View view) {
        if (null != mOnViewHolderClickListener) {
            mOnViewHolderClickListener.onClick(getAdapterPosition());
        }
    }
}
