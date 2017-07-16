package com.gzlk.android.isp.holder.home;

import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.activity.Activity;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;

/**
 * <b>功能描述：</b>首页推荐活动的item<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/03 23:53 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/03 23:53 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ActivityHomeViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_tool_view_document_additional_container)
    private View container;
    @ViewId(R.id.ui_holder_view_home_seminar_item_image)
    private ImageDisplayer imageDisplayer;
    @ViewId(R.id.ui_holder_view_home_seminar_item_title)
    private TextView textView;

    private int imageWidth, imageHeight;
    //private ArchiveAdditionalViewHolder additionalViewHolder;

    public ActivityHomeViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        imageWidth = getDimension(R.dimen.ui_static_dp_60);
        imageHeight = getDimension(R.dimen.ui_static_dp_60);
        container.setVisibility(View.INVISIBLE);
        //additionalViewHolder = new ArchiveAdditionalViewHolder(itemView, fragment);
    }

    public void showContent(Activity activity) {
        String image = activity.getCover();
        if (isEmpty(image)) {
            image = "drawable://" + R.mipmap.img_image_loading_fail;
        }
        imageDisplayer.displayImage(image, imageWidth, imageHeight, false, false);
        textView.setText(activity.getTitle());
    }

    @Click({R.id.ui_holder_view_home_seminar_item_container})
    private void click(View view) {
        if (null != mOnViewHolderClickListener) {
            mOnViewHolderClickListener.onClick(getAdapterPosition());
        }
    }
}
