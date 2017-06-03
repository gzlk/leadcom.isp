package com.gzlk.android.isp.holder.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.archive.ArchiveAdditionalViewHolder;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.archive.Archive;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;

/**
 * <b>功能描述：</b>首页会议中的view<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/02 15:33 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/02 15:33 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveHomeViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_home_seminar_item_image)
    private ImageDisplayer imageDisplayer;
    @ViewId(R.id.ui_holder_view_home_seminar_item_title)
    private TextView textView;

    private ArchiveAdditionalViewHolder additionalViewHolder;
    private int width, height;

    public ArchiveHomeViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        imageDisplayer.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
        additionalViewHolder = new ArchiveAdditionalViewHolder(itemView, fragment);
        width = getDimension(R.dimen.ui_static_dp_110);
        height = getDimension(R.dimen.ui_static_dp_80);
    }

    public void showContent(Archive archive) {
        textView.setText(archive.getTitle());
        additionalViewHolder.showContent(archive);
        // 优先级：封面
        String image = archive.getCover();
        if (!isEmpty(image)) {
            imageDisplayer.displayImage(image, width, height, false, false);
            return;
        }
        // 视频
        if (null != archive.getVideo() && archive.getVideo().size() > 0) {
            imageDisplayer.displayImage("drawable://" + R.drawable.img_image_video, width, height, false, false);
            return;
        }
        // 文档
        // 图片
        if (null != archive.getImage() && archive.getImage().size() > 0) {
            image = archive.getImage().get(0).getUrl();
            imageDisplayer.displayImage(image, width, height, false, false);
            return;
        }
        if (isEmpty(image)) {
            if (null != archive.getImage() && archive.getImage().size() > 0) {
                image = archive.getImage().get(0).getUrl();
            }
        }
        imageDisplayer.displayImage(image, width, height, false, false);
    }

    @Click({R.id.ui_holder_view_home_seminar_item_container})
    private void click(View view) {

    }
}
