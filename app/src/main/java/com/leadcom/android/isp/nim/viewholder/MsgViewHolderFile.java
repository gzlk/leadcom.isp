package com.leadcom.android.isp.nim.viewholder;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.etc.ImageCompress;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.nim.file.FileIcons;
import com.leadcom.android.isp.nim.file.FilePreviewHelper;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.util.file.AttachmentStore;
import com.netease.nim.uikit.common.util.file.FileUtil;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderBase;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * <b>功能描述：</b>网易云信显示文件附件的Holder<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/07 14:17 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/07 14:17 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MsgViewHolderFile extends MsgViewHolderBase {

    private ImageView fileIcon;
    private TextView fileNameLabel;
    private TextView fileStatusLabel;
    private ProgressBar progressBar;

    private FileAttachment msgAttachment;

    public MsgViewHolderFile(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.nim_msg_view_holder_file;
    }

    @Override
    protected void inflateContentView() {
        fileIcon = (ImageView) view.findViewById(R.id.message_item_file_icon_image);
        fileNameLabel = (TextView) view.findViewById(R.id.message_item_file_name_label);
        fileStatusLabel = (TextView) view.findViewById(R.id.message_item_file_status_label);
        progressBar = (ProgressBar) view.findViewById(R.id.message_item_file_transfer_progress_bar);
    }

    @Override
    protected void bindContentView() {
        msgAttachment = (FileAttachment) message.getAttachment();
        String path = msgAttachment.getPath();
        initDisplay();

        if (!TextUtils.isEmpty(path)) {
            loadImageView();
        } else {
            refreshDownloadStatus(message);
        }
    }

    private void refreshDownloadStatus(IMMessage message) {
        AttachStatusEnum status = message.getAttachStatus();
        switch (status) {
            case def:
                updateFileStatusLabel();
                break;
            case transferring:
                fileStatusLabel.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                int percent = (int) (getMsgAdapter().getProgress(message) * 100);
                progressBar.setProgress(percent);
                break;
            case transferred:
            case fail:
                updateFileStatusLabel();
                break;
        }
    }

    private void loadImageView() {
        fileStatusLabel.setVisibility(View.VISIBLE);
        // 文件长度
        fileStatusLabel.setText(FileUtil.formatFileSize(msgAttachment.getSize()));

        progressBar.setVisibility(View.GONE);
    }

    private void initDisplay() {
        int iconResId = FileIcons.smallIcon(msgAttachment.getDisplayName());
        fileIcon.setImageResource(iconResId);
        fileNameLabel.setText(msgAttachment.getDisplayName());
    }

    private void updateFileStatusLabel() {
        fileStatusLabel.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        // 文件长度
        StringBuilder sb = new StringBuilder();
        sb.append(FileUtil.formatFileSize(msgAttachment.getSize()));
        sb.append("  ");
        // 下载状态
        String path = msgAttachment.getPathForSave();
        if (AttachmentStore.isFileExist(path)) {
            sb.append(context.getString(R.string.file_transfer_state_downloaded));
        } else {
            sb.append(context.getString(R.string.file_transfer_state_undownload));
        }
        fileStatusLabel.setText(sb.toString());
    }

    @Override
    protected void onItemClick() {
        if (isOriginDataHasDownloaded(message)) {
            // 已经下载时，打开预览
            FileAttachment attachment = (FileAttachment) message.getAttachment();
            // 如果是Office文档或图片，则取url地址，否则取本地缓存目录地址
            String ext = attachment.getExtension();
            String url = (Attachment.isOffice(ext) || ImageCompress.isImage(ext)) ? attachment.getUrl() : attachment.getPath();
            tryToOpenFile(url, attachment.getDisplayName(), attachment.getExtension());
        } else {
            // 未下载时开始下载
            downloadFile();
        }
    }

    @Override
    protected int leftBackground() {
        return R.drawable.nim_message_left_white_bg;
    }

    @Override
    protected int rightBackground() {
        return R.drawable.nim_message_right_blue_bg;
    }

    // 尝试打开已经下载了的文件
    private void tryToOpenFile(String path, String name, String extension) {
        FilePreviewHelper.previewFile(context, path, name, extension);
    }

    // 附件是否已经下载了
    private boolean isOriginDataHasDownloaded(final IMMessage message) {
        return !TextUtils.isEmpty(((FileAttachment) message.getAttachment()).getPath());
    }

    private void downloadFile() {
        registerObservers(true);
        NIMClient.getService(MsgService.class).downloadAttachment(message, false);
    }

    private void registerObservers(boolean register) {
        NIMClient.getService(MsgServiceObserve.class).observeMsgStatus(statusObserver, register);
    }

    private Observer<IMMessage> statusObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage msg) {
            if (!msg.isTheSame(message)) {
                return;
            }
            refreshDownloadStatus(msg);
            if (msg.getAttachStatus() == AttachStatusEnum.fail) {
                ToastHelper.make().showMsg(R.string.ui_nim_attachment_download_failure);
            }
        }
    };
}
