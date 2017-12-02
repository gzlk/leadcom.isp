package com.leadcom.android.isp.nim.model.extension;

import org.json.JSONObject;

/**
 * <b>功能描述：</b>历康自定义消息基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/19 20:44 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/19 20:44 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class CustomAttachment extends BaseAttachment {

    // id
    private String id;
    //创建者Id
    private String creatorId;
    //创建者名称
    private String creatorName;
    // 创建时间
    private String createDate;
    //修改时间
    private String modifyDate;
    // ios中用的id
    private String customId;

    CustomAttachment(int type) {
        super(type);
    }

    public String getId() {
        if (isEmpty(id)) {
            id = customId;
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getCustomId() {
        return customId;
    }

    public void setCustomId(String customId) {
        this.customId = customId;
    }

    @Override
    protected void parseData(JSONObject data) {
        try {
            if (data.has("id")) {
                id = data.getString("id");
            }
            if (data.has("creatorId")) {
                creatorId = data.getString("creatorId");
            }
            if (data.has("creatorName")) {
                creatorName = data.getString("creatorName");
            }
            if (data.has("createDate")) {
                createDate = data.getString("createDate");
            }
            if (data.has("modifyDate")) {
                modifyDate = data.getString("modifyDate");
            }
            if (data.has("customId")) {
                customId = data.getString("customId");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject packData() {
        JSONObject object = new JSONObject();
        try {
            object.put("id", id)
                    .put("customId", customId)
                    .put("creatorId", creatorId)
                    .put("creatorName", creatorName)
                    .put("createDate", createDate)
                    .put("modifyDate", modifyDate);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }
}
