package com.leadcom.android.isp.holder.talk;

import android.text.Html;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.model.user.SimpleUser;

/**
 * <b>功能描述：</b>群成员ViewHolder<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/29 16:18 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/03/29 16:18 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class TalkTeamMemberViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_talk_team_member_head_layout)
    private RelativeLayout root;
    @ViewId(R.id.ui_holder_view_talk_team_member_head)
    private ImageDisplayer headView;
    @ViewId(R.id.ui_holder_view_talk_team_member_name)
    private TextView nameView;
    @ViewId(R.id.ui_holder_view_talk_team_member_manager)
    private View managerIcon;
    @ViewId(R.id.ui_holder_view_talk_team_member_mask)
    private View maskView;
    @ViewId(R.id.ui_holder_view_talk_team_member_mask_icon)
    private CustomTextView maskIcon;

    private int margin, size;

    public TalkTeamMemberViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        headView.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(ImageDisplayer displayer, String url) {
                root.performClick();
            }
        });
        margin = getDimension(R.dimen.ui_base_dimen_margin_padding);
        int width = fragment.getScreenWidth();
        size = (width - margin * 6) / 5;
    }

    public void showContent(SimpleUser user, String searchingText, boolean selectable) {
        headView.displayImage(user.getHeadPhoto(), size, false, false);
        String name = user.getUserName();
        boolean isSelf = user.getUserId().equals(Cache.cache().userId);
        if (isSelf) {
            name = StringHelper.getString(R.string.ui_base_text_myself);
        }
        if (!isEmpty(name)) {
            name = getSearchingText(name, searchingText);
        }
        nameView.setText(Html.fromHtml(name));
        managerIcon.setVisibility(user.isRead() ? View.VISIBLE : View.GONE);
        if (selectable) {
            maskView.setVisibility(user.isSelected() && !isSelf ? View.VISIBLE : View.GONE);
            maskIcon.setText(R.string.ui_icon_select_solid);
            maskIcon.setTextColor(getColor(R.color.colorPrimary));
        } else {
            maskView.setVisibility(user.isSelectable() && !isSelf ? View.VISIBLE : View.GONE);
        }
    }

    public void showMargin(boolean left, boolean right) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) headView.getLayoutParams();
        params.topMargin = margin;
        params.rightMargin = right ? 0 : margin;
        params.leftMargin = left ? margin : 0;
        params.width = size;
        params.height = size;
        headView.setLayoutParams(params);
    }

    @Click({R.id.ui_holder_view_talk_team_member_head_layout, R.id.ui_holder_view_talk_team_member_mask})
    private void onClick(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        }
    }
}
