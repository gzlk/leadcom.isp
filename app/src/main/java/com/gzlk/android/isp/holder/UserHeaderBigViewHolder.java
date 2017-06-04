package com.gzlk.android.isp.holder;

import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.base.BaseImageSelectableSupportFragment;
import com.gzlk.android.isp.fragment.base.BaseTransparentSupportFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.activity.Activity;
import com.gzlk.android.isp.model.organization.Organization;
import com.gzlk.android.isp.model.user.User;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;

/**
 * <b>功能描述：</b>个人信息大头像部分<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/02 08:55 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/02 08:55 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class UserHeaderBigViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_user_name)
    private TextView nameTextView;
    @ViewId(R.id.ui_holder_view_user_name_icon)
    private CustomTextView nameIcon;
    @ViewId(R.id.ui_holder_view_user_phone)
    private TextView phoneTextView;
    @ViewId(R.id.ui_holder_view_user_header)
    private ImageDisplayer headerImage;
    @ViewId(R.id.ui_holder_view_user_header_icon)
    private CustomTextView headerIcon;

    public UserHeaderBigViewHolder(View itemView, final BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        ((BaseTransparentSupportFragment) fragment).tryPaddingContent(itemView, false);
        headerImage.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(String url) {
                openImageSelector();
            }
        });
    }

    public void showContent(User user) {
        nameTextView.setText(user.getName());
        headerImage.displayImage(user.getHeadPhoto(), getDimension(R.dimen.ui_static_dp_100), false, false);
        nameIcon.setVisibility(user.isLocalDeleted() ? View.VISIBLE : View.GONE);
        headerIcon.setVisibility(user.isLocalDeleted() ? View.VISIBLE : View.GONE);
        phoneTextView.setText(user.getPhone());
        phoneTextView.setVisibility(user.getId().equals(Cache.cache().userId) ? View.GONE : View.VISIBLE);
    }

    public void showContent(Organization org) {
        String text = null != org && !StringHelper.isEmpty(org.getName()) ? org.getName() : StringHelper.getString(R.string.ui_base_text_not_set);
        nameTextView.setText(text);
        text = null != org && !StringHelper.isEmpty(org.getLogo()) ? org.getLogo() : "";
        headerImage.displayImage(text, getDimension(R.dimen.ui_static_dp_100), false, false);
        nameIcon.setVisibility(null != org && org.isLocalDeleted() ? View.VISIBLE : View.GONE);
        headerIcon.setVisibility(null != org && org.isLocalDeleted() ? View.VISIBLE : View.GONE);
        phoneTextView.setVisibility(View.GONE);
    }

    public void showContent(Activity activity) {
        String text = null != activity && !isEmpty(activity.getTitle()) ? activity.getTitle() : StringHelper.getString(R.string.ui_base_text_not_set);
        nameTextView.setText(text);
        phoneTextView.setVisibility(View.GONE);
        text = null != activity && !isEmpty(activity.getImg()) ? activity.getImg() : "";
        headerImage.displayImage(text, getDimension(R.dimen.ui_static_dp_100), false, false);
        nameIcon.setVisibility(View.GONE);
        headerIcon.setVisibility(View.GONE);
    }

    @Click({R.id.ui_holder_view_user_header_icon,
            R.id.ui_holder_view_user_name_icon,
            R.id.ui_holder_view_user_name})
    private void elementClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ui_holder_view_user_header_icon:
                openImageSelector();
                break;
            case R.id.ui_holder_view_user_name_icon:
            case R.id.ui_holder_view_user_name:
                if (null != mOnViewHolderClickListener && headerIcon.getVisibility() == View.VISIBLE) {
                    mOnViewHolderClickListener.onClick(0);
                }
                break;
        }
    }

    private void openImageSelector() {
        if (headerIcon.getVisibility() == View.VISIBLE) {
            ((BaseImageSelectableSupportFragment) fragment()).openImageSelector();
        }
    }
}
