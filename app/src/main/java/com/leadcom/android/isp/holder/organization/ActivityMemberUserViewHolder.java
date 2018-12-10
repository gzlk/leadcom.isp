package com.leadcom.android.isp.holder.organization;

import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.model.organization.Member;

import java.util.Calendar;


/**
 * <b>功能描述：</b>活动统计中成员信息<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/12/10 11:48 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/12/10 11:48  <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ActivityMemberUserViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_activity_member_user_name)
    private TextView nameView;
    @ViewId(R.id.ui_holder_view_activity_member_user_status)
    private TextView statusView;
    @ViewId(R.id.ui_holder_view_activity_member_user_status1)
    private TextView statusView1;
    @ViewId(R.id.ui_holder_view_activity_member_user_status2)
    private TextView statusView2;
    @ViewId(R.id.ui_holder_view_activity_member_user_status3)
    private TextView statusView3;
    @ViewId(R.id.ui_holder_view_activity_member_user_time)
    private TextView timeView;

    public ActivityMemberUserViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(Member member) {
        nameView.setText(member.getUserName());
        statusView.setText(member.getStatus());
        statusView.setTextColor(getColor(member.isJoined() ? R.color.colorPrimary : (member.isLeaved() ? R.color.colorCaution : R.color.textColorHint)));

        statusView1.setText(member.getList().get(0));
        statusView2.setText(member.getList().get(1));
        statusView3.setText(member.getList().get(2));

        String date = Utils.format("yyyy", Calendar.getInstance().getTime());
        String that = !member.isCreateDateDefault() ? member.getCreateDate().substring(0, 4) : "";
        timeView.setText(member.isCreateDateDefault() ? "-" : fragment().formatDate(member.getCreateDate(), (that.equals(date) ? "MM-dd" : "yyyy\nMM-dd")));
    }
}
