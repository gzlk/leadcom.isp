package com.gzlk.android.isp.fragment.organization;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.archive.ArchiveRequest;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.fragment.archive.ArchiveDetailsFragment;
import com.gzlk.android.isp.fragment.archive.ArchiveNewFragment;
import com.gzlk.android.isp.fragment.organization.archive.OrgArchiveManagementFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.helper.TooltipHelper;
import com.gzlk.android.isp.holder.archive.ArchiveViewHolder;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.organization.Member;
import com.gzlk.android.isp.model.organization.Organization;

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
        bundle.putString(PARAM_QUERY_ID, params);
        af.setArguments(bundle);
        return af;
    }

    private ArchiveAdapter mAdapter;

    @Override
    protected void onSwipeRefreshing() {
        fetchingRemoteArchives();
    }

    @Override
    protected void onLoadingMore() {
        isLoadingComplete(true);
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
        if (!StringHelper.isEmpty(mQueryId)) {
            if (isNeedRefresh()) {
                fetchingRemoteArchives();
            }
            loadingLocalArchive();
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
     * 设置新的组织id并查找该组织的档案列表
     */
    public void setNewQueryId(String queryId) {
        if (!StringHelper.isEmpty(mQueryId) && mQueryId.equals(queryId)) {
            return;
        }
        mQueryId = queryId;
        loadingLocalArchive();
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
        showTooltip(view, layout, true, TooltipHelper.TYPE_RIGHT, onClickListener);
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
                        openActivity(ArchiveNewFragment.class.getName(), format("%d,,%s", Archive.Type.GROUP, mQueryId), true, true);
                    }
                    break;
                case R.id.ui_tooltip_menu_organization_document_manage:
                    // 管理组织档案
                    openActivity(OrgArchiveManagementFragment.class.getName(), mQueryId, false, false);
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
                        mAdapter.update(list);
                        mAdapter.sort();
                    }
                }
                displayLoading(false);
                stopRefreshing();
                displayNothing(mAdapter.getItemCount() < 1);
            }
        }).list(mQueryId, remotePageNumber);
    }

    private Dao<Archive> dao;

    private void loadingLocalArchive() {
        initializeAdapter();
        if (null == dao) {
            dao = new Dao<>(Archive.class);
        }
        List<Archive> temp = dao.query(Organization.Field.GroupId, mQueryId);
        if (null != temp) {
            mAdapter.update(temp);
            mAdapter.sort();
        }
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new ArchiveAdapter();
        }
        if (null != mRecyclerView) {
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private OnViewHolderClickListener viewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            // 打开组织档案详情，一个webview框架
            Archive archive = mAdapter.get(index);
            openActivity(ArchiveDetailsFragment.class.getName(), format("%d,%s", Archive.Type.GROUP, archive.getId()), true, false);
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
