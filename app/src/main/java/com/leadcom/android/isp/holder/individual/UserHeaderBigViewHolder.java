package com.leadcom.android.isp.holder.individual;

import android.view.View;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseTransparentSupportFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.model.activity.Activity;
import com.leadcom.android.isp.model.organization.Organization;
import com.leadcom.android.isp.model.user.User;
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
    @ViewId(R.id.ui_holder_view_user_signature)
    private TextView signatureTextView;
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
            public void onImageClick(ImageDisplayer displayer, String url) {
                openImageSelector();
            }
        });
    }

    public void showContent(User user) {
        String name = user.getName();
        if (isEmpty(name)) {
            name = StringHelper.getString(R.string.ui_text_user_information_name_empty);
        }
        nameTextView.setText(name);
        headerImage.displayImage(user.getHeadPhoto(), getDimension(R.dimen.ui_static_dp_100), false, false);
        boolean isMe = !isEmpty(user.getId()) && user.getId().equals(Cache.cache().userId);
        nameIcon.setVisibility(isMe ? View.VISIBLE : View.GONE);
        headerIcon.setVisibility(isMe ? View.VISIBLE : View.GONE);
        // 个性签名
        String signature = user.getSignature();
        if (isEmpty(signature)) {
            if (user.getId().equals(Cache.cache().userId)) {
                signature = StringHelper.getString(R.string.ui_text_user_information_signature_empty);
            } else {
                signatureTextView.setVisibility(View.GONE);
            }
        }
        signatureTextView.setText(signature);
        //signatureTextView.setVisibility(user.getId().equals(Cache.cache().userId) ? View.GONE : View.VISIBLE);
    }

    public void showContent(Organization org) {
        String text = null != org && !StringHelper.isEmpty(org.getName()) ? org.getName() : StringHelper.getString(R.string.ui_base_text_not_set);
        nameTextView.setText(text);
        text = null != org && !StringHelper.isEmpty(org.getLogo()) ? org.getLogo() : "";
        headerImage.displayImage(text, getDimension(R.dimen.ui_static_dp_100), false, false);
        nameIcon.setVisibility(null != org && org.isLocalDeleted() ? View.VISIBLE : View.GONE);
        headerIcon.setVisibility(null != org && org.isLocalDeleted() ? View.VISIBLE : View.GONE);
        signatureTextView.setVisibility(View.GONE);
    }

    public void showContent(Activity activity) {
        String text = null != activity && !isEmpty(activity.getTitle()) ? activity.getTitle() : StringHelper.getString(R.string.ui_base_text_not_set);
        nameTextView.setText(text);
        signatureTextView.setVisibility(View.GONE);
        text = null != activity && !isEmpty(activity.getCover()) ? activity.getCover() : ("drawable://" + R.drawable.img_default_group_icon);
        headerImage.displayImage(text, getDimension(R.dimen.ui_static_dp_100), false, false);
        nameIcon.setVisibility(View.GONE);
        headerIcon.setVisibility(View.GONE);
    }

    @Click({R.id.ui_holder_view_user_header_icon,
            R.id.ui_holder_view_user_name_icon,
            R.id.ui_holder_view_user_name,
            R.id.ui_holder_view_user_signature})
    private void elementClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ui_holder_view_user_header_icon:
                openImageSelector();
                break;
            case R.id.ui_holder_view_user_name_icon:
            case R.id.ui_holder_view_user_name:
                if (null != mOnViewHolderClickListener) {
                    mOnViewHolderClickListener.onClick(-1);
                }
                break;
            case R.id.ui_holder_view_user_signature:
                if (null != mOnViewHolderClickListener) {
                    mOnViewHolderClickListener.onClick(0);
                }
                break;
        }
    }

    private void openImageSelector() {
        if (null != mOnViewHolderClickListener) {
            mOnViewHolderClickListener.onClick(-2);
        }
//        if (headerIcon.getVisibility() == View.VISIBLE) {
//            ((BaseImageSelectableSupportFragment) fragment()).openImageSelector();
//        }
    }
}
