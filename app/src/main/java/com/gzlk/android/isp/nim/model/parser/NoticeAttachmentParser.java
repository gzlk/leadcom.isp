package com.gzlk.android.isp.nim.model.parser;

import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.nim.model.extension.NoticeAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser;

/**
 * <b>功能描述：</b>通知类消息解析器<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/15 18:54 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/15 18:54 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class NoticeAttachmentParser implements MsgAttachmentParser {

    @Override
    public MsgAttachment parse(String s) {
        return Json.gson().<NoticeAttachment>fromJson(s, new TypeToken<NoticeAttachment>() {
        }.getType());
    }

    public static String packData(NoticeAttachment notice) {
        return Json.gson().toJson(notice, new TypeToken<NoticeAttachment>() {
        }.getType());
    }
}
