package com.leadcom.android.isp.fragment.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.activity.ActRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.org.OrgRequest;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.activity.ActivityCreatorFragment;
import com.leadcom.android.isp.fragment.activity.ActivityDetailsMainFragment;
import com.leadcom.android.isp.fragment.activity.ActivityManagementFragment;
import com.leadcom.android.isp.fragment.activity.UnApprovedInviteFragment;
import com.leadcom.android.isp.fragment.activity.topic.TopicListFragment;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.organization.BaseOrganizationFragment;
import com.leadcom.android.isp.fragment.organization.StructureFragment;
import com.leadcom.android.isp.helper.SimpleDialogHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.helper.TooltipHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.activity.ActivityViewHolder;
import com.leadcom.android.isp.holder.organization.OrgStructureViewHolder;
import com.leadcom.android.isp.lib.DepthViewPager;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.activity.Activity;
import com.leadcom.android.isp.model.activity.topic.AppTopic;
import com.leadcom.android.isp.model.common.SimpleClickableItem;
import com.leadcom.android.isp.model.organization.Member;
import com.leadcom.android.isp.model.organization.Organization;
import com.leadcom.android.isp.nim.model.extension.ArchiveAttachment;
import com.leadcom.android.isp.nim.model.extension.MinutesAttachment;
import com.leadcom.android.isp.nim.model.extension.NoticeAttachment;
import com.leadcom.android.isp.nim.model.extension.SigningNotifyAttachment;
import com.leadcom.android.isp.nim.model.extension.TopicAttachment;
import com.leadcom.android.isp.nim.model.extension.VoteAttachment;
import com.leadcom.android.isp.nim.session.NimSessionHelper;
import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <b>功能描述：</b>主页活动<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/20 10:46 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/20 10:46 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ActivityFragment extends BaseOrganizationFragment {

    private static final String PARAM_SELECTED_ = "act_selected_index";
    private static final String PARAM_INVITE = "act_invite_number";

    private String[] items;
    private int selectedIndex = -1;
    private int inviteNumber = 0;
    private ArrayList<AppTopic> appTopics = new ArrayList<>();
    private ActivityAdapter mAdapter;
    private OrgStructureViewHolder concernedViewHolder;

    public MainFragment mainFragment;
    public StructureFragment structureFragment;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        selectedIndex = bundle.getInt(PARAM_SELECTED_, -1);
        inviteNumber = bundle.getInt(PARAM_INVITE, 0);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        if (null != concernedViewHolder) {
            selectedIndex = concernedViewHolder.getSelected();
        }
        bundle.putInt(PARAM_SELECTED_, selectedIndex);
        bundle.putInt(PARAM_INVITE, inviteNumber);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_activity_home_page_items);
        }
        //  注册/注销观察者
        NIMClient.getService(MsgServiceObserve.class).observeRecentContact(messageObserver, true);
        tryPaddingContent(true);
        if (null != mainFragment) {
            mainFragment.showUnreadFlag(NIMClient.getService(MsgService.class).getTotalUnreadCount());
        }
    }

    @Override
    public void onDestroy() {
        //  注册/注销观察者
        NIMClient.getService(MsgServiceObserve.class).observeRecentContact(messageObserver, false);
        super.onDestroy();
    }

    //  创建观察者对象
    private Observer<List<RecentContact>> messageObserver = new Observer<List<RecentContact>>() {
        @Override
        public void onEvent(List<RecentContact> contacts) {
            // 当最近联系人列表数据有变化时，同步当前显示组织里的所有活动的未读标记和最近聊天内容
            resetUnreadFlags(contacts);
            showUnreadNum(NIMClient.getService(MsgService.class).getTotalUnreadCount() + inviteNumber);
        }
    };

    private void showUnreadNum(int num) {
        // 未读消息总数大于0时，显示有未读消息
        mainFragment.showUnreadFlag(num);
    }

    // 查询最近联系人列表，并同步更新未读消息
    private void resetUnreadFlags() {
        NIMClient.getService(MsgService.class).queryRecentContacts().setCallback(new RequestCallbackWrapper<List<RecentContact>>() {
            @Override
            public void onResult(int code, List<RecentContact> recents, Throwable e) {
                // recents参数即为最近联系人列表（最近会话列表）
                resetUnreadFlags(recents);
            }
        });
    }

    private void clearUnreadCount(List<RecentContact> list) {
        if (null != list && list.size() > 0) {
            for (RecentContact contact : list) {
                if (contact.getUnreadCount() > 0) {
                    clearUnreadCount(contact);
                }
            }
        }
    }

    private void clearUnreadCount(RecentContact contact) {
        List<String> uuid = new ArrayList<>(1);
        uuid.add(contact.getRecentMessageId());
        List<IMMessage> messages = NIMClient.getService(MsgService.class).queryMessageListByUuidBlock(uuid);
        if (messages != null && messages.size() > 0) {
            IMMessage msg = messages.get(0);
            Team team = TeamDataCache.getInstance().getTeamById(msg.getSessionId());
            if (null != team) {
                clearUnreadCount(team.getId(), team.getName());
            }
        }
    }

    private void clearUnreadCount(final String teamId, final String teamName) {
        TeamDataCache.getInstance().fetchTeamMember(teamId, Cache.cache().userId, new SimpleCallback<TeamMember>() {
            @Override
            public void onResult(boolean success, TeamMember result) {
                boolean cleanable = true;
                if (success && null != result) {
                    if (result.isInTeam()) {
                        cleanable = false;
                    }
                }
                if (cleanable) {
                    NIMClient.getService(MsgService.class).clearUnreadCount(teamId, SessionTypeEnum.Team);
                    log(format("clean unread count which i am not member of team: %s, %s", teamId, teamName));
                    // 清理完毕未读消息之后重新显示未读数量
                    showUnreadNum(NIMClient.getService(MsgService.class).getTotalUnreadCount() + inviteNumber);
                }
            }
        });
    }

    private void resetUnreadFlags(List<RecentContact> contacts) {
        clearUnreadCount(contacts);
        if (null == mAdapter) {
            return;
        }
        for (int i = 0, size = mAdapter.getItemCount(); i < size; i++) {
            Model model = mAdapter.get(i);
            if (model instanceof Activity) {
                Activity act = (Activity) model;
                RecentContact contact = get(act.getTid(), contacts);
                if (null != contact) {
                    act.setUnreadNum(contact.getUnreadCount());
                    // 最后发送消息的时间
                    act.setBeginDate(Utils.format(StringHelper.getString(R.string.ui_base_text_date_time_format), contact.getTime()));
                    String nick = "";
                    try {
                        nick = contact.getFromNick();
                    } catch (Exception ignore) {
                        ignore.printStackTrace();
                    }
                    if (isEmpty(nick)) {
                        nick = "";
                    }
                    act.setIntro(format("%s: %s", nick, getRecentMsgType(contact)));
                    mAdapter.notifyItemChanged(i);
                }
            } else if (model.getId().equals("2")) {
                // 议题列表的未读消息统计
                resetTopicsUnreadFlag(contacts);
            }
        }
    }

    private void resetTopicsUnreadFlag(List<RecentContact> list) {
        if (null == appTopics || appTopics.size() < 1) {
            return;
        }
        int unread = 0;
        for (AppTopic topic : appTopics) {
            RecentContact contact = get(topic.getTid(), list);
            if (null != contact) {
                unread += contact.getUnreadCount();
            }
        }

        String string = format(items[2], 0);
        SimpleClickableItem item = new SimpleClickableItem(string);
        int index = mAdapter.indexOf(item);
        if (index > 0) {
            item = (SimpleClickableItem) mAdapter.get(index);
            item.setAdditionalNum(unread);
            mAdapter.update(item);
        }
    }

    private String getRecentMsgType(RecentContact contact) {
        String ret = contact.getContent();
        if (ret.contains(StringHelper.getString(R.string.ui_nim_app_recent_contact_type_custom))) {
            MsgAttachment attachment = contact.getAttachment();
            if (attachment instanceof NoticeAttachment) {
                ret = StringHelper.getString(R.string.ui_nim_app_recent_contact_type_notice);
            } else if (attachment instanceof SigningNotifyAttachment) {
                ret = StringHelper.getString(R.string.ui_nim_app_recent_contact_type_signing);
            } else if (attachment instanceof VoteAttachment) {
                ret = StringHelper.getString(R.string.ui_nim_app_recent_contact_type_vote);
            } else if (attachment instanceof TopicAttachment) {
                ret = StringHelper.getString(R.string.ui_nim_app_recent_contact_type_topic);
            } else if (attachment instanceof MinutesAttachment) {
                ret = StringHelper.getString(R.string.ui_nim_app_recent_contact_type_minutes);
            } else if (attachment instanceof ArchiveAttachment) {
                ret = StringHelper.getString(R.string.ui_nim_app_recent_contact_type_archive);
            }
        }
        return ret;
    }

    private RecentContact get(String tid, List<RecentContact> contacts) {
        if (null == contacts || contacts.size() < 1) {
            return null;
        }
        for (RecentContact r : contacts) {
            if (r.getContactId().equals(tid)) {
                return r;
            }
        }
        return null;
    }

    @Override
    public void doingInResume() {
        initializeAdapter();
        resetTitle();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void onSwipeRefreshing() {
        remotePageNumber = 1;
        isLoadingComplete(false);
        setSupportLoadingMore(true);
        refreshingItems();
    }

    private void refreshingItems() {
        setLoadingText(R.string.ui_activity_fetching_groups);
        displayLoading(true);
        fetchingJoinedRemoteOrganizations(OrgRequest.GROUP_LIST_OPE_ACTIVITY);
        if (!isEmpty(mQueryId)) {
            fetchingActivities();
        }
    }

    @Override
    protected void onFetchingJoinedRemoteOrganizationsComplete(List<Organization> list) {
        displayLoading(false);
        if (null != list) {
            concernedViewHolder.add(list);
        } else {
            // 当前显示本fragment时才提示用户
            if (getUserVisibleHint()) {
                ToastHelper.make().showMsg(R.string.ui_organization_structure_no_group_exist);
            }
        }
        stopRefreshing();
    }

    private void fetchingActivities() {
        setLoadingText(R.string.ui_activity_fetching_front_list);
        displayLoading(true);
        ActRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Activity>() {
            @Override
            public void onResponse(List<Activity> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (remotePageNumber <= 1) {
                    // 第一页时，清理已显示的活动列表
                    clearActivities();
                }
                if (success) {
                    if (null != list) {
                        if (list.size() >= pageSize) {
                            remotePageNumber++;
                            isLoadingComplete(false);
                        } else {
                            isLoadingComplete(true);
                        }
                    } else {
                        isLoadingComplete(true);
                        // 当前显示本fragment时才提示用户
                        if (getUserVisibleHint()) {
                            ToastHelper.make().showMsg(R.string.ui_activity_main_not_exist_any_more);
                        }
                    }
                } else {
                    isLoadingComplete(true);
                    list = Activity.getByGroupId(mQueryId);
                }
                updateActivityList(list);
                displayLoading(false);
                stopRefreshing();
                // 拉取我未处理的群活动邀请
                inviteNumber = invtNum;
                appTopics = actTopicList;
                showUnreadNum(NIMClient.getService(MsgService.class).getTotalUnreadCount() + inviteNumber);
                resetUnhandledInvite(invtNum);
                resetTopics();
                resetUnreadFlags();
            }
        }).listFront(mQueryId, remotePageNumber, Cache.cache().groupIds);
    }

    private void resetUnhandledInvite(int invtNum) {
        String string = format(items[1], invtNum);
        SimpleClickableItem item = new SimpleClickableItem(string);
        item.setAdditionalNum(invtNum);
        if (invtNum > 0) {
            // 有未处理的活动请求时才显示活动，否则隐藏
            if (mAdapter.exist(item)) {
                mAdapter.update(item);
            } else {
                mAdapter.add(item, item.getIndex());
            }
            // 有待处理活动时，删除没有活动的提醒
            resetNothingItem(true);
        } else {
            if (mAdapter.exist(item)) {
                mAdapter.remove(item);
            }
            refreshNothingItem();
        }
    }

    // 重置该组织中的议题
    private void resetTopics() {
        int num = null == appTopics ? 0 : appTopics.size();
        String string = format(items[2], num);
        SimpleClickableItem item = new SimpleClickableItem(string);
        item.setAdditionalNum(num);
        if (num > 0) {
            // 有议题
            if (mAdapter.exist(item)) {
                mAdapter.update(item);
            } else {
                SimpleClickableItem sci = new SimpleClickableItem(items[1]);
                int index = mAdapter.indexOf(sci);
                if (index > 0) {
                    // 如果有未读消息提醒则加在其后面
                    mAdapter.add(item, index + 1);
                } else {
                    mAdapter.add(item, 1);
                }
                //mAdapter.add(item, item.getModelTag());
            }
        } else {
            if (mAdapter.exist(item)) {
                mAdapter.remove(item);
            }
        }
    }

    private void refreshNothingItem() {
        resetNothingItem(hasActivity());
    }

    private void resetNothingItem(boolean remove) {
        // 没有活动的提醒item
        SimpleClickableItem sci = new SimpleClickableItem(items[3]);
        if (remove) {
            if (mAdapter.exist(sci)) {
                mAdapter.remove(sci);
            }
        } else {
            if (!mAdapter.exist(sci)) {
                mAdapter.add(sci);
            }
        }
    }

    @Override
    protected void onLoadingMore() {
        fetchingActivities();
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    public void rightIconClick(View view) {
        if (mQueryId.equals(Organization.OTHER_ID)) {
            showTooltip(view, R.id.ui_tooltip_activity_management_normal, true, TooltipHelper.TYPE_LEFT, onClickListener);
        } else {
            showTooltip(view, R.id.ui_tooltip_activity_management, true, TooltipHelper.TYPE_LEFT, onClickListener);
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ui_tooltip_menu_activity_add:
                    if (isEmpty(mQueryId)) {
                        ToastHelper.make().showMsg(R.string.ui_organization_structure_no_group_exist);
                    } else {
                        ActivityCreatorFragment.open(ActivityFragment.this, mQueryId, "");
                        //openActivity(ActivityCreatorFragment.class.getName(), format(",%s", mQueryId), REQ_CREATE, true, true);
                    }
                    break;
                case R.id.ui_tooltip_menu_activity_manage:
                case R.id.ui_tooltip_menu_activity_manage_normal:
                    openActivity(ActivityManagementFragment.class.getName(), mQueryId, false, false);
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == REQUEST_CREATE) {
            fetchingActivities();
        }
        super.onActivityResult(requestCode, data);
    }

    private void initializeAdapter() {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_activity_home_page_items);
        }
        if (null == mAdapter) {
            mAdapter = new ActivityAdapter();
            mRecyclerView.setAdapter(mAdapter);
        }
        initializeItems();
    }

    private void initializeItems() {
        for (String string : items) {
            String text;
            // 未参加的数量、议题、空活动列表默认不显示
            if (string.contains("%d") || string.charAt(0) == '3') {
                // 默认不显示未参加的活动和议题两个item
                text = "";
            } else {
                text = string;
            }
            if (!isEmpty(text)) {
                SimpleClickableItem item = new SimpleClickableItem(text);
                if (mAdapter.exist(item)) {
                    mAdapter.update(item);
                } else {
                    mAdapter.add(item, item.getIndex());
                }
            }
        }
        if (!isEmpty(mQueryId)) {
            fetchingActivities();
        }
    }

    private boolean hasActivity() {
        for (int i = 0, size = mAdapter.getItemCount(); i < size; i++) {
            if (mAdapter.get(i) instanceof Activity) {
                return true;
            }
        }
        return false;
    }

    private void updateActivityList(List<Activity> list) {
        if (null == list) {
            return;
        }
        //list.clear();
        for (Activity activity : list) {
            mAdapter.update(activity);
        }
    }

    private void clearActivities() {
        Iterator<Model> iterator = mAdapter.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            Model model = iterator.next();
            if (model instanceof Activity) {
                iterator.remove();
                mAdapter.notifyItemRemoved(index);
            }
            index++;
        }
    }

    @Override
    protected void onViewPagerDisplayedChanged(boolean visible) {
        super.onViewPagerDisplayedChanged(visible);
        if (visible) {
            resetTitle();
            refreshingItems();
        }
    }

    private void resetTitle() {
//        if (getUserVisibleHint()) {
//            mainFragment.showRightIcon(true);
//        }
        changeSelectedActivity();
    }

    private void changeSelectedActivity() {
        if (isEmpty(StructureFragment.selectedGroupId)) {
            // 组织切换时，如果当前组织不是在组织里显示的那个，则不要显示+号
            mainFragment.showRightIcon(false);
        }
        if (selectedIndex < 0) {
            mainFragment.showRightIcon(false);
            return;
        }
        // 加载本地该组织的活动列表
        Organization org = concernedViewHolder.get(selectedIndex);
        if (null == org) {
            mainFragment.showRightIcon(false);
            return;
        }
        if (isEmpty(mQueryId) || !mQueryId.equals(org.getId())) {
            remotePageNumber = 1;
            setSupportLoadingMore(true);
            // 群id不一样时才刷新
            mQueryId = org.getId();
            mOrganizationId = mQueryId;
            // 更改标题栏上的文字和icon
            if (getUserVisibleHint()) {
                // 如果当前显示的是组织页面才更改标题栏文字，否则不需要
                mainFragment.setTitleText(org.getName());
            }
            clearActivities();
            refreshingItems();
        }
        if (getUserVisibleHint()) {
            // 查看当前选择的组织是否是我关注的组织
            //mainFragment.showRightIcon(null != structureFragment && structureFragment.isConcerned(mOrganizationId));
            mainFragment.showRightIcon(true);
        }
    }

    private DepthViewPager.OnPageChangeListener onPageChangeListener = new DepthViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            selectedIndex = position;
            Handler().post(new Runnable() {
                @Override
                public void run() {
                    changeSelectedActivity();
                }
            });
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * 我在当前选中的活动中的角色
     */
    public static Member myActMember;

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Model model = mAdapter.get(index);
            if (model instanceof Activity) {
                Activity act = (Activity) model;
                myActMember = act.getActMember();
                //openActivity(CreateActivityFragment.class.getName(), format("%s,%s", act.getId(), act.getGroupId()), true, true);
                //openActivity(ActivityPropertiesFragment.class.getName(), act.getId(), false, false, true);
                if (null != myActMember && myActMember.activityCheckable()) {
                    if (act.getStatus() == Activity.Status.ACTIVE) {
                        // 未结束的活动打开群聊窗口
                        NimSessionHelper.startTeamSession(Activity(), act.getTid());
                    } else {
                        // 已结束的活动打开活动详情页
                        openActivity(ActivityDetailsMainFragment.class.getName(), act.getId(), false, false);
                    }
                } else {
                    // 无权查看活动
                    SimpleDialogHelper.init(Activity()).show(R.string.ui_activity_home_no_permission_check);
                }
            } else if (model instanceof SimpleClickableItem) {
                SimpleClickableItem sci = (SimpleClickableItem) model;
                // 打开未参加的活动列表
                switch (sci.getIndex()) {
                    case 1:
                        openActivity(UnApprovedInviteFragment.class.getName(), mQueryId, true, false);
                        break;
                    case 2:
                        // 议题列表
                        TopicListFragment.open(ActivityFragment.this, mQueryId);
                        break;
                }
            }
        }
    };

    private class ActivityAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {

        private static final int VT_HEAD = 0, VT_ACT = 1;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            BaseFragment fragment = ActivityFragment.this;
            switch (viewType) {
                case VT_HEAD:
                    if (null == concernedViewHolder) {
                        concernedViewHolder = new OrgStructureViewHolder(itemView, fragment);
                        concernedViewHolder.setPageChangeListener(onPageChangeListener);
                    }
                    return concernedViewHolder;
                default:
                    ActivityViewHolder holder = new ActivityViewHolder(itemView, fragment);
                    holder.addOnViewHolderClickListener(onViewHolderClickListener);
                    return holder;
            }
        }

        @Override
        public int itemLayout(int viewType) {
            switch (viewType) {
                case VT_HEAD:
                    return R.layout.holder_view_organization_concerned;
                default:
                    return R.layout.holder_view_activity_home_item;
            }
        }

        @Override
        public int getItemViewType(int position) {
            switch (position) {
                case 0:
                    return VT_HEAD;
                default:
                    return VT_ACT;
            }
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof ActivityViewHolder) {
                if (item instanceof SimpleClickableItem) {
                    ((ActivityViewHolder) holder).showContent(((SimpleClickableItem) item));
                } else {
                    ((ActivityViewHolder) holder).showContent((Activity) item);
                }
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
