package com.gzlk.android.isp.nim.action;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.helper.StringHelper;
import com.netease.nim.uikit.session.actions.BaseAction;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.io.File;

/**
 * <b>功能描述：</b>网易云信发送文件Action<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/07 10:37 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/07 10:37 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class FileAction extends BaseAction {

    /**
     * 文件
     */
    public FileAction() {
        super(R.drawable.nim_action_file, R.string.ui_nim_action_file);
    }

    @Override
    public void onClick() {
        pickFile();
    }

    // 文件选择
    private transient FilePickerDialog filePickerDialog;

    private void pickFile() {
        if (null == filePickerDialog) {
            DialogProperties properties = new DialogProperties();
            // 选择文件
            properties.selection_type = DialogConfigs.FILE_SELECT;
            // 可以多选
            properties.selection_mode = DialogConfigs.MULTI_MODE;
            // 最多可选文件数量
            properties.maximum_count = 5;
            // 文件扩展名过滤
            //properties.extensions = StringHelper.getStringArray(R.array.ui_base_file_pick_types);
            filePickerDialog = new FilePickerDialog(getActivity(), properties);
            filePickerDialog.setTitle(StringHelper.getString(R.string.ui_text_document_picker_title));
            filePickerDialog.setPositiveBtnName(StringHelper.getString(R.string.ui_base_text_confirm));
            filePickerDialog.setNegativeBtnName(StringHelper.getString(R.string.ui_base_text_cancel));
            filePickerDialog.setDialogSelectionListener(new DialogSelectionListener() {
                @Override
                public void onSelectedFilePaths(String[] strings) {
                    // 按文件挨个发送消息
                    sendFile(strings);
                }
            });
        }
        filePickerDialog.show();
    }

    private void sendFile(String[] files) {
        if (null == files || files.length < 1) return;

        for (String path : files) {
            sendFile(path);
        }
    }

    private void sendFile(String path) {
        File file = new File(path);
        IMMessage message = MessageBuilder.createFileMessage(getAccount(), getSessionType(), file, file.getName());
        sendMessage(message);
    }
}
