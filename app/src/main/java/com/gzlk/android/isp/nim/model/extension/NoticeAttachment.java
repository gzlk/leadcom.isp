package com.gzlk.android.isp.nim.model.extension;

import org.json.JSONObject;

/**
 * <b>功能描述：</b>通知消息类实体<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/15 18:17 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/15 18:17 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class NoticeAttachment extends CustomAttachment {

    public NoticeAttachment() {
        super(AttachmentType.NOTICE);
    }

    // 所属活动的id
    private String actId;
    // 通知标题
    private String title;
    // 通知内容
    private String content;

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    protected void parseData(JSONObject data) {
        super.parseData(data);
        try {
            if (data.has("actId")) {
                actId = data.getString("actId");
            }
            if (data.has("title")) {
                title = data.getString("title");
            }
            if (data.has("content")) {
                content = data.getString("content");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject packData() {
        JSONObject object = super.packData();
        try {
            object.put("actId", actId)
                    .put("title", title)
                    .put("content", content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }
}
