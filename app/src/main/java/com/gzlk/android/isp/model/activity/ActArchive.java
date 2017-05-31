package com.gzlk.android.isp.model.activity;

import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.common.Attachment;
import com.gzlk.android.isp.model.organization.Organization;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

/**
 * <b>功能描述：</b>活动中的文件<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/31 16:48 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/31 16:48 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(Activity.Table.ACTIVITY_ARCHIVE)
public class ActArchive extends Model {

    @Column(Organization.Field.GroupId)
    private String groupId;        //群体ID
    @Column(Activity.Field.ActivityId)
    private String actId;          //活动id
    @Column(Archive.Field.Type)
    private String type;           //文件类型(1.文档,2.图片,3.视频,4.其他)
    @Column(Field.Name)
    private String name;           //文件名(带后缀)
    @Column(Attachment.Field.Url)
    private String url;            //文件url;
    @Column(Attachment.Field.Pdf)
    private String pdf;            //Office文档映射的PDF地址(用来浏览Office文档)
    @Column(Field.UserId)
    private String userId;         //档案发起者ID
    @Column(Field.UserName)
    private String userName;       //档案发起者姓名
    @Column(Field.CreateDate)
    private String createDate;     //创建时间
    @Column(Activity.Field.Status)
    private String status;         //审核状态:1.未审核(如果档案已存档,则表示未通过审核),2.审核通过,3.审核未通过
    private String auditorId;      //档案审核人用户ID
    private String auditDate;      //审核时间

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAuditorId() {
        return auditorId;
    }

    public void setAuditorId(String auditorId) {
        this.auditorId = auditorId;
    }

    public String getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(String auditDate) {
        this.auditDate = auditDate;
    }
}
