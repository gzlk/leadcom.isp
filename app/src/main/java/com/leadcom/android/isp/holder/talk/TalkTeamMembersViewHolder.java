package com.leadcom.android.isp.holder.talk;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CorneredView;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.organization.GroupContactPickFragment;
import com.leadcom.android.isp.helper.popup.DeleteDialogHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.organization.SubMember;
import com.leadcom.android.isp.nim.constant.StatusCode;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.SimpleCallback;
import com.netease.nim.uikit.impl.cache.TeamDataCache;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.TeamServiceObserver;
import com.netease.nimlib.sdk.team.constant.TeamMemberType;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;

import java.util.ArrayList;
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
    @ViewId(R.id.ui_holder_view_talk_team_members_more)
    private LinearLayout morView;
    private View add, delete;

    private int margin, size;
    private boolean isAdmin = false, deletable = false, isUser = false;
    private String sessionId;

    public TalkTeamMembersViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        margin = getDimension(R.dimen.ui_base_dimen_margin_padding);
        int width = fragment.getScreenWidth();
        size = (width - margin * 6) / 5;
        initializeViews();
        registerObserver(true);
    }

    @Override
    public void detachedFromWindow() {
        registerObserver(false);
        super.detachedFromWindow();
    }

    private void registerObserver(boolean register) {
        NIMClient.getService(TeamServiceObserver.class).observeMemberUpdate(memberUpdateObserver, register);
    }

    private Observer<List<TeamMember>> memberUpdateObserver = new com.netease.nimlib.sdk.Observer<List<TeamMember>>() {
        @Override
        public void onEvent(List<TeamMember> members) {
            //displayMembers(members);
            fetchingMembers(sessionId);
        }
    };

    private void initializeViews() {
        if (null == add) {
            add = View.inflate(fragment().Activity(), R.layout.holder_view_talk_team_member_add, null);
            CustomTextView view = add.findViewById(R.id.ui_holder_view_talk_team_member_add_icon);
            view.setText(R.string.ui_icon_add);
            add.setOnClickListener(clickListener);
        }
        if (null == delete) {
            delete = View.inflate(fragment().Activity(), R.layout.holder_view_talk_team_member_add, null);
            CustomTextView view = delete.findViewById(R.id.ui_holder_view_talk_team_member_add_icon);
            view.setText(R.string.ui_icon_vertical_bar);
            delete.setOnClickListener(clickListener);
        }
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    /**
     * 设置是否是显示单用户方式
     */
    public void setUser(boolean user) {
        isUser = user;
        morView.setVisibility(isUser ? View.GONE : View.VISIBLE);
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
        View iconView = view.findViewById(R.id.ui_holder_view_talk_team_member_add_icon_layout);
        resetSizeParams(iconView, (LinearLayout.LayoutParams) iconView.getLayoutParams());
        resetMarginParams(view, (FlexboxLayout.LayoutParams) view.getLayoutParams());
    }

    private void resetSizeParams(View view, ViewGroup.MarginLayoutParams params) {
        params.width = size;
        params.height = size;
        view.setLayoutParams(params);
    }

    private void resetMarginParams(View view, FlexboxLayout.LayoutParams params) {
        int count = headers.getChildCount();
        int lines = count / 5 + (count % 5 > 0 ? 1 : 0);
        params.topMargin = lines > 1 ? margin : 0;
        params.rightMargin = count % 5 == 0 ? 0 : margin;
        view.setLayoutParams(params);
    }

    public void showContent(Model model) {
        sessionId = model.getId();
        isAdmin = model.isSelectable();
        if (isUser) {
            displayUser(NIMClient.getService(UserService.class).getUserInfo(sessionId));
            addView(add);
        } else {
            TeamDataCache.getInstance().fetchTeamMemberList(sessionId, new SimpleCallback<List<TeamMember>>() {
                @Override
                public void onResult(boolean success, List<TeamMember> result, int code) {
                    displayMembers(result);
                }
            });
        }
    }

    private void displayMembers(List<TeamMember> members) {
        headers.setVisibility((null == members || members.size() < 1) ? View.GONE : View.VISIBLE);
        headers.removeAllViews();
        if (null != members) {
            for (TeamMember member : members) {
                if (member.getTid().equals(sessionId)) {
                    UserInfo info = NimUIKit.getUserInfoProvider().getUserInfo(member.getAccount());
                    displayUser(member, info);
                }
            }
        }
        addView(add);
        if (isAdmin) {
            addView(delete);
        }
    }

    private void fetchingMembers(String tid) {
        TeamDataCache.getInstance().fetchTeamMemberList(tid, new SimpleCallback<List<TeamMember>>() {
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

    private void displayUser(NimUserInfo info) {
        View view = View.inflate(headers.getContext(), R.layout.holder_view_talk_team_member_head, null);
        ImageDisplayer head = view.findViewById(R.id.ui_holder_view_talk_team_member_head);
        TextView name = view.findViewById(R.id.ui_holder_view_talk_team_member_name);
        headers.addView(view);
        resetSizeParams(head, (RelativeLayout.LayoutParams) head.getLayoutParams());
        resetMarginParams(view, (FlexboxLayout.LayoutParams) view.getLayoutParams());
        name.setText(info.getName());
        head.displayImage(info.getAvatar(), size, false, false);

        head.setTag(R.id.hlklib_ids_custom_view_click_tag, info);
        head.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(ImageDisplayer displayer, String url) {
                NimUserInfo userInfo = (NimUserInfo) displayer.getTag(R.id.hlklib_ids_custom_view_click_tag);
                openUserProperty(userInfo.getAccount());
            }
        });
    }

    private void displayUser(TeamMember member, UserInfo info) {
        View view = View.inflate(headers.getContext(), R.layout.holder_view_talk_team_member_head, null);
        ImageDisplayer head = view.findViewById(R.id.ui_holder_view_talk_team_member_head);
        TextView name = view.findViewById(R.id.ui_holder_view_talk_team_member_name);
        view.findViewById(R.id.ui_holder_view_talk_team_member_manager).setVisibility(member.getType() == TeamMemberType.Owner ? View.VISIBLE : View.GONE);
        CorneredView mask = view.findViewById(R.id.ui_holder_view_talk_team_member_mask);
        mask.setTag(R.id.hlklib_ids_custom_view_click_tag, member);
        mask.setOnClickListener(clickListener);
        view.setTag(R.id.hlklib_ids_custom_view_click_tag, member);
        view.setOnClickListener(clickListener);
        headers.addView(view);
        resetSizeParams(head, (RelativeLayout.LayoutParams) head.getLayoutParams());
        resetMarginParams(view, (FlexboxLayout.LayoutParams) view.getLayoutParams());

        head.setTag(R.id.hlklib_ids_custom_view_click_tag, member);
        head.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(ImageDisplayer displayer, String url) {
                TeamMember mb = (TeamMember) displayer.getTag(R.id.hlklib_ids_custom_view_click_tag);
                openUserProperty(mb.getAccount());
            }
        });
        String header = null == info ? "" : info.getAvatar();
        head.displayImage(header, size, false, false);
        name.setText(member.getAccount().equals(Cache.cache().userId) ? StringHelper.getString(R.string.ui_base_text_myself) : (null == info ? StringHelper.getString(R.string.ui_base_text_no_name) : info.getName()));
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            if (v == add) {
                v.startAnimation(App.clickAnimation());
                ArrayList<SubMember> members = getExistsMembers();
                // 加入我自己
                SubMember me = new SubMember();
                me.setUserName(Cache.cache().userName);
                me.setUserId(Cache.cache().userId);
                if (!members.contains(me)) {
                    members.add(me);
                }
                GroupContactPickFragment.open(fragment(), "", true, false, SubMember.toJson(members));
            } else if (v == delete) {
                v.startAnimation(App.clickAnimation());
                deletable = !deletable;
                prepareDelete(deletable);
            } else {
                final TeamMember mb = (TeamMember) v.getTag(R.id.hlklib_ids_custom_view_click_tag);
                if (v instanceof CorneredView) {
                    // 删除成员
                    DeleteDialogHelper.helper().init(fragment()).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
                        @Override
                        public boolean onConfirm() {
                            removeMember(mb, v);
                            return true;
                        }
                    }).setTitleText(R.string.ui_team_talk_team_member_remove_dialog_title).setConfirmText(R.string.ui_base_text_remove).show();
                } else {
                    openUserProperty(mb.getAccount());
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
                ToastHelper.make().showMsg(StatusCode.getStatus(code));
            }

            @Override
            public void onException(Throwable exception) {

            }
        });
    }

    private ArrayList<SubMember> getExistsMembers() {
        ArrayList<SubMember> members = new ArrayList<>();
        for (int i = 0, len = headers.getChildCount(); i < len; i++) {
            View view = headers.getChildAt(i);
            if (view instanceof RelativeLayout && ((RelativeLayout) view).getChildCount() > 2) {
                Object object = view.getTag(R.id.hlklib_ids_custom_view_click_tag);
                SubMember sub = new SubMember();
                if (object instanceof TeamMember) {
                    TeamMember member = (TeamMember) object;
                    sub.setUserId(member.getAccount());
                    sub.setUserName(member.getTeamNick());
                    members.add(sub);
                } else if (object instanceof NimUserInfo) {
                    NimUserInfo info = (NimUserInfo) object;
                    sub.setUserId(info.getAccount());
                    sub.setUserName(info.getName());
                    members.add(sub);
                }
            }
        }
        return members;
    }

    private void prepareDelete(boolean delete) {
        for (int i = 0, len = headers.getChildCount(); i < len; i++) {
            View view = headers.getChildAt(i);
            if (view instanceof RelativeLayout && ((RelativeLayout) view).getChildCount() > 2) {
                TeamMember member = (TeamMember) view.getTag(R.id.hlklib_ids_custom_view_click_tag);
                // 管理员不能踢出自己
                boolean isSelf = member.getAccount().equals(Cache.cache().userId);
                view.findViewById(R.id.ui_holder_view_talk_team_member_mask).setVisibility((delete && !isSelf) ? View.VISIBLE : View.GONE);
            }
        }
        CustomTextView view = this.delete.findViewById(R.id.ui_holder_view_talk_team_member_add_icon);
        view.setText(delete ? R.string.ui_icon_rich_editor_undo_solid : R.string.ui_icon_vertical_bar);
        view.setRotation(delete ? 0 : 90);
    }

    private void openUserProperty(String account) {
        if (!account.equals(Cache.cache().userId)) {
            App.openUserInfo(fragment(), account);
        }
    }
}
