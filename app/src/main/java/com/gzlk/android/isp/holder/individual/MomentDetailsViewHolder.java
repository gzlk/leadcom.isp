package com.gzlk.android.isp.holder.individual;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.common.ImageViewerFragment;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.lib.view.ExpandableTextView;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.user.Moment;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;

/**
 * <b>功能描述：</b>说说的详情<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/07/23 15:36 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/07/23 15:36 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MomentDetailsViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_moment_details_header)
    private ImageDisplayer authorHeader;
    @ViewId(R.id.ui_holder_view_moment_details_name)
    private TextView authorName;
    @ViewId(R.id.ui_holder_view_moment_details_content)
    private ExpandableTextView momentContent;
    @ViewId(R.id.ui_holder_view_moment_details_time)
    private TextView timeView;
    @ViewId(R.id.ui_holder_view_moment_details_images1)
    private LinearLayout images1;
    @ViewId(R.id.ui_holder_view_moment_details_images2)
    private LinearLayout images2;
    @ViewId(R.id.ui_holder_view_moment_details_images3)
    private LinearLayout images3;
    @ViewId(R.id.ui_holder_view_individual_moment_like_name_layout)
    private View likeLayout;
    @ViewId(R.id.ui_holder_view_individual_moment_like_names)
    private TextView likeNames;
    @ViewId(R.id.ui_holder_view_individual_moment_like_name_line)
    private View likeLine;
    @ViewId(R.id.ui_holder_view_moment_details_bottom_padding)
    private View bottomPaddingView;

    private ImageLineViewHolder imageLine1, imageLine2, imageLine3;
    private int imageSize;
    private boolean showLike = false;

    public MomentDetailsViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        imageSize = getDimension(R.dimen.ui_base_user_header_image_size_small);
        imageLine1 = new ImageLineViewHolder(images1, fragment);
        imageLine2 = new ImageLineViewHolder(images2, fragment);
        imageLine3 = new ImageLineViewHolder(images3, fragment);

        ImageDisplayer.OnImageClickListener onImageClickListener = new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(ImageDisplayer displayer, String url) {
                if (null != mOnHandlerBoundDataListener) {
                    Object object = mOnHandlerBoundDataListener.onHandlerBoundData(MomentDetailsViewHolder.this);
                    if (null != object && object instanceof Moment) {
                        Moment moment = (Moment) object;
                        int index = moment.getImage().indexOf(url);
                        ImageViewerFragment.open(fragment(), index, moment.getImage());
                    }
                }
            }
        };
        imageLine1.setOnImageClickListener(onImageClickListener);
        imageLine2.setOnImageClickListener(onImageClickListener);
        imageLine3.setOnImageClickListener(onImageClickListener);
    }

    /**
     * 设置是否在这里直接显示点赞列表
     */
    public void isShowLike(boolean shown) {
        showLike = shown;
    }

    public void showContent(Moment moment) {
        authorHeader.displayImage(moment.getHeadPhoto(), imageSize, false, false);
        authorName.setText(moment.getUserName());
        momentContent.setText(moment.getContent());
        momentContent.makeExpandable();
        momentContent.setVisibility(isEmpty(moment.getContent()) ? View.GONE : View.VISIBLE);
        timeView.setText(fragment().formatTimeAgo(moment.getCreateDate()));

        int size = null == moment.getImage() ? 0 : moment.getImage().size();
        if (size > 0) {
            if (size > 6) {
                imageLine1.clearImages();
                imageLine1.showContent(moment.getImage(), 0);
                imageLine1.showBottomMargin(true);
                imageLine2.clearImages();
                imageLine2.showContent(moment.getImage(), 3);
                imageLine2.showBottomMargin(true);
                imageLine3.clearImages();
                imageLine3.showContent(moment.getImage(), 6);
                imageLine3.showBottomMargin(false);
            } else if (size > 3) {
                imageLine1.clearImages();
                imageLine1.showContent(moment.getImage(), 0);
                imageLine1.showBottomMargin(true);
                imageLine2.clearImages();
                imageLine2.showContent(moment.getImage(), 3);
                imageLine2.showBottomMargin(false);
                imageLine3.clearImages();
            } else {
                imageLine1.clearImages();
                imageLine1.showContent(moment.getImage(), 0);
                imageLine1.showBottomMargin(false);
                imageLine2.clearImages();
                imageLine3.clearImages();
            }
        } else {
            imageLine1.clearImages();
            imageLine2.clearImages();
            imageLine3.clearImages();
        }
        showLikes(moment);
        bottomPaddingView.setVisibility(moment.getUserMmtCmtList().size() > 0 ? View.GONE : View.VISIBLE);
    }

    private void showLikes(Moment moment) {
        if (!showLike) return;
        likeLayout.setVisibility(moment.getUserMmtLikeList().size() > 0 ? View.VISIBLE : View.GONE);
        likeNames.setText(moment.getLikeNames());
        likeLine.setVisibility(moment.getUserMmtCmtList().size() > 0 ? View.VISIBLE : View.GONE);
    }

    @Click({R.id.ui_holder_view_moment_details_container,
            R.id.ui_holder_view_moment_details_more})
    private void elementClick(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        }
    }
}
