package com.leadcom.android.isp.holder.home;

import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.model.archive.Additional;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.archive.RecommendArchive;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.model.user.Collection;


/**
 * <b>功能描述：</b>首页档案推荐item<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/18 09:57 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/18 09:57 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveHomeRecommendedViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_archive_home_recommend_group)
    private TextView groupView;
    @ViewId(R.id.ui_tool_view_document_user_header_layout)
    private View headerView;
    @ViewId(R.id.ui_tool_view_document_user_header_image)
    private ImageDisplayer authorHeader;
    @ViewId(R.id.ui_tool_view_document_user_header_name)
    private TextView authorName;
    @ViewId(R.id.ui_tool_view_document_user_header_time)
    private TextView timeView;
    @ViewId(R.id.ui_holder_view_archive_home_recommend_cover)
    private ImageDisplayer coverView;
    @ViewId(R.id.ui_holder_view_archive_home_recommend_title)
    private TextView titleView;
    @ViewId(R.id.ui_holder_view_archive_home_recommend_content)
    private TextView contentView;
    @ViewId(R.id.ui_holder_view_archive_home_recommend_award)
    private View awardView;
    // 附加信息
    @ViewId(R.id.ui_holder_view_archive_home_recommend_additional)
    private TextView additionalText;
    @ViewId(R.id.ui_tool_view_archive_additional_comment_number)
    private TextView commentsView;
    @ViewId(R.id.ui_tool_view_archive_additional_like_number)
    private TextView likesView;
    @ViewId(R.id.ui_tool_view_archive_additional_collection_number)
    private TextView collectsView;
    @ViewId(R.id.ui_tool_view_archive_additional_like_icon)
    private CustomTextView likeIcon;
    @ViewId(R.id.ui_tool_view_archive_additional_collection_icon)
    private CustomTextView collectIcon;

    private int width, height, headerSize;
    private boolean showHeader;
    private boolean showGroup;
    private String searchingText = "";

    public ArchiveHomeRecommendedViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        headerView.setVisibility(View.GONE);
        headerSize = getDimension(R.dimen.ui_base_user_header_image_size_small);
        width = fragment.getScreenWidth() - getDimension(R.dimen.ui_base_dimen_margin_padding) * 2;
        height = width / 2;
        //resetImageHeight();
        coverView.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(ImageDisplayer displayer, String url) {
                // 图片点击等同于整个view点击
                if (null != mOnViewHolderClickListener) {
                    mOnViewHolderClickListener.onClick(getAdapterPosition());
                }
            }
        });
        authorHeader.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(ImageDisplayer displayer, String url) {
                if (null != mOnViewHolderElementClickListener) {
                    mOnViewHolderElementClickListener.onClick(authorHeader, getAdapterPosition());
                }
            }
        });
    }

    public void setShowGroup(boolean shown) {
        groupView.setVisibility(shown ? View.VISIBLE : View.GONE);
    }

    private void resetImageHeight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) coverView.getLayoutParams();
        params.height = height;
        coverView.setLayoutParams(params);
    }

    public void setHeaderShaoable(boolean shown) {
        showHeader = shown;
        groupView.setVisibility(shown ? View.GONE : View.VISIBLE);
        headerView.setVisibility(shown ? View.VISIBLE : View.GONE);
    }

    public void showContent(RecommendArchive archive) {
        groupView.setText(StringHelper.getString(R.string.ui_text_home_archive_recommend_group_name, Html.fromHtml(archive.getGroupName())));
        Archive doc = null == archive.getUserDoc() ? archive.getGroDoc() : archive.getUserDoc();
        if (null != doc) {
            showContent(doc);
        }
    }

    public void setSearchingText(String text) {
        searchingText = text;
    }

    public void showContent(Archive archive) {
        //headerView.setVisibility(showHeader ? View.VISIBLE : View.GONE);
        if (showHeader) {
            String header = archive.getHeadPhoto();
            if (isEmpty(header) || header.length() < 20) {
                header = "drawable://" + R.drawable.img_default_user_header;
            }
            authorHeader.displayImage(header, headerSize, false, false);
            authorName.setText(archive.getUserName());
            timeView.setText(fragment().formatTimeAgo(archive.getCreateDate()));
        }
        String cover = archive.getCover();
        if (isEmpty(cover) && archive.getImage().size() > 0) {
            cover = archive.getImage().get(0).getUrl();
        }
        coverView.displayImage(cover, getDimension(R.dimen.ui_static_dp_80), false, false);
        coverView.setVisibility(isEmpty(cover) || cover.length() < 10 ? View.GONE : View.VISIBLE);
        String text = archive.getTitle();
        if (!isEmpty(text)) {
            text = getSearchingText(text, searchingText);
        } else {
            text = StringHelper.getString(R.string.ui_text_archive_details_title_empty);
        }
        titleView.setText(Html.fromHtml(text));
        text = archive.getGroupName();
        if (isEmpty(text)) {
            text = "";
        }
        groupView.setText(StringHelper.getString(R.string.ui_text_home_archive_recommend_group_name, Html.fromHtml(text)));
        // 是否获奖
        awardView.setVisibility(archive.getAwardable() > 0 ? View.VISIBLE : View.GONE);
        // 去掉所有html标签
        text = isEmpty(archive.getContent()) ? archive.getAbstrContent() : archive.getContent();
        contentView.setVisibility(isEmpty(text) ? View.GONE : View.VISIBLE);
        contentView.setText(isEmpty(text) ? "" : Html.fromHtml(Utils.clearHtml(text)));
        // 显示新版赞、收藏等内容
        Additional add = archive.getAddition();
        text = StringHelper.getString(R.string.ui_text_home_archive_additional, add.getCmtNum(), add.getLikeNum(), add.getColNum());
        additionalText.setText(text);
        commentsView.setText(String.valueOf(archive.getCmtNum()));

        likesView.setText(String.valueOf(archive.getLikeNum()));
        boolean liked = archive.getLike() == Archive.LikeType.LIKED;
        likeIcon.setText(liked ? R.string.ui_icon_like_solid : R.string.ui_icon_like_hollow);
        likeIcon.setTextColor(getColor(liked ? R.color.colorCaution : R.color.textColorHint));

        liked = archive.getCollection() == Collection.CollectionType.COLLECTED;
        collectsView.setText(String.valueOf(archive.getColNum()));
        collectIcon.setText(liked ? R.string.ui_icon_pentagon_corner_solid : R.string.ui_icon_pentagon_corner_hollow);
        collectIcon.setTextColor(getColor(liked ? R.color.colorCaution : R.color.textColorHint));
    }

    @Click({R.id.ui_holder_view_archive_home_recommend_layout,
            R.id.ui_tool_view_archive_additional_comment_layout,
            R.id.ui_tool_view_archive_additional_like_layout,
            R.id.ui_tool_view_archive_additional_collection_layout
    })
    private void elementClick(View view) {
        switch (view.getId()) {
            case R.id.ui_holder_view_archive_home_recommend_layout:
                if (null != mOnViewHolderClickListener) {
                    mOnViewHolderClickListener.onClick(getAdapterPosition());
                }
                break;
            default:
                if (null != mOnViewHolderElementClickListener) {
                    mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
                }
                break;
        }
    }
}
