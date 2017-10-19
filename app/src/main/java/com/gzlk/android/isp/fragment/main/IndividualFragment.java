package com.gzlk.android.isp.fragment.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.archive.ArchiveRequest;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.user.CollectionRequest;
import com.gzlk.android.isp.api.user.MomentRequest;
import com.gzlk.android.isp.application.NimApplication;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.archive.ArchiveDetailsFragment;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.fragment.individual.CollectionDetailsFragment;
import com.gzlk.android.isp.fragment.individual.MomentCreatorFragment;
import com.gzlk.android.isp.fragment.individual.MomentImagesFragment;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.archive.ArchiveViewHolder;
import com.gzlk.android.isp.holder.common.NothingMoreViewHolder;
import com.gzlk.android.isp.holder.individual.CollectionItemViewHolder;
import com.gzlk.android.isp.holder.individual.IndividualFunctionViewHolder;
import com.gzlk.android.isp.holder.individual.IndividualHeaderViewHolder;
import com.gzlk.android.isp.holder.individual.MomentDetailsViewHolder;
import com.gzlk.android.isp.holder.individual.MomentHomeCameraViewHolder;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.listener.OnNimMessageEvent;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.listener.OnViewHolderElementClickListener;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.user.Collection;
import com.gzlk.android.isp.model.user.Moment;
import com.gzlk.android.isp.model.user.User;
import com.gzlk.android.isp.nim.model.notification.NimMessage;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/20 22:15 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/20 22:15 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class IndividualFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_SHOWN = "title_bar_shown";
    private static final String PARAM_SELECTED = "function_selected";
    private static final String PAGE_TAG = "ifmt_page_%d_";
    private boolean isTitleBarShown = false;
    private int selectedFunction = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NimApplication.addNimMessageEvent(messageEvent);
    }

    @Override
    public void onDestroy() {
        NimApplication.removeNimMessageEvent(messageEvent);
        super.onDestroy();
    }

    private OnNimMessageEvent messageEvent = new OnNimMessageEvent() {
        @Override
        public void onMessageEvent(NimMessage message) {
            if (!message.isSavable()) {
                // 拉取消息列表
                performRefresh();
            }
        }
    };

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        isTitleBarShown = bundle.getBoolean(PARAM_SHOWN, false);
        selectedFunction = bundle.getInt(PARAM_SELECTED, 0);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        bundle.putBoolean(PARAM_SHOWN, isTitleBarShown);
        bundle.putInt(PARAM_SELECTED, selectedFunction);
        super.saveParamsToBundle(bundle);
    }

    /**
     * 标题栏是否已经显示了
     */
    public boolean isTitleBarShown() {
        return isTitleBarShown;
    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        initializeAdapter();
        //autoRefreshing();
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
        resetList();
        performRefresh();
    }

    @Override
    protected void onLoadingMore() {
        performRefresh();
    }

    @Override
    protected String getLocalPageTag() {
        return format(PAGE_TAG, selectedFunction);
    }

    /**
     * 尝试自动刷新
     */
//    private void autoRefreshing() {
//        if (!isNeedRefresh()) {
//            return;
//        }
//        // 远程刷新的页码小于总页码时才刷新
//        refreshing();
//        performRefresh();
//    }
    private void performRefresh() {
        adapter.remove(noMore());
        displayLoading(true);
        switch (selectedFunction) {
            case 0:
                // 刷新动态列表
                refreshingRemoteMoments();
                break;
            case 1:
                // 刷新档案列表
                refreshingRemoteDocuments();
                break;
            case 2:
                // 刷新收藏列表
                refreshingFavorites();
                break;
        }
    }

    /**
     * 拉取我的最新说说列表
     */
    private void refreshingRemoteMoments() {
        //setLoadingText(R.string.ui_individual_moment_list_loading);
        //displayLoading(true);
        MomentRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Moment>() {

            @SuppressWarnings("unchecked")
            @Override
            public void onResponse(List<Moment> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                int count = null == list ? 0 : list.size();
                adjustRemotePages(count, pageSize);
                if (success) {
                    if (null != list) {
                        if (selectedFunction == 0) {
                            Moment today = (Moment) adapter.get(2);
                            today.setAuthPublic(userInfoNum);
                            today.setContent(lastHeadPhoto);
                            adapter.notifyItemChanged(2);
                            for (Moment moment : list) {
                                adapter.update(moment);
                            }
                        }
                    }
                }
                if (count < pageSize) {
                    adapter.add(noMore());
                }
            }
        }).list(Cache.cache().userId, remotePageNumber);
    }

    /**
     * 拉取我的档案列表
     */
    private void refreshingRemoteDocuments() {
        //adapter.remove(noMore());
        //setLoadingText(R.string.ui_individual_archive_list_loading);
        //displayLoading(true);
        ArchiveRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Archive>() {
            @SuppressWarnings("unchecked")
            @Override
            public void onResponse(List<Archive> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                int count = null == list ? 0 : list.size();
                adjustRemotePages(count, pageSize);
                if (success) {
                    if (null != list) {
                        if (selectedFunction == 1) {
                            for (Archive archive : list) {
                                adapter.update(archive);
                            }
                        }
                    }
                }
                if (count < pageSize) {
                    adapter.add(noMore());
                }
            }
        }).list(remotePageNumber, Cache.cache().userId);
    }

    private void refreshingFavorites() {
        //adapter.remove(noMore());
        //setLoadingText(R.string.ui_individual_collection_list_loading);
        //displayLoading(true);
        CollectionRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Collection>() {
            @SuppressWarnings("unchecked")
            @Override
            public void onResponse(List<Collection> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                int count = null == list ? 0 : list.size();
                adjustRemotePages(count, pageSize);
                if (success) {
                    if (null != list) {
                        if (selectedFunction == 2) {
                            for (Collection collection : list) {
                                adapter.update(collection);
                            }
                        }
                    }
                }
                if (count < pageSize) {
                    adapter.add(noMore());
                }
            }
        }).list(Collection.Type.ALL_ARCHIVE, CollectionRequest.OPE_MONTH, remotePageNumber);
    }

    private void adjustRemotePages(int fetchedCount, int pageSize) {
        // 如果当前拉取的是满页数据，则下次再拉取的时候拉取下一页
        remotePageNumber += (fetchedCount >= pageSize ? 1 : 0);
        isLoadingComplete(fetchedCount < pageSize);
        displayLoading(false);
        stopRefreshing();
    }

    private IndividualAdapter adapter;
    private Model functions, camera, nomore;
    private Moment today;

    // 功能列表
    private Model functions() {
        if (null == functions) {
            functions = new Model();
            functions.setId("only for functions");
        }
        return functions;
    }

    // 今天
    private Moment today() {
        if (null == today) {
            today = new Moment();
            today.setId(getString(R.string.ui_text_moment_item_default_today));
        }
        // 设置时间为今天最后一秒，在排序时会一直排在最前面
        today.setCreateDate(Utils.formatDateOfNow("yyyy-MM-dd 23:59:59"));
        return today;
    }

    private Model noMore() {
        if (null == nomore) {
            nomore = new Model();
            nomore.setId(getString(R.string.ui_base_text_nothing_more_id));
            nomore.setAccessToken(getString(R.string.ui_base_text_nothing_more));
        }
        return nomore;
    }

    private Model momentCamera() {
        if (null == camera) {
            camera = new Model();
            camera.setId(getString(R.string.ui_text_moment_item_default_today));
        }
        return camera;
    }

    private void appendListHeader(boolean needToday) {
        adapter.add(Cache.cache().me, 0);
        adapter.add(functions(), 1);
        if (needToday) {
            adapter.add(today(), 2);
        }
    }

    private void initializeAdapter() {
        if (null == adapter) {
            // 这里不需要直接上传，只需要把选择的图片传递给新建动态页面即可，上传在那里实现
            isSupportDirectlyUpload = false;
            // 添加图片选择
            addOnImageSelectedListener(imageSelectedListener);
            mRecyclerView.addOnScrollListener(scrollListener);
            adapter = new IndividualAdapter();
            mRecyclerView.setAdapter(adapter);
            appendListHeader(selectedFunction == 0);
            // 自动加载本地缓存中的记录
            performRefresh();
        } else {
            // 更新我的信息
            adapter.update(Cache.cache().me);
        }
    }

    private OnImageSelectedListener imageSelectedListener = new OnImageSelectedListener() {
        @Override
        public void onImageSelected(ArrayList<String> selected) {
            // 打开新建动态页面
            openActivity(MomentCreatorFragment.class.getName(), Json.gson().toJson(selected), true, true);
        }
    };

    private SoftReference<View> toolBarView;

    public IndividualFragment setToolBar(View view) {
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

    private void resetList() {
        int size = adapter.getItemCount();
        while (size > 2) {
            adapter.remove(size - 1);
            size = adapter.getItemCount();
        }
        if (selectedFunction == 0) {
            adapter.add(today());
        }
        setSupportLoadingMore(true);
    }

    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == REQUEST_CHANGE) {
            // 上层返回的有更改的
            resetList();
            performRefresh();
        }
        super.onActivityResult(requestCode, data);
    }

    private IndividualFunctionViewHolder.OnFunctionChangeListener functionChangeListener = new IndividualFunctionViewHolder.OnFunctionChangeListener() {
        @Override
        public void onChange(int index) {
            if (selectedFunction != index) {
                setSupportLoadingMore(true);
                // 重置本地页码
                remotePageNumber = 1;
                selectedFunction = index;
                resetList();
                // 尝试从服务器上拉取新纪录
                performRefresh();
            }
        }
    };

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Model model = adapter.get(index);
            if (model instanceof Moment) {
                momentClick((Moment) model);
            } else if (model instanceof Archive) {
                archiveClick((Archive) model);
            } else if (model instanceof Collection) {
                openActivity(CollectionDetailsFragment.class.getName(), model.getId(), true, false);
            }
        }
    };

    private OnViewHolderElementClickListener onViewHolderElementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            switch (view.getId()) {
                case R.id.ui_holder_view_moment_camera_icon:
                    // 选择照片
                    openImageSelector(true);
                    break;
                case R.id.ui_holder_view_moment_camera_message_layer:
                    // 打开消息列表
                    break;
            }
        }
    };

    private void momentClick(Moment moment) {
        if (null != moment) {
            // 点击打开新窗口查看详情
            if (moment.getId().contains("today's")) {
                openImageSelector(true);
            } else {
                // 默认显示第一张图片
                MomentImagesFragment.open(IndividualFragment.this, moment.getId(), 0);
            }
        }
    }

    private void archiveClick(Archive archive) {
        if (null != archive) {
            int type = isEmpty(archive.getGroupId()) ? Archive.Type.USER : Archive.Type.GROUP;
            ArchiveDetailsFragment.open(IndividualFragment.this, type, archive.getId(), REQUEST_CHANGE);
        }
    }

    private class IndividualAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {

        private static final int VT_HEADER = 0, VT_FUNCTION = 1, VT_MOMENT = 2, VT_ARCHIVE = 3, VT_COLLECTION = 4, VT_CAMERA = 5, VT_NO_MORE = 6;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            BaseFragment fragment = IndividualFragment.this;
            switch (viewType) {
                case VT_HEADER:
                    return new IndividualHeaderViewHolder(itemView, fragment);
                case VT_FUNCTION:
                    IndividualFunctionViewHolder ifvh = new IndividualFunctionViewHolder(itemView, fragment);
                    // 初始化选中第一个
                    ifvh.setSelected(0);
                    ifvh.addOnFunctionChangeListener(functionChangeListener);
                    return ifvh;
                case VT_MOMENT:
//                    MomentViewHolder mvh = new MomentViewHolder(itemView, fragment);
//                    mvh.addOnViewHolderClickListener(onViewHolderClickListener);
//                    mvh.addOnGotPositionListener(gotPositionListener);
//                    return mvh;
                    MomentDetailsViewHolder mdvh = new MomentDetailsViewHolder(itemView, fragment);
                    mdvh.addOnViewHolderClickListener(onViewHolderClickListener);
                    // 赞、评论快捷菜单
                    //mdvh.setOnMoreClickListener(onMoreClickListener);
                    // 图片点击
                    //mdvh.setOnImageClickListener(onImageClickListener);
                    return mdvh;
                case VT_CAMERA:
                    MomentHomeCameraViewHolder mhcvh = new MomentHomeCameraViewHolder(itemView, fragment);
                    mhcvh.setOnViewHolderElementClickListener(onViewHolderElementClickListener);
                    return mhcvh;
                case VT_ARCHIVE:
                    ArchiveViewHolder holder = new ArchiveViewHolder(itemView, fragment);
                    holder.addOnViewHolderClickListener(onViewHolderClickListener);
                    return holder;
                case VT_COLLECTION:
                    CollectionItemViewHolder civh = new CollectionItemViewHolder(itemView, fragment);
                    civh.addOnViewHolderClickListener(onViewHolderClickListener);
                    return civh;
                case VT_NO_MORE:
                    return new NothingMoreViewHolder(itemView, fragment);
            }
            return null;
        }

        @Override
        public int itemLayout(int viewType) {
            switch (viewType) {
                case VT_HEADER:
                    return R.layout.holder_view_individual_header;
                case VT_FUNCTION:
                    return R.layout.holder_view_individual_main_functions;
                case VT_MOMENT:
                    return R.layout.holder_view_individual_moment_details;
                case VT_ARCHIVE:
                    return R.layout.holder_view_document;
                case VT_CAMERA:
                    return R.layout.holder_view_individual_moment_camera;
                case VT_NO_MORE:
                    return R.layout.holder_view_nothing_more;
                default:
                    return R.layout.holder_view_collection;
            }
        }

        @Override
        public int getItemViewType(int position) {
            Model model = get(position);
            if (model instanceof User) {
                return VT_HEADER;
            } else if (model.getId().contains("moment")) {
                return VT_CAMERA;
            } else if (model.getId().contains("no_more_any_things")) {
                return VT_NO_MORE;
            } else if (model instanceof Moment) {
                return VT_MOMENT;
            } else if (model instanceof Archive) {
                return VT_ARCHIVE;
            } else if (model instanceof Collection) {
                return VT_COLLECTION;
            } else return VT_FUNCTION;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof IndividualHeaderViewHolder) {
                ((IndividualHeaderViewHolder) holder).showContent((User) item);
            } else if (holder instanceof MomentDetailsViewHolder) {
                ((MomentDetailsViewHolder) holder).showContent((Moment) item);
            } else if (holder instanceof ArchiveViewHolder) {
                ((ArchiveViewHolder) holder).showContent((Archive) item);
            } else if (holder instanceof CollectionItemViewHolder) {
                ((CollectionItemViewHolder) holder).showContent((Collection) item);
            } else if (holder instanceof MomentHomeCameraViewHolder) {
                ((MomentHomeCameraViewHolder) holder).showContent((Moment) item);
            } else if (holder instanceof NothingMoreViewHolder) {
                ((NothingMoreViewHolder) holder).showContent(item);
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            if (item1 instanceof Moment && item2 instanceof Moment) {
                Moment m1 = (Moment) item1;
                Moment m2 = (Moment) item2;
                return -m1.getCreateDate().compareTo(m2.getCreateDate());
            }
            return 0;
        }
    }
}
