package com.leadcom.android.isp.holder.organization;

import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.organization.ActSquad;
import com.leadcom.android.isp.model.organization.Concern;
import com.leadcom.android.isp.model.organization.Member;


/**
 * <b>功能描述：</b>活动成员报名统计列表Item<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/09/23 19:01 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ActivityMemberItemViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_activity_member_item_top_line)
    private View topLine;
    @ViewId(R.id.ui_holder_view_activity_member_item_text)
    private TextView textView;
    @ViewId(R.id.ui_holder_view_activity_member_item_count)
    private TextView countView;
    @ViewId(R.id.ui_holder_view_activity_member_item_status)
    private TextView statusView;
    @ViewId(R.id.ui_holder_view_activity_member_item_time)
    private TextView timeView;
    @ViewId(R.id.ui_holder_view_activity_member_item_right_icon)
    private View iconView;

    public ActivityMemberItemViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(Model model) {
        if (model instanceof Archive)
            showContent((Archive) model);
        else if (model instanceof ActSquad)
            showContent((ActSquad) model);
        else if (model instanceof Member)
            showContent((Member) model);
        else if (model instanceof Concern) {
            showContent((Concern) model);
        }
    }

    private void showContent(Archive archive) {
        topLine.setVisibility(View.GONE);
        textView.setText(archive.getCountResult());
        textView.setTextColor(getColor(R.color.colorPrimary));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimension(R.dimen.ui_base_text_size_small));
        countView.setVisibility(View.GONE);
        statusView.setVisibility(View.GONE);
        timeView.setVisibility(View.GONE);
        iconView.setVisibility(View.GONE);
    }

    private void showContent(ActSquad squad) {
        topLine.setVisibility(View.VISIBLE);
        textView.setText(Html.fromHtml(format("<b>%s</b>" + (squad.getSquadId().equals("0") ? "" : "(<font color=\"#a1a1a1\">支部</font>)"), squad.getSquadName())));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimension(R.dimen.ui_base_text_size));
        textView.setTextColor(getColor(R.color.textColor));
        countView.setVisibility(View.GONE);
        statusView.setVisibility(View.GONE);
        timeView.setVisibility(View.GONE);
        iconView.setVisibility(View.GONE);
    }

    private void showContent(Member member) {
        topLine.setVisibility(View.GONE);
        boolean isGroup = !isEmpty(member.getGroupId());
        textView.setText(isGroup ? member.getGroupName() : member.getUserName());
        textView.setTextColor(getColor(R.color.textColor));
        textView.setSelected(true);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimension(R.dimen.ui_base_text_size));
        countView.setVisibility(isGroup ? View.VISIBLE : View.GONE);
        countView.setText(format("报名%d", member.getReportNum()));
        statusView.setText(Html.fromHtml(isGroup ? format("请假%d", member.getLeaveNum()) : getStatus(member)));
        statusView.setVisibility(View.VISIBLE);
        statusView.setSelected(true);
        statusView.setGravity(isGroup ? Gravity.CENTER_HORIZONTAL : Gravity.CENTER_VERTICAL);
        resetWeight(isGroup);
        timeView.setVisibility(isGroup ? View.GONE : View.VISIBLE);
        timeView.setText(member.isCreateDateDefault() ? "-" : fragment().formatDate(member.getCreateDate(), R.string.ui_base_text_date_format));
        iconView.setVisibility(isGroup ? View.VISIBLE : View.GONE);
    }

    private void resetWeight(boolean isGroup) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) statusView.getLayoutParams();
        params.weight = isGroup ? 0.8f : 2f;
        statusView.setLayoutParams(params);

        params = (LinearLayout.LayoutParams) textView.getLayoutParams();
        params.weight = isGroup ? 1.8f : 0.8f;
        textView.setLayoutParams(params);
    }

    private String getStatus(Member member) {
        String string = member.getStatus();
        if (member.getList().size() > 0) {
            String sel = "";
            for (String text : member.getList()) {
                if (!isEmpty(text) && !sel.contains(text)) {
                    sel += (!isEmpty(sel) ? "/" : "") + text;
                }
            }
            if (!isEmpty(sel)) {
                string += "(<font color=\"#a1a1a1\">" + sel + "</font>)";
            }
        }
        return string;
    }

    private void showContent(Concern group) {
        topLine.setVisibility(View.GONE);
        textView.setText(Html.fromHtml(format("%s" + (!group.isSelected() ? "" : "(<font color=\"#a1a1a1\">本组织</font>)"), group.getGroupName())));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimension(R.dimen.ui_base_text_size));
        textView.setTextColor(getColor(R.color.textColor));
        countView.setVisibility(View.GONE);
        statusView.setVisibility(View.GONE);
        timeView.setText(group.getReportStr());
        timeView.setVisibility(View.VISIBLE);
        iconView.setVisibility(View.VISIBLE);
    }

    @Click({R.id.ui_holder_view_activity_member_item_layout})
    private void onViewClick(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        }
    }
}
