package com.leadcom.android.isp.nim.model.extension;

import org.json.JSONObject;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/04/01 09:25 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class MomentAttachment extends CustomAttachment {

    public MomentAttachment() {
        super(AttachmentType.MOMENT);
    }

    MomentAttachment(int type) {
        super(type);
    }

    /**
     * 标题
     */
    private String title;
    /**
     * 摘要
     */
    private String summary;
    /**
     * 图片地址
     */
    private String image;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    protected void parseData(JSONObject data) {
        super.parseData(data);
        try {
            title = data.optString("title", "");
            summary = data.optString("summary", "");
            image = data.optString("image", "");
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    @Override
    protected JSONObject packData() {
        JSONObject object = super.packData();
        try {
            object.put("title", title)
                    .put("summary", summary)
                    .put("image", image);
        } catch (Exception ignore) {
        }
        return object;
    }
}
