package com.gzlk.android.isp.holder.archive;

import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.archive.Archive;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/27 14:42 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/27 14:42 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveDetailsViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_archive_details_title)
    private TextView titleView;
    @ViewId(R.id.ui_holder_view_archive_details_time)
    private TextView timeView;
    @ViewId(R.id.ui_holder_view_archive_details_author)
    private TextView authorView;
    @ViewId(R.id.ui_holder_view_archive_details_cover)
    private ImageDisplayer coverView;
    @ViewId(R.id.ui_holder_view_archive_details_content)
    private WebView contentView;
    @ViewId(R.id.ui_holder_view_archive_details_bottom_line)
    private View bottomLine;
    // additional
    @ViewId(R.id.ui_tool_view_archive_additional_like_icon)
    private CustomTextView likeIcon;
    @ViewId(R.id.ui_tool_view_archive_additional_like_number)
    private TextView likeNumber;
    @ViewId(R.id.ui_tool_view_archive_additional_collection_icon)
    private CustomTextView collectIcon;
    @ViewId(R.id.ui_tool_view_archive_additional_collection_number)
    private TextView collectNumber;

    private int width, height;

    public ArchiveDetailsViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        contentView.getSettings().setDefaultTextEncodingName("UTF-8");
        resetCoverSize();
    }

    public void onFragmentDestroy() {
        contentView.destroy();
    }

    private void resetCoverSize() {
        width = fragment().getScreenWidth() - getDimension(R.dimen.ui_base_dimen_margin_padding) * 2;
        height = width / 2;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) coverView.getLayoutParams();
        params.height = height;
        coverView.setLayoutParams(params);
    }

    public void showContent(Archive archive) {
        titleView.setText(archive.getTitle());
        timeView.setText(fragment().formatDate(archive.getCreateDate(), R.string.ui_base_text_date_time_format));
        authorView.setText(StringHelper.getString(R.string.ui_text_archive_details_author_text, archive.getUserName()));
        coverView.displayImage(archive.getCover(), width, height, false, false);
        boolean liked = archive.getLike() == Archive.LikeType.LIKED;
        likeIcon.setText(liked ? R.string.ui_icon_like_solid : R.string.ui_icon_like_hollow);
        likeIcon.setTextColor(getColor(liked ? R.color.colorCaution : R.color.textColorHint));
        likeNumber.setText(String.valueOf(archive.getLikeNum()));
        liked = archive.getCollection() == Archive.CollectionType.COLLECTED;
        collectIcon.setTextColor(getColor(liked ? R.color.colorCaution : R.color.textColorHint));
        collectIcon.setText(liked ? R.string.ui_icon_pentagon_corner_solid : R.string.ui_icon_pentagon_corner_hollow);
        collectNumber.setText(String.valueOf(archive.getColNum()));
        String content = archive.getContent();
        contentView.setVisibility((isEmpty(content) || content.equals("null")) ? View.GONE : View.VISIBLE);
        contentView.loadData(StringHelper.getString(R.string.ui_text_archive_details_content_html, content), "text/html; charset=UTF-8", null);
        bottomLine.setVisibility(archive.getCmtNum() > 0 ? View.VISIBLE : View.GONE);
    }

    @Click({R.id.ui_tool_view_archive_additional_like_layout, R.id.ui_tool_view_archive_additional_collection_layout})
    private void onElementClick(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        }
    }
}
