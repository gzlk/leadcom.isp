package com.leadcom.android.isp.fragment.organization;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.home.GroupDetailsViewHolder;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.common.SimpleClickableItem;
import com.leadcom.android.isp.model.organization.Role;

/**
 * <b>功能描述：</b>财务管理主页<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/14 16:36 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/14 16:36 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class FinanceManageFragment extends BaseSwipeRefreshSupportFragment {

    public static FinanceManageFragment newInstance(Bundle bundle) {
        FinanceManageFragment fmf = new FinanceManageFragment();
        fmf.setArguments(bundle);
        return fmf;
    }

    public static void open(BaseFragment fragment, String groupId, String groupName) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, groupId);
        bundle.putString(PARAM_SEARCHED, groupName);
        fragment.openActivity(FinanceManageFragment.class.getName(), bundle, true, false);
    }

    private String mGroupName;
    private String[] items;
    private ItemAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        enableSwipe(false);
        isLoadingComplete(true);
        String title = StringHelper.getString(R.string.ui_group_finance_management_main_title);
        if (!isEmpty(mGroupName)) {
            title = format("%s(%s)", title, mGroupName);
        }
        setCustomTitle(title);
        items = StringHelper.getStringArray(R.array.ui_group_finance_management_items);
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mGroupName = bundle.getString(PARAM_SEARCHED, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_SEARCHED, mGroupName);
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
        if (null == mAdapter) {
            mAdapter = new ItemAdapter();
            mRecyclerView.setAdapter(mAdapter);
            showItems();
        }
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    private void showItems() {
        boolean isFinanceManager = Role.isFinanceManager(mQueryId);
        for (String string : items) {
            if (!isFinanceManager && string.startsWith("4|")) {
                // 不是管理员不允许查看收支统计项目
                continue;
            }
            SimpleClickableItem item = new SimpleClickableItem(string);
            mAdapter.add(item);
        }
    }

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            SimpleClickableItem item = mAdapter.get(index);
            switch (item.getIndex()) {
                case 1:
                    // 缴费记账
                    FinanceListFragment.open(FinanceManageFragment.this, mQueryId, mGroupName, "");
                    break;
                case 2:
                    // 支出记账
                    break;
                case 3:
                    // 我的审批
                    break;
                case 4:
                    // 收支统计
                    break;
            }
        }
    };

    private class ItemAdapter extends RecyclerViewAdapter<GroupDetailsViewHolder, SimpleClickableItem> {
        @Override
        public GroupDetailsViewHolder onCreateViewHolder(View itemView, int viewType) {
            GroupDetailsViewHolder holder = new GroupDetailsViewHolder(itemView, FinanceManageFragment.this);
            holder.setOnViewHolderElementClickListener(elementClickListener);
            return holder;
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
