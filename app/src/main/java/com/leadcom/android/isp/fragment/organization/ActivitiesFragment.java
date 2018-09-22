package com.leadcom.android.isp.fragment.organization;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.archive.ArchiveRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.fragment.archive.ArchiveDetailsFragment;
import com.leadcom.android.isp.fragment.archive.ArchiveEditorFragment;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.organization.ActivityItemViewHolder;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.operation.GRPOperation;

import java.util.List;

/**
 * <b>功能描述：</b>活动列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/09/17 09:12 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/09/17 09:12  <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ActivitiesFragment extends GroupBaseFragment {

    public static ActivitiesFragment newInstance(Bundle bundle) {
        ActivitiesFragment fragment = new ActivitiesFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private static Bundle getBundle(String groupId, String groupName) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, groupId);
        bundle.putString(PARAM_NAME, groupName);
        return bundle;
    }

    public static void open(BaseFragment fragment, String groupId, String groupName) {
        Bundle bundle = getBundle(groupId, groupName);
        fragment.openActivity(ActivitiesFragment.class.getName(), bundle, true, false);
    }

    public static void open(Context context, String groupId, String groupName) {
        Bundle bundle = getBundle(groupId, groupName);
        BaseActivity.openActivity(context, ActivitiesFragment.class.getName(), bundle, true, false);
    }

    private ActivityAdapter mAdapter;
    private String mGroupName;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mGroupName = bundle.getString(PARAM_NAME, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_NAME, mGroupName);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isLoadingComplete(true);
        String title = StringHelper.getString(R.string.ui_group_activity_fragment_title);
        if (!isEmpty(mGroupName)) {
            title = format("%s(%s)", title, mGroupName);
        }
        setCustomTitle(title);
        if (hasOperation(mQueryId, GRPOperation.ACTIVITY_PUBLISH)) {
            setRightText(R.string.ui_base_text_launch);
            setRightTitleClickListener(new OnTitleButtonClickListener() {
                @Override
                public void onClick() {
                    ArchiveEditorFragment.open(ActivitiesFragment.this, mQueryId, Archive.ArchiveType.ACTIVITY);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == REQUEST_CREATE) {
            loadingActivities();
        }
        super.onActivityResult(requestCode, data);
    }

    @Override
    protected void onSwipeRefreshing() {
        loadingActivities();
    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    public void doingInResume() {
        initializeAdapter();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    private void loadingActivities() {
        displayNothing(false);
        displayLoading(true);
        ArchiveRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Archive>() {
            @Override
            public void onResponse(List<Archive> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success && null != list) {
                    for (Archive archive : list) {
                        archive.setDocType(Archive.ArchiveType.ACTIVITY);
                        archive.setAccessToken(mQueryId);
                        if (isEmpty(archive.getGroupId())) {
                            archive.setGroupId(mQueryId);
                        }
                        if (isEmpty(archive.getId())) {
                            archive.setId(archive.getGroActivityId());
                        }
                    }
                    mAdapter.setData(list);
                }
                displayLoading(false);
                stopRefreshing();
            }
        }).listActivities(mQueryId, remotePageNumber);
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            setNothingText(R.string.ui_group_activity_nothing);
            setLoadingText(R.string.ui_group_activity_loading);
            mAdapter = new ActivityAdapter();
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setOnDataHandingListener(handingListener);
            loadingActivities();
        }
    }

    private RecyclerViewAdapter.OnDataHandingListener handingListener = new RecyclerViewAdapter.OnDataHandingListener() {
        @Override
        public void onStart() {

        }

        @Override
        public void onProgress(int currentPage, int maxPage, int maxCount) {

        }

        @Override
        public void onComplete() {
            displayNothing(mAdapter.getItemCount() <= 0);
        }
    };

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            ArchiveDetailsFragment.open(ActivitiesFragment.this, mAdapter.get(index));
        }
    };

    private class ActivityAdapter extends RecyclerViewAdapter<ActivityItemViewHolder, Archive> {

        @Override
        public ActivityItemViewHolder onCreateViewHolder(View itemView, int viewType) {
            ActivityItemViewHolder aivh = new ActivityItemViewHolder(itemView, ActivitiesFragment.this);
            aivh.setOnViewHolderElementClickListener(elementClickListener);
            return aivh;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_group_activity_item;
        }

        @Override
        public void onBindHolderOfView(ActivityItemViewHolder holder, int position, @Nullable Archive item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(Archive item1, Archive item2) {
            return 0;
        }
    }
}
