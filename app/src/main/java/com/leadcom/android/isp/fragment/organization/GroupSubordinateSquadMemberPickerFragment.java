package com.leadcom.android.isp.fragment.organization;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.org.RelationRequest;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.organization.ContactViewHolder;
import com.leadcom.android.isp.holder.organization.GroupInterestViewHolder;
import com.leadcom.android.isp.holder.organization.SquadViewHolder;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.organization.Member;
import com.leadcom.android.isp.model.organization.RelateGroup;
import com.leadcom.android.isp.model.organization.Squad;
import com.leadcom.android.isp.model.organization.SubMember;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * <b>功能描述：</b>组织成员、小组成员，以及下级组织拾取器<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/12/07 13:20 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/12/07 13:20  <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class GroupSubordinateSquadMemberPickerFragment extends GroupBaseFragment {

    public static GroupSubordinateSquadMemberPickerFragment newInstance(Bundle bundle) {
        GroupSubordinateSquadMemberPickerFragment gssmpf = new GroupSubordinateSquadMemberPickerFragment();
        gssmpf.setArguments(bundle);
        return gssmpf;
    }

    public static void open(BaseFragment fragment, String groupId, String groupName, String title, ArrayList<String> groups, ArrayList<SubMember> members) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, groupId);
        bundle.putString(PARAM_NAME, groupName);
        bundle.putString(PARAM_JSON, title);
        bundle.putBoolean(PARAM_SQUAD_ID, true);
        bundle.putStringArrayList(PARAM_SEARCHED, groups);
        bundle.putSerializable(PARAM_SELECTABLE, members);
        fragment.openActivity(GroupSubordinateSquadMemberPickerFragment.class.getName(), bundle, REQUEST_SELECT, true, false);
    }

    @ViewId(R.id.ui_tool_view_select_all_root)
    private View selectAllRoot;
    @ViewId(R.id.ui_tool_view_select_all_title)
    private TextView selectAllTitle;
    @ViewId(R.id.ui_tool_view_select_all_icon)
    private CustomTextView selectAllIcon;

    private String mGroupName, mTitle, searchingText = "", mGroupMemberSquadId;
    private ArrayList<String> groups;
    private ArrayList<Squad> squads = new ArrayList<>();
    private ArrayList<SubMember> members;
    private PickerAdapter mAdapter;
    private boolean showSubordinate = true;

    @SuppressWarnings("unchecked")
    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mGroupName = bundle.getString(PARAM_NAME, "");
        mTitle = bundle.getString(PARAM_JSON, "");
        showSubordinate = bundle.getBoolean(PARAM_SQUAD_ID, true);
        groups = bundle.getStringArrayList(PARAM_SEARCHED);
        if (null == groups) {
            groups = new ArrayList<>();
        }
        members = (ArrayList<SubMember>) bundle.getSerializable(PARAM_SELECTABLE);
        if (null == members) {
            members = new ArrayList<>();
        }
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_NAME, mGroupName);
        bundle.putBoolean(PARAM_SQUAD_ID, showSubordinate);
        bundle.putString(PARAM_JSON, mTitle);
        bundle.putStringArrayList(PARAM_SEARCHED, groups);
        bundle.putSerializable(PARAM_SELECTABLE, members);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mGroupMemberSquadId = "group_" + mQueryId;
        enableSwipe(false);
        isLoadingComplete(true);
        setCustomTitle(isEmpty(mTitle) ? "选择成员" : mTitle);
        setRightText(R.string.ui_base_text_confirm);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                // 返回选择了的成员或组织
                resultSelectedData();
            }
        });
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_organization_member;
    }

    @Override
    protected void onSwipeRefreshing() {

    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    public void doingInResume() {
        if (null == mAdapter) {
            mAdapter = new PickerAdapter();
            mRecyclerView.setAdapter(mAdapter);
            OverScrollDecoratorHelper.setUpOverScroll(mRecyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
            RelateGroup group = new RelateGroup();
            group.setId(mQueryId);
            group.setGroupId(mQueryId);
            group.setGroupName(mGroupName + ("(<font color=\"#a1a1a1\">本组织</font>)"));
            mAdapter.add(group);
            // 拉取下级组织列表
            if (showSubordinate) {
                loadSubordinates();
            }
        }
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    private void resultSelectedData() {
        // 选择了的成员
        ArrayList<SubMember> selected = new ArrayList<>();
        for (Squad squad : squads) {
            for (Member member : squad.getGroSquMemberList()) {
                if (member.isSelected()) {
                    SubMember sub = new SubMember(member);
                    if (sub.getSquadId().equals(mGroupMemberSquadId)) {
                        sub.setSquadId("");
                    }
                    selected.add(sub);
                }
            }
        }
        Iterator<Model> iterator = mAdapter.iterator();
        while (iterator.hasNext()) {
            Model model = iterator.next();
            if (model instanceof RelateGroup && model.isSelected()) {
                selected.add(new SubMember((RelateGroup) model));
            }
        }
        resultData(SubMember.toJson(selected));
    }

    private void loadSubordinates() {
        RelationRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<RelateGroup>() {
            @Override
            public void onResponse(List<RelateGroup> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success && null != list) {
                    for (RelateGroup group : list) {
                        if (groups.contains(group.getGroupId())) {
                            group.setSelected(true);
                        }
                        mAdapter.update(group);
                    }
                }
            }
        }).list(mQueryId, RelateGroup.RelationType.SUBORDINATE);
    }

    @Override
    protected void onFetchingRemoteSquadsComplete(List<Squad> list) {
        if (null != list) {
            squads.clear();
            for (Squad squad : list) {
                for (Member member : squad.getGroSquMemberList()) {
                    SubMember sub = new SubMember(member);
                    if (isMemberExist(sub, true)) {
                        member.setSelected(true);
                    }
                }
                squad.setSelected(isSquadMemberAllSelected(squad));
            }
            squads.addAll(list);
            Squad squad = new Squad();
            squad.setId(mGroupMemberSquadId);
            squad.setName("本组织成员");
            squads.add(squad);
            displaySquads();
        }
        // 停止
        Model model = mAdapter.get(0);
        model.setRead(false);
        mAdapter.update(model);
    }

    private boolean isSquadMemberAllSelected(Squad squad) {
        for (Member member : squad.getGroSquMemberList()) {
            if (!member.isSelected()) {
                return false;
            }
        }
        return true;
    }

    private boolean isMemberExist(SubMember m, boolean squadable) {
        for (SubMember member : members) {
            if (squadable) {
                if (!isEmpty(member.getSquadId()) && member.getSquadId().equals(m.getSquadId()) && member.getUserId().equals(m.getUserId())) {
                    return true;
                }
            } else {
                if (isEmpty(member.getSquadId()) && member.getUserId().equals(m.getUserId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onFetchingRemoteMembersComplete(List<Member> list) {
        Squad squad = (Squad) mAdapter.get(mGroupMemberSquadId);
        squad.getGroSquMemberList().clear();
        if (null != list && list.size() > 0) {
            for (Member member : list) {
                member.setSquadId(mGroupMemberSquadId);
                SubMember sub = new SubMember();
                sub.setUserId(member.getUserId());
                member.setSelected(isMemberExist(sub, false));
            }
            squad.getGroSquMemberList().addAll(list);
            displaySquadMember(squad, mAdapter.indexOf(squad));
        }
        squad.setSelected(isSquadMemberAllSelected(squad));
        squad.setRead(false);
        mAdapter.update(squad);
    }

    private void displaySquads() {
        Model group = mAdapter.get(mQueryId);
        int index = mAdapter.indexOf(group), cnt = 0;
        for (Squad squad : squads) {
            cnt++;
            mAdapter.add(squad, index + cnt);
            if (squad.isSelectable()) {
                // 同时恢复小组成员的展开状态
                for (Member member : squad.getGroSquMemberList()) {
                    cnt++;
                    mAdapter.add(member, index + cnt);
                }
            }
            if (!squad.getId().equals(mGroupMemberSquadId)) {
                checkSquadMemberAllSelected(squad.getId());
            }
        }
    }

    // 从列表里删除小组和其已经显示了的成员
    private void removeSquads() {
        Iterator<Model> iterator = mAdapter.iterator();
        String lastSquadId = "";
        int cnt = 0;
        while (iterator.hasNext()) {
            Model model = iterator.next();
            if (model instanceof Squad) {
                cnt++;
                lastSquadId = model.getId();
                iterator.remove();
            } else if (model instanceof Member) {
                Member member = (Member) model;
                if (member.getSquadId().equals(lastSquadId)) {
                    cnt++;
                    iterator.remove();
                }
            }
        }
        mAdapter.notifyItemRangeRemoved(1, cnt);
    }

    private void displaySquadMember(Squad squad, int index) {
        int mIndex = 0;
        if (squad.isSelectable()) {
            // 显示小组成员
            if (squad.getGroSquMemberList().size() > 0) {
                for (Member member : squad.getGroSquMemberList()) {
                    boolean addable = true;
                    if (!isEmpty(searchingText)) {
                        // 如果是在搜索则只显示搜索匹配的记录
                        addable = !isEmpty(member.getUserName()) && member.getUserName().contains(searchingText);
                    }
                    if (addable) {
                        mIndex += 1;
                        mAdapter.add(member, index + mIndex);
                    }
                }
            } else if (squad.getId().equals(mGroupMemberSquadId)) {
                squad.setRead(true);
                mAdapter.update(squad);
                // 组织成员还未拉取，拉取组织成员
                fetchingRemoteMembers(mQueryId, "");
            }
        } else {
            // 隐藏小组成员
            Iterator<Model> iterator = mAdapter.iterator();
            while (iterator.hasNext()) {
                Model model = iterator.next();
                if (model instanceof Member) {
                    Member member = (Member) model;
                    if (!isEmpty(member.getSquadId()) && member.getSquadId().equals(squad.getId())) {
                        iterator.remove();
                    }
                }
            }
            mAdapter.notifyItemRangeRemoved(mAdapter.indexOf(squad) + 1, squad.getGroSquMemberList().size());
        }
    }

    private void selectSquadMembers(Squad squad) {
        squad.setSelected(!squad.isSelected());
        squad.setCollapseStatus(squad.isSelected() ? squad.getGroSquMemberList().size() : 0);
        mAdapter.update(squad);

        for (Member member : squad.getGroSquMemberList()) {
            member.setSelected(squad.isSelected());
            if (mAdapter.indexOf(member) >= 0) {
                mAdapter.update(member);
            }
        }
    }

    private void checkSquadMemberAllSelected(String squadId) {
        Squad squad = (Squad) mAdapter.get(squadId);
        int selected = 0;
        for (Member member : squad.getGroSquMemberList()) {
            if (member.isSelected()) {
                selected++;
            }
        }
        squad.setCollapseStatus(selected);
        squad.setSelected(selected == squad.getGroSquMemberList().size());
        mAdapter.update(squad);
    }

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            Model model = mAdapter.get(index);
            switch (view.getId()) {
                case R.id.ui_holder_view_group_interest_root:
                    // 下级组织的选中
                    if (model.getId().equals(mQueryId)) {
                        // 展开或收缩小组列表
                        model.setSelectable(!model.isSelectable());
                        mAdapter.update(model);
                        if (model.isSelectable()) {
                            // 展开小组列表
                            if (squads.size() <= 0) {
                                model.setRead(true);
                                mAdapter.update(model);
                                fetchingRemoteSquads(mQueryId);
                            } else {
                                displaySquads();
                            }
                        } else {
                            removeSquads();
                        }
                    } else {
                        model.setSelected(!model.isSelected());
                        mAdapter.update(model);
                    }
                    break;
                case R.id.ui_holder_view_group_squad_container:
                    // 显示或隐藏小组成员
                    model.setSelectable(!model.isSelectable());
                    mAdapter.update(model);
                    displaySquadMember((Squad) model, index);
                    break;
                case R.id.ui_holder_view_group_squad_picker:
                    // 小组成员全选或取消全选
                    selectSquadMembers((Squad) model);
                    break;
                case R.id.ui_holder_view_contact_layout:
                case R.id.ui_holder_view_contact_picker:
                    model.setSelected(!model.isSelected());
                    mAdapter.update(model);
                    // 组织成员或小组成员选择
                    Member member = (Member) model;
                    checkSquadMemberAllSelected(member.getSquadId());
                    break;
            }
        }
    };

    private class PickerAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {
        private static final int TP_SQUAD = 1, TP_SUBGROUP = 2, TP_MEMBER = 3;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            BaseFragment fragment = GroupSubordinateSquadMemberPickerFragment.this;
            switch (viewType) {
                case TP_MEMBER:
                    ContactViewHolder cvh = new ContactViewHolder(itemView, fragment);
                    cvh.setOnViewHolderElementClickListener(elementClickListener);
                    cvh.showPicker(true);
                    cvh.setLeftBlankTimes(2);
                    return cvh;
                case TP_SQUAD:
                    SquadViewHolder svh = new SquadViewHolder(itemView, fragment);
                    svh.setOnViewHolderElementClickListener(elementClickListener);
                    svh.showPicker(true);
                    svh.showBlank(true);
                    return svh;
                case TP_SUBGROUP:
                    GroupInterestViewHolder givh = new GroupInterestViewHolder(itemView, fragment);
                    givh.setOnViewHolderElementClickListener(elementClickListener);
                    givh.setSelectable(true);
                    // 显示上下级时才显示按钮
                    givh.setButtonShown(false);
                    return givh;
            }
            return null;
        }

        @Override
        public int itemLayout(int viewType) {
            switch (viewType) {
                case TP_SQUAD:
                    return R.layout.holder_view_group_squad;
                case TP_SUBGROUP:
                    return R.layout.holder_view_group_interesting_item;
                case TP_MEMBER:
                    return R.layout.tool_view_organization_contact;
            }
            return 0;
        }

        @Override
        public int getItemViewType(int position) {
            Model model = get(position);
            if (model instanceof RelateGroup) {
                // 下级组织
                return TP_SUBGROUP;
            } else if (model instanceof Squad) {
                return TP_SQUAD;
            }
            return TP_MEMBER;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof ContactViewHolder) {
                ((ContactViewHolder) holder).showContent((Member) item, "");
            } else if (holder instanceof SquadViewHolder) {
                ((SquadViewHolder) holder).showContent((Squad) item, "");
            } else if (holder instanceof GroupInterestViewHolder) {
                ((GroupInterestViewHolder) holder).showContent((RelateGroup) item, "");
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
