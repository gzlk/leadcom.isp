package com.leadcom.android.isp.fragment.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.hlk.hlklib.lib.inject.ViewId;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.activity.ActRequest;
import com.leadcom.android.isp.api.archive.RecommendArchiveRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.activity.ActivityEntranceFragment;
import com.leadcom.android.isp.fragment.archive.ArchiveDetailsWebViewFragment;
import com.leadcom.android.isp.fragment.base.BaseCmtLikeColFragment;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.archive.ArchiveManagementViewHolder;
import com.leadcom.android.isp.holder.common.ClickableSearchViewHolder;
import com.leadcom.android.isp.holder.home.ActivityHomeViewHolder;
import com.leadcom.android.isp.holder.home.ArchiveHomeRecommendedViewHolder;
import com.leadcom.android.isp.holder.home.HomeImagesViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.activity.Activity;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.archive.RecommendArchive;
import com.leadcom.android.isp.model.common.PriorityPlace;
import com.leadcom.android.isp.nim.session.NimSessionHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能描述：</b>首页 - 特色推荐内容<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/05 15:03 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/05 15:03 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class HomeFeaturedFragment extends BaseCmtLikeColFragment {

    private static final int TYPE_NOTHING = -1;
    /**
     * 全部
     */
    public static final int TYPE_ALL = 0;
    /**
     * 活动
     */
    public static final int TYPE_ACTIVITY = 1;
    /**
     * 档案
     */
    public static final int TYPE_ARCHIVE = 2;
    /**
     * 编辑位
     */
    public static final int TYPE_EDITOR = 3;

    private static final String PARAM_TYPE = "hrf_param_type";

    private static int selectedIndex = 0;

    public static HomeFeaturedFragment newInstance(String params) {
        HomeFeaturedFragment hrf = new HomeFeaturedFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(PARAM_TYPE, Integer.valueOf(params));
        hrf.setArguments(bundle);
        return hrf;
    }

    private int mType = TYPE_NOTHING;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mType = bundle.getInt(PARAM_TYPE, TYPE_ALL);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_TYPE, mType);
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        switch (requestCode) {
            case REQUEST_DELETE:
                // 上层返回的有更改的或删除的
                String id = getResultedData(data);
                Model result = getResultModel(data, RESULT_ARCHIVE);
                if (null != result) {
                    if (selectedIndex > 0) {
                        // 焦点图不需要更新点赞、收藏
                        RecommendArchive archive = (RecommendArchive) mAdapter.get(selectedIndex);
                        boolean isGroup = archive.getType() == RecommendArchive.RecommendType.GROUP;
                        if (isGroup) {
                            archive.setGroDoc((Archive) result);
                        } else {
                            archive.setUserDoc((Archive) result);
                        }
                        mAdapter.update(archive);
                    }
                } else {
                    Model model = new Model();
                    model.setId(id);
                    mAdapter.remove(model);
                }
                break;
        }
        super.onActivityResult(requestCode, data);
    }

    @Override
    protected void onViewPagerDisplayedChanged(boolean visible) {
        super.onViewPagerDisplayedChanged(visible);
        if (visible) {
            if (mType > TYPE_NOTHING) {
                fetchingRecommendedArchives();
            }
        }
    }

    private RecommendedAdapter mAdapter;
    private HomeImagesViewHolder homeImagesViewHolder;
    @ViewId(R.id.ui_holder_view_searchable_container)
    private View searchableView;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        resetNothing();
        initializeAdapter();
    }

    private void resetNothing() {
        switch (mType) {
            case TYPE_ALL:
                break;
            case TYPE_ACTIVITY:
                setNothingText(R.string.ui_text_home_activity_nothing);
                break;
            case TYPE_ARCHIVE:
                setNothingText(R.string.ui_text_home_archive_nothing);
                break;
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ClickableSearchViewHolder searchViewHolder = new ClickableSearchViewHolder(searchableView, this);
        searchViewHolder.addOnViewHolderClickListener(new OnViewHolderClickListener() {
            @Override
            public void onClick(int index) {
                ArchiveSearchFragment.open(HomeFeaturedFragment.this, ArchiveSearchFragment.SEARCH_HOME, "", "");
            }
        });
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_main_home_featured;
    }

    @Override
    protected void onSwipeRefreshing() {
        remotePageNumber = 1;
        setSupportLoadingMore(true);
        if (mType == TYPE_ALL || mType == TYPE_ARCHIVE) {
            fetchingFocusImages();
        } else {
            fetchingRecommendedArchives();
        }
    }

    @Override
    protected void onLoadingMore() {
        fetchingRecommendedArchives();
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    private void setTestData() {
        mAdapter.add(new Model());
    }

    private void fetchingFocusImages() {
        setLoadingText(R.string.ui_text_home_focus_image_loading);
        //displayLoading(true);
        displayNothing(false);
        RecommendArchiveRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<RecommendArchive>() {
            @Override
            public void onResponse(List<RecommendArchive> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    headline.clear();
                    if (null != list) {
                        headline.addAll(list);
                        if (null != homeImagesViewHolder) {
                            homeImagesViewHolder.addImages(headline);
                        }
                    }
                }
                displayLoading(false);
                stopRefreshing();
                fetchingRecommendedArchives();
            }
        }).focusImage();
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new RecommendedAdapter();
            //mRecyclerView.addOnScrollListener(scrollListener);
            mRecyclerView.setAdapter(mAdapter);
            if (getUserVisibleHint()) {
                if (mType == TYPE_ALL || mType == TYPE_ARCHIVE) {
                    setTestData();
                    fetchingFocusImages();
                } else {
                    fetchingRecommendedArchives();
                }
            }
        }
    }

    /**
     * 首页头条推荐
     */
    private List<RecommendArchive> headline = new ArrayList<>();

    private RecommendArchive getArchiveByCover(String imageUrl) {
        for (RecommendArchive archive : headline) {
            Archive doc = archive.getDoc();
            String cover = doc.getCover();
            if (isEmpty(cover)) {
                // 封面为空时，查找第一张图片是否为当前url
                int size = doc.getImage().size();
                if (size > 0 && imageUrl.equals(doc.getImage().get(0).getUrl())) {
                    return archive;
                } else if (imageUrl.equals(doc.getId())) {
                    return archive;
                }
            } else if (cover.equals(imageUrl)) {
                return archive;
            }
        }
        return null;
    }

    private ImageDisplayer.OnImageClickListener onImageClickListener = new ImageDisplayer.OnImageClickListener() {
        @Override
        public void onImageClick(ImageDisplayer displayer, String url) {
            RecommendArchive archive = getArchiveByCover(url);
            if (null != archive) {
                // 打开档案详情页
                int type = archive.getType() == RecommendArchive.RecommendType.GROUP ? Archive.Type.GROUP : Archive.Type.USER;
                ArchiveDetailsWebViewFragment.open(HomeFeaturedFragment.this, archive.getDocId(), type);
            } else {
                ToastHelper.make().showMsg("无效的推荐内容");
            }
        }
    };

    private void openDefaultWeb(String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        if (!Utils.isUrl(url)) {
            url = "http://" + url;
        }
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        Activity().startActivity(intent);
    }

    private void fetchingRecommendedArchives() {
        setLoadingText(R.string.ui_text_home_archive_loading);
        //displayLoading(true);
        displayNothing(false);
        RecommendArchiveRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<RecommendArchive>() {
            @Override
            public void onResponse(List<RecommendArchive> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (remotePageNumber <= 1) {
                    removeArchives();
                }
                int count = null == list ? 0 : list.size();
                remotePageNumber += count < pageSize ? 0 : 1;
                isLoadingComplete(count < pageSize);
                if (success && null != list) {
                    for (RecommendArchive archive : list) {
                        mAdapter.update(archive);
                    }
                }
                stopRefreshing();
                displayLoading(false);
                displayNothing(mAdapter.getItemCount() < 2);
            }
        }).listHomeFeatured(remotePageNumber, "");
    }

    private void removeArchives() {
        int size = mAdapter.getItemCount();
        while (size > 1) {
            mAdapter.remove(size - 1);
            size = mAdapter.getItemCount();
        }
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Model model = mAdapter.get(index);
            if (model instanceof Activity) {
                // 到活动详情报名页
                Activity act = (Activity) model;
                isJoinedPublicAct(act.getId(), act.getTid());
            } else if (model instanceof RecommendArchive) {
                selectedIndex = index;
                RecommendArchive recommend = (RecommendArchive) model;
                int type = recommend.getType() == RecommendArchive.RecommendType.USER ? Archive.Type.USER : Archive.Type.GROUP;
                ArchiveDetailsWebViewFragment.open(HomeFeaturedFragment.this, recommend.getDocId(), type);
            } else if (model instanceof Archive) {
                // 到档案详情
                Archive arc = (Archive) model;
                int type = isEmpty(arc.getGroupId()) ? Archive.Type.USER : Archive.Type.GROUP;
                ArchiveDetailsWebViewFragment.open(HomeFeaturedFragment.this, arc.getId(), type);
            } else if (model instanceof PriorityPlace) {
                // 编辑推荐
                PriorityPlace place = (PriorityPlace) model;
                openDefaultWeb(place.getTargetPath());
            }
        }
    };

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            RecommendArchive archive = (RecommendArchive) mAdapter.get(index);
            boolean isGroup = archive.getType() == RecommendArchive.RecommendType.GROUP;
            switch (view.getId()) {
                case R.id.ui_tool_view_archive_additional_comment_layout:
                    // 打开档案详情页评论
                    //selectedIndex = index;
                    //break;
                case R.id.ui_tool_view_archive_additional_like_layout:
                    // 赞或取消赞
                    //selectedIndex = index;
                    //like(doc);
                    //break;
                case R.id.ui_tool_view_archive_additional_collection_layout:
                    // 收藏或取消收藏
                    selectedIndex = index;
                    ArchiveDetailsWebViewFragment.open(HomeFeaturedFragment.this, archive.getDocId(), isGroup ? Archive.Type.GROUP : Archive.Type.USER);
                    //collect(doc);
                    break;
            }
        }
    };

    private void resetArchive(Archive archive) {
        RecommendArchive recmd = (RecommendArchive) mAdapter.get(selectedIndex);
        if (recmd.getType() == RecommendArchive.RecommendType.GROUP) {
            recmd.setGroDoc(archive);
        } else {
            recmd.setUserDoc(archive);
        }
        mAdapter.update(recmd);
        selectedIndex = 0;
    }

    private void isJoinedPublicAct(final String actId, final String tid) {
        ActRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Activity>() {
            @Override
            public void onResponse(Activity activity, boolean success, String message) {
                super.onResponse(activity, success, message);
                if (success) {
                    NimSessionHelper.startTeamSession(Activity(), tid);
                } else {
                    // 如果不在该群则打开报名页面
                    openActivity(ActivityEntranceFragment.class.getName(), format("%s,%s", actId, tid), true, false);
                }
            }
        }).isJoinPublicAct(actId);
    }

    @Override
    protected void onLikeComplete(boolean success, Model model) {
        if (success) {
            resetArchive((Archive) model);
        }
    }

    @Override
    protected void onCollectComplete(boolean success, Model model) {
        if (success) {
            resetArchive((Archive) model);
        }
    }

    private class RecommendedAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {

        private static final int VT_HEADER = 0, VT_ACTIVITY = 1, VT_ARCHIVE = 2, VT_EDITOR = 3;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            switch (viewType) {
                case VT_HEADER:
                    if (null == homeImagesViewHolder) {
                        homeImagesViewHolder = new HomeImagesViewHolder(itemView, HomeFeaturedFragment.this);
                        homeImagesViewHolder.setOnImageClickListener(onImageClickListener);
                        homeImagesViewHolder.addImages(headline);
                    }
                    return homeImagesViewHolder;
                case VT_ACTIVITY:
                    ActivityHomeViewHolder holder = new ActivityHomeViewHolder(itemView, HomeFeaturedFragment.this);
                    holder.addOnViewHolderClickListener(onViewHolderClickListener);
                    return holder;
                case VT_ARCHIVE:
                    ArchiveHomeRecommendedViewHolder ahrvh = new ArchiveHomeRecommendedViewHolder(itemView, HomeFeaturedFragment.this);
                    ahrvh.addOnViewHolderClickListener(onViewHolderClickListener);
                    ahrvh.setOnViewHolderElementClickListener(elementClickListener);
                    return ahrvh;
                default:
                    ArchiveManagementViewHolder ahvh = new ArchiveManagementViewHolder(itemView, HomeFeaturedFragment.this);
                    ahvh.addOnViewHolderClickListener(onViewHolderClickListener);
                    return ahvh;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if ((mType == TYPE_ALL || mType == TYPE_ARCHIVE) && position == 0) {
                return VT_HEADER;
            }
            Model model = get(position);
            if (model instanceof Activity) {
                return VT_ACTIVITY;
            } else if (model instanceof RecommendArchive) {
                return VT_ARCHIVE;
            } else {
                return VT_EDITOR;
            }
        }

        @Override
        public int itemLayout(int viewType) {
            switch (viewType) {
                case VT_HEADER:
                    // 轮播图
                    return R.layout.holder_view_home_images;
                case VT_ACTIVITY:
                    // 活动
                    return R.layout.holder_view_home_seminar;
                case VT_ARCHIVE:
                    return R.layout.holder_view_archive_home_feature;
                default:
                    // 档案
                    return R.layout.holder_view_archive_management;
            }
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof ArchiveManagementViewHolder) {
                if (item instanceof Archive) {
                    ((ArchiveManagementViewHolder) holder).showContent((Archive) item, "");
                } else if (item instanceof PriorityPlace) {
                    ((ArchiveManagementViewHolder) holder).showContent((PriorityPlace) item);
                }
            } else if (holder instanceof ActivityHomeViewHolder) {
                ((ActivityHomeViewHolder) holder).showContent((Activity) item);
            } else if (holder instanceof ArchiveHomeRecommendedViewHolder) {
                ((ArchiveHomeRecommendedViewHolder) holder).showContent((RecommendArchive) item);
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
