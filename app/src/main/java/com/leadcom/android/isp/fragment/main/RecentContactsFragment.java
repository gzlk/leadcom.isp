package com.leadcom.android.isp.fragment.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.team.TeamRequest;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.fragment.organization.GroupContactPickFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.TooltipHelper;
import com.leadcom.android.isp.holder.activity.ActivityViewHolder;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.common.TalkTeam;
import com.leadcom.android.isp.model.organization.SubMember;
import com.leadcom.android.isp.nim.session.NimSessionHelper;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.contact.ContactChangedObserver;
import com.netease.nim.uikit.api.model.team.TeamDataChangedObserver;
import com.netease.nim.uikit.api.model.team.TeamMemberDataChangedObserver;
import com.netease.nim.uikit.api.model.user.UserInfoObserver;
import com.netease.nim.uikit.business.recent.TeamMemberAitHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * <b>功能描述：</b>最近会话列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/08 09:02 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/03/08 09:02 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class RecentContactsFragment extends BaseSwipeRefreshSupportFragment {

    // 置顶功能可直接使用，也可作为思路，供开发者充分利用RecentContact的tag字段
    public static final long RECENT_TAG_STICKY = 1; // 联系人置顶tag

    @ViewId(R.id.ui_main_tool_bar_container)
    private View toolBar;
    public MainFragment mainFragment;
    private ContactAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        enableSwipe(false);
        isLoadingComplete(true);
        tryPaddingContent(toolBar, false);
        setNothingText(R.string.ui_recent_contacts_nothing);
        if (null != mainFragment) {
            mainFragment.showUnreadFlag(NIMClient.getService(MsgService.class).getTotalUnreadCount());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registerObservers(false);
    }

    @Override
    protected void onDelayRefreshComplete(int type) {

    }

    @Override
    public void doingInResume() {
        initializeAdapter();
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_main_recent_contacts;
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void destroyView() {

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

    @Click({R.id.ui_recent_contacts_add})
    private void viewClick(View view) {
        view.startAnimation(App.clickAnimation());
        showTooltip(view, R.id.ui_tooltip_recent_contact, true, TooltipHelper.TYPE_RIGHT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ui_tooltip_menu_recent_contact_list:
                        TeamListFragment.open(RecentContactsFragment.this);
                        break;
                    case R.id.ui_tooltip_menu_recent_contact_create:
                        ArrayList<SubMember> members = new ArrayList<>();
                        SubMember me = new SubMember();
                        me.setUserId(Cache.cache().userId);
                        me.setUserName(Cache.cache().userName);
                        members.add(me);
                        GroupContactPickFragment.open(RecentContactsFragment.this, "", true, false, SubMember.toJson(members));
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == REQUEST_MEMBER) {
            String json = getResultedData(data);
            if (!isEmpty(json) && json.length() > 10) {
                ArrayList<SubMember> members = SubMember.fromJson(json);
                prepareCreateTeam(members);
            }
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
                        // 打开新群并可以直接聊天
                        NimSessionHelper.startTeamSession(Activity(), talkTeam.getTid());
                    }
                }
            }
        }).add(team);
    }

    /**
     * ********************** 收消息，处理状态变化 ************************
     */
    private void registerObservers(boolean register) {
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeReceiveMessage(messageReceiverObserver, register);
        service.observeRecentContact(recentContactChangeObserver, register);
        service.observeMsgStatus(msgStatusObserver, register);
        service.observeRecentContactDeleted(recentContactDeleteObserver, register);

        //registerTeamObservers(register);
        //NimUIKit.getContactChangedObservable().registerObserver(friendDataChangedObserver, register);
        //registerUserInfoObservers(register);
    }

    private void registerTeamObservers(boolean register) {
        NimUIKit.getTeamChangedObservable().registerTeamDataChangedObserver(teamDataChangedObserver, register);
        NimUIKit.getTeamChangedObservable().registerTeamMemberDataChangedObserver(teamMemberDataChangedObserver, register);
    }

    private void registerUserInfoObservers(boolean register) {
        NimUIKit.getUserInfoObservable().registerObserver(userInfoObserver, register);
    }

    // 暂存消息，当RecentContact 监听回来时使用，结束后清掉
    private Map<String, Set<IMMessage>> cacheMessages = new HashMap<>();

    //监听在线消息中是否有@我
    private Observer<List<IMMessage>> messageReceiverObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> imMessages) {
            if (imMessages != null) {
                for (IMMessage imMessage : imMessages) {
                    if (!TeamMemberAitHelper.isAitMessage(imMessage)) {
                        continue;
                    }
                    Set<IMMessage> cacheMessageSet = cacheMessages.get(imMessage.getSessionId());
                    if (cacheMessageSet == null) {
                        cacheMessageSet = new HashSet<>();
                        cacheMessages.put(imMessage.getSessionId(), cacheMessageSet);
                    }
                    cacheMessageSet.add(imMessage);
                }
            }
        }
    };

    Observer<List<RecentContact>> recentContactChangeObserver = new Observer<List<RecentContact>>() {
        @Override
        public void onEvent(List<RecentContact> recentContacts) {
            log("message observer onEvent: " + (null == recentContacts ? "null" : recentContacts.size()));
            if (null != recentContacts) {
                onRecentContactChanged(recentContacts);
            }
        }
    };

    private void onRecentContactChanged(List<RecentContact> recentContacts) {
        int index;
        for (RecentContact r : recentContacts) {
            index = mAdapter.indexOf(r);
            if (index >= 0) {
                mAdapter.replace(r, index);
            } else {
                mAdapter.add(r);
            }

            if (r.getSessionType() == SessionTypeEnum.Team && cacheMessages.get(r.getContactId()) != null) {
                TeamMemberAitHelper.setRecentContactAited(r, cacheMessages.get(r.getContactId()));
            }
        }

        cacheMessages.clear();

        refreshMessages();
    }

    Observer<IMMessage> msgStatusObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage message) {
            int index = getItemIndex(message.getUuid());
            if (index >= 0 && index < mAdapter.getItemCount()) {
                RecentContact item = mAdapter.get(index);
                item.setMsgStatus(message.getStatus());
                refreshViewHolderByIndex(index);
            }
        }
    };

    Observer<RecentContact> recentContactDeleteObserver = new Observer<RecentContact>() {
        @Override
        public void onEvent(RecentContact recentContact) {
            if (recentContact != null) {
                Iterator<RecentContact> iterator = mAdapter.iterator();
                while (iterator.hasNext()) {
                    //for (RecentContact item : items) {
                    RecentContact item = iterator.next();
                    if (TextUtils.equals(item.getContactId(), recentContact.getContactId()) && item.getSessionType() == recentContact.getSessionType()) {
                        mAdapter.remove(item);
                        //refreshMessages(true);
                        break;
                    }
                }
            } else {
                mAdapter.clear();
                refreshMessages();
            }
        }
    };

    TeamDataChangedObserver teamDataChangedObserver = new TeamDataChangedObserver() {

        @Override
        public void onUpdateTeams(List<Team> teams) {
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onRemoveTeam(Team team) {

        }
    };

    TeamMemberDataChangedObserver teamMemberDataChangedObserver = new TeamMemberDataChangedObserver() {
        @Override
        public void onUpdateTeamMember(List<TeamMember> members) {
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onRemoveTeamMember(List<TeamMember> member) {

        }
    };

    ContactChangedObserver friendDataChangedObserver = new ContactChangedObserver() {
        @Override
        public void onAddedOrUpdatedFriends(List<String> accounts) {
            refreshMessages();
        }

        @Override
        public void onDeletedFriends(List<String> accounts) {
            //refreshMessages();
        }

        @Override
        public void onAddUserToBlackList(List<String> account) {
            //refreshMessages();
        }

        @Override
        public void onRemoveUserFromBlackList(List<String> account) {
            //refreshMessages();
        }
    };

    UserInfoObserver userInfoObserver = new UserInfoObserver() {
        @Override
        public void onUserInfoChanged(List<String> accounts) {
            refreshMessages();
        }
    };

    private int getItemIndex(String uuid) {
        for (int i = 0, len = mAdapter.getItemCount(); i < len; i++) {
            RecentContact item = mAdapter.get(i);
            if (TextUtils.equals(item.getRecentMessageId(), uuid)) {
                return i;
            }
        }
        return -1;
    }

    protected void refreshViewHolderByIndex(final int index) {
        Activity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mAdapter.notifyItemChanged(index);
            }
        });
    }

    private void refreshMessages() {
        mAdapter.sort();
        mAdapter.notifyDataSetChanged();

        int unreadNum = NIMClient.getService(MsgService.class).getTotalUnreadCount();
        if (null != mainFragment) {
            mainFragment.showUnreadFlag(unreadNum);
        }
    }

    private void getRecentMessages() {
        Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                NIMClient.getService(MsgService.class).queryRecentContacts().setCallback(new RequestCallbackWrapper<List<RecentContact>>() {
                    @Override
                    public void onResult(int code, List<RecentContact> result, Throwable exception) {
                        if (code != ResponseCode.RES_SUCCESS || result == null) {
                            return;
                        }
                        log("getRecentMessage: " + result.size());
                        // 初次加载，更新离线的消息中是否有@我的消息
                        int index;
                        for (RecentContact loadedRecent : result) {
                            if (loadedRecent.getSessionType() == SessionTypeEnum.Team) {
                                updateOfflineContactAited(loadedRecent);
                            }
                            index = mAdapter.indexOf(loadedRecent);
                            if (index < 0) {
                                mAdapter.add(loadedRecent);
                            } else {
                                mAdapter.replace(loadedRecent, index);
                            }
                        }
                        displayNothing(mAdapter.getItemCount() <= 0);
                    }
                });
            }
        }, duration());
    }

    private void updateOfflineContactAited(final RecentContact recentContact) {
        if (recentContact == null || recentContact.getSessionType() != SessionTypeEnum.Team
                || recentContact.getUnreadCount() <= 0) {
            return;
        }

        // 锚点
        List<String> uuid = new ArrayList<>(1);
        uuid.add(recentContact.getRecentMessageId());

        List<IMMessage> messages = NIMClient.getService(MsgService.class).queryMessageListByUuidBlock(uuid);

        if (messages == null || messages.size() < 1) {
            return;
        }
        final IMMessage anchor = messages.get(0);

        // 查未读消息
        NIMClient.getService(MsgService.class).queryMessageListEx(anchor, QueryDirectionEnum.QUERY_OLD,
                recentContact.getUnreadCount() - 1, false).setCallback(new RequestCallbackWrapper<List<IMMessage>>() {

            @Override
            public void onResult(int code, List<IMMessage> result, Throwable exception) {
                if (code == ResponseCode.RES_SUCCESS && result != null) {
                    result.add(0, anchor);
                    Set<IMMessage> messages = null;
                    // 过滤存在的@我的消息
                    for (IMMessage msg : result) {
                        if (TeamMemberAitHelper.isAitMessage(msg)) {
                            if (messages == null) {
                                messages = new HashSet<>();
                            }
                            messages.add(msg);
                        }
                    }

                    // 更新并展示
                    if (messages != null) {
                        TeamMemberAitHelper.setRecentContactAited(recentContact, messages);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new ContactAdapter();
            mRecyclerView.setAdapter(mAdapter);
            // ios style
            OverScrollDecoratorHelper.setUpOverScroll(mRecyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

            registerObservers(true);
            getRecentMessages();
        }
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            RecentContact recent = mAdapter.get(index);
            if (recent.getSessionType() == SessionTypeEnum.Team) {
                NimSessionHelper.startTeamSession(Activity(), recent.getContactId());
            } else if (recent.getSessionType() == SessionTypeEnum.P2P) {
                NimSessionHelper.startP2PSession(Activity(), recent.getContactId());
            }
        }
    };

    private class ContactAdapter extends RecyclerViewAdapter<ActivityViewHolder, RecentContact> {

        @Override
        public ActivityViewHolder onCreateViewHolder(View itemView, int viewType) {
            ActivityViewHolder holder = new ActivityViewHolder(itemView, RecentContactsFragment.this);
            holder.addOnViewHolderClickListener(onViewHolderClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_activity_home_item;
        }

        @Override
        public void onBindHolderOfView(ActivityViewHolder holder, int position, @Nullable RecentContact item) {
            holder.showContent(item);
        }

        @Override
        public int indexOf(RecentContact item) {
            for (int i = 0, len = getItemCount(); i < len; i++) {
                RecentContact contact = get(i);
                if (contact.getContactId().equals(item.getContactId()) && contact.getSessionType() == (item.getSessionType())) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        protected int comparator(RecentContact item1, RecentContact item2) {
            // 先比较置顶tag
            long sticky = (item1.getTag() & RECENT_TAG_STICKY) - (item2.getTag() & RECENT_TAG_STICKY);
            if (sticky != 0) {
                return sticky > 0 ? -1 : 1;
            } else {
                long time = item1.getTime() - item2.getTime();
                return time == 0 ? 0 : (time > 0 ? -1 : 1);
            }
        }
    }
}
