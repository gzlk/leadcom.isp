package com.leadcom.android.isp.nim.model.extension;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/19 20:33 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/19 20:33 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

interface AttachmentType {
    /**
     * 群通知
     */
    int NOTICE = 1;
    /**
     * 群签到提醒
     */
    int SIGN = 2;
    /**
     * 投票
     */
    int VOTE = 3;
    /**
     * 议题
     */
    int TOPIC = 4;
    /**
     * 会议纪要
     */
    int MINUTES = 5;
    /**
     * 分享的档案
     */
    int ARCHIVE = 6;
}
