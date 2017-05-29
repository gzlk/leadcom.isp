package com.gzlk.android.isp.holder;

import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.common.Contact;
import com.hlk.hlklib.etc.Utility;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CorneredButton;

/**
 * <b>功能描述：</b>手机通讯录ViewHolder<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/09 16:33 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/09 16:33 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class PhoneContactViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_phone_contact_name)
    private TextView nameView;
    @ViewId(R.id.ui_holder_view_phone_contact_phone)
    private TextView phoneView;
    @ViewId(R.id.ui_holder_view_phone_contact_button)
    private CorneredButton button;

    public PhoneContactViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(Contact contact, String searchingText) {
        String text = contact.getName();
        if (!StringHelper.isEmpty(searchingText)) {
            text = Utility.addColor(text, searchingText, getColor(R.color.colorAccent));
        }
        nameView.setText(Html.fromHtml(text));
        phoneView.setText(contact.getPhone());
        if (contact.isMember()) {
            button.setEnabled(false);
            button.setText(R.string.ui_phone_contact_invited);
        } else {
            button.setEnabled(!contact.isInvited());
            button.setText(StringHelper.getString(contact.isInvited() ? R.string.ui_phone_contact_inviting : R.string.ui_phone_contact_invite));
        }
    }

    @Click({R.id.ui_holder_view_phone_contact_button})
    private void click(View view) {
        if (view.getId() == R.id.ui_holder_view_phone_contact_button) {
            if (null != mOnViewHolderClickListener) {
                mOnViewHolderClickListener.onClick(getAdapterPosition());
            }
        }
    }
}
