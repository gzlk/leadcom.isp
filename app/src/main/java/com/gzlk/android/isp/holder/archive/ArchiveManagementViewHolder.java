package com.gzlk.android.isp.holder.archive;

import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.attachment.AttachmentViewHolder;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.archive.Archive;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CorneredView;
import com.hlk.hlklib.lib.view.CustomTextView;

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

    public ArchiveManagementViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(Archive archive, String searchingText) {
        selector.setVisibility(archive.isSelectable() ? View.VISIBLE : View.GONE);
        selector.setTextColor(getColor(archive.isSelected() ? R.color.colorPrimary : R.color.textColorHintLightLight));
        String image = firstImage(archive);
        // 有附件且附件不包含图片时，显示文档图标
        imageView.setVisibility(!StringHelper.isEmpty(image) ? View.VISIBLE : View.GONE);
        if (!StringHelper.isEmpty(image)) {
            // 显示第一章图片
            imageView.displayImage(image, getDimension(R.dimen.ui_static_dp_60), false, false);
        }
        // 显示第一个文件的格式
        image = firstFile(archive);
        iconContainer.setVisibility(!StringHelper.isEmpty(image) ? View.VISIBLE : View.GONE);
        if (!StringHelper.isEmpty(image)) {
            assert image != null;
            String name = image.substring(image.lastIndexOf('/') + 1);
            String ext = name.substring(name.lastIndexOf('.') + 1);
            iconView.setText(AttachmentViewHolder.getFileExtension(ext));
        }
        String text = archive.getTitle();
        if (StringHelper.isEmpty(text)) {
            text = StringHelper.getString(R.string.ui_archive_approve_no_title);
        }
        text = getSearchingText(text, searchingText);
        titleView.setText(Html.fromHtml(text));
        filesView.setText(StringHelper.getString(R.string.ui_archive_approving_archive_attachments, filesCount(archive)));
        filesView.setVisibility(filesCount(archive) > 0 ? View.VISIBLE : View.GONE);
        sourceView.setText("来源：无");
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

    private String firstFile(Archive archive) {
        if (null != archive.getOffice() && archive.getOffice().size() > 0) {
            return archive.getOffice().get(0).getUrl();
        }
        if (null != archive.getVideo() && archive.getVideo().size() > 0) {
            return archive.getVideo().get(0).getUrl();
        }
        return (null != archive.getAttach() && archive.getAttach().size() > 0) ? archive.getAttach().get(0).getUrl() : null;
    }

    private String firstImage(Archive archive) {
        return (null != archive.getImage() && archive.getImage().size() > 0) ? archive.getImage().get(0).getUrl() : null;
    }

    @Click({R.id.ui_holder_view_archive_management_root})
    private void elementClick(View view) {
        if (null != mOnViewHolderClickListener) {
            mOnViewHolderClickListener.onClick(getAdapterPosition());
        }
    }
}
