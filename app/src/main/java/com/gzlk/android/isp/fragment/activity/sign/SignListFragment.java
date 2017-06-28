package com.gzlk.android.isp.fragment.activity.sign;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.activity.ActRequest;
import com.gzlk.android.isp.api.activity.AppSigningRequest;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.activity.SingingViewHolder;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.activity.Activity;
import com.gzlk.android.isp.model.activity.AppSigning;

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

    public static SignListFragment newInstance(String params) {
        SignListFragment slf = new SignListFragment();
        Bundle bundle = new Bundle();
        // 网易云传过来的活动的tid
        bundle.putString(PARAM_QUERY_ID, params);
        slf.setArguments(bundle);
        return slf;
    }

    private String activityId;
    private SigningAdapter mAdapter;

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
        setLoadingText(R.string.ui_activity_sign_list_loading);
        setNothingText(R.string.ui_activity_sign_list_nothing);
        initializeAdapter();
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == REQ_CREATOR) {
            loadingSignings();
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
        loadingSignings();
    }

    @Override
    protected void onLoadingMore() {
        isLoadingComplete(true);
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    private void fetchingActivity() {
        ActRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Activity>() {
            @Override
            public void onResponse(Activity activity, boolean success, String message) {
                super.onResponse(activity, success, message);
                if (success && null != activity) {
                    activityId = activity.getId();
                    loadingSignings();
                } else {
                    ToastHelper.make().showMsg(message);
                }
            }
        }).findByTid(mQueryId);
    }

    // 远程拉取签到列表
    private void loadingSignings() {
        displayLoading(true);
        displayNothing(false);
        if (isEmpty(activityId)) {
            fetchingActivity();
        } else {
            AppSigningRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<AppSigning>() {
                @Override
                public void onResponse(List<AppSigning> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                    super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                    if (success) {
                        if (null != list) {
                            mAdapter.update(list, false);
                        }
                    }
                    displayLoading(false);
                    displayNothing(mAdapter.getItemCount() < 1);
                    stopRefreshing();
                }
            }).list(activityId);
        }
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
            AppSigning signing = mAdapter.get(index);
            String json = Json.gson().toJson(signing, new TypeToken<AppSigning>() {
            }.getType());
            // 打开查看签到应用详情
            openActivity(SignDetailsFragment.class.getName(), format("%s,%s", mQueryId, StringHelper.replaceJson(json, false)), true, false);
        }
    };

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
