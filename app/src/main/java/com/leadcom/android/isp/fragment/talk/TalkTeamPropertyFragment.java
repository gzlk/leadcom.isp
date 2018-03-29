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
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.fragment.main.RecentContactsFragment;
import com.leadcom.android.isp.helper.DeleteDialogHelper;
import com.leadcom.android.isp.helper.DialogHelper;
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
import com.netease.nim.uikit.api.model.SimpleCallback;
import com.netease.nim.uikit.business.team.model.TeamExtras;
import com.netease.nim.uikit.impl.cache.TeamDataCache;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.TeamServiceObserver;
import com.netease.nimlib.sdk.team.constant.TeamMemberType;
import com.netease.nimlib.sdk.team.constant.TeamMessageNotifyTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;

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
     * 是否打开用户属性页
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
        } else {
            NIMClient.getService(TeamServiceObserver.class).observeTeamUpdate(teamUpdateObserver, register);
            NIMClient.getService(TeamServiceObserver.class).observeTeamRemove(teamRemoveObserver, register);
        }
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
            dismissTeam();
        } else {
            // 退出群聊
            quitTeam();
        }
    }

    private void quitTeam() {
        NIMClient.getService(TeamService.class).quitTeam(mQueryId).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                ToastHelper.make().showMsg(R.string.ui_team_talk_quit_team_successfully);
                resultData(TeamExtras.RESULT_EXTRA_REASON_QUIT);
            }

            @Override
            public void onFailed(int code) {

            }

            @Override
            public void onException(Throwable exception) {

            }
        });
    }

    private void dismissTeam() {
        NIMClient.getService(TeamService.class).dismissTeam(mQueryId).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                ToastHelper.make().showMsg(R.string.ui_team_talk_team_dismissed);
                resultData(TeamExtras.RESULT_EXTRA_REASON_DISMISS);
            }

            @Override
            public void onFailed(int code) {

            }

            @Override
            public void onException(Throwable exception) {

            }
        });
    }

    private void initializeTeam(Team team) {
        setCustomTitle(team.getName());
        Model m = new Model();
        m.setId(team.getId());
        mAdapter.add(m, 0);
        for (String item : items) {
            if (item.charAt(0) == '-') {
                Model model = new Model();
                model.setId(item);
                mAdapter.add(model);
            } else if (item.startsWith("2|")) {
                // 管理权转让
                TeamMember member = TeamDataCache.getInstance().getTeamMember(mQueryId, Cache.cache().userId);
                // 群聊拥有者或管理员可以转让管理权
                if (null != member && (member.getType() == TeamMemberType.Manager || member.getType() == TeamMemberType.Owner)) {
                    // 如果当前用户是群拥有者
                    if (member.getType() == TeamMemberType.Owner) {
                        isSelfOwner = true;
                        bottomButton.setText(R.string.ui_team_talk_quit_team_owner);
                    }
                    SimpleClickableItem transfer = new SimpleClickableItem(item);
                    mAdapter.add(transfer);
                }
            } else if (item.startsWith("3|")) {
                // 置顶
                RecentContact contact = getRecentContact();
                SimpleClickableItem click = new SimpleClickableItem(format(items[3], null == contact ? 0 : (isTagSet(contact) ? 1 : 0)));
                mAdapter.add(click);
            } else if (item.startsWith("4|")) {
                // 是否静音
                SimpleClickableItem click = new SimpleClickableItem(format(item, team.getMessageNotifyType() == TeamMessageNotifyTypeEnum.Mute ? 1 : 0));
                mAdapter.add(click);
            } else {
                SimpleClickableItem click = new SimpleClickableItem(item.startsWith("1|") ? format(item, team.getName()) : item);
                mAdapter.add(click);
            }
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
                    SimpleClickableItem item = new SimpleClickableItem(format(items[1], team.getName()));
                    mAdapter.update(item);
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
            }
        }
    };

    private ToggleableViewHolder.OnViewHolderToggleChangedListener toggleChangedListener = new ToggleableViewHolder.OnViewHolderToggleChangedListener() {
        @Override
        public void onChange(int index, boolean togged) {
            if (index == 3) {
                // 置顶群聊
                RecentContact contact = getRecentContact();
                if (null != contact) {
                    if (isTagSet(contact)) {
                        addTag(contact);
                    } else {
                        removeTag(contact);
                    }
                    NIMClient.getService(MsgService.class).updateRecent(contact);
                }
            } else if (index == 4) {
                // 静音
                if (isUser) {
                    NIMClient.getService(FriendService.class).setMessageNotify(mQueryId, !togged);
                } else {
                    // 群聊静音
                    NIMClient.getService(TeamService.class).muteTeam(mQueryId, togged ? TeamMessageNotifyTypeEnum.Mute : TeamMessageNotifyTypeEnum.All);
                }
            }
        }
    };

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
                    ttmvh.setAdmin(isSelfOwner);
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
