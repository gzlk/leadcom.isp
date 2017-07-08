package com.gzlk.android.isp.holder.activity;

import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.activity.Activity;
import com.hlk.hlklib.etc.Utility;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;

/**
 * <b>功能描述：</b>活动管理页面中的活动item<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/30 11:12 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/30 11:12 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ActivityManagementViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_activity_management_item_image)
    private ImageDisplayer imageDisplayer;
    @ViewId(R.id.ui_holder_view_activity_management_item_title)
    private TextView titleTextView;
    @ViewId(R.id.ui_holder_view_activity_management_item_time)
    private TextView timeTextView;
    @ViewId(R.id.ui_holder_view_activity_management_item_address)
    private TextView addrTextView;

    public ActivityManagementViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        imageDisplayer.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    public void showContent(Activity activity, String searchingText) {
        imageDisplayer.displayImage(activity.getImg(), getDimension(R.dimen.ui_static_dp_100), getDimension(R.dimen.ui_static_dp_80), false, false);
        String text = activity.getTitle();
        if (isEmpty(text)) {
            text = StringHelper.getString(R.string.ui_base_text_not_set);
        }
        text = getSearchingText(text, searchingText);
        titleTextView.setText(Html.fromHtml(text));
        addrTextView.setText(activity.getSite());
        timeTextView.setText(fragment().formatDateTime(activity.getBeginDate()));
    }

    @Click({R.id.ui_holder_view_activity_management_item_container})
    private void onClick(View view) {
        if (null != mOnViewHolderClickListener) {
            mOnViewHolderClickListener.onClick(getAdapterPosition());
        }
    }
}
