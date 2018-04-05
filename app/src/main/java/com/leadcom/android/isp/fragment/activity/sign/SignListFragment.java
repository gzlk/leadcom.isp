package com.leadcom.android.isp.fragment.activity.sign;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.activity.AppSigningRecordRequest;
import com.leadcom.android.isp.api.activity.AppSigningRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.holder.activity.SingingViewHolder;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.activity.sign.AppSignRecord;
import com.leadcom.android.isp.model.activity.sign.AppSigning;
import com.leadcom.android.isp.view.SwipeItemLayout;
import com.netease.nim.uikit.impl.cache.TeamDataCache;
import com.netease.nimlib.sdk.team.constant.TeamMemberType;
import com.netease.nimlib.sdk.team.model.TeamMember;

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

public class SignListFragment extends BaseSignFragment {

    private static final String PARAM_OWNER = "slf_owner";

    public static SignListFragment newInstance(Bundle bundle) {
        SignListFragment slf = new SignListFragment();
        slf.setArguments(bundle);
        return slf;
    }

    public static void open(Context context, String tid, int requestCode) {
        BaseActivity.openActivity(context, SignListFragment.class.getName(), getBundle(tid), requestCode, true, true);
    }

    private boolean isSelfOwner = false;
    private SigningAdapter mAdapter;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        isSelfOwner = bundle.getBoolean(PARAM_OWNER, false);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putBoolean(PARAM_OWNER, isSelfOwner);
    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_activity_sign_creator_fragment_title);
        setRightText(R.string.ui_base_text_launch);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                // 发布一个新的签到应用
                resultSucceededActivity();
            }
        });
        setNothingText(R.string.ui_activity_sign_list_nothing);
        initializeAdapter();
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        switch (requestCode) {
            case REQUEST_DELETE:
                String id = getResultedData(data);
                mAdapter.remove(id);
                break;
        }
        super.onActivityResult(requestCode, data);
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

    // 远程拉取签到列表
    private void loadingSignings() {
        setLoadingText(R.string.ui_activity_sign_list_loading);
        displayLoading(true);
        displayNothing(false);
        AppSigningRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<AppSigning>() {
            @Override
            public void onResponse(List<AppSigning> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (remotePageNumber <= 1) {
                    mAdapter.clear();
                }
                int size = null == list ? 0 : list.size();
                isLoadingComplete(size < pageSize);
                remotePageNumber += size < pageSize ? 0 : 1;
                if (success && null != list) {
                    mAdapter.update(list, false);
                }
                displayLoading(false);
                displayNothing(mAdapter.getItemCount() < 1);
                stopRefreshing();
            }
        }).listTeamSinging(mQueryId, remotePageNumber);
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            TeamMember member = TeamDataCache.getInstance().getTeamMember(mQueryId, Cache.cache().userId);
            isSelfOwner = member.getType() == TeamMemberType.Owner;
            mAdapter = new SigningAdapter();
            mRecyclerView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(Activity()));
            mRecyclerView.setAdapter(mAdapter);
            loadingSignings();
        }
    }

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            switch (view.getId()) {
                case R.id.ui_tool_view_contact_button2:
                    warningDelete(mAdapter.get(index).getId());
                    break;
                default:
                    checkMySigningRecords(mAdapter.get(index));
                    break;
            }
        }
    };

    @Override
    protected void onDeleteSigningComplete(boolean success, String signingId) {
        if (success) {
            mAdapter.remove(signingId);
        }
        displayNothing(mAdapter.getItemCount() < 1);
    }

    private void checkMySigningRecords(AppSigning signing) {
        String json = AppSigning.toJson(signing);
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
        AppSigningRecordRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<AppSignRecord>() {
            @Override
            public void onResponse(List<AppSignRecord> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                displayLoading(false);
                if (success) {
                    AppSignRecord record = AppSignRecord.getMineRecord(signId);
                    if (null == record) {
                        // 没有签到，打开签到页面
                        SignFragment.open(SignListFragment.this, mQueryId, signId, "");
                    } else {
                        // 已签到，打开签到记录列表页面
                        SignDetailsFragment.open(SignListFragment.this, REQUEST_DELETE, mQueryId, json);
                    }
                }
            }
        }).listTeamSignRecord(signId);
    }

    private class SigningAdapter extends RecyclerViewAdapter<SingingViewHolder, AppSigning> {

        @Override
        public SingingViewHolder onCreateViewHolder(View itemView, int viewType) {
            SingingViewHolder holder = new SingingViewHolder(itemView, SignListFragment.this);
            holder.setOnViewHolderElementClickListener(elementClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return isSelfOwner ? R.layout.holder_view_activity_signing_item_deletable : R.layout.holder_view_activity_signing_item;
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
