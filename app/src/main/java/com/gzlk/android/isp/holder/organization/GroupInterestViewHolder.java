package com.gzlk.android.isp.holder.organization;

import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.organization.Concern;
import com.gzlk.android.isp.model.organization.Organization;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CorneredButton;

/**
 * <b>功能描述：</b>感兴趣的组织item<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/07/09 21:30 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/07/09 21:30 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class GroupInterestViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_group_interest_cover)
    private ImageDisplayer coverView;
    @ViewId(R.id.ui_holder_view_group_interest_name)
    private TextView nameView;
    @ViewId(R.id.ui_holder_view_group_interest_button)
    private CorneredButton buttonView;

    public GroupInterestViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(Organization organization) {
        String cover = organization.getLogo();
        if (isEmpty(cover)) {
            cover = "drawable://" + R.mipmap.img_image_loading_fail;
        }
        coverView.displayImage(cover, getDimension(R.dimen.ui_static_dp_35), false, false);
        String name = organization.getName();
        if (organization instanceof Concern) {
            Concern concern = (Concern) organization;
            if (concern.getType() > 0) {
                name += format("(%s)", Concern.getTypeString(concern.getType()));
            }
        }
        nameView.setText(name);
        buttonView.setText(organization.isConcerned() ? R.string.ui_organization_interesting_concerned : R.string.ui_organization_interesting_concern);
        buttonView.setNormalColor(getColor(organization.isConcerned() ? R.color.color_3eb135 : R.color.colorPrimary));
    }

    @Click({R.id.ui_holder_view_group_interest_button})
    private void elementClick(View view) {
        if (null != mOnViewHolderClickListener) {
            mOnViewHolderClickListener.onClick(getAdapterPosition());
        }
    }
}
