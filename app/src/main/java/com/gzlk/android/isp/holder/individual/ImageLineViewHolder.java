package com.gzlk.android.isp.holder.individual;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.lib.view.ImageDisplayer;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>一行显示3张图片<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/07/23 21:12 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/07/23 21:12 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ImageLineViewHolder extends BaseViewHolder {

    private ViewGroup root;
    private int layout, imageSize;

    public ImageLineViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        root = (ViewGroup) itemView;
        layout = R.layout.holder_view_individual_moment_image;
        imageSize = getDimension(R.dimen.ui_base_image_line_image_size);
    }

    public void clearImages() {
        root.removeAllViews();
    }

    public void showContent(ArrayList<String> images, int start) {
        for (int i = start, size = images.size(); i < size; i++) {
            showContent(images.get(i));
        }
    }

    private void showContent(String image) {
        ImageDisplayer displayer = (ImageDisplayer) LayoutInflater.from(root.getContext()).inflate(layout, null);
        displayer.displayImage(image, imageSize, false, false);
        root.addView(displayer);
    }
}
