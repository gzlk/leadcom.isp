package com.leadcom.android.isp.holder.talk;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.model.Model;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/30 08:32 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/03/30 08:32 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class TalkTeamMemberAddViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_talk_team_member_add_icon_layout)
    private View iconLayout;
    @ViewId(R.id.ui_holder_view_talk_team_member_add_icon)
    private CustomTextView iconView;

    private int margin;

    public TalkTeamMemberAddViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        margin = getDimension(R.dimen.ui_base_dimen_margin_padding);
        int width = fragment.getScreenWidth();
        int size = (width - margin * 6) / 5;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) iconLayout.getLayoutParams();
        params.width = size;
        params.height = size;
        iconLayout.setLayoutParams(params);
    }

    public void showContent(Model model) {
        iconView.setText(model.getAccessToken());
    }

    public void showMargin(boolean left, boolean right) {
        RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
        param.topMargin = margin;
        param.leftMargin = left ? margin : 0;
        param.rightMargin = right ? 0 : margin;
        itemView.setLayoutParams(param);
    }

    @Click({R.id.ui_holder_view_talk_team_member_add_icon_layout})
    private void viewClick(View view) {
        view.startAnimation(App.clickAnimation());
        if (null != mOnViewHolderClickListener) {
            mOnViewHolderClickListener.onClick(getAdapterPosition());
        }
    }
}
