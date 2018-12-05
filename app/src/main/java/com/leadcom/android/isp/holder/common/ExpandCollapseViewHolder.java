package com.leadcom.android.isp.holder.common;

import android.view.View;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.model.Model;

public class ExpandCollapseViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_expand_collapse_icon)
    private CustomTextView iconView;

    public ExpandCollapseViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(Model model) {
        iconView.setRotation(model.isSelected() ? 0f : 180f);
    }

    @Click({R.id.ui_holder_view_expand_collapse_clicker})
    private void elementClick(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        }
    }
}
