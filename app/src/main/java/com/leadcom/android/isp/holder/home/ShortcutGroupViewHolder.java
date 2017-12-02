package com.leadcom.android.isp.holder.home;

import android.view.View;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.model.organization.Organization;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;

/**
 * <b>功能描述：</b>快捷通道里的组织ViewHolder<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/11/27 11:32 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/11/27 11:32 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ShortcutGroupViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_tool_organization_concerned_pager_container)
    private View containerView;
    @ViewId(R.id.ui_tool_organization_concerned_pager_logo)
    private ImageDisplayer logoView;
    @ViewId(R.id.ui_tool_organization_concerned_pager_name)
    private TextView nameView;

    public ShortcutGroupViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        logoView.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(ImageDisplayer displayer, String url) {
                containerView.performClick();
            }
        });
    }

    public void showContent(Organization group, int type) {
        logoView.displayImage(group.getLogo(), getDimension(R.dimen.ui_static_dp_60), false, false);
        nameView.setText(group.getName());
        containerView.setTag(R.id.hlklib_ids_custom_view_click_tag, type);
    }

    @Click({R.id.ui_tool_organization_concerned_pager_container})
    private void elementClick(View view) {
        view.startAnimation(App.clickAnimation());
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        }
    }
}
