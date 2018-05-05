package com.leadcom.android.isp.holder.activity;

import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.nim.session.NimSessionHelper;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.SimpleCallback;
import com.netease.nim.uikit.impl.cache.TeamDataCache;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;


/**
 * <b>功能描述：</b>分享时的群聊列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/12/27 20:54 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ActivityListItemViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_nim_activity_list_item_image)
    private ImageDisplayer iconView;
    @ViewId(R.id.ui_nim_activity_list_item_name)
    private TextView nameView;

    public ActivityListItemViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(Team team) {
        iconView.displayImage(team.getIcon(), getDimension(R.dimen.ui_base_dimen_button_height), false, false);
        nameView.setText(team.getName());
    }

    public void showContent(RecentContact contact) {
        if (contact.getSessionType() == SessionTypeEnum.P2P) {
            // 点对点单聊
            NimUIKit.getUserInfoProvider().getUserInfoAsync(contact.getContactId(), new SimpleCallback<UserInfo>() {

                @Override
                public void onResult(boolean success, UserInfo info, int code) {
                    String img = null == info ? ("drawable://" + R.drawable.img_default_user_header) : info.getAvatar();
                    iconView.displayImage(img, getDimension(R.dimen.ui_base_dimen_button_height), false, false);
                    nameView.setText(null == info ? StringHelper.getString(R.string.ui_base_text_no_name) : info.getName());
                }
            });
        } else if (contact.getSessionType() == SessionTypeEnum.Team) {
            // 群聊
            Team team = TeamDataCache.getInstance().getTeamById(contact.getContactId());
            String img = (null == team || isEmpty(team.getIcon())) ? ("drawable://" + R.drawable.img_default_group_icon) : team.getIcon();
            iconView.displayImage(img, getDimension(R.dimen.ui_base_dimen_button_height), false, false);
            nameView.setText((null == team || isEmpty(team.getName())) ? StringHelper.getString(R.string.ui_base_text_no_name_team) : team.getName());
        }
    }

    @Click({R.id.ui_nim_activity_list_item})
    private void click(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        }
    }
}
