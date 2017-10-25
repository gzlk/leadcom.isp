package com.gzlk.android.isp.fragment.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.activity.ActRequest;
import com.gzlk.android.isp.api.archive.LikeRequest;
import com.gzlk.android.isp.api.archive.RecommendArchiveRequest;
import com.gzlk.android.isp.api.common.FocusImageRequest;
import com.gzlk.android.isp.api.common.RecommendRequest;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.user.CollectionRequest;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.activity.ActivityEntranceFragment;
import com.gzlk.android.isp.fragment.archive.ArchiveDetailsFragment;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.fragment.common.InnerWebViewFragment;
import com.gzlk.android.isp.helper.ToastHelper;
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
import com.gzlk.android.isp.model.archive.Additional;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.archive.ArchiveLike;
import com.gzlk.android.isp.model.archive.Comment;
import com.gzlk.android.isp.model.archive.RecommendArchive;
import com.gzlk.android.isp.model.common.FocusImage;
import com.gzlk.android.isp.model.common.PriorityPlace;
import com.gzlk.android.isp.model.common.RecommendContent;
import com.gzlk.android.isp.model.user.Collection;
import com.gzlk.android.isp.nim.session.NimSessionHelper;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能描述：</b>首页 - 推荐内容<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/05 15:03 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/05 15:03 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class HomeRecommendedFragment extends BaseSwipeRefreshSupportFragment {

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

    public static HomeRecommendedFragment newInstance(String params) {
        HomeRecommendedFragment hrf = new HomeRecommendedFragment();
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

    public HomeRecommendedFragment setToolBar(View view) {
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
                fetchingRecommended();
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
            fetchingRecommended();
        }
    }

    @Override
    protected void onLoadingMore() {
        fetchingRecommended();
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
        FocusImageRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<FocusImage>() {
            @Override
            public void onResponse(List<FocusImage> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    images.clear();
                    if (null != list) {
                        images.addAll(list);
                        homeImagesViewHolder.addImages(images);
                    }
                }
                displayLoading(false);
                stopRefreshing();
                fetchingRecommended();
            }
        }).all();
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
                    fetchingRecommended();
                }
            }
        }
    }

    private List<FocusImage> images = new ArrayList<>();

    private FocusImage getImage(String imageUrl) {
        for (FocusImage image : images) {
            if (image.getImageUrl().equals(imageUrl)) {
                return image;
            }
        }
        return null;
    }

    private ImageDisplayer.OnImageClickListener onImageClickListener = new ImageDisplayer.OnImageClickListener() {
        @Override
        public void onImageClick(ImageDisplayer displayer, String url) {
            FocusImage image = getImage(url);
            if (null != image) {
                String type = image.getType();
                String target = image.getTargetPath();
                if (isEmpty(target)) {
                    ToastHelper.make().showMsg("无效的url路径");
                } else {
                    if (isEmpty(type) || type.equals("inner")) {
                        openActivity(InnerWebViewFragment.class.getName(), format("%s,%s", image.getTargetPath(), image.getTitle()), true, false);
                    } else {
                        openDefaultWeb(image.getTargetPath());
                    }
                }
            } else {
                ToastHelper.make().showMsg("未指定的推荐内容");
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

    private void fetchingRecommended() {
//        if (mType == TYPE_ARCHIVE) {
//            fetchingRecommendedArchives();
//            return;
//        }
        displayLoading(true);
        displayNothing(false);
        RecommendRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<RecommendContent>() {
            @Override
            public void onResponse(List<RecommendContent> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                initializeAdapter();
                if (success) {
                    if (null != list) {
                        if (list.size() >= pageSize) {
                            remotePageNumber++;
                            isLoadingComplete(false);
                        } else {
                            isLoadingComplete(true);
                        }
                        for (RecommendContent content : list) {
                            switch (content.getSourceType()) {
//                                case RecommendContent.SourceType.ACTIVITY:
//                                    if (null != content.getActivity()) {
//                                        mAdapter.update(content.getActivity());
//                                    }
//                                    break;
                                case RecommendContent.SourceType.ARCHIVE:
                                    if (null != content.getGroDocRcmd()) {
                                        mAdapter.update(content.getGroDocRcmd());
                                    }
                                    break;
                                case RecommendContent.SourceType.PRIORITY_PLACE:
                                    if (null != content.getPriorityPlace()) {
                                        mAdapter.update(content.getPriorityPlace());
                                    }
                                    break;
                            }
                        }
                        //mAdapter.sort();
                    } else {
                        isLoadingComplete(true);
                    }
                } else {
                    isLoadingComplete(true);
                }
                stopRefreshing();
                displayLoading(false);
                displayNothing(mAdapter.getItemCount() < (mType == TYPE_ALL ? 2 : 1));
            }
        }).list(remotePageNumber);
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
        }).front(remotePageNumber);
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
                openActivity(ArchiveDetailsFragment.class.getName(), format("%d,%s", type, recommend.getDocId()), true, false);
            } else if (model instanceof Archive) {
                // 到档案详情
                Archive arc = (Archive) model;
                int type = isEmpty(arc.getGroupId()) ? Archive.Type.USER : Archive.Type.GROUP;
                openActivity(ArchiveDetailsFragment.class.getName(), format("%d,%s", type, arc.getId()), true, false);
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
                    if (doc.getLike() == Archive.LikeType.LIKED) {
                        unlikeArchive(doc.getId(), type, index);
                    } else {
                        likeArchive(doc.getId(), type, index);
                    }
                    break;
                case R.id.ui_tool_view_archive_additional_collection_layout:
                    // 收藏或取消收藏
                    if (doc.getCollection() == Archive.CollectionType.UN_COLLECT) {
                        collectArchive(isGroup ? Collection.Type.GROUP_ARCHIVE : Collection.Type.USER_ARCHIVE,
                                isGroup ? Collection.SourceType.GROUP_ARCHIVE : Collection.SourceType.USER_ARCHIVE,
                                doc, index);
                    } else {
                        unCollectArchive(doc.getColId(), index);
                    }
                    break;
            }
        }
    };

    private void likeArchive(String archiveId, final int type, final int index) {
        setLoadingText(R.string.ui_base_text_loading);
        displayLoading(true);
        LikeRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<ArchiveLike>() {
            @Override
            public void onResponse(ArchiveLike archiveLike, boolean success, String message) {
                super.onResponse(archiveLike, success, message);
                if (success) {
                    // 点赞成功
                    RecommendArchive archive = (RecommendArchive) mAdapter.get(index);
                    Archive doc = type == Comment.Type.GROUP ? archive.getGroDoc() : archive.getUserDoc();
                    Additional additional = doc.getAddition();
                    additional.setLikeNum(additional.getLikeNum() + 1);
                    doc.setLike(Archive.LikeType.LIKED);
                    mAdapter.update(archive);
                }
                displayLoading(false);
            }
        }).add(type, archiveId);
    }

    private void unlikeArchive(String archiveId, final int type, final int index) {
        setLoadingText(R.string.ui_base_text_loading);
        displayLoading(true);
        LikeRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<ArchiveLike>() {
            @Override
            public void onResponse(ArchiveLike archiveLike, boolean success, String message) {
                super.onResponse(archiveLike, success, message);
                if (success) {
                    // 成功取消了赞
                    RecommendArchive archive = (RecommendArchive) mAdapter.get(index);
                    Archive doc = type == Comment.Type.GROUP ? archive.getGroDoc() : archive.getUserDoc();
                    Additional additional = doc.getAddition();
                    int num = additional.getLikeNum() - 1;
                    additional.setLikeNum(num <= 0 ? 0 : num);
                    doc.setLike(Archive.LikeType.UN_LIKE);
                    mAdapter.update(archive);
                }
                displayLoading(false);
            }
        }).delete(type, archiveId);
    }

    private void collectArchive(int type, int source, Archive doc, final int index) {
        setLoadingText(R.string.ui_base_text_loading);
        displayLoading(true);
        CollectionRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Collection>() {
            @Override
            public void onResponse(Collection collection, boolean success, String message) {
                super.onResponse(collection, success, message);
                if (success) {
                    RecommendArchive archive = (RecommendArchive) mAdapter.get(index);
                    Archive doc = archive.getType() == RecommendArchive.RecommendType.GROUP ? archive.getGroDoc() : archive.getUserDoc();
                    Additional additional = doc.getAddition();
                    int num = additional.getColNum() + 1;
                    additional.setColNum(num);
                    doc.setCollection(Archive.CollectionType.COLLECTED);
                    if (null != collection) {
                        doc.setColId(collection.getId());
                    }
                    mAdapter.notifyItemChanged(index);
                }
                displayLoading(false);
            }
        }).add(type, "", doc.getUserId(), doc.getUserName(), doc.getHeadPhoto(), source, doc.getId(), doc.getTitle(), doc.getLabel(), null);
    }

    private void unCollectArchive(String collectId, final int index) {
        setLoadingText(R.string.ui_base_text_loading);
        displayLoading(true);
        CollectionRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Collection>() {
            @Override
            public void onResponse(Collection collection, boolean success, String message) {
                super.onResponse(collection, success, message);
                if (success) {
                    RecommendArchive archive = (RecommendArchive) mAdapter.get(index);
                    Archive doc = archive.getType() == RecommendArchive.RecommendType.GROUP ? archive.getGroDoc() : archive.getUserDoc();
                    Additional additional = doc.getAddition();
                    int num = additional.getColNum() - 1;
                    additional.setColNum(num <= 0 ? 0 : num);
                    doc.setCollection(Archive.CollectionType.UN_COLLECT);
                    doc.setColId("");
                    mAdapter.notifyItemChanged(index);
                }
                displayLoading(false);
            }
        }).delete(collectId);
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
                        homeImagesViewHolder = new HomeImagesViewHolder(itemView, HomeRecommendedFragment.this);
                        homeImagesViewHolder.setOnImageClickListener(onImageClickListener);
                    }
                    return homeImagesViewHolder;
                case VT_ACTIVITY:
                    ActivityHomeViewHolder holder = new ActivityHomeViewHolder(itemView, HomeRecommendedFragment.this);
                    holder.addOnViewHolderClickListener(onViewHolderClickListener);
                    return holder;
                case VT_ARCHIVE:
                    ArchiveHomeRecommendedViewHolder ahrvh = new ArchiveHomeRecommendedViewHolder(itemView, HomeRecommendedFragment.this);
                    ahrvh.addOnViewHolderClickListener(onViewHolderClickListener);
                    ahrvh.setOnViewHolderElementClickListener(elementClickListener);
                    return ahrvh;
                default:
                    ArchiveManagementViewHolder ahvh = new ArchiveManagementViewHolder(itemView, HomeRecommendedFragment.this);
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
                    return R.layout.holder_view_archive_home_recommended;
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
