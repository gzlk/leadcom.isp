package com.gzlk.android.isp.holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.ArchiveSecurity;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.organization.Member;
import com.gzlk.android.isp.model.organization.Organization;
import com.gzlk.android.isp.model.user.User;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.List;

/**
 * <b>功能描述：</b>档案隐私设置<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/16 22:04 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/16 22:04 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveSecurityViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_archive_security_setting_container_root)
    private LinearLayout root;
    @ViewId(R.id.ui_holder_view_archive_security_setting_icon)
    private CustomTextView icon;
    @ViewId(R.id.ui_holder_view_archive_security_setting_text)
    private TextView textView;
    @ViewId(R.id.ui_holder_view_archive_security_setting_description)
    private TextView descriptionView;

    private int padding;

    public ArchiveSecurityViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        padding = getDimension(R.dimen.ui_base_dimen_margin_padding);
    }

    public void showContent(Model model) {
        if (model instanceof ArchiveSecurity) {
            showContent((ArchiveSecurity) model);
        } else if (model instanceof Organization) {
            showContent((Organization) model);
        } else if (model instanceof User) {
            showContent((User) model);
        } else {
            showContent();
        }
    }

    private void showContent() {
        // 从通讯录选择
        root.setPadding(padding * 2, 0, 0, 0);
        icon.setVisibility(View.INVISIBLE);
        textView.setText(R.string.ui_security_select_from_contact);
        textView.setTextColor(getColor(R.color.colorPrimary));
        descriptionView.setVisibility(View.GONE);
    }

    public void showContent(ArchiveSecurity security) {
        root.setPadding(padding, 0, 0, 0);
        icon.setText(R.string.ui_icon_choose);
        icon.setVisibility(View.VISIBLE);
        icon.setTextColor(getColor(security.isSelected() ? R.color.colorPrimary : R.color.textColorHintLightLight));
        textView.setText(security.getText());
        textView.setTextColor(getColor(R.color.textColor));
        descriptionView.setText(security.getDescription());
        descriptionView.setVisibility(View.VISIBLE);
    }

    public void showContent(User user) {
        root.setPadding(padding * 2, 0, 0, 0);
        icon.setText(R.string.ui_icon_confirm);
        icon.setVisibility(View.VISIBLE);
        icon.setTextColor(getColor(user.isLocalDeleted() ? R.color.colorPrimary : R.color.textColorHintLightLight));
        textView.setText(user.getName());
        textView.setTextColor(getColor(R.color.textColor));
        descriptionView.setVisibility(View.GONE);
    }

    public void showContent(Organization organization) {
        root.setPadding(padding * 2, 0, 0, 0);
        icon.setText(R.string.ui_icon_confirm);
        icon.setVisibility(View.VISIBLE);
        icon.setTextColor(getColor(organization.isLocalDeleted() ? R.color.colorPrimary : R.color.textColorHintLightLight));
        textView.setText(organization.getName());
        textView.setTextColor(getColor(R.color.textColor));
        descriptionView.setText(getOrganizationMembers(organization.getId()));
        descriptionView.setVisibility(View.VISIBLE);
    }

    private String getOrganizationMembers(String orgId) {
        QueryBuilder<Member> query = new QueryBuilder<>(Member.class)
                .whereEquals(Organization.Field.GroupId, orgId)
                .whereAppendAnd()
                .whereAppend(Organization.Field.SquadId + " IS NULL")
                .orderBy(Model.Field.CreateDate);
        List<Member> members = new Dao<>(Member.class).query(query);
        String string = "";
        int i = 0;
        for (Member member : members) {
            if (i > 4) {
                string += "等";
                break;
            }
            string += (StringHelper.isEmpty(string) ? "" : "、") + member.getUserName();
            i++;
        }
        return string;
    }

    @Click({R.id.ui_holder_view_archive_security_setting_container})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.ui_holder_view_archive_security_setting_container:
                if (null != mOnViewHolderClickListener) {
                    mOnViewHolderClickListener.onClick(getAdapterPosition());
                }
                break;
        }
    }
}
