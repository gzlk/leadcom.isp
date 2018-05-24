package com.leadcom.android.isp.fragment.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.hlk.hlklib.lib.inject.ViewId;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.archive.ArchiveRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.archive.ArchiveDetailsWebViewFragment;
import com.leadcom.android.isp.fragment.base.BaseCmtLikeColFragment;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.archive.ArchiveManagementViewHolder;
import com.leadcom.android.isp.holder.common.ClickableSearchViewHolder;
import com.leadcom.android.isp.holder.home.ArchiveHomeRecommendedViewHolder;
import com.leadcom.android.isp.holder.home.HomeImagesViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.common.PriorityPlace;

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

    private static int selectedIndex = -1;

    public static HomeFeaturedFragment newInstance(String params) {
        HomeFeaturedFragment hrf = new HomeFeaturedFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(PARAM_TYPE, Integer.valueOf(params));
        hrf.setArguments(bundle);
        return hrf;
    }

    private int mType = TYPE_NOTHING;
    private Model mHeadLine;

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
                        mAdapter.update(result);
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
        mHeadLine = new Model();
        mHeadLine.setId("headline");
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

    private void fetchingFocusImages() {
        setLoadingText(R.string.ui_text_home_focus_image_loading);
        displayLoading(true);
        displayNothing(false);
        ArchiveRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Archive>() {
            @Override
            public void onResponse(List<Archive> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    headline.clear();
                    if (null != list) {
                        for (Archive archive : list) {
                            archive.setId(archive.getDocId());
                        }
                        headline.addAll(list);
                        if (null != homeImagesViewHolder) {
                            homeImagesViewHolder.addImages(headline);
                        }
                    }
                }
                if (headline.size() < 1) {
                    mAdapter.remove(mHeadLine);
                } else {
                    mAdapter.update(mHeadLine);
                }
                displayLoading(false);
                stopRefreshing();
                fetchingRecommendedArchives();
            }
        }).listHomeHeadline();
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new RecommendedAdapter();
            //mRecyclerView.addOnScrollListener(scrollListener);
            mRecyclerView.setAdapter(mAdapter);
            if (getUserVisibleHint()) {
                if (mType == TYPE_ALL || mType == TYPE_ARCHIVE) {
                    mAdapter.add(mHeadLine);
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
    private List<Archive> headline = new ArrayList<>();

    private Archive getArchiveByCover(String imageUrl) {
        for (Archive doc : headline) {
            String cover = doc.getCover();
            if (isEmpty(cover)) {
                // 封面为空时，查找第一张图片是否为当前url
                int size = doc.getImage().size();
                if (size > 0 && imageUrl.equals(doc.getImage().get(0).getUrl())) {
                    return doc;
                } else if (imageUrl.equals(doc.getId())) {
                    return doc;
                }
            } else if (cover.equals(imageUrl)) {
                return doc;
            }
        }
        return null;
    }

    private ImageDisplayer.OnImageClickListener onImageClickListener = new ImageDisplayer.OnImageClickListener() {
        @Override
        public void onImageClick(ImageDisplayer displayer, String url) {
            selectedIndex = -1;
            Archive archive = getArchiveByCover(url);
            if (null != archive) {
                if (isEmpty(archive.getDocId())) {
                    ToastHelper.make().showMsg("docId is null");
                } else {
                    // 打开档案详情页
                    ArchiveDetailsWebViewFragment.open(HomeFeaturedFragment.this, archive);
                }
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
        displayLoading(remotePageNumber <= 1);
        displayNothing(false);
        ArchiveRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Archive>() {
            @Override
            public void onResponse(List<Archive> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (remotePageNumber <= 1) {
                    removeArchives();
                }
                int count = null == list ? 0 : list.size();
                remotePageNumber += count < pageSize ? 0 : 1;
                isLoadingComplete(count < pageSize);
                if (success && null != list) {
                    for (Archive archive : list) {
                        mAdapter.update(archive);
                    }
                }
                stopRefreshing();
                displayLoading(false);
                displayNothing(mAdapter.getItemCount() < 2);
            }
        }).listHomeRecommend(remotePageNumber, "");
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
            if (model instanceof Archive) {
                // 到档案详情
                Archive arc = (Archive) model;
                ArchiveDetailsWebViewFragment.open(HomeFeaturedFragment.this, arc);
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
            Archive archive = (Archive) mAdapter.get(index);
            boolean isGroup = !isEmpty(archive.getGroupId());
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
                    ArchiveDetailsWebViewFragment.open(HomeFeaturedFragment.this, archive);
                    //collect(doc);
                    break;
            }
        }
    };

    private void resetArchive(Archive archive) {
        mAdapter.update(archive);
        selectedIndex = -1;
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

        private static final int VT_HEADER = 0, VT_ARCHIVE = 1, VT_EDITOR = 2;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            switch (viewType) {
                case VT_HEADER:
                    if (null == homeImagesViewHolder) {
                        homeImagesViewHolder = new HomeImagesViewHolder(itemView, HomeFeaturedFragment.this);
                        homeImagesViewHolder.setOnImageClickListener(onImageClickListener);
                        //homeImagesViewHolder.addImages(headline);
                    }
                    return homeImagesViewHolder;
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
            Model model = get(position);
            if (model.getId().equals("headline")) {
                return VT_HEADER;
            }
            if (model instanceof Archive) {
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
            } else if (holder instanceof ArchiveHomeRecommendedViewHolder) {
                ((ArchiveHomeRecommendedViewHolder) holder).showContent((Archive) item);
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
