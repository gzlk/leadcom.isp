package com.leadcom.android.isp.nim.action;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;

import com.netease.nim.uikit.session.actions.BaseAction;
import com.netease.nim.uikit.session.constant.RequestCode;
import com.netease.nim.uikit.session.helper.VideoMessageHelper;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.io.File;

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
    private boolean isChoose = false;
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
        videoHelper().setVideoSources(makeRequestCode(RequestCode.GET_LOCAL_VIDEO), makeRequestCode(RequestCode.CAPTURE_VIDEO));
        if (isChoose) {
            // 从相册中选取视频文件
            videoHelper().chooseVideoFromLocal();
        } else {
            // 直接拍摄视频
            videoHelper().chooseVideoFromCamera();
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
