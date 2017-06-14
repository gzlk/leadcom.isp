package com.gzlk.android.isp.nim.action;

import com.gzlk.android.isp.R;
import com.netease.nim.uikit.common.media.picker.activity.PickImageActivity;
import com.netease.nim.uikit.session.actions.PickImageAction;
import com.netease.nim.uikit.session.constant.RequestCode;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.io.File;

/**
 * <b>功能描述：</b>网易云信照相机拍照Action<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/07 14:28 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/07 14:28 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class CameraAction extends PickImageAction {

    public CameraAction() {
        super(R.drawable.nim_action_camera, R.string.ui_nim_action_camera, false);
    }

    @Override
    public void onClick() {
        openCamera();
    }

    @Override
    protected void onPicked(File file) {
        IMMessage message = MessageBuilder.createImageMessage(getAccount(), getSessionType(), file, file.getName());
        sendMessage(message);
    }

    private void openCamera() {
        int requestCode = makeRequestCode(RequestCode.PICK_IMAGE);
        PickImageActivity.start(getActivity(), requestCode, PickImageActivity.FROM_CAMERA, tempFile(), true, 1, true, false, 0, 0);
    }
}
