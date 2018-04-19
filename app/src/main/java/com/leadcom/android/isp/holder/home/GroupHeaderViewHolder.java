package com.leadcom.android.isp.holder.home;

import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.model.operation.GRPOperation;
import com.leadcom.android.isp.model.organization.Organization;
import com.leadcom.android.isp.model.organization.Role;


/**
 * <b>功能描述：</b>组织详细信息的头部<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/16 20:03 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class GroupHeaderViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_group_header_logo)
    private ImageDisplayer logoView;
    @ViewId(R.id.ui_holder_view_group_header_intro)
    private TextView introView;
    @ViewId(R.id.ui_holder_view_group_header_edit_icon)
    private CustomTextView editIcon;

    public GroupHeaderViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        logoView.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(ImageDisplayer displayer, String url) {
                logoView.startAnimation(App.clickAnimation());
                viewClick(logoView);
            }
        });
    }

    public void showContent(Organization organization) {
        String logo = organization.getLogo();
        if (isEmpty(logo)) {
            logo = "drawable://" + R.drawable.img_default_group_icon;
        }
        logoView.displayImage(logo, getDimension(R.dimen.ui_static_dp_60), false, false);
        logo = organization.getIntro();
        introView.setText(isEmpty(logo) ? "" : Html.fromHtml(logo));
        Role role = Cache.cache().getGroupRole(organization.getId());
        editIcon.setVisibility((null != role && role.hasOperation(GRPOperation.GROUP_PROPERTY)) ? View.VISIBLE : View.INVISIBLE);
    }

    @Click({R.id.ui_holder_view_group_header_logo, R.id.ui_holder_view_group_header_edit_icon, R.id.ui_holder_view_group_header_container})
    private void viewClick(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        }
    }
}
