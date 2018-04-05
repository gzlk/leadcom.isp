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
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.popup.DeleteDialogHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.holder.activity.NoticeViewHolder;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.activity.AppNotice;
import com.leadcom.android.isp.view.SwipeItemLayout;
import com.netease.nim.uikit.impl.cache.TeamDataCache;
import com.netease.nimlib.sdk.team.constant.TeamMemberType;
import com.netease.nimlib.sdk.team.model.TeamMember;

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

public class NoticeListFragment extends BaseNoticeFragment {

    private static final String PARAM_CREATABLE = "nlf_creatable";
    private static final String PARAM_OWNER = "nlf_owner";

    public static NoticeListFragment newInstance(Bundle bundle) {
        NoticeListFragment nlf = new NoticeListFragment();
        nlf.setArguments(bundle);
        return nlf;
    }

    private static Bundle getBundle(String tid, boolean creatable) {
        Bundle bundle = new Bundle();
        // 传过来的tid
        bundle.putString(PARAM_QUERY_ID, tid);
        // 是否允许新建通知
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
    private boolean mCreatable, isOwner;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mCreatable = bundle.getBoolean(PARAM_CREATABLE, false);
        isOwner = bundle.getBoolean(PARAM_OWNER, false);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putBoolean(PARAM_CREATABLE, mCreatable);
        bundle.putBoolean(PARAM_OWNER, isOwner);
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
    protected void onSwipeRefreshing() {
        remotePageNumber = 1;
        loadingNotices();
    }

    @Override
    protected void onLoadingMore() {
        loadingNotices();
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
            mRootView.setBackgroundColor(getColor(R.color.windowBackground));
            mAdapter = new NoticeAdapter();
            mRecyclerView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(Activity()));
            mRecyclerView.setAdapter(mAdapter);
            TeamMember member = TeamDataCache.getInstance().getTeamMember(mQueryId, Cache.cache().userId);
            isOwner = member.getType() == TeamMemberType.Owner;
            loadingNotices();
        }
    }

    @Override
    protected void onDeleteNoticeComplete(boolean success, String noticeId) {
        if (success) {
            mAdapter.remove(noticeId);
        }
    }

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, final int index) {
            AppNotice notice = mAdapter.get(index);
            switch (view.getId()) {
                case R.id.ui_tool_view_contact_button2:
                    warningDelete(notice.getId());
                    break;
                default:
                    notice.setRead(true);
                    AppNotice.save(notice);
                    mAdapter.notifyItemChanged(index);
                    NoticeDetailsFragment.open(NoticeListFragment.this, notice.getId());
                    break;
            }
        }
    };

    private class NoticeAdapter extends RecyclerViewAdapter<NoticeViewHolder, AppNotice> {

        @Override
        public NoticeViewHolder onCreateViewHolder(View itemView, int viewType) {
            NoticeViewHolder holder = new NoticeViewHolder(itemView, NoticeListFragment.this);
            holder.setOnViewHolderElementClickListener(elementClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return isOwner ? R.layout.holder_view_activity_notice_item_deletable : R.layout.holder_view_activity_notice_item;
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
