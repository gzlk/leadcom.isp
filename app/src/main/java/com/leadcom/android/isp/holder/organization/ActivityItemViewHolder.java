package com.leadcom.android.isp.holder.organization;

import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.model.archive.Archive;


/**
 * <b>功能描述：</b>组织活动ViewHolder<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/09/17 15:38 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/09/17 15:38  <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ActivityItemViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_group_activity_item_title)
    private TextView titleView;
    @ViewId(R.id.ui_holder_view_group_activity_item_time)
    private TextView timeView;

    public ActivityItemViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(Archive activity) {
        titleView.setText(activity.getTitle());
        titleView.setSelected(true);
        timeView.setText(StringHelper.getString(R.string.ui_group_activity_item_time, fragment().formatDate(activity.getCreateDate())));
    }

    @Click({R.id.ui_holder_view_group_activity_item_layout})
    private void click(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        } else if (null != mOnViewHolderClickListener) {
            mOnViewHolderClickListener.onClick(getAdapterPosition());
        }
    }
}
