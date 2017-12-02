package com.leadcom.android.isp.holder.individual;

import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.common.SimpleClickableViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.model.user.SimpleMoment;
import com.hlk.hlklib.lib.inject.ViewId;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>个人简单的说说图片列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/08 13:28 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/08 13:28 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class UserSimpleMomentViewHolder extends SimpleClickableViewHolder {

    @ViewId(R.id.ui_holder_view_simple_moment_image1)
    private ImageDisplayer image1;
    @ViewId(R.id.ui_holder_view_simple_moment_image2)
    private ImageDisplayer image2;
    @ViewId(R.id.ui_holder_view_simple_moment_image3)
    private ImageDisplayer image3;
    @ViewId(R.id.ui_holder_view_simple_moment_image4)
    private ImageDisplayer image4;
    @ViewId(R.id.ui_holder_view_simple_moment_image5)
    private ImageDisplayer image5;

    private int imageSize;

    public UserSimpleMomentViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        imageSize = getDimension(R.dimen.ui_static_dp_50);
        ImageDisplayer.OnImageClickListener listener = new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(ImageDisplayer displayer, String url) {
                if (null != mOnViewHolderClickListener) {
                    mOnViewHolderClickListener.onClick(getAdapterPosition());
                }
            }
        };
        image1.addOnImageClickListener(listener);
        image2.addOnImageClickListener(listener);
        image3.addOnImageClickListener(listener);
        image4.addOnImageClickListener(listener);
        image5.addOnImageClickListener(listener);
    }

    public void showContent(ArrayList<SimpleMoment> moments) {
        int size = moments.size();

        image1.setVisibility(size >= 1 ? View.VISIBLE : View.GONE);
        if (size >= 1) {
            displayImage(image1, moments.get(0).getUrl());
        }

        image2.setVisibility(size >= 2 ? View.VISIBLE : View.GONE);
        if (size >= 2) {
            displayImage(image2, moments.get(1).getUrl());
        }

        image3.setVisibility(size >= 3 ? View.VISIBLE : View.GONE);
        if (size >= 3) {
            displayImage(image3, moments.get(2).getUrl());
        }

        image4.setVisibility(size >= 4 ? View.VISIBLE : View.GONE);
        if (size >= 4) {
            displayImage(image4, moments.get(3).getUrl());
        }

        image5.setVisibility(size >= 5 ? View.VISIBLE : View.GONE);
        if (size >= 5) {
            displayImage(image5, moments.get(4).getUrl());
        }
    }

    private void displayImage(ImageDisplayer displayer, String url) {
        displayer.displayImage(url, imageSize, false, false);
    }
}
