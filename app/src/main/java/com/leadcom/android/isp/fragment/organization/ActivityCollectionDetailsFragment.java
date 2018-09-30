package com.leadcom.android.isp.fragment.organization;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.archive.ArchiveRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.holder.organization.ActivityMemberItemViewHolder;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.organization.ActSquad;
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

    public static Bundle getBundle(String groupId, String activityId, int type) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, groupId);
        bundle.putString(PARAM_NAME, activityId);
        bundle.putInt(PARAM_JSON, type);
        return bundle;
    }

    private String mActivityId;
    private int mType;
    private MemberAdapter mAdapter;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mActivityId = bundle.getString(PARAM_NAME, "");
        mType = bundle.getInt(PARAM_JSON, Member.Type.GROUP);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_NAME, mActivityId);
        bundle.putInt(PARAM_JSON, mType);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
        switch (mType) {
            case Member.Type.GROUP:
                loadingGroupMembers();
                break;
            case Member.Type.ACTIVITY:
                loadingSubordinateMembers();
                break;
        }
    }

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
                        if(squad.getSquadId().equals("0")){
                            squad.setSquadName("本组织成员");
                        }
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
        }).listActivityGroupMember(mQueryId, mActivityId);
    }

    private void loadingSubordinateMembers() {
        setLoadingText(R.string.ui_group_activity_collection_subordinate_members_loading);
        setNothingText(R.string.ui_group_activity_collection_subordinate_members_nothing);
        displayNothing(false);
        displayLoading(true);
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success && null != archive) {
                    mAdapter.add(archive);
                    for (Member member : archive.getGroActivityReplyList()) {
                        mAdapter.add(member);
                    }
                }
                displayLoading(false);
                displayNothing(mAdapter.getItemCount() <= 1);
                stopRefreshing();
            }
        }).listActivitySubordinateMember(mQueryId, mActivityId);
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
            if (model instanceof Member) {
                Member member = (Member) model;
                if (!isEmpty(member.getGroupId())) {
                    // 查看下属组织的回复详情
                    ActivityReplyFragment.open(ActivityCollectionDetailsFragment.this, member.getId());
                }
            }
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
