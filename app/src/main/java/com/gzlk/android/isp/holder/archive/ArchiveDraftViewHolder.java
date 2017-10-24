package com.gzlk.android.isp.holder.archive;

import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.archive.ArchiveDraft;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/23 19:48 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/23 19:48 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ArchiveDraftViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_tool_view_archive_draft_cover)
    private ImageDisplayer coverView;
    @ViewId(R.id.ui_tool_view_archive_draft_title)
    private TextView titleView;
    @ViewId(R.id.ui_tool_view_archive_draft_time)
    private TextView timeView;
    @ViewId(R.id.ui_tool_view_archive_draft_group)
    private TextView groupView;
    @ViewId(R.id.ui_tool_view_archive_draft_selector)
    private CustomTextView selectorView;

    public ArchiveDraftViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(ArchiveDraft draft) {
        Archive archive = Archive.fromJson(draft.getArchiveJson());
        coverView.displayImage(archive.getCover(), getDimension(R.dimen.ui_base_user_header_image_size), false, false);
        titleView.setText(archive.getTitle());
        timeView.setText(StringHelper.getString(R.string.ui_text_archive_creator_editor_create_draft_time, fragment().formatTimeAgo(draft.getCreateDate())));
        selectorView.setTextColor(getColor(draft.isSelected() ? R.color.colorPrimary : R.color.textColorHintLight));
        if (!isEmpty(draft.getGroupId())) {
            groupView.setText(draft.getGroupName());
        } else {
            groupView.setText("个人档案");
        }
    }

    @Click({R.id.ui_tool_view_archive_draft_layout, R.id.ui_tool_view_archive_draft_delete})
    private void elementClick(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        }
    }
}
