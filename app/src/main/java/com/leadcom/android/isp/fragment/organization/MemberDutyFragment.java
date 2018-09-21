package com.leadcom.android.isp.fragment.organization;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.main.ArchiveSearchFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.home.GroupDetailsViewHolder;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.common.SimpleClickableItem;
import com.leadcom.android.isp.model.operation.GRPOperation;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * <b>功能描述：</b>组织成员履职统计主页<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/09/13 14:30 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/09/13 14:30  <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class MemberDutyFragment extends GroupBaseFragment {

    public static MemberDutyFragment newInstance(Bundle bundle) {
        MemberDutyFragment gmdmf = new MemberDutyFragment();
        gmdmf.setArguments(bundle);
        return gmdmf;
    }

    public static void open(BaseFragment fragment, String groupId, String groupName) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, groupId);
        bundle.putString(PARAM_NAME, groupName);
        fragment.openActivity(MemberDutyFragment.class.getName(), bundle, true, false);
    }

    private ItemAdapter mAdapter;
    private String[] items;
    private String mGroupName;

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        enableSwipe(false);
        isLoadingComplete(true);
        setCustomTitle(R.string.ui_group_member_duty_main_fragment_title);
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

    private void initializeAdapter() {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_group_member_duty_main_items);
        }
        if (null == mAdapter) {
            mAdapter = new ItemAdapter();
            mRecyclerView.setAdapter(mAdapter);
            OverScrollDecoratorHelper.setUpOverScroll(mRecyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
            for (String string : items) {
                SimpleClickableItem item = new SimpleClickableItem(string);
                mAdapter.add(item);
            }
        }
    }

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            switch (index) {
                case 0:
                    // 本组成员履职统计
                    if (hasOperation(mQueryId, GRPOperation.MEMBER_DUTY)) {
                        ArchiveSearchFragment.open(MemberDutyFragment.this, ArchiveSearchFragment.SEARCH_DUTY, mQueryId, "", mGroupName);
                    } else {
                        ToastHelper.make().showMsg(R.string.ui_group_details_no_permission_to_member_duty);
                    }
                    break;
                case 1:
                    // 本组支部履职统计
                    if (hasOperation(mQueryId, GRPOperation.SQUAD_DUTY)) {
                        ArchiveSearchFragment.open(MemberDutyFragment.this, ArchiveSearchFragment.SEARCH_DUTY_SQUAD, mQueryId, "", mGroupName);
                    } else {
                        ToastHelper.make().showMsg(R.string.ui_group_details_no_permission_to_squad_duty);
                    }
                    break;
            }
        }
    };

    private class ItemAdapter extends RecyclerViewAdapter<GroupDetailsViewHolder, SimpleClickableItem> {

        @Override
        public GroupDetailsViewHolder onCreateViewHolder(View itemView, int viewType) {
            GroupDetailsViewHolder gdvh = new GroupDetailsViewHolder(itemView, MemberDutyFragment.this);
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
