package com.gzlk.android.isp.fragment.activity.archive;

import android.os.Bundle;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.archive.ArchiveDetailsFragment;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.fragment.organization.archive.ArchiveAdapter;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.organization.Organization;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.List;

/**
 * <b>功能描述：</b>全部未审核的文档列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/24 22:36 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/24 22:36 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ApprovableArchivesFragment extends BaseSwipeRefreshSupportFragment {

    public static final int ALL = 0;
    public static final int ARCHIVES = 1;
    public static final int IMAGES = 2;
    public static final int VIDEOS = 3;

    private static final String PARAM_TYPE = "_aaf_approving_type_";

    public static ApprovableArchivesFragment newInstance(String params) {
        ApprovableArchivesFragment aaf = new ApprovableArchivesFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        bundle.putInt(PARAM_TYPE, Integer.valueOf(strings[1]));
        aaf.setArguments(bundle);
        return aaf;
    }

    private int archiveType = ARCHIVES;
    private ArchiveAdapter mAdapter;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        archiveType = bundle.getInt(PARAM_TYPE, ARCHIVES);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_TYPE, archiveType);
    }

    @Override
    protected void onSwipeRefreshing() {
        stopRefreshing();
    }

    @Override
    protected void onLoadingMore() {
        isLoadingComplete(true);
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    @Override
    public void doingInResume() {
        setNothingText(R.string.ui_archive_approvable_nothing);
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
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    private void loadingLocalArchives() {
        if (archiveType == IMAGES || archiveType == VIDEOS) {
            displayNothing(true);
            return;
        }
        QueryBuilder<Archive> builder = new QueryBuilder<>(Archive.class)
                .whereEquals(Organization.Field.GroupId, mQueryId)
                .appendOrderDescBy(Model.Field.CreateDate);
        List<Archive> list = new Dao<>(Archive.class).query(builder);
        if (null != list) {
            mAdapter.update(list);
        }
        displayNothing(mAdapter.getItemCount() <= 0);
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new ArchiveAdapter(this);
            mAdapter.setOnViewHolderClickListener(viewHolderClickListener);
            mRecyclerView.setAdapter(mAdapter);
        }
        loadingLocalArchives();
    }

    private OnViewHolderClickListener viewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            // 打开详情页
            openActivity(ArchiveDetailsFragment.class.getName(), format("%d,%s", Archive.Type.GROUP, mAdapter.get(index).getId()), true, false);
        }
    };
}
