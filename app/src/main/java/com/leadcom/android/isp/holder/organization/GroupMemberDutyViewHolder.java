package com.leadcom.android.isp.holder.organization;

import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.model.user.MemberDuty;


/**
 * <b>功能描述：</b>组织成员履职统计内容<br />
 * <b>创建作者：</b>SYSTEM <br />
 * <b>创建时间：</b>2018/08/14 22:03 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class GroupMemberDutyViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_group_member_duty_count_name)
    private TextView nameView;
    @ViewId(R.id.ui_holder_view_group_member_duty_count_archive)
    private TextView archiveView;
    @ViewId(R.id.ui_holder_view_group_member_duty_count_activity)
    private TextView activityView;

    public GroupMemberDutyViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(MemberDuty duty) {
        nameView.setText(isEmpty(duty.getUserName()) ? duty.getSquadName() : duty.getUserName());
        nameView.setSelected(true);
        archiveView.setText(String.valueOf(duty.getDocNum()));
        activityView.setText(String.valueOf(duty.getActivityNum()));
    }

    @Click({R.id.ui_holder_view_group_member_duty_count_archive, R.id.ui_holder_view_group_member_duty_count_activity})
    private void viewClick(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        }
    }
}
