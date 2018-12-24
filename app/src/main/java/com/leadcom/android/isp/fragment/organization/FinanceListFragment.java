package com.leadcom.android.isp.fragment.organization;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.org.PaymentRequest;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.home.GroupDetailsViewHolder;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.organization.Payment;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能描述：</b>财务列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/12/24 21:28 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/12/24 21:28  <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class FinanceListFragment extends GroupBaseFragment {

    public static FinanceListFragment newInstance(Bundle bundle) {
        FinanceListFragment flf = new FinanceListFragment();
        flf.setArguments(bundle);
        return flf;
    }

    public static void open(BaseFragment fragment, String groupId, String groupName, String userId) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, groupId);
        bundle.putString(PARAM_NAME, groupName);
        bundle.putString(PARAM_SEARCHED, userId);
        fragment.openActivity(FinanceListFragment.class.getName(), bundle, true, false);
    }

    private String mGroupName, mUserId;
    private PaymentAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String title = StringHelper.getString(R.string.ui_group_finance_list_title);
        if (!isEmpty(mGroupName)) {
            title = format("%s(%s)", title, mGroupName);
        }
        setCustomTitle(title);
        isLoadingComplete(true);
        setNothingText(R.string.ui_group_finance_list_nothing);
        setLoadingText(R.string.ui_group_finance_list_loading_title);
        setRightIcon(R.string.ui_icon_add);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                // 添加缴费记录
            }
        });
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mGroupName = bundle.getString(PARAM_NAME, "");
        mUserId = bundle.getString(PARAM_SEARCHED, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_NAME, mGroupName);
        bundle.putString(PARAM_SEARCHED, mUserId);
    }

    @Override
    protected void onSwipeRefreshing() {
        loadingGroupPayment();
    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    public void doingInResume() {
        if (null == mAdapter) {
            mAdapter = new PaymentAdapter();
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setOnDataHandingListener(handingListener);
            loadingGroupPayment();
        }
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    private void loadingGroupPayment() {
        displayLoading(true);
        displayNothing(false);
        PaymentRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Payment>() {
            @Override
            public void onResponse(List<Payment> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (null == list) {
                    list = new ArrayList<>();
                }
                Payment payment = new Payment();
                payment.setUserName("小果");
                payment.setUserHeadPhoto("http://120.25.124.199:8008/group1/M00/00/01/eBk66lkruluAfabnAAIkQkp4t78087.jpg");
                payment.setTotalPayAmount(12345.67);
                list.add(payment);
                //mAdapter.setData(null == list ? new ArrayList<Payment>() : list);
                mAdapter.setData(list);
            }
        }).list(mQueryId);
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
            displayLoading(false);
            displayNothing(mAdapter.getItemCount() <= 0);
            stopRefreshing();
        }
    };

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {

        }
    };

    private class PaymentAdapter extends RecyclerViewAdapter<GroupDetailsViewHolder, Payment> {

        @Override
        public GroupDetailsViewHolder onCreateViewHolder(View itemView, int viewType) {
            GroupDetailsViewHolder gdvh = new GroupDetailsViewHolder(itemView, FinanceListFragment.this);
            gdvh.setOnViewHolderElementClickListener(elementClickListener);
            return gdvh;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_group_details;
        }

        @Override
        public void onBindHolderOfView(GroupDetailsViewHolder holder, int position, @Nullable Payment item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(Payment item1, Payment item2) {
            return 0;
        }
    }
}
