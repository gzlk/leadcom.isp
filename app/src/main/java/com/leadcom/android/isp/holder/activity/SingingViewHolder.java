package com.leadcom.android.isp.holder.activity;

import android.view.View;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.model.activity.sign.AppSignRecord;
import com.leadcom.android.isp.model.activity.sign.AppSigning;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;

import java.util.Date;

/**
 * <b>功能描述：</b>活动应用中的签到和签到记录item<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/16 22:32 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/16 22:32 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SingingViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_activity_signing_item_icon)
    private CustomTextView iconView;
    @ViewId(R.id.ui_holder_view_activity_signing_item_title)
    private TextView titleView;
    @ViewId(R.id.ui_holder_view_activity_signing_item_time)
    private TextView timeView;
    @ViewId(R.id.ui_holder_view_activity_signing_item_description)
    private TextView descView;
    @ViewId(R.id.ui_holder_view_activity_signing_item_icon_right)
    private CustomTextView rightIconView;

    public SingingViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(AppSigning signing) {
        iconView.setTextColor(getColor(getIconColor(signing.getBeginDate(), signing.getEndDate())));
        titleView.setText(signing.getTitle());
        String time = fragment().formatDate(signing.getEndDate(), R.string.ui_base_text_time_format_hhmm);
        descView.setText(fragment().getString(R.string.ui_activity_sign_list_item_desc, time));
        timeView.setText(fragment().formatTimeAgo(signing.getCreateDate()));
    }

    private int getIconColor(String beginTime, String endTime) {
        long now = new Date().getTime();
        long begin = Utils.parseDate(StringHelper.getString(R.string.ui_base_text_date_time_format), beginTime).getTime();
        long end = Utils.parseDate(StringHelper.getString(R.string.ui_base_text_date_time_format), endTime).getTime();
        if (now < begin) {
            return R.color.color_1f6b41;
        } else if (now > end) {
            return R.color.textColorHint;
        }
        return R.color.colorPrimary;
    }

    public void showContent(AppSignRecord record) {
        titleView.setText(record.getUserName());
        timeView.setText(fragment().formatTimeAgo(record.getCreateDate()));
        descView.setText(record.getSite());
    }

    @Click({R.id.ui_holder_view_activity_signing_item_container})
    private void elementClick(View view) {
        if (null != mOnViewHolderClickListener) {
            mOnViewHolderClickListener.onClick(getAdapterPosition());
        }
    }
}
