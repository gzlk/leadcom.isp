package com.leadcom.android.isp.fragment.activity.notice;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.activity.AppNoticeRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.holder.activity.NoticeViewHolder;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.activity.AppNotice;

import java.util.List;

/**
 * <b>功能描述：</b>通知列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/28 21:21 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/28 21:21 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class NoticeListFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_CREATABLE = "nlf_creatable";

    public static NoticeListFragment newInstance(Bundle bundle) {
        NoticeListFragment nlf = new NoticeListFragment();
        //String[] strings = splitParameters(params);
        //Bundle bundle = new Bundle();
        // 传过来的tid
        //bundle.putString(PARAM_QUERY_ID, strings[0]);
        // 是否允许新建通知
        //mCreatable = Boolean.valueOf(strings[1]);
        nlf.setArguments(bundle);
        return nlf;
    }

    private static Bundle getBundle(String tid, boolean creatable) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, tid);
        bundle.putBoolean(PARAM_CREATABLE, creatable);
        return bundle;
    }

    public static void open(BaseFragment fragment, String tid, boolean creatable) {
        fragment.openActivity(NoticeListFragment.class.getName(), getBundle(tid, creatable), true, false);
    }

    public static void open(Context context, int requestCode, String tid, boolean creatable) {
        BaseActivity.openActivity(context, NoticeListFragment.class.getName(), getBundle(tid, creatable), requestCode, true, false);
    }

    private NoticeAdapter mAdapter;
    private boolean mCreatable;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mCreatable = bundle.getBoolean(PARAM_CREATABLE, false);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putBoolean(PARAM_CREATABLE, mCreatable);
    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        // 通知列表
        setCustomTitle(R.string.ui_activity_notice_list_fragment_title);
        if (mCreatable) {
            resetRightEvent();
        }
        initializeAdapter();
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_top_paddingable_swipe_recycler_view;
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
        loadingNotices();
    }

    @Override
    protected void onLoadingMore() {
        loadingNotices();
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    private void resetRightEvent() {
        setRightText(R.string.ui_base_text_new);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                resultSucceededActivity();
            }
        });
    }

    private void loadingNotices() {
        setLoadingText(R.string.ui_activity_notice_list_loading_notices);
        displayLoading(true);
        displayNothing(false);
        AppNoticeRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<AppNotice>() {
            @Override
            public void onResponse(List<AppNotice> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (remotePageNumber <= 1) {
                    mAdapter.clear();
                }
                int size = null == list ? 0 : list.size();
                remotePageNumber += size < pageSize ? 0 : 1;
                isLoadingComplete(size < pageSize);
                if (success) {
                    if (null != list) {
                        mAdapter.update(list, false);
                    }
                }
                displayLoading(false);
                displayNothing(mAdapter.getItemCount() < 1);
                stopRefreshing();
            }
        }).listTeamNotice(mQueryId, remotePageNumber);
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new NoticeAdapter();
            mRecyclerView.setAdapter(mAdapter);
            loadingNotices();
        }
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            AppNotice notice = mAdapter.get(index);
            notice.setRead(true);
            AppNotice.save(notice);
            mAdapter.notifyItemChanged(index);
            NoticeDetailsFragment.open(NoticeListFragment.this, notice.getId());
        }
    };

    private class NoticeAdapter extends RecyclerViewAdapter<NoticeViewHolder, AppNotice> {

        @Override
        public NoticeViewHolder onCreateViewHolder(View itemView, int viewType) {
            NoticeViewHolder holder = new NoticeViewHolder(itemView, NoticeListFragment.this);
            holder.addOnViewHolderClickListener(onViewHolderClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_activity_notice_item;
        }

        @Override
        public void onBindHolderOfView(NoticeViewHolder holder, int position, @Nullable AppNotice item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(AppNotice item1, AppNotice item2) {
            return 0;
        }
    }
}
