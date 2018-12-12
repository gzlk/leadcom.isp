package com.leadcom.android.isp.fragment.individual.moment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.user.MomentRequest;
import com.leadcom.android.isp.api.user.UserMsgRequest;
import com.leadcom.android.isp.api.user.UserRequest;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.fragment.individual.UserMessageFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.individual.MomentViewHolder;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.user.Moment;
import com.leadcom.android.isp.model.user.User;

import java.util.ArrayList;
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

    public static void open(BaseFragment fragment, String userId) {
        fragment.openActivity(MomentListFragment.class.getName(), userId, REQUEST_CHANGE, true, false);
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
        fetchingMoments();
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        switch (requestCode) {
            case REQUEST_DELETE:
                // 删除
                Moment moment = new Moment();
                moment.setId(getResultedData(data));
                mAdapter.remove(moment);
                mAdapter.notifyDataSetChanged();
                break;
        }
        super.onActivityResult(requestCode, data);
    }

    private void fetchingMoments() {
        setLoadingText(R.string.ui_individual_moment_list_loading);
        //displayLoading(true);
        displayNothing(false);
        MomentRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Moment>() {
            @Override
            public void onResponse(List<Moment> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                int size = success ? (null == list ? 0 : list.size()) : 0;
                isLoadingComplete(size < pageSize);
                remotePageNumber += (size >= pageSize ? 1 : 0);
                if (success && null != list) {
                    mAdapter.update(list, false);
                    mAdapter.sort();
                }
                displayLoading(false);
                stopRefreshing();
                displayNothing(mAdapter.getItemCount() < 1);
            }
        }).list(mQueryId, remotePageNumber);
    }

    private void fetchUser() {
        UserRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<User>() {
            @Override
            public void onResponse(User user, boolean success, String message) {
                super.onResponse(user, success, message);
                if (success) {
                    if (null != user) {
                        setCustomTitle(StringHelper.getString(R.string.ui_individual_moment_list_fragment_title, user.getName()));
                    } else {
                        ToastHelper.helper().showMsg(R.string.ui_individual_moment_list_user_not_exists);
                    }
                }
            }
        }).find(mQueryId, "", true);
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            // 打开详情页了
            Moment moment = mAdapter.get(index);
            if (moment.getId().equals(today().getId())) {
                openImageSelector(true);
            } else {
                if (moment.getImage().size() < 1) {
                    // 没有图片，直接打开说说详情页
                    MomentDetailsFragment.open(MomentListFragment.this, moment.getId());
                } else {
                    // 默认打开第一个图片
                    MomentImagesFragment.open(MomentListFragment.this, moment.getId(), 0);
                }
            }
        }
    };

    private MomentViewHolder.OnGotPositionListener gotPositionListener = new MomentViewHolder.OnGotPositionListener() {
        @Override
        public Moment previous(int myPosition) {
            if (myPosition <= 0) return null;
            return mAdapter.get(myPosition - 1);
        }
    };

    private Moment cameraMoment;

    // 今天
    private Moment today() {
        if (null == cameraMoment) {
            cameraMoment = new Moment();
            cameraMoment.setId(getString(R.string.ui_text_moment_item_default_today));
        }
        // 设置时间为今天最后一秒，在排序时会一直排在最前面
        cameraMoment.setCreateDate(Utils.formatDateOfNow("yyyy-MM-dd 23:59:59"));
        return cameraMoment;
    }

    private OnImageSelectedListener imageSelectedListener = new OnImageSelectedListener() {
        @Override
        public void onImageSelected(ArrayList<String> selected) {
            // 打开新建动态页面
            MomentCreatorFragment.open(MomentListFragment.this, Json.gson().toJson(selected));
            getWaitingForUploadFiles().clear();
        }
    };

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new MomentAdapter();
            mRecyclerView.setAdapter(mAdapter);
            if (mQueryId.equals(Cache.cache().userId)) {
                mAdapter.add(today());
                setCustomTitle(StringHelper.getString(R.string.ui_individual_moment_list_fragment_title, StringHelper.getString(R.string.ui_base_text_myself)));
                // 这里不需要直接上传，只需要把选择的图片传递给新建动态页面即可，上传在那里实现
                isSupportDirectlyUpload = false;
                // 添加图片选择
                addOnImageSelectedListener(imageSelectedListener);
                setRightIcon(R.string.ui_icon_comment);
                setRightTitleClickListener(new OnTitleButtonClickListener() {
                    @Override
                    public void onClick() {
                        // 用户动态相关的消息
                        UserMessageFragment.open(MomentListFragment.this, UserMsgRequest.TYPE_MOMENT);
                    }
                });
            } else {
                fetchUser();
            }
            fetchingMoments();
        }
    }

    private class MomentAdapter extends RecyclerViewAdapter<MomentViewHolder, Moment> {
        @Override
        public MomentViewHolder onCreateViewHolder(View itemView, int viewType) {
            MomentViewHolder holder = new MomentViewHolder(itemView, MomentListFragment.this);
            holder.addOnViewHolderClickListener(onViewHolderClickListener);
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
