package com.leadcom.android.isp.fragment.organization;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.org.RelationRequest;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.home.GroupDetailsViewHolder;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.common.SimpleClickableItem;
import com.leadcom.android.isp.model.operation.GRPOperation;
import com.leadcom.android.isp.model.organization.RelateGroup;
import com.leadcom.android.isp.model.organization.Squad;

import java.util.List;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * <b>功能描述：</b>组织架构首页<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/09/11 13:31 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/09/11 13:31  <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class GroupConstructFragment extends GroupBaseFragment {

    public static String TITLE = "";

    public static GroupConstructFragment newInstance(Bundle bundle) {
        GroupConstructFragment gcf = new GroupConstructFragment();
        gcf.setArguments(bundle);
        return gcf;
    }

    public static void open(BaseFragment fragment, String groupId, String groupName) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, groupId);
        bundle.putString(PARAM_NAME, groupName);
        fragment.openActivity(GroupConstructFragment.class.getName(), bundle, true, false);
    }

    private ItemAdapter mAdapter;
    private String[] items;
    private String mGroupName;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        enableSwipe(false);
        isLoadingComplete(true);
        if (isEmpty(TITLE)) {
            setCustomTitle(format("%s(%s)", StringHelper.getString(R.string.ui_group_constructor_fragment_title), mGroupName));
        } else {
            setCustomTitle(TITLE);
        }
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mGroupName = bundle.getString(PARAM_NAME, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_NAME, mGroupName);
    }

    @Override
    protected void onSwipeRefreshing() {

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
        return true;
    }

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            SimpleClickableItem item = mAdapter.get(index);
            if (item.isDisabled()) {
                showToast(item.getIndex());
                return;
            }
            switch (item.getIndex()) {
                case 0:
                    // 上级组织
                    GroupsFragment.open(GroupConstructFragment.this, mQueryId, mGroupName, RelateGroup.RelationType.SUPERIOR, false, null);
                    break;
                case 1:
                    // 下级组织
                    GroupsFragment.open(GroupConstructFragment.this, mQueryId, mGroupName, RelateGroup.RelationType.SUBORDINATE, false, null);
                    break;
                case 2:
                    // 下属小组
                    SquadsFragment.open(GroupConstructFragment.this, mQueryId, mGroupName);
                    break;
            }
        }

        private void showToast(int index) {
            ToastHelper.make().showMsg(index == 0 ? R.string.ui_group_constructor_groups_no_super : (index == 1 ? R.string.ui_group_constructor_groups_no_subordinate : R.string.ui_group_constructor_groups_no_squad));
        }
    };

    private void initializeAdapter() {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_group_constructor_items);
        }
        if (null == mAdapter) {
            mAdapter = new ItemAdapter();
            mRecyclerView.setAdapter(mAdapter);
            OverScrollDecoratorHelper.setUpOverScroll(mRecyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

            for (String string : items) {
                if (string.startsWith("1|") && !hasOperation(mQueryId, GRPOperation.GROUP_PROPERTY_SUBORDINATE)) {
                    // 没有权限查看下级组织时，不显示
                    continue;
                }
                SimpleClickableItem item = new SimpleClickableItem(string);
                mAdapter.add(item);
            }
            loadingGroups(RelateGroup.RelationType.SUPERIOR);
            loadingGroups(RelateGroup.RelationType.SUBORDINATE);
            fetchingRemoteSquads(mQueryId);
        }
    }

    private void loadingGroups(final int type) {
        RelationRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<RelateGroup>() {
            @Override
            public void onResponse(List<RelateGroup> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);

                SimpleClickableItem item = new SimpleClickableItem(items[type == RelateGroup.RelationType.SUPERIOR ? 0 : 1]);
                item.setDisabled(!success || null == list || list.size() <= 0);
                if (mAdapter.exist(item)) {
                    mAdapter.update(item);
                }
            }
        }).list(mQueryId, type);
    }

    @Override
    protected void onFetchingRemoteSquadsComplete(List<Squad> list) {
        SimpleClickableItem item = new SimpleClickableItem(items[2]);
        item.setDisabled(null == list || list.size() <= 0);
        if (mAdapter.exist(item)) {
            mAdapter.update(item);
        }
    }

    private class ItemAdapter extends RecyclerViewAdapter<GroupDetailsViewHolder, SimpleClickableItem> {

        @Override
        public GroupDetailsViewHolder onCreateViewHolder(View itemView, int viewType) {
            GroupDetailsViewHolder gdvh = new GroupDetailsViewHolder(itemView, GroupConstructFragment.this);
            gdvh.setOnViewHolderElementClickListener(elementClickListener);
            return gdvh;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_group_details;
        }

        @Override
        public void onBindHolderOfView(GroupDetailsViewHolder holder, int position, @Nullable SimpleClickableItem item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(SimpleClickableItem item1, SimpleClickableItem item2) {
            return 0;
        }
    }
}
