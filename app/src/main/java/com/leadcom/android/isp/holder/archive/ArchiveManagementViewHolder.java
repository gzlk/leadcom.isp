package com.leadcom.android.isp.holder.archive;

import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.attachment.AttachmentViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.activity.ActArchive;
import com.leadcom.android.isp.model.activity.Activity;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.common.PriorityPlace;
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
    @ViewId(R.id.ui_holder_view_archive_management_date)
    private TextView dateView;
    @ViewId(R.id.ui_holder_view_archive_management_status)
    private TextView statusView;

    private int imageSize;

    public ArchiveManagementViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        imageSize = getDimension(R.dimen.ui_static_dp_60);
        imageView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    public void showStatus(boolean shown) {
        statusView.setVisibility(shown ? View.VISIBLE : View.GONE);
    }

    public void showContent(ActArchive archive, String searchingText) {
        selector.setVisibility(archive.isSelectable() ? View.VISIBLE : View.GONE);
        selector.setTextColor(getColor(archive.isSelected() ? R.color.colorPrimary : R.color.textColorHintLightLight));
        String text = archive.getName();
        if (isEmpty(text)) {
            text = StringHelper.getString(R.string.ui_activity_archiving_no_attachment_name);
        }
        if (StringHelper.isEmpty(text)) {
            text = StringHelper.getString(R.string.ui_archive_approve_no_title);
        }
        text = getSearchingText(text, searchingText);
        titleView.setText(text);
        filesView.setText(fragment().formatDateTime(archive.getCreateDate()));

        Activity act = new Dao<>(Activity.class).query(archive.getActId());
        if (null == act || isEmpty(act.getTitle())) {
            text = "";
        } else {
            text = act.getTitle();
        }

        dateView.setText(StringHelper.getString(R.string.ui_activity_archiving_source, text));
        statusView.setText(StringHelper.getString(R.string.ui_archive_management_list_item_status, Attachment.getAttachmentStatus(archive.getStatus())));
        Attachment attachment = new Attachment();
        attachment.setUrl(archive.getUrl());
        attachment.setName(archive.getName());
        attachment.resetInformation();
        switch (archive.getType()) {
            case Attachment.AttachmentType.IMAGE:
                showImage(archive.getUrl());
                break;
            case Attachment.AttachmentType.OFFICE:
                showIcon(attachment);
                break;
            case Attachment.AttachmentType.OTHER:
                showIcon(attachment);
                break;
            case Attachment.AttachmentType.VIDEO:
                showIcon(attachment);
                break;
            default:
                imageView.setVisibility(View.GONE);
                iconContainer.setVisibility(View.GONE);
                iconContainer.setNormalColor(getColor(attachment.iconColor()));
                break;
        }
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
        dateView.setText(StringHelper.getString(R.string.ui_archive_management_list_item_create_date, fragment().formatDateTime(archive.getCreateDate())));
        statusView.setText(StringHelper.getString(R.string.ui_archive_management_list_item_status, archive.getArchiveStatus()));

        // 显示优先级
        // cover
        String path = archive.getCover();
        if (!isEmpty(path)) {
            showImage(path);
            return;
        }
        // 视频
        Attachment attachment = firstOf(archive.getVideo());
        if (null != attachment && !isEmpty(attachment.getUrl())) {
            // 显示视频图片或默认视频图片
            showIcon(attachment);
            return;
        }
        // office 文档
        attachment = firstOf(archive.getOffice());
        if (null != attachment && !isEmpty(attachment.getUrl())) {
            showIcon(attachment);
            return;
        }
        // 图片
        attachment = firstOf(archive.getImage());
        if (null != attachment && !isEmpty(attachment.getUrl())) {
            showImage(attachment.getUrl());
            return;
        }
        // 其他附件
        attachment = firstOf(archive.getAttach());
        if (null != attachment && !isEmpty(attachment.getUrl())) {
            showIcon(attachment);
            return;
        }
        // 什么附件都没有则不显示图标
        imageView.setVisibility(View.GONE);
        iconContainer.setVisibility(View.GONE);
        iconContainer.setNormalColor(getColor(null == attachment ? R.color.colorPrimary : attachment.iconColor()));
    }

    private void showImage(String url) {
        iconContainer.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
        imageView.displayImage(url, imageSize, false, false);
    }

    private void showIcon(Attachment attachment) {
        imageView.setVisibility(View.GONE);
        iconContainer.setVisibility(View.VISIBLE);
        iconContainer.setNormalColor(getColor(attachment.iconColor()));
        iconView.setText(AttachmentViewHolder.getFileExtension(attachment.getExt()));
    }

    private Attachment firstOf(ArrayList<Attachment> list) {
        return (null != list && list.size() > 0) ? list.get(0) : null;
    }

    private int filesCount(Archive archive) {
        return archive.getOffice().size() + archive.getImage().size() +
                archive.getVideo().size() + archive.getAttach().size();
    }

    public void showContent(PriorityPlace place) {
        selector.setVisibility(View.GONE);
        showImage(place.getImageUrl());
        titleView.setText(place.getTitle());
        dateView.setText(StringHelper.getString(R.string.ui_archive_management_list_item_create_date, fragment().formatDateTime(place.getCreateTime())));
        filesView.setText(format("推荐者：%s", place.getCreaterName()));
        statusView.setVisibility(View.INVISIBLE);
    }

    @Click({R.id.ui_holder_view_archive_management_root})
    private void elementClick(View view) {
        if (null != mOnViewHolderClickListener) {
            mOnViewHolderClickListener.onClick(getAdapterPosition());
        }
    }
}
