package com.netease.nim.uikit.api;

import android.content.Context;

import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * <b>功能描述：</b>会话中的视频消息点击事件<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/11/10 10:10 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/11/10 10:10 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public interface OnSessionMessageViewHolderClick {
    void onClick(Context context, IMMessage message);
}
