package com.gzlk.android.isp.nim.model.extension;

import org.json.JSONObject;

/**
 * <b>功能描述：</b>会议纪要消息实体<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/08/30 10:27 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/08/30 10:27 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MinutesAttachment extends CustomAttachment {

    public MinutesAttachment() {
        super(AttachmentType.MINUTES);
    }

    private String url;
    private String title;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    protected void parseData(JSONObject data) {
        super.parseData(data);
        super.parseData(data);
        try {
            if (data.has("url")) {
                url = data.getString("url");
            }
            if (data.has("title")) {
                title = data.getString("title");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject packData() {
        JSONObject object = super.packData();
        try {
            object.put("url", url)
                    .put("title", title);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }
}
