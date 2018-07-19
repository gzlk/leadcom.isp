package com.leadcom.android.isp.holder.common;

import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CorneredView;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.model.common.ShareItem;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/07/19 14:13 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/07/19 14:13 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ShareItemViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_share_item_icon_bg)
    private CorneredView backgroundView;
    @ViewId(R.id.ui_holder_view_share_item_icon)
    private CustomTextView iconView;
    @ViewId(R.id.ui_holder_view_share_item_text)
    private TextView textView;

    public ShareItemViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(ShareItem item){
        backgroundView.setNormalColor(item.getIconBackground());
        iconView.setText(item.getIcon());
        textView.setText(item.getText());
    }

    @Click({R.id.ui_holder_view_share_item_layer})
    private void viewClick(View view) {
        if (null != mOnViewHolderClickListener) {
            mOnViewHolderClickListener.onClick(getAdapterPosition());
        }
    }
}
