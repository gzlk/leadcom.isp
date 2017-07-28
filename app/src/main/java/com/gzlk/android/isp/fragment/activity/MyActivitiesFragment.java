package com.gzlk.android.isp.fragment.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.activity.ActRequest;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.holder.activity.ActivityManagementViewHolder;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.activity.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能描述：</b>活动管理列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/30 10:23 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/30 10:23 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MyActivitiesFragment extends BaseSwipeRefreshSupportFragment {

    public static final int TYPE_JOINED = 1;
    public static final int TYPE_CREATED = 2;
    public static final int TYPE_NO_JOIN = 3;
    public static final int TYPE_ENDED = 4;

    private static final String PARAM_TYPE = "maf_param_list_type";
    private static final String PARAM_SEARCHING_TEXT = "maf_searching_text";
    private static final String PARAM_LAST_SEARCHING = "maf_last_searching";

    public static MyActivitiesFragment newInstance(String params) {
        MyActivitiesFragment maf = new MyActivitiesFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        // 组织的id
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        // 查询类型
        bundle.putInt(PARAM_TYPE, Integer.valueOf(strings[1]));
        maf.setArguments(bundle);
        return maf;
    }

    private String searchingText = "", lastSearching = "";

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mType = bundle.getInt(PARAM_TYPE, TYPE_JOINED);
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

    private int mType = 0;
    // 从远程服务器拉取回来的活动列表，此时不能保存到本地
    private ArrayList<Activity> activities = new ArrayList<>();
    private ActivityAdapter mAdapter;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        initializeAdapter();
        if (isViewPagerDisplayedCurrent()) {
            loadingActivity();
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
        remotePageNumber = 1;
        refreshActivity();
    }

    @Override
    protected void onLoadingMore() {
        refreshActivity();
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
                loadingActivity();
            }
        }
    }

    public void onSearching(String text) {
        searchingText = isEmpty(text) ? "" : text;
        if (getUserVisibleHint()) {
            // 只有当前显示的是本fragment时才进行列表操作
            loadingActivity();
        }
    }

    private void loadingActivity() {
        if (null == dao) {
            dao = new Dao<>(Activity.class);
        }
        if (activities.size() < 1) {
            // 列表为空则从缓存中取
            refreshActivity();
        } else {
            showActivity();
        }
    }

    private void refreshActivity() {
        switch (mType) {
            case TYPE_CREATED:
                setNothingText(R.string.ui_activity_management_nothing_created);
                setLoadingText(R.string.ui_activity_management_loading_created);
                displayLoading(true);
                loadingCreatedActivity(false);
                break;
            case TYPE_ENDED:
                setNothingText(R.string.ui_activity_management_nothing_ended);
                setLoadingText(R.string.ui_activity_management_loading_ended);
                displayLoading(true);
                loadingEndedActivity();
                break;
            case TYPE_JOINED:
                setNothingText(R.string.ui_activity_management_nothing_joined);
                setLoadingText(R.string.ui_activity_management_loading_joined);
                displayLoading(true);
                loadingJoinedActivity();
                break;
            case TYPE_NO_JOIN:
                setNothingText(R.string.ui_activity_management_nothing_not_join);
                setLoadingText(R.string.ui_activity_management_loading_not_join);
                displayLoading(true);
                loadingNotJoinActivity();
                break;
        }
    }

    private Dao<Activity> dao;

    private void addMultipleActivity(List<Activity> list, int pageSize) {
        if (null != list) {
            if (list.size() >= pageSize) {
                remotePageNumber++;
                isLoadingComplete(false);
            } else {
                isLoadingComplete(true);
            }
            for (Activity activity : list) {
                if (!activities.contains(activity)) {
                    activities.add(activity);
                }
            }
        } else {
            isLoadingComplete(true);
        }
        showActivity();
    }

    // 从本地缓存中查找我发起的活动列表
    private void loadingCreatedActivity(boolean fromLocal) {
        ActRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Activity>() {
            @Override
            public void onResponse(List<Activity> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    addMultipleActivity(list, pageSize);
                } else {
                    isLoadingComplete(true);
                }
                displayLoading(false);
                stopRefreshing();
            }
        }).list(mQueryId, ActRequest.LIST_CREATED, "", remotePageNumber);
    }

    private void loadingEndedActivity() {
        ActRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Activity>() {
            @Override
            public void onResponse(List<Activity> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    addMultipleActivity(list, pageSize);
                } else {
                    isLoadingComplete(true);
                }
                displayLoading(false);
                stopRefreshing();
            }
        }).list(mQueryId, ActRequest.LIST_ENDED, "", remotePageNumber);
    }

    private void loadingJoinedActivity() {
        ActRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Activity>() {
            @Override
            public void onResponse(List<Activity> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    addMultipleActivity(list, pageSize);
                } else {
                    isLoadingComplete(true);
                }
                displayLoading(false);
                stopRefreshing();
            }
        }).list(mQueryId, ActRequest.LIST_JOINED, "", remotePageNumber);
    }

    private void loadingNotJoinActivity() {
        stopRefreshing();
        displayLoading(false);
        displayNothing(true);
    }

    private void showActivity() {
        if (!lastSearching.equals(searchingText)) {
            // 如果最后一次搜索条件跟当前搜索条件不一样则清空列表
            lastSearching = searchingText;
            mAdapter.clear();
        }
        for (Activity activity : activities) {
            if (isEmpty(searchingText)) {
                mAdapter.update(activity);
            } else {
                if (activity.getTitle().contains(searchingText)) {
                    mAdapter.update(activity);
                }
            }
        }
        displayNothing(mAdapter.getItemCount() < 1);
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == REQUEST_DELETE) {
            Activity act = new Activity();
            act.setId(getResultedData(data));
            activities.remove(act);
            mAdapter.remove(act);
        }
        super.onActivityResult(requestCode, data);
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Activity act = mAdapter.get(index);
            if (mType == TYPE_NO_JOIN) {
                // 未参加的打开参加页面
                openActivity(ActivityEntranceFragment.class.getName(), format("%s,%s", act.getId(), act.getTid()), true, false);
            } else {
                // 已参加的活动，打开活动详情页（2017-06-21 11:20）
                // 已结束的活动不在这里存档内容，需要在组织档案管理页面未存档里找
                openActivity(ActivityDetailsMainFragment.class.getName(), act.getId(), REQUEST_DELETE, false, false);
//                // 已结束的活动
//                if (act.getStatus() == Activity.Status.ENDED) {
//                    // 如果是我自己创建的，打开改活动待存档文件管理页面
//                    if (act.getCreatorId().equals(Cache.cache().userId)) {
//                        openActivity(ActivityArchivingManagementFragment.class.getName(), act.getId(), true, false);
//                    } else {
//                        // 不是我创建的则打开详情页
//                        openActivity(ActivityDetailsMainFragment.class.getName(), act.getId(), false, false);
//                    }
//                } else {
//                    // 已参加的打开活动详情页面
//                    openActivity(ActivityDetailsMainFragment.class.getName(), act.getId(), false, false);
//                }
            }
        }
    };

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new ActivityAdapter();
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private class ActivityAdapter extends RecyclerViewAdapter<ActivityManagementViewHolder, Activity> {

        @Override
        public ActivityManagementViewHolder onCreateViewHolder(View itemView, int viewType) {
            ActivityManagementViewHolder holder = new ActivityManagementViewHolder(itemView, MyActivitiesFragment.this);
            holder.addOnViewHolderClickListener(onViewHolderClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_activity_management_item;
        }

        @Override
        public void onBindHolderOfView(ActivityManagementViewHolder holder, int position, @Nullable Activity item) {
            holder.showContent(item, searchingText);
        }

        @Override
        protected int comparator(Activity item1, Activity item2) {
            return 0;
        }
    }
}
