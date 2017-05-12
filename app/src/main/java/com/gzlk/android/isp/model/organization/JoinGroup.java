package com.gzlk.android.isp.model.organization;

import com.gzlk.android.isp.model.Model;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

/**
 * <b>功能描述：</b>加入组织<br />
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
        public static final String AppUserId = "appUserId";
        public static final String AppUsrName = "appUserName";
        public static final String HandleUserId = "handleUserId";
        public static final String HandleUserName = "handleUserName";
        public static final String AllowSeeAppInfo = "allowSeeApplicantBaseInfo";
    }

    //群
    @Column(Organization.Field.GroupId)
    private String groupId;
    @Column(Organization.Field.GroupName)
    private String groupName;
    //申请人
    @Column(Field.AppUserId)
    private String appUserId;
    @Column(Field.AppUsrName)
    private String appUserName;
    //申请处理人
    @Column(Field.HandleUserId)
    private String handleUserId;
    @Column(Field.HandleUserName)
    private String handleUserName;
    //申请时间
    @Column(Model.Field.CreateDate)
    private String createTime;
    //是否允许查看申请人信息
    @Column(Field.AllowSeeAppInfo)
    private String allowSeeApplicantBaseInfo;
    //处理时间
    @Column(Invitation.Field.HandleTime)
    private String handleTime;
    //申请附加消息
    @Column(Invitation.Field.Message)
    private String msg;
    //状态
    @Column(Invitation.Field.State)
    private String state;
    //uuid 文本消息和申请流程消息公用一个uuid
    @Column(Model.Field.UUID)
    private String uuid;
    //访问令牌：用于移动端访问的唯标志
    @Column(Model.Field.AccessToken)
    private String accessToken;

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

    public String getAppUserId() {
        return appUserId;
    }

    public void setAppUserId(String appUserId) {
        this.appUserId = appUserId;
    }

    public String getAppUserName() {
        return appUserName;
    }

    public void setAppUserName(String appUserName) {
        this.appUserName = appUserName;
    }

    public String getHandleUserId() {
        return handleUserId;
    }

    public void setHandleUserId(String handleUserId) {
        this.handleUserId = handleUserId;
    }

    public String getHandleUserName() {
        return handleUserName;
    }

    public void setHandleUserName(String handleUserName) {
        this.handleUserName = handleUserName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getAllowSeeApplicantBaseInfo() {
        return allowSeeApplicantBaseInfo;
    }

    public void setAllowSeeApplicantBaseInfo(String allowSeeApplicantBaseInfo) {
        this.allowSeeApplicantBaseInfo = allowSeeApplicantBaseInfo;
    }

    public String getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(String handleTime) {
        this.handleTime = handleTime;
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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
