package com.gzlk.android.isp.fragment.organization;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.archive.ArchiveRequest;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.fragment.archive.ArchiveCreateSelectorFragment;
import com.gzlk.android.isp.fragment.archive.ArchiveDetailsFragment;
import com.gzlk.android.isp.fragment.archive.ArchiveEditorFragment;
import com.gzlk.android.isp.fragment.main.IndividualFragment;
import com.gzlk.android.isp.fragment.organization.archive.OrgArchiveManagementFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.helper.TooltipHelper;
import com.gzlk.android.isp.holder.archive.ArchiveViewHolder;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.organization.Member;

import java.util.List;

/**
 * <b>功能描述：</b>组织档案列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/05 10:39 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/05 10:39 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchivesFragment extends BaseOrganizationFragment {

    public static ArchivesFragment newInstance(String params) {
        ArchivesFragment af = new ArchivesFragment();
        Bundle bundle = new Bundle();
        // 组织id
        bundle.putString(PARAM_QUERY_ID, params);
        af.setArguments(bundle);
        return af;
    }

    private ArchiveAdapter mAdapter;

    @Override
    protected void onSwipeRefreshing() {
        remotePageNumber = 1;
        setSupportLoadingMore(true);
        //mAdapter.clear();
        fetchingRemoteArchives();
    }

    @Override
    protected void onLoadingMore() {
        fetchingRemoteArchives();
    }

    @Override
    protected String getLocalPageTag() {
        if (StringHelper.isEmpty(mQueryId)) return null;
        return format("af_grp_archive_%s", mQueryId);
    }

    @Override
    public void doingInResume() {
        setLoadingText(R.string.ui_organization_archive_loading);
        setNothingText(R.string.ui_organization_archive_nothing);
        initializeAdapter();
        fetchingRemoteArchives();
    }

    @Override
    protected void onViewPagerDisplayedChanged(boolean visible) {
        super.onViewPagerDisplayedChanged(visible);
        if (visible) {
            refreshArchives();
        }
    }

    private void refreshArchives() {
        if (!StringHelper.isEmpty(mQueryId)) {
            if (isNeedRefresh()) {
                fetchingRemoteArchives();
            }
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

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        switch (requestCode) {
            case REQUEST_DELETE:
                String id = getResultedData(data);
                if (!isEmpty(id)) {
                    Archive archive = new Archive();
                    archive.setId(id);
                    mAdapter.remove(archive);
                }
                break;
            case REQUEST_CHANGE:
            case REQUEST_CREATE:
                // 新增组织档案、组织档案管理页面返回时，重新刷新第一页
                onSwipeRefreshing();
                break;
            case REQUEST_SELECT:
                ArchiveEditorFragment.open(ArchivesFragment.this, mQueryId, getResultedData(data));
                break;
        }
        super.onActivityResult(requestCode, data);
    }

    /**
     * 设置新的组织id并查找该组织的档案列表
     */
    public void setNewQueryId(String queryId) {
        if (!StringHelper.isEmpty(mQueryId) && mQueryId.equals(queryId)) {
            return;
        }
        mQueryId = queryId;
        //fetchingRemoteArchives();
    }

    // 我是否可以管理组织档案
    private boolean isMeCanManageArchives() {
        Member me = StructureFragment.my;
        return null != me && (me.archiveApprovable() || me.isArchiveManager());
    }

    /**
     * 打开新建、管理菜单
     */
    public void openTooltipMenu(View view) {
        int layout = isMeCanManageArchives() ? R.id.ui_tooltip_organization_document_management :
                R.id.ui_tooltip_organization_document_manage_normal;
        showTooltip(view, layout, true, TooltipHelper.TYPE_LEFT, onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ui_tooltip_menu_organization_document_new:
                case R.id.ui_tooltip_menu_organization_document_new_normal:
                    if (isEmpty(mQueryId)) {
                        ToastHelper.make().showMsg(R.string.ui_organization_structure_no_group_exist);
                    } else {
                        // 新建组织档案
                        //openActivity(ArchiveCreatorFragment.class.getName(), format("%d,,%s", Archive.Type.GROUP, mQueryId), REQUEST_CHANGE, true, true);
                        ArchiveCreateSelectorFragment.open(ArchivesFragment.this, mQueryId);
                    }
                    break;
                case R.id.ui_tooltip_menu_organization_document_manage:
                    // 管理组织档案
                    openActivity(OrgArchiveManagementFragment.class.getName(), mQueryId, REQUEST_CHANGE, false, false);
                    break;
            }
        }
    };

    private void fetchingRemoteArchives() {
        displayLoading(true);
        displayNothing(false);
        ArchiveRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Archive>() {
            @Override
            public void onResponse(List<Archive> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    if (null != list) {
                        if (null != mAdapter) {
                            // 覆盖方式重置list
                            mAdapter.update(list, remotePageNumber <= 1);
                            // 第一页的时候排序一下，把最新的放在最前面
                            if (remotePageNumber <= 1) {
                                mAdapter.sort();
                            }
                        }
                        if (list.size() >= pageSize) {
                            remotePageNumber++;
                            isLoadingComplete(false);
                        } else {
                            isLoadingComplete(true);
                        }
                    } else {
                        isLoadingComplete(true);
                    }
                } else {
                    isLoadingComplete(true);
                }
                displayLoading(false);
                stopRefreshing();
                if (null != mAdapter) {
                    displayNothing(mAdapter.getItemCount() < 1);
                }
            }
        }).list(mQueryId, remotePageNumber);
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new ArchiveAdapter();
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private OnViewHolderClickListener viewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            // 打开组织档案详情，一个webview框架
            Archive archive = mAdapter.get(index);
            ArchiveDetailsFragment.open(ArchivesFragment.this, Archive.Type.GROUP, archive.getId(), REQUEST_DELETE);
            //openActivity(ArchiveDetailsFragment.class.getName(), format("%d,%s", Archive.Type.GROUP, archive.getId()), REQUEST_DELETE, true, false);
        }
    };

    private class ArchiveAdapter extends RecyclerViewAdapter<ArchiveViewHolder, Archive> {

        @Override
        public ArchiveViewHolder onCreateViewHolder(View itemView, int viewType) {
            ArchiveViewHolder holder = new ArchiveViewHolder(itemView, ArchivesFragment.this);
            holder.addOnViewHolderClickListener(viewHolderClickListener);
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
            return -item1.getCreateDate().compareTo(item2.getCreateDate());
        }
    }
}
