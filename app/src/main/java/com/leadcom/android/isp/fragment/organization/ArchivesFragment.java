package com.leadcom.android.isp.fragment.organization;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.archive.ArchiveRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.user.UserMsgRequest;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.archive.ArchiveDetailsFragment;
import com.leadcom.android.isp.fragment.base.BaseCmtLikeColFragment;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.individual.UserMessageFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.home.ArchiveHomeRecommendedViewHolder;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.organization.Role;

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

public class ArchivesFragment extends BaseCmtLikeColFragment {

    private static final String PARAM_TITLE = "af_title";
    private static final String PARAM_HAS_TITLE = "af_has_title";
    private static final String PARAM_CLASSIFY_ID = "af_classify_id";
    private static final String PARAM_CLASSIFY_NAME = "af_classify_name";

    public static ArchivesFragment newInstance(Bundle bundle) {
        ArchivesFragment af = new ArchivesFragment();
        af.setArguments(bundle);
        return af;
    }

    /**
     * 打开具有标题栏的组织档案列表页面
     */
    public static void open(BaseFragment fragment, String groupId, String groupName, String classifyId, String classifyName) {
        Bundle bundle = new Bundle();
        // 组织id
        bundle.putString(PARAM_QUERY_ID, groupId);
        bundle.putString(PARAM_TITLE, groupName);
        bundle.putString(PARAM_CLASSIFY_ID, classifyId);
        bundle.putString(PARAM_CLASSIFY_NAME, classifyName);
        bundle.putBoolean(PARAM_HAS_TITLE, true);
        fragment.openActivity(ArchivesFragment.class.getName(), bundle, true, false);
    }

    private boolean hasTitle = false;
    private String mTitle = "", mClassifyId = "", mClassifyName = "";
    private ArchiveAdapter mAdapter;
    @ViewId(R.id.ui_holder_view_searchable_container)
    private View searchView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        searchView.setVisibility(View.GONE);
        Role role = Cache.cache().getGroupRole(mQueryId);
        if (null != role) {
            setRightIcon(R.string.ui_icon_comment);
            setRightTitleClickListener(new OnTitleButtonClickListener() {
                @Override
                public void onClick() {
                    // 用户动态相关的消息
                    UserMessageFragment.open(ArchivesFragment.this, UserMsgRequest.TYPE_GROUP_ARCHIVE);
                }
            });
        }
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        hasTitle = bundle.getBoolean(PARAM_HAS_TITLE, false);
        mTitle = bundle.getString(PARAM_TITLE, "");
        mClassifyId = bundle.getString(PARAM_CLASSIFY_ID, "");
        mClassifyName = bundle.getString(PARAM_CLASSIFY_NAME, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putBoolean(PARAM_HAS_TITLE, hasTitle);
        bundle.putString(PARAM_TITLE, mTitle);
        bundle.putString(PARAM_CLASSIFY_ID, mClassifyId);
        bundle.putString(PARAM_CLASSIFY_NAME, mClassifyName);
    }

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
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_main_home_featured;
    }

    @Click({R.id.ui_holder_view_searchable_container})
    private void viewClick(View view) {

    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return hasTitle;
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
                Model result = getResultModel(data, RESULT_ARCHIVE);
                if (null != result) {
                    mAdapter.update((Archive) result);
                } else if (!isEmpty(id)) {
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
                //ArchiveEditorFragment.open(ArchivesFragment.this, "", getResultedData(data));
                break;
        }
        super.onActivityResult(requestCode, data);
    }

    private void fetchingRemoteArchives() {
        displayLoading(true);
        displayNothing(false);
        ArchiveRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Archive>() {
            @Override
            public void onResponse(List<Archive> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (remotePageNumber <= 1) {
                    mAdapter.clear();
                }
                int size = null == list ? 0 : list.size();
                isLoadingComplete(size < pageSize);
                remotePageNumber += size >= pageSize ? 1 : 0;
                if (success && null != list) {
                    // 覆盖方式重置list
                    mAdapter.update(list, remotePageNumber <= 1);
                }
                displayLoading(false);
                stopRefreshing();
                if (null != mAdapter) {
                    displayNothing(mAdapter.getItemCount() < 1);
                }
            }
        }).list(mQueryId, remotePageNumber, "", mClassifyId);
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            if (hasTitle) {
                setCustomTitle(mTitle + (!isEmpty(mClassifyName) ? format("(%s)", mClassifyName) : ""));
            }
            mAdapter = new ArchiveAdapter();
            mRecyclerView.setAdapter(mAdapter);
            if (!isEmpty(mQueryId)) {
                fetchingRemoteArchives();
            }
        }
    }

    private OnViewHolderClickListener viewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            // 打开组织档案详情，一个webview框架
            ArchiveDetailsFragment.open(ArchivesFragment.this, mAdapter.get(index));
        }
    };

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            switch (view.getId()) {
                case R.id.ui_tool_view_document_user_header_image:
                    // 点击头像，打开个人属性页
                    App.openUserInfo(ArchivesFragment.this, mAdapter.get(index).getUserId());
                    break;
                case R.id.ui_tool_view_archive_additional_comment_layout:
                case R.id.ui_tool_view_archive_additional_like_layout:
                case R.id.ui_tool_view_archive_additional_collection_layout:
                    // 个人档案评论
                    ArchiveDetailsFragment.open(ArchivesFragment.this, mAdapter.get(index));
                    break;
//                case R.id.ui_tool_view_archive_additional_like_layout:
//                    // 个人档案点赞
//                    like(mAdapter.get(index));
//                    break;
//                case R.id.ui_tool_view_archive_additional_collection_layout:
//                    // 个人档案收藏
//                    collect(mAdapter.get(index));
//                    break;
            }
        }
    };

    @Override
    protected void onLikeComplete(boolean success, Model model) {
        if (success) {
            mAdapter.update((Archive) model);
        }
    }

    @Override
    protected void onCollectComplete(boolean success, Model model) {
        if (success) {
            mAdapter.update((Archive) model);
        }
    }

    private class ArchiveAdapter extends RecyclerViewAdapter<ArchiveHomeRecommendedViewHolder, Archive> {

        @Override
        public ArchiveHomeRecommendedViewHolder onCreateViewHolder(View itemView, int viewType) {
            ArchiveHomeRecommendedViewHolder holder = new ArchiveHomeRecommendedViewHolder(itemView, ArchivesFragment.this);
            holder.addOnViewHolderClickListener(viewHolderClickListener);
            holder.setOnViewHolderElementClickListener(elementClickListener);
            holder.setHeaderShowable(true);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_archive_home_feature;
        }

        @Override
        public void onBindHolderOfView(ArchiveHomeRecommendedViewHolder holder, int position, @Nullable Archive item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(Archive item1, Archive item2) {
            return 0;
        }
    }
}
