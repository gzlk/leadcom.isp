package com.leadcom.android.isp.holder.activity;

import android.view.View;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.model.organization.Member;
import com.leadcom.android.isp.model.user.User;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CorneredView;

/**
 * <b>功能描述：</b>活动成员<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/06 14:43 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/06 14:43 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ActivityMemberViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_activity_member_header)
    private ImageDisplayer headerImage;
    @ViewId(R.id.ui_holder_view_activity_member_manager)
    private CorneredView managerView;
    @ViewId(R.id.ui_holder_view_activity_member_name)
    private TextView nameView;

    private int size;

    public ActivityMemberViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        size = getDimension(R.dimen.ui_static_dp_70);
        ImageDisplayer.OnImageClickListener onImageClickListener = new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(ImageDisplayer displayer, String url) {
                if (null != mOnViewHolderClickListener) {
                    mOnViewHolderClickListener.onClick(getAdapterPosition());
                }
            }
        };
        headerImage.addOnImageClickListener(onImageClickListener);
    }

    public void showContent(User user) {
        headerImage.displayImage(user.getHeadPhoto(), size, false, false);
        managerView.setVisibility(user.isLocalDeleted() ? View.VISIBLE : View.GONE);
        String name = user.getName();
        if (isEmpty(name)) {
            name = user.getPhone();
        }
        nameView.setText(name);
    }

    public void showContent(Member member) {
        headerImage.displayImage(member.getHeadPhoto(), size, false, false);
        managerView.setVisibility(member.isLocalDeleted() ? View.VISIBLE : View.GONE);
        nameView.setText(member.getUserName());
    }

    @Click({R.id.ui_holder_view_activity_member_container})
    private void click(View view) {
        if (null != mOnViewHolderClickListener) {
            mOnViewHolderClickListener.onClick(getAdapterPosition());
        }
    }
}
