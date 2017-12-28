package com.leadcom.android.isp.holder.activity;

import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.netease.nimlib.sdk.team.model.Team;


/**
 * <b>功能描述：</b>分享时的群聊列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/12/27 20:54 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ActivityListItemViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_nim_activity_list_item_image)
    private ImageDisplayer iconView;
    @ViewId(R.id.ui_nim_activity_list_item_name)
    private TextView nameView;

    public ActivityListItemViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(Team team) {
        iconView.displayImage(team.getIcon(), getDimension(R.dimen.ui_base_dimen_button_height), false, false);
        nameView.setText(team.getName());
    }

    @Click({R.id.ui_nim_activity_list_item})
    private void click(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        }
    }
}
