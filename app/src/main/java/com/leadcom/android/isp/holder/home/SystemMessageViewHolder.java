package com.leadcom.android.isp.holder.home;

import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.model.common.PushMessage;

/**
 * <b>功能描述：</b>系统消息<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/20 16:27 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/20 16:27 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SystemMessageViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_system_message_title)
    private TextView titleView;
    @ViewId(R.id.ui_holder_view_system_message_time)
    private TextView timeView;
    @ViewId(R.id.ui_holder_view_system_message_unread)
    private TextView unreadView;
    @ViewId(R.id.ui_holder_view_system_message_description)
    private TextView descView;

    public SystemMessageViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(PushMessage msg) {
        titleView.setText(msg.getTitle());
        titleView.setSelected(true);
        descView.setText(msg.getContent());
        descView.setSelected(true);
        String time = fragment().formatTimeAgo(msg.getCreateDate());
        timeView.setText(time);
        unreadView.setVisibility(msg.isRead() ? View.GONE : View.VISIBLE);
    }

    @Click({R.id.ui_holder_view_system_message_container, R.id.ui_tool_view_contact_button2})
    private void elementClick(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        }
    }
}
