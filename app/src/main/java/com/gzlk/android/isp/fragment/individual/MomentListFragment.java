package com.gzlk.android.isp.fragment.individual;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.user.MomentRequest;
import com.gzlk.android.isp.api.user.UserRequest;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.individual.MomentViewHolder;
import com.gzlk.android.isp.model.user.Moment;
import com.gzlk.android.isp.model.user.User;

import java.util.List;

/**
 * <b>功能描述：</b>查看用户的动态说说列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/03 21:17 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/03 21:17 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MomentListFragment extends BaseSwipeRefreshSupportFragment {

    public static MomentListFragment newInstance(String params) {
        MomentListFragment mlf = new MomentListFragment();
        Bundle bundle = new Bundle();
        // 用户的id
        bundle.putString(PARAM_QUERY_ID, params);
        mlf.setArguments(bundle);
        return mlf;
    }

    private MomentAdapter mAdapter;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        setNothingText(R.string.ui_individual_moment_list_nothing);
        initializeAdapter();
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
        fetchingMoments();
    }

    @Override
    protected void onLoadingMore() {
        isLoadingComplete(true);
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    private void fetchingMoments() {
        setLoadingText(R.string.ui_individual_moment_list_loading);
        displayLoading(true);
        displayNothing(false);
        MomentRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Moment>() {
            @Override
            public void onResponse(List<Moment> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    if (null != list) {
                        if (list.size() >= pageSize) {
                            remotePageNumber++;
                        }
                        mAdapter.update(list, false);
                        mAdapter.sort();
                    }
                }
                displayLoading(false);
                stopRefreshing();
                displayNothing(mAdapter.getItemCount() < 1);
            }
        }).list(Cache.cache().accessToken, remotePageNumber);
    }

    private void fetchUser() {
        UserRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<User>() {
            @Override
            public void onResponse(User user, boolean success, String message) {
                super.onResponse(user, success, message);
                if (success) {
                    if (null != user) {
                        setCustomTitle(user.getName());
                    } else {
                        ToastHelper.make().showMsg(R.string.ui_individual_moment_list_user_not_exists);
                    }
                }
            }
        }).find(mQueryId, true);
    }

    private BaseViewHolder.OnHandlerBoundDataListener<Moment> onHandlerBoundDataListener = new BaseViewHolder.OnHandlerBoundDataListener<Moment>() {
        @Override
        public Moment onHandlerBoundData(BaseViewHolder holder) {
            return mAdapter.get(holder.getAdapterPosition());
        }
    };

    private MomentViewHolder.OnGotPositionListener gotPositionListener = new MomentViewHolder.OnGotPositionListener() {
        @Override
        public Moment previous(int myPosition) {
            if (myPosition <= 0) return null;
            return mAdapter.get(myPosition - 1);
        }
    };

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new MomentAdapter();
            mRecyclerView.setAdapter(mAdapter);
            fetchUser();
            fetchingMoments();
        }
    }

    private class MomentAdapter extends RecyclerViewAdapter<MomentViewHolder, Moment> {
        @Override
        public MomentViewHolder onCreateViewHolder(View itemView, int viewType) {
            MomentViewHolder holder = new MomentViewHolder(itemView, MomentListFragment.this);
            holder.addOnHandlerBoundDataListener(onHandlerBoundDataListener);
            holder.addOnGotPositionListener(gotPositionListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_moment;
        }

        @Override
        public void onBindHolderOfView(MomentViewHolder holder, int position, @Nullable Moment item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(Moment item1, Moment item2) {
            // 倒序排序
            return -item1.getCreateDate().compareTo(item2.getCreateDate());
        }
    }
}
