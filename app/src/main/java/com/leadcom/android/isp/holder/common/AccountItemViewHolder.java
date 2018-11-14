package com.leadcom.android.isp.holder.common;

import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.BaseViewHolder;


/**
 * <b>功能描述：</b>登录页账户列表Item<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/11/14 11:50 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/11/14 11:50  <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class AccountItemViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_account_item_phone)
    private TextView phoneText;

    public AccountItemViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(String text, String searchingText) {
        if (isEmpty(text)) {
            text = "";
        }
        text = getSearchingText(text, searchingText);
        phoneText.setText(Html.fromHtml(text));
    }

    @Click({R.id.ui_holder_view_account_item_layout, R.id.ui_tool_view_contact_button2})
    private void click(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        }
    }
}
