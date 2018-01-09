package com.leadcom.android.isp.api.upload;

import com.leadcom.android.isp.api.Api;

import org.json.JSONObject;

/**
 * <b>功能描述：</b>文件上传返回的结果<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/23 16:42 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/23 16:42 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class Upload extends Api {

    private String orgName;
    private String filePath;
    private long fileSize;
    private String createTime;
    private String id;
    private String objectId;
    private String objectType;

    public Upload() {
        super();
    }

    public Upload(JSONObject object) {
        super();
        if (null != object) {
            orgName = object.optString("orgName", "");
            createTime = object.optString("createTime", "");
            fileSize = object.optLong("fileSize", 0);
            filePath = object.optString("filePath", "");
            id = object.optString("id", "");
            objectId = object.optString("objectId", "");
            objectType = object.optString("objectType", "");
        }
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }
}
