package com.gzlk.android.isp.fragment.organization.archive;

import android.os.Bundle;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.archive.ArchiveRequest;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.fragment.archive.ArchiveDetailsFragment;
import com.gzlk.android.isp.fragment.organization.BaseOrganizationFragment;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.archive.Archive;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能描述：</b>管理档案列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/31 02:08 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/31 02:08 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveListFragment extends BaseOrganizationFragment {

    /**
     * 所有档案
     */
    public static final int TYPE_ALL = 1;
    /**
     * 待存档档案
     */
    public static final int TYPE_ARCHIVING = 2;
    /**
     * 待审核档案
     */
    public static final int TYPE_APPROVING = 3;

    private static final String PARAM_TYPE = "alf_list_type";
    private static final String PARAM_SEARCHING_TEXT = "alf_searching_text";
    private static final String PARAM_LAST_SEARCHING = "alf_last_searching";

    public static ArchiveListFragment newInstance(String params) {
        ArchiveListFragment alf = new ArchiveListFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        // 组织id
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        // 列表类型
        bundle.putInt(PARAM_TYPE, Integer.valueOf(strings[1]));
        alf.setArguments(bundle);
        return alf;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mType = bundle.getInt(PARAM_TYPE, TYPE_ALL);
        searchingText = bundle.getString(PARAM_SEARCHING_TEXT, "");
        lastSearching = bundle.getString(PARAM_LAST_SEARCHING, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_TYPE, mType);
        bundle.putString(PARAM_SEARCHING_TEXT, searchingText);
        bundle.putString(PARAM_LAST_SEARCHING, lastSearching);
    }

    private int mType;
    private String searchingText = "", lastSearching = "";
    private ArrayList<Archive> archives = new ArrayList<>();
    private ArchiveAdapter mAdapter;

    public void onSearching(String text) {
        searchingText = isEmpty(text) ? "" : text;
        if (getUserVisibleHint()) {
            // 只有当前显示的是本fragment时才进行列表操作
            loadingArchive();
        }
    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        setNothingText(R.string.ui_archive_approvable_nothing);
        initializeAdapter();
        if (isViewPagerDisplayedCurrent()) {
            archives.clear();
            loadingArchive();
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
        archives.clear();
        refreshArchive();
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
    protected void onViewPagerDisplayedChanged(boolean visible) {
        super.onViewPagerDisplayedChanged(visible);
        if (visible) {
            // 变为可见状态时才查询列表并显示
            if (mType > 0) {
                loadingArchive();
            }
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
            Archive acv = (Archive) mAdapter.get(index);
            if (mType == TYPE_ARCHIVING) {
                // 待存档档案，打开活动存档页面（2017-06-21 11:30）
                openActivity(ActivityArchivingManagementFragment.class.getName(), format("%s,%s", acv.getActId(), acv.getId()), true, false);
                // 只有活动的档案才会出现未存档
                // 打开未存档档案页，将其存档
//                String json = Json.gson().toJson(acv, new TypeToken<Archive>() {
//                }.getType());
//                openActivity(ArchiveHandlerFragment.class.getName(), format("%d,%s,%s", ArchiveHandlerFragment.TYPE_ARCHIVE, acv.getId(), replaceJson(json, false)), true, false);
                // 打开档案详细页，存档或
                //openActivity(ActivityArchivingManagementFragment.class.getName(), acv.getActId(), true, false);
                //openActivity(ArchiveDetailsFragment.class.getName(), format("%d,%s", Archive.Type.GROUP, acv.getId()), true, false);
            } else if (mType == TYPE_APPROVING) {
                // 待审核档案
                openActivity(ArchiveHandlerFragment.class.getName(), format("%d,%s,", ArchiveHandlerFragment.TYPE_APPROVE, acv.getId()), true, false);
            } else {
                // 全部页面中，打开档案详情页
                openActivity(ArchiveDetailsFragment.class.getName(), format("%d,%s", Archive.Type.GROUP, acv.getId()), true, false);
            }
        }
    };

    private void showArchive() {
        if (!lastSearching.equals(searchingText)) {
            // 如果最后一次搜索条件跟当前搜索条件不一样则清空列表
            lastSearching = searchingText;
            mAdapter.clear();
        }
        for (Archive archive : archives) {
            if (isEmpty(searchingText)) {
                mAdapter.update(archive);
            } else {
                if (archive.getTitle().contains(searchingText)) {
                    mAdapter.update(archive);
                }
            }
        }
        stopRefreshing();
        displayNothing(mAdapter.getItemCount() < 1);
    }

    private void loadingArchive() {
        if (archives.size() < 1) {
            refreshArchive();
        } else {
            showArchive();
        }
    }

    private void refreshArchive() {
        displayNothing(false);
        switch (mType) {
            case TYPE_ALL:
                setNothingText(R.string.ui_archive_management_nothing_1);
                setLoadingText(R.string.ui_archive_management_loading_1);
                loadingAllArchive();
                break;
            case TYPE_ARCHIVING:
                setNothingText(R.string.ui_archive_management_nothing_2);
                setLoadingText(R.string.ui_archive_management_loading_2);
                loadingArchivingArchive();
                break;
            case TYPE_APPROVING:
                setNothingText(R.string.ui_archive_management_nothing_3);
                setLoadingText(R.string.ui_archive_management_loading_3);
                loadingApprovingArchive();
                break;
        }
    }

    private void addMultipleArchive(List<Archive> list, int pageSize) {
        if (null != list) {
            if (pageSize > 0) {
                if (list.size() >= pageSize) {
                    remotePageNumber++;
                }
            }
            for (Archive archive : list) {
                if (!archives.contains(archive)) {
                    archives.add(archive);
                }
            }
        }
        if (null == list || list.size() < 1) {
            mAdapter.clear();
        }
        showArchive();
    }

    // 加载全部档案
    private void loadingAllArchive() {
        displayLoading(true);
        ArchiveRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Archive>() {
            @Override
            public void onResponse(List<Archive> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    addMultipleArchive(list, pageSize);
                }
                displayLoading(false);
            }
        }).list(mQueryId, remotePageNumber);
    }

    // 未存档列表
    private void loadingArchivingArchive() {
        displayLoading(true);
        ArchiveRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Archive>() {
            @Override
            public void onResponse(List<Archive> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    addMultipleArchive(list, pageSize);
                }
                displayLoading(false);
            }
        }).archiveList(mQueryId, remotePageNumber);
    }

    // 未审核列表
    private void loadingApprovingArchive() {
        displayLoading(true);
        ArchiveRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Archive>() {
            @Override
            public void onResponse(List<Archive> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    addMultipleArchive(list, pageSize);
                }
                displayLoading(false);
            }
        }).approveList(mQueryId, remotePageNumber);
    }
}
