package com.gzlk.android.isp.fragment.organization;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.org.SimpleOrgRequest;
import com.gzlk.android.isp.api.org.SimpleOutput;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.SimpleDialogHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.organization.GroupSquadContactViewHolder;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.listener.OnHandleBoundDataListener;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.organization.SimpleGroup;
import com.gzlk.android.isp.model.organization.SimpleMember;
import com.gzlk.android.isp.model.organization.SimpleSquad;
import com.gzlk.android.isp.model.organization.SubMember;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>组织、关注的组织、小组等联系人拾取器<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/07/14 09:32 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/07/14 09:32 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class GroupsContactPickerFragment extends BaseSwipeRefreshSupportFragment {

    public static GroupsContactPickerFragment newInstance(String params) {
        GroupsContactPickerFragment gcpf = new GroupsContactPickerFragment();
        Bundle bundle = new Bundle();
        // 传过来的组织id
        bundle.putString(PARAM_QUERY_ID, params);
        gcpf.setArguments(bundle);
        return gcpf;
    }

    public static void open(BaseFragment fragment, String groupId) {
        fragment.openActivity(GroupsContactPickerFragment.class.getName(), groupId, REQUEST_MEMBER, true, false);
    }

    private ContactPickerAdapter mAdapter;
    private ArrayList<Model> models = new ArrayList<>();

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
        int selected = 0;
        for (Model model : models) {
            if (model instanceof SimpleMember) {
                SimpleMember member = (SimpleMember) model;
                if (member.isSelected()) {
                    SubMember mbr = new SubMember();
                    mbr.setUserId(member.getUserId());
                    mbr.setUserName(member.getUserName());
                    if (!members.contains(mbr)) {
                        members.add(mbr);
                    }
                    selected++;
                }
            }
        }
        if (members.size() < 1) {
            warningNothingSelected();
        } else {
            //ToastHelper.make().showMsg(StringHelper.getString(R.string.ui_organization_contact_picker_resulted, selected, members.size()));
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

    private void fetchingAllMembers() {
        SimpleOrgRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<SimpleOutput>() {
            @Override
            public void onResponse(SimpleOutput simpleOutput, boolean success, String message) {
                super.onResponse(simpleOutput, success, message);
                if (success) {
                    restoreModels(simpleOutput);
                }
            }
        }).listAllMember(mQueryId);
    }

    private void restoreModels(SimpleOutput output) {
        models.clear();
        // 当前组织
        restoreGroup(output.getOwnGroup());
        // 上级组织
        restoreGroups(output.getSupGroup());
        // 友好组织
        restoreGroups(output.getFrdGroup());
        // 下级组织
        restoreGroups(output.getSubGroup());

        // 初次显示组织和小组列表
        restoreDefault();
    }

    private void restoreGroup(SimpleGroup group) {
        models.add(group);
        // 组织的成员
        restoreMembers(group.getMemberList(), group.getId());
        // 组织的小组列表
        restoreSquads(group.getSquadList(), group.getId());
    }

    private void restoreGroups(ArrayList<SimpleGroup> groups) {
        for (SimpleGroup group : groups) {
            restoreGroup(group);
        }
    }

    private void restoreSquads(ArrayList<SimpleSquad> squads, String groupId) {
        if (null != squads) {
            for (SimpleSquad squad : squads) {
                squad.setGroupId(groupId);
                // 小组
                models.add(squad);
                // 小组成员
                restoreMembers(squad.getMemberList(), squad.getId());
            }
        }
    }

    private void restoreMembers(ArrayList<SimpleMember> members, String supperId) {
        if (null != members) {
            for (SimpleMember member : members) {
                // 成员所属上级组织或小组的id
                member.setSupperId(supperId);
                models.add(member);
            }
        }
    }

    // 只列举组织、小组
    private void restoreDefault() {
        for (Model model : models) {
            if (model instanceof SimpleMember) {
                if (mAdapter.exist(model)) {
                    mAdapter.remove(model);
                }
            } else {
                mAdapter.update(model);
            }
        }
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new ContactPickerAdapter();
            mRecyclerView.setAdapter(mAdapter);
            fetchingAllMembers();
        }
    }

    private void changeMembers(String supperId, boolean forAdd, int baseIndex) {
        int index = 1;
        for (Model model : models) {
            if (model instanceof SimpleMember) {
                SimpleMember member = (SimpleMember) model;
                if (member.getSupperId().equals(supperId)) {
                    if (forAdd) {
                        mAdapter.add(model, baseIndex + index);
                        index++;
                    } else {
                        mAdapter.remove(model);
                    }
                }
            }
        }
    }

    private void selectAll(String supperId, boolean all) {
        for (Model model : models) {
            if (model instanceof SimpleMember) {
                SimpleMember member = (SimpleMember) model;
                if (member.getSupperId().equals(supperId)) {
                    member.setSelected(all);
                    if (mAdapter.exist(member)) {
                        mAdapter.update(member);
                    }
                }
            }
        }
    }

    private void checkSelectAllStatus(String supperId) {
        Model supper = null;
        int[] select = new int[2];
        select[0] = 0;
        select[1] = 0;
        for (Model model : models) {
            if (model.getId().equals(supperId)) {
                supper = model;
            }
            if (model instanceof SimpleMember) {
                SimpleMember member = (SimpleMember) model;
                if (member.getSupperId().equals(supperId)) {
                    select[0]++;
                    select[1] += member.isSelected() ? 1 : 0;
                }
            }
        }
        if (null != supper) {
            supper.setSelectable(select[0] == select[1]);
            mAdapter.update(supper);
        }
    }

    // 展开小组或组织的成员列表
    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Model model = mAdapter.get(index);
            model.setSelected(!model.isSelected());
            mAdapter.notifyItemChanged(index);
            if ((model instanceof SimpleGroup) || (model instanceof SimpleSquad)) {
                // 展开或收缩组织、小组成员列表
                changeMembers(model.getId(), model.isSelected(), index);
            } else if (model instanceof SimpleMember) {
                // 选中或取消选中之后，设置其上级机构的全选状态
                checkSelectAllStatus(((SimpleMember) model).getSupperId());
            }
        }
    };

    // 全选选中的组织或小组成员
    private OnHandleBoundDataListener<Model> onHandlerBoundDataListener = new OnHandleBoundDataListener<Model>() {
        @Override
        public Model onHandlerBoundData(BaseViewHolder holder) {
            // 全选或取消全选
            int index = holder.getAdapterPosition();
            Model model = mAdapter.get(index);
            if (!(model instanceof SimpleMember)) {
                // 不是成员的话，则全选或取消全选这个上级的所有成员
                model.setSelectable(!model.isSelectable());
                mAdapter.notifyItemChanged(index);
                selectAll(model.getId(), model.isSelectable());
            }
            return null;
        }
    };

    private class ContactPickerAdapter extends RecyclerViewAdapter<GroupSquadContactViewHolder, Model> {
        @Override
        public GroupSquadContactViewHolder onCreateViewHolder(View itemView, int viewType) {
            GroupSquadContactViewHolder holder = new GroupSquadContactViewHolder(itemView, GroupsContactPickerFragment.this);
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
            if (item instanceof SimpleGroup) {
                holder.showContent((SimpleGroup) item);
            } else if (item instanceof SimpleSquad) {
                holder.showContent((SimpleSquad) item);
            } else if (item instanceof SimpleMember) {
                holder.showContent((SimpleMember) item);
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
