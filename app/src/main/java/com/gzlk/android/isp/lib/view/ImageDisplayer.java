package com.gzlk.android.isp.lib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gzlk.android.isp.R;
import com.hlk.hlklib.lib.view.CorneredView;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

/**
 * <b>功能描述：</b>图片显示工具<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/14 09:27 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/14 09:27 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ImageDisplayer extends RelativeLayout {

    public ImageDisplayer(Context context) {
        this(context, null);
    }

    public ImageDisplayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageDisplayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ImageDisplayer, defStyleAttr, 0);
        try {
            displayType = array.getInt(R.styleable.ImageDisplayer_id_image_type, 0);
            initializeViews();

            if (array.hasValue(R.styleable.ImageDisplayer_id_image_src)) {
                srcDrawable = array.getResourceId(R.styleable.ImageDisplayer_id_image_src, 0);
                if (0 < srcDrawable) {
                    imageView.setImageResource(srcDrawable);
                }
            }
            displayUrl = array.getString(R.styleable.ImageDisplayer_id_image_url);

            String string = array.getString(R.styleable.ImageDisplayer_id_delete_icon);
            deleteIconTextView.setText(string);
            string = array.getString(R.styleable.ImageDisplayer_id_select_icon);
            selectIconTextView.setText(string);

            showDelete = array.getBoolean(R.styleable.ImageDisplayer_id_show_delete, false);
            deleteContainer.setVisibility(showDelete ? VISIBLE : GONE);

            showSelect = array.getBoolean(R.styleable.ImageDisplayer_id_show_select, false);
            selectContainer.setVisibility(showSelect ? VISIBLE : GONE);

            selected = array.getBoolean(R.styleable.ImageDisplayer_id_selected, false);
            setSelected();

            imageSize = array.getDimensionPixelSize(R.styleable.ImageDisplayer_id_image_size, 0);
            if (imageSize > 0) {
                imageWidth = imageSize;
                imageHeight = imageSize;
            } else {
                imageWidth = array.getDimensionPixelOffset(R.styleable.ImageDisplayer_id_image_width, 0);
                imageHeight = array.getDimensionPixelOffset(R.styleable.ImageDisplayer_id_image_height, 0);
            }
        } finally {
            array.recycle();
        }
    }

    private int srcDrawable = 0;
    private RoundedImageView imageView;
    private CircleProgressBar progressBar;
    private CorneredView selectContainer, deleteContainer;
    private CustomTextView deleteIconTextView, selectIconTextView;

    private void initializeViews() {
        View view = LayoutInflater.from(getContext()).inflate(getLayout(), this);
        imageView = (RoundedImageView) view.findViewById(R.id.ui_tool_image_view_image);
        progressBar = (CircleProgressBar) view.findViewById(R.id.ui_tool_image_view_loading);
        deleteContainer = (CorneredView) view.findViewById(R.id.ui_tool_image_view_delete_container);
        deleteIconTextView = (CustomTextView) view.findViewById(R.id.ui_tool_image_view_delete_icon);
        selectContainer = (CorneredView) view.findViewById(R.id.ui_tool_image_view_select_container);
        selectIconTextView = (CustomTextView) view.findViewById(R.id.ui_tool_image_view_select_icon);

        imageView.setOnClickListener(onClickListener);
        deleteContainer.setOnClickListener(onClickListener);
        selectContainer.setOnClickListener(onClickListener);
        onAttachedToWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!TextUtils.isEmpty(displayUrl)) {
            displayImage(displayUrl, imageWidth, imageHeight, showSelect, showDelete);
        } else {
            imageView.setImageResource(0 < srcDrawable ? srcDrawable : R.mipmap.img_default_user_header);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        // 显示空资源以便释放缓存
        imageView.setImageDrawable(null);
        super.onDetachedFromWindow();
        // 释放图片时gc一下
        //System.gc();
    }

    /**
     * 图片的url地址
     */
    private String displayUrl;
    private int imageSize, imageWidth, imageHeight;
    private boolean showDelete, showSelect, selected;

    /**
     * 设置是否已选中
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
        setSelected();
    }

    private void setSelected() {
        if (showSelect) {
            selectContainer.setBackground(ContextCompat.getColor(getContext(), selected ? R.color.colorPrimary : R.color.textColorHintLight));
        }
    }

    /**
     * 是否已选中
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * 显示图片
     */
    public void displayImage(String url, int size, boolean selectable, boolean deletable) {
        imageSize = size;
        displayImage(url, size, size, selectable, deletable);
    }

    /**
     * 显示图片
     */
    public void displayImage(String url, int width, int height, boolean selectable, boolean deletable) {
        imageWidth = width;
        imageHeight = height;
        showDelete = deletable;
        showSelect = selectable;
        displayUrl = url;
        displayImage();
    }

    private void displayImage() {
        if (TextUtils.isEmpty(displayUrl) || displayUrl.length() < 5) {
            imageView.setImageResource(R.mipmap.img_image_loading_fail);
        } else {
            String url = displayUrl;
            if (!url.contains("://")) {
                // 默认显示本地图片
                url = "file://" + displayUrl;
                //imageView.setImageResource(R.mipmap.img_image_loading_fail);
            } //else {
            // String url = LxbgApp.getInstance().gotFullDownloadUrl(image);
            // http://
            // drawable://  ex.: "drawable://" + R.drawable.image
            // assets://image.png
            // file:///mnt/sdcard/image.png  ex.: "file://" + uri(string)
            // content://media/external/audio/albumart/13

            ImageLoader.getInstance().displayImage(url, new ImageViewAware(imageView),
                    null, new ImageSize(imageWidth, imageHeight),
                    mImageLoadingListener, mImageLoadingProgressListener);
            //}
        }
    }

    private ImageLoadingListener mImageLoadingListener = new ImageLoadingListener() {

        @Override
        public void onLoadingStarted(String imageUri, View view) {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            progressBar.setVisibility(View.GONE);
        }
    };

    private ImageLoadingProgressListener mImageLoadingProgressListener = new ImageLoadingProgressListener() {

        @Override
        public void onProgressUpdate(String imageUri, View view, int current, int total) {
            progressBar.setProgress(current / total);
        }
    };


    /**
     * 正常显示方式
     */
    public static final int TYPE_NORMAL = 0;
    /**
     * 边角显示方式
     */
    public static final int TYPE_ROUNDED = 1;
    /**
     * 圆形显示方式
     */
    public static final int TYPE_OVAL = 2;

    /**
     * 图片显示方式
     */
    @IntDef({TYPE_NORMAL, TYPE_ROUNDED, TYPE_OVAL})
    public @interface DisplayType {
    }

    /**
     * 图片的显示方式
     */
    private int displayType;

    private int getLayout() {
        switch (displayType) {
            case TYPE_ROUNDED:
                return R.layout.tool_view_image_rounded;
            case TYPE_OVAL:
                return R.layout.tool_view_image_oval;
            default:
                return R.layout.tool_view_image_normal;
        }
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == deleteContainer && null != deleteClickListener) {
                deleteClickListener.onDeleteClick(displayUrl);
            } else if (v == imageView && null != imageClickListener) {
                imageClickListener.onImageClick(displayUrl);
            } else if (v == selectContainer && null != selectorClickListener) {
                setSelected(!selected);
                selectorClickListener.onSelectorClick(displayUrl, selected);
            }
        }
    };

    private OnImageClickListener imageClickListener;

    /**
     * 添加图片点击事件回调
     */
    public void addOnImageClickListener(OnImageClickListener l) {
        imageClickListener = l;
    }

    /**
     * 图片点击事件接口
     */
    public interface OnImageClickListener {
        /**
         * 图片点击了
         */
        void onImageClick(String url);
    }

    private OnSelectorClickListener selectorClickListener;

    /**
     * 添加选择事件回调
     */
    public void addOnSelectorClickListener(OnSelectorClickListener l) {
        selectorClickListener = l;
    }

    /**
     * 选择事件处理接口
     */
    public interface OnSelectorClickListener {
        void onSelectorClick(String url, boolean selected);
    }

    private OnDeleteClickListener deleteClickListener;

    /**
     * 添加删除事件回调
     */
    public void addOnDeleteClickListener(OnDeleteClickListener l) {
        deleteClickListener = l;
    }

    /**
     * 删除事件处理接口
     */
    public interface OnDeleteClickListener {
        void onDeleteClick(String url);
    }
}
