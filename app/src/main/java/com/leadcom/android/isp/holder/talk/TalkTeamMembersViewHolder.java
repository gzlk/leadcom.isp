package com.leadcom.android.isp.holder.talk;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CorneredView;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.individual.UserPropertyFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.organization.Member;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.SimpleCallback;
import com.netease.nim.uikit.impl.cache.TeamDataCache;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;

import java.util.List;

/**
 * <b>功能描述：</b>群聊用户列表ViewHolder<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/29 12:57 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/03/29 12:57 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class TalkTeamMembersViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_talk_team_members)
    private FlexboxLayout headers;
    private CorneredView add, delete;

    private int margin, size;
    private boolean isAdmin = false, deletable = false;

    public TalkTeamMembersViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        margin = getDimension(R.dimen.ui_base_dimen_margin_padding);
        int width = fragment.getScreenWidth();
        size = (width - margin * 6) / 5;
        initializeViews();
    }

    private void initializeViews() {
        if (null == add) {
            add = (CorneredView) View.inflate(fragment().Activity(), R.layout.holder_view_talk_team_member_add, null);
            CustomTextView view = (CustomTextView) add.getChildAt(0);
            view.setText(R.string.ui_icon_add);
            add.setOnClickListener(clickListener);
        }
        if (null == delete) {
            delete = (CorneredView) View.inflate(fragment().Activity(), R.layout.holder_view_talk_team_member_add, null);
            CustomTextView view = (CustomTextView) delete.getChildAt(0);
            view.setText(R.string.ui_icon_vertical_bar);
            delete.setOnClickListener(clickListener);
        }
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @Click({R.id.ui_holder_view_talk_team_members_more})
    private void viewClick(View view) {
        if (null != mOnViewHolderClickListener) {
            mOnViewHolderClickListener.onClick(getAdapterPosition());
        }
    }

    private void addView(View view) {
        fragment().clearDirectParent(view);
        headers.addView(view);
    }

    public void showContent(Model model) {
        TeamDataCache.getInstance().fetchTeamMemberList(model.getId(), new SimpleCallback<List<TeamMember>>() {
            @Override
            public void onResult(boolean success, List<TeamMember> result, int code) {
                headers.setVisibility((null == result || result.size() < 1) ? View.GONE : View.VISIBLE);
                headers.removeAllViews();
                if (success && null != result) {
                    for (TeamMember member : result) {
                        UserInfo info = NimUIKit.getUserInfoProvider().getUserInfo(member.getAccount());
                        displayUser(member, info);
                    }
                }
                addView(add);
                if (isAdmin) {
                    addView(delete);
                }
            }
        });
    }

    private void displayUser(TeamMember member, UserInfo info) {
        View view = View.inflate(headers.getContext(), R.layout.holder_view_talk_team_member_head, null);
        ImageDisplayer head = view.findViewById(R.id.ui_holder_view_talk_team_member_head);
        TextView name = view.findViewById(R.id.ui_holder_view_talk_team_member_name);
        CorneredView mask = view.findViewById(R.id.ui_holder_view_talk_team_member_mask);
        mask.setTag(R.id.hlklib_ids_custom_view_click_tag, member);
        mask.setOnClickListener(clickListener);
        view.setTag(R.id.hlklib_ids_custom_view_click_tag, member);
        view.setOnClickListener(clickListener);
        headers.addView(view);
        int count = headers.getChildCount();
        int lines = count / 5 + (count % 5 > 0 ? 1 : 0);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) head.getLayoutParams();
        params.topMargin = lines > 1 ? margin : 0;
        params.rightMargin = count % 5 == 0 ? 0 : margin;
        params.width = size;
        params.height = size;
        head.setLayoutParams(params);

        head.setTag(R.id.hlklib_ids_custom_view_click_tag, member);
        head.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(ImageDisplayer displayer, String url) {
                TeamMember mb = (TeamMember) displayer.getTag(R.id.hlklib_ids_custom_view_click_tag);
                openUserProperty(mb);
            }
        });
        String header = null == info ? "" : info.getAvatar();
        header = isEmpty(header) ? "" : header;
        head.displayImage(header, size, false, false);
        name.setText(member.getAccount().equals(Cache.cache().userId) ? "我" : (null == info ? "无名氏" : info.getName()));
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == add) {
            } else if (v == delete) {
                deletable = !deletable;
                prepareDelete(deletable);
            } else {
                TeamMember mb = (TeamMember) v.getTag(R.id.hlklib_ids_custom_view_click_tag);
                if (v instanceof CorneredView) {
                    // 删除成员
                    removeMember(mb, v);
                } else {
                    openUserProperty(mb);
                }
            }
        }
    };

    private void removeMember(TeamMember member, final View view) {
        NIMClient.getService(TeamService.class).removeMember(member.getTid(), member.getAccount()).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                headers.removeView((View) view.getParent());
                ToastHelper.make().showMsg(R.string.ui_team_talk_team_member_removed);
            }

            @Override
            public void onFailed(int code) {
                ToastHelper.make().showMsg(StringHelper.getString(R.string.ui_team_talk_team_member_remove_fail, code));
            }

            @Override
            public void onException(Throwable exception) {

            }
        });
    }

    private void prepareDelete(boolean delete) {
        for (int i = 0, len = headers.getChildCount(); i < len; i++) {
            View view = headers.getChildAt(i);
            if (view instanceof RelativeLayout && ((RelativeLayout) view).getChildCount() > 2) {
                view.findViewById(R.id.ui_holder_view_talk_team_member_mask).setVisibility(delete ? View.VISIBLE : View.GONE);
            }
        }
    }

    private void openUserProperty(TeamMember member) {
        if (!member.getAccount().equals(Cache.cache().userId)) {
            UserPropertyFragment.open(fragment(), member.getAccount());
        }
    }
}
