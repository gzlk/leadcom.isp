package com.gzlk.android.isp.holder.archive;

import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.attachment.AttachmentViewHolder;
import com.gzlk.android.isp.model.common.Attachment;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CorneredView;
import com.hlk.hlklib.lib.view.CustomTextView;

/**
 * <b>功能描述：</b>档案详情中的附件列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/11/04 15:31 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/11/04 15:31 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ArchiveAttachmentViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_archive_attachment_icon_container)
    private CorneredView iconLayout;
    @ViewId(R.id.ui_holder_view_archive_attachment_icon)
    private CustomTextView iconView;
    @ViewId(R.id.ui_holder_view_archive_attachment_name)
    private TextView nameView;
    @ViewId(R.id.ui_holder_view_archive_attachment_size)
    private TextView sizeView;

    public ArchiveAttachmentViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(Attachment attachment) {
        iconView.setText(AttachmentViewHolder.getFileExtension(attachment.getExt()));
        nameView.setText(attachment.getName());
        iconLayout.setNormalColor(getColor(attachment.iconColor()));
        iconLayout.setActiveColor(getColor(attachment.iconColor()));
        sizeView.setVisibility(0 >= attachment.getSize() ? View.GONE : View.VISIBLE);
        sizeView.setText(Utils.formatSize(attachment.getSize()));
    }

    @Click({R.id.ui_holder_view_archive_attachment_layout})
    private void elementClick(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        }
    }
}
