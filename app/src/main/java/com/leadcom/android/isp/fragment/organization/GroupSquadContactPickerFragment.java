package com.leadcom.android.isp.fragment.organization;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.helper.popup.SimpleDialogHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.organization.GroupSquadContactViewHolder;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.listener.OnHandleBoundDataListener;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.organization.Member;
import com.leadcom.android.isp.model.organization.Organization;
import com.leadcom.android.isp.model.organization.Squad;
import com.leadcom.android.isp.model.organization.SubMember;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能描述：</b>组织、小组联系人拾取器<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/27 00:36 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/27 00:36 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class GroupSquadContactPickerFragment extends GroupBaseFragment {

    private static final String PARAM_USERS = "gscpf_members";
    private static final String PARAM_FORCE_LOCK = "gscpf_force_to_lock";
    private static final String PARAM_SELECTED = "gscpf_selected";

    public static GroupSquadContactPickerFragment newInstance(Bundle bundle) {
        GroupSquadContactPickerFragment gscpf = new GroupSquadContactPickerFragment();
//        String[] strings = splitParameters(params);
//        Bundle bundle = new Bundle();
//        // 组织的id
//        bundle.putString(PARAM_QUERY_ID, strings[0]);
//        // 是否锁定传过来的已存在项目
//        bundle.putBoolean(PARAM_FORCE_LOCK, Boolean.valueOf(strings[1]));
//        // 已选中的成员列表
//        bundle.putString(PARAM_USERS, replaceJson(strings[2], true));
        gscpf.setArguments(bundle);
        return gscpf;
    }

    private static Bundle getBundle(String groupId, String groupName, boolean forceLock, String exists) {
        Bundle bundle = new Bundle();
        // 组织的id
        bundle.putString(PARAM_QUERY_ID, groupId);
        // 是否锁定传过来的已存在项目
        bundle.putBoolean(PARAM_FORCE_LOCK, forceLock);
        // 已选中的成员列表
        bundle.putString(PARAM_USERS, replaceJson(exists, true));
        return bundle;
    }

    public static void open(BaseFragment fragment, String groupId, String groupName, String exists) {
        Bundle bundle = getBundle(groupId, "", false, exists);
        fragment.openActivity(GroupSquadContactPickerFragment.class.getName(), bundle, REQUEST_SELECT, true, false);
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        isLockable = bundle.getBoolean(PARAM_FORCE_LOCK, false);
        mSelected = bundle.getInt(PARAM_SELECTED, -1);
        String json = bundle.getString(PARAM_USERS, "[]");
        existsUsers = Json.gson().fromJson(json, new TypeToken<ArrayList<SubMember>>() {
        }.getType());
        if (null == existsUsers) {
            existsUsers = new ArrayList<>();
        }
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_SELECTED, mSelected);
        bundle.putBoolean(PARAM_FORCE_LOCK, isLockable);
        bundle.putString(PARAM_USERS, Json.gson().toJson(existsUsers));
    }

    private boolean isLockable = false;
    private int mSelected = -1;
    private GroupSquadContactAdapter mAdapter;
    /**
     * 已选中的用户列表
     */
    private ArrayList<SubMember> existsUsers;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        enableSwipe(false);
        setSupportLoadingMore(false);
        setCustomTitle(R.string.ui_organization_contact_picker_fragment_title);
        setRightText(R.string.ui_base_text_finish);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                resultMembers();
            }
        });
        initializeAdapter();
    }

    private void resultMembers() {
        // 返回选中的用户id和姓名
        ArrayList<SubMember> members = new ArrayList<>();
        for (int i = 0, len = mAdapter.getItemCount(); i < len; i++) {
            Model model = mAdapter.get(i);
            if (model instanceof Member) {
                Member member = (Member) model;
                if (member.isSelected()) {
                    SubMember mbr = new SubMember();
                    mbr.setUserId(member.getUserId());
                    mbr.setUserName(member.getUserName());
                    members.add(mbr);
                }
            }
        }
        if (members.size() < 1) {
            warningNothingSelected();
        } else {
            resultData(Json.gson().toJson(members, new TypeToken<ArrayList<SubMember>>() {
            }.getType()));
        }
    }

    private void warningNothingSelected() {
        SimpleDialogHelper.init(Activity()).show(R.string.ui_organization_contact_picker_nothing_picked, R.string.ui_base_text_yes, R.string.ui_base_text_cancel, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                finish();
                return true;
            }
        }, null);
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
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

    private void loadGroup() {
        Organization org = new Dao<>(Organization.class).query(mQueryId);
        if (!mAdapter.exist(org)) {
            mAdapter.add(org, 0);
        }
        // 拉取远程我加入的小组列表
        setLoadingText(R.string.ui_organization_squad_contact_loading_squads);
        displayLoading(true);
        fetchingRemoteSquads(mQueryId);
    }

    @Override
    protected void onFetchingRemoteSquadsComplete(List<Squad> list) {
        displayLoading(false);
        if (null != list && list.size() > 0) {
            for (Squad squad : list) {
                mAdapter.add(squad);
            }
        } else {
            // 组织下没有任何小组，则直接加载组织内的成员列表
            mAdapter.get(0).setSelected(true);
            mSelected = 0;
            mAdapter.notifyItemChanged(0);
            fetchingMembers(mQueryId, "");
        }
    }

    private void fetchingMembers(String groupId, String squadId) {
        if (isExistOne(groupId, squadId)) {
            // 存在记录则不需要再拉取了
            resetMembers();
        } else {
            setLoadingText(R.string.ui_organization_contact_loading_text);
            displayLoading(true);
            fetchingRemoteMembers(groupId, squadId);
        }
    }

    private boolean isExistOne(String groupId, String squadId) {
        for (Member member : members) {
            if (!isEmpty(squadId)) {
                // 查询小组成员
                if (!isEmpty(member.getSquadId()) && member.getSquadId().equals(squadId)) {
                    return true;
                }
            } else {
                if (member.getGroupId().equals(groupId) && isEmpty(member.getSquadId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onFetchingRemoteMembersComplete(List<Member> list) {
        displayLoading(false);
        if (null != list) {
            members.addAll(list);
            resetMembers();
        }
    }

    private void resetMembers() {
        if (mSelected >= 0) {
            Model model = mAdapter.get(mSelected);
            String orgId = "", squadId = "";
            if (model instanceof Organization) {
                orgId = model.getId();
                squadId = "";
            } else if (model instanceof Squad) {
                Squad squad = (Squad) model;
                orgId = squad.getGroupId();
                squadId = squad.getId();
            }

            // 成员列表直接插在点击item后面
            int index = mSelected + 1;
            for (Member member : members) {
                if (!isEmpty(squadId)) {
                    // 小组成员
                    if (!isEmpty(member.getSquadId()) && member.getSquadId().equals(squadId)) {
                        int pos = mAdapter.indexOf(member);
                        if (pos >= 0) {
                            mAdapter.update(member);
                        } else {
                            mAdapter.add(member, index);
                            index++;
                        }
                    }
                } else {
                    // 组织成员
                    if (member.getGroupId().equals(orgId) && isEmpty(member.getSquadId())) {
                        int pos = mAdapter.indexOf(member);
                        if (pos >= 0) {
                            mAdapter.update(member);
                        } else {
                            mAdapter.add(member, index);
                            index++;
                        }
                    }
                }
            }
        }
    }

    private void removeMember(String groupId, String squadId) {
        int index = 0;
        int size = mAdapter.getItemCount();
        while (index < size) {
            Model model = mAdapter.get(index);
            if (model instanceof Member) {
                Member member = (Member) model;
                if (!isEmpty(squadId)) {
                    // 移除小组成员
                    if (!isEmpty(member.getSquadId()) && member.getSquadId().equals(squadId)) {
                        mAdapter.remove(model);
                        size = mAdapter.getItemCount();
                        continue;
                    }
                } else {
                    // 移除组织成员
                    if (member.getGroupId().equals(groupId) && isEmpty(member.getSquadId())) {
                        mAdapter.remove(model);
                        size = mAdapter.getItemCount();
                        continue;
                    }
                }
            }
            index++;
        }
    }

    private ArrayList<Member> members = new ArrayList<>();

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Model model = mAdapter.get(index);
            model.setSelected(!model.isSelected());
            if (model instanceof Organization) {
                mSelected = index;
                mAdapter.notifyItemChanged(index);
                if (model.isSelected()) {
                    fetchingMembers(model.getId(), "");
                } else {
                    // 移除组织成员
                    removeMember(model.getId(), "");
                }
            } else if (model instanceof Squad) {
                mSelected = index;
                mAdapter.notifyItemChanged(index);
                Squad squad = (Squad) model;
                if (model.isSelected()) {
                    fetchingMembers(squad.getGroupId(), squad.getId());
                } else {
                    removeMember(squad.getGroupId(), squad.getId());
                }
            } else if (model instanceof Member) {
                mAdapter.notifyItemChanged(index);
            }
        }
    };

    private void resetSelectAll(String groupId, String squadId, boolean selectAll) {
        for (int i = 0, size = mAdapter.getItemCount(); i < size; i++) {
            Model model = mAdapter.get(i);
            if (model instanceof Member) {
                Member member = (Member) model;
                if (!isEmpty(squadId)) {
                    if (!isEmpty(member.getSquadId()) && member.getSquadId().equals(squadId)) {
                        member.setSelected(selectAll);
                        mAdapter.notifyItemChanged(i);
                    }
                } else {
                    if (member.getGroupId().equals(groupId) && isEmpty(member.getSquadId())) {
                        member.setSelected(selectAll);
                        mAdapter.notifyItemChanged(i);
                    }
                }
            }
        }
    }

    private boolean isExistsInAdapter(String groupId, String squadId) {
        for (int i = 0, size = mAdapter.getItemCount(); i < size; i++) {
            Model model = mAdapter.get(i);
            if (model instanceof Member) {
                Member member = (Member) model;
                if (!isEmpty(squadId)) {
                    if (!isEmpty(member.getSquadId()) && member.getSquadId().equals(squadId)) {
                        return true;
                    }
                } else {
                    if (member.getGroupId().equals(groupId) && isEmpty(member.getSquadId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // 全选
    private OnHandleBoundDataListener<Model> onHandlerBoundDataListener = new OnHandleBoundDataListener<Model>() {
        @Override
        public Model onHandlerBoundData(BaseViewHolder holder) {
            int position = holder.getAdapterPosition();
            Model model = mAdapter.get(position);
            if (model instanceof Organization) {
                // adapter里有当前组织成员时说明是展开状态，此时可以全选
                if (isExistsInAdapter(model.getId(), "")) {
                    model.setSelectable(!model.isSelectable());
                    mAdapter.notifyItemChanged(position);
                    // 全选或取消全选组织的成员
                    resetSelectAll(model.getId(), "", model.isSelectable());
                }
            } else if (model instanceof Squad) {
                Squad squad = (Squad) model;
                // adapter里有当前小组成员时说明小组是展开状态，此时可以全选
                if (isExistsInAdapter(squad.getGroupId(), squad.getId())) {
                    model.setSelectable(!model.isSelectable());
                    mAdapter.notifyItemChanged(position);
                    // 全选或取消全选小组的成员
                    resetSelectAll(squad.getGroupId(), squad.getId(), squad.isSelectable());
                }
            }
            return null;
        }
    };

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new GroupSquadContactAdapter();
            mRecyclerView.setAdapter(mAdapter);
            // 加载本地组织信息
            loadGroup();
        }
    }

    private class GroupSquadContactAdapter extends RecyclerViewAdapter<GroupSquadContactViewHolder, Model> {

        @Override
        public GroupSquadContactViewHolder onCreateViewHolder(View itemView, int viewType) {
            GroupSquadContactViewHolder holder = new GroupSquadContactViewHolder(itemView, GroupSquadContactPickerFragment.this);
            // 点击展开成员列表或选中成员
            holder.addOnViewHolderClickListener(onViewHolderClickListener);
            // 全选成员
            holder.addOnHandlerBoundDataListener(onHandlerBoundDataListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_group_squad_contact_item;
        }

        @Override
        public void onBindHolderOfView(GroupSquadContactViewHolder holder, int position, @Nullable Model item) {
            if (item instanceof Organization) {
                holder.showContent((Organization) item);
            } else if (item instanceof Squad) {
                holder.showContent((Squad) item);
            } else if (item instanceof Member) {
                holder.showContent((Member) item);
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
