package com.leadcom.android.isp.nim.model.extension;

import org.json.JSONObject;

/**
 * <b>功能描述：</b>分享到群中的档案内容<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/12/22 09:14 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ArchiveAttachment extends CustomAttachment {

    public ArchiveAttachment() {
        super(AttachmentType.ARCHIVE);
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
    /**
     * 档案类型：1=个人档案；2=组织档案；3=个人档案草稿；4=组织档案草稿
     */
    private int archiveType;

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

    public int getArchiveType() {
        return archiveType;
    }

    public void setArchiveType(int archiveType) {
        this.archiveType = archiveType;
    }

    @Override
    protected void parseData(JSONObject data) {
        super.parseData(data);
        try {
            title = data.optString("title", "");
            summary = data.optString("summary", "");
            image = data.optString("image", "");
            archiveType = data.optInt("archiveType", 1);
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
                    .put("image", image)
                    .put("archiveType", archiveType);
        } catch (Exception ignore) {
        }
        return object;
    }
}
