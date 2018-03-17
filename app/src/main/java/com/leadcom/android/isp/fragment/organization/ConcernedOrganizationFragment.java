package com.leadcom.android.isp.fragment.organization;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.org.OrgRequest;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.DialogHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.organization.GroupInterestViewHolder;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.organization.Concern;
import com.leadcom.android.isp.model.organization.Organization;
import com.hlk.hlklib.lib.view.CustomTextView;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>已关注的组织列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/07/12 10:50 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/07/12 10:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ConcernedOrganizationFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_GROUPS = "cof_concerned_groups";

    public static ConcernedOrganizationFragment newInstance(String params) {
        ConcernedOrganizationFragment cof = new ConcernedOrganizationFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        // 传过来的组织id
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        // 已关注的组织列表
        bundle.putString(PARAM_GROUPS, StringHelper.replaceJson(strings[1], true));
        cof.setArguments(bundle);
        return cof;
    }

    public static void open(BaseFragment fragment, String groupId, String conGroups, int req) {
        fragment.openActivity(ConcernedOrganizationFragment.class.getName(), format("%s,%s", groupId, conGroups), req, true, false);
    }

    String groupJson;
    private ArrayList<Concern> concerned = new ArrayList<>();
    private ConcernedAdapter mAdapter;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        groupJson = bundle.getString(PARAM_GROUPS, EMPTY_ARRAY);
        concerned = Json.gson().fromJson(groupJson, new TypeToken<ArrayList<Concern>>() {
        }.getType());
        if (null == concerned) {
            concerned = new ArrayList<>();
        }
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_GROUPS, groupJson);
    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        initializeAdapter();
    }

    @Override
    public int getLayout() {
        return R.layout.tool_view_recycler_view_none_swipe_refreshable;
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

    private void initializeAdapter() {
        if (null == mAdapter) {
            setCustomTitle(R.string.ui_organization_concerned_fragment_title);
            mAdapter = new ConcernedAdapter();
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.update(concerned);
        }
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Concern concern = mAdapter.get(index);
            if (concern.isConcerned()) {
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
                    Concern concern = mAdapter.get(index);
                    concern.setConcernType(Concern.ConcernType.NONE);
                    concern.setType(0);
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
                    upperIcon = dialogView.findViewById(R.id.ui_dialog_group_interest_concern_as_upper_icon);
                    friendIcon = dialogView.findViewById(R.id.ui_dialog_group_interest_concern_as_friend_icon);
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
                    // 关注成功之后设置关注属性并刷新列表
                    Concern concern = mAdapter.get(index);
                    concern.setConcernType(Concern.ConcernType.CONCERNED);
                    concern.setType(isUpper ? OrgRequest.CONCERN_UPPER : OrgRequest.CONCERN_FRIEND);
                    mAdapter.notifyItemChanged(index);
                }
            }
        }).concern(mQueryId, mAdapter.get(index).getId(), (isUpper ? OrgRequest.CONCERN_UPPER : OrgRequest.CONCERN_FRIEND));
    }

    private class ConcernedAdapter extends RecyclerViewAdapter<GroupInterestViewHolder, Concern> {
        @Override
        public GroupInterestViewHolder onCreateViewHolder(View itemView, int viewType) {
            GroupInterestViewHolder holder = new GroupInterestViewHolder(itemView, ConcernedOrganizationFragment.this);
            holder.addOnViewHolderClickListener(onViewHolderClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_group_interesting_item;
        }

        @Override
        public void onBindHolderOfView(GroupInterestViewHolder holder, int position, @Nullable Concern item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(Concern item1, Concern item2) {
            return 0;
        }
    }
}
