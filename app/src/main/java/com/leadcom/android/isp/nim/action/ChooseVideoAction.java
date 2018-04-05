package com.leadcom.android.isp.nim.action;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;

import com.netease.nim.uikit.business.session.actions.BaseAction;
import com.netease.nim.uikit.business.session.constant.RequestCode;
import com.netease.nim.uikit.business.session.helper.VideoMessageHelper;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.io.File;
import java.util.ArrayList;

/**
 * <b>功能描述：</b>网易云信视频相关Action<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/16 08:54 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/16 08:54 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class ChooseVideoAction extends BaseAction {

    // 是否是选择视频
    private boolean isChoose;
    // 视频
    private transient VideoMessageHelper videoMessageHelper;

    /**
     * 视频选择Action基类
     */
    protected ChooseVideoAction(int iconResId, int titleId, boolean choose) {
        super(iconResId, titleId);
        isChoose = choose;
    }

    @Override
    public void onClick() {
        //videoHelper().showVideoSource(makeRequestCode(RequestCode.GET_LOCAL_VIDEO), makeRequestCode(RequestCode.CAPTURE_VIDEO));
        if (isChoose) {
            // 从相册中选取视频文件
            if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // 已有外置SD卡访问权限
                videoHelper().chooseVideoFromLocal(makeRequestCode(RequestCode.GET_LOCAL_VIDEO));
            } else {
                // 没有权限时申请运行时权限
                requestPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
            }
        } else {
            // 直接拍摄视频
            ArrayList<String> permissions = new ArrayList<>();
            if (!hasPermission(Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (!hasPermission(Manifest.permission.RECORD_AUDIO)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (permissions.size() < 1) {
                // 已有相机和录音权限
                videoHelper().chooseVideoFromCamera(makeRequestCode(RequestCode.CAPTURE_VIDEO));
            } else {
                requestPermission(permissions.toArray(new String[permissions.size()]));
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RequestCode.GET_LOCAL_VIDEO:
                videoHelper().onGetLocalVideoResult(data);
                break;
            case RequestCode.CAPTURE_VIDEO:
                videoHelper().onCaptureVideoResult(data);
                break;
        }
    }

    /**
     * ********************** 视频 *******************************
     */
    private void initVideoMessageHelper() {
        videoMessageHelper = new VideoMessageHelper(getActivity(), new VideoMessageHelper.VideoMessageHelperListener() {

            @Override
            public void onVideoPicked(File file, String md5) {
                MediaPlayer mediaPlayer = getVideoMediaPlayer(file);
                long duration = mediaPlayer == null ? 0 : mediaPlayer.getDuration();
                int height = mediaPlayer == null ? 0 : mediaPlayer.getVideoHeight();
                int width = mediaPlayer == null ? 0 : mediaPlayer.getVideoWidth();
                IMMessage message = MessageBuilder.createVideoMessage(getAccount(), getSessionType(), file, duration, width, height, md5);
                sendMessage(message);
            }
        });
    }

    /**
     * 获取视频mediaPlayer
     *
     * @param file 视频文件
     * @return mediaPlayer
     */
    private MediaPlayer getVideoMediaPlayer(File file) {
        try {
            return MediaPlayer.create(getActivity(), Uri.fromFile(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private VideoMessageHelper videoHelper() {
        if (videoMessageHelper == null) {
            initVideoMessageHelper();
        }
        return videoMessageHelper;
    }
}
