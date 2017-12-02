package com.leadcom.android.isp.model.activity;

import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.organization.Organization;
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

    public interface Field {
        String AuditorId = "auditorId";
        String AuditorDate = "auditorDate";
    }

    @Column(Organization.Field.GroupId)
    private String groupId;        //群体ID
    @Column(Activity.Field.ActivityId)
    private String actId;          //活动id
    @Column(Archive.Field.Type)
    private int type;              //文件类型(1.文档,2.图片,3.视频,4.其他)
    @Column(Model.Field.Name)
    private String name;           //文件名(带后缀)
    @Column(Attachment.Field.Url)
    private String url;            //文件url;
    @Column(Attachment.Field.Pdf)
    private String pdf;            //Office文档映射的PDF地址(用来浏览Office文档)
    @Column(Model.Field.UserId)
    private String userId;         //档案发起者ID
    @Column(Model.Field.UserName)
    private String userName;       //档案发起者姓名
    @Column(Model.Field.CreateDate)
    private String createDate;     //创建时间
    @Column(Activity.Field.Status)
    private int status;            //审核状态:1.未审核(如果档案已存档,则表示未通过审核),2.审核通过,3.审核未通过
    @Column(Field.AuditorId)
    private String auditorId;      //档案审核人用户ID
    @Column(Field.AuditorDate)
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
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
