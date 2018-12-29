package com.leadcom.android.isp.fragment.organization;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.org.PaymentRequest;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.home.GroupDetailsViewHolder;
import com.leadcom.android.isp.holder.organization.PaymentUserDetailsViewHolder;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.organization.Payment;
import com.leadcom.android.isp.model.organization.Role;

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

    public static void open(BaseFragment fragment, String groupId, String groupName, String userId, int type) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, groupId);
        bundle.putString(PARAM_NAME, groupName);
        bundle.putString(PARAM_SEARCHED, userId);
        bundle.putInt(PARAM_SELECTABLE, type);
        fragment.openActivity(FinanceListFragment.class.getName(), bundle, true, false);
    }

    private String mGroupName, mUserId;
    private int mType;
    private PaymentAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isShowLoadingBackground = true;
        String title = StringHelper.getString(mType == Payment.Type.PAYMENT ? R.string.ui_group_finance_1 : (mType == Payment.Type.EXPEND ? R.string.ui_group_finance_2 : R.string.ui_group_finance_3));
        if (!isEmpty(mGroupName)) {
            title = format("%s(%s)", title, mGroupName);
        }
        setCustomTitle(title);
        isLoadingComplete(true);
        if (isEmpty(mUserId) && Role.isFinanceManager(mQueryId) && (mType == Payment.Type.PAYMENT || mType == Payment.Type.EXPEND)) {
            setRightIcon(R.string.ui_icon_add);
            setRightTitleClickListener(new OnTitleButtonClickListener() {
                @Override
                public void onClick() {
                    // 添加缴费记录
                    FinanceCreatorFragment.open(FinanceListFragment.this, mType, mQueryId, mGroupName, "", "", "");
                }
            });
        }
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mGroupName = bundle.getString(PARAM_NAME, "");
        mUserId = bundle.getString(PARAM_SEARCHED, "");
        mType = bundle.getInt(PARAM_SELECTABLE, Payment.Type.PAYMENT);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_NAME, mGroupName);
        bundle.putString(PARAM_SEARCHED, mUserId);
        bundle.putInt(PARAM_SELECTABLE, mType);
    }

    @Override
    protected void onSwipeRefreshing() {
        remotePageNumber = 1;
        setSupportLoadingMore(true);
        loading();
    }

    @Override
    protected void onLoadingMore() {
        loading();
    }

    @Override
    public void doingInResume() {
        if (null == mAdapter) {
            mAdapter = new PaymentAdapter();
            mRecyclerView.setAdapter(mAdapter);
            loading();
        }
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == REQUEST_CREATE) {
            if (mType == Payment.Type.CHECK) {
                // 表示审批完毕
                String id = getResultedData(data);
                if (!isEmpty(id)) {
                    mAdapter.remove(id);
                }
            } else {
                // 创建、修改状态操作完毕之后，拉取列表
                loading();
            }
        }
        super.onActivityResult(requestCode, data);
    }

    private void loading() {
        switch (mType) {
            case Payment.Type.PAYMENT:
                setNothingText(R.string.ui_group_finance_list_nothing);
                setLoadingText(R.string.ui_group_finance_list_loading_title);
                if (isEmpty(mUserId)) {
                    loadingGroupPayment();
                } else {
                    loadingUserPayments();
                }
                break;
            case Payment.Type.EXPEND:
                setNothingText(R.string.ui_group_finance_list_expend_nothing);
                setLoadingText(R.string.ui_group_finance_list_expend_loading_title);
                loadingGroupExpend();
                break;
            case Payment.Type.CHECK:
                setNothingText(R.string.ui_group_finance_list_check_nothing);
                setLoadingText(R.string.ui_group_finance_list_check_loading_title);
                loadingGroupUnchecked();
                break;
        }
    }

    private void loadingGroupPayment() {
        displayLoading(true);
        displayNothing(false);
        PaymentRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Payment>() {
            @Override
            public void onResponse(List<Payment> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success && null != list) {
                    for (Payment payment : list) {
                        payment.setType(mType);
                    }
                }
                handlePagePayment(list);
            }
        }).listPayment(mQueryId, remotePageNumber);
    }

    private void loadingUserPayments() {
        displayLoading(true);
        displayNothing(false);
        PaymentRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Payment>() {
            @Override
            public void onResponse(List<Payment> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success && null != list) {
                    for (Payment payment : list) {
                        payment.setLocalDeleted(true);
                        payment.setType(mType);
                    }
                }
                handlePagePayment(list);
            }
        }).listPaymentByUserId(mQueryId, mUserId, remotePageNumber);
    }

    private void loadingGroupExpend() {
        displayLoading(true);
        displayNothing(false);
        PaymentRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Payment>() {
            @Override
            public void onResponse(List<Payment> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success && null != list) {
                    for (Payment payment : list) {
                        payment.setType(mType);
                    }
                }
                handlePagePayment(list);
            }
        }).listExpend(mQueryId, remotePageNumber);
    }

    private void loadingGroupUnchecked() {
        displayLoading(true);
        displayNothing(false);
        PaymentRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Payment>() {
            @Override
            public void onResponse(List<Payment> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success && null != list) {
                    for (Payment payment : list) {
                        payment.setType(mType);
                    }
                }
                handlePagePayment(list);
            }
        }).listUnchecked(mQueryId, remotePageNumber);
    }

    private void handlePagePayment(List<Payment> list) {
        if (remotePageNumber <= 1) {
            mAdapter.clear();
        }
        int cnt = null == list ? 0 : list.size();
        remotePageNumber += cnt >= remotePageSize ? 1 : 0;
        if (null != list) {
            mAdapter.update(list);
        }
        displayLoading(false);
        displayNothing(mAdapter.getItemCount() <= 0);
        stopRefreshing();
        isLoadingComplete(cnt < remotePageSize);
    }

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            Payment payment = mAdapter.get(index);
            String id = mType == Payment.Type.PAYMENT ? payment.getId() : payment.getExpendFlowerId();
            if (isEmpty(mUserId)) {
                if (payment.isPayment()) {
                    // 打开某个用户的记账记录
                    FinanceListFragment.open(FinanceListFragment.this, mQueryId, mGroupName, payment.getUserId(), mType);
                } else {
                    // 打开查看支出申请详情或审批页面
                    FinanceCreatorFragment.open(FinanceListFragment.this, mType, mQueryId, mGroupName, "", id, payment.getUserId());
                }
            } else {
                // 打开某个记账记录详情，查看凭证图片
                FinanceCreatorFragment.open(FinanceListFragment.this, mType, mQueryId, mGroupName, "", id, payment.getUserId());
            }
        }
    };

    private class PaymentAdapter extends RecyclerViewAdapter<BaseViewHolder, Payment> {

        private static final int TP_USER = 0, TP_DETAILS = 1;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            switch (viewType) {
                case TP_USER:
                    GroupDetailsViewHolder gdvh = new GroupDetailsViewHolder(itemView, FinanceListFragment.this);
                    gdvh.setOnViewHolderElementClickListener(elementClickListener);
                    return gdvh;
                case TP_DETAILS:
                    PaymentUserDetailsViewHolder pudvh = new PaymentUserDetailsViewHolder(itemView, FinanceListFragment.this);
                    pudvh.setOnViewHolderElementClickListener(elementClickListener);
                    return pudvh;
            }
            return null;
        }

        @Override
        public int itemLayout(int viewType) {
            return viewType == TP_USER ? R.layout.holder_view_group_details : R.layout.holder_view_payment_user_details;
        }

        @Override
        public int getItemViewType(int position) {
            return get(position).isLocalDeleted() ? TP_DETAILS : TP_USER;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Payment item) {
            if (holder instanceof GroupDetailsViewHolder) {
                ((GroupDetailsViewHolder) holder).showContent(item);
            } else if (holder instanceof PaymentUserDetailsViewHolder) {
                ((PaymentUserDetailsViewHolder) holder).showContent(item);
            }
        }

        @Override
        protected int comparator(Payment item1, Payment item2) {
            return 0;
        }
    }
}
