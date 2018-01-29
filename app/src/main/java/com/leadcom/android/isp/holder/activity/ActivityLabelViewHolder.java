package com.leadcom.android.isp.holder.activity;

import android.view.View;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.model.activity.Label;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CornerTagView;
import com.hlk.hlklib.lib.view.CorneredView;
import com.leadcom.android.isp.model.archive.Dictionary;

/**
 * <b>功能描述：</b>活动标签<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/29 22:17 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/29 22:17 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ActivityLabelViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_activity_label_container)
    private CorneredView containerView;
    @ViewId(R.id.ui_holder_view_activity_label_text)
    private TextView textView;
    @ViewId(R.id.ui_holder_view_activity_label_selected)
    private CornerTagView tagView;

    public ActivityLabelViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(Label label) {
        textView.setText(label.getName());
        containerView.setNormalColor(getColor(label.isSelected() ? R.color.colorPrimary : R.color.textColorHintLight));
        tagView.setVisibility(label.isSelected() ? View.VISIBLE : View.GONE);
        //itemView.setTranslationZ(label.isSelected() ? getDimension(R.dimen.ui_base_translationZ_small) : 0);
    }

    public void showContent(Dictionary dictionary) {
        textView.setText(dictionary.getName());
        containerView.setNormalColor(getColor(dictionary.isSelected() ? R.color.colorPrimary : R.color.textColorHintLight));
        tagView.setVisibility(dictionary.isSelected() ? View.VISIBLE : View.GONE);
    }

    @Click({R.id.ui_holder_view_activity_label_container})
    private void viewClick(View view) {
        if (null != mOnViewHolderClickListener) {
            mOnViewHolderClickListener.onClick(getAdapterPosition());
        }
    }
}
