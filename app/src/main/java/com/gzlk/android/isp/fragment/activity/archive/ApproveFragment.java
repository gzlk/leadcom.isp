package com.gzlk.android.isp.fragment.activity.archive;

import android.os.Bundle;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.fragment.organization.archive.ArchiveAdapter;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.common.SearchableViewHolder;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.organization.Organization;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.List;

/**
 * <b>功能描述：</b>审核组织档案<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/25 01:56 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/25 01:56 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ApproveFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_SELECT_ALL = "af_select_all_";

    public static ApproveFragment newInstance(String params) {
        ApproveFragment af = new ApproveFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, params);
        af.setArguments(bundle);
        return af;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        isSelectAll = bundle.getBoolean(PARAM_SELECT_ALL, false);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putBoolean(PARAM_SELECT_ALL, isSelectAll);
    }

    @ViewId(R.id.ui_tool_view_select_all_root)
    private View selectAllView;
    @ViewId(R.id.ui_tool_view_select_all_icon)
    private CustomTextView selectAllIcon;

    private boolean isSelectAll = false;
    private ArchiveAdapter mAdapter;

    private SearchableViewHolder searchableViewHolder;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_archive_management_title_button_3);
        initializeAdapter();
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_archive_approve;
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

    @Click({R.id.ui_tool_view_select_all_root, R.id.ui_archive_approve_reject, R.id.ui_archive_approve_passed})
    private void elementClick(View view) {
        switch (view.getId()) {
            case R.id.ui_tool_view_select_all_root:
                // 全选或全部选
                isSelectAll = !isSelectAll;
                resetSelectAll();
                break;
            case R.id.ui_archive_approve_reject:
                // 未通过审核
                break;
            case R.id.ui_archive_approve_passed:
                // 通过审核
                break;
        }
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Archive archive = mAdapter.get(index);
            archive.setSelected(!archive.isSelected());
            mAdapter.notifyItemChanged(index);
        }
    };

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new ArchiveAdapter(this);
            mAdapter.setOnViewHolderClickListener(onViewHolderClickListener);
            mRecyclerView.setAdapter(mAdapter);
        }
        if (null == searchableViewHolder) {
            searchableViewHolder = new SearchableViewHolder(mRootView, this);
            searchableViewHolder.setOnSearchingListener(onSearchingListener);
        }
        loadLocalArchives("");
    }

    private SearchableViewHolder.OnSearchingListener onSearchingListener = new SearchableViewHolder.OnSearchingListener() {
        @Override
        public void onSearching(String text) {
            if (StringHelper.isEmpty(text)) {
                loadLocalArchives("");
            } else {
                loadLocalArchives(text);
            }
        }
    };

    // 重置全选或全不选状态
    private void resetSelectAll() {
        selectAllIcon.setTextColor(getColor(isSelectAll ? R.color.colorPrimary : R.color.textColorHintLight));
        for (int i = 0, len = mAdapter.getItemCount(); i < len; i++) {
            Archive archive = mAdapter.get(i);
            archive.setSelected(isSelectAll);
            mAdapter.notifyItemChanged(i);
        }
    }

    private void loadLocalArchives(String searchingText) {
        mAdapter.setSearchingText(searchingText);
        QueryBuilder<Archive> builder = new QueryBuilder<>(Archive.class)
                .whereEquals(Organization.Field.GroupId, mQueryId);
        if (!StringHelper.isEmpty(searchingText)) {
            builder = builder.whereAppendAnd()
                    .whereAppend(Archive.Field.Title + " like ?", "%" + searchingText + "%");
        }
        builder = builder.appendOrderDescBy(Model.Field.CreateDate);
        List<Archive> list = new Dao<>(Archive.class).query(builder);
        if (null != list) {
            for (Archive archive : list) {
                // 设置为可选择状态
                archive.setSelectable(true);
                // 默认是否为全选状态
                archive.setSelected(isSelectAll);
            }
            mAdapter.update(list);
        }
        displayNothing(mAdapter.getItemCount() <= 0);
    }
}
