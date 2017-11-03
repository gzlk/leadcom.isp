package com.gzlk.android.isp.fragment.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.activity.ActRequest;
import com.gzlk.android.isp.api.archive.RecommendArchiveRequest;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.activity.ActivityEntranceFragment;
import com.gzlk.android.isp.fragment.archive.ArchiveDetailsWebViewFragment;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.helper.publishable.CollectHelper;
import com.gzlk.android.isp.helper.publishable.LikeHelper;
import com.gzlk.android.isp.helper.publishable.listener.OnCollectedListener;
import com.gzlk.android.isp.helper.publishable.listener.OnLikeListener;
import com.gzlk.android.isp.helper.publishable.listener.OnUncollectedListener;
import com.gzlk.android.isp.helper.publishable.listener.OnUnlikeListener;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.archive.ArchiveManagementViewHolder;
import com.gzlk.android.isp.holder.home.ActivityHomeViewHolder;
import com.gzlk.android.isp.holder.home.ArchiveHomeRecommendedViewHolder;
import com.gzlk.android.isp.holder.home.HomeImagesViewHolder;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.listener.OnViewHolderElementClickListener;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.activity.Activity;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.archive.Comment;
import com.gzlk.android.isp.model.archive.RecommendArchive;
import com.gzlk.android.isp.model.common.PriorityPlace;
import com.gzlk.android.isp.model.user.Collection;
import com.gzlk.android.isp.nim.session.NimSessionHelper;

import java.lang.ref.SoftReference;
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

public class FeaturedFragment extends BaseSwipeRefreshSupportFragment {

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

    private static final String PARAM_SHOWN = "title_bar_shown";

    public static FeaturedFragment newInstance(String params) {
        FeaturedFragment hrf = new FeaturedFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(PARAM_TYPE, Integer.valueOf(params));
        hrf.setArguments(bundle);
        return hrf;
    }

    private boolean isTitleBarShown = false;

    private int mType = TYPE_NOTHING;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mType = bundle.getInt(PARAM_TYPE, TYPE_ALL);
        isTitleBarShown = bundle.getBoolean(PARAM_SHOWN, false);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_TYPE, mType);
        bundle.putBoolean(PARAM_SHOWN, isTitleBarShown);
    }

    /**
     * 标题栏是否已经显示了
     */
    public boolean isTitleBarShown() {
        return isTitleBarShown;
    }

    private SoftReference<View> toolBarView;

    public FeaturedFragment setToolBar(View view) {
        if (null == toolBarView || null == toolBarView.get()) {
            toolBarView = new SoftReference<>(view);
        }
        return this;
    }

    private SoftReference<View> textView;

    public void setToolBarTextView(View view) {
        if (null == textView || null == textView.get()) {
            textView = new SoftReference<>(view);
        }
    }

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        private int scrolledY = 0;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (!isViewPagerDisplayedCurrent()) {
                return;
            }
            scrolledY += dy;
            if (scrolledY >= 0 && scrolledY <= 500) {
                float alpha = scrolledY * 0.005f;
                if (null != toolBarView && null != toolBarView.get()) {
                    toolBarView.get().setAlpha(alpha);
                    isTitleBarShown = toolBarView.get().getAlpha() >= 1;
                }
                if (null != textView && null != textView.get()) {
                    textView.get().setAlpha(alpha);
                }
            }
        }
    };

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
    protected void onSwipeRefreshing() {
        remotePageNumber = 1;
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
        displayLoading(true);
        displayNothing(false);
//        FocusImageRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<FocusImage>() {
//            @Override
//            public void onResponse(List<FocusImage> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
//                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
//                if (success) {
//                    headline.clear();
//                    if (null != list) {
//                        headline.addAll(list);
//                        homeImagesViewHolder.addImages(headline);
//                    }
//                }
//                displayLoading(false);
//                stopRefreshing();
//                fetchingRecommended();
//            }
//        }).all();
        RecommendArchiveRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<RecommendArchive>() {
            @Override
            public void onResponse(List<RecommendArchive> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    headline.clear();
                    if (null != list) {
                        headline.addAll(list);
                        homeImagesViewHolder.addImages(headline);
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
            mRecyclerView.addOnScrollListener(scrollListener);
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
                ArchiveDetailsWebViewFragment.open(FeaturedFragment.this, archive.getDocId(), type);
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
        displayLoading(true);
        displayNothing(false);
        RecommendArchiveRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<RecommendArchive>() {
            @Override
            public void onResponse(List<RecommendArchive> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    if (remotePageNumber <= 1) {
                        removeArchives();
                    }
                    if (null != list) {
                        if (list.size() >= pageSize) {
                            remotePageNumber++;
                            isLoadingComplete(false);
                        } else {
                            isLoadingComplete(true);
                        }
                        for (RecommendArchive archive : list) {
                            mAdapter.update(archive);
                        }
                    } else {
                        isLoadingComplete(true);
                    }
                } else {
                    isLoadingComplete(true);
                }
                stopRefreshing();
                displayLoading(false);
                displayNothing(mAdapter.getItemCount() < 2);
            }
        }).list(remotePageNumber);
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
                RecommendArchive recommend = (RecommendArchive) model;
                int type = recommend.getType() == RecommendArchive.RecommendType.USER ? Archive.Type.USER : Archive.Type.GROUP;
                ArchiveDetailsWebViewFragment.open(FeaturedFragment.this, recommend.getDocId(), type);
                //openActivity(ArchiveDetailsFragment.class.getName(), format("%d,%s", type, recommend.getDocId()), true, false);
            } else if (model instanceof Archive) {
                // 到档案详情
                Archive arc = (Archive) model;
                int type = isEmpty(arc.getGroupId()) ? Archive.Type.USER : Archive.Type.GROUP;
                ArchiveDetailsWebViewFragment.open(FeaturedFragment.this, arc.getId(), type);
                //openActivity(ArchiveDetailsFragment.class.getName(), format("%d,%s", type, arc.getId()), true, false);
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
            Archive doc = isGroup ? archive.getGroDoc() : archive.getUserDoc();
            int type = isGroup ? Comment.Type.GROUP : Comment.Type.USER;
            switch (view.getId()) {
                case R.id.ui_tool_view_archive_additional_comment_layout:
                    // 评论
                    break;
                case R.id.ui_tool_view_archive_additional_like_layout:
                    // 赞或取消赞
                    checkLikeStatus(type, doc, index);
                    break;
                case R.id.ui_tool_view_archive_additional_collection_layout:
                    // 收藏或取消收藏
                    checkCollectStatus(type, doc, index);
                    break;
            }
        }
    };

    private void checkLikeStatus(final int type, Archive archive, final int index) {
        if (archive.isLiked()) {
            setLoadingText(R.string.ui_text_archive_details_unliking);
            displayLoading(true);
            LikeHelper.helper().setArchive(archive).setUnlikeListener(new OnUnlikeListener() {
                @Override
                public void onUnlike(boolean success, Model model) {
                    displayLoading(false);
                    if (success) {
                        resetArchive(type, (Archive) model, index);
                    }
                }
            }).unlike(type, archive.getId());
        } else {
            setLoadingText(R.string.ui_text_archive_details_liking);
            displayLoading(true);
            LikeHelper.helper().setArchive(archive).setLikeListener(new OnLikeListener() {
                @Override
                public void onLiked(boolean success, Model model) {
                    displayLoading(false);
                    if (success) {
                        resetArchive(type, (Archive) model, index);
                    }
                }
            }).like(type, archive.getId());
        }
    }

    private void resetArchive(int type, Archive archive, int index) {
        RecommendArchive recmd = (RecommendArchive) mAdapter.get(index);
        if (type == Comment.Type.GROUP) {
            recmd.setGroDoc(archive);
        } else {
            recmd.setUserDoc(archive);
        }
        mAdapter.update(recmd);
    }

    private void checkCollectStatus(final int type, final Archive archive, final int index) {
        if (archive.isCollected()) {
            setLoadingText(R.string.ui_text_archive_details_uncollecting);
            displayLoading(true);
            CollectHelper.helper().setArchive(archive).setUncollectedListener(new OnUncollectedListener() {
                @Override
                public void onUncollected(boolean success, Model model) {
                    displayLoading(false);
                    if (success) {
                        resetArchive(type, archive, index);
                    }
                }
            }).uncollect(archive.getColId());
        } else {
            setLoadingText(R.string.ui_text_archive_details_collecting);
            displayLoading(true);
            CollectHelper.helper().setArchive(archive).setCollectedListener(new OnCollectedListener() {
                @Override
                public void onCollected(boolean success, Model model) {
                    displayLoading(false);
                    if (success) {
                        resetArchive(type, archive, index);
                    }
                }
            }).collect(Collection.get(archive));
        }
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

    private class RecommendedAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {

        private static final int VT_HEADER = 0, VT_ACTIVITY = 1, VT_ARCHIVE = 2, VT_EDITOR = 3;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            switch (viewType) {
                case VT_HEADER:
                    if (null == homeImagesViewHolder) {
                        homeImagesViewHolder = new HomeImagesViewHolder(itemView, FeaturedFragment.this);
                        homeImagesViewHolder.setOnImageClickListener(onImageClickListener);
                    }
                    return homeImagesViewHolder;
                case VT_ACTIVITY:
                    ActivityHomeViewHolder holder = new ActivityHomeViewHolder(itemView, FeaturedFragment.this);
                    holder.addOnViewHolderClickListener(onViewHolderClickListener);
                    return holder;
                case VT_ARCHIVE:
                    ArchiveHomeRecommendedViewHolder ahrvh = new ArchiveHomeRecommendedViewHolder(itemView, FeaturedFragment.this);
                    ahrvh.addOnViewHolderClickListener(onViewHolderClickListener);
                    ahrvh.setOnViewHolderElementClickListener(elementClickListener);
                    return ahrvh;
                default:
                    ArchiveManagementViewHolder ahvh = new ArchiveManagementViewHolder(itemView, FeaturedFragment.this);
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
