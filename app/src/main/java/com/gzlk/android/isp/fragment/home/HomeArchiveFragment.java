package com.gzlk.android.isp.fragment.home;

import android.support.annotation.Nullable;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.archive.ArchiveRequest;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.fragment.archive.ArchiveDetailsWebViewFragment;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.holder.archive.ArchiveManagementViewHolder;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.archive.Archive;

import java.util.List;

/**
 * <b>功能描述：</b>首页档案<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/05 15:08 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/05 15:08 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class HomeArchiveFragment extends BaseSwipeRefreshSupportFragment {

    private ArchiveAdapter mAdapter;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        setNothingText(R.string.ui_text_home_archive_nothing);
        initializeAdapter();
    }

    @Override
    protected void onViewPagerDisplayedChanged(boolean visible) {
        super.onViewPagerDisplayedChanged(visible);
        if (visible) {
            fetchingPublicArchives();
        }
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
        fetchingPublicArchives();
    }

    @Override
    protected void onLoadingMore() {
        fetchingPublicArchives();
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    private void fetchingPublicArchives() {
        displayLoading(true);
        displayNothing(false);
        ArchiveRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Archive>() {
            @Override
            public void onResponse(List<Archive> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    if (null != list) {
                        if (list.size() >= pageSize) {
                            remotePageNumber++;
                            isLoadingComplete(false);
                        } else {
                            isLoadingComplete(true);
                        }
                        initializeAdapter();
                        mAdapter.update(list, false);
                        //mAdapter.sort();
                    } else {
                        isLoadingComplete(true);
                    }
                } else {
                    isLoadingComplete(true);
                }
                displayNothing(mAdapter.getItemCount() < 1);
                stopRefreshing();
                displayLoading(false);
            }
        }).listPublic(remotePageNumber);
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            // 到档案详情
            Archive arc = mAdapter.get(index);
            int type = isEmpty(arc.getGroupId()) ? Archive.Type.USER : Archive.Type.GROUP;
            ArchiveDetailsWebViewFragment.open(HomeArchiveFragment.this, arc.getId(), type);
            //openActivity(ArchiveDetailsFragment.class.getName(), format("%d,%s", type, arc.getId()), true, false);
        }
    };

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new ArchiveAdapter();
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private class ArchiveAdapter extends RecyclerViewAdapter<ArchiveManagementViewHolder, Archive> {

        @Override
        public ArchiveManagementViewHolder onCreateViewHolder(View itemView, int viewType) {
            ArchiveManagementViewHolder holder = new ArchiveManagementViewHolder(itemView, HomeArchiveFragment.this);
            holder.addOnViewHolderClickListener(onViewHolderClickListener);
            holder.showStatus(false);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_archive_management;
        }

        @Override
        public void onBindHolderOfView(ArchiveManagementViewHolder holder, int position, @Nullable Archive item) {
            holder.showContent(item, "");
        }

        @Override
        protected int comparator(Archive item1, Archive item2) {
            return -item1.getCreateDate().compareTo(item2.getCreateDate());
        }
    }
}
