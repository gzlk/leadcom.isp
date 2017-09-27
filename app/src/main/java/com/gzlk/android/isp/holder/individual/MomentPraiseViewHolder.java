package com.gzlk.android.isp.holder.individual;

import android.view.View;

import com.google.android.flexbox.FlexboxLayout;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.individual.UserPropertyFragment;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.archive.ArchiveLike;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;

import java.util.List;

/**
 * <b>功能描述：</b>说说中的赞<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/07/25 10:13 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/07/25 10:13 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MomentPraiseViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_individual_moment_praises)
    private FlexboxLayout headers;

    private int imageSize, margin;
    private boolean hasShown = false;

    public MomentPraiseViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        imageSize = getDimension(R.dimen.ui_base_user_header_image_size_small);
        margin = getDimension(R.dimen.ui_static_dp_5);
    }

    public void setHasShown(boolean hasShown) {
        this.hasShown = hasShown;
    }

    public boolean hasShown() {
        return hasShown;
    }

    public void showContent(List<ArchiveLike> likes) {
        hasShown = true;
        headers.removeAllViews();
        addHeaders(likes);
    }

    private void addHeaders(List<ArchiveLike> likes) {
        for (ArchiveLike like : likes) {
            ImageDisplayer displayer = new ImageDisplayer(headers.getContext());
            displayer.setShowHeader(true);
            displayer.displayImage(like.getHeadPhoto(), imageSize, false, false);
            headers.addView(displayer);
            FlexboxLayout.LayoutParams params = (FlexboxLayout.LayoutParams) displayer.getLayoutParams();
            params.width = imageSize;
            params.height = imageSize;
            params.rightMargin = margin;
            params.leftMargin = margin;
            params.bottomMargin = margin;
            params.topMargin = margin;
            displayer.setLayoutParams(params);
            displayer.addOnImageClickListener(onImageClickListener);
            displayer.setTag(R.id.hlklib_ids_custom_view_click_tag, like.getUserId());
        }
    }

    private ImageDisplayer.OnImageClickListener onImageClickListener = new ImageDisplayer.OnImageClickListener() {

        @Override
        public void onImageClick(ImageDisplayer displayer, String url) {
            String clicked = (String) displayer.getTag(R.id.hlklib_ids_custom_view_click_tag);
            if (!isEmpty(url)) {
                UserPropertyFragment.open(fragment(), clicked);
            } else {
                ToastHelper.make().showMsg(R.string.ui_text_user_information_blank_or_error_id);
            }
        }
    };
}
