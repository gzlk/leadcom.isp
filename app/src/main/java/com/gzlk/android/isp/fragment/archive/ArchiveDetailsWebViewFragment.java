package com.gzlk.android.isp.fragment.archive;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.archive.ArchiveRequest;
import com.gzlk.android.isp.api.archive.CommentRequest;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.archive.ArchiveDetailsCommentViewHolder;
import com.gzlk.android.isp.holder.archive.ArchiveDetailsViewHolder;
import com.gzlk.android.isp.listener.OnViewHolderElementClickListener;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.archive.Comment;

import java.util.List;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/27 14:10 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/27 14:10 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveDetailsWebViewFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_DOC_TYPE = "adwvf_archive_type";

    public static ArchiveDetailsWebViewFragment newInstance(String params) {
        ArchiveDetailsWebViewFragment adwvf = new ArchiveDetailsWebViewFragment();
        Bundle bundle = new Bundle();
        String[] strings = splitParameters(params);
        // 档案id
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        // 档案类型：组织档案或个人档案
        bundle.putInt(PARAM_DOC_TYPE, Integer.valueOf(strings[1]));
        adwvf.setArguments(bundle);
        return adwvf;
    }

    public static void open(BaseFragment fragment, String archiveId, int archiveType) {
        String params = format("%s,%d", archiveId, archiveType);
        fragment.openActivity(ArchiveDetailsWebViewFragment.class.getName(), params, REQUEST_DELETE, true, false);
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        archiveType = bundle.getInt(PARAM_DOC_TYPE, Archive.Type.GROUP);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_DOC_TYPE, archiveType);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nothingMore = Model.getNoMore();
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_archive_details_new;
    }

    @Override
    public void onDestroy() {
        if (null != detailsViewHolder) {
            // 停止播放视频或声音
            detailsViewHolder.onFragmentDestroy();
        }
        super.onDestroy();
    }

    private Model nothingMore;
    private int archiveType;
    private DetailsAdapter mAdapter;
    private ArchiveDetailsViewHolder detailsViewHolder;

    @Override
    protected void onDelayRefreshComplete(int type) {

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
        loadingArchive();
    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    private boolean isGroup() {
        return archiveType == Archive.Type.GROUP;
    }

    private void loadingComments() {
        mAdapter.remove(nothingMore);
        setLoadingText(R.string.ui_text_document_details_loading_comments);
        displayLoading(true);
        CommentRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Comment>() {
            @Override
            public void onResponse(List<Comment> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                displayLoading(false);
                isLoadingComplete(null == list || list.size() < pageSize);
                if (success && null != list) {
                    for (Comment comment : list) {
                        mAdapter.update(comment);
                    }
                }
                stopRefreshing();
                mAdapter.update(nothingMore);
            }
        }).list(isGroup() ? Comment.Type.GROUP : Comment.Type.USER, mQueryId, remotePageNumber);
    }

    private void loadingArchive() {
        setLoadingText(R.string.ui_text_archive_details_loading);
        displayLoading(true);
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                displayLoading(false);
                if (success && null != archive) {
                    mAdapter.update(archive);
                    loadingComments();
                }
            }
        }).find(archiveType, mQueryId, false);
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new DetailsAdapter();
            mRecyclerView.setAdapter(mAdapter);
            loadingArchive();
        }
    }

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {

        }
    };

    private class DetailsAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {
        private static final int VT_ARCHIVE = 0, VT_COMMENT = 1, VT_NOTHING = 2;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            switch (viewType) {
                case VT_ARCHIVE:
                    if (null == detailsViewHolder) {
                        detailsViewHolder = new ArchiveDetailsViewHolder(itemView, ArchiveDetailsWebViewFragment.this);
                        detailsViewHolder.setOnViewHolderElementClickListener(elementClickListener);
                    }
                    return detailsViewHolder;
                case VT_COMMENT:
                    ArchiveDetailsCommentViewHolder adcvh = new ArchiveDetailsCommentViewHolder(itemView, ArchiveDetailsWebViewFragment.this);
                    return adcvh;
            }
            return null;
        }

        @Override
        public int itemLayout(int viewType) {
            switch (viewType) {
                case VT_ARCHIVE:
                    return R.layout.holder_view_archive_details;
                case VT_COMMENT:
                    return R.layout.holder_view_archive_details_comment;
            }
            return 0;
        }

        @Override
        public int getItemViewType(int position) {
            Model model = get(position);
            if (model instanceof Archive) {
                return VT_ARCHIVE;
            } else if (model instanceof Comment) {
                return VT_COMMENT;
            }
            return VT_NOTHING;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof ArchiveDetailsViewHolder) {
                ((ArchiveDetailsViewHolder) holder).showContent((Archive) item);
            } else if (holder instanceof ArchiveDetailsCommentViewHolder) {
                ((ArchiveDetailsCommentViewHolder) holder).showContent((Comment) item);
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
