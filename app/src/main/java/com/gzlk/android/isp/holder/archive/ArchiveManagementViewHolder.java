package com.gzlk.android.isp.holder.archive;

import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.attachment.AttachmentViewHolder;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.common.Attachment;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CorneredView;
import com.hlk.hlklib.lib.view.CustomTextView;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>待审核档案列表item<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/25 01:22 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/25 01:22 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveManagementViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_archive_management_selector)
    private CustomTextView selector;
    @ViewId(R.id.ui_holder_view_archive_management_icon_container)
    private CorneredView iconContainer;
    @ViewId(R.id.ui_holder_view_archive_management_icon)
    private CustomTextView iconView;
    @ViewId(R.id.ui_holder_view_archive_management_image)
    private ImageDisplayer imageView;
    @ViewId(R.id.ui_holder_view_archive_management_title)
    private TextView titleView;
    @ViewId(R.id.ui_holder_view_archive_management_files)
    private TextView filesView;
    @ViewId(R.id.ui_holder_view_archive_management_source)
    private TextView sourceView;

    private int imageSize;

    public ArchiveManagementViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        imageSize = getDimension(R.dimen.ui_static_dp_60);
        imageView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    public void showContent(Archive archive, String searchingText) {
        selector.setVisibility(archive.isSelectable() ? View.VISIBLE : View.GONE);
        selector.setTextColor(getColor(archive.isSelected() ? R.color.colorPrimary : R.color.textColorHintLightLight));
        String text = archive.getTitle();
        if (StringHelper.isEmpty(text)) {
            text = StringHelper.getString(R.string.ui_archive_approve_no_title);
        }
        text = getSearchingText(text, searchingText);
        titleView.setText(Html.fromHtml(text));
        filesView.setText(StringHelper.getString(R.string.ui_archive_approving_archive_attachments, filesCount(archive)));
        filesView.setVisibility(filesCount(archive) > 0 ? View.VISIBLE : View.GONE);
        sourceView.setText("来源：无");
        // 显示优先级
        // cover
        String path = archive.getCover();
        if (!isEmpty(path)) {
            iconContainer.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            imageView.displayImage(path, imageSize, false, false);
            return;
        }
        // 视频
        Attachment attachment = firstOf(archive.getVideo());
        if (null != attachment && !isEmpty(attachment.getUrl())) {
            // 显示视频图片或默认视频图片
            imageView.setVisibility(View.GONE);
            iconContainer.setVisibility(View.VISIBLE);
            iconContainer.setNormalColor(getColor(attachment.iconColor()));
            iconView.setText(AttachmentViewHolder.getFileExtension(attachment.getExt()));
            return;
        }
        // office 文档
        attachment = firstOf(archive.getOffice());
        if (null != attachment && !isEmpty(attachment.getUrl())) {
            imageView.setVisibility(View.GONE);
            iconContainer.setVisibility(View.VISIBLE);
            iconContainer.setNormalColor(getColor(attachment.iconColor()));
            iconView.setText(AttachmentViewHolder.getFileExtension(attachment.getExt()));
            return;
        }
        // 图片
        attachment = firstOf(archive.getImage());
        if (null != attachment && !isEmpty(attachment.getUrl())) {
            imageView.setVisibility(View.VISIBLE);
            iconContainer.setVisibility(View.GONE);
            imageView.displayImage(attachment.getUrl(), imageSize, false, false);
            return;
        }
        // 其他附件
        attachment = firstOf(archive.getAttach());
        if (null != attachment && !isEmpty(attachment.getUrl())) {
            imageView.setVisibility(View.GONE);
            iconContainer.setVisibility(View.VISIBLE);
            iconContainer.setNormalColor(getColor(attachment.iconColor()));
            iconView.setText(AttachmentViewHolder.getFileExtension(attachment.getExt()));
            return;
        }
        // 什么附件都没有则不显示图标
        imageView.setVisibility(View.GONE);
        iconContainer.setVisibility(View.GONE);
        iconContainer.setNormalColor(getColor(null == attachment ? R.color.colorPrimary : attachment.iconColor()));
    }

    private Attachment firstOf(ArrayList<Attachment> list) {
        return (null != list && list.size() > 0) ? list.get(0) : null;
    }

    private int filesCount(Archive archive) {
        int cnt = 0;
        if (null != archive.getOffice()) {
            cnt += archive.getOffice().size();
        }
        if (null != archive.getImage()) {
            cnt += archive.getImage().size();
        }
        if (null != archive.getVideo()) {
            cnt += archive.getVideo().size();
        }
        if (null != archive.getAttach()) {
            cnt += archive.getAttach().size();
        }
        return cnt;
    }

    @Click({R.id.ui_holder_view_archive_management_root})
    private void elementClick(View view) {
        if (null != mOnViewHolderClickListener) {
            mOnViewHolderClickListener.onClick(getAdapterPosition());
        }
    }
}
