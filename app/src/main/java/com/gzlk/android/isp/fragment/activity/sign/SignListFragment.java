package com.gzlk.android.isp.fragment.activity.sign;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.activity.ActRequest;
import com.gzlk.android.isp.api.activity.AppSignRecordRequest;
import com.gzlk.android.isp.api.activity.AppSigningRequest;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.activity.SingingViewHolder;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.activity.Activity;
import com.gzlk.android.isp.model.activity.sign.AppSignRecord;
import com.gzlk.android.isp.model.activity.sign.AppSigning;

import java.util.List;

/**
 * <b>功能描述：</b>活动中的签到列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/16 21:54 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/16 21:54 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SignListFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_SELECTED = "slf_selected";

    public static SignListFragment newInstance(String params) {
        SignListFragment slf = new SignListFragment();
        Bundle bundle = new Bundle();
        // 网易云传过来的活动的tid
        bundle.putString(PARAM_QUERY_ID, params);
        slf.setArguments(bundle);
        return slf;
    }

    private String activityId;
    private int selectedIndex = -1;
    private SigningAdapter mAdapter;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        selectedIndex = bundle.getInt(PARAM_SELECTED, -1);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_SELECTED, selectedIndex);
    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    private static final int REQ_CREATOR = ACTIVITY_BASE_REQUEST + 10;

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_activity_sign_creator_fragment_title);
        setRightText(R.string.ui_base_text_launch);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                // 发布一个新的签到应用
                resultSucceededActivity();
                //openActivity(SignCreatorFragment.class.getName(), mQueryId, REQ_CREATOR, true, true);
            }
        });
        setNothingText(R.string.ui_activity_sign_list_nothing);
        initializeAdapter();
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        switch (requestCode) {
            case REQ_CREATOR:
                loadingSignings();
                break;
            case REQUEST_DELETE:
                String id = getResultedData(data);
                AppSigning sign = new AppSigning();
                sign.setId(id);
                mAdapter.remove(sign);
                break;
        }
        super.onActivityResult(requestCode, data);
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
        remotePageNumber = 1;
        loadingSignings();
    }

    @Override
    protected void onLoadingMore() {
        loadingSignings();
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    private void fetchingActivity() {
        if (isEmpty(activityId)) {
            Activity act = Activity.getByTid(mQueryId);
            if (null != act) {
                activityId = act.getId();
            }
        }
    }

    // 远程拉取签到列表
    private void loadingSignings() {
        fetchingActivity();
        setLoadingText(R.string.ui_activity_sign_list_loading);
        displayLoading(true);
        displayNothing(false);
        AppSigningRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<AppSigning>() {
            @Override
            public void onResponse(List<AppSigning> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    if (remotePageNumber <= 1) {
                        mAdapter.clear();
                    }
                    if (null != list) {
                        mAdapter.update(list, false);
                        if (list.size() >= pageSize) {
                            remotePageNumber++;
                        }
                        isLoadingComplete(list.size() < pageSize);
                    } else {
                        isLoadingComplete(true);
                    }
                } else {
                    isLoadingComplete(true);
                }
                displayLoading(false);
                displayNothing(mAdapter.getItemCount() < 1);
                stopRefreshing();
            }
        }).list(activityId, remotePageNumber);
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new SigningAdapter();
            mRecyclerView.setAdapter(mAdapter);
            loadingSignings();
        }
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            selectedIndex = index;
            AppSigning signing = mAdapter.get(index);
            checkMySigningRecords(signing);
        }
    };

    private void checkMySigningRecords(AppSigning signing) {
        String json = Json.gson().toJson(signing, new TypeToken<AppSigning>() {
        }.getType());
        //openActivity(SignDetailsFragment.class.getName(), format("%s,%s", mQueryId, StringHelper.replaceJson(json, false)), REQUEST_DELETE, true, false);
        // 查询本地我是否已经签过到
        AppSignRecord myRecord = AppSignRecord.getMyRecord(signing.getId());
        if (null == myRecord) {
            // 我没有签到或本地没有我的签到记录，拉取该签到应用的签到记录
            loadingRemoteSignRecords(signing.getId(), json);
        } else {
            // 我已签过到，打开签到详情列表
            // 打开查看签到应用详情
            SignDetailsFragment.open(SignListFragment.this, REQUEST_DELETE, mQueryId, json);
        }
    }

    private void loadingRemoteSignRecords(final String signId, final String json) {
        setLoadingText(R.string.ui_activity_sign_details_loading_sign_records);
        displayLoading(true);
        AppSignRecordRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<AppSignRecord>() {
            @Override
            public void onResponse(List<AppSignRecord> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                displayLoading(false);
                if (success) {
                    AppSignRecord record = AppSignRecord.getMyRecord(signId);
                    if (null == record) {
                        // 没有签到，打开签到页面
                        SignFragment.open(SignListFragment.this, mQueryId, signId, "");
                    } else {
                        // 已签到，打开签到记录列表页面
                        SignDetailsFragment.open(SignListFragment.this, REQUEST_DELETE, mQueryId, json);
                    }
                }
            }
        }).list(signId);
    }

    private class SigningAdapter extends RecyclerViewAdapter<SingingViewHolder, AppSigning> {

        @Override
        public SingingViewHolder onCreateViewHolder(View itemView, int viewType) {
            SingingViewHolder holder = new SingingViewHolder(itemView, SignListFragment.this);
            holder.addOnViewHolderClickListener(onViewHolderClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_activity_signing_item;
        }

        @Override
        public void onBindHolderOfView(SingingViewHolder holder, int position, @Nullable AppSigning item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(AppSigning item1, AppSigning item2) {
            return 0;
        }
    }
}
