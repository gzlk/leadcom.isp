package com.leadcom.android.isp.fragment.organization;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.org.ConcernRequest;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.main.GroupFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.popup.DeleteDialogHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.holder.common.InputableSearchViewHolder;
import com.leadcom.android.isp.holder.organization.GroupInterestViewHolder;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.operation.GRPOperation;
import com.leadcom.android.isp.model.organization.Concern;
import com.leadcom.android.isp.model.organization.Organization;
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

public class GroupConcernedFragment extends GroupBaseFragment {

    private static final String PARAM_UPPER = "cof_upper";
    private static final String PARAM_GROUP_NAME = "cof_group_name";
    private static final String PARAM_TYPE = "cof_concern_type";

    public static GroupConcernedFragment newInstance(Bundle bundle) {
        GroupConcernedFragment cof = new GroupConcernedFragment();
        cof.setArguments(bundle);
        return cof;
    }

    static Bundle getBundle(String groupId, String groupName, int concernType) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, groupId);
        bundle.putString(PARAM_GROUP_NAME, groupName);
        bundle.putInt(PARAM_TYPE, concernType);
        return bundle;
    }

    public static void open(BaseFragment fragment, String groupId, String groupName, int concernType) {
        fragment.openActivity(GroupConcernedFragment.class.getName(),
                getBundle(groupId, groupName, concernType), REQUEST_CONCERNED, true, false);
    }

    public static void open(Context context, String groupId, String groupName, int concernType) {
        BaseActivity.openActivity(context, GroupConcernedFragment.class.getName(),
                getBundle(groupId, groupName, concernType), REQUEST_CONCERNED, true, false);
    }

    private ArrayList<Concern> concerns = new ArrayList<>();
    private ConcernedAdapter mAdapter;

    private int mConcernType = ConcernRequest.CONCERN_TO;
    @ViewId(R.id.ui_main_archive_search_functions)
    private View functionView;
    @ViewId(R.id.ui_holder_view_searchable_container)
    private View searchableView;
    private String searchingText, mGroupName;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity().setResult(Activity.RESULT_OK);
        resetTitle();
        functionView.setVisibility(View.GONE);
        enableSwipe(false);
        isLoadingComplete(true);
        // 查看的是被关注列表时不显示搜索框；查看的是已关注列表且没有权限关注或取关时，不显示搜索框
        if (mConcernType == ConcernRequest.CONCERN_FROM || !hasOperation(mQueryId, GRPOperation.GROUP_ASSOCIATION)) {
            searchableView.setVisibility(View.GONE);
        }
        InputableSearchViewHolder searchViewHolder = new InputableSearchViewHolder(searchableView, this);
        searchViewHolder.setOnSearchingListener(new InputableSearchViewHolder.OnSearchingListener() {
            @Override
            public void onSearching(String text) {
                searchingText = text;
                fetchingConcernableGroups();
            }
        });
    }

    private void resetTitle() {
        String title = StringHelper.getString(mConcernType == ConcernRequest.CONCERN_TO ? R.string.ui_organization_concerned_fragment_title : R.string.ui_organization_concerned_from_fragment_title);
        if (!isEmpty(mGroupName)) {
            title = format("%s %s", mGroupName, title);
        }
        setCustomTitle(title);
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        searchingText = bundle.getString(PARAM_SEARCHED, "");
        isUpper = bundle.getBoolean(PARAM_UPPER, false);
        mGroupName = bundle.getString(PARAM_GROUP_NAME, "");
        mConcernType = bundle.getInt(PARAM_TYPE, ConcernRequest.CONCERN_TO);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_SEARCHED, searchingText);
        bundle.putBoolean(PARAM_UPPER, isUpper);
        bundle.putString(PARAM_GROUP_NAME, mGroupName);
        bundle.putInt(PARAM_TYPE, mConcernType);
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
            mAdapter = new ConcernedAdapter();
            mRecyclerView.setAdapter(mAdapter);
            fetchingConcernableGroups();
            if (isEmpty(mGroupName)) {
                fetchingRemoteOrganization(mQueryId);
            }
        }
    }

    @Override
    protected void onFetchingRemoteOrganizationComplete(Organization organization) {
        if (null != organization) {
            mGroupName = organization.getName();
            resetTitle();
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
                }
                searching();
            }
        }).list(mQueryId, mConcernType, remotePageNumber, searchingText);
    }

    private void searching() {
        mAdapter.clear();
        setNothingText(isEmpty(searchingText) ? (mConcernType == ConcernRequest.CONCERN_TO ? R.string.ui_organization_concerned_nothing : R.string.ui_organization_concerned_from_nothing) : R.string.ui_organization_concerned_search_nothing);
        for (Concern concern : concerns) {
            if (!isEmpty(searchingText)) {
                if (concern.getName().contains(searchingText)) {
                    mAdapter.update(concern);
                }
            } else {
                mAdapter.update(concern);
            }
        }
        displayNothing(mAdapter.getItemCount() < 1);
    }

    private boolean hasOperation(String groupId, String operation) {
        Role role = Cache.cache().getGroupRole(groupId);
        return null != role && role.hasOperation(operation);
    }

    private void checkConcern(int index) {
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

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            switch (view.getId()) {
                case R.id.ui_holder_view_group_interest_root:
                case R.id.ui_holder_view_group_interest_cover:
                    //UserIntroductionFragment.open(GroupConcernedFragment.this, mAdapter.get(index));
                    GroupFragment.open(GroupConcernedFragment.this, mAdapter.get(index).getId());
                    break;
                case R.id.ui_holder_view_group_interest_button:
                    checkConcern(index);
                    break;
            }
        }
    };

    private class ConcernedAdapter extends RecyclerViewAdapter<GroupInterestViewHolder, Concern> {
        @Override
        public GroupInterestViewHolder onCreateViewHolder(View itemView, int viewType) {
            GroupInterestViewHolder holder = new GroupInterestViewHolder(itemView, GroupConcernedFragment.this);
            holder.setOnViewHolderElementClickListener(elementClickListener);
            holder.setButtonShown(mConcernType == ConcernRequest.CONCERN_TO && hasOperation(mQueryId, GRPOperation.GROUP_ASSOCIATION));
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
