package com.leadcom.android.isp.fragment.organization;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.org.OrgRequest;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.organization.GroupInterestViewHolder;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.organization.Organization;
import com.leadcom.android.isp.model.organization.RelateGroup;

import java.util.List;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * <b>功能描述：</b>组织拾取器<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/28 12:56 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/03/28 12:56 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class GroupPickerFragment extends BaseOrganizationFragment {

    public static GroupPickerFragment newInstance(String params) {
        GroupPickerFragment gpf = new GroupPickerFragment();
        Bundle bundle = new Bundle();
        // 传过来的以选中的组织id
        bundle.putString(PARAM_SQUAD_ID, params);
        gpf.setArguments(bundle);
        return gpf;
    }

    public static void open(BaseFragment fragment, String selectedGroupId) {
        selected = -1;
        fragment.openActivity(GroupPickerFragment.class.getName(), selectedGroupId, REQUEST_GROUP, true, false);
    }

    private static int selected = -1;
    private GroupAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        enableSwipe(false);
        isLoadingComplete(true);
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

    @Override
    protected void onDelayRefreshComplete(int type) {

    }

    @Override
    public void doingInResume() {
        initializeAdapter();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            setCustomTitle("选择组织");
            setRightText(R.string.ui_base_text_confirm);
            setRightTitleClickListener(new OnTitleButtonClickListener() {
                @Override
                public void onClick() {
                    if (selected >= 0) {
                        Organization org = mAdapter.get(selected);
                        RelateGroup group = new RelateGroup(org);
                        resultData(RelateGroup.toJson(group));
                    }
                }
            });
            mAdapter = new GroupAdapter();
            mRecyclerView.setAdapter(mAdapter);
            OverScrollDecoratorHelper.setUpOverScroll(mRecyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
            fetchingJoinedRemoteOrganizations(OrgRequest.GROUP_LIST_OPE_JOINED);
        }
    }

    @Override
    protected void onFetchingJoinedRemoteOrganizationsComplete(List<Organization> list) {
        if (null != list) {
            for (Organization group : list) {
                if (group.getId().equals(mQueryId)) {
                    group.setSelected(true);
                    selected = mAdapter.getItemCount();
                }
                mAdapter.update(group);
            }
        }
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Organization group = mAdapter.get(index);
            group.setSelected(!group.isSelected());
            if (group.isSelected()) {
                selected = index;
            }
            mAdapter.update(group);
            for (int i = 0, len = mAdapter.getItemCount(); i < len; i++) {
                Organization org = mAdapter.get(i);
                if (!org.getId().equals(group.getId())) {
                    if (org.isSelected()) {
                        org.setSelected(false);
                        mAdapter.update(org);
                    }
                }
            }
        }
    };

    private class GroupAdapter extends RecyclerViewAdapter<GroupInterestViewHolder, Organization> {

        @Override
        public GroupInterestViewHolder onCreateViewHolder(View itemView, int viewType) {
            GroupInterestViewHolder holder = new GroupInterestViewHolder(itemView, GroupPickerFragment.this);
            holder.addOnViewHolderClickListener(onViewHolderClickListener);
            holder.setSelectable(true);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_group_interesting_item;
        }

        @Override
        public void onBindHolderOfView(GroupInterestViewHolder holder, int position, @Nullable Organization item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(Organization item1, Organization item2) {
            return 0;
        }

    }
}