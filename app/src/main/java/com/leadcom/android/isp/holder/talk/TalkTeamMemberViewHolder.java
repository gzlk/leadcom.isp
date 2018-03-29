package com.leadcom.android.isp.holder.talk;

import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;

/**
 * <b>功能描述：</b>群成员ViewHolder<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/29 16:18 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/03/29 16:18 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class TalkTeamMemberViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_talk_team_member_head)
    private ImageDisplayer headView;
    @ViewId(R.id.ui_holder_view_talk_team_member_name)
    private TextView nameView;

    public TalkTeamMemberViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this,itemView);
    }

    public void showContent(){}
}
