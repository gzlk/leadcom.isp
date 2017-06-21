package com.gzlk.android.isp.fragment.organization.archive;

import android.os.Bundle;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.activity.ActArchiveRequest;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.activity.ActArchive;
import com.gzlk.android.isp.model.common.Attachment;
import com.gzlk.android.isp.nim.file.FilePreviewHelper;

import java.util.List;

/**
 * <b>功能描述：</b>全部待审核的活动文件列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/24 22:36 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/24 22:36 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ActivityArchivingListFragment extends BaseSwipeRefreshSupportFragment {

    public static final int ALL = 0;
    public static final int ARCHIVES = 1;
    public static final int IMAGES = 2;
    public static final int VIDEOS = 3;

    private static final String PARAM_TYPE = "_aaf_approving_type_";
    private static final String PARAM_EDITING = "aalf_is_it_editing";

    public static ActivityArchivingListFragment newInstance(String params) {
        ActivityArchivingListFragment aaf = new ActivityArchivingListFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        bundle.putInt(PARAM_TYPE, Integer.valueOf(strings[1]));
        aaf.setArguments(bundle);
        return aaf;
    }

    /**
     * 当前查看的档案类别
     */
    private int aType = ALL;
    // 是否在编辑状态
    private boolean isEditing = false;
    private ArchiveAdapter mAdapter;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        aType = bundle.getInt(PARAM_TYPE, ARCHIVES);
        isEditing = bundle.getBoolean(PARAM_EDITING, false);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_TYPE, aType);
        bundle.putBoolean(PARAM_EDITING, isEditing);
    }

    @Override
    protected void onViewPagerDisplayedChanged(boolean visible) {
        super.onViewPagerDisplayedChanged(visible);
        if (visible) {
            loadingArchives();
        }
    }

    @Override
    protected void onSwipeRefreshing() {
        loadingArchives();
    }

    @Override
    protected void onLoadingMore() {
        loadingArchives();
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    @Override
    public void doingInResume() {
        resetLoadingNothingText();
        initializeAdapter();
        if (isViewPagerDisplayedCurrent()) {
            loadingArchives();
        }
    }

    private void resetLoadingNothingText() {
        switch (aType) {
            case ALL:
                setNothingText(R.string.ui_activity_archive_nothing_all);
                setLoadingText(R.string.ui_activity_archive_loading_all);
                break;
            case ARCHIVES:
                setNothingText(R.string.ui_activity_archive_nothing_archive);
                setLoadingText(R.string.ui_activity_archive_loading_archive);
                break;
            case IMAGES:
                setNothingText(R.string.ui_activity_archive_nothing_image);
                setLoadingText(R.string.ui_activity_archive_loading_image);
                break;
            case VIDEOS:
                setNothingText(R.string.ui_activity_archive_nothing_video);
                setLoadingText(R.string.ui_activity_archive_loading_video);
                break;
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
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    /**
     * 加载文档列表
     */
    private void loadingArchives() {
        if (isEmpty(mQueryId)) {
            return;
        }
        displayLoading(true);
        displayNothing(false);
        ActArchiveRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<ActArchive>() {
            @Override
            public void onResponse(List<ActArchive> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    if (null != list) {
                        // 下次拉取下一页
                        if (list.size() >= pageSize) {
                            remotePageNumber++;
                            // 还有下一页
                            isLoadingComplete(false);
                        } else {
                            // 加载完了
                            isLoadingComplete(true);
                        }
                        updateAdapter(list);
                    }
                }
                stopRefreshing();
                displayLoading(false);
                displayNothing(mAdapter.getItemCount() <= 0);
            }
        }).list(mQueryId, aType, remotePageNumber);
    }

    private void updateAdapter(List<ActArchive> list) {
        for (ActArchive archive : list) {
            //archive.setSelectable(true);
            mAdapter.update(archive);
        }
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new ArchiveAdapter(this);
            mAdapter.setOnViewHolderClickListener(viewHolderClickListener);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private OnViewHolderClickListener viewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            // 点击一个审批一个
//            ToastHelper.make().showMsg("预览文件");
            ActArchive archive = (ActArchive) mAdapter.get(index);
            if (isEditing) {
                if (archive.getStatus() == Attachment.AttachmentStatus.ARCHIVING) {
                    archive.setSelected(true);
                    mAdapter.notifyItemChanged(index);
                }
            } else {
                String url = archive.getUrl();
                String name = archive.getName();
                String ext = Attachment.getExtension(url.contains("netease.com") ? name : url);
                FilePreviewHelper.previewFile(Activity(), url, name, ext);
            }
            //openActivity(ArchiveDetailsFragment.class.getName(), format("%d,%s", Archive.Type.GROUP, mAdapter.get(index).getId()), true, false);
        }
    };


}
