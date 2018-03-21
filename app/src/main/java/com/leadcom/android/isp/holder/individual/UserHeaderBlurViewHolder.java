package com.leadcom.android.isp.holder.individual;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.model.user.User;

/**
 * <b>功能描述：</b>首页 - 个人 - 高斯模糊背景的头像部分<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/21 11:54 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/03/21 11:54 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class UserHeaderBlurViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_tool_individual_name)
    private TextView nameTextView;
    @ViewId(R.id.ui_tool_individual_additional)
    private TextView additionalTextView;
    @ViewId(R.id.ui_holder_view_user_header_layout)
    private View userHeaderLayout;
    @ViewId(R.id.ui_holder_view_user_header)
    private ImageDisplayer userHeader;
    @ViewId(R.id.tool_view_individual_top_padding)
    private LinearLayout topPadding;

    public UserHeaderBlurViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        paddingContent();
        userHeader.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(ImageDisplayer displayer, String url) {

            }
        });
    }

    private void paddingContent() {
        int status = BaseActivity.getStatusHeight(fragment().Activity());
        int actionSize = fragment().Activity().getActionBarSize();
        topPadding.setPadding(0, status + actionSize, 0, 0);
    }

    public void showContent(User user) {
        nameTextView.setText(isEmpty(user.getName()) ? StringHelper.getString(R.string.ui_text_user_information_name_empty) : user.getName());
        userHeader.displayImage(user.getHeadPhoto(), getDimension(R.dimen.ui_static_dp_60), false, false);
        additionalTextView.setText(isEmpty(user.getSignature()) ? StringHelper.getString(R.string.ui_text_user_information_signature_empty) : user.getSignature());
    }

    @Click({})
    private void viewClick(View view){

    }
}
