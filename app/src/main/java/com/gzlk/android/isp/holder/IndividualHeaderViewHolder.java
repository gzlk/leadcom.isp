package com.gzlk.android.isp.holder;

import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.base.BaseTransparentSupportFragment;
import com.gzlk.android.isp.fragment.individual.QRCodeFragment;
import com.gzlk.android.isp.fragment.individual.UserInformationFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.user.User;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;

/**
 * <b>功能描述：</b>个人头像<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/13 16:50 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/13 16:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class IndividualHeaderViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_tool_individual_name)
    private TextView nameTextView;
    @ViewId(R.id.ui_tool_individual_additional)
    private TextView additionalTextView;
    @ViewId(R.id.ui_holder_view_user_header)
    private ImageDisplayer userHeader;

    public IndividualHeaderViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        ((BaseTransparentSupportFragment) fragment).tryPaddingContent(itemView, true);
        userHeader.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {

            @Override
            public void onImageClick(String url) {
                openActivity(UserInformationFragment.class.getName(), Cache.cache().userId, false, false, true);
            }
        });

    }

    @Click({R.id.ui_tool_individual_header_to_2d_code})
    private void click(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ui_tool_individual_header_to_2d_code:
                openActivity(QRCodeFragment.class.getName(), "", false, false, true);
                break;
        }
    }

    public void showContent(User user) {
        ((BaseTransparentSupportFragment) fragment()).tryPaddingContent(itemView, true);
        nameTextView.setText(StringHelper.isEmpty(user.getName()) ? user.getLoginId() : user.getName());
        userHeader.displayImage("https://img3.cache.netease.com/photo/0001/2017-04-20/CIGA5N5D00AN0001.jpg", getDimension(R.dimen.ui_static_dp_60), false, false);
    }
}
