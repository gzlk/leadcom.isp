package com.gzlk.android.isp.nim.model.extension;

import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;

import org.json.JSONObject;

/**
 * <b>功能描述：</b>消息基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/15 20:59 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/15 20:59 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class BaseAttachment implements MsgAttachment {

    protected int type;

    public BaseAttachment(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void fromJson(JSONObject data) {
        if (data != null) {
            parseData(data);
        }
    }

    @Override
    public String toJson(boolean send) {
        return BaseAttachmentParser.packData(type, packData());
    }

    protected abstract void parseData(JSONObject data);

    protected abstract JSONObject packData();
}
