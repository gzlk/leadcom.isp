package com.gzlk.android.isp.fragment.organization;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.daimajia.swipe.util.Attributes;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.activity.TitleActivity;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.org.MemberRequest;
import com.gzlk.android.isp.fragment.individual.UserPropertyFragment;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.SimpleDialogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.helper.TooltipHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.common.SearchableViewHolder;
import com.gzlk.android.isp.holder.organization.ContactViewHolder;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.organization.Member;
import com.gzlk.android.isp.model.organization.Role;
import com.gzlk.android.isp.model.organization.Squad;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
     * 新建一个实例
     * param: 0=type,1=groupId,2=squadId
     */
    public static ContactFragment newInstance(String params) {
        ContactFragment cf = new ContactFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        // 类型
        bundle.putInt(PARAM_TYPE, Integer.valueOf(strings[0]));
        // 组织的id
        bundle.putString(PARAM_QUERY_ID, strings[1]);
        // 小组的id
        bundle.putString(PARAM_SQUAD_ID, strings[2]);
        cf.setArguments(bundle);
        return cf;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        showType = bundle.getInt(PARAM_TYPE, TYPE_NONE);
        isCreator = bundle.getBoolean(PARAM_CREATOR, false);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_TYPE, showType);
        bundle.putBoolean(PARAM_CREATOR, isCreator);
    }

    // view
    @ViewId(R.id.ui_holder_view_searchable_container)
    private View searchView;
    @ViewId(R.id.ui_tool_view_phone_contact_container)
    private View phoneContactView;

    // holder
    private SearchableViewHolder searchableViewHolder;
    private ArrayList<Member> members = new ArrayList<>();
    private ContactAdapter mAdapter;

    // 默认显示组织的联系人列表
    private int showType = TYPE_ORG;
    /**
     * 当前登录者是否是组织的创建者
     */
    private boolean isCreator = false;

    @Override
    public int getLayout() {
        return R.layout.fragment_contact;
    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    public void setNewQueryId(String queryId) {
        if (!StringHelper.isEmpty(mQueryId) && mQueryId.equals(queryId)) {
            return;
        }
        // 切换组织指挥设置可以加载更多
        remotePageNumber = 1;
        isLoadingComplete(false);
        setSupportLoadingMore(true);
        mQueryId = queryId;
        mOrganizationId = mQueryId;
        members.clear();
        mAdapter.clear();
        loadingQueryItem();
    }

    public void setIsCreator(boolean isCreator) {
        this.isCreator = isCreator;
    }

    @Click({R.id.ui_tool_view_phone_contact_container})
    private void elementClick(View view) {
        openActivity(PhoneContactFragment.class.getName(), format("%s,", mQueryId), true, false);
    }

    @Override
    public void doingInResume() {
        searchView.setVisibility(showType == TYPE_SQUAD ? View.VISIBLE : View.GONE);
        phoneContactView.setVisibility(showType == TYPE_ORG ? View.VISIBLE : View.GONE);
        setNothingText(showType == TYPE_ORG ? R.string.ui_organization_contact_no_member : R.string.ui_organization_contact_squad_no_member);
        initializeTitleEvent();
        initializeHolders();
    }

    // 小组联系人列表时，需要处理标题栏
    private void initializeTitleEvent() {
        if (showType == TYPE_SQUAD) {
            setRightIcon(R.string.ui_icon_add);
            setRightTitleClickListener(new OnTitleButtonClickListener() {
                @Override
                public void onClick() {
                    showTooltip(((TitleActivity) Activity()).getRightButton(), R.id.ui_tooltip_squad_contact_picker, true, TooltipHelper.TYPE_RIGHT, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupMenuClickHandle(v);
                        }
                    });
                }
            });
        }
    }

    private void popupMenuClickHandle(View view) {
        switch (view.getId()) {
            case R.id.ui_tooltip_menu_squad_contact_organization:
                // 打开组织通讯录并尝试将里面的用户邀请到小组
                openActivity(OrganizationContactFragment.class.getName(), format("%s,%s", mOrganizationId, mSquadId), true, false);
                break;
            case R.id.ui_tooltip_menu_squad_contact_phone:
                // 打开手机通讯录，并尝试将用户拉进小组
                openActivity(PhoneContactFragment.class.getName(), format("%s,%s", mOrganizationId, mSquadId), true, false);
                break;
        }
    }

    /**
     * 打开手机通讯录并添加成员到当前组织
     */
    public void addMemberToOrganizationFromPhoneContact(View view) {
        if (showType != TYPE_ORG) return;
        if (StringHelper.isEmpty(mQueryId)) {
            ToastHelper.make().showMsg(R.string.ui_organization_structure_no_group_exist);
            return;
        }
        openActivity(PhoneContactFragment.class.getName(), format("%s,", mQueryId), true, false);
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        // 小组才显示标题栏，组织通讯录不需要
        return showType == TYPE_SQUAD;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    protected void onSwipeRefreshing() {
        fetchingRemoteMembers(mOrganizationId, mSquadId);
    }

    @Override
    protected void onLoadingMore() {
        fetchingRemoteMembers(mOrganizationId, mSquadId);
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    @Override
    protected void onViewPagerDisplayedChanged(boolean visible) {
        super.onViewPagerDisplayedChanged(visible);
        if (visible) {
            loadingQueryItem();
        }
    }

    private void initializeHolders() {
        if (null == searchableViewHolder) {
            searchableViewHolder = new SearchableViewHolder(mRootView, this);
            searchableViewHolder.setOnSearchingListener(searchingListener);
        }
        initializeAdapter();
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new ContactAdapter();
            mAdapter.setMode(Attributes.Mode.Single);
            mRecyclerView.addItemDecoration(new StickDecoration());
            mRecyclerView.setAdapter(mAdapter);
            searchingListener.onSearching("");
        }
        if (getUserVisibleHint()) {
            loadingQueryItem();
        }
    }

    /**
     * 加载查询的对象
     */
    private void loadingQueryItem() {
        if (StringHelper.isEmpty(mQueryId)) {
            return;
        }
        switch (showType) {
            case TYPE_ORG:
                fetchingRemoteMembers(mQueryId, "");
                break;
            case TYPE_SQUAD:
                loadingSquad();
                break;
        }
    }

    private void loadingSquad() {
        Squad squad = new Dao<>(Squad.class).query(mQueryId);
        if (null == squad) {
            fetchingRemoteSquad(mSquadId);
        } else {
            setCustomTitle(squad.getName());
            if (isEmpty(mOrganizationId)) {
                mOrganizationId = squad.getGroupId();
            }
            fetchingRemoteMembers(mOrganizationId, mSquadId);
        }
    }

    @Override
    protected void onFetchingRemoteSquadComplete(Squad squad) {
        if (null != squad && !StringHelper.isEmpty(squad.getId())) {
            setCustomTitle(squad.getName());
            fetchingRemoteMembers(squad.getGroupId(), squad.getId());
        }
    }

    @Override
    protected void fetchingRemoteMembers(String groupId, String squadId) {
        setLoadingText(R.string.ui_organization_contact_loading_text);
        displayLoading(true);
        displayNothing(false);
        super.fetchingRemoteMembers(groupId, squadId);
    }

    @Override
    protected void onFetchingRemoteMembersComplete(List<Member> list) {
        if (null != list && list.size() > 0) {
            for (Member member : list) {
                if (!members.contains(member)) {
                    members.add(member);
                } else {
                    int index = members.indexOf(member);
                    members.set(index, member);
                }
            }
            //Collections.sort(members, new MemberComparator());
            searchingListener.onSearching(searchingText);
        }
        displayLoading(false);
        displayNothing(mAdapter.getItemCount() < 1);
        stopRefreshing();
    }

    /**
     * 根据加入时间排序
     */
    private class MemberComparator implements Comparator<Member> {
        @Override
        public int compare(Member u1, Member u2) {
            return u1.getSpell().compareTo(u2.getSpell());
        }
    }

    private String searchingText = "";
    private SearchableViewHolder.OnSearchingListener searchingListener = new SearchableViewHolder.OnSearchingListener() {
        @Override
        public void onSearching(String text) {
            if (!StringHelper.isEmpty(text)) {
                searching(text);
            } else {
                searchingText = "";
                mAdapter.addAll(members);
            }
            stopRefreshing();
        }
    };

    private void searching(String text) {
        searchingText = text;
        mAdapter.clear();
        for (Member member : members) {
            // 根据姓名和手机号码模糊查询
            if (member.getUserName().contains(text) || member.getPhone().contains(text)) {
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
        SimpleDialogHelper.init(Activity()).show(StringHelper.getString(R.string.ui_organization_contact_remove_member, name), StringHelper.getString(R.string.ui_base_text_yes), StringHelper.getString(R.string.ui_base_text_cancel), new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                deleteMember(index);
                return true;
            }
        }, null);
    }

    // 删除成员
    private void deleteMember(final int index) {
        setLoadingText(R.string.ui_organization_contact_removing);
        displayLoading(true);
        Member member = mAdapter.get(index);
        MemberRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Member>() {
            @Override
            public void onResponse(Member member, boolean success, String message) {
                super.onResponse(member, success, message);
                if (success) {
                    members.remove(index);
                    searching(searchingText);
                }
                ToastHelper.make().showMsg(message);
                displayLoading(false);
            }
        }).groupMemberDelete(member.getId(), member.getGroupId());
    }

    // 转让组群，转让管理权
    private ContactViewHolder.OnTransferManagementListener transferManagementListener = new ContactViewHolder.OnTransferManagementListener() {
        @Override
        public void onTransfer(ContactViewHolder holder) {
            Member member = mAdapter.get(holder.getAdapterPosition());
            String name = member.getUserName();
            if (isEmpty(name)) {
                name = member.getPhone();
            }
            Member me = StructureFragment.my;
            String text = StringHelper.getString(me.isOwner() ? R.string.ui_organization_contact_transfer_owner_to : R.string.ui_organization_contact_transfer_management_to, name);
            warningTransfer(text, holder.getAdapterPosition());
        }
    };

    private void warningTransfer(String text, final int index) {
        SimpleDialogHelper.init(Activity()).show(text, StringHelper.getString(R.string.ui_base_text_yes), StringHelper.getString(R.string.ui_base_text_cancel), new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                transferManage(index);
                return true;
            }
        }, null);
    }

    private void transferManage(int index) {
        Member member = mAdapter.get(index);
        Member me = StructureFragment.my;
        setLoadingText(me.isOwner() ? R.string.ui_organization_contact_transferring_owner : R.string.ui_organization_contact_transferring_management);
        displayLoading(true);
        Role role = new Role();
        role.setId(me.isOwner() ? Member.Code.GROUP_OWNER_ROLE_ID : Member.Code.GROUP_MANAGER_ROLE_ID);
        role.setRoleName(me.isOwner() ? Member.Code.GROUP_OWNER_ROLE_NAME : Member.Code.GROUP_MANAGER_ROLE_NAME);
        role.setRolCode(me.isOwner() ? Member.Code.GROUP_OWNER_ROLE_CODE : Member.Code.GROUP_MANAGER_ROLE_CODE);
        updateMember(member, role, true);
    }

    /**
     * 重置我的角色为普通角色
     */
    private void resetMyCharacter() {
        Member me = StructureFragment.my;
        me.getGroRole().setId(Member.Code.GROUP_COMMON_MEMBER_ROLE_ID);
        me.getGroRole().setRolCode(Member.Code.GROUP_COMMON_MEMBER_ROLE_CODE);
        me.getGroRole().setRoleName(Member.Code.GROUP_COMMON_MEMBER_ROLE_NAME);
        me.getGroRole().getPerList().clear();
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
                        resetMyCharacter();
                    }
                    // 设置成功之后重新拉取成员列表
                    fetchingRemoteMembers(mOrganizationId, mSquadId);
                }
                ToastHelper.make().showMsg(message);
            }
        }).groupMemberUpdate(member.getId(), member.getGroupId(), toRole, member.getUserId());
    }

    // 点击用户打开用户详情
    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            // 点击打开用户属性页
            Member member = mAdapter.get(index);
            openActivity(UserPropertyFragment.class.getName(), member.getUserId(), false, false, true);
        }
    };

    // 小组内设为管理员
    private BaseViewHolder.OnHandlerBoundDataListener<Member> onHandlerBoundDataListener = new BaseViewHolder.OnHandlerBoundDataListener<Member>() {
        @Override
        public Member onHandlerBoundData(BaseViewHolder holder) {
            // 升级或撤销管理员
            Member member = mAdapter.get(holder.getAdapterPosition());
            String name = member.getUserName();
            if (isEmpty(name)) {
                name = member.getPhone();
            }
            String text = StringHelper.getString(member.isManager() ? R.string.ui_organization_contact_unset_to_manager : R.string.ui_organization_contact_set_to_manager, name);
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

    private void resetManager(final int index) {
        Member member = mAdapter.get(index);
        setLoadingText(member.isManager() ? R.string.ui_organization_contact_unseting_manager : R.string.ui_organization_contact_seting_manager);
        displayLoading(true);
        Role role = new Role();
        role.setId(member.isManager() ? Member.Code.GROUP_COMMON_MEMBER_ROLE_ID : Member.Code.GROUP_MANAGER_ROLE_ID);
        role.setRoleName(member.isManager() ? Member.Code.GROUP_COMMON_MEMBER_ROLE_NAME : Member.Code.GROUP_MANAGER_ROLE_NAME);
        role.setRolCode(member.isManager() ? Member.Code.GROUP_COMMON_MEMBER_ROLE_CODE : Member.Code.GROUP_MANAGER_ROLE_CODE);
        updateMember(member, role, false);
    }

    private static final int REQ_MEMBER = ACTIVITY_BASE_REQUEST + 10;

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
        Role role = new Role();
        role.setId(member.isArchiveManager() ? Member.Code.GROUP_COMMON_MEMBER_ROLE_ID : Member.Code.GROUP_DOC_MANAGER_ROLE_ID);
        role.setRolCode(member.isArchiveManager() ? Member.Code.GROUP_COMMON_MEMBER_ROLE_CODE : Member.Code.GROUP_DOC_MANAGER_ROLE_CODE);
        role.setRoleName(member.isArchiveManager() ? Member.Code.GROUP_COMMON_MEMBER_ROLE_NAME : Member.Code.GROUP_DOC_MANAGER_ROLE_NAME);
        updateMember(member, role, false);
    }

    private class ContactAdapter extends RecyclerSwipeAdapter<ContactViewHolder> {

        private ArrayList<Member> list = new ArrayList<>();

        private void delete(ContactViewHolder holder) {
            mItemManger.removeShownLayouts(holder.getSwipeLayout());
            int pos = holder.getAdapterPosition();
            remove(pos);
            mItemManger.closeAllItems();
        }

        private Member get(int position) {
            return list.get(position);
        }

        private void sort() {
            Collections.sort(mAdapter.list, new MemberComparator());
            notifyItemRangeChanged(0, list.size());
        }

        private void addAll(List<Member> all) {
            if (null == all || all.size() < 1) return;

            for (Member member : all) {
                add(member);
            }
            sort();
        }

        private void add(Member member) {
            int index = list.indexOf(member);
            if (index < 0) {
                list.add(member);
                notifyItemChanged(list.size() - 1);
            } else {
                list.set(index, member);
                notifyItemChanged(index);
            }
        }

        private void clear() {
            int size = list.size();
            while (size > 0) {

                remove(size - 1);
                size = list.size();
            }
        }

        private void remove(int index) {
            list.remove(index);
            notifyItemRemoved(index);
        }

        private void delete(Member member) {
            int index = list.indexOf(member);
            if (index >= 0) {
                remove(index);
            }
        }

        private int getFirstCharCount(char chr) {
            int ret = 0;
            for (Member member : list) {
                if (member.getSpell().charAt(0) == chr) {
                    ret++;
                }
            }
            return ret;
        }

        @Override
        public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            int layout = R.layout.holder_view_organization_contact;
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(layout, viewGroup, false);
            ContactViewHolder holder = new ContactViewHolder(view, ContactFragment.this);
            // 打开用户详情页
            holder.addOnViewHolderClickListener(onViewHolderClickListener);
            // 设为管理员
            holder.addOnHandlerBoundDataListener(onHandlerBoundDataListener);
            // 删除用户
            holder.setOnUserDeleteListener(onUserDeleteListener);
            // 转让管理权
            holder.setOnTransferManagementListener(transferManagementListener);
            holder.setOnSetArchiveManagerListener(archiveManagerListener);
            return holder;
        }

        @Override
        public void onBindViewHolder(ContactViewHolder holder, int position) {
            Member member = list.get(position);
            Member me = StructureFragment.my;
            boolean isMe = (null != me) && !isEmpty(member.getId()) && !isEmpty(me.getId()) && member.getId().equals(me.getId());
            // 转让群组或转让管理权
            if (showType == TYPE_ORG) {
                // 组织内转让管理权
                if ((null != me) && me.isManager() && member.isManager()) {
                    holder.button0Text(R.string.ui_organization_contact_transfer_manager);
                    holder.showButton0(true);
                } else if ((null != me) && me.isOwner() && member.isManager()) {
                    holder.button0Text(R.string.ui_organization_contact_transfer_owner);
                    holder.showButton0(true);
                } else {
                    holder.showButton0(false);
                }
//                holder.button0Text(R.string.ui_organization_contact_transfer_manager);
//                holder.showButton0(!isMe && (null != me) && me.isManager() && member.isManager());
//                //} else if (showType == TYPE_SQUAD) {
//                // 小组内转让组群
//                holder.button0Text(R.string.ui_organization_contact_transfer_owner);
//                // 我是群主且对方是管理员时才允许转让群组
//                holder.showButton0(!isMe && (null != me) && me.isOwner() && member.isManager());
            } else {
                holder.showButton0(false);
            }

            if (showType == TYPE_ORG) {
                // 组织内可以显示设为档案管理员或取消档案管理员
                // 对方不是管理员且不是档案管理员时，可以将其设为档案管理员
                holder.button0d5Text(member.isArchiveManager() ? R.string.ui_organization_contact_unset_archive_manager : R.string.ui_organization_contact_set_archive_manager);
                holder.showButton0d5(!isMe && (null != me) && me.isRoleEditable() && !(member.isManager() || member.isOwner()));
            } else {
                holder.showButton0d5(false);
            }

            if (showType == TYPE_ORG) {
                // 显示设为管理员或取消管理员
                holder.button1Text(member.isManager() ? R.string.ui_squad_contact_unset_to_admin : R.string.ui_squad_contact_set_to_admin);
                // 我是群主或管理员且有编辑成员角色属性时，可以设置
                holder.showButton1(!isMe && (null != me) && (me.isOwner() || me.isManager()) && me.isRoleEditable() && !(member.isManager() || member.isOwner()));
            } else {
                holder.showButton1(false);
            }

            // 我且具有删除成员权限，且对方是普通成员时显示删除按钮
            holder.showButton2(!isMe && (null != me) && me.isMemberDeletable() && member.isMember());

            holder.showContent(member, searchingText);
            mItemManger.bindView(holder.itemView, position);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public int getSwipeLayoutResourceId(int i) {
            return R.id.ui_holder_view_contact_swipe_layout;
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
            String text = mAdapter.list.get(position).getSpell().substring(0, 1);
            text = format(FMT, text, mAdapter.getFirstCharCount(text.charAt(0)));
            // 绘制文本
            canvas.drawText(text, x, y, textPaint);
        }

        private boolean isFirstInGroup(int position) {
            return position >= 0 && (position == 0 || mAdapter.list.get(position).getSpell().charAt(0) != mAdapter.list.get(position - 1).getSpell().charAt(0));
        }
    }
}
