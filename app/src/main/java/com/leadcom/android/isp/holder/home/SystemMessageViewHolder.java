package com.leadcom.android.isp.holder.home;

import android.view.View;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CorneredView;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.nim.model.notification.NimMessage;

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

    @ViewId(R.id.ui_holder_view_swipe_layout)
    private SwipeLayout swipeLayout;
    @ViewId(R.id.ui_holder_view_system_message_icon_container)
    private CorneredView iconContainer;
    @ViewId(R.id.ui_holder_view_system_message_icon)
    private CustomTextView iconView;
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

    public void showContent(NimMessage msg) {
        titleView.setText(getTitle(msg));
        descView.setText(msg.getMsgContent());
        String time = fragment().formatTimeAgo(msg.getSendDate());
        timeView.setText(time);
        unreadView.setVisibility(msg.isRead() ? View.GONE : View.VISIBLE);
    }

    private String getTitle(NimMessage msg) {
        if (isEmpty(msg.getMsgTitle())) {
            return NimMessage.getMsgType(0 == msg.getType() ? msg.getMsgType() : msg.getType());
        }
        return msg.getMsgTitle();
    }

    @Click({R.id.ui_holder_view_system_message_container, R.id.ui_holder_view_system_message_delete})
    private void elementClick(View view) {
        switch (view.getId()) {
            case R.id.ui_holder_view_system_message_container:
                // 打开查看详情
                if (null != mOnViewHolderClickListener) {
                    mOnViewHolderClickListener.onClick(getAdapterPosition());
                }
                break;
            case R.id.ui_holder_view_system_message_delete:
                // 删除
                if (null != mOnHandlerBoundDataListener) {
                    mOnHandlerBoundDataListener.onHandlerBoundData(SystemMessageViewHolder.this);
                }
                break;
        }
    }
}
