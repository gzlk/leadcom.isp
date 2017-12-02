package com.leadcom.android.isp.listener;

import com.leadcom.android.isp.nim.model.notification.NimMessage;

/**
 * <b>功能描述：</b>接收到推送通知<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/08/02 21:08 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/08/02 21:08 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public interface OnNimMessageEvent {
    void onMessageEvent(NimMessage message);
}
