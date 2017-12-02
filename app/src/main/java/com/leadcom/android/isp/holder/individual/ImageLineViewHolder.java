package com.leadcom.android.isp.holder.individual;

import android.view.View;
import android.widget.LinearLayout;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;

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

    @ViewId(R.id.ui_holder_view_individual_moment_image_1)
    private ImageDisplayer displayer1;
    @ViewId(R.id.ui_holder_view_individual_moment_image_2)
    private ImageDisplayer displayer2;
    @ViewId(R.id.ui_holder_view_individual_moment_image_3)
    private ImageDisplayer displayer3;

    private int imageSize, bottomMargin;

    public ImageLineViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        imageSize = getDimension(R.dimen.ui_base_image_line_image_size);
        bottomMargin = getDimension(R.dimen.ui_static_dp_5);
    }

    public void clearImages() {
        displayer1.setVisibility(View.GONE);
        displayer2.setVisibility(View.GONE);
        displayer3.setVisibility(View.GONE);
    }

    public void showBottomMargin(boolean shown) {
        showBottomMargin(displayer1, shown);
        showBottomMargin(displayer2, shown);
        showBottomMargin(displayer3, shown);
    }

    private void showBottomMargin(ImageDisplayer displayer, boolean shown) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) displayer.getLayoutParams();
        params.bottomMargin = shown ? bottomMargin : 0;
        displayer.setLayoutParams(params);
    }

    public void showContent(ArrayList<String> images, int start) {
        for (int i = start, size = images.size(); i < size; i++) {
            switch (i - start) {
                case 0:
                    showContent(displayer1, images.get(i));
                    break;
                case 1:
                    showContent(displayer2, images.get(i));
                    break;
                case 2:
                    showContent(displayer3, images.get(i));
                    break;
            }
        }
    }

    public void setOnImageClickListener(ImageDisplayer.OnImageClickListener listener) {
        displayer1.addOnImageClickListener(listener);
        displayer2.addOnImageClickListener(listener);
        displayer3.addOnImageClickListener(listener);
    }

    private void showContent(ImageDisplayer displayer, String image) {
        displayer.setVisibility(View.VISIBLE);
        displayer.displayImage(image, imageSize, false, false);
    }
}
