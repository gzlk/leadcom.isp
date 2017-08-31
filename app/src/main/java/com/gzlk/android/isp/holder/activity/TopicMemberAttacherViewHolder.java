package com.gzlk.android.isp.holder.activity;

import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.model.Model;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;

/**
 * <b>功能描述：</b>议题成员增加、删除Holder<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/08/31 22:50 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/08/31 22:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class TopicMemberAttacherViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_activity_topic_member_attach_icon)
    private CustomTextView iconView;
    @ViewId(R.id.ui_holder_view_activity_topic_member_attach_text)
    private TextView textView;

    public TopicMemberAttacherViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(Model model) {
        boolean isAdd = model.getId().equals("+");
        iconView.setText(isAdd ? R.string.ui_icon_add : R.string.ui_icon_vertical_bar);
        textView.setText(isAdd ? R.string.ui_phone_contact_invite : R.string.ui_activity_topic_property_members_delete);
    }

    @Click({R.id.ui_holder_view_activity_topic_member_attach_layout})
    private void elementClick(View view) {
        if (null != mOnViewHolderClickListener) {
            mOnViewHolderClickListener.onClick(getAdapterPosition());
        }
    }
}
