package com.gzlk.android.isp.fragment.activity;

import android.os.Bundle;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseImageSelectableSupportFragment;
import com.gzlk.android.isp.helper.SimpleDialogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.common.SimpleClickableViewHolder;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.common.Attachment;
import com.hlk.hlklib.lib.inject.ViewId;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>活动logo拾取器<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/29 17:30 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/29 17:30 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class CoverPickFragment extends BaseImageSelectableSupportFragment {

    public static CoverPickFragment newInstance(String params) {
        CoverPickFragment cpf = new CoverPickFragment();
        Bundle bundle = new Bundle();
        // 传过来的params也即queryId就是已经设置好了的封面地址
        bundle.putString(PARAM_QUERY_ID, params);
        cpf.setArguments(bundle);
        return cpf;
    }

    // view
    @ViewId(R.id.ui_activity_cover_picker_from_gallery)
    private View galleryView;
    @ViewId(R.id.ui_activity_cover_picker_from_camera)
    private View cameraView;
    @ViewId(R.id.ui_activity_cover_picker_from_library)
    private View libraryView;

    // holder
    private SimpleClickableViewHolder galleryHolder;
    private SimpleClickableViewHolder cameraHolder;
    private SimpleClickableViewHolder libraryHolder;

    // data
    private String[] items;

    @Override
    public int getLayout() {
        return R.layout.fragment_activity_cover_picker;
    }

    @Override
    public void doingInResume() {
        // 只能选择一张图片
        maxSelectable = 1;
        setCustomTitle(R.string.ui_activity_cover_picker_fragment_title);
        setRightText(R.string.ui_base_text_confirm);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                // 确定的时候上传封面并返回到上一层
                confirmCover();
            }
        });
        initializeHolders();
        addOnImageSelectedListener(imageSelectedListener);
        setOnFileUploadingListener(onFileUploadingListener);
    }

    private void confirmCover() {
        if (getWaitingForUploadFiles().size() > 0) {
            uploadFiles();
        } else {
            warningNullCover();
        }
    }

    private void warningNullCover() {
        SimpleDialogHelper.init(Activity()).show(R.string.ui_activity_cover_picker_none_image_selected);
    }

    private OnFileUploadingListener onFileUploadingListener = new OnFileUploadingListener() {
        @Override
        public void onUploading(int all, int current, String file, long size, long uploaded) {

        }

        @Override
        public void onUploadingComplete(ArrayList<Attachment> uploaded) {
            resultData(uploaded.get(0).getUrl());
        }
    };

    private OnImageSelectedListener imageSelectedListener = new OnImageSelectedListener() {
        @Override
        public void onImageSelected(ArrayList<String> selected) {
            if (selected.size() > 0) {
                galleryHolder.showContent(format(items[0], ""));
                galleryHolder.showImage(selected.get(0));
            }
        }
    };

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    private void initializeHolders() {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_activity_cover_picker_items);
        }
        if (null == galleryHolder) {
            galleryHolder = new SimpleClickableViewHolder(galleryView, this);
            galleryHolder.addOnViewHolderClickListener(onViewHolderClickListener);
        }
        String fmt;
        if (isEmpty(mQueryId)) {
            // 如果图片地址为空则显示提醒
            fmt = format(items[0], StringHelper.getString(R.string.ui_activity_cover_picker_image_warning));
            galleryHolder.showContent(fmt);
            galleryHolder.showImage(null);
        } else {
            // 图片地址不为空则显示图片预览图
            fmt = format(items[0], "");
            galleryHolder.showContent(fmt);
            galleryHolder.showImage(mQueryId);
        }
        if (null == cameraHolder) {
            cameraHolder = new SimpleClickableViewHolder(cameraView, this);
            cameraHolder.addOnViewHolderClickListener(onViewHolderClickListener);
            cameraHolder.showContent(items[1]);
        }
        if (null == libraryHolder) {
            libraryHolder = new SimpleClickableViewHolder(libraryView, this);
            libraryHolder.addOnViewHolderClickListener(onViewHolderClickListener);
            libraryHolder.showContent(items[2]);
        }
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            switch (index) {
                case 0:
                    // 打开相册
                    startGalleryForResult();
                    break;
                case 1:
                    startCameraForResult();
                    break;
                case 2:
                    // 模板库
                    ToastHelper.make().showMsg("模板库");
                    break;
            }
        }
    };
}
