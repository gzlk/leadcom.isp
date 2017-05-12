package com.gzlk.android.isp.holder;

import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.organization.Member;
import com.gzlk.android.isp.model.user.User;
import com.hlk.hlklib.etc.Utility;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;

/**
 * <b>功能描述：</b>联系人<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/09 09:17 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/09 09:17 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ContactViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_contact_swipe_layout)
    private SwipeLayout swipeLayout;
    @ViewId(R.id.ui_holder_view_contact_header)
    private ImageDisplayer headerView;
    @ViewId(R.id.ui_holder_view_contact_name)
    private TextView nameView;
    @ViewId(R.id.ui_holder_view_contact_phone)
    private TextView phoneView;
    @ViewId(R.id.ui_holder_view_contact_myself)
    private TextView myselfView;

    public ContactViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public SwipeLayout getSwipeLayout() {
        return swipeLayout;
    }

    public void showContent(User user, String searching) {
        String text = user.getName();
        if (!StringHelper.isEmpty(searching)) {
            text = Utility.addColor(text, searching, getColor(R.color.colorAccent));
        }
        nameView.setText(Html.fromHtml(text));
        phoneView.setText(user.getPhone());
        myselfView.setVisibility(user.getId().equals(Cache.cache().userId) ? View.VISIBLE : View.GONE);
    }

    public void showContent(Member member, String searchingText) {
        String text = member.getUserName();
        if (!StringHelper.isEmpty(searchingText)) {
            text = Utility.addColor(text, searchingText, getColor(R.color.colorAccent));
        }
        nameView.setText(Html.fromHtml(text));
        text = member.getPhone();
        if (!StringHelper.isEmpty(searchingText)) {
            text = Utility.addColor(text, searchingText, getColor(R.color.colorAccent));
        }
        phoneView.setText(Html.fromHtml(text));
        myselfView.setVisibility(member.getUserId().equals(Cache.cache().userId) ? View.VISIBLE : View.GONE);
    }

    @Click({R.id.ui_tool_view_contact_delete})
    private void click(View view) {
        if (view.getId() == R.id.ui_tool_view_contact_delete) {
            if (null != onUserDeleteListener) {
                onUserDeleteListener.onDelete(ContactViewHolder.this);
            }
        }
    }

    private OnUserDeleteListener onUserDeleteListener;

    public void setOnUserDeleteListener(OnUserDeleteListener l) {
        onUserDeleteListener = l;
    }

    public interface OnUserDeleteListener {
        void onDelete(ContactViewHolder holder);
    }
}
