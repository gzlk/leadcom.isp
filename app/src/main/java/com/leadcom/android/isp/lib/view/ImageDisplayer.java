package com.leadcom.android.isp.lib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.helper.LogHelper;
import com.leadcom.android.isp.helper.StringHelper;
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
        loadingFailedTimes = 0;
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ImageDisplayer, defStyleAttr, 0);
        try {
            displayType = array.getInt(R.styleable.ImageDisplayer_id_image_type, 0);
            initializeViews();

            if (array.hasValue(R.styleable.ImageDisplayer_id_image_src)) {
                srcDrawable = array.getResourceId(R.styleable.ImageDisplayer_id_image_src, 0);
                displayDrawable();
            }
            displayUrl = array.getString(R.styleable.ImageDisplayer_id_image_url);
            defaultNullable = array.getBoolean(R.styleable.ImageDisplayer_id_nullable_drawable, true);

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

            showLoading = array.getBoolean(R.styleable.ImageDisplayer_id_show_loading, true);
            imageSize = array.getDimensionPixelSize(R.styleable.ImageDisplayer_id_image_size, 0);
            if (imageSize > 0) {
                imageWidth = imageSize;
                imageHeight = imageSize;
            } else {
                imageWidth = array.getDimensionPixelOffset(R.styleable.ImageDisplayer_id_image_width, 0);
                imageHeight = array.getDimensionPixelOffset(R.styleable.ImageDisplayer_id_image_height, 0);
            }
            scaleType = array.getInt(R.styleable.ImageDisplayer_id_image_scale_type, 0);
            isShowHeader = array.getBoolean(R.styleable.ImageDisplayer_id_show_header, false);
        } finally {
            array.recycle();
        }
    }

    private int srcDrawable = 0, scaleType;
    // 加载错误的次数
    private int loadingFailedTimes = 0;
    private boolean isShowHeader;
    private RoundedImageView imageView;
    private CircleProgressBar progressBar;
    private CorneredView selectContainer, deleteContainer;
    private CustomTextView deleteIconTextView, selectIconTextView;

    private void initializeViews() {
        View view = LayoutInflater.from(getContext()).inflate(getLayout(), this);
        imageView = view.findViewById(R.id.ui_tool_image_view_image);
        progressBar = view.findViewById(R.id.ui_tool_image_view_loading);
        deleteContainer = view.findViewById(R.id.ui_tool_image_view_delete_container);
        deleteIconTextView = view.findViewById(R.id.ui_tool_image_view_delete_icon);
        selectContainer = view.findViewById(R.id.ui_tool_image_view_select_container);
        selectIconTextView = view.findViewById(R.id.ui_tool_image_view_select_icon);

        imageView.setOnClickListener(onClickListener);
        deleteContainer.setOnClickListener(onClickListener);
        selectContainer.setOnClickListener(onClickListener);
        onAttachedToWindow();
    }

    // 显示用户设置的默认的drawable资源
    private void displayDrawable() {
        if (0 < srcDrawable) {
            imageView.setImageResource(srcDrawable);
        } else {
            // 没有设置资源时显示空白
            displayBlankImage();
        }
    }

    private void displayBlankImage() {
        if (defaultNullable) {
            // 设置了默认显示空白时直接显示null
            imageView.setImageDrawable(null);
        } else {
            // 未设置是显示系统默认的头像图片
            imageView.setImageResource(R.drawable.img_default_user_header);
            //imageView.setImageResource(R.mipmap.img_image_loading_fail);
        }
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        //begin boilerplate code that allows parent classes to save state
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);
        //end

        ss.failedTimes = loadingFailedTimes;

        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        //begin boilerplate code so parent classes can restore state
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        //end

        loadingFailedTimes = ss.failedTimes;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (scaleType > 0) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        if (!isNullUrl() && !isInEditMode()) {
            displayImage(displayUrl, imageWidth, imageHeight, showSelect, showDelete);
        } else {
            displayDrawable();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        // 显示空资源以便释放缓存
        imageView.setImageDrawable(null);
        super.onDetachedFromWindow();
        // 释放图片时gc一下
        System.gc();
    }

    /**
     * 清除image
     */
    public void clearImage() {
        imageView.setImageDrawable(null);
    }

    /**
     * 图片的url地址
     */
    private String displayUrl;
    private int imageSize, imageWidth, imageHeight;
    private boolean showDelete, showSelect, selected, showLoading, defaultNullable;

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
     * 设置是否在url或res为空时drawable设置为null以便释放内存
     */
    public void setDefaultNullable(boolean nullable) {
        defaultNullable = nullable;
    }

    /**
     * 是否已选中
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * 显示图片，支持以下uri方式：
     * <ul>
     * <li>http://</li>
     * <li>drawable://  ex.: "drawable://" + R.drawable.image</li>
     * <li>assets://image.png</li>
     * <li>file:///mnt/sdcard/image.png  ex.: "file://" + uri(string)</li>
     * <li>content://media/external/audio/albumart/13</li>
     * </ul>
     */
    public void displayImage(String url, int size, boolean selectable, boolean deletable) {
        imageSize = size;
        displayImage(url, size, size, selectable, deletable);
    }

    /**
     * 显示图片，支持以下uri方式：
     * <ul>
     * <li>http://</li>
     * <li>drawable://  ex.: "drawable://" + R.drawable.image</li>
     * <li>assets://image.png</li>
     * <li>file:///mnt/sdcard/image.png  ex.: "file://" + uri(string)</li>
     * <li>content://media/external/audio/albumart/13</li>
     * </ul>
     */
    public void displayImage(String url, int width, int height, boolean selectable, boolean deletable) {
        imageWidth = width;
        imageHeight = height;
        calculateSize();
        showDelete = deletable;
        showSelect = selectable;
        displayUrl = url;
        if (StringHelper.isEmpty(displayUrl) || displayUrl.contains("(null)")) {
            displayUrl = "drawable://" + (isShowHeader ? R.drawable.img_default_user_header : R.mipmap.img_image_loading_fail);
        }
        displayImage2();
    }

    public void setShowHeader(boolean shown) {
        isShowHeader = shown;
    }

    private void calculateSize() {
        if (imageWidth <= 0 || imageHeight <= 0) {
            this.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            imageWidth = getMeasuredWidth();
            imageHeight = getMeasuredHeight();
        }
    }

    /**
     * 设置图像的缩放方式
     */
    public void setImageScaleType(ImageView.ScaleType scaleType) {
        imageView.setScaleType(scaleType);
    }

    private boolean isNullUrl() {
        return TextUtils.isEmpty(displayUrl) || displayUrl.length() < 5;
    }

    @SuppressWarnings("ConstantConditions")
    private void displayImage2() {
        if (isNullUrl()) {
            // 图片地址为空时显示默认的drawable
            displayDrawable();
        } else {
            String url = displayUrl;
            if (!url.contains("://")) {
                // 默认显示本地图片
                url = "file://" + url;
            }
            ImageLoader.getInstance().displayImage(url, new ImageViewAware(imageView), null,
                    new ImageSize(imageWidth, imageHeight), mImageLoadingListener, mImageLoadingProgressListener);
        }
    }

    private ImageLoadingListener mImageLoadingListener = new ImageLoadingListener() {

        @Override
        public void onLoadingStarted(String imageUri, View view) {
            if (showLoading) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            progressBar.setVisibility(View.GONE);
            loadingFailedTimes++;
            if (loadingFailedTimes <= 5) {
                FailReason.FailType type = failReason.getType();
                LogHelper.log("ImageDisplayer", StringHelper.format("loading failed(%s) %d times for url: %s, 3 seconds later to reload.", type, loadingFailedTimes, imageUri));
                if (type == FailReason.FailType.IO_ERROR || type == FailReason.FailType.NETWORK_DENIED) {
                    // 加载失败后，间隔3s再次加载一次图片
                    postDelayed(runnable, 3000);
                }
            }
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            progressBar.setVisibility(View.GONE);
        }

        private Runnable runnable = new Runnable() {
            @Override
            public void run() {
                displayImage2();
            }
        };
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

    public void setDisplayType(@DisplayType int type) {
        displayType = type;
        initializeViews();
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == deleteContainer && null != deleteClickListener) {
                deleteClickListener.onDeleteClick(displayUrl);
            } else if (v == imageView && null != imageClickListener) {
                imageClickListener.onImageClick(ImageDisplayer.this, displayUrl);
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
        void onImageClick(ImageDisplayer displayer, String url);
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

    static class SavedState extends BaseSavedState {

        int failedTimes;

        public SavedState(Parcel source) {
            super(source);
            failedTimes = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(failedTimes);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
