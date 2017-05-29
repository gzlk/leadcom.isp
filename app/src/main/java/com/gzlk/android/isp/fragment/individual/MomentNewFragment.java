package com.gzlk.android.isp.fragment.individual;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.user.MomentRequest;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.archive.PrivacyFragment;
import com.gzlk.android.isp.fragment.archive.UserPrivacyFragment;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.AttachItemViewHolder;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.ImageViewHolder;
import com.gzlk.android.isp.holder.SimpleClickableViewHolder;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.listener.RecycleAdapter;
import com.gzlk.android.isp.model.common.Attachment;
import com.gzlk.android.isp.model.common.BaiduLocation;
import com.gzlk.android.isp.model.common.Seclusion;
import com.gzlk.android.isp.model.user.Moment;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;

import java.util.ArrayList;

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

    private static final String PARAM_IMAGE = "mnf_initialized_image";
    private static final String PARAM_ADDRESS = "mnf_fetched_address";

    // UI
    @ViewId(R.id.ui_moment_new_text_content)
    private ClearEditText momentContent;

    private SimpleClickableViewHolder privacyHolder;
    private String[] textItems;
    private String address = "";
    private String privacy;

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
        address = bundle.getString(PARAM_ADDRESS, "");
        String json = bundle.getString(PARAM_IMAGE, EMPTY_ARRAY);
        ArrayList<String> images = Json.gson().fromJson(json, new TypeToken<ArrayList<String>>() {
        }.getType());
        getSelectedImages().clear();
        getSelectedImages().addAll(images);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(address, PARAM_ADDRESS);
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
        if (StringHelper.isEmpty(address)) {
            tryFetchingLocation();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void tryAddMoment() {
        if (StringHelper.isEmpty(momentContent.getValue())) {
            ToastHelper.make().showMsg(R.string.ui_text_new_moment_content_cannot_blank);
            return;
        }
        Utils.hidingInputBoard(momentContent);
        if (getSelectedImages().size() > 0) {
            // 如果选择了的图片大于1张，则需要压缩图片并且上传
            compressImage();
            uploadFiles();
        } else {
            addMoment(null);
        }
    }

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
            addMoment(temp);
        }
    };

    private void addMoment(ArrayList<String> images) {
        Seclusion seclusion = PrivacyFragment.getSeclusion(privacy);
        String content = StringHelper.escapeToHtml(momentContent.getValue());
        MomentRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Moment>() {
            @Override
            public void onResponse(Moment moment, boolean success, String message) {
                super.onResponse(moment, success, message);
                if (success) {
                    finish();
                }
            }
        }).add(address, content, images);
    }

    @Override
    protected void onFetchingLocationComplete(boolean success, BaiduLocation location) {
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
            privacyHolder = new SimpleClickableViewHolder(mRootView, MomentNewFragment.this);
            privacyHolder.addOnViewHolderClickListener(privacyListener);
            privacyHolder.showContent(format(textItems[1], "公开"));
        }
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            // 这里不需要直接上传，只需要把选择的图片传递给新建动态页面即可，上传在那里实现
            isSupportDirectlyUpload = true;
            // 图片选择后的回调
            addOnImageSelectedListener(albumImageSelectedListener);
            // 图片压缩完毕后的回调处理
            setOnImageCompressedListener(imageCompressedListener);
            // 文件上传完毕后的回调处理
            setOnFileUploadingListener(mOnFileUploadingListener);
            // 不需要下拉加载更多
            setSupportLoadingMore(false);
            mRecyclerView.addItemDecoration(new SpacesItemDecoration());
            mAdapter = new ImageAdapter();
            mRecyclerView.setAdapter(mAdapter);
            // 初始化时为空白
            resetImages(getSelectedImages());
        }
    }

    private void resetImages(ArrayList<String> images) {
        mAdapter.clear();
        for (String string : images) {
            mAdapter.add(string);
        }
        appendAttacher();
    }

    private void appendAttacher() {
        if (mAdapter.getItemCount() < getMaxSelectable()) {
            mAdapter.add("");
        }
    }

    // 相册选择返回了
    private OnImageSelectedListener albumImageSelectedListener = new OnImageSelectedListener() {
        @Override
        public void onImageSelected(ArrayList<String> selected) {
            resetImages(selected);
        }
    };

    private OnImageCompressedListener imageCompressedListener = new OnImageCompressedListener() {
        @Override
        public void onCompressed(ArrayList<String> compressed) {
            resetImages(compressed);
        }
    };

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == PrivacyFragment.REQUEST_SECURITY) {
            // 隐私设置返回了
            privacy = getResultedData(data);
        }
        super.onActivityResult(requestCode, data);
    }

    // 隐私设置点击了
    private OnViewHolderClickListener privacyListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Seclusion seclusion = PrivacyFragment.getSeclusion(privacy);
            openActivity(UserPrivacyFragment.class.getName(), String.valueOf(seclusion.getStatus()), PrivacyFragment.REQUEST_SECURITY, true, false);
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
            getSelectedImages().remove(url);
            mAdapter.remove(url);
            appendAttacher();
        }
    };

    private BaseViewHolder.OnHandlerBoundDataListener<String> handlerBoundDataListener = new BaseViewHolder.OnHandlerBoundDataListener<String>() {
        @Override
        public String onHandlerBoundData(BaseViewHolder holder) {
            return mAdapter.get(holder.getAdapterPosition());
        }
    };

    // 照片预览点击
    private ImageDisplayer.OnImageClickListener imagePreviewClickListener = new ImageDisplayer.OnImageClickListener() {
        @Override
        public void onImageClick(String url) {
            // 相册预览
            startGalleryPreview(getSelectedImages().indexOf(url));
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

    private class ImageAdapter extends RecyclerViewAdapter<BaseViewHolder, String> implements RecycleAdapter<String> {
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
        public void onBindHolderOfView(BaseViewHolder holder, int position, String item) {
            if (holder instanceof ImageViewHolder) {
                gotSize();
                ImageViewHolder ivh = (ImageViewHolder) holder;
                ivh.addOnDeleteClickListener(imageDeleteClickListener);
                ivh.addOnImageClickListener(imagePreviewClickListener);
                ivh.addOnHandlerBoundDataListener(handlerBoundDataListener);
                ivh.setImageSize(width, height);
                ivh.showContent(getSelectedImages().get(position));
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (StringHelper.isEmpty(get(position))) {
                return VT_ATTACH;
            } else {
                return VT_IMAGE;
            }
        }

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            gotSize();
            return viewType == VT_IMAGE ? new ImageViewHolder(itemView, MomentNewFragment.this) :
                    new AttachItemViewHolder(itemView, MomentNewFragment.this)
                            .setSize(width, height).setOnViewHolderClickListener(imagePickClickListener);
        }

        @Override
        protected int comparator(String item1, String item2) {
            return item1.compareTo(item2);
        }
    }
}
