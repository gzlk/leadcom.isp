package com.leadcom.android.isp.fragment.organization;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.archive.ArchiveRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.PreferenceHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.common.ExpandCollapseViewHolder;
import com.leadcom.android.isp.holder.organization.ActivityMemberItemViewHolder;
import com.leadcom.android.isp.holder.organization.ActivityMemberUserViewHolder;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.organization.ActSquad;
import com.leadcom.android.isp.model.organization.Concern;
import com.leadcom.android.isp.model.organization.Member;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>活动报名详情列表页<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/09/22 22:10 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ActivityCollectionDetailsFragment extends GroupBaseFragment {

    public static ActivityCollectionDetailsFragment newInstance(Bundle bundle) {
        ActivityCollectionDetailsFragment amcdf = new ActivityCollectionDetailsFragment();
        amcdf.setArguments(bundle);
        return amcdf;
    }

    static Bundle getBundle(String groupId, String activityId, int type) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, groupId);
        bundle.putString(PARAM_NAME, activityId);
        bundle.putInt(PARAM_JSON, type);
        return bundle;
    }

    public static void open(BaseFragment fragment, String groupId, Archive activity, boolean checkingGroup) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, groupId);
        bundle.putSerializable(PARAM_JSON, activity);
        bundle.putBoolean(PARAM_NAME, checkingGroup);
        fragment.openActivity(ActivityCollectionDetailsFragment.class.getName(), bundle, true, false);
    }

    private Archive mActivity, dActivity;
    private MemberAdapter mAdapter;
    private boolean isCheckingGroups;
    private String mineGroupId;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mActivity = (Archive) bundle.getSerializable(PARAM_JSON);
        isCheckingGroups = bundle.getBoolean(PARAM_NAME, false);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putSerializable(PARAM_JSON, mActivity);
        bundle.putBoolean(PARAM_NAME, isCheckingGroups);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setCustomTitle(isCheckingGroups ? "组织报名统计" : "报名详情");
        isLoadingComplete(true);
        mineGroupId = PreferenceHelper.get(Cache.get(R.string.pf_last_login_user_group_current, R.string.pf_last_login_user_group_current_beta), "");
    }

    @Override
    protected void onSwipeRefreshing() {
        if (null != mAdapter) {
            mAdapter.clear();
        }
        loadingData();
    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    public void doingInResume() {
        initializeAdapter();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    private void loadingData() {
        if (isCheckingGroups) {
            loadingGroupsMembers();
        } else {
            loadingGroupMembers();
        }
    }

    /**
     * 拉取所有组织的报名统计列表
     */
    private void loadingGroupsMembers() {
        setLoadingText(R.string.ui_group_activity_collection_group_members_loading);
        setNothingText(R.string.ui_group_activity_collection_group_members_nothing);
        displayLoading(true);
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success && null != archive) {
                    archive.setCountResult(archive.getTotalResult());
                    mAdapter.add(archive);
                    if (isCheckingGroups) {
                        // 加载组织报名统计结果
                        for (Concern group : archive.getMemberGroVoList()) {
                            if (isEmpty(group.getId())) {
                                group.setId(group.getGroupId());
                            }
                            if (group.getId().equals(mQueryId)) {
                                group.setSelected(true);
                            }
                            mAdapter.add(group);
                        }
                    }
                }
                displayLoading(false);
                displayNothing(mAdapter.getItemCount() <= 1);
                stopRefreshing();
            }
        }).selectActivityGroups(mQueryId, mActivity.getGroActivityId());
    }

    private static final String LOADING = "_load_more_members";

    // 拉取指定组织内的报名详细列表
    private void loadingGroupMembers() {
        setLoadingText(R.string.ui_group_activity_collection_group_members_loading);
        setNothingText(R.string.ui_group_activity_collection_group_members_nothing);
        displayLoading(true);
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success && null != archive) {
                    if (!isEmpty(archive.getTotalResult())) {
                        archive.setCountResult(archive.getTotalResult());
                    }
                    if (!isEmpty(archive.getGroupMemberTotalResult())) {
                        archive.setCountResult(archive.getGroupMemberTotalResult());
                    }
                    dActivity = archive;
                    mAdapter.add(archive);
                    int count;
                    if (archive.getGroSquadList().size() > 0) {
                        for (ActSquad squad : archive.getGroSquadList()) {
                            mAdapter.add(squad);
                            count = 0;
                            for (Member member : squad.getGroActivityMemberList()) {
                                member.setSquadId(squad.getId());
                                mAdapter.add(member);
                                count++;
                                if (count >= PAGE_SIZE && squad.getGroActivityMemberList().size() > PAGE_SIZE) {
                                    Model model = new Model();
                                    model.setId(LOADING + squad.getSquadId());
                                    model.setCollapseStatus(count);
                                    mAdapter.add(model);
                                    break;
                                }
                            }
                        }
                    }
                    if (archive.getDtoList().size() > 0) {
                        count = 0;
                        for (Member member : archive.getDtoList()) {
                            mAdapter.add(member);
                            count++;
                            if (count >= PAGE_SIZE && archive.getDtoList().size() > PAGE_SIZE) {
                                Model model = new Model();
                                model.setId(LOADING);
                                model.setCollapseStatus(count);
                                mAdapter.add(model);
                                break;
                            }
                        }
                    }
                }
                displayLoading(false);
                displayNothing(mAdapter.getItemCount() <= 1);
                stopRefreshing();
            }
        }).selectActivityGroupMember(mQueryId, mineGroupId, mActivity.getGroActivityId());
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new MemberAdapter();
            mRecyclerView.setAdapter(mAdapter);
            loadingData();
        }
    }

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            Model model = mAdapter.get(index);
            switch (view.getId()) {
                case R.id.ui_holder_view_activity_member_item_layout:
                    if (model instanceof Concern) {
                        // 打开下级组织的的报名情况列表
                        Concern group = (Concern) model;
                        ActivityCollectionDetailsFragment.open(ActivityCollectionDetailsFragment.this, group.getGroupId(), mActivity, false);
                    }
                    break;
                case R.id.ui_holder_view_expand_collapse_clicker:
                    // 展开更多
                    if (dActivity.getDtoList().size() > 0) {
                        expandMembers(dActivity.getDtoList(), model, index);
                    } else if (dActivity.getGroSquadList().size() > 0) {
                        String squadId = model.getId().replace(LOADING, "");
                        for (ActSquad squad : dActivity.getGroSquadList()) {
                            if (squad.getSquadId().equals(squadId)) {
                                expandMembers(squad.getGroActivityMemberList(), model, index);
                            }
                        }
                    }
                    break;
            }
        }
    };

    private void expandMembers(ArrayList<Member> members, Model model, int modelIndex) {
        int index = 0;
        int size = members.size();
        for (int i = model.getCollapseStatus(); i < size; i++) {
            mAdapter.add(members.get(i), index + modelIndex);
            index++;
            if (index >= PAGE_SIZE) {
                // 超出一页，再等待下一次列取
                model.setCollapseStatus(index + model.getCollapseStatus());
                break;
            }
        }
        if (index < PAGE_SIZE) {
            // 一页未满但已经没有了
            //model.setCollapseStatus(index + model.getCollapseStatus());
            //model.setSelected(true);
            mAdapter.remove(model);
        }
    }

    private class MemberAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {
        private static final int TYPE_MORE = 0, TYPE_MEMBER = 1, TYPE_USER = 2;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            if (viewType == TYPE_MORE) {
                ExpandCollapseViewHolder ecvh = new ExpandCollapseViewHolder(itemView, ActivityCollectionDetailsFragment.this);
                ecvh.setOnViewHolderElementClickListener(elementClickListener);
                return ecvh;
            } else if (viewType == TYPE_USER) {
                return new ActivityMemberUserViewHolder(itemView, ActivityCollectionDetailsFragment.this);
            }
            ActivityMemberItemViewHolder holder = new ActivityMemberItemViewHolder(itemView, ActivityCollectionDetailsFragment.this);
            holder.setOnViewHolderElementClickListener(elementClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            switch (viewType) {
                case TYPE_MORE:
                    return R.layout.holder_view_expand_collapse;
                case TYPE_MEMBER:
                    return R.layout.holder_view_activity_member_item;
                case TYPE_USER:
                    return R.layout.holder_view_activity_member_user;
            }
            return 0;
        }

        @Override
        public int getItemViewType(int position) {
            Model model = get(position);
            if (model instanceof Member) {
                Member member = (Member) model;
                return !isEmpty(member.getGroupId()) ? TYPE_MEMBER : TYPE_USER;
            }
            return (!isEmpty(model.getId()) && model.getId().contains(LOADING)) ? TYPE_MORE : TYPE_MEMBER;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof ActivityMemberUserViewHolder) {
                ((ActivityMemberUserViewHolder) holder).showContent((Member) item);
            } else if (holder instanceof ActivityMemberItemViewHolder) {
                ((ActivityMemberItemViewHolder) holder).showContent(item);
            } else if (holder instanceof ExpandCollapseViewHolder) {
                ((ExpandCollapseViewHolder) holder).showContent(item);
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
