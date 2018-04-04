package com.leadcom.android.isp.holder.activity;

import android.view.View;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.model.activity.AppNotice;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;

/**
 * <b>功能描述：</b>通知列表里的单个通知item<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/28 21:39 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/28 21:39 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class NoticeViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_activity_notice_item_title)
    private TextView titleView;
    @ViewId(R.id.ui_holder_view_activity_notice_item_time)
    private TextView timeView;
    @ViewId(R.id.ui_holder_view_activity_notice_item_read)
    private TextView readView;

    public NoticeViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(AppNotice notice) {
        String time = fragment().formatDate(notice.getCreateDate(), R.string.ui_base_text_date_format_chs_mmdd);
        titleView.setText(fragment().getString(R.string.ui_activity_notice_list_item_title, time, notice.getTitle()));
        timeView.setText(fragment().formatTimeAgo(notice.getCreateDate()));
        readView.setText(notice.isRead() ? R.string.ui_base_text_has_read : R.string.ui_base_text_not_read);
        readView.setTextColor(getColor(notice.isRead() ? R.color.textColorHint : R.color.colorCaution));
    }

    @Click({R.id.ui_holder_view_activity_notice_item, R.id.ui_tool_view_contact_button2})
    private void click(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        }
    }
}
