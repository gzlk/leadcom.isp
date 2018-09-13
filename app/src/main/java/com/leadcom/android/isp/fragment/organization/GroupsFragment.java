package com.leadcom.android.isp.fragment.organization;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.hlk.hlklib.lib.inject.ViewId;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.org.RelationRequest;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.helper.popup.DeleteDialogHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.holder.common.InputableSearchViewHolder;
import com.leadcom.android.isp.holder.organization.GroupInterestViewHolder;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.operation.GRPOperation;
import com.leadcom.android.isp.model.organization.RelateGroup;
import com.leadcom.android.isp.model.organization.Role;

import java.util.List;

/**
 * <b>功能描述：</b>上下级组织列表页<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/09/13 11:40 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/09/13 11:40  <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class GroupsFragment extends BaseOrganizationFragment {

    private static final String PARAM_GROUPS_TYPE = "gf_param_groups_type";

    public static GroupsFragment newInstance(Bundle bundle) {
        GroupsFragment gf = new GroupsFragment();
        gf.setArguments(bundle);
        return gf;
    }

    public static void open(BaseFragment fragment, String groupId, String groupName, int openType) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, groupId);
        bundle.putString(PARAM_NAME, groupName);
        bundle.putInt(PARAM_GROUPS_TYPE, openType);
        fragment.openActivity(GroupsFragment.class.getName(), bundle, REQUEST_CREATE, true, false);
    }

    @ViewId(R.id.ui_holder_view_searchable_container)
    private View searchView;

    private RelationAdapter mAdapter;
    private String mGroupName, mSearchingText = "";
    private int mRelateType;
    private boolean isSearching = false;

    private boolean hasOperation(String operation) {
        Role role = Cache.cache().getGroupRole(mQueryId);
        return null != role && role.hasOperation(operation);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setCustomTitle(mRelateType == RelateGroup.RelationType.SUPERIOR ? R.string.ui_group_constructor_groups_fragment_title_supper : (mRelateType == RelateGroup.RelationType.ADD ? R.string.ui_group_constructor_groups_fragment_title_supper_add : R.string.ui_group_constructor_groups_fragment_title_sub));
        if (mRelateType == RelateGroup.RelationType.SUPERIOR && hasOperation(GRPOperation.GROUP_ASSOCIATION)) {
            setRightText(R.string.ui_base_text_add);
            setRightTitleClickListener(new OnTitleButtonClickListener() {
                @Override
                public void onClick() {
                    GroupsFragment.open(GroupsFragment.this, mQueryId, mGroupName, RelateGroup.RelationType.ADD);
                }
            });
        }
        InputableSearchViewHolder searchViewHolder = new InputableSearchViewHolder(searchView, this);
        searchViewHolder.setOnSearchingListener(new InputableSearchViewHolder.OnSearchingListener() {
            @Override
            public void onSearching(String text) {
                if (isSearching) {
                    ToastHelper.make().showMsg(R.string.ui_phone_contact_waiting_pagination);
                    return;
                }
                mSearchingText = text;
                if (isEmpty(mSearchingText)) {
                    mSearchingText = "";
                } else {
                    searchGroups();
                }
            }
        });
        searchView.setVisibility(mRelateType == RelateGroup.RelationType.ADD ? View.VISIBLE : View.GONE);
        setNothingText(mRelateType == RelateGroup.RelationType.ADD ? R.string.ui_group_constructor_groups_none_search : (mRelateType == RelateGroup.RelationType.SUPERIOR ? R.string.ui_group_constructor_groups_none_superior : R.string.ui_group_constructor_groups_none_sub));
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mGroupName = bundle.getString(PARAM_NAME, "");
        mRelateType = bundle.getInt(PARAM_GROUPS_TYPE, RelateGroup.RelationType.NONE);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_NAME, mGroupName);
        bundle.putInt(PARAM_GROUPS_TYPE, mRelateType);
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == REQUEST_CREATE) {
            loadingGroups();
        }
        super.onActivityResult(requestCode, data);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_searchable_list_swipe_disabled;
    }

    @Override
    protected void onSwipeRefreshing() {

    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    public void doingInResume() {
        // 设置可以回调旧的页面
        Activity().setResult(Activity.RESULT_OK);
        initializeAdapter();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new RelationAdapter();
            mAdapter.setOnDataHandingListener(handingListener);
            mRecyclerView.setAdapter(mAdapter);
            if (mRelateType >= RelateGroup.RelationType.SUPERIOR && mRelateType <= RelateGroup.RelationType.SUBORDINATE) {
                loadingGroups();
            }
        }
    }

    private void handleData(List<RelateGroup> list) {
        for (RelateGroup group : list) {
            if (isEmpty(group.getId())) {
                group.setId(group.getGroupId());
            }
            if (group.isSuperior() && mRelateType == RelateGroup.RelationType.ADD) {
                group.setSelectable(true);
            }
        }
        // 分页处理，避免主UI卡死
        mAdapter.setData(list);
    }

    private RecyclerViewAdapter.OnDataHandingListener handingListener = new RecyclerViewAdapter.OnDataHandingListener() {
        @Override
        public void onStart() {

        }

        @Override
        public void onProgress(int currentPage, int maxPage, int maxCount) {

        }

        @Override
        public void onComplete() {
            displayNothing(mAdapter.getItemCount() <= 0);
            isSearching = false;
        }
    };

    private void loadingGroups() {
        RelationRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<RelateGroup>() {
            @Override
            public void onResponse(List<RelateGroup> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success && null != list) {
                    handleData(list);
                }
            }
        }).list(mQueryId, mRelateType);
    }

    private void searchGroups() {
        isSearching = true;
        RelationRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<RelateGroup>() {
            @Override
            public void onResponse(List<RelateGroup> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success && null != list) {
                    handleData(list);
                }
            }
        }).search(mQueryId, RelateGroup.RelationType.SUPERIOR, mSearchingText);
    }

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            switch (view.getId()) {
                case R.id.ui_holder_view_group_interest_button:
                    RelateGroup group = mAdapter.get(index);
                    if (group.isNoneRelation()) {
                        if (mQueryId.equals(group.getGroupId())) {
                            ToastHelper.make().showMsg(R.string.ui_group_constructor_groups_add_self_warning);
                        } else {
                            warningRelationAdd(group.getGroupId(), group.getGroupName());
                        }
                    } else if (group.isSuperior()) {
                        warningRelationRemove(group.getId(), group.getGroupId(), group.getGroupName());
                    }
                    break;
            }
        }
    };

    private void warningRelationAdd(final String groupId, String groupName) {
        String title = StringHelper.getString(R.string.ui_group_constructor_groups_add_warning, groupName);
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                addRelation(groupId);
                return true;
            }
        }).setTitleText(title).setConfirmText(R.string.ui_base_text_confirm).show();
    }

    private void addRelation(final String groupId) {
        RelationRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<RelateGroup>() {
            @Override
            public void onResponse(RelateGroup group, boolean success, String message) {
                super.onResponse(group, success, message);
                if (success) {
                    RelateGroup grp = mAdapter.get(groupId);
                    int index = mAdapter.indexOf(grp);
                    grp.setId(group.getId());
                    grp.setRelationType(RelateGroup.RelationType.SUPERIOR);
                    mAdapter.replace(grp, index);
                }
            }
        }).add(mQueryId, groupId, RelateGroup.RelationType.SUPERIOR);
    }

    private void warningRelationRemove(final String relationId, final String groupId, String groupName) {
        String title = StringHelper.getString(R.string.ui_group_constructor_groups_remove_warning, groupName, mGroupName);
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                removeRelation(relationId, groupId);
                return true;
            }
        }).setTitleText(title).setConfirmText(R.string.ui_base_text_confirm).show();
    }

    private void removeRelation(final String relationId, final String groupId) {
        RelationRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<RelateGroup>() {
            @Override
            public void onResponse(RelateGroup group, boolean success, String message) {
                super.onResponse(group, success, message);
                if (success) {
                    if (mRelateType == RelateGroup.RelationType.ADD) {
                        RelateGroup grp = mAdapter.get(relationId);
                        int index = mAdapter.indexOf(grp);
                        grp.setRelationType(RelateGroup.RelationType.NONE);
                        grp.setId(groupId);
                        mAdapter.replace(grp, index);
                    } else {
                        mAdapter.remove(relationId);
                    }
                }
            }
        }).delete(relationId);
    }

    private class RelationAdapter extends RecyclerViewAdapter<GroupInterestViewHolder, RelateGroup> {

        @Override
        public GroupInterestViewHolder onCreateViewHolder(View itemView, int viewType) {
            GroupInterestViewHolder givh = new GroupInterestViewHolder(itemView, GroupsFragment.this);
            givh.setOnViewHolderElementClickListener(elementClickListener);
            // 显示上下级时才显示按钮
            givh.setButtonShown(mRelateType == RelateGroup.RelationType.SUPERIOR || mRelateType == RelateGroup.RelationType.ADD);
            return givh;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_group_interesting_item;
        }

        @Override
        public void onBindHolderOfView(GroupInterestViewHolder holder, int position, @Nullable RelateGroup item) {
            holder.showContent(item, mSearchingText);
        }

        @Override
        protected int comparator(RelateGroup item1, RelateGroup item2) {
            return 0;
        }
    }
}
