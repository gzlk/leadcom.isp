package com.leadcom.android.isp.fragment.organization;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.View;

import com.hlk.hlklib.lib.inject.ViewId;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.org.MemberRequest;
import com.leadcom.android.isp.api.org.RoleRequest;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.helper.popup.SimpleDialogHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.common.InputableSearchViewHolder;
import com.leadcom.android.isp.holder.organization.ContactViewHolder;
import com.leadcom.android.isp.listener.OnHandleBoundDataListener;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.operation.GRPOperation;
import com.leadcom.android.isp.model.organization.Member;
import com.leadcom.android.isp.model.organization.Role;
import com.leadcom.android.isp.model.organization.Squad;
import com.leadcom.android.isp.model.organization.SubMember;
import com.leadcom.android.isp.view.SwipeItemLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * <b>功能描述：</b>通讯录(包括小组通讯录和组织通讯录)<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/09 00:25 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/09 00:25 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ContactFragment extends BaseOrganizationFragment {

    private static final String PARAM_TYPE = "_cf_type_";
    private static final String PARAM_CREATOR = "_cf_manager_";
    private static final String PARAM_DIAL_INDEX = "_cf_dial_index";
    private static final String PARAM_OPENABLE = "_cf_openable";
    private static final String PARAM_SQUAD_OBJECT = "_cf_squad_object";
    /**
     * 没有查询任何数据
     */
    public static final int TYPE_NONE = 0;
    /**
     * 打开的是小组的通讯录
     */
    public static final int TYPE_SQUAD = 1;
    /**
     * 打开的是组织的通讯录
     */
    public static final int TYPE_ORG = 2;
    /**
     * 打开的是我的联系人列表
     */
    public static final int TYPE_MINE = 3;

    private boolean isOpenable = false;

    public static ContactFragment newInstance(Bundle bundle) {
        ContactFragment cf = new ContactFragment();
        cf.setArguments(bundle);
        return cf;
    }

    public static Bundle getBundle(int type, String groupId, String squadId) {
        Bundle bundle = new Bundle();
        // 类型
        bundle.putInt(PARAM_TYPE, type);
        // 组织的id
        bundle.putString(PARAM_QUERY_ID, groupId);
        // 小组的id
        bundle.putString(PARAM_SQUAD_ID, squadId);
        return bundle;
    }

    /**
     * 打开具有标题栏的组织成员列表页面
     */
    public static void open(BaseFragment fragment, String groupId) {
        Bundle bundle = getBundle(TYPE_ORG, groupId, "");
        bundle.putBoolean(PARAM_OPENABLE, true);
        fragment.openActivity(ContactFragment.class.getName(), bundle, true, false);
    }

    /**
     * 打开具有标题栏的小组成员列表页面
     */
    public static void open(BaseFragment fragment, Squad squad) {
        Bundle bundle = getBundle(TYPE_SQUAD, squad.getGroupId(), squad.getId());
        bundle.putBoolean(PARAM_OPENABLE, true);
        bundle.putSerializable(PARAM_SQUAD_OBJECT, squad);
        fragment.openActivity(ContactFragment.class.getName(), bundle, true, false);
    }

    /**
     * 打开我的联系人列表
     */
    public static void open(BaseFragment fragment) {
        Bundle bundle = getBundle(TYPE_MINE, "", "");
        fragment.openActivity(ContactFragment.class.getName(), bundle, true, false);
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        showType = bundle.getInt(PARAM_TYPE, TYPE_NONE);
        isCreator = bundle.getBoolean(PARAM_CREATOR, false);
        dialIndex = bundle.getInt(PARAM_DIAL_INDEX, -1);
        isOpenable = bundle.getBoolean(PARAM_OPENABLE, false);
        mSquad = (Squad) bundle.getSerializable(PARAM_SQUAD_OBJECT);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_TYPE, showType);
        bundle.putBoolean(PARAM_CREATOR, isCreator);
        bundle.putInt(PARAM_DIAL_INDEX, dialIndex);
        bundle.putBoolean(PARAM_OPENABLE, isOpenable);
        bundle.putSerializable(PARAM_SQUAD_OBJECT, mSquad);
    }

    // view
    @ViewId(R.id.ui_holder_view_searchable_container)
    private View searchView;

    // holder
    private InputableSearchViewHolder inputableSearchViewHolder;
    private ArrayList<Member> members;
    private ContactAdapter mAdapter;

    private Squad mSquad;
    // 默认显示组织的联系人列表
    private int showType = TYPE_ORG;
    /**
     * 当前登录者是否是组织的创建者
     */
    private boolean isCreator = false;
    private int dialIndex = -1;

    private Role myRole;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isOpenable) {
            myRole = Cache.cache().getGroupRole(mQueryId);
            // 有权限添加成员时，显示手机通讯录入口
            if (showType == TYPE_ORG && hasOperation(GRPOperation.MEMBER_ADD)) {
                setRightIcon(R.string.ui_icon_add);
                setRightTitleClickListener(new OnTitleButtonClickListener() {
                    @Override
                    public void onClick() {
                        PhoneContactFragment.open(ContactFragment.this, mQueryId, "");
                    }
                });
                //phoneContactView.setVisibility(View.VISIBLE);
                setCustomTitle(R.string.ui_group_member_fragment_title);
            } else if (showType == TYPE_SQUAD && hasOperation(GRPOperation.SQUAD_MEMBER_INVITE)) {
                setRightIcon(R.string.ui_icon_add);
                setRightTitleClickListener(new OnTitleButtonClickListener() {
                    @Override
                    public void onClick() {
                        // + 号点击之后直接打开组织通讯录(2017/08/02 10:00)
                        // 打开组织通讯录并尝试将里面的用户邀请到小组
                        String json = SubMember.toJson(getSubMembers());
                        GroupContactPickFragment.open(ContactFragment.this, mOrganizationId, true, false, json);
                    }
                });
                setCustomTitle(R.string.ui_group_squad_member_fragment_title);
            }
        }
        // 小组成员、个人通讯录可以搜索成员名字
        //searchView.setVisibility((showType == TYPE_SQUAD || showType == TYPE_MINE) ? View.VISIBLE : View.GONE);
    }

    private ArrayList<SubMember> getSubMembers() {
        ArrayList<SubMember> sub = new ArrayList<>();
        for (Member member : members) {
            sub.add(new SubMember(member));
        }
        return sub;
    }

    /**
     * 登录者是否具有某项组织权限
     */
    private boolean hasOperation(String operation) {
        return null != myRole && myRole.hasOperation(operation);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_organization_contact;
    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        initializeHolders();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        // 小组才显示标题栏，组织通讯录不需要
        return isOpenable || showType == TYPE_SQUAD || showType == TYPE_MINE;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    protected void onSwipeRefreshing() {
        remotePageNumber = 1;
        fetchingRemoteMembers(mOrganizationId, mSquadId);
    }

    @Override
    protected void onLoadingMore() {
        fetchingRemoteMembers(mOrganizationId, mSquadId);
    }

    @Override
    protected String getLocalPageTag() {
        if (StringHelper.isEmpty(mQueryId)) return null;
        return format("cf_grp_contact_%s", mQueryId);
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        switch (requestCode) {
            case REQUEST_MEMBER:
                // 添加了成员并且返回了
                String json = getResultedData(data);
                if (!isEmpty(json) && json.length() > 10) {
                    ArrayList<SubMember> subs = SubMember.fromJson(json);
                    checkSelectedMembers(subs);
                }
                break;
        }
        super.onActivityResult(requestCode, data);
    }

    private void checkSelectedMembers(ArrayList<SubMember> list) {
        Iterator<SubMember> iterator = list.iterator();
        // 清除已经存在队列里的重复成员
        while (iterator.hasNext()) {
            SubMember member = iterator.next();
            if (exist(member.getUserId())) {
                iterator.remove();
            }
        }
        if (list.size() > 0) {
            // 如果清除掉已经存在队列里的成员之后还有剩余，则需要将其加入小组
            MemberRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Member>() {
                @Override
                public void onResponse(Member member, boolean success, String message) {
                    super.onResponse(member, success, message);
                    if (success) {
                        // 加入成功，则重新刷新一遍小组成员
                        fetchingRemoteMembers(mOrganizationId, mSquadId);
                    }
                }
            }).addToSquadFromGroup(mSquadId, SubMember.getUserIds(list));
        }
    }

    private boolean exist(String userId) {
        for (Member member : members) {
            if (member.getUserId().equals(userId)) return true;
        }
        return false;
    }

    private void initializeHolders() {
        if (null == inputableSearchViewHolder) {
            inputableSearchViewHolder = new InputableSearchViewHolder(searchView, this);
            inputableSearchViewHolder.setOnSearchingListener(searchingListener);
        }
        if (null == members) {
            members = new ArrayList<>();
        }
        initializeAdapter();
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new ContactAdapter();
            mRecyclerView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(Activity()));
            if (showType != TYPE_ORG) {
                mRecyclerView.addItemDecoration(new StickDecoration());
            }
            mRecyclerView.setAdapter(mAdapter);
            searchingListener.onSearching("");
            loadingQueryItem();
        }
    }

    /**
     * 加载查询的对象
     */
    private void loadingQueryItem() {
        switch (showType) {
            case TYPE_ORG:
                setNothingText(R.string.ui_organization_contact_no_member);
                fetchingRemoteMembers(mQueryId, "");
                break;
            case TYPE_SQUAD:
                setNothingText(R.string.ui_organization_contact_squad_no_member);
                loadingSquad();
                break;
            case TYPE_MINE:
                loadingMineContacts();
                break;
        }
    }

    private void loadingSquad() {
        if (null == mSquad) {
            fetchingRemoteSquad(mSquadId);
        } else {
            setCustomTitle(StringHelper.getString(R.string.ui_group_squad_member_fragment_title_string, mSquad.getName()));
            members.clear();
            members.addAll(mSquad.getGroSquMemberList());
            if (null == members || members.size() < 1) {
                fetchingRemoteMembers(mOrganizationId, mSquadId);
            } else {
                isLoadingComplete(true);
                refreshMemberList();
            }
        }
    }

    private void loadingMineContacts() {
        // 我的联系人列表不需要再次拉取
        enableSwipe(false);
        isLoadingComplete(true);
        setCustomTitle(R.string.ui_text_personality_contact_fragment_title);
        setNothingText(R.string.ui_text_personality_contact_nothing);
        setLoadingText(R.string.ui_text_personality_contact_loading);
        displayLoading(true);
        displayNothing(false);
        OverScrollDecoratorHelper.setUpOverScroll(mRecyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        MemberRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Member>() {
            @Override
            public void onResponse(List<Member> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success && null != list) {
                    for (Member member : list) {
                        member.setId(member.getUserId());
                        members.add(member);
                        mAdapter.update(member);
                    }
                }
                mAdapter.sort();
                displayNothing(mAdapter.getItemCount() <= 0);
                displayLoading(false);
                //isLoadingComplete(true);
                //stopRefreshing();
            }
        }).listAllGroup();
    }

    @Override
    protected void onFetchingRemoteSquadComplete(Squad squad) {
        if (null != squad && !StringHelper.isEmpty(squad.getId())) {
            setCustomTitle(StringHelper.getString(R.string.ui_group_squad_member_fragment_title_string, squad.getName()));
            fetchingRemoteMembers(squad.getGroupId(), squad.getId());
        }
    }

    @Override
    protected void fetchingRemoteMembers(String groupId, String squadId) {
        if (isEmpty(groupId)) {
            return;
        }
        setLoadingText(R.string.ui_organization_contact_loading_text);
        displayLoading(true);
        displayNothing(false);
        super.fetchingRemoteMembers(groupId, squadId);
    }

    @Override
    protected void onFetchingRemoteMembersComplete(List<Member> list) {
        if (null != list && list.size() > 0) {
            members.clear();
            members.addAll(list);
            refreshMemberList();
        }
        displayLoading(false);
        displayNothing(mAdapter.getItemCount() < 1);
        stopRefreshing();
    }

    private void refreshMemberList() {
        clearAdapterNotExists();
        if (showType == TYPE_SQUAD) {
            // 扫描管理员
            for (Member member : members) {
                if (member.getUserId().equals(mSquad.getCreatorId())) {
                    if (null == member.getGroRole()) {
                        Role role = new Role();
                        role.setRolCode(Member.Code.GROUP_ROLE_CODE_SQUAD_MANAGER);
                        member.setGroRole(role);
                    }
                }
            }
        }
        mAdapter.clear();
        mAdapter.add(members);
        mAdapter.sort();
    }

    private String searchingText = "";
    private InputableSearchViewHolder.OnSearchingListener searchingListener = new InputableSearchViewHolder.OnSearchingListener() {
        @Override
        public void onSearching(String text) {
            searching(text);
            stopRefreshing();
        }
    };

    /**
     * 清除列表里不在新队列里的记录
     */
    private void clearAdapterNotExists() {
        Iterator<Member> iterator = mAdapter.iterator();
        while (iterator.hasNext()) {
            Member member = iterator.next();
            if (!members.contains(member)) {
                iterator.remove();
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void searching(String text) {
        searchingText = text;
        mAdapter.clear();
        for (Member member : members) {
            // 根据姓名和手机号码模糊查询
            if (!isEmpty(searchingText)) {
                if (!isEmpty(member.getUserName()) && member.getUserName().contains(text)) {
                    mAdapter.add(member);
                }
            } else {
                mAdapter.add(member);
            }
        }
        mAdapter.sort();
    }

    private ContactViewHolder.OnUserDeleteListener onUserDeleteListener = new ContactViewHolder.OnUserDeleteListener() {
        @Override
        public void onDelete(ContactViewHolder holder) {
            warningDeleteMember(holder.getAdapterPosition());
        }
    };

    // 删除成员警示框
    private void warningDeleteMember(final int index) {
        Member member = mAdapter.get(index);
        String name = member.getUserName();
        if (isEmpty(name)) {
            name = member.getPhone();
        }
        final String id = member.getId();
        SimpleDialogHelper.init(Activity()).show(StringHelper.getString(R.string.ui_organization_contact_remove_member, name), StringHelper.getString(R.string.ui_base_text_yes), StringHelper.getString(R.string.ui_base_text_cancel), new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                if (!isEmpty(mSquadId)) {
                    deleteSquadMember(id);
                } else {
                    deleteGroupMember(id);
                }
                return true;
            }
        }, null);
    }

    // 删除成员
    private void deleteGroupMember(final String memberId) {
        setLoadingText(R.string.ui_organization_contact_removing);
        displayLoading(true);
        MemberRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Member>() {
            @Override
            public void onResponse(Member member, boolean success, String message) {
                super.onResponse(member, success, message);
                if (success) {
                    Member mbr = new Member();
                    mbr.setId(memberId);
                    members.remove(mbr);
                    Member.remove(memberId);
                    //searching(searchingText);
                    mAdapter.remove(mbr);
                }
                ToastHelper.make().showMsg(message);
                displayLoading(false);
            }
        }).groupMemberDelete(memberId);
    }

    private void deleteSquadMember(final String memberId) {
        setLoadingText(R.string.ui_organization_contact_removing_squad_member);
        displayLoading(true);
        MemberRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Member>() {
            @Override
            public void onResponse(Member member, boolean success, String message) {
                super.onResponse(member, success, message);
                if (success) {
                    Member mbr = new Member();
                    mbr.setId(memberId);
                    members.remove(mbr);
                    Member.remove(memberId);
                    //searching(searchingText);
                    mAdapter.remove(mbr);
                }
                ToastHelper.make().showMsg(message);
                displayLoading(false);
            }
        }).squadMemberDelete(memberId);
    }

    private void updateMember(Member member, final Role toRole, final boolean resettable) {
        MemberRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Member>() {
            @Override
            public void onResponse(Member member, boolean success, String message) {
                super.onResponse(member, success, message);
                displayLoading(false);
                if (success) {
                    if (resettable) {
                        // 需要重置本地我的角色
                        //resetMyCharacter();
                    }
                    // 设置成功之后重新拉取成员列表
                    fetchingRemoteMembers(mOrganizationId, mSquadId);
                }
                ToastHelper.make().showMsg(message);
            }
        }).groupMemberUpdate(member.getId(), toRole);
    }

    // 点击用户打开用户详情
    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            // 点击打开用户属性页
            Member member = mAdapter.get(index);
            App.openUserInfo(ContactFragment.this, member.getUserId(), member.getGroupId());
        }
    };

    // 小组内设为管理员
    private OnHandleBoundDataListener<Member> onHandlerBoundDataListener = new OnHandleBoundDataListener<Member>() {
        @Override
        public Member onHandlerBoundData(BaseViewHolder holder) {
            // 升级或撤销管理员
            Member member = mAdapter.get(holder.getAdapterPosition());
            String name = member.getUserName();
            if (isEmpty(name)) {
                name = member.getPhone();
            }
            String text = StringHelper.getString(member.isGroupManager() ? R.string.ui_organization_contact_unset_to_manager : R.string.ui_organization_contact_set_to_manager, name);
            warningEditManager(text, holder.getAdapterPosition());
            return null;
        }
    };

    private void warningEditManager(String text, final int index) {
        SimpleDialogHelper.init(Activity()).show(text, StringHelper.getString(R.string.ui_base_text_yes), StringHelper.getString(R.string.ui_base_text_cancel), new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                resetManager(index);
                return true;
            }
        }, null);
    }

    private final int TYPE_MANAGER = 1, TYPE_ARCHIVE = 2;

    private void fetchingRemoteRoles(final int index, final int type) {
        RoleRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Role>() {
            @Override
            public void onResponse(List<Role> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    if (type == TYPE_MANAGER) {
                        resetManager(index);
                    } else if (type == TYPE_ARCHIVE) {
                        setAsArchiveManager(index);
                    }
                }
            }
        }).list();
    }

    private void resetManager(final int index) {
        Member member = mAdapter.get(index);
        setLoadingText(member.isGroupManager() ? R.string.ui_organization_contact_unseting_manager : R.string.ui_organization_contact_seting_manager);
        displayLoading(true);
        boolean isManager = member.isGroupManager();
        Role role = Role.getRoleByCode(isManager ? Member.Code.GROUP_ROLE_CODE_COMMON_MEMBER :
                showType == TYPE_ORG ? Member.Code.GROUP_ROLE_CODE_MANAGER : Member.Code.GROUP_ROLE_CODE_SQUAD_MANAGER);
        if (null == role) {
            // 如果角色为空则拉取服务器上的角色列表
            fetchingRemoteRoles(index, TYPE_MANAGER);
        } else {
            updateMember(member, role, false);
        }
    }

    // 设置为档案管理员
    private ContactViewHolder.OnSetArchiveManagerListener archiveManagerListener = new ContactViewHolder.OnSetArchiveManagerListener() {
        @Override
        public void onSetting(int index) {
            setAsArchiveManager(index);
        }
    };

    private void setAsArchiveManager(int index) {
        Member member = mAdapter.get(index);
        setLoadingText(member.isArchiveManager() ? R.string.ui_organization_contact_unset_to_archive_manager : R.string.ui_organization_contact_set_to_archive_manager);
        displayLoading(true);
        boolean isArchiveManager = member.isArchiveManager();
        Role role = Role.getRoleByCode(isArchiveManager ? Member.Code.GROUP_ROLE_CODE_COMMON_MEMBER : Member.Code.GROUP_ROLE_CODE_DOC_MANAGER);
        if (null == role) {
            // 如果角色为空则拉取服务器上的角色列表
            fetchingRemoteRoles(index, TYPE_ARCHIVE);
        } else {
            updateMember(member, role, false);
        }
    }

//    private ContactViewHolder.OnPhoneDialListener onPhoneDialListener = new ContactViewHolder.OnPhoneDialListener() {
//        @Override
//        public void onDial(int index) {
//            dialIndex = index;
//            requestPhoneCallPermission();
//        }
//    };

    @Override
    public void permissionGranted(String[] permissions, int requestCode) {
        super.permissionGranted(permissions, requestCode);
        if (requestCode == GRANT_PHONE_CALL) {
            warningDial();
        }
    }

    private void warningDial() {
        if (dialIndex < 0) return;
        final String text = mAdapter.get(dialIndex).getPhone();
        if (!isEmpty(text)) {
            String yes = getString(R.string.ui_base_text_dial);
            String no = getString(R.string.ui_base_text_cancel);
            SimpleDialogHelper.init(Activity()).show(text, yes, no, new DialogHelper.OnDialogConfirmListener() {
                @Override
                public boolean onConfirm() {
                    dialPhone(text);
                    return true;
                }
            }, null);
        }
    }

    private class ContactAdapter extends RecyclerViewAdapter<ContactViewHolder, Member> {

        @Override
        protected int comparator(Member item1, Member item2) {
            return item1.getSpell().compareTo(item2.getSpell());
        }

        private int getFirstCharCount(char chr) {
            int ret = 0;
            for (int i = 0, size = getItemCount(); i < size; i++) {
                if (get(i).getSpell().charAt(0) == chr) {
                    ret++;
                }
            }
            return ret;
        }

        @Override
        public ContactViewHolder onCreateViewHolder(View itemView, int viewType) {
            ContactViewHolder holder = new ContactViewHolder(itemView, ContactFragment.this);
            // 打开用户详情页
            holder.addOnViewHolderClickListener(onViewHolderClickListener);
            // 设为管理员
            holder.addOnHandlerBoundDataListener(onHandlerBoundDataListener);
            // 删除用户
            holder.setOnUserDeleteListener(onUserDeleteListener);
            // 转让管理权
            //holder.setOnTransferManagementListener(transferManagementListener);
            // 设置档案管理员
            holder.setOnSetArchiveManagerListener(archiveManagerListener);
            // 点击拨号
            //holder.setOnPhoneDialListener(onPhoneDialListener);
            //holder.setPhoneVisible(showType != TYPE_MINE);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_organization_contact;
        }

        @Override
        public void onBindHolderOfView(ContactViewHolder holder, int position, @Nullable Member member) {
            String memberUserId = (null != member) ? member.getUserId() : "";
            boolean isMe = !isEmpty(memberUserId) && memberUserId.equals(Cache.cache().userId);

            if (showType == TYPE_ORG) {
                // 组织内可以显示设为档案管理员或取消档案管理员
                // 对方不是管理员且不是档案管理员时，可以将其设为档案管理员
                holder.button0d5Text((null != member && member.isArchiveManager()) ? R.string.ui_organization_contact_unset_archive_manager : R.string.ui_organization_contact_set_archive_manager);
                holder.showButton0d5(!isMe && hasOperation(GRPOperation.MEMBER_ROLE) && (null != member && !member.isGroupManager()));
            } else {
                holder.showButton0d5(false);
            }

            if (showType == TYPE_ORG) {
                // 显示设为管理员或取消管理员
                holder.button1Text((null != member && member.isGroupManager()) ? R.string.ui_squad_contact_unset_to_admin : R.string.ui_squad_contact_set_to_admin);
                // 我是群主或管理员且有编辑成员角色属性时，可以设置
                holder.showButton1(!isMe && hasOperation(GRPOperation.MEMBER_ROLE));
            } else {
                holder.showButton1(false);
            }

            if (showType == TYPE_ORG) {
                // 我且具有删除成员权限，且对方是普通成员时显示删除按钮
                holder.showButton2(!isMe && hasOperation(GRPOperation.MEMBER_DELETE) && (null != member && !member.isGroupManager()));
            } else {
                // 小组成员删除权限
                holder.showButton2(!isMe && (hasOperation(GRPOperation.SQUAD_MEMBER_DELETE) || (null != mSquad.getGroRole() && mSquad.getGroRole().hasOperation(GRPOperation.SQUAD_MEMBER_DELETE))));
            }

            holder.showContent(member, searchingText);
        }
    }

    private class StickDecoration extends RecyclerView.ItemDecoration {
        private int topHeight = getDimension(R.dimen.ui_static_dp_20);
        private int padding = getDimension(R.dimen.ui_base_dimen_margin_padding);
        private int textSize = getDimension(R.dimen.ui_base_text_size_little);
        private TextPaint textPaint;
        private Paint paint;
        private static final String FMT = "%s(%d人)";
        private float baseLine, textHeight;

        StickDecoration() {
            super();
            paint = new Paint();
            paint.setColor(getColor(R.color.textColorHintLightLight));
            textPaint = new TextPaint();
            textPaint.setAntiAlias(true);
            textPaint.setTextSize(textSize);
            textPaint.setColor(getColor(R.color.textColorHint));
            Paint.FontMetrics fm = textPaint.getFontMetrics();
            // 计算文字高度
            textHeight = fm.bottom - fm.top;
            baseLine = fm.bottom;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int position = parent.getChildAdapterPosition(view);
            if (isFirstInGroup(position)) {
                outRect.top = topHeight;
            } else {
                outRect.top = 0;
            }
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDraw(c, parent, state);
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View view = parent.getChildAt(i);
                int position = parent.getChildAdapterPosition(view);
                //String textLine = members.get(position).getSpell().substring(0, 1);
                //textLine = format(FMT, textLine, getFirstCharCount(textLine.charAt(0)));
                if (isFirstInGroup(position)) {
                    float top = view.getTop() - topHeight;
                    float bottom = view.getTop();
                    drawBackground(c, left, top, right, bottom);
                    drawText(c, position, padding, bottom - (topHeight - textHeight) / 2 - baseLine);
                    //c.drawRect(left, top, right, bottom, paint);//绘制矩形背景
                    //c.drawText(textLine, left + padding, bottom - 30, textPaint);//绘制文本
                }
            }
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDrawOver(c, parent, state);
            int position = ((LinearLayoutManager) (parent.getLayoutManager())).findFirstVisibleItemPosition();
            if (position < 0) {
                return;
            }
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();
            int top = parent.getPaddingTop();
            int bottom = top + topHeight;
            drawBackground(c, left, top, right, bottom);
            drawText(c, position, padding, bottom - (topHeight - textHeight) / 2 - baseLine);
            //c.drawRect(left, 0, right, topHeight, paint);//绘制红色矩形
            //String text = members.get(position).getSpell().substring(0, 1);
            //text = format(FMT, text, getFirstCharCount(text.charAt(0)));
            //c.drawText(text, 30, topHeight - 30, textPaint);//绘制文本
        }

        private void drawBackground(Canvas canvas, float left, float top, float right, float bottom) {
            // 绘制矩形背景
            canvas.drawRect(left, top, right, bottom, paint);
        }

        private void drawText(Canvas canvas, int position, float x, float y) {
            String text = mAdapter.get(position).getSpell().substring(0, 1);
            text = format(FMT, text, mAdapter.getFirstCharCount(text.charAt(0)));
            // 绘制文本
            canvas.drawText(text, x, y, textPaint);
        }

        private boolean isFirstInGroup(int position) {
            return position >= 0 && (position == 0 || mAdapter.get(position).getSpell().charAt(0) != mAdapter.get(position - 1).getSpell().charAt(0));
        }
    }
}
