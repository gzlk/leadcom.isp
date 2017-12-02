package com.leadcom.android.isp.model.organization;

import com.leadcom.android.isp.model.Model;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

/**
 * <b>功能描述：</b>加入组织的申请<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/10 09:40 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/10 09:40 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(Organization.Table.JOIN)
public class JoinGroup extends Model {
    static class Field {
        public static final String AppUserId = "applicantId";
        public static final String AppUsrName = "applicantName";
        public static final String HandleUserId = "handlerId";
        public static final String HandleUserName = "handlerName";
        public static final String AllowSeeAppInfo = "allowSeeApplicantBaseInfo";
    }

    //群
    @Column(Organization.Field.GroupId)
    private String groupId;
    @Column(Organization.Field.GroupName)
    private String groupName;
    //申请人
    @Column(Field.AppUserId)
    private String applicantId;
    @Column(Field.AppUsrName)
    private String applicantName;
    //申请处理人
    @Column(Field.HandleUserId)
    private String handlerId;
    @Column(Field.HandleUserName)
    private String handlerName;
    //申请时间
    @Column(Model.Field.CreateDate)
    private String createDate;
    //是否允许查看申请人信息
    @Column(Field.AllowSeeAppInfo)
    private String allowSeeApplicantBaseInfo;
    //处理时间
    @Column(Invitation.Field.HandleDate)
    private String handleDate;
    //申请附加消息
    @Column(Invitation.Field.Message)
    private String msg;
    //状态
    @Column(Invitation.Field.State)
    private String state;
    //uuid 文本消息和申请流程消息公用一个uuid
    @Column(Model.Field.UUID)
    private String uuid;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getHandlerId() {
        return handlerId;
    }

    public void setHandlerId(String handlerId) {
        this.handlerId = handlerId;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getAllowSeeApplicantBaseInfo() {
        return allowSeeApplicantBaseInfo;
    }

    public void setAllowSeeApplicantBaseInfo(String allowSeeApplicantBaseInfo) {
        this.allowSeeApplicantBaseInfo = allowSeeApplicantBaseInfo;
    }

    public String getHandleDate() {
        return handleDate;
    }

    public void setHandleDate(String handleDate) {
        this.handleDate = handleDate;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
