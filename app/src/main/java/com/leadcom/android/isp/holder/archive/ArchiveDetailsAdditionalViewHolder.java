package com.leadcom.android.isp.holder.archive;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.model.archive.Additional;
import com.leadcom.android.isp.model.archive.Archive;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.model.user.Collection;

/**
 * <b>功能描述：</b>档案详情页中的档案附加信息<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/11/06 08:40 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/11/06 08:40 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveDetailsAdditionalViewHolder extends BaseViewHolder {

    // additional
    @ViewId(R.id.ui_holder_view_archive_details_additional_layout)
    private LinearLayout additionalView;
    @ViewId(R.id.ui_tool_view_archive_additional_comment_number)
    private TextView commentView;
    @ViewId(R.id.ui_tool_view_archive_additional_like_icon)
    private CustomTextView likeIcon;
    @ViewId(R.id.ui_tool_view_archive_additional_like_number)
    private TextView likeNumber;
    @ViewId(R.id.ui_tool_view_archive_additional_collection_icon)
    private CustomTextView collectIcon;
    @ViewId(R.id.ui_tool_view_archive_additional_collection_number)
    private TextView collectNumber;

    public ArchiveDetailsAdditionalViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(Additional additional) {
        additionalView.setVisibility((additional.getLikeNum() <= 0 && additional.getCmtNum() <= 0 && additional.getColNum() <= 0) ? View.GONE : View.VISIBLE);
        commentView.setText(StringHelper.getString(R.string.ui_text_archive_details_comment_title, additional.getCmtNum() > 0 ? (String.valueOf(additional.getCmtNum())) : ""));
        boolean liked = additional.getLike() == Archive.LikeType.LIKED;
        likeIcon.setText(liked ? R.string.ui_icon_like_solid : R.string.ui_icon_like_hollow);
        likeIcon.setTextColor(getColor(liked ? R.color.colorCaution : R.color.textColorHint));
        likeNumber.setText(String.valueOf(additional.getLikeNum()));
        liked = additional.getCollection() == Collection.CollectionType.COLLECTED;
        collectIcon.setTextColor(getColor(liked ? R.color.colorCaution : R.color.textColorHint));
        collectIcon.setText(liked ? R.string.ui_icon_pentagon_corner_solid : R.string.ui_icon_pentagon_corner_hollow);
        collectNumber.setText(String.valueOf(additional.getColNum()));
    }

    @Click({R.id.ui_tool_view_archive_additional_like_layout, R.id.ui_tool_view_archive_additional_collection_layout})
    private void onElementClick(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        }
    }
}
