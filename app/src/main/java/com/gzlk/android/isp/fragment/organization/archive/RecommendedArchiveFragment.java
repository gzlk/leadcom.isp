package com.gzlk.android.isp.fragment.organization.archive;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.bumptech.glide.util.Util;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.archive.RecommendArchiveRequest;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.archive.ArchiveDetailsFragment;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.archive.ArchiveRecommendViewHolder;
import com.gzlk.android.isp.listener.OnHandleBoundDataListener;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.archive.RecommendArchive;

import java.util.List;

/**
 * <b>功能描述：</b>组织内推荐档案列表(管理员可以看得见)<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/17 10:01 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/17 10:01 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class RecommendedArchiveFragment extends BaseSwipeRefreshSupportFragment {

    public static RecommendedArchiveFragment newInstance(String params) {
        RecommendedArchiveFragment raf = new RecommendedArchiveFragment();
        Bundle bundle = new Bundle();
        // 传入的组织 id
        bundle.putString(PARAM_QUERY_ID, params);
        raf.setArguments(bundle);
        return raf;
    }

    private RecommendAdapter mAdapter;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        initializeAdapter();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    protected void onSwipeRefreshing() {
        remotePageNumber = 1;
        setSupportLoadingMore(true);
        loadingRecommended();
    }

    @Override
    protected void onLoadingMore() {
        loadingRecommended();
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    private void loadingRecommended() {
        displayLoading(true);
        displayNothing(false);
        RecommendArchiveRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<RecommendArchive>() {
            @Override
            public void onResponse(List<RecommendArchive> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    if (remotePageNumber <= 1) {
                        mAdapter.clear();
                    }
                    if (null != list) {
                        if (list.size() >= pageNumber) {
                            remotePageNumber++;
                            isLoadingComplete(false);
                        } else {
                            isLoadingComplete(true);
                        }
                        mAdapter.update(list, false);
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
        }).list(mQueryId, remotePageNumber);
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            setNothingText(R.string.ui_archive_recommend_nothing);
            setLoadingText(R.string.ui_archive_recommend_loading);
            mAdapter = new RecommendAdapter();
            mRecyclerView.setAdapter(mAdapter);
            loadingRecommended();
        }
    }

    // 点击打开档案详情
    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            RecommendArchive archive = mAdapter.get(index);
            ArchiveDetailsFragment.open(RecommendedArchiveFragment.this, (null == archive.getUserDoc() ? Archive.Type.GROUP : Archive.Type.USER), archive.getDocId(), REQUEST_CHANGE);
        }
    };

    // 推荐或取消推荐
    private OnHandleBoundDataListener<RecommendArchive> onHandleBoundDataListener = new OnHandleBoundDataListener<RecommendArchive>() {
        @Override
        public RecommendArchive onHandlerBoundData(BaseViewHolder holder) {
            RecommendArchive archive = mAdapter.get(holder.getAdapterPosition());
            recommendArchive(archive);
            return null;
        }
    };

    private long getArchiveContentLength(String content) {
        String html = Utils.clearHtml(content);
        if (isEmpty(html)) return 0;
        return html.length();
    }

    private void recommendArchive(RecommendArchive archive) {
        Archive doc = null == archive.getUserDoc() ? archive.getGroDoc() : archive.getUserDoc();
        if (null != doc) {
            long len = getArchiveContentLength(doc.getContent());
            if (len < 70) {
                ToastHelper.make().showMsg(R.string.ui_archive_recommend_archive_content_too_short);
            }else{

            }
        } else {
            ToastHelper.make().showMsg(R.string.ui_archive_recommend_archive_null);
        }
    }

    private class RecommendAdapter extends RecyclerViewAdapter<ArchiveRecommendViewHolder, RecommendArchive> {
        @Override
        public ArchiveRecommendViewHolder onCreateViewHolder(View itemView, int viewType) {
            ArchiveRecommendViewHolder holder = new ArchiveRecommendViewHolder(itemView, RecommendedArchiveFragment.this);
            holder.addOnViewHolderClickListener(onViewHolderClickListener);
            holder.addOnHandlerBoundDataListener(onHandleBoundDataListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_archive_recommend;
        }

        @Override
        public void onBindHolderOfView(ArchiveRecommendViewHolder holder, int position, @Nullable RecommendArchive item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(RecommendArchive item1, RecommendArchive item2) {
            return 0;
        }
    }
}
