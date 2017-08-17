package com.gzlk.android.isp.nim.model.extension;

import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <b>功能描述：</b>附件消息基类的解析器<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/19 20:38 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/19 20:38 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class BaseAttachmentParser implements MsgAttachmentParser {

    private static final String KEY_TYPE = "type";
    private static final String KEY_DATA = "data";

    @Override
    public MsgAttachment parse(String s) {
        BaseAttachment attachment = null;
        try {
            JSONObject object = new JSONObject(s);
            int type = object.getInt(KEY_TYPE);
            JSONObject data = object.getJSONObject(KEY_DATA);
            switch (type) {
                case AttachmentType.NOTICE:
                    attachment = new NoticeAttachment();
                    break;
                case AttachmentType.SIGN:
                    attachment = new SigningNotifyAttachment();
                    break;
                case AttachmentType.VOTE:
                    attachment = new VoteAttachment();
                    break;
            }
            if (null != attachment) {
                attachment.fromJson(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return attachment;
    }

    /**
     * 打包数据
     */
    static String packData(int type, JSONObject data) {
        JSONObject object = new JSONObject();
        try {
            object.put(KEY_TYPE, type);
            if (null != data) {
                object.put(KEY_DATA, data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }
}
