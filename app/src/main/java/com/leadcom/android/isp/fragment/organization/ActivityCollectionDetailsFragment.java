package com.leadcom.android.isp.fragment.organization;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.archive.ArchiveRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.organization.ActivityMemberItemViewHolder;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.organization.ActSquad;
import com.leadcom.android.isp.model.organization.Concern;
import com.leadcom.android.isp.model.organization.Member;

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

    private Archive mActivity;
    private MemberAdapter mAdapter;
    private boolean isCheckingGroups;

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
        return false;
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
                    mAdapter.add(archive);
                    for (ActSquad squad : archive.getGroSquadList()) {
                        mAdapter.add(squad);
                        for (Member member : squad.getGroActivityMemberList()) {
                            mAdapter.add(member);
                        }
                    }
                }
                displayLoading(false);
                displayNothing(mAdapter.getItemCount() <= 1);
                stopRefreshing();
            }
        }).selectActivityGroupMember(mActivity.getGroupId(), mQueryId, mActivity.getGroActivityId());
    }
//    private void loadingSubordinateMembers() {
//        setLoadingText(R.string.ui_group_activity_collection_subordinate_members_loading);
//        setNothingText(R.string.ui_group_activity_collection_subordinate_members_nothing);
//        displayNothing(false);
//        displayLoading(true);
//        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
//            @Override
//            public void onResponse(Archive archive, boolean success, String message) {
//                super.onResponse(archive, success, message);
//                if (success && null != archive) {
//                    mAdapter.add(archive);
//                    for (Member member : archive.getGroActivityReplyList()) {
//                        mAdapter.add(member);
//                    }
//                }
//                displayLoading(false);
//                displayNothing(mAdapter.getItemCount() <= 1);
//                stopRefreshing();
//            }
//        }).listActivitySubordinateMember(mQueryId, mActivityId);
//    }

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
            if (model instanceof Concern) {
                Concern group = (Concern) model;
                ActivityCollectionDetailsFragment.open(ActivityCollectionDetailsFragment.this, group.getGroupId(), mActivity, false);
            }
//            if (model instanceof Member) {
//                Member member = (Member) model;
//                if (!isEmpty(member.getGroupId())) {
//                    // 查看下属组织的回复详情
//                    ActivityReplyFragment.open(ActivityCollectionDetailsFragment.this, member.getId(), mActivity.getGroActivityId());
//                }
//            }
        }
    };

    private class MemberAdapter extends RecyclerViewAdapter<ActivityMemberItemViewHolder, Model> {
        @Override
        public ActivityMemberItemViewHolder onCreateViewHolder(View itemView, int viewType) {
            ActivityMemberItemViewHolder holder = new ActivityMemberItemViewHolder(itemView, ActivityCollectionDetailsFragment.this);
            holder.setOnViewHolderElementClickListener(elementClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_activity_member_item;
        }

        @Override
        public void onBindHolderOfView(ActivityMemberItemViewHolder holder, int position, @Nullable Model item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
