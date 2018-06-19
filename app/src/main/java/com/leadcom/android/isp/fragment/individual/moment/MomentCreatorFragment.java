package com.leadcom.android.isp.fragment.individual.moment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.gson.reflect.TypeToken;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.user.MomentRequest;
import com.leadcom.android.isp.fragment.archive.PrivacyFragment;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.attachment.AttacherItemViewHolder;
import com.leadcom.android.isp.holder.common.SimpleClickableViewHolder;
import com.leadcom.android.isp.holder.individual.ImageViewHolder;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.common.HLKLocation;
import com.leadcom.android.isp.model.common.Seclusion;
import com.leadcom.android.isp.model.user.Moment;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * <b>功能描述：</b>个人 - 添加新的动态<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/16 14:21 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/16 14:21 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class MomentCreatorFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_IMAGE = "mnf_initialized_image";
    private static final String PARAM_ADDRESS = "mnf_fetched_address";
    private static final String PARAM_PRIVACY = "mnf_privacy";

    // UI
    @ViewId(R.id.ui_moment_new_text_content)
    private ClearEditText momentContent;
    @ViewId(R.id.ui_moment_new_drag_flag)
    private View dragFlag;

    private SimpleClickableViewHolder privacyHolder;
    private String[] textItems;
    private String address = "";
    private String privacy = "";
    private String imageJson = "[]";

    public static MomentCreatorFragment newInstance(Bundle bundle) {
        MomentCreatorFragment mnf = new MomentCreatorFragment();
        mnf.setArguments(bundle);
        return mnf;
    }

    public static void open(BaseFragment fragment, String jsonSelected) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_IMAGE, jsonSelected);
        fragment.openActivity(MomentCreatorFragment.class.getName(), bundle, REQUEST_CREATE, true, true);
    }

    public static void open(Context context, String jsonSelected) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_IMAGE, jsonSelected);
        BaseActivity.openActivity(context, MomentCreatorFragment.class.getName(), bundle, REQUEST_CREATE, true, true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        layoutType = TYPE_GRID;
        gridSpanCount = 3;
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        address = bundle.getString(PARAM_ADDRESS, "");
        imageJson = bundle.getString(PARAM_IMAGE, EMPTY_ARRAY);
        ArrayList<String> images = Json.gson().fromJson(imageJson, new TypeToken<ArrayList<String>>() {
        }.getType());
        if (images.size() > 0) {
            waitingFroCompressImages.clear();
            waitingFroCompressImages.addAll(images);
//            getWaitingForUploadFiles().clear();
//            getWaitingForUploadFiles().addAll(images);
        }
        imageJson = "[]";
        privacy = bundle.getString(PARAM_PRIVACY, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_IMAGE, imageJson);
        bundle.putString(PARAM_ADDRESS, address);
        bundle.putString(PARAM_PRIVACY, privacy);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_individual_moment_creator;
    }

    @Override
    protected void onSwipeRefreshing() {

    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    @Override
    public void doingInResume() {
        setLeftIcon(0);
        setLeftText(R.string.ui_base_text_cancel);
        setCustomTitle(R.string.ui_text_new_moment_fragment_title);
        setRightIcon(0);
        setRightText(R.string.ui_base_text_send);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                tryAddMoment();
            }
        });
        initializeHolder();
        initializeAdapter();
        if (isEmpty(address)) {
            tryFetchingLocation();
        }
    }

    private void tryAddMoment() {
//        Utils.hidingInputBoard(momentContent);
        //if (getWaitingForUploadFiles().size() > 0) {
        if (waitingFroCompressImages.size() > 0) {
            // 有图片时，可以不必填写动态内容
            // 重置用户设定的顺序
            retreatList();
            // 如果选择了的图片大于1张，则需要压缩图片并且上传
            compressImage();
            //uploadFiles();
        } else {
            if (StringHelper.isEmpty(momentContent.getValue())) {
                ToastHelper.make().showMsg(R.string.ui_text_new_moment_content_cannot_blank);
                return;
            }
            addMoment(null, "");
        }
    }

    private OnImageCompressedListener onImageCompressedListener = new OnImageCompressedListener() {
        @Override
        public void onCompressed(ArrayList<String> compressed) {
            //uploadFiles();
        }
    };

    private OnFileUploadingListener mOnFileUploadingListener = new OnFileUploadingListener() {
        @Override
        public void onUploading(int all, int current, String file, long size, long uploaded) {

        }

        @Override
        public void onUploadingComplete(ArrayList<Attachment> uploaded) {
            ArrayList<String> temp = new ArrayList<>();
            for (Attachment att : uploaded) {
                temp.add(att.getUrl());
            }
            addMoment(temp, Attachment.getFieldIds(uploaded));
        }
    };

    private void addMoment(ArrayList<String> images, String fileIds) {
        Seclusion seclusion = PrivacyFragment.getSeclusion(privacy);
        String content = momentContent.getValue();
        int auth = seclusion.getStatus();
        if (auth == Seclusion.Type.Private) {
            auth = 2;
        }
        MomentRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Moment>() {
            @Override
            public void onResponse(Moment moment, boolean success, String message) {
                super.onResponse(moment, success, message);
                if (success) {
                    resultSucceededActivity();
                }
            }
        }).add(address, content, images, "", auth, fileIds);
    }

    @Override
    protected void onFetchingLocationComplete(boolean success, HLKLocation location) {
        address = location.getAddress();
        log(address);
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    private void initializeHolder() {
        if (null == textItems) {
            textItems = StringHelper.getStringArray(R.array.ui_individual_new_moment);
        }

        if (null == privacyHolder) {
            privacyHolder = new SimpleClickableViewHolder(mRootView, MomentCreatorFragment.this);
            privacyHolder.addOnViewHolderClickListener(privacyListener);
        }
        privacyHolder.showContent(format(textItems[1], PrivacyFragment.getPrivacy(PrivacyFragment.getSeclusion(privacy))));
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            isSupportCompress = true;
            // 图片选择后的回调
            addOnImageSelectedListener(albumImageSelectedListener);
            // 图片压缩完毕后的回调处理
            //setOnImageCompressedListener(onImageCompressedListener);
            // 文件上传完毕后的回调处理
            setOnFileUploadingListener(mOnFileUploadingListener);
            // 不需要下拉加载更多
            setSupportLoadingMore(false);
            mRecyclerView.addItemDecoration(new SpacesItemDecoration());
            mAdapter = new ImageAdapter();
            mRecyclerView.setAdapter(mAdapter);
            //1.创建item helper
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
            //2.绑定到recyclerview上面去
            itemTouchHelper.attachToRecyclerView(mRecyclerView);
            //3.在ItemHelper的接口回调中过滤开启长按拖动，拓展其他操作
            // 初始化时为空白
            //resetImages(getWaitingForUploadFiles());
            resetImages(waitingFroCompressImages);
        }
    }

    private ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {

        /**
         * 官方文档的说明如下：
         * o control which actions user can take on each view, you should override getMovementFlags(RecyclerView, ViewHolder)
         * and return appropriate set of direction flags. (LEFT, RIGHT, START, END, UP, DOWN).
         * 返回我们要监控的方向，上下左右，我们做的是上下拖动，要返回都是UP和DOWN
         * 关键坑爹的是下面方法返回值只有1个，也就是说只能监控一个方向。
         * 不过点入到源码里面有惊喜。源码标记方向如下：
         *  public static final int UP = 1     0001
         *  public static final int DOWN = 1 << 1; （位运算：值其实就是2）0010
         *  public static final int LEFT = 1 << 2   左 值是3
         *  public static final int RIGHT = 1 << 3  右 值是8
         */
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            // 如果想支持滑动(删除)操作, swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END
            int swipeFlags = 0;
            int dragFlags = 0;
            if (mAdapter.getItemCount() <= 2 || mAdapter.get(viewHolder.getAdapterPosition()).getId().equals("+")) {
                // 如果是 + 号，则不需要挪动
                return makeMovementFlags(dragFlags, swipeFlags);
            }
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager || manager instanceof StaggeredGridLayoutManager || manager instanceof FlexboxLayoutManager) {
                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            } else {
                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            }
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        /**
         * 官方文档的说明如下
         * If user drags an item, ItemTouchHelper will call onMove(recyclerView, dragged, target). Upon receiving this callback,
         * you should move the item from the old position (dragged.getAdapterPosition()) to new position (target.getAdapterPosition())
         * in your adapter and also call notifyItemMoved(int, int).
         * 拖动某个item的回调，在return前要更新item位置，调用notifyItemMoved（draggedPosition，targetPosition）
         * viewHolde: 正在拖动item
         * target：要拖到的目标
         * @return true 表示消费事件
         */
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            int pos1 = viewHolder.getAdapterPosition(), pos2 = target.getAdapterPosition();
            if (mAdapter.get(pos2).getId().equals("+")) {
                // 如果是 + 号，则不需要排序
                return false;
            }
            // 直接按照文档来操作啊，这文档写得太给力了,简直完美！
            mAdapter.notifyItemMoved(pos1, pos2);
            // 注意这里有个坑的，itemView 都移动了，对应的数据也要移动
            mAdapter.swap(pos1, pos2);
            log(format("swap %d to %d", pos1, pos2));
            return true;
        }

        /**
         * 谷歌官方文档说明如下：
         * 这个看了一下主要是做左右拖动的回调
         * When a View is swiped, ItemTouchHelper animates it until it goes out of bounds, then calls onSwiped(ViewHolder, int).
         * At this point, you should update your adapter (e.g. remove the item) and call related Adapter#notify event.
         */
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        }

        /**
         * 官方文档如下：返回true 当前tiem可以被拖动到目标位置后，直接”落“在target上，其他的上面的item跟着“落”，
         * 所以要重写这个方法，不然只是拖动的tiem在动，target tiem不动，静止的
         * Return true if the current ViewHolder can be dropped over the the target ViewHolder.
         */
        @Override
        public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
            return true;
        }

        /**
         * 官方文档说明如下：
         * Returns whether ItemTouchHelper should start a drag and drop operation if an item is long pressed.
         * 是否开启长按 拖动
         */
        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }
    };

    private void resetImages(ArrayList<String> images) {
        mAdapter.clear();
        for (String string : images) {
            Model model = new Model();
            model.setId(string);
            mAdapter.add(model);
        }
        appendAttacher();
    }

    private void retreatList() {
        waitingFroCompressImages.clear();
        Iterator<Model> iterator = mAdapter.iterator();
        while (iterator.hasNext()) {
            Model model = iterator.next();
            if (!model.getId().equals("+")) {
                waitingFroCompressImages.add(model.getId());
            }
        }
    }

    private Model appender;

    private Model appender() {
        if (null == appender) {
            appender = new Model();
            appender.setId("+");
        }
        return appender;
    }

    private void appendAttacher() {
        if (mAdapter.getItemCount() < getMaxSelectable()) {
            mAdapter.add(appender());
        }
        dragFlag.setVisibility(mAdapter.getItemCount() >= 3 ? View.VISIBLE : View.GONE);
    }

    // 相册选择返回了
    private OnImageSelectedListener albumImageSelectedListener = new OnImageSelectedListener() {
        @Override
        public void onImageSelected(ArrayList<String> selected) {
            resetImages(selected);
        }
    };

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == REQUEST_SECURITY) {
            // 隐私设置返回了
            privacy = getResultedData(data);
            privacyHolder.showContent(format(textItems[1], PrivacyFragment.getPrivacy(PrivacyFragment.getSeclusion(privacy))));
        }
        super.onActivityResult(requestCode, data);
    }

    // 隐私设置点击了
    private OnViewHolderClickListener privacyListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Seclusion seclusion = PrivacyFragment.getSeclusion(privacy);
            PrivacyFragment.open(MomentCreatorFragment.this, seclusion, true);
            //openActivity(UserPrivacyFragment.class.getName(), json, REQUEST_SECURITY, true, false);
            //ToastHelper.make(Activity()).showMsg("隐私设置");
        }
    };

    // 需要增加照片
    private OnViewHolderClickListener imagePickClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            // 需要重新再选择图片
            startGalleryForResult();
        }
    };

    // 照片删除
    private ImageDisplayer.OnDeleteClickListener imageDeleteClickListener = new ImageDisplayer.OnDeleteClickListener() {
        @Override
        public void onDeleteClick(String url) {
            //getWaitingForUploadFiles().remove(url);
            waitingFroCompressImages.remove(url);
            mAdapter.remove(url);
            appendAttacher();
        }
    };

    // 照片预览点击
    private ImageDisplayer.OnImageClickListener imagePreviewClickListener = new ImageDisplayer.OnImageClickListener() {
        @Override
        public void onImageClick(ImageDisplayer displayer, String url) {
            // 相册预览
            //startGalleryPreview(getWaitingForUploadFiles().indexOf(url));
            startGalleryPreview(waitingFroCompressImages.indexOf(url));
        }
    };

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int dimen = getDimension(R.dimen.ui_base_dimen_margin_padding);
            int position = parent.getChildAdapterPosition(view);
            outRect.bottom = 0;
            outRect.left = 0;
            GridLayoutManager manager = (GridLayoutManager) parent.getLayoutManager();
            int spanCount = manager.getSpanCount();
            // 第一行有顶部无空白，其余行顶部有空白
            outRect.top = (position / spanCount == 0) ? 0 : dimen;
            // 最后列右侧无空白，其余列右侧有空白
            outRect.right = (position % spanCount < (spanCount - 1)) ? dimen : 0;
        }
    }

    private ImageAdapter mAdapter;

    private class ImageAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {
        private static final int VT_IMAGE = 0, VT_ATTACH = 1;

        private int width, height;

        private void gotSize() {
            if (width == 0) {
                int _width = getScreenWidth();
                int padding = getDimension(R.dimen.ui_base_dimen_margin_padding) * (2 + gridSpanCount - 1);
                int size = (_width - padding) / gridSpanCount;
                width = size;
                height = size;
            }
        }

        @Override
        public int itemLayout(int viewType) {
            return viewType == VT_IMAGE ? R.layout.holder_view_image : R.layout.holder_view_attach_item;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, Model item) {
            if (holder instanceof ImageViewHolder) {
                gotSize();
                ImageViewHolder ivh = (ImageViewHolder) holder;
                ivh.addOnDeleteClickListener(imageDeleteClickListener);
                ivh.addOnImageClickListener(imagePreviewClickListener);
                ivh.setImageSize(width, height);
                ivh.showContent(item.getId());
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (get(position).getId().equals("+")) {
                return VT_ATTACH;
            } else {
                return VT_IMAGE;
            }
        }

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            gotSize();
            return viewType == VT_IMAGE ? new ImageViewHolder(itemView, MomentCreatorFragment.this) :
                    new AttacherItemViewHolder(itemView, MomentCreatorFragment.this)
                            .setSize(width, height).setOnViewHolderClickListener(imagePickClickListener);
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return item1.getId().compareTo(item2.getId());
        }
    }
}
