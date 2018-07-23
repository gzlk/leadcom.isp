package com.leadcom.android.isp.model.archive;

import com.leadcom.android.isp.model.Model;

/**
 * <b>功能描述：</b>档案意见建议<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/07/23 10:23 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/07/23 10:23 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class Suggest extends Model {

    private String createTime;
    private String content;
    private String byUserId;
    private String byUserName;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getByUserId() {
        return byUserId;
    }

    public void setByUserId(String byUserId) {
        this.byUserId = byUserId;
    }

    public String getByUserName() {
        return byUserName;
    }

    public void setByUserName(String byUserName) {
        this.byUserName = byUserName;
    }
}
