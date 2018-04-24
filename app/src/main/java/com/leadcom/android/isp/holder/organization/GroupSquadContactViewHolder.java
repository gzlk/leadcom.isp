package com.leadcom.android.isp.holder.organization;

import android.text.Html;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.model.organization.Concern;
import com.leadcom.android.isp.model.organization.Member;
import com.leadcom.android.isp.model.organization.Organization;
import com.leadcom.android.isp.model.organization.SimpleGroup;
import com.leadcom.android.isp.model.organization.SimpleMember;
import com.leadcom.android.isp.model.organization.SimpleSquad;
import com.leadcom.android.isp.model.organization.Squad;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;

/**
 * <b>功能描述：</b>组织、小组联系人多选ViewHolder<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/27 08:15 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/27 08:15 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class GroupSquadContactViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_group_squad_contact_selector_icon)
    private CustomTextView selectorIcon;
    @ViewId(R.id.ui_holder_view_group_squad_contact_text)
    private TextView titleText;
    @ViewId(R.id.ui_holder_view_group_squad_contact_select_all)
    private LinearLayout selectAll;
    @ViewId(R.id.ui_holder_view_group_squad_contact_select_all_icon)
    private CustomTextView selectAllIcon;
    @ViewId(R.id.ui_holder_view_group_squad_contact_lock_icon)
    private CustomTextView lockIcon;
    @ViewId(R.id.ui_holder_view_group_squad_contact_right_icon)
    private CustomTextView rightIcon;

    private int margin;

    public GroupSquadContactViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        margin = getDimension(R.dimen.ui_base_dimen_margin_padding);
    }

    private void resetLeftMargin(int times) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) selectorIcon.getLayoutParams();
        params.leftMargin = times * margin;
        selectorIcon.setLayoutParams(params);
    }

    public void showContent(Organization group) {
        // 组织不需要缩进
        resetLeftMargin(0);
        rightIcon.setVisibility(View.VISIBLE);
        titleText.setText(Html.fromHtml(group.getName()));
        selectorIcon.setText(group.isSelectable() ? R.string.ui_icon_checkbox_checked : R.string.ui_icon_checkbox_unchecked);
        selectorIcon.setTextColor(getColor(group.isSelected() ? R.color.colorPrimary : R.color.textColorHintLightLight));
        //selectAll.setVisibility(group.isSelected() ? View.VISIBLE : View.GONE);
        // 是否全选状态
        //selectAllIcon.setTextColor(getColor(group.isSelectable() ? R.color.colorPrimary : R.color.textColorHintLightLight));
        lockIcon.setVisibility(View.GONE);
        showRightIconAnimate(group.isSelected());
    }

    public void showContent(SimpleGroup group) {
        // 组织不需要缩进
        resetLeftMargin(0);
        rightIcon.setVisibility(View.VISIBLE);
        String name = format("%s(%s)", group.getName(), Concern.getTypeString(group.getType()));
        titleText.setText(name);
        selectorIcon.setText(group.isSelectable() ? R.string.ui_icon_checkbox_checked : R.string.ui_icon_checkbox_unchecked);
        selectorIcon.setTextColor(getColor(group.isSelected() ? R.color.colorPrimary : R.color.textColorHintLightLight));
        //selectAll.setVisibility(group.isSelected() ? View.VISIBLE : View.GONE);
        // 是否全选状态
        //selectAllIcon.setTextColor(getColor(group.isSelectable() ? R.color.colorPrimary : R.color.textColorHintLightLight));
        lockIcon.setVisibility(View.GONE);
        showRightIconAnimate(group.isSelected());
    }

    public void showContent(Squad squad) {
        // 小组需要1倍缩进
        resetLeftMargin(2);
        rightIcon.setVisibility(View.VISIBLE);
        titleText.setText(squad.getName());
        selectorIcon.setText(squad.isSelectable() ? R.string.ui_icon_checkbox_checked : R.string.ui_icon_checkbox_unchecked);
        selectorIcon.setTextColor(getColor(squad.isSelected() ? R.color.colorPrimary : R.color.textColorHintLightLight));
        //selectAll.setVisibility(squad.isSelected() ? View.VISIBLE : View.GONE);
        // 是否全选状态
        //selectAllIcon.setTextColor(getColor(squad.isSelectable() ? R.color.colorPrimary : R.color.textColorHintLightLight));
        lockIcon.setVisibility(View.GONE);
        showRightIconAnimate(squad.isSelected());
    }

    public void showContent(SimpleSquad squad) {
        // 小组需要1倍缩进
        resetLeftMargin(2);
        rightIcon.setVisibility(View.VISIBLE);
        titleText.setText(squad.getName());
        selectorIcon.setText(squad.isSelectable() ? R.string.ui_icon_checkbox_checked : R.string.ui_icon_checkbox_unchecked);
        selectorIcon.setTextColor(getColor(squad.isSelected() ? R.color.colorPrimary : R.color.textColorHintLightLight));
        //selectAll.setVisibility(squad.isSelected() ? View.VISIBLE : View.GONE);
        // 是否全选状态
        //selectAllIcon.setTextColor(getColor(squad.isSelectable() ? R.color.colorPrimary : R.color.textColorHintLightLight));
        lockIcon.setVisibility(View.GONE);
        showRightIconAnimate(squad.isSelected());
    }

    public void showContent(Member member) {
        resetLeftMargin(4);
        rightIcon.setVisibility(View.GONE);
        titleText.setText(format("%s(%s)", member.getUserName(), member.getPhone()));
        selectorIcon.setText(R.string.ui_icon_select_solid);
        selectorIcon.setTextColor(getColor(member.isSelected() ? R.color.colorPrimary : R.color.textColorHintLightLight));
        selectAll.setVisibility(View.GONE);
        lockIcon.setVisibility(member.isLocalDeleted() ? View.VISIBLE : View.GONE);
    }

    public void showContent(SimpleMember member) {
        resetLeftMargin(4);
        rightIcon.setVisibility(View.GONE);
        titleText.setText(format("%s(%s)", member.getUserName(), member.getPhone()));
        selectorIcon.setText(R.string.ui_icon_select_solid);
        selectorIcon.setTextColor(getColor(member.isSelected() ? R.color.colorPrimary : R.color.textColorHintLightLight));
        selectAll.setVisibility(View.GONE);
        lockIcon.setVisibility(member.isLocalDeleted() ? View.VISIBLE : View.GONE);
    }

    public void showRightIconAnimate(boolean rotateDown) {
        rightIcon.animate()
                .rotation(rotateDown ? 90.0F : 0.0F)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(500)
                .start();
    }

    @Click({R.id.ui_holder_view_group_squad_contact_root,
            R.id.ui_holder_view_group_squad_contact_selector_icon})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.ui_holder_view_group_squad_contact_root:
                // 点击展开成员列表
                if (null != mOnViewHolderClickListener) {
                    mOnViewHolderClickListener.onClick(getAdapterPosition());
                }
                break;
            case R.id.ui_holder_view_group_squad_contact_selector_icon:
                // 全选当前组织或小组内的成员
                if (null != mOnHandlerBoundDataListener) {
                    mOnHandlerBoundDataListener.onHandlerBoundData(GroupSquadContactViewHolder.this);
                }
                break;
        }
    }
}
