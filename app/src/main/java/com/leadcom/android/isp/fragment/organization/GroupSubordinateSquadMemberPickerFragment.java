package com.leadcom.android.isp.fragment.organization;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.hlk.hlklib.lib.inject.ViewId;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.org.RelationRequest;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.common.InputableSearchViewHolder;
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

    public static void open(BaseFragment fragment, String groupId, String groupName, String title, boolean subordinate, ArrayList<String> groups, ArrayList<SubMember> members) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, groupId);
        bundle.putString(PARAM_NAME, groupName);
        bundle.putString(PARAM_JSON, title);
        bundle.putBoolean(PARAM_SQUAD_ID, subordinate);
        bundle.putStringArrayList(PARAM_SEARCHED, groups);
        bundle.putSerializable(PARAM_SELECTABLE, members);
        fragment.openActivity(GroupSubordinateSquadMemberPickerFragment.class.getName(), bundle, REQUEST_SELECT, true, false);
    }

    @ViewId(R.id.ui_holder_view_searchable_container)
    private View searchView;
    private InputableSearchViewHolder searchViewHolder;

    private String mGroupName, mTitle, searchingText = "", mGroupMemberSquadId;
    private ArrayList<String> selectedGroups;
    private ArrayList<Squad> squads = new ArrayList<>();
    private ArrayList<RelateGroup> groups = new ArrayList<>();
    private ArrayList<SubMember> selectedMembers;
    private PickerAdapter mAdapter;
    private boolean showSubordinate = true;

    @SuppressWarnings("unchecked")
    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mGroupName = bundle.getString(PARAM_NAME, "");
        mTitle = bundle.getString(PARAM_JSON, "");
        showSubordinate = bundle.getBoolean(PARAM_SQUAD_ID, true);
        selectedGroups = bundle.getStringArrayList(PARAM_SEARCHED);
        if (null == selectedGroups) {
            selectedGroups = new ArrayList<>();
        }
        selectedMembers = (ArrayList<SubMember>) bundle.getSerializable(PARAM_SELECTABLE);
        if (null == selectedMembers) {
            selectedMembers = new ArrayList<>();
        }
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_NAME, mGroupName);
        bundle.putBoolean(PARAM_SQUAD_ID, showSubordinate);
        bundle.putString(PARAM_JSON, mTitle);
        bundle.putStringArrayList(PARAM_SEARCHED, selectedGroups);
        bundle.putSerializable(PARAM_SELECTABLE, selectedMembers);
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
        return R.layout.fragment_searchable_list_swipe_enabled;
    }

    @Override
    protected void onSwipeRefreshing() {

    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    public void doingInResume() {
        if (null == searchViewHolder) {
            searchViewHolder = new InputableSearchViewHolder(searchView, this);
            searchViewHolder.setOnSearchingListener(onSearchingListener);
            // 初始化的时候由于还未加载成员列表或小组，不能输入进行搜索
            searchViewHolder.setInputEnabled(false);
        }
        if (null == mAdapter) {
            mAdapter = new PickerAdapter();
            mRecyclerView.setAdapter(mAdapter);
            OverScrollDecoratorHelper.setUpOverScroll(mRecyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
            RelateGroup group = new RelateGroup();
            group.setId(mQueryId);
            group.setGroupId(mQueryId);
            group.setGroupName(mGroupName + ("(<font color=\"#a1a1a1\">本组织</font>)"));
            groups.add(group);
            // 拉取下级组织列表
            if (showSubordinate) {
                loadSubordinates();
            } else {
                showGroups();
            }
        }
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    private InputableSearchViewHolder.OnSearchingListener onSearchingListener = new InputableSearchViewHolder.OnSearchingListener() {
        @Override
        public void onSearching(String text) {
            searchingText = text;
            mAdapter.clear();
            if (isEmpty(searchingText)) {
                showGroups();
            } else {
                searching();
            }
        }
    };

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

    private void searching() {
        // 显示所有符合条件的下级组织名称
        for (RelateGroup group : groups) {
            group.setLocalDeleted(group.isSelectable());
            if (group.getGroupId().equals(mQueryId)) {
                mAdapter.add(group);
            } else if (!isEmpty(group.getGroupName()) && group.getGroupName().contains(searchingText)) {
                mAdapter.add(group);
            }
        }
        // 显示所有符合条件的支部以及其下符合条件的成员
        int index = 1;
        for (Squad squad : squads) {
            squad.setLocalDeleted(squad.isSelectable());
            if (!isEmpty(squad.getName()) && squad.getName().contains(searchingText)) {
                // 如果小组名也在搜索范围内，则直接显示小组
                squad.setSelectable(false);
                mAdapter.add(squad);
            }
            for (Member member : squad.getGroSquMemberList()) {
                if (!isEmpty(member.getUserName()) && member.getUserName().contains(searchingText)) {
                    if (!squad.isSelectable()) {
                        squad.setSelectable(true);
                    }
                    mAdapter.update(squad);
                    mAdapter.add(member);
                }
            }
        }
    }

    private void loadSubordinates() {
        RelationRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<RelateGroup>() {
            @Override
            public void onResponse(List<RelateGroup> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success && null != list) {
                    groups.addAll(list);
                }
                showGroups();
            }
        }).list(mQueryId, RelateGroup.RelationType.SUBORDINATE);
    }

    private void showGroups() {
        for (RelateGroup group : groups) {
            // 恢复之前的展开、收缩状态
            group.setSelectable(group.isLocalDeleted());
            if (!group.getGroupId().equals(mQueryId) && selectedGroups.contains(group.getGroupId())) {
                group.setSelected(true);
            }
            mAdapter.add(group);
        }
        if (squads.size() > 0) {
            // 显示小组列表
            displaySquads();
        } else if (selectedMembers.size() > 0) {
            // 如果小组列表为空，则看看传入的成员列表是否有数据，有则直接拉取小组列表，否则等到点击之后再拉取
            fetchingRemoteSquads(mQueryId);
        }
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
        searchViewHolder.setInputEnabled(true);
    }

    private boolean isSquadMemberAllSelected(Squad squad) {
        for (Member member : squad.getGroSquMemberList()) {
            if (!member.isSelected()) {
                return false;
            }
        }
        return true;
    }

    private boolean isMemberExist(SubMember m, boolean isCheckSquad) {
        for (SubMember member : selectedMembers) {
            if (isCheckSquad) {
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
        checkSquadMemberSelectedStatus(squad.getId());
        //mAdapter.update(squad);

        // 检测是否有预先全选中
        Model model = mAdapter.get(mQueryId);
        model.setSelected(isTotalMemberSelected());
        mAdapter.update(model);
    }

    private void displaySquads() {
        Model group = mAdapter.get(mQueryId);
        int index = mAdapter.indexOf(group), cnt = 0;
        for (Squad squad : squads) {
            // 恢复之前的状态
            squad.setSelectable(squad.isLocalDeleted());
            cnt++;
            if (squad.getId().equals(mGroupMemberSquadId) && squad.getGroSquMemberList().size() <= 0) {
                // 第一次显示本组织的成员但还未拉取成员列表时，直接拉取成员列表
                squad.setRead(true);
                fetchingRemoteMembers(mOrganizationId, "");
            }
            mAdapter.add(squad, index + cnt);
            if (squad.isSelectable()) {
                // 同时恢复小组成员的展开状态
                for (Member member : squad.getGroSquMemberList()) {
                    cnt++;
                    mAdapter.add(member, index + cnt);
                }
            }
            if (!squad.getId().equals(mGroupMemberSquadId)) {
                checkSquadMemberSelectedStatus(squad.getId());
            }
        }
        // 检测是否所有成员都被选中了
        checkTotalMemberSelected();
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
        if (squad.isSelectable()) {
            // 显示小组成员
            if (squad.getGroSquMemberList().size() > 0) {
                int mIndex = 0;
                for (Member member : squad.getGroSquMemberList()) {
                    //boolean addable = true;
                    //if (!isEmpty(searchingText)) {
                    //    // 如果是在搜索则只显示搜索匹配的记录
                    //    addable = !isEmpty(member.getUserName()) && member.getUserName().contains(searchingText);
                    //}
                    //if (addable) {
                    mIndex += 1;
                    mAdapter.add(member, index + mIndex);
                    //}
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

    /**
     * 全选中某个小组的成员并显示
     */
    private void allSelectSquadMembers(Squad squad, boolean select) {
        squad.setSelected(select);
        squad.setCollapseStatus(squad.isSelected() ? squad.getGroSquMemberList().size() : 0);
        if (mAdapter.exist(squad)) {
            mAdapter.update(squad);
        }

        for (Member member : squad.getGroSquMemberList()) {
            member.setSelected(squad.isSelected());
            if (mAdapter.indexOf(member) >= 0) {
                mAdapter.update(member);
            }
        }
    }

    /**
     * 检测小组成员的选中情况并更新小组
     */
    private void checkSquadMemberSelectedStatus(String squadId) {
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

    private void checkTotalMemberSelected() {
        RelateGroup group = (RelateGroup) mAdapter.get(mQueryId);
        group.setSelected(isTotalMemberSelected());
        mAdapter.update(group);
    }

    /**
     * 是否所有成员都除以选中状态
     */
    private boolean isTotalMemberSelected() {
        for (Squad squad : squads) {
            if (!squad.isSelected()) {
                return false;
            }
            for (Member member : squad.getGroSquMemberList()) {
                if (!member.isSelected()) {
                    return false;
                }
            }
        }
        return true;
    }

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            Model model = mAdapter.get(index);
            switch (view.getId()) {
                case R.id.ui_holder_view_group_interest_root:
                case R.id.ui_holder_view_group_interest_select:
                    // 下级组织的选中
                    if (model.getId().equals(mQueryId)) {
                        if (!isEmpty(searchingText)) {
                            // 只有不处于搜索列表时才展开、收缩小组列表列表
                            return;
                        }
                        if (view.getId() == R.id.ui_holder_view_group_interest_select) {
                            // 如果是选择控件，则需要判断是全选还是展开列表
                            if (squads.size() > 0) {
                                model.setSelected(!model.isSelected());
                                // 需要全选组织内的所有小组和成员
                                for (Squad squad : squads) {
                                    allSelectSquadMembers(squad, model.isSelected());
                                }
                                mAdapter.update(model);
                                return;
                            }
                        }
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
                    Squad squad = (Squad) model;
                    if (!isEmpty(searchingText)) {
                        // 在搜索状态下，如果小组名称包含搜索的文字，则需要打开小组的成员列表；
                        // 反之，如果小组名称不包含所搜索的文字，则说明其下有成员被搜索出来了，则不需要展开、收起
                        if (squad.getName().contains(searchingText)) {
                            squad.setSelectable(!squad.isSelectable());
                            mAdapter.update(squad);
                            displaySquadMember(squad, index);
                        }
                        return;
                    }
                    // 显示或隐藏小组成员
                    squad.setSelectable(!squad.isSelectable());
                    squad.setLocalDeleted(squad.isSelectable());
                    mAdapter.update(squad);
                    displaySquadMember(squad, index);
                    break;
                case R.id.ui_holder_view_group_squad_picker:
                    if (!isEmpty(searchingText)) {
                        // 只有不处于搜索列表时才展开、收缩小组成员列表
                        return;
                    }
                    // 小组成员全选或取消全选
                    allSelectSquadMembers((Squad) model, !model.isSelected());
                    // 检测是否所有成员都被选中了
                    checkTotalMemberSelected();
                    break;
                case R.id.ui_holder_view_contact_layout:
                case R.id.ui_holder_view_contact_picker:
                    model.setSelected(!model.isSelected());
                    mAdapter.update(model);
                    // 组织成员或小组成员选择
                    Member member = (Member) model;
                    checkSquadMemberSelectedStatus(member.getSquadId());
                    // 检测是否所有成员都被选中了
                    checkTotalMemberSelected();
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
                ((ContactViewHolder) holder).showContent((Member) item, searchingText);
            } else if (holder instanceof SquadViewHolder) {
                ((SquadViewHolder) holder).showContent((Squad) item, searchingText);
            } else if (holder instanceof GroupInterestViewHolder) {
                ((GroupInterestViewHolder) holder).showContent((RelateGroup) item, searchingText);
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
