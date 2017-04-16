package com.gzlk.android.isp.fragment.individual;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.AttachItemViewHolder;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.ImageViewHolder;
import com.gzlk.android.isp.holder.SimpleClickableViewHolder;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.lib.view.LoadingMoreSupportedRecyclerView;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.listener.RecycleAdapter;
import com.hlk.hlklib.etc.Utility;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;

import java.util.ArrayList;
import java.util.List;

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
public class MomentNewFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_IMAGE = "initialized_image";

    // UI
    @ViewId(R.id.ui_moment_new_text_content)
    private ClearEditText momentContent;

    private SimpleClickableViewHolder privacyHolder;
    private String[] textItems;

    public static MomentNewFragment newInstance(String params) {
        MomentNewFragment mnf = new MomentNewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_IMAGE, params);
        mnf.setArguments(bundle);
        return mnf;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        layoutType = TYPE_GRID;
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        String image = bundle.getString(PARAM_IMAGE, "");
        if (!StringHelper.isEmpty(image)) {
            if (!cachedImages.contains(image)) {
                cachedImages.add(image);
            }
        }
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_moment_new;
    }

    @Override
    protected void onSwipeRefreshing() {

    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    public void doingInResume() {
        setLeftIcon(0);
        setLeftText(R.string.ui_base_text_cancel);
        setCustomTitle(R.string.ui_text_new_moment_fragment_title);
        setRightIcon(0);
        setRightText(R.string.ui_base_text_send);
        initializeHolder();
        initializeAdapter();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    private void initializeHolder() {
        if (null == textItems) {
            textItems = Activity().app().getResources().getStringArray(R.array.ui_individual_new_moment);
        }

        if (null == privacyHolder) {
            privacyHolder = new SimpleClickableViewHolder(mRootView, MomentNewFragment.this);
            privacyHolder.showContent(format(textItems[0], "公开"));
        }
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            // 这里不需要直接上传，只需要把选择的图片传递给新建动态页面即可，上传在那里实现
            isSupportDirectlyUpload = false;
            // 添加图片选择
            addOnImageSelectedListener(imageSelectedListener);
            // 不需要下拉加载更多
            mRecyclerView.setSupportLoadingMore(false);
            mRecyclerView.addItemDecoration(new SpacesItemDecoration());
            mAdapter = new ImageAdapter();
            mRecyclerView.setAdapter(mAdapter);
            resetImages();
        }
    }

    private List<String> images = new ArrayList<>();

    private void resetImages() {
        mAdapter.clear();
        for (String string : cachedImages) {
            mAdapter.add(string);
        }
    }

    private OnImageSelectedListener imageSelectedListener = new OnImageSelectedListener() {
        @Override
        public void onImageSelected(String compressed) {
            if (!StringHelper.isEmpty(compressed)) {
                mAdapter.add(compressed);
            }
        }
    };

    private OnViewHolderClickListener clickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            mAdapter.add("/storage/emulated/0/DCIM/20170416_193204.jpg");
        }
    };

    private ImageDisplayer.OnDeleteClickListener deleteClickListener = new ImageDisplayer.OnDeleteClickListener() {
        @Override
        public void onDeleteClick(String url) {
            cachedImages.remove(url);
            mAdapter.remove(url);
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

    private class ImageAdapter extends LoadingMoreSupportedRecyclerView.LoadingMoreAdapter<BaseViewHolder> implements RecycleAdapter<String> {
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
        public boolean isFirstItemFullLine() {
            return false;
        }

        @Override
        public int itemLayout(int viewType) {
            return viewType == VT_IMAGE ? R.layout.holder_view_image : R.layout.holder_view_attach_item;
        }

        @Override
        public int gotItemCount() {
            int size = images.size();
            return size + (size < maxCachedImage ? 1 : 0);// 小于最大数限制时最后一个是+号
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position) {
            if (holder instanceof ImageViewHolder) {
                gotSize();
                ImageViewHolder ivh = (ImageViewHolder) holder;
                ivh.addOnDeleteClickListener(deleteClickListener);
                ivh.setImageSize(width, height);
                ivh.showContent(images.get(position));
            }
        }

        @Override
        public int gotItemViewType(int position) {
            int size = images.size();
            if (size < maxCachedImage) {
                // size小于最大数时，最后一个是+号
                return position < size ? VT_IMAGE : VT_ATTACH;
            } else {
                // 大于等于9时，直接显示图片
                return size == maxCachedImage ? VT_IMAGE : VT_ATTACH;
            }
        }

        @Override
        public BaseViewHolder footerViewHolder(View itemView) {
            return null;
        }

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            gotSize();
            return viewType == VT_IMAGE ? new ImageViewHolder(itemView, MomentNewFragment.this) :
                    new AttachItemViewHolder(itemView, MomentNewFragment.this).setSize(width, height).setOnViewHolderClickListener(clickListener);
        }

        @Override
        public void clear() {
            int size = images.size();
            while (size > 0) {
                remove(size - 1);
                size = images.size();
            }
        }

        @Override
        public void remove(int position) {
            images.remove(position);
            notifyItemRemoved(position);
        }

        @Override
        public void remove(String item) {
            remove(images.indexOf(item));
        }

        @Override
        public void add(String item) {
            images.add(item);
            notifyItemInserted(images.size() - 1);
        }

        @Override
        public void add(String item, int position) {

        }

        @Override
        public boolean exist(String item) {
            return false;
        }
    }
}
