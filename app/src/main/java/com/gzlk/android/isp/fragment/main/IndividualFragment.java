package com.gzlk.android.isp.fragment.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.archive.ArchiveRequest;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.user.CollectionRequest;
import com.gzlk.android.isp.api.user.MomentRequest;
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
import com.gzlk.android.isp.holder.individual.CollectionItemViewHolder;
import com.gzlk.android.isp.holder.individual.IndividualFunctionViewHolder;
import com.gzlk.android.isp.holder.individual.IndividualHeaderViewHolder;
import com.gzlk.android.isp.holder.individual.MomentViewHolder;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.listener.OnHandleBoundDataListener;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.user.Collection;
import com.gzlk.android.isp.model.user.Moment;
import com.gzlk.android.isp.model.user.User;

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
        MomentRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Moment>() {

            @SuppressWarnings("unchecked")
            @Override
            public void onResponse(List<Moment> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                adjustRemotePages(null == list ? 0 : list.size(), pageSize);
                if (success) {
                    if (null != list) {
                        if (selectedFunction == 0) {
                            for (Moment moment : list) {
                                adapter.update(moment);
                            }
                        }
                    }
                }
            }
        }).list(Cache.cache().userId, remotePageNumber);
    }

    /**
     * 拉取我的档案列表
     */
    private void refreshingRemoteDocuments() {
        ArchiveRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Archive>() {
            @SuppressWarnings("unchecked")
            @Override
            public void onResponse(List<Archive> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                adjustRemotePages(null == list ? 0 : list.size(), pageSize);
                if (success) {
                    if (null != list) {
                        if (selectedFunction == 1) {
                            for (Archive archive : list) {
                                adapter.update(archive);
                            }
                        }
                    }
                }
            }
        }).list(remotePageNumber, Cache.cache().userId);
    }

    private void refreshingFavorites() {
        CollectionRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Collection>() {
            @SuppressWarnings("unchecked")
            @Override
            public void onResponse(List<Collection> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                adjustRemotePages(null == list ? 0 : list.size(), pageSize);
                if (success) {
                    if (null != list) {
                        if (selectedFunction == 2) {
                            for (Collection collection : list) {
                                adapter.update(collection);
                            }
                        }
                    }
                }
            }
        }).list(Collection.Type.ALL_ARCHIVE, CollectionRequest.OPE_MONTH, remotePageNumber);
    }

    private void adjustRemotePages(int fetchedCount, int pageSize) {
        // 如果当前拉取的是满页数据，则下次再拉取的时候拉取下一页
        remotePageNumber += (fetchedCount >= pageSize ? 1 : 0);
        isLoadingComplete(fetchedCount < pageSize);
        stopRefreshing();
    }

    private IndividualAdapter adapter;
    private Model functions;
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

    private MomentViewHolder.OnGotPositionListener gotPositionListener = new MomentViewHolder.OnGotPositionListener() {
        @Override
        public Moment previous(int myPosition) {
            if (myPosition > 1) {
                Model model = adapter.get(myPosition - 1);
                if (model instanceof Moment) {
                    return (Moment) model;
                }
            }
            return null;
        }
    };

    private OnHandleBoundDataListener<Model> boundMomentDataListener = new OnHandleBoundDataListener<Model>() {
        @Override
        public Moment onHandlerBoundData(BaseViewHolder holder) {
            Model model = adapter.get(holder.getAdapterPosition());
            return (model instanceof Moment) ? ((Moment) model) : null;
        }
    };

    private OnHandleBoundDataListener<Model> boundDocumentListener = new OnHandleBoundDataListener<Model>() {
        @Override
        public Archive onHandlerBoundData(BaseViewHolder holder) {
            Model model = adapter.get(holder.getAdapterPosition());
            return (model instanceof Archive) ? ((Archive) model) : null;
        }
    };

    private OnHandleBoundDataListener<Model> boundCollectionListener = new OnHandleBoundDataListener<Model>() {
        @Override
        public Collection onHandlerBoundData(BaseViewHolder holder) {
            Model model = adapter.get(holder.getAdapterPosition());
            return (model instanceof Collection) ? ((Collection) model) : null;
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

    private void momentClick(Moment moment) {
        if (null != moment) {
            // 点击打开新窗口查看详情
            if (moment.getId().contains("today's")) {
                openImageSelector(true);
            } else {
                // 默认显示第一张图片
                MomentImagesFragment.open(IndividualFragment.this, moment.getId(), 0);
                //openActivity(MomentImagesFragment.class.getName(), format("%s,0", moment.getId()), true, false);
            }
        }
    }

    private void archiveClick(Archive archive) {
        if (null != archive) {
            int type = isEmpty(archive.getGroupId()) ? Archive.Type.USER : Archive.Type.GROUP;
            ArchiveDetailsFragment.open(IndividualFragment.this, type, archive.getId(), REQUEST_CHANGE);
            //openActivity(ArchiveDetailsFragment.class.getName(), format("%d,%s", type, archive.getId()), BaseFragment.REQUEST_CHANGE, true, false);
        }
    }

    private class IndividualAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {

        private static final int VT_HEADER = 0, VT_FUNCTION = 1, VT_MOMENT = 2, VT_ARCHIVE = 3, VT_COLLECTION = 4;

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
                    MomentViewHolder mvh = new MomentViewHolder(itemView, fragment);
                    mvh.addOnViewHolderClickListener(onViewHolderClickListener);
                    mvh.addOnGotPositionListener(gotPositionListener);
                    return mvh;
                case VT_ARCHIVE:
                    ArchiveViewHolder holder = new ArchiveViewHolder(itemView, fragment);
                    holder.addOnViewHolderClickListener(onViewHolderClickListener);
                    return holder;
                case VT_COLLECTION:
                    CollectionItemViewHolder civh = new CollectionItemViewHolder(itemView, fragment);
                    civh.addOnViewHolderClickListener(onViewHolderClickListener);
                    return civh;
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
                    return R.layout.holder_view_moment;
                case VT_ARCHIVE:
                    return R.layout.holder_view_document;
                default:
                    return R.layout.holder_view_collection;
            }
        }

        @Override
        public int getItemViewType(int position) {
            Model model = get(position);
            if (model instanceof User) {
                return VT_HEADER;
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
            } else if (holder instanceof MomentViewHolder) {
                ((MomentViewHolder) holder).showContent((Moment) item);
            } else if (holder instanceof ArchiveViewHolder) {
                ((ArchiveViewHolder) holder).showContent((Archive) item);
            } else if (holder instanceof CollectionItemViewHolder) {
                ((CollectionItemViewHolder) holder).showContent((Collection) item);
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
