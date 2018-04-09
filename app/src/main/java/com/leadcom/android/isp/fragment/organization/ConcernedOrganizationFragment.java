package com.leadcom.android.isp.fragment.organization;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.org.ConcernRequest;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.popup.DeleteDialogHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.holder.common.InputableSearchViewHolder;
import com.leadcom.android.isp.holder.organization.GroupInterestViewHolder;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.operation.GRPOperation;
import com.leadcom.android.isp.model.organization.Concern;
import com.leadcom.android.isp.model.organization.Role;

import java.util.ArrayList;
import java.util.List;

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

    private static final String PARAM_UPPER = "cof_upper";

    public static ConcernedOrganizationFragment newInstance(Bundle bundle) {
        ConcernedOrganizationFragment cof = new ConcernedOrganizationFragment();
        cof.setArguments(bundle);
        return cof;
    }

    public static void open(BaseFragment fragment, String groupId) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, groupId);
        fragment.openActivity(ConcernedOrganizationFragment.class.getName(), bundle, REQUEST_CONCERNED, true, false);
    }

    private ArrayList<Concern> concerns = new ArrayList<>();
    private ConcernedAdapter mAdapter;

    @ViewId(R.id.ui_main_archive_search_functions)
    private View functionView;
    @ViewId(R.id.ui_holder_view_searchable_container)
    private View searchableView;
    private String searchingText;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity().setResult(Activity.RESULT_OK);
        functionView.setVisibility(View.GONE);
        enableSwipe(false);
        isLoadingComplete(true);
        InputableSearchViewHolder searchViewHolder = new InputableSearchViewHolder(searchableView, this);
        searchViewHolder.setOnSearchingListener(new InputableSearchViewHolder.OnSearchingListener() {
            @Override
            public void onSearching(String text) {
                searchingText = text;
                searching();
            }
        });
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        searchingText = bundle.getString(PARAM_SEARCHED, "");
        isUpper = bundle.getBoolean(PARAM_UPPER, false);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_SEARCHED, searchingText);
        bundle.putBoolean(PARAM_UPPER, isUpper);
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
        return R.layout.fragment_main_archive_search;
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
            fetchingConcernableGroups();
        }
    }

    private void fetchingConcernableGroups() {
        ConcernRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Concern>() {
            @Override
            public void onResponse(List<Concern> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                concerns.clear();
                if (success && null != list) {
                    concerns.addAll(list);
                    mAdapter.update(list);
                }
            }
        }).list(mQueryId, remotePageNumber, "");
    }

    private void searching() {
        mAdapter.clear();
        for (Concern concern : concerns) {
            if (!isEmpty(searchingText)) {
                if (concern.getName().contains(searchingText)) {
                    mAdapter.update(concern);
                }
            } else {
                mAdapter.update(concern);
            }
        }
    }

    private boolean hasOperation(String groupId, String operation) {
        Role role = Cache.cache().getGroupRole(groupId);
        return null != role && role.hasOperation(operation);
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            if (!hasOperation(mQueryId, GRPOperation.GROUP_ASSOCIATION)) {
                return;
            }
            Concern concern = mAdapter.get(index);
            if (concern.isConcerned()) {
                // 取消关注
                warningCancelConcern(index);
            } else {
                // 点击关注
                warningConcern(index);
            }
        }
    };

    private void warningCancelConcern(final int index) {
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                cancelConcern(index);
                return true;
            }
        }).setTitleText(getString(R.string.ui_organization_interesting_cancel_concern_warning, mAdapter.get(index).getName())).setConfirmText(R.string.ui_base_text_confirm).show();
    }

    private void cancelConcern(final int index) {
        setLoadingText(R.string.ui_organization_interesting_cancel_concern);
        displayLoading(true);
        ConcernRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Concern>() {
            @Override
            public void onResponse(Concern concern, boolean success, String message) {
                super.onResponse(concern, success, message);
                if (success) {
                    Concern c = mAdapter.get(index);
                    // 未关注
                    c.setType(Concern.Type.CONCERNABLE);
                    mAdapter.notifyItemChanged(index);
                }
                displayLoading(false);
            }
        }).delete(mQueryId, mAdapter.get(index).getId());
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

    private void warningConcern(final int index) {
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                concernGroup(index);
                return true;
            }
        }).setTitleText(getString(R.string.ui_organization_interesting_concern_warning, mAdapter.get(index).getName())).setConfirmText(R.string.ui_base_text_confirm).show();
    }

    private void concernGroup(final int index) {
        setLoadingText(R.string.ui_organization_interesting_concerning);
        displayLoading(true);
        ConcernRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Concern>() {
            @Override
            public void onResponse(Concern concern, boolean success, String message) {
                super.onResponse(concern, success, message);
                displayLoading(false);
                if (success) {
                    // 关注成功之后设置关注属性并刷新列表
                    Concern c = mAdapter.get(index);
                    c.setType(Concern.Type.FRIEND);
                    mAdapter.notifyItemChanged(index);
                }
            }
        }).add(mQueryId, mAdapter.get(index).getId());
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
            holder.showContent(item, searchingText);
        }

        @Override
        protected int comparator(Concern item1, Concern item2) {
            return 0;
        }
    }
}
