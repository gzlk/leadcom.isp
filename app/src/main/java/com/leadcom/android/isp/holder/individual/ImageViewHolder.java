package com.leadcom.android.isp.holder.individual;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.hlk.hlklib.lib.inject.ViewUtility;

/**
 * <b>功能描述：</b>显示照片的ViewHolder<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/16 15:13 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/16 15:13 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ImageViewHolder extends BaseViewHolder {

    private ImageDisplayer imageDisplayer;

    public ImageViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        imageDisplayer = (ImageDisplayer) itemView;
        imageDisplayer.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
        imageDisplayer.addOnDeleteClickListener(new ImageDisplayer.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(String url) {
//                if (null != mOnHandlerBoundDataListener) {
//                    String string = (String) mOnHandlerBoundDataListener.onHandlerBoundData(ImageViewHolder.this);
//                    log(format("try to delete %s, in position is: %s", url, string));
//                }
                if (null != _outerDeleteClickListener) {
                    _outerDeleteClickListener.onDeleteClick(url);
                }
            }
        });
    }

    private boolean deleteable = false;
    private boolean selectable = false;

    /**
     * 是否显示删除按钮
     */
    public void showDelete(boolean show) {
        deleteable = show;
    }

    /**
     * 是否显示选择按钮
     */
    public void showSelect(boolean show) {
        selectable = show;
    }

    private int width, height;

    /**
     * 设置要显示的图片的尺寸
     */
    public void setImageSize(int size) {
        setImageSize(size, size);
    }

    /**
     * 设置要显示的图片的尺寸
     */
    public void setImageSize(int width, int height) {
        this.width = width;
        this.height = height;
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) itemView.getLayoutParams();
        params.width = width;
        params.height = height;
        itemView.setLayoutParams(params);
    }

//    @Click({R.id.ui_holder_view_image})
//    private void elementClick(View view) {
//        if (null != mOnHandlerBoundDataListener) {
//            mOnHandlerBoundDataListener.onHandlerBoundData(this);
//        }
//    }

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
    public void showContent(String uri) {
        if (!StringHelper.isEmpty(uri)) {
            imageDisplayer.displayImage(uri, width, height, selectable, deleteable);
        }
    }

    /**
     * 添加图片点击事件回调
     */
    public void addOnImageClickListener(ImageDisplayer.OnImageClickListener l) {
        imageDisplayer.addOnImageClickListener(l);
    }

    /**
     * 添加选择事件回调
     */
    public void addOnSelectorClickListener(ImageDisplayer.OnSelectorClickListener l) {
        imageDisplayer.addOnSelectorClickListener(l);
    }

    private ImageDisplayer.OnDeleteClickListener _outerDeleteClickListener;

    /**
     * 添加删除事件回调
     */
    public void addOnDeleteClickListener(ImageDisplayer.OnDeleteClickListener l) {
        _outerDeleteClickListener = l;
        //imageDisplayer.addOnDeleteClickListener(l);
    }
}
