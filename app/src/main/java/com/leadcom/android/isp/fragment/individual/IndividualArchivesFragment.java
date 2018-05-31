package com.leadcom.android.isp.fragment.individual;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.archive.ArchiveRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.fragment.archive.ArchiveDetailsFragment;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.archive.ArchiveViewHolder;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.user.User;

import java.util.List;

/**
 * <b>功能描述：</b>查看个人档案列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/09/27 10:19 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/09/27 10:19 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class IndividualArchivesFragment extends BaseSwipeRefreshSupportFragment {

    public static IndividualArchivesFragment newInstance(String params) {
        IndividualArchivesFragment af = new IndividualArchivesFragment();
        Bundle bundle = new Bundle();
        // 传过来的用户id
        bundle.putString(PARAM_QUERY_ID, params);
        af.setArguments(bundle);
        return af;
    }

    public static void open(BaseFragment fragment, String userId, int req) {
        fragment.openActivity(IndividualArchivesFragment.class.getName(), userId, req, true, false);
    }

    private ArchiveAdapter mAdapter;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
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
        remotePageNumber = 1;
        fetchingArchives();
    }

    @Override
    protected void onLoadingMore() {
        fetchingArchives();
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    private void fetchingArchives() {
        displayLoading(true);
        displayNothing(false);
        ArchiveRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Archive>() {
            @Override
            public void onResponse(List<Archive> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    if (remotePageNumber <= 1) {
                        mAdapter.clear();
                    }
                    if (null != list) {
                        remotePageNumber += list.size() >= pageSize ? 1 : 0;
                        isLoadingComplete(list.size() < pageSize);
                        mAdapter.update(list, false);
                    } else {
                        isLoadingComplete(true);
                    }
                } else {
                    isLoadingComplete(true);
                }
                stopRefreshing();
                displayLoading(false);
                displayNothing(mAdapter.getItemCount() < 1);
            }
        }).list(remotePageNumber, mQueryId);
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            User user = User.get(mQueryId);
            setCustomTitle(StringHelper.getString(R.string.ui_individual_archive_list_fragment_title, null == user ? "-" : user.getName()));
            setLoadingText(R.string.ui_individual_archive_list_loading);
            setNothingText(R.string.ui_individual_archive_list_nothing);
            mAdapter = new ArchiveAdapter();
            mRecyclerView.setAdapter(mAdapter);
            fetchingArchives();
        }
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        switch (requestCode) {
            case REQUEST_DELETE:
                // 上层返回的有更改的或删除的
                String id = getResultedData(data);
                Model result = getResultModel(data, RESULT_ARCHIVE);
                if (null != result) {
                    mAdapter.update((Archive) result);
                }
                break;
        }
        super.onActivityResult(requestCode, data);
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            // 打开档案详情页
            Archive archive = mAdapter.get(index);
            ArchiveDetailsFragment.open(IndividualArchivesFragment.this, archive);
        }
    };

    private class ArchiveAdapter extends RecyclerViewAdapter<ArchiveViewHolder, Archive> {

        @Override
        public ArchiveViewHolder onCreateViewHolder(View itemView, int viewType) {
            ArchiveViewHolder holder = new ArchiveViewHolder(itemView, IndividualArchivesFragment.this);
            holder.addOnViewHolderClickListener(onViewHolderClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_document;
        }

        @Override
        public void onBindHolderOfView(ArchiveViewHolder holder, int position, @Nullable Archive item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(Archive item1, Archive item2) {
            return 0;
        }
    }
}
