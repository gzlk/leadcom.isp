package com.leadcom.android.isp.holder.organization;

import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.etc.Utility;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
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
    @ViewId(R.id.ui_tool_view_contact_button_edit)
    private View editButton;

    public SquadViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showEdit(boolean shown) {
        editButton.setVisibility(shown ? View.VISIBLE : View.GONE);
    }

    public void showContent(Squad squad, String searchingText) {
        String name = squad.getName();
        if (!isEmpty(name)) {
            name = getSearchingText(name, searchingText);
        } else {
            name = StringHelper.getString(R.string.ui_base_text_no_name_squad);
        }
        nameView.setText(Html.fromHtml(name));
        numberView.setText(format("%d人", squad.isSelectable() ? Integer.valueOf(squad.getAccessToken()) : squad.getMemberNum()));
    }

    @Click({R.id.ui_holder_view_group_squad_container, R.id.ui_tool_view_contact_button_edit, R.id.ui_tool_view_contact_button2})
    private void viewClick(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        }
    }
}
