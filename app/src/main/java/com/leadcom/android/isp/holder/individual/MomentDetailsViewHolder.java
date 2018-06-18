package com.leadcom.android.isp.holder.individual;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hlk.hlklib.lib.emoji.EmojiUtility;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.common.ImageViewerFragment;
import com.leadcom.android.isp.fragment.individual.moment.MomentImagesFragment;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.lib.view.ExpandableTextView;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.model.user.Moment;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;

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
    @ViewId(R.id.ui_holder_view_individual_moment_like_icon)
    private CustomTextView likeIcon;
    @ViewId(R.id.ui_holder_view_individual_moment_like_names)
    private TextView likeNames;
    @ViewId(R.id.ui_holder_view_individual_moment_like_name_line)
    private View likeLine;
    @ViewId(R.id.ui_holder_view_moment_details_bottom_padding)
    private View bottomPaddingView;

    private ImageLineViewHolder imageLine1, imageLine2, imageLine3;
    private int imageSize;
    private boolean showLike = false;
    private boolean isToDetails = false;
    private boolean isCollected = false;

    public MomentDetailsViewHolder(View itemView, final BaseFragment fragment) {
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
                        if (isToDetails) {
                            // 到说说图片详情页
                            MomentImagesFragment.isCollected = isCollected;
                            MomentImagesFragment.open(fragment(), moment.getId(), index);
                        } else {
                            ImageViewerFragment.isCollected = isCollected;
                            ImageViewerFragment.open(fragment(), index, moment.getImage());
                        }
                    }
                }
            }
        };
        imageLine1.setOnImageClickListener(onImageClickListener);
        imageLine2.setOnImageClickListener(onImageClickListener);
        imageLine3.setOnImageClickListener(onImageClickListener);
        authorHeader.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(ImageDisplayer displayer, String url) {
                if (null != mOnViewHolderElementClickListener) {
                    mOnViewHolderElementClickListener.onClick(authorHeader, getAdapterPosition());
                }
            }
        });
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }

    /**
     * 设置点击图片时是否打开说说的图片详情页还是打开图片浏览页
     */
    public void setToDetails(boolean to) {
        isToDetails = to;
    }

    /**
     * 设置是否在这里直接显示点赞列表
     */
    public void isShowLike(boolean shown) {
        showLike = shown;
    }

    public void showContent(Moment moment) {
        String header = moment.getHeadPhoto();
        if (isEmpty(header) || header.length() < 20) {
            header = "drawable://" + R.drawable.img_default_user_header;
        }
        authorHeader.displayImage(header, imageSize, false, false);
        authorName.setText(moment.getUserName());
        if (!isEmpty(moment.getContent())) {
            momentContent.setText(EmojiUtility.getEmojiString(momentContent.getContext(), moment.getContent(), true));
            momentContent.makeExpandable();
        }
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
    }

    private void showLikes(Moment moment) {
        likeLayout.setVisibility(showLike ? View.VISIBLE : View.GONE);
        if (!showLike) return;
        int comments = moment.getUserMmtCmtList().size();
        int likes = moment.getUserMmtLikeList().size();
        likeLayout.setVisibility(comments <= 0 && likes <= 0 ? View.GONE : View.VISIBLE);
        likeIcon.setVisibility(likes > 0 ? View.VISIBLE : View.GONE);
        likeNames.setVisibility(likes > 0 ? View.VISIBLE : View.GONE);
        bottomPaddingView.setVisibility(comments > 0 ? View.GONE : View.VISIBLE);
        likeNames.setText(moment.getLikeNames());
        likeLine.setVisibility(comments > 0 ? (likes > 0 ? View.VISIBLE : View.GONE) : View.GONE);
    }

    @Click({R.id.ui_holder_view_moment_details_container,
            R.id.ui_holder_view_moment_details_more})
    private void elementClick(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        }
    }
}
