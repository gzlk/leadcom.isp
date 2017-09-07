package com.gzlk.android.isp.holder.archive;

import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.attachment.AttachmentViewHolder;
import com.gzlk.android.isp.lib.view.ExpandableTextView;
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
 * <b>功能描述：</b>个人档案<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/27 00:15 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/27 00:15 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveViewHolder extends BaseViewHolder {

    // header
    @ViewId(R.id.ui_tool_view_document_user_header_image)
    private ImageDisplayer userHead;
    @ViewId(R.id.ui_tool_view_document_user_header_name)
    private TextView userName;
    @ViewId(R.id.ui_tool_view_document_user_header_time)
    private TextView createTime;
    // content
    @ViewId(R.id.ui_holder_view_document_title)
    private TextView documentTitle;
    @ViewId(R.id.ui_holder_view_document_content_layout)
    private LinearLayout documentContentLayout;
    @ViewId(R.id.ui_holder_view_document_content_text)
    private ExpandableTextView documentContentText;
    @ViewId(R.id.ui_holder_view_document_content_image)
    private ImageDisplayer documentContentImage;
    @ViewId(R.id.ui_holder_view_document_content_icon_container)
    private CorneredView documentContentIconContainer;
    @ViewId(R.id.ui_holder_view_document_content_icon)
    private CustomTextView documentContentIcon;

    private ArchiveAdditionalViewHolder additionalViewHolder;
    private int imageWidth, imageHeight;

    public ArchiveViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        userHead.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
        additionalViewHolder = new ArchiveAdditionalViewHolder(itemView, fragment);
        imageWidth = getDimension(R.dimen.ui_static_dp_80);
        imageHeight = getDimension(R.dimen.ui_static_dp_70);
        documentContentImage.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(String url) {
                if (null != mOnViewHolderClickListener) {
                    mOnViewHolderClickListener.onClick(getAdapterPosition());
                }
            }
        });
    }

    public void showContent(Archive archive) {
        userHead.displayImage(archive.getHeadPhoto(), getDimension(R.dimen.ui_static_dp_35), false, false);
        userName.setText(archive.getUserName());
        createTime.setText(fragment().formatDate(archive.getCreateDate()));
        documentTitle.setText(Html.fromHtml(archive.getTitle()));
        documentContentText.setText(StringHelper.escapeFromHtml(archive.getIntro()));
        documentContentText.makeExpandable();
        additionalViewHolder.showContent(archive);
        documentContentIconContainer.setVisibility(View.GONE);
        String path = archive.getCover();
        if (!isEmpty(path)) {
            showImage(path);
            return;
        }
        // 视频
        Attachment attachment = firstOf(archive.getVideo());
        if (null != attachment && !isEmpty(attachment.getUrl())) {
            // 显示视频图片或默认视频图片
            showIconAttachment(attachment);
            return;
        }
        // office 文档
        attachment = firstOf(archive.getOffice());
        if (null != attachment && !isEmpty(attachment.getUrl())) {
            showIconAttachment(attachment);
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
            showIconAttachment(attachment);
            return;
        }
        // 什么附件都没有则不显示图标
        documentContentImage.setVisibility(View.GONE);
        documentContentIconContainer.setVisibility(View.GONE);
    }

    private Attachment firstOf(ArrayList<Attachment> list) {
        return (null != list && list.size() > 0) ? list.get(0) : null;
    }

    private void showImage(String url) {
        documentContentImage.setVisibility(View.VISIBLE);
        documentContentImage.displayImage(url, imageWidth, imageHeight, false, false);
    }

    private void showIconAttachment(Attachment attachment) {
        documentContentImage.setVisibility(View.GONE);
        documentContentIconContainer.setVisibility(View.VISIBLE);
        documentContentIconContainer.setNormalColor(getColor(attachment.iconColor()));
        documentContentIcon.setText(AttachmentViewHolder.getFileExtension(attachment.getExt()));
    }

    @Click({R.id.ui_holder_view_document_content_container})
    private void elementClick(View view) {
        if (null != mOnViewHolderClickListener) {
            mOnViewHolderClickListener.onClick(getAdapterPosition());
        }
    }
}
