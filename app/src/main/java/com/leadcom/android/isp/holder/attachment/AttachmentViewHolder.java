package com.leadcom.android.isp.holder.attachment;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.etc.ImageCompress;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.FilePreviewHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.model.common.Attachment;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CorneredView;
import com.hlk.hlklib.lib.view.CustomTextView;

import java.io.File;
import java.util.Locale;

/**
 * <b>功能描述：</b>档案中的附件item<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/25 17:01 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/25 17:01 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class AttachmentViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_attachment_icon_container)
    private CorneredView iconContainer;
    @ViewId(R.id.ui_holder_view_attachment_icon)
    private CustomTextView iconTextView;
    @ViewId(R.id.ui_holder_view_attachment_image)
    private ImageDisplayer imageDisplayer;
    @ViewId(R.id.ui_holder_view_attachment_name)
    private TextView nameTextView;
    @ViewId(R.id.ui_holder_view_attachment_additional)
    private LinearLayout additionalView;
    @ViewId(R.id.ui_holder_view_attachment_path)
    private TextView pathTextView;
    @ViewId(R.id.ui_holder_view_attachment_size)
    private TextView sizeTextView;
    @ViewId(R.id.ui_holder_view_attachment_uploading)
    private View loadingView;
    @ViewId(R.id.ui_holder_view_attachment_delete)
    private CustomTextView deleteView;

    public AttachmentViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        imageDisplayer.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    // 设置是否显示删除按钮
    public void setEditable(boolean editable) {
        deleteView.setVisibility(editable ? View.VISIBLE : View.GONE);
    }

//    public void showContent(String filePath) {
//        String name = filePath.substring(filePath.lastIndexOf('/') + 1);
//        String ext = name.substring(name.lastIndexOf('.') + 1);
//        iconTextView.setText(getFileExtension(ext));
//        nameTextView.setText(name);
//        pathTextView.setText(filePath.replace(name, ""));
//        boolean isFile = filePath.indexOf('/') >= 0;
//        additionalView.setVisibility(isFile ? View.VISIBLE : View.GONE);
//        if (isFile) {
//            File file = new File(filePath);
//            if (file.exists() && file.length() > 0) {
//                sizeTextView.setText(Utils.formatSize(file.length()));
//            } else {
//                sizeTextView.setText(null);
//            }
//        }
//    }

    public void showContent(Attachment attachment) {
        iconTextView.setTag(R.id.hlklib_ids_custom_view_click_tag, attachment);
        iconTextView.setText(getFileExtension(attachment.getExt()));
        iconContainer.setVisibility(attachment.isImage() ? View.GONE : View.VISIBLE);
        imageDisplayer.setVisibility(attachment.isImage() ? View.VISIBLE : View.GONE);
        if (attachment.isImage()) {
            String url = attachment.isLocalFile() ? attachment.getFullPath() : attachment.getUrl();
            imageDisplayer.displayImage(url, getDimension(R.dimen.ui_static_dp_30), false, false);
        }
        nameTextView.setText(attachment.getName());
        pathTextView.setText(attachment.getFullPath());
        additionalView.setVisibility(attachment.isLocalFile() ? View.VISIBLE : View.GONE);
        if (attachment.isLocalFile()) {
            File file = new File(attachment.getFullPath());
            if (file.exists() && file.length() > 0) {
                sizeTextView.setText(Utils.formatSize(file.length()));
            } else {
                sizeTextView.setText(null);
            }
        }
        loadingView.setVisibility(attachment.isSelected() ? View.VISIBLE : View.GONE);
        deleteView.setVisibility(attachment.isSelected() ? View.GONE : View.VISIBLE);
    }

    public static int getFileExtension(String ext) {
        int res = R.string.ui_icon_attachment_unknown;
        ext = ext.toLowerCase(Locale.getDefault());
        if (ext.contains("doc")) {
            res = R.string.ui_icon_attachment_word;
        } else if (ext.contains("xls")) {
            res = R.string.ui_icon_attachment_excel;
        } else if (ext.contains("ppt")) {
            res = R.string.ui_icon_attachment_powerpoint;
        } else if (ImageCompress.isImage(ext)) {
            res = R.string.ui_icon_attachment_picture;
        } else if (ImageCompress.isVideo(ext)) {
            res = R.string.ui_icon_attachment_video;
        } else {
            switch (ext) {
                case "pdf":
                    res = R.string.ui_icon_attachment_pdf;
                    break;
                case "rar":
                    res = R.string.ui_icon_attachment_rar;
                    break;
                case "zip":
                    res = R.string.ui_icon_attachment_zip;
                    break;
                case "mp4":
                    res = R.string.ui_icon_attachment_mp4;
                    break;
                case "txt":
                    res = R.string.ui_icon_attachment_text;
                    break;
            }
        }
        return res;
    }

    @Click({R.id.ui_holder_view_attachment_delete, R.id.ui_holder_view_attachment_content})
    private void elementClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ui_holder_view_attachment_delete:
                if (null != mOnViewHolderClickListener) {
                    mOnViewHolderClickListener.onClick(getAdapterPosition());
                }
                break;
            case R.id.ui_holder_view_attachment_content:
                // 文件预览
                Object object = iconTextView.getTag(R.id.hlklib_ids_custom_view_click_tag);
                if (null != object) {
                    Attachment attachment = (Attachment) object;
                    FilePreviewHelper.previewFile(iconTextView.getContext(), attachment.getUrl(), attachment.getName(), attachment.getExt());
                }
                break;
        }
    }
}
