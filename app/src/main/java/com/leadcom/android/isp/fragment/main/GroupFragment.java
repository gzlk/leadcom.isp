package com.leadcom.android.isp.fragment.main;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hlk.hlklib.layoutmanager.CustomLinearLayoutManager;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.org.OrgRequest;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.organization.BaseOrganizationFragment;
import com.leadcom.android.isp.fragment.organization.CreateOrganizationFragment;
import com.leadcom.android.isp.holder.organization.GroupInterestViewHolder;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.organization.Organization;

import java.util.List;

/**
 * <b>功能描述：</b>首页 - 组织<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/15 09:52 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/03/15 09:52 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class GroupFragment extends BaseOrganizationFragment {

    private static boolean isFirst = true;
    @ViewId(R.id.ui_main_group_title_text)
    private TextView titleTextView;
    @ViewId(R.id.ui_main_group_title_allow)
    private CustomTextView titleAllow;
    @ViewId(R.id.ui_main_group_mine_background)
    private RelativeLayout groupsBkg;
    @ViewId(R.id.ui_main_group_mine_list_bg)
    private LinearLayout groupListBg;
    @ViewId(R.id.ui_main_group_mine_list)
    private RecyclerView groupList;

    private GroupAdapter gAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFirst = true;
    }

    @Override
    protected void onDelayRefreshComplete(int type) {

    }

    @Override
    public void doingInResume() {
        initializeGroupsAdapter();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    public int getLayout() {
        return R.layout.fragment_main_group;
    }

    @Override
    protected void onSwipeRefreshing() {
        // 拉取我已经加入的组织列表
        fetchingJoinedRemoteOrganizations(OrgRequest.GROUP_LIST_OPE_JOINED);
    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == REQUEST_CREATE) {
            // 组织创建成功，需要重新刷新组织列表
        }
        super.onActivityResult(requestCode, data);
    }

    @Click({R.id.ui_main_group_title_container, R.id.ui_main_group_mine_background,
            R.id.ui_main_group_create})
    private void viewClick(View view) {
        switch (view.getId()) {
            case R.id.ui_main_group_title_container:
                // 打开组织选择内容
                showGroupSelector(groupsBkg.getVisibility() == View.GONE);
                break;
            case R.id.ui_main_group_mine_background:
                showGroupSelector(false);
                break;
            case R.id.ui_main_group_create:
                view.startAnimation(App.clickAnimation());
                CreateOrganizationFragment.open(GroupFragment.this);
                break;
        }
    }

    private void initializeGroupsPosition() {
        Handler().post(new Runnable() {
            @Override
            public void run() {
                showGroupList(false, duration());
            }
        });
    }

    private void showGroupSelector(boolean shown) {
        titleAllow.animate()
                .rotation(shown ? -90 : 90)
                .setDuration(duration())
                .start();
        showGroupList(shown, duration());
    }

    private void showGroupList(final boolean shown, long duration) {
        groupsBkg.animate()
                .alpha(shown ? 1.0f : 0.0f)
                .setDuration(duration)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (shown) {
                            groupsBkg.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!shown) {
                            groupsBkg.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();
        groupListBg.animate()
                .alpha(shown ? 1.0f : 0.0f)
                .translationY(shown ? 0 : -groupListBg.getMeasuredHeight() * 1.1f)
                .setDuration(duration)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (shown) {
                            groupListBg.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!shown) {
                            groupListBg.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();
    }

    @Override
    protected void onFetchingJoinedRemoteOrganizationsComplete(List<Organization> list) {
        if (null != list) {
            for (Organization group : list) {
                group.setSelectable(true);
                gAdapter.update(group);
                Cache.cache().updateGroup(group);
            }
            if (isFirst) {
                isFirst = false;
                initializeGroupsPosition();
                // 初始化第一个组织
                if (gAdapter.getItemCount() > 0) {
                    onGroupChange(gAdapter.get(0));
                }
            }
        }
        displayNothing(gAdapter.getItemCount() <= 0);
        if (gAdapter.getItemCount() <= 0) {
            titleTextView.setText(null);
        }
    }

    private void initializeGroupsAdapter() {
        if (null == gAdapter) {
            setNothingText(R.string.ui_organization_structure_no_group_exist);
            gAdapter = new GroupAdapter();
            groupList.setLayoutManager(new CustomLinearLayoutManager(groupList.getContext()));
            groupList.setAdapter(gAdapter);
            fetchingJoinedRemoteOrganizations(OrgRequest.GROUP_LIST_OPE_JOINED);
        }
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            onGroupChange(gAdapter.get(index));
        }
    };

    private void onGroupChange(Organization group) {
        titleTextView.setText(group.getName());
        for (int i = 0, len = gAdapter.getItemCount(); i < len; i++) {
            Organization org = gAdapter.get(i);
            if (org.isSelected()) {
                if (!org.getId().equals(group.getId())) {
                    org.setSelected(false);
                    gAdapter.update(org);
                }
            } else if (org.getId().equals(group.getId())) {
                org.setSelected(true);
                gAdapter.update(org);
            }
        }
    }

    private class GroupAdapter extends RecyclerViewAdapter<GroupInterestViewHolder, Organization> {

        @Override
        public GroupInterestViewHolder onCreateViewHolder(View itemView, int viewType) {
            GroupInterestViewHolder holder = new GroupInterestViewHolder(itemView, GroupFragment.this);
            holder.addOnViewHolderClickListener(onViewHolderClickListener);
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
