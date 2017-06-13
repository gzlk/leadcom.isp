package com.gzlk.android.isp.fragment.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.activity.ActRequest;
import com.gzlk.android.isp.api.common.FocusImageRequest;
import com.gzlk.android.isp.api.common.RecommendRequest;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.fragment.activity.ActivityEntranceFragment;
import com.gzlk.android.isp.fragment.archive.ArchiveDetailsWebViewFragment;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.archive.ArchiveManagementViewHolder;
import com.gzlk.android.isp.holder.home.ActivityHomeViewHolder;
import com.gzlk.android.isp.holder.home.HomeImagesViewHolder;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.activity.Activity;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.common.FocusImage;
import com.gzlk.android.isp.model.common.PriorityPlace;
import com.gzlk.android.isp.model.common.RecommendContent;
import com.gzlk.android.isp.nim.session.NimSessionHelper;

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

    public static HomeRecommendedFragment newInstance(String params) {
        HomeRecommendedFragment hrf = new HomeRecommendedFragment();
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
        if (mType == TYPE_ALL) {
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
            mRecyclerView.setAdapter(mAdapter);
            if (getUserVisibleHint()) {
                if (mType == TYPE_ALL) {
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
        public void onImageClick(String url) {
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
        if (!url.startsWith("http://")) {
            url = "http://" + url;
        }
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        Activity().startActivity(intent);
    }

    private void fetchingRecommended() {
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
                                case RecommendContent.SourceType.ACTIVITY:
                                    if (null != content.getActivity()) {
                                        mAdapter.update(content.getActivity());
                                    }
                                    break;
                                case RecommendContent.SourceType.ARCHIVE:
                                    if (null != content.getGroDoc()) {
                                        mAdapter.update(content.getGroDoc());
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
        }).list(mType);
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Model model = mAdapter.get(index);
            if (model instanceof Activity) {
                // 到活动详情报名页
                Activity act = (Activity) model;
                isJoinedPublicAct(act.getId(), act.getTid());
            } else if (model instanceof Archive) {
                // 到档案详情
                Archive arc = (Archive) model;
                if (isEmpty(arc.getContent())) {
                    ToastHelper.make().showMsg(R.string.ui_text_home_archive_content_empty);
                } else {
                    int type = isEmpty(arc.getGroupId()) ? Archive.Type.USER : Archive.Type.GROUP;
                    openActivity(ArchiveDetailsWebViewFragment.class.getName(), format("%d,%s,%s", type, arc.isManager(), arc.getId()), true, false);
                }
            } else if (model instanceof PriorityPlace) {
                // 编辑推荐
                PriorityPlace place = (PriorityPlace) model;
                openDefaultWeb(place.getTargetPath());
            }
        }
    };

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
                default:
                    ArchiveManagementViewHolder ahvh = new ArchiveManagementViewHolder(itemView, HomeRecommendedFragment.this);
                    ahvh.addOnViewHolderClickListener(onViewHolderClickListener);
                    return ahvh;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (mType == TYPE_ALL && position == 0) {
                return VT_HEADER;
            }
            Model model = get(position);
            if (model instanceof Activity) {
                return VT_ACTIVITY;
            } else {
                return VT_ARCHIVE;
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
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
