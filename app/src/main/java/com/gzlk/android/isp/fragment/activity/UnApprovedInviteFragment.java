package com.gzlk.android.isp.fragment.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.org.InvitationRequest;
import com.gzlk.android.isp.fragment.organization.BaseOrganizationFragment;
import com.gzlk.android.isp.holder.activity.ActivityViewHolder;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.organization.Invitation;

import java.util.List;

/**
 * <b>功能描述：</b>组织内查询未参加的活动（包括暂不参加和未处理的）<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/04 12:55 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/04 12:55 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class UnApprovedInviteFragment extends BaseOrganizationFragment {

    public static UnApprovedInviteFragment newInstance(String params) {
        UnApprovedInviteFragment uhif = new UnApprovedInviteFragment();
        Bundle bundle = new Bundle();
        // 组织的id，这里要显示改组织内未处理的所有活动邀请
        bundle.putString(PARAM_QUERY_ID, params);
        uhif.setArguments(bundle);
        return uhif;
    }

    private InviteAdapter mAdapter;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_activity_un_handled_invite_fragment_title);
        initializeAdapter();
        setLoadingText(R.string.ui_activity_unhandled_invite_loading);
        fetchingUnhandled();
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
        fetchingUnhandled();
    }

    @Override
    protected void onLoadingMore() {
        fetchingUnhandled();
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    private void fetchingUnhandled() {
        displayLoading(true);
        displayNothing(false);
        fetchingUnApprovedActivityInvites();
    }

    // 加载我未参加的活动邀请列表
    // 改成未处理的活动邀请列表2017-07-05 16:37
    private void fetchingUnApprovedActivityInvites() {
        InvitationRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Invitation>() {
            @Override
            public void onResponse(List<Invitation> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (null != list) {
                    if (list.size() >= pageSize) {
                        remotePageNumber++;
                        isLoadingComplete(false);
                    } else {
                        isLoadingComplete(true);
                    }
                    mAdapter.update(list, true);
                } else {
                    isLoadingComplete(true);
                }
                stopRefreshing();
                displayLoading(false);
                displayNothing(mAdapter.getItemCount() < 1);
            }
        }).activityToBeHandled(mQueryId);
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            setNothingText(R.string.ui_activity_unhandled_invite_nothing);
            mAdapter = new InviteAdapter();
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            // 打开活动报名页面
            Invitation inv = mAdapter.get(index);
            openActivity(ActivityEntranceFragment.class.getName(), format("%s,%s", inv.getActId(), inv.getTid()), true, false);
        }
    };

    private class InviteAdapter extends RecyclerViewAdapter<ActivityViewHolder, Invitation> {

        @Override
        public ActivityViewHolder onCreateViewHolder(View itemView, int viewType) {
            ActivityViewHolder holder = new ActivityViewHolder(itemView, UnApprovedInviteFragment.this);
            holder.addOnViewHolderClickListener(onViewHolderClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_activity_home_item;
        }

        @Override
        public void onBindHolderOfView(ActivityViewHolder holder, int position, @Nullable Invitation item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(Invitation item1, Invitation item2) {
            return 0;
        }
    }
}
