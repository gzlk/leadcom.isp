package com.leadcom.android.isp.fragment.organization;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.org.OrgRequest;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.holder.organization.GroupInterestViewHolder;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.organization.Organization;
import com.hlk.hlklib.lib.view.CustomTextView;

import java.util.List;

/**
 * <b>功能描述：</b>感兴趣的组织<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/07/08 21:32 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/07/08 21:32 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class InterestingOrganizationFragment extends BaseSwipeRefreshSupportFragment {

    public static InterestingOrganizationFragment newInstance(String params) {
        InterestingOrganizationFragment iof = new InterestingOrganizationFragment();
        Bundle bundle = new Bundle();
        // 传过来的组织id
        bundle.putString(PARAM_QUERY_ID, params);
        iof.setArguments(bundle);
        return iof;
    }

    public static void open(BaseFragment fragment, String groupId, int req) {
        fragment.openActivity(InterestingOrganizationFragment.class.getName(), groupId, req, true, false);
    }

    private InterestAdapter mAdapter;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

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

    @Override
    public int getLayout() {
        return R.layout.fragment_top_paddingable_swipe_recycler_view;
    }

    @Override
    protected void onSwipeRefreshing() {
        loadingInterestingGroups();
    }

    @Override
    protected void onLoadingMore() {
        loadingInterestingGroups();
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    private void loadingInterestingGroups() {
        setLoadingText(R.string.ui_organization_interesting_loading);
        setNothingText(R.string.ui_organization_interesting_nothing);
        displayLoading(true);
        displayNothing(false);
        OrgRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Organization>() {
            @Override
            public void onResponse(List<Organization> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                displayLoading(false);
                if (success) {
                    if (null != list) {
                        if (list.size() >= pageSize) {
                            remotePageNumber++;
                            isLoadingComplete(false);
                        } else {
                            isLoadingComplete(true);
                        }
                        mAdapter.update(list, false);
                    } else {
                        isLoadingComplete(true);
                    }
                } else {
                    isLoadingComplete(true);
                }
                displayNothing(mAdapter.getItemCount() < 1);
                stopRefreshing();
            }
        }).listInteresting(mQueryId, remotePageNumber);
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Organization org = mAdapter.get(index);
            if (org.isConcerned()) {
                // 取消关注
                cancelDialog(index);
            } else {
                // 点击关注
                concernDialog(index);
            }
        }
    };

    private void cancelDialog(final int index) {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                return View.inflate(Activity(), R.layout.popup_dialog_group_interest_cancel_concern, null);
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {

            }
        }).addOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                cancelConcern(index);
                return true;
            }
        }).setAdjustScreenWidth(true).setPopupType(DialogHelper.SLID_IN_BOTTOM).show();
    }

    private void cancelConcern(final int index) {
        setLoadingText(R.string.ui_organization_interesting_cancel_concern);
        displayLoading(true);
        OrgRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Organization>() {
            @Override
            public void onResponse(Organization organization, boolean success, String message) {
                super.onResponse(organization, success, message);
                displayLoading(false);
                if (success) {
                    mAdapter.get(index).setConcerned(false);
                    mAdapter.notifyItemChanged(index);
                }
            }
        }).concern(mQueryId, mAdapter.get(index).getId(), OrgRequest.CONCERN_CANCEL);
    }

    private View dialogView;
    private CustomTextView upperIcon, friendIcon;
    private boolean isUpper = false;

    private void resetDialogIcons() {
        upperIcon.setText(isUpper ? R.string.ui_icon_radio_selected : R.string.ui_icon_radio_unselected);
        friendIcon.setText(isUpper ? R.string.ui_icon_radio_unselected : R.string.ui_icon_radio_selected);
        upperIcon.setTextColor(getColor(isUpper ? R.color.colorPrimary : R.color.textColorHint));
        friendIcon.setTextColor(getColor(isUpper ? R.color.textColorHint : R.color.colorPrimary));
    }

    private void clearDialogIcons() {
        upperIcon.setText(R.string.ui_icon_radio_unselected);
        friendIcon.setText(R.string.ui_icon_radio_unselected);
        upperIcon.setTextColor(getColor(R.color.textColorHint));
        friendIcon.setTextColor(getColor(R.color.textColorHint));
    }

    private void concernDialog(final int selectedIndex) {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == dialogView) {
                    dialogView = View.inflate(Activity(), R.layout.popup_dialog_group_interest_concern, null);
                    upperIcon = (CustomTextView) dialogView.findViewById(R.id.ui_dialog_group_interest_concern_as_upper_icon);
                    friendIcon = (CustomTextView) dialogView.findViewById(R.id.ui_dialog_group_interest_concern_as_friend_icon);
                }
                isUpper = false;
                clearDialogIcons();
                return dialogView;
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {

            }
        }).addOnEventHandlerListener(new DialogHelper.OnEventHandlerListener() {
            @Override
            public int[] clickEventHandleIds() {
                return new int[]{
                        R.id.ui_dialog_group_interest_concern_container,
                        R.id.ui_dialog_group_interest_concern_as_upper,
                        R.id.ui_dialog_group_interest_concern_as_friend
                };
            }

            @Override
            public boolean onClick(View view) {
                switch (view.getId()) {
                    case R.id.ui_dialog_group_interest_concern_container:
                        // 背景点击，关闭对话框
                        return true;
                    case R.id.ui_dialog_group_interest_concern_as_upper:
                        isUpper = true;
                        resetDialogIcons();
                        break;
                    case R.id.ui_dialog_group_interest_concern_as_friend:
                        isUpper = false;
                        resetDialogIcons();
                        break;
                }
                return false;
            }
        }).addOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                concernGroup(selectedIndex);
                return true;
            }
        }).setPopupType(DialogHelper.SLID_IN_BOTTOM).show();
    }

    private void concernGroup(final int index) {
        setLoadingText(R.string.ui_organization_interesting_concerning);
        displayLoading(true);
        OrgRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Organization>() {
            @Override
            public void onResponse(Organization organization, boolean success, String message) {
                super.onResponse(organization, success, message);
                displayLoading(false);
                if (success) {
                    // 关注成功之后重新拉取感兴趣的组织列表
                    //loadingInterestingGroups();
                    mAdapter.get(index).setConcerned(true);
                    mAdapter.notifyItemChanged(index);
                }
            }
        }).concern(mQueryId, mAdapter.get(index).getId(), (isUpper ? OrgRequest.CONCERN_UPPER : OrgRequest.CONCERN_FRIEND));
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            setCustomTitle(R.string.ui_organization_interesting_fragment_title);
            mAdapter = new InterestAdapter();
            mRecyclerView.setAdapter(mAdapter);
            loadingInterestingGroups();
        }
    }

    private class InterestAdapter extends RecyclerViewAdapter<GroupInterestViewHolder, Organization> {
        @Override
        public GroupInterestViewHolder onCreateViewHolder(View itemView, int viewType) {
            GroupInterestViewHolder holder = new GroupInterestViewHolder(itemView, InterestingOrganizationFragment.this);
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
