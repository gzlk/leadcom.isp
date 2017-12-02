package com.leadcom.android.isp.nim.action;

import com.leadcom.android.isp.R;

/**
 * <b>功能描述：</b>网易云信选择视频Action<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/16 09:06 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/16 09:06 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class VideoCaptureAction extends ChooseVideoAction {

    /**
     * 拍摄视频
     */
    public VideoCaptureAction() {
        super(R.drawable.nim_action_video_capture, R.string.ui_nim_action_video_capture, false);
    }
}
