package com.leadcom.android.isp.holder.organization;

import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.model.operation.GRPOperation;
import com.leadcom.android.isp.model.organization.Squad;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

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

    @ViewId(R.id.ui_holder_view_group_squad_blank)
    private View blankView;
    @ViewId(R.id.ui_holder_view_group_squad_picker)
    private CustomTextView picker;
    @ViewId(R.id.ui_holder_view_group_squad_logo)
    private CustomTextView logo;
    @ViewId(R.id.ui_holder_view_group_squad_name)
    private TextView nameView;
    @ViewId(R.id.ui_holder_view_group_squad_members)
    private TextView numberView;
    @ViewId(R.id.ui_holder_view_group_squad_loading)
    private CircleProgressBar loading;
    @ViewId(R.id.ui_tool_view_contact_button_edit)
    private View editButton;

    private boolean showPicker = false;
    private boolean showBlank = false;

    public SquadViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showEdit(boolean shown) {
        if (null != editButton) {
            editButton.setVisibility(shown ? View.VISIBLE : View.GONE);
        }
    }

    public void showPicker(boolean shown) {
        showPicker = shown;
        if (null != picker) {
            picker.setVisibility(shown ? View.VISIBLE : View.GONE);
        }
        logo.setVisibility(shown ? View.GONE : View.VISIBLE);
    }

    public void showBlank(boolean shown) {
        showBlank = shown;
    }

    public void showContent(Squad squad, String searchingText) {
        String name = squad.getName();
        if (!isEmpty(name)) {
            name = getSearchingText(name, searchingText);
        } else {
            name = StringHelper.getString(R.string.ui_base_text_no_name_squad);
        }
        // 可以编辑小组名称
        if (null != editButton && editButton.getVisibility() != View.VISIBLE) {
            showEdit(null != squad.getGroRole() && squad.getGroRole().hasOperation(GRPOperation.SQUAD_PROPERTY));
        }
        if (null != picker) {
            // 小组中成员是否被全选中
            picker.setTextColor(getColor(squad.isSelected() ? R.color.colorPrimary : R.color.textColorHintLight));
        }

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) blankView.getLayoutParams();
        params.width = getDimension(showBlank ? R.dimen.ui_static_dp_15 : R.dimen.ui_static_dp_1);
        blankView.setLayoutParams(params);

        nameView.setText(Html.fromHtml(name));
        int selected = squad.getCollapseStatus();
        int total = squad.getGroSquMemberList().size();
        numberView.setText(total > 0 ? format("%s%d人", (showPicker ? (selected > 0 ? format("%d/", selected) : "") : ""), squad.getGroSquMemberList().size()) : "");

        loading.setVisibility(squad.isRead() ? View.VISIBLE : View.GONE);
    }

    @Click({R.id.ui_holder_view_group_squad_container,
            R.id.ui_tool_view_contact_button_edit,
            R.id.ui_tool_view_contact_button2,
            R.id.ui_holder_view_group_squad_picker})
    private void viewClick(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        }
    }
}
