package com.leadcom.android.isp.fragment.organization;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hlk.hlklib.layoutmanager.CustomLinearLayoutManager;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.org.PaymentRequest;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.DateTimePicker;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.home.GroupDetailsViewHolder;
import com.leadcom.android.isp.holder.organization.FinanceCollectionViewHolder;
import com.leadcom.android.isp.holder.organization.GroupInterestViewHolder;
import com.leadcom.android.isp.holder.organization.SquadViewHolder;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.organization.Payment;
import com.leadcom.android.isp.model.organization.Squad;
import com.tencent.mm.opensdk.modelpay.PayReq;

import java.util.Date;
import java.util.Iterator;
import java.util.List;


/**
 * <b>功能描述：</b>财务统计页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/12/28 10:48 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/12/28 10:48  <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class FinanceCollectionFragment extends GroupBaseFragment {

    public static FinanceCollectionFragment newInstance(Bundle bundle) {
        FinanceCollectionFragment fcf = new FinanceCollectionFragment();
        fcf.setArguments(bundle);
        return fcf;
    }

    public static void open(BaseFragment fragment, String groupId, String groupName, String squadId) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, groupId);
        bundle.putString(PARAM_SEARCHED, groupName);
        bundle.putString(PARAM_SQUAD_ID, squadId);
        fragment.openActivity(FinanceCollectionFragment.class.getName(), bundle, true, false);
    }

    @ViewId(R.id.ui_group_payment_collection_functions_0_text)
    private TextView function0Text;
    @ViewId(R.id.ui_group_payment_collection_functions_0_icon)
    private CustomTextView function0Icon;
    @ViewId(R.id.ui_group_payment_collection_functions_1_text)
    private TextView function1Text;
    @ViewId(R.id.ui_group_payment_collection_functions_1_icon)
    private CustomTextView function1Icon;
    @ViewId(R.id.ui_group_payment_collection_time_picker)
    private FrameLayout timePickerRoot;
    @ViewId(R.id.ui_group_payment_collection_group_squads_picker)
    private View squadPickerRoot;
    @ViewId(R.id.ui_group_payment_collection_group_squads)
    private RecyclerView squadsView;
    @ViewId(R.id.ui_group_payment_collection_content_background)
    private View selectorBackground;

    private String mGroupName;

    private static final int TYPE_NONE = 0, TYPE_DATE = 1, TYPE_SQUAD = 2;
    private String searchYear = "", searchSquad = "";
    private int function = TYPE_NONE;

    private DateTimePicker dateTimePicker;
    private SquadAdapter sAdapter;
    private PaymentAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isShowLoadingBackground = true;
        String title = StringHelper.getString(R.string.ui_group_finance_4);
        if (!isEmpty(mGroupName)) {
            title = format("%s(%s)", title, mGroupName);
        }
        setCustomTitle(title);
        setNothingText(R.string.ui_group_finance_collection_nothing);
        setLoadingText(R.string.ui_group_finance_collection_loading);
        dateTimePicker = DateTimePicker.picker().setSelectionType(true, false, false, false, false, false)
                .setSelectedTitleFormat("yyyy年").setOnDateTimePickedListener(new DateTimePicker.OnDateTimePickedListener() {
                    @Override
                    public void onPicked(Date date) {
                        searchYear = Utils.format("yyyy", date);
                        //hideChooser();
                        fetchingPaymentCollection();
                    }

                    @Override
                    public void onReset() {
                        searchYear = "";
                        fetchingPaymentCollection();
                    }
                }).setCancelText(R.string.ui_base_text_all);
        initializeChooserPosition();
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
        remotePageNumber = 1;
        setSupportLoadingMore(true);
        fetchingPaymentCollection();
    }

    @Override
    protected void onLoadingMore() {
        fetchingPaymentCollection();
    }

    @Override
    public void doingInResume() {
        initializeSquadAdapter();
        initializePaymentAdapter();
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_group_payment_collection;
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Click({R.id.ui_group_payment_collection_functions_0,
            R.id.ui_group_payment_collection_functions_1,
            R.id.ui_group_payment_collection_content_background})
    private void viewClick(View view) {
        switch (view.getId()) {
            case R.id.ui_group_payment_collection_content_background:
                hideChooser();
                break;
            case R.id.ui_group_payment_collection_functions_0:
                if (function != TYPE_DATE) {
                    function = TYPE_DATE;
                    showChooser();
                } else {
                    hideChooser();
                }
                break;
            case R.id.ui_group_payment_collection_functions_1:
                if (function != TYPE_SQUAD) {
                    function = TYPE_SQUAD;
                    showChooser();
                } else {
                    hideChooser();
                }
                break;
        }
    }

    private void initializeChooserPosition() {
        Handler().post(new Runnable() {
            @Override
            public void run() {
                if (null != dateTimePicker) {
                    dateTimePicker.show(timePickerRoot);
                }
                hideChooser();
                resetFunction();
            }
        });
    }

    private void showChooser() {
        showSelectorBackground(true);
        showTimePicker(function == TYPE_DATE);
        showSquadPicker(function == TYPE_SQUAD);
        resetFunction();
    }

    private void hideChooser() {
        showSelectorBackground(false);
        showTimePicker(false);
        showSquadPicker(false);
        function = TYPE_NONE;
        resetFunction();
    }

    private void showSelectorBackground(final boolean shown) {
        if (shown && selectorBackground.getVisibility() == View.VISIBLE) return;
        if (!shown && selectorBackground.getVisibility() == View.GONE) return;
        selectorBackground.animate()
                .alpha(shown ? 1.0f : 0.0f)
                .setDuration(duration())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (shown) {
                            selectorBackground.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!shown) {
                            selectorBackground.setVisibility(View.GONE);
                        }
                    }
                }).start();
    }

    private void showTimePicker(final boolean shown) {
        function0Icon.animate().rotation(shown ? -90 : 90).setDuration(duration()).start();
        timePickerRoot.animate()
                .translationY(shown ? 0 : -timePickerRoot.getMeasuredHeight() * 1.1f)
                .alpha(shown ? 1.0f : 0.0f).setDuration(duration())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (shown) {
                            timePickerRoot.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!shown) {
                            timePickerRoot.setVisibility(View.GONE);
                        }
                    }
                }).start();
    }

    private void showSquadPicker(final boolean shown) {
        function1Icon.animate().rotation(shown ? -90 : 90).setDuration(duration()).start();
        squadPickerRoot.animate()
                .translationY(shown ? 0 : -squadPickerRoot.getMeasuredHeight() * 1.1f)
                .alpha(shown ? 1.0f : 0.0f).setDuration(duration())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (shown) {
                            squadPickerRoot.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!shown) {
                            squadPickerRoot.setVisibility(View.GONE);
                        } else {
                            if (sAdapter.getItemCount() <= 1) {
                                // 拉取远程的小组列表
                                fetchingRemoteSquads(mQueryId);
                            }
                        }
                    }
                }).start();
    }

    private void resetFunction() {
        int color1 = getColor(R.color.colorPrimary), color2 = getColor(R.color.textColorHint);

        function0Text.setTextColor(function == TYPE_DATE ? color1 : color2);
        function0Icon.setTextColor(function == TYPE_DATE ? color1 : color2);

        function1Text.setTextColor(function == TYPE_SQUAD ? color1 : color2);
        function1Icon.setTextColor(function == TYPE_SQUAD ? color1 : color2);
    }

    private void initializeSquadAdapter() {
        if (null == sAdapter) {
            sAdapter = new SquadAdapter();
            squadsView.setLayoutManager(new CustomLinearLayoutManager(squadsView.getContext()));
            squadsView.setAdapter(sAdapter);
            Squad squad = new Squad();
            squad.setId("-");
            squad.setName("全部小组");
            squad.setSelected(true);
            sAdapter.add(squad);
            fetchingRemoteSquads(mQueryId);
        }
    }

    @Override
    protected void onFetchingRemoteSquadsComplete(List<Squad> list) {
        if (null != list && list.size() > 0) {
            for (Squad squad : list) {
                squad.setLocalDeleted(true);
            }
            sAdapter.update(list);
        }
    }

    private void fetchingPaymentCollection() {
        displayLoading(true);
        displayNothing(false);
        PaymentRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Payment>() {
            @Override
            public void onResponse(List<Payment> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (remotePageNumber <= 1) {
                    mAdapter.clear();
                }
                if (success && null != list) {
                    for (Payment payment : list) {
                        payment.setLocalDeleted(true);
                    }
                    mAdapter.update(list);
                }
                int cnt = null == list ? 0 : list.size();
                remotePageNumber += cnt < pageSize ? 0 : 1;
                isLoadingComplete(cnt < pageSize);
                stopRefreshing();
                displayLoading(false);
                displayNothing(mAdapter.getItemCount() <= 0);
            }
        }).collectPayment(mQueryId, searchSquad, searchYear, remotePageNumber);
    }

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            Squad squad = sAdapter.get(index);
            squad.setSelected(!squad.isSelected());
            sAdapter.update(squad);
            String id = squad.isSelected() ? squad.getId() : "";
            if (id.equals("-")) {
                id = "";
            }
            // 重置其他已选中的
            Iterator<Squad> iterator = sAdapter.iterator();
            while (iterator.hasNext()) {
                Squad s = iterator.next();
                if (s.isSelected() && !s.getId().equals(squad.getId())) {
                    s.setSelected(false);
                    sAdapter.update(s);
                }
            }
            if (squad.isSelected() && !searchSquad.equals(id)) {
                fetchingPaymentCollection();
            }
        }
    };

    private class SquadAdapter extends RecyclerViewAdapter<SquadViewHolder, Squad> {
        @Override
        public SquadViewHolder onCreateViewHolder(View itemView, int viewType) {
            SquadViewHolder svh = new SquadViewHolder(itemView, FinanceCollectionFragment.this);
            svh.setOnViewHolderElementClickListener(elementClickListener);
            svh.showPicker(true);
            svh.setUnSelectedColor(R.color.transparent_00);
            return svh;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_group_squad;
        }

        @Override
        public void onBindHolderOfView(SquadViewHolder holder, int position, @Nullable Squad item) {
            holder.showContent(item, "");
        }

        @Override
        protected int comparator(Squad item1, Squad item2) {
            return 0;
        }
    }

    private void initializePaymentAdapter() {
        if (null == mAdapter) {
            mAdapter = new PaymentAdapter();
            mRecyclerView.setAdapter(mAdapter);
            fetchingPaymentCollection();
        }
    }

    private class PaymentAdapter extends RecyclerViewAdapter<FinanceCollectionViewHolder, Payment> {
        @Override
        public FinanceCollectionViewHolder onCreateViewHolder(View itemView, int viewType) {
            return new FinanceCollectionViewHolder(itemView, FinanceCollectionFragment.this);
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_finance_collect_item;
        }

        @Override
        public void onBindHolderOfView(FinanceCollectionViewHolder holder, int position, @Nullable Payment item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(Payment item1, Payment item2) {
            return 0;
        }
    }
}
