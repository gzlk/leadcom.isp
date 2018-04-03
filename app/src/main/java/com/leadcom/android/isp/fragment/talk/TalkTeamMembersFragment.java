package com.leadcom.android.isp.fragment.talk;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.hlk.hlklib.lib.inject.ViewId;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.org.MemberRequest;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.fragment.organization.GroupContactPickFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.helper.popup.DeleteDialogHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.common.InputableSearchViewHolder;
import com.leadcom.android.isp.holder.talk.TalkTeamMemberAddViewHolder;
import com.leadcom.android.isp.holder.talk.TalkTeamMemberViewHolder;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.organization.Member;
import com.leadcom.android.isp.model.organization.SubMember;
import com.leadcom.android.isp.model.user.SimpleUser;
import com.leadcom.android.isp.nim.constant.StatusCode;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.SimpleCallback;
import com.netease.nim.uikit.impl.cache.TeamDataCache;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.team.TeamServiceObserver;
import com.netease.nimlib.sdk.team.constant.TeamMemberType;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <b>功能描述：</b>群成员列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/29 16:13 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/03/29 16:13 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class TalkTeamMembersFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_SELECTED_INDEX = "ttmf_selected_index";
    private static final String PARAM_SELECTABLE = "ttmf_selectable";
    private static final String PARAM_EDITABLE = "ttmf_editable";

    public static TalkTeamMembersFragment newInstance(Bundle bundle) {
        TalkTeamMembersFragment ttmf = new TalkTeamMembersFragment();
        ttmf.setArguments(bundle);
        return ttmf;
    }

    public static void open(BaseFragment fragment, String tid, boolean selectable, boolean editable) {
        Bundle bundle = new Bundle();
        // 群聊的id
        bundle.putString(PARAM_QUERY_ID, tid);
        // 是否是选择成员方式
        bundle.putBoolean(PARAM_SELECTABLE, selectable);
        // 是否立即进入编辑状态
        bundle.putBoolean(PARAM_EDITABLE, editable);
        fragment.openActivity(TalkTeamMembersFragment.class.getName(), bundle, selectable ? REQUEST_SELECT : REQUEST_CHANGE, true, false);
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        selectable = bundle.getBoolean(PARAM_SELECTABLE, false);
        editable = bundle.getBoolean(PARAM_EDITABLE, false);
        selectedIndex = bundle.getInt(PARAM_SELECTED_INDEX, -1);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putBoolean(PARAM_SELECTABLE, selectable);
        bundle.putBoolean(PARAM_EDITABLE, editable);
        bundle.putInt(PARAM_SELECTED_INDEX, selectedIndex);
    }

    private static String searchingText = "";
    /**
     * 是否为可选状态（转让管理权的时候需要单选某一个人）
     */
    private boolean selectable, editable;
    private int selectedIndex;
    private boolean isSelfOwner = false;
    private MemberAdapter mAdapter;
    private Model addModel;
    private ArrayList<SimpleUser> users = new ArrayList<>();
    @ViewId(R.id.ui_holder_view_searchable_container)
    private View searchView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        selectedIndex = -1;
        searchingText = "";
        addModel = new Model();
        addModel.setId("+");
        addModel.setAccessToken(StringHelper.getString(R.string.ui_icon_add));
        enableSwipe(false);
        isLoadingComplete(true);
        mRecyclerView.setLayoutManager(new FlexboxLayoutManager(mRecyclerView.getContext(), FlexDirection.ROW, FlexWrap.WRAP));
        registerObservers(true);
        InputableSearchViewHolder searchViewHolder = new InputableSearchViewHolder(searchView, this);
        searchViewHolder.setOnSearchingListener(onSearchingListener);
    }

    private InputableSearchViewHolder.OnSearchingListener onSearchingListener = new InputableSearchViewHolder.OnSearchingListener() {
        @Override
        public void onSearching(String text) {
            searchingText = text;
            mAdapter.clear();
            searchMemberName();
            if (isEmpty(searchingText)) {
                mAdapter.add(addModel);
            }
        }
    };

    private void searchMemberName() {
        for (SimpleUser user : users) {
            if (!isEmpty(searchingText)) {
                if (user.getUserName().contains(searchingText)) {
                    mAdapter.add(user);
                }
            } else {
                mAdapter.add(user);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registerObservers(false);
    }

    private void registerObservers(boolean register) {
        NIMClient.getService(TeamServiceObserver.class).observeMemberUpdate(memberUpdateObserver, register);
        NIMClient.getService(TeamServiceObserver.class).observeMemberRemove(memberRemoveObserver, register);
    }

    /**
     * 群组成员信息更改观察者
     */
    private Observer<List<TeamMember>> memberUpdateObserver = new com.netease.nimlib.sdk.Observer<List<TeamMember>>() {
        @Override
        public void onEvent(List<TeamMember> members) {
            if (null != members) {
                for (TeamMember member : members) {
                    if (member.getTid().equals(mQueryId)) {
                        SimpleUser user = getUser(member);
                        if (mAdapter.exist(user)) {
                            mAdapter.update(user);
                        } else {
                            mAdapter.add(user, mAdapter.indexOf(addModel));
                        }
                    }
                }
            }
        }
    };

    private SimpleUser getUser(TeamMember member) {
        SimpleUser user = new SimpleUser();
        user.setId(member.getAccount());
        user.setUserId(member.getAccount());
        user.setUserName(member.getTeamNick());
        UserInfo info = NimUIKit.getUserInfoProvider().getUserInfo(member.getAccount());
        user.setHeadPhoto(null == info ? "" : info.getAvatar());
        if (isEmpty(member.getTeamNick())) {
            user.setUserName(null == info ? "" : info.getName());
        }
        // 是否管理者
        user.setRead(member.getType() == TeamMemberType.Owner);
        user.setSelectable(editable);
        int index = users.indexOf(user);
        if (index >= 0) {
            users.set(index, user);
        } else {
            users.add(user);
        }
        if (!isSelfOwner) {
            isSelfOwner = member.getAccount().equals(Cache.cache().userId) && member.getType() == TeamMemberType.Owner;
        }
        return user;
    }

    /**
     * 群组成员删除事件
     */
    private Observer<List<TeamMember>> memberRemoveObserver = new Observer<List<TeamMember>>() {

        @Override
        public void onEvent(List<TeamMember> members) {
            if (null != members) {
                for (TeamMember member : members) {
                    if (member.getTid().equals(mQueryId)) {
                        mAdapter.remove(getUser(member));
                    }
                }
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == REQUEST_MEMBER) {
            // 选择了成员并返回了
            String json = getResultedData(data);
            if (!isEmpty(json) && json.length() > 10) {
                ArrayList<SubMember> members = SubMember.fromJson(json);
                addUserToTeam(SubMember.getUserIds(members));
            }
        }
        super.onActivityResult(requestCode, data);
    }

    private void addUserToTeam(ArrayList<String> accounts) {
        MemberRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Member>() {
            @Override
            public void onResponse(Member member, boolean success, String message) {
                super.onResponse(member, success, message);
            }
        }).addTeamMember(mQueryId, accounts);
    }

    @Override
    protected void onSwipeRefreshing() {

    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    public int getLayout() {
        return R.layout.fragment_group_squads;
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    @Override
    protected void onDelayRefreshComplete(int type) {

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

    private void fetchingTeamMember() {
        TeamDataCache.getInstance().fetchTeamMemberList(mQueryId, new SimpleCallback<List<TeamMember>>() {
            @Override
            public void onResult(boolean success, List<TeamMember> members, int code) {
                if (success) {
                    if (null != members) {
                        for (TeamMember member : members) {
                            if (member.isInTeam()) {
                                SimpleUser user = getUser(member);
                                if (mAdapter.exist(user)) {
                                    mAdapter.update(user);
                                } else {
                                    int index = mAdapter.indexOf(addModel);
                                    if (index >= 0) {
                                        mAdapter.add(user, index);
                                    } else {
                                        mAdapter.add(user);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    ToastHelper.make().showMsg(StatusCode.getStatus(code));
                }
                if (!selectable) {
                    // 非选择状态下才有+号和编辑按钮
                    mAdapter.update(addModel);
                }
                resetTitleEvent();
            }
        });
    }

    private void resetTitleEvent() {
        if (selectable) {
            setCustomTitle(R.string.ui_team_talk_team_members_fragment_title_selectable);
        } else {
            setCustomTitle(StringHelper.getString(R.string.ui_team_talk_team_members_fragment_title, users.size()));
        }
        // 选择状态下，右上角为确定按钮。
        // 普通状态下当前用户是组群拥有者时，可以编辑删除用户
        setRightText(selectable ? R.string.ui_base_text_confirm : (isSelfOwner ? (editable ? R.string.ui_base_text_cancel : R.string.ui_base_text_edit) : 0));
        setRightTitleClickListener(selectable || isSelfOwner ? new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                if (selectable) {
                    // 返回已选中的用户
                    if (selectedIndex >= 0) {
                        SimpleUser user = (SimpleUser) mAdapter.get(selectedIndex);
                        SubMember member = new SubMember(user);
                        resultData(SubMember.toJson(member));
                    } else {
                        finish();
                    }
                } else {
                    editable = !editable;
                    for (int i = 0, len = mAdapter.getItemCount(); i < len; i++) {
                        Model model = mAdapter.get(i);
                        model.setSelectable(editable);
                        mAdapter.update(model);
                    }
                    setRightText(editable ? R.string.ui_base_text_cancel : R.string.ui_base_text_edit);
                }
            }
        } : null);
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            setCustomTitle(R.string.ui_team_talk_team_member_more);
            mAdapter = new MemberAdapter();
            mRecyclerView.setAdapter(mAdapter);
            fetchingTeamMember();
        }
    }

    private OnViewHolderClickListener holderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Model model = mAdapter.get(index);
            if (model.getId().equals("+")) {
                GroupContactPickFragment.open(TalkTeamMembersFragment.this, "", true, false, SubMember.toJson(SubMember.getMember(users)));
            }
        }
    };

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            switch (view.getId()) {
                case R.id.ui_holder_view_talk_team_member_head_layout:
                    if (selectable) {
                        // 选中或取消选中index的用户
                        Model model = mAdapter.get(index);
                        if (model.getId().equals(Cache.cache().userId)) {
                            ToastHelper.make().showMsg(R.string.ui_team_talk_team_member_pick_no_select_self);
                        } else {
                            model.setSelected(!model.isSelected());
                            mAdapter.update(model);
                            if (model.isSelected()) {
                                selectedIndex = index;
                            } else {
                                selectedIndex = -1;
                            }
                            Iterator<Model> iterator = mAdapter.iterator();
                            while (iterator.hasNext()) {
                                Model m = iterator.next();
                                if (!m.getId().equals(model.getId())) {
                                    if (m.isSelected()) {
                                        m.setSelected(false);
                                        mAdapter.update(m);
                                    }
                                }
                            }
                        }
                    } else {
                        // 到用户属性页
                        App.openUserInfo(TalkTeamMembersFragment.this, mAdapter.get(index).getId());
                    }
                    break;
                case R.id.ui_holder_view_talk_team_member_mask:
                    if (selectable) {
                        Model model = mAdapter.get(index);
                        model.setSelected(false);
                        mAdapter.update(model);
                        selectedIndex = -1;
                    } else {
                        selectedIndex = index;
                        // 删除用户
                        prepareRemoveMember();
                    }
                    break;
            }
        }
    };

    private void prepareRemoveMember() {
        // 删除成员
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                removeMember();
                return true;
            }
        }).setTitleText(R.string.ui_team_talk_team_member_remove_dialog_title).setConfirmText(R.string.ui_base_text_remove).show();
    }

    private void removeMember() {
        MemberRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Member>() {
            @Override
            public void onResponse(Member member, boolean success, String message) {
                super.onResponse(member, success, message);
                if (success) {
                    ToastHelper.make().showMsg(R.string.ui_team_talk_team_member_removed);
                    mAdapter.clear();
                    fetchingTeamMember();
                }
            }
        }).removeTeamMember(mQueryId, mAdapter.get(selectedIndex).getId());
    }


    private class MemberAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {

        private static final int VT_MEMBER = 0, VT_CLICK = 1;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            switch (viewType) {
                case VT_MEMBER:
                    TalkTeamMemberViewHolder holder = new TalkTeamMemberViewHolder(itemView, TalkTeamMembersFragment.this);
                    holder.setOnViewHolderElementClickListener(elementClickListener);
                    return holder;
                default:
                    TalkTeamMemberAddViewHolder add = new TalkTeamMemberAddViewHolder(itemView, TalkTeamMembersFragment.this);
                    add.addOnViewHolderClickListener(holderClickListener);
                    return add;
            }
        }

        @Override
        public int itemLayout(int viewType) {
            switch (viewType) {
                case VT_MEMBER:
                    return R.layout.holder_view_talk_team_member_head;
                default:
                    return R.layout.holder_view_talk_team_member_add;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (get(position) instanceof SimpleUser) return VT_MEMBER;
            return VT_CLICK;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof TalkTeamMemberViewHolder) {
                ((TalkTeamMemberViewHolder) holder).showMargin(position % 5 == 0, position % 5 == 4);
                ((TalkTeamMemberViewHolder) holder).showContent((SimpleUser) item, searchingText, selectable);
            } else if (holder instanceof TalkTeamMemberAddViewHolder) {
                ((TalkTeamMemberAddViewHolder) holder).showContent(item);
                ((TalkTeamMemberAddViewHolder) holder).showMargin(position % 5 == 0, position % 5 == 4);
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
