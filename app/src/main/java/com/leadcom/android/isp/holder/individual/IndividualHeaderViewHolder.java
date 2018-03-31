package com.leadcom.android.isp.holder.individual;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.main.IndividualFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.model.user.User;

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
    @ViewId(R.id.tool_view_individual_top_padding)
    private LinearLayout topPadding;

    public IndividualHeaderViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        resetTopPadding();
        userHeader.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {

            @Override
            public void onImageClick(ImageDisplayer displayer, String url) {
                App.openUserInfo(fragment(), Cache.cache().userId);
            }
        });
    }

    private void resetTopPadding() {
        int statusBarHeight = BaseActivity.getStatusHeight(fragment().Activity());
        int actionBarHeight = fragment().Activity().getActionBarSize();
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) topPadding.getLayoutParams();
        params.height = statusBarHeight + actionBarHeight;
        topPadding.setLayoutParams(params);
    }

    @Click({R.id.ui_tool_individual_header_to_2d_code, R.id.ui_holder_view_user_name})
    private void click(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ui_tool_individual_header_to_2d_code:
                //openActivity(QRCodeFragment.class.getName(), "", false, false, true);
                ((IndividualFragment) fragment()).openUserMessageList();
                break;
            case R.id.ui_holder_view_user_name:
                App.openUserInfo(fragment(), Cache.cache().userId);
                break;
        }
    }

    public void showContent(User user) {
        nameTextView.setText(isEmpty(user.getName()) ? user.getLoginId() : user.getName());
        userHeader.displayImage(user.getHeadPhoto(), getDimension(R.dimen.ui_static_dp_60), false, false);
        additionalTextView.setText(isEmpty(user.getSignature()) ? StringHelper.getString(R.string.ui_text_user_information_signature_empty) : user.getSignature());
    }
}
