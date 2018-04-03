package com.leadcom.android.isp.fragment.talk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredButton;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.org.MemberRequest;
import com.leadcom.android.isp.api.team.TeamRequest;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.fragment.main.RecentContactsFragment;
import com.leadcom.android.isp.helper.popup.DeleteDialogHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.helper.popup.EditableDialogHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.common.SimpleClickableViewHolder;
import com.leadcom.android.isp.holder.common.TextViewHolder;
import com.leadcom.android.isp.holder.common.ToggleableViewHolder;
import com.leadcom.android.isp.holder.talk.TalkTeamMembersViewHolder;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.common.SimpleClickableItem;
import com.leadcom.android.isp.model.common.TalkTeam;
import com.leadcom.android.isp.model.organization.Member;
import com.leadcom.android.isp.model.organization.SubMember;
import com.leadcom.android.isp.nim.activity.SessionHistoryActivity;
import com.leadcom.android.isp.nim.callback.StickChangeCallback;
import com.leadcom.android.isp.nim.constant.StatusCode;
import com.leadcom.android.isp.nim.session.NimSessionHelper;
import com.netease.nim.uikit.api.model.SimpleCallback;
import com.netease.nim.uikit.business.session.helper.MessageListPanelHelper;
import com.netease.nim.uikit.business.team.model.TeamExtras;
import com.netease.nim.uikit.impl.cache.TeamDataCache;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.TeamServiceObserver;
import com.netease.nimlib.sdk.team.constant.TeamMemberType;
import com.netease.nimlib.sdk.team.constant.TeamMessageNotifyTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.UserServiceObserve;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * <b>功能描述：</b>沟通属性页<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/29 10:18 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/03/29 10:18 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class TalkTeamPropertyFragment extends BaseSwipeRefreshSupportFragment {

    public static TalkTeamPropertyFragment newInstance(String params) {
        TalkTeamPropertyFragment ttpf = new TalkTeamPropertyFragment();
        Bundle bundle = new Bundle();
        // 群聊的tid
        bundle.putString(PARAM_QUERY_ID, params);
        ttpf.setArguments(bundle);
        return ttpf;
    }

    public static void open(Context context, String tid, boolean isUser) {
        TalkTeamPropertyFragment.isUser = isUser;
        BaseActivity.openActivity(context, TalkTeamPropertyFragment.class.getName(), tid, REQUEST_CHANGE, true, false);
    }

    public static void open(BaseFragment fragment, String tid, boolean isUser) {
        TalkTeamPropertyFragment.isUser = isUser;
        fragment.openActivity(TalkTeamPropertyFragment.class.getName(), tid, REQUEST_CHANGE, true, false);
    }

    /**
     * 最近联系人置顶项目更改回调
     */
    public static StickChangeCallback stickChangeCallback;
    /**
     * 是否打开用户属性页；是否为添加用户
     */
    private static boolean isUser = false;
    private boolean isSelfOwner = false;
    private String[] items;
    private TeamAdapter mAdapter;
    // 最近联系人列表，为了查看置顶聊天
    private List<RecentContact> contacts;
    @ViewId(R.id.ui_talk_property_bottom_button)
    private CorneredButton bottomButton;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        enableSwipe(false);
        isLoadingComplete(true);
        // 点对点单聊不需要退出按钮
        bottomButton.setVisibility(isUser ? View.GONE : View.VISIBLE);
        registerObservers(true);
        items = StringHelper.getStringArray(R.array.ui_team_talk_property);
        fetchingRecentContacts();
    }

    @Override
    public void onDestroy() {
        registerObservers(false);
        super.onDestroy();
        isUser = false;
    }

    private void registerObservers(boolean register) {
        if (isUser) {
            NIMClient.getService(UserServiceObserve.class).observeUserInfoUpdate(userInfoObserver, register);
        } else {
            NIMClient.getService(TeamServiceObserver.class).observeTeamUpdate(teamUpdateObserver, register);
            NIMClient.getService(TeamServiceObserver.class).observeTeamRemove(teamRemoveObserver, register);
            // 注册最近联系人列表更改观察者
            NIMClient.getService(MsgServiceObserve.class).observeRecentContact(recentContactChangeObserver, register);
        }
    }

    /**
     * 群组资料更改监视
     */
    private Observer<List<Team>> teamUpdateObserver = new Observer<List<Team>>() {
        @Override
        public void onEvent(List<Team> teams) {
            for (Team team : teams) {
                if (team.getId().equals(mQueryId)) {
                    // 当前的组群资料更改了
                    initializeTeam(team);
                    //SimpleClickableItem item = new SimpleClickableItem(format(items[1], team.getName()));
                    //mAdapter.update(item);
                }
            }
        }
    };

    /**
     * 群组删除或解散
     */
    private Observer<Team> teamRemoveObserver = new Observer<Team>() {
        @Override
        public void onEvent(Team team) {
            if (null != team && team.getId().equals(mQueryId)) {
                if (!isSelfOwner) {
                    // 不是组群所有者时提醒组群解散了
                    DeleteDialogHelper.helper().init(TalkTeamPropertyFragment.this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
                        @Override
                        public boolean onConfirm() {
                            Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    resultData(TeamExtras.RESULT_EXTRA_REASON_QUIT);
                                }
                            }, duration());
                            return true;
                        }
                    }).setConfirmText(R.string.ui_base_text_confirm).setTitleText(R.string.ui_team_talk_team_dismissed).show();
                }
            }
        }
    };

    /**
     * 用户信息更改观察者
     */
    private Observer<List<NimUserInfo>> userInfoObserver = new Observer<List<NimUserInfo>>() {
        @Override
        public void onEvent(List<NimUserInfo> users) {
            if (null != users) {
                for (NimUserInfo info : users) {
                    if (info.getAccount().equals(mQueryId)) {
                        // 更改用户的信息
                        initializeUser(info);
                    }
                }
            }
        }
    };

    Observer<List<RecentContact>> recentContactChangeObserver = new Observer<List<RecentContact>>() {
        @Override
        public void onEvent(List<RecentContact> recentContacts) {
            log("message observer onEvent: " + (null == recentContacts ? "null" : recentContacts.size()));
            if (null != recentContacts) {
                for (RecentContact contact : recentContacts) {
                    int index = indexOfContact(contact);
                    if (index >= 0) {
                        contacts.set(index, contact);
                    } else {
                        contacts.add(contact);
                    }
                }
                // 最近联系人列表有变动时，如果当前群聊在里面，则更新群聊状态
            }
        }
    };

    private int indexOfContact(RecentContact contact) {
        for (int i = 0, len = contacts.size(); i < len; i++) {
            RecentContact recent = contacts.get(i);
            if (recent.getContactId().equals(contact.getContactId()) && recent.getSessionType() == contact.getSessionType()) {
                return i;
            }
        }
        return -1;
    }

    private void fetchingRecentContacts() {
        NIMClient.getService(MsgService.class).queryRecentContacts().setCallback(new RequestCallbackWrapper<List<RecentContact>>() {
            @Override
            public void onResult(int code, List<RecentContact> recents, Throwable exception) {
                if (code != ResponseCode.RES_SUCCESS || recents == null) {
                    return;
                }
                contacts = recents;
                // 查找当前用户或群聊是否是置顶
                RecentContact contact = getRecentContact();
                if (null != contact) {
                    SimpleClickableItem item = new SimpleClickableItem(format(items[3], isTagSet(contact) ? 1 : 0));
                    if (null != mAdapter && mAdapter.exist(item)) {
                        // 如果列表存在则更新，否则不用
                        mAdapter.update(item);
                    }
                }
            }
        });
    }

    private RecentContact getRecentContact() {
        if (null == contacts || contacts.size() < 1) return null;
        for (RecentContact contact : contacts) {
            if (contact.getContactId().equals(mQueryId)) {
                return contact;
            }
        }
        return null;
    }

    private void addTag(RecentContact recent) {
        long tag = recent.getTag() | RecentContactsFragment.RECENT_TAG_STICKY;
        recent.setTag(tag);
    }

    private void removeTag(RecentContact recent) {
        long tag = recent.getTag() & ~RecentContactsFragment.RECENT_TAG_STICKY;
        recent.setTag(tag);
    }

    private boolean isTagSet(RecentContact recent) {
        return (recent.getTag() & RecentContactsFragment.RECENT_TAG_STICKY) == RecentContactsFragment.RECENT_TAG_STICKY;
    }

    @Override
    protected void onSwipeRefreshing() {

    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    @Override
    protected void onDelayRefreshComplete(int type) {

    }

    @Override
    public int getLayout() {
        return R.layout.fragment_talk_property;
    }

    @Override
    public void doingInResume() {
        initializeAdapter();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    public void resultData(String data) {
        Activity().setResult(Activity.RESULT_OK, new Intent().putExtra(TeamExtras.RESULT_EXTRA_REASON, data));
        finish();
    }

    @Click({R.id.ui_talk_property_bottom_button})
    private void click(View view) {
        if (bottomButton.getText().toString().equals(getString(R.string.ui_team_talk_quit_team_owner))) {
            // 解散群聊
            DeleteDialogHelper.helper().init(TalkTeamPropertyFragment.this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
                @Override
                public boolean onConfirm() {
                    dismissTeam();
                    return true;
                }
            }).setTitleText(R.string.ui_team_talk_team_dismiss_dialog_title).setConfirmText(R.string.ui_base_text_dismiss).show();
        } else {
            // 退出群聊
            DeleteDialogHelper.helper().init(TalkTeamPropertyFragment.this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
                @Override
                public boolean onConfirm() {
                    quitTeam();
                    return true;
                }
            }).setTitleText(R.string.ui_team_talk_team_quit_dialog_title).setConfirmText(R.string.ui_base_text_confirm).show();
        }
    }

    private void quitTeam() {
        MemberRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Member>() {
            @Override
            public void onResponse(Member member, boolean success, String message) {
                super.onResponse(member, success, message);
                if (success) {
                    ToastHelper.make().showMsg(R.string.ui_team_talk_quit_team_successfully);
                    resultData(TeamExtras.RESULT_EXTRA_REASON_QUIT);
                }
            }
        }).exitTeam(mQueryId);
    }

    private void dismissTeam() {
        MemberRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Member>() {
            @Override
            public void onResponse(Member member, boolean success, String message) {
                super.onResponse(member, success, message);
                if (success) {
                    ToastHelper.make().showMsg(R.string.ui_team_talk_team_dismissed);
                    resultData(TeamExtras.RESULT_EXTRA_REASON_DISMISS);
                }
            }
        }).exitTeam(mQueryId);
    }

    private void initializeTeam(Team team) {
        TeamMember self = TeamDataCache.getInstance().getTeamMember(mQueryId, Cache.cache().userId);
        isSelfOwner = null != self && self.getType() == TeamMemberType.Owner;
        setCustomTitle(team.getName());
        Model m = new Model();
        m.setId(team.getId());
        m.setSelectable(isSelfOwner);
        if (mAdapter.exist(m)) {
            mAdapter.update(m);
        } else {
            mAdapter.add(m, 0);
        }
        bottomButton.setText(R.string.ui_team_talk_quit_team);
        for (String item : items) {
            if (item.charAt(0) == '-') {
                Model model = new Model();
                model.setId(item);
                mAdapter.update(model);
            } else if (item.startsWith("2|")) {
                SimpleClickableItem transfer = new SimpleClickableItem(item);
                // 管理权转让
                TeamMember member = TeamDataCache.getInstance().getTeamMember(mQueryId, Cache.cache().userId);
                // 群聊拥有者或管理员可以转让管理权
                if (null != member && (member.getType() == TeamMemberType.Manager || member.getType() == TeamMemberType.Owner)) {
                    // 如果当前用户是群拥有者
                    if (member.getType() == TeamMemberType.Owner) {
                        isSelfOwner = true;
                        m.setSelectable(isSelfOwner);
                        mAdapter.update(m);
                        bottomButton.setText(R.string.ui_team_talk_quit_team_owner);
                    }
                    int index = mAdapter.indexOf(transfer);
                    if (index >= 0) {
                        mAdapter.update(transfer);
                    } else {
                        SimpleClickableItem name = new SimpleClickableItem(items[1]);
                        // 加到名称后面
                        mAdapter.add(transfer, mAdapter.indexOf(name) + 1);
                    }
                } else {
                    mAdapter.remove(transfer);
                }
            } else if (item.startsWith("3|")) {
                // 置顶
                RecentContact contact = getRecentContact();
                SimpleClickableItem click = new SimpleClickableItem(format(items[3], null == contact ? 0 : (isTagSet(contact) ? 1 : 0)));
                mAdapter.update(click);
            } else if (item.startsWith("4|")) {
                // 是否静音
                SimpleClickableItem click = new SimpleClickableItem(format(item, team.getMessageNotifyType() == TeamMessageNotifyTypeEnum.Mute ? 1 : 0));
                mAdapter.update(click);
            } else {
                SimpleClickableItem click = new SimpleClickableItem(item.startsWith("1|") ? format(item, team.getName()) : item);
                mAdapter.update(click);
            }
        }
    }

    private void fetchingTeam() {
        TeamDataCache.getInstance().fetchTeamById(mQueryId, new SimpleCallback<Team>() {
            @Override
            public void onResult(boolean success, Team result, int code) {
                if (success && null != result) {
                    initializeTeam(result);
                }
            }
        });
    }

    private void fetchingUser() {
        ArrayList<String> ids = new ArrayList<>();
        ids.add(mQueryId);
        NIMClient.getService(UserService.class).fetchUserInfo(ids).setCallback(new RequestCallback<List<NimUserInfo>>() {
            @Override
            public void onSuccess(List<NimUserInfo> param) {
                if (null != param && param.size() > 0) {
                    initializeUser(param.get(0));
                }
            }

            @Override
            public void onFailed(int code) {
                ToastHelper.make().showMsg(StringHelper.getString(R.string.ui_team_talk_user_info_fetch_fail, code));
            }

            @Override
            public void onException(Throwable exception) {

            }
        });
    }

    private void initializeUser(NimUserInfo info) {
        setCustomTitle(info.getName());
        Model m = new Model();
        m.setId(mQueryId);
        mAdapter.update(m);
        for (String item : items) {
            if (item.charAt(0) == '-') {
                Model model = new Model();
                model.setId(item);
                mAdapter.update(model);
            } else if (item.startsWith("3|")) {
                // 置顶
                RecentContact contact = getRecentContact();
                SimpleClickableItem click = new SimpleClickableItem(format(items[3], null == contact ? 0 : (isTagSet(contact) ? 1 : 0)));
                mAdapter.update(click);
            } else if (item.startsWith("4|")) {
                // 是否通知
                boolean notify = NIMClient.getService(FriendService.class).isNeedMessageNotify(mQueryId);
                SimpleClickableItem click = new SimpleClickableItem(format(item, notify ? 0 : 1));
                mAdapter.update(click);
            } else if (!item.startsWith("1|") && !item.startsWith("2|")) {
                // 个人聊天属性页不需要名称、管理权转让两项
                SimpleClickableItem click = new SimpleClickableItem(item);
                mAdapter.update(click);
            }
        }
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new TeamAdapter();
            mRecyclerView.setAdapter(mAdapter);
            OverScrollDecoratorHelper.setUpOverScroll(mRecyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
            if (isUser) {
                fetchingUser();
            } else {
                fetchingTeam();
            }
        }
    }

    private OnViewHolderClickListener clickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            if (index == 0) {
                // 打开群的所有成员列表
                TalkTeamMembersFragment.open(TalkTeamPropertyFragment.this, mQueryId, false, false);
            } else {
                SimpleClickableItem item = (SimpleClickableItem) mAdapter.get(index);
                switch (item.getIndex()) {
                    case 1:
                        // 组群名称，组群拥有着可以更改
                        if (isSelfOwner) {
                            prepareEditTeamName(item.getValue());
                        }
                        break;
                    case 2:
                        // 管理权转让，组群拥有着可以更改
                        warningTransferAdmin();
                        break;
                    case 5:
                        // 查看聊天记录
                        SessionHistoryActivity.start(Activity(), mQueryId, isUser ? SessionTypeEnum.P2P : SessionTypeEnum.Team);
                        break;
                    case 6:
                        // 删除聊天记录
                        warningCleanChatHistory();
                        break;
                }
            }
        }
    };

    private EditableDialogHelper editableDialogHelper;

    private void prepareEditTeamName(final String oldName) {
        if (null == editableDialogHelper) {
            editableDialogHelper = EditableDialogHelper.helper().init(this);
        }
        editableDialogHelper.setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                String input = editableDialogHelper.getInputValue();
                if (isEmpty(input)) {
                    ToastHelper.make().showMsg(R.string.ui_team_talk_team_name_change_dialog_input_empty);
                    return false;
                } else if (!input.equals(oldName)) {
                    TeamRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<TalkTeam>() {
                        @Override
                        public void onResponse(TalkTeam talkTeam, boolean success, String message) {
                            super.onResponse(talkTeam, success, message);
                        }
                    }).update(mQueryId, input, null);
                    return true;
                }
                return true;
            }
        }).setTitleText(R.string.ui_team_talk_team_name_change_dialog_title).setInputValue(oldName).setInputHint(R.string.ui_team_talk_team_name_change_dialog_title).show();
    }

    private void warningCleanChatHistory() {
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                NIMClient.getService(MsgService.class).clearChattingHistory(mQueryId, isUser ? SessionTypeEnum.P2P : SessionTypeEnum.Team);
                MessageListPanelHelper.getInstance().notifyClearMessages(mQueryId);
                return true;
            }
        }).setTitleText(R.string.ui_team_talk_clean_chat_history_dialog_title).setConfirmText(R.string.ui_base_text_delete).show();
    }

    private void warningTransferAdmin() {
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                // 非添加用户，说明是要转让管理权
                TalkTeamMembersFragment.open(TalkTeamPropertyFragment.this, mQueryId, true, false);
                return true;
            }
        }).setTitleText(R.string.ui_team_talk_team_transfer_admin_dialog_title).setConfirmText(R.string.ui_base_text_confirm).show();
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SELECT:
                // 转让管理权的用户选择返回了
                String result = getResultedData(data);
                if (!isEmpty(result)) {
                    SubMember member = SubMember.fromJsonOne(result);
                    if (null != member && !isEmpty(member.getUserId())) {
                        // 转让管理权给选中的成员
                        warningTransferAdminConfirm(member.getUserId(), member.getUserName());
                    }
                }
                break;
            case REQUEST_CHANGE:
                // 从成员列表里返回，可能有增删用户，所以需要重新刷新一下成员列表
                mAdapter.notifyItemChanged(0);
                break;
            case REQUEST_MEMBER:
                // 选择了成员并返回了
                String json = getResultedData(data);
                if (!isEmpty(json) && json.length() > 10) {
                    ArrayList<SubMember> members = SubMember.fromJson(json);
                    if (isUser) {
                        // 单用户时，需要创建一个新的群聊
                        prepareCreateTeam(members);
                    } else {
                        prepareAddUser(members);
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, data);
    }

    private void prepareCreateTeam(ArrayList<SubMember> members) {
        TalkTeam team = new TalkTeam();
        team.setTitle(StringHelper.getString(R.string.ui_team_talk_team_new_title, members.size()));
        team.setUserIdList(SubMember.getUserIds(members));
        TeamRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<TalkTeam>() {
            @Override
            public void onResponse(TalkTeam talkTeam, boolean success, String message) {
                super.onResponse(talkTeam, success, message);
                if (success) {
                    if (null != talkTeam) {
                        NimSessionHelper.startTeamSession(Activity(), talkTeam.getTid());
                        resultData(TeamExtras.RESULT_EXTRA_REASON_QUIT);
                    }
                }
            }
        }).add(team);
    }

    private void prepareAddUser(ArrayList<SubMember> members) {
        ArrayList<String> ids = new ArrayList<>();
        String name = "";
        int count = 0;
        if (null != members && members.size() > 0) {
            for (SubMember member : members) {
                TeamMember tm = TeamDataCache.getInstance().getTeamMember(mQueryId, member.getUserId());
                if (null == tm || !tm.isInTeam()) {
                    // 成员中不存在用户时才添加
                    ids.add(member.getUserId());
                    if (count < 3) {
                        name += (isEmpty(name) ? "" : "、") + member.getUserName();
                    }
                    count++;
                }
            }
            name = "[" + name + "]";
            if (ids.size() > 1) {
                name += "等";
            }
        }
        if (ids.size() > 0) {
            warningAddNewUser(ids, name);
        } else {
            ToastHelper.make().showMsg(R.string.ui_team_talk_team_member_add_no_new_member);
        }
    }

    private void warningAddNewUser(final ArrayList<String> userIds, String names) {
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                addNewUser(userIds);
                return true;
            }
        }).setTitleText(getString(R.string.ui_team_talk_team_member_add_dialog_title, names, userIds.size())).setConfirmText(R.string.ui_base_text_confirm).show();
    }

    private void addNewUser(ArrayList<String> userIds) {
        MemberRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Member>() {
            @Override
            public void onResponse(Member member, boolean success, String message) {
                super.onResponse(member, success, message);
                if (success) {
                    ToastHelper.make().showMsg(R.string.ui_team_talk_team_member_add_complete);
                    // 重新刷新成员列表
                    mAdapter.notifyItemChanged(0);
                }
            }
        }).addTeamMember(mQueryId, userIds);
    }

    /**
     * 转让确定
     */
    private void warningTransferAdminConfirm(final String userId, String userName) {
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                MemberRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Member>() {
                    @Override
                    public void onResponse(Member member, boolean success, String message) {
                        super.onResponse(member, success, message);
                        if (success) {
                            // 重新拉取组群信息
                            mAdapter.clear();
                            fetchingTeam();
                        }
                    }
                }).grantManager(mQueryId, userId);
                return true;
            }
        }).setTitleText(getString(R.string.ui_team_talk_team_transfer_admin_confirm_dialog_title, userName)).setConfirmText(R.string.ui_base_text_confirm).show();
    }

    private ToggleableViewHolder.OnViewHolderToggleChangedListener toggleChangedListener = new ToggleableViewHolder.OnViewHolderToggleChangedListener() {
        @Override
        public void onChange(int index, boolean togged) {
            SimpleClickableItem item = (SimpleClickableItem) mAdapter.get(index);
            if (item.getIndex() == 3) {
                // 置顶群聊
                RecentContact contact = getRecentContact();
                if (null != contact) {
                    if (isTagSet(contact)) {
                        addTag(contact);
                    } else {
                        removeTag(contact);
                    }
                    NIMClient.getService(MsgService.class).updateRecent(contact);
                    if (null != stickChangeCallback) {
                        stickChangeCallback.onChange(contact);
                    }
                }
            } else if (item.getIndex() == 4) {
                // 静音
                if (isUser) {
                    notifyUser(!togged);
                } else {
                    // 群聊静音
                    muteTeam(togged);
                }
            }
        }
    };

    /**
     * 设置用户消息是否提醒
     *
     * @param notify true=提醒，false=静音
     */
    private void notifyUser(final boolean notify) {
        NIMClient.getService(FriendService.class).setMessageNotify(mQueryId, notify).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                ToastHelper.make().showMsg(notify ? R.string.ui_team_talk_team_mute : R.string.ui_team_talk_team_muted);
            }

            @Override
            public void onFailed(int code) {
                ToastHelper.make().showMsg(StringHelper.getString(R.string.ui_team_talk_user_mute_fail, StatusCode.getStatus(code)));
            }

            @Override
            public void onException(Throwable exception) {

            }
        });
    }

    private void muteTeam(final boolean mute) {
        NIMClient.getService(TeamService.class).muteTeam(mQueryId, mute ? TeamMessageNotifyTypeEnum.Mute : TeamMessageNotifyTypeEnum.All).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                ToastHelper.make().showMsg(mute ? R.string.ui_team_talk_team_muted : R.string.ui_team_talk_team_mute);
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

    private class TeamAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {

        private static final int VT_MEMBERS = 0, VT_LINE = 1, VT_CLICK = 2, VT_TOGGLE = 3;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            switch (viewType) {
                case VT_LINE:
                    return new TextViewHolder(itemView, TalkTeamPropertyFragment.this);
                case VT_CLICK:
                    SimpleClickableViewHolder scvh = new SimpleClickableViewHolder(itemView, TalkTeamPropertyFragment.this);
                    scvh.addOnViewHolderClickListener(clickListener);
                    return scvh;
                case VT_TOGGLE:
                    ToggleableViewHolder tvh = new ToggleableViewHolder(itemView, TalkTeamPropertyFragment.this);
                    tvh.addOnViewHolderToggleChangedListener(toggleChangedListener);
                    return tvh;
                case VT_MEMBERS:
                    TalkTeamMembersViewHolder ttmvh = new TalkTeamMembersViewHolder(itemView, TalkTeamPropertyFragment.this);
                    ttmvh.addOnViewHolderClickListener(clickListener);
                    ttmvh.setUser(isUser);
                    return ttmvh;
            }
            return null;
        }

        @Override
        public int itemLayout(int viewType) {
            switch (viewType) {
                case VT_MEMBERS:
                    return R.layout.holder_view_talk_team_member;
                case VT_LINE:
                    return R.layout.tool_view_divider_big;
                case VT_TOGGLE:
                    return R.layout.holder_view_toggle;
            }
            return R.layout.holder_view_simple_clickable;
        }

        @Override
        public int getItemViewType(int position) {
            Model model = get(position);
            if (model instanceof SimpleClickableItem) {
                if (model.getId().equals("3") || model.getId().equals("4")) {
                    return VT_TOGGLE;
                }
                return VT_CLICK;
            }
            if (model.getId().equals("-")) {
                return VT_LINE;
            }
            return VT_MEMBERS;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof SimpleClickableViewHolder) {
                ((SimpleClickableViewHolder) holder).showContent(item);
            } else if (holder instanceof ToggleableViewHolder) {
                ((ToggleableViewHolder) holder).showContent((SimpleClickableItem) item);
            } else if (holder instanceof TalkTeamMembersViewHolder) {
                ((TalkTeamMembersViewHolder) holder).showContent(item);
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
