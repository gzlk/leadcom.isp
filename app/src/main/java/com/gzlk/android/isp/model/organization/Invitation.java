package com.gzlk.android.isp.model.organization;

import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.activity.Activity;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;

import java.util.List;

/**
 * <b>功能描述：</b>邀请用户加入组织<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/07 09:04 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/07 09:04 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(Organization.Table.INVITATION)
public class Invitation extends Model {

    public interface Field {
        String InviterId = "inviterId";
        String InviterName = "inviterName";
        String InviteeId = "inviteeId";
        String InviteeName = "inviteeName";
        String AllowSee = "allowSeeInviterBaseInfo";
        String HandleDate = "handleDime";
        String Message = "message";
        String State = "state";
        String CreateTime = "createTime";
    }

    public static boolean isInvited(String userId, String groupId, String squadId) {
        QueryBuilder<Invitation> builder = new QueryBuilder<>(Invitation.class);
        if (!StringHelper.isEmpty(squadId)) {
            // 邀请进小组的
            builder = builder.whereEquals(Organization.Field.SquadId, squadId);
            //.whereAppendAnd().whereAppend(Organization.Field.GroupId + " IS NULL");
        } else {
            // 邀请进组织的
            builder = builder.whereEquals(Organization.Field.GroupId, groupId);
            //.whereAppendAnd().whereAppend(Organization.Field.SquadId + " IS NULL");
        }
        builder = builder.whereAppendAnd().whereEquals(Invitation.Field.InviteeId, userId);
        List<Invitation> list = new Dao<>(Invitation.class).query(builder);
        return null != list && list.size() > 0;
    }

    public static void removeInvite(String userId, String groupId, String squadId) {
        WhereBuilder builder = new WhereBuilder(Invitation.class);
        if (!isEmpty(squadId)) {
            builder = builder.where(Organization.Field.SquadId + " = ? ", squadId);
        } else {
            builder = builder.where(Organization.Field.GroupId + " = ? ", groupId);
        }
        builder = builder.and().where(Invitation.Field.InviteeId + " = ? ", userId);
        new Dao<>(Invitation.class).delete(builder);
    }

    // 组织id和名称
    @Column(Organization.Field.GroupId)
    private String groupId;
    @Column(Organization.Field.GroupName)
    private String groupName;

    //小组id和名称
    @Column(Organization.Field.SquadId)
    private String groSquId;
    @Column(Organization.Field.SquadName)
    private String groSquName;

    // 活动相关的邀请
    @Column(Activity.Field.ActivityId)
    private String actId;
    @Column(Activity.Field.ActivityName)
    private String actName;
    @Column(Activity.Field.NimId)
    private String tid;
    @Column(Field.CreateTime)
    private String createTime;
    @Column(Activity.Field.ActivityImage)
    private String actImg;

    //邀请人ID和姓名
    @Column(Field.InviterId)
    private String inviterId;
    @Column(Field.InviterName)
    private String inviterName;

    //被邀请人ID和姓名
    @Column(Field.InviteeId)
    private String inviteeId;
    @Column(Field.InviteeName)
    private String inviteeName;

    //邀请时间
    @Column(Model.Field.CreateDate)
    private String createDate;
    //是否允许被邀请人查看群体基本信息
    @Column(Field.AllowSee)
    private String allowSeeInviterBaseInfo;
    //处理时间
    @Column(Field.HandleDate)
    private String handleTime;
    //留言
    @Column(Field.Message)
    private String msg;
    //处理状态：1未处理，2已处理，3已失效
    @Column(Field.State)
    private String state;
    //uuid是作为联结业务流消息和显示消息的字段，可被视为是Message表的外键
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

    public String getGroSquId() {
        return groSquId;
    }

    public void setGroSquId(String groSquId) {
        this.groSquId = groSquId;
    }

    public String getGroSquName() {
        return groSquName;
    }

    public void setGroSquName(String groSquName) {
        this.groSquName = groSquName;
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public String getActName() {
        return actName;
    }

    public void setActName(String actName) {
        this.actName = actName;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getActImg() {
        return actImg;
    }

    public void setActImg(String actImg) {
        this.actImg = actImg;
    }

    public String getInviterId() {
        return inviterId;
    }

    public void setInviterId(String inviterId) {
        this.inviterId = inviterId;
    }

    public String getInviterName() {
        return inviterName;
    }

    public void setInviterName(String inviterName) {
        this.inviterName = inviterName;
    }

    public String getInviteeId() {
        return inviteeId;
    }

    public void setInviteeId(String inviteeId) {
        this.inviteeId = inviteeId;
    }

    public String getInviteeName() {
        return inviteeName;
    }

    public void setInviteeName(String inviteeName) {
        this.inviteeName = inviteeName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getAllowSeeInviterBaseInfo() {
        return allowSeeInviterBaseInfo;
    }

    public void setAllowSeeInviterBaseInfo(String allowSeeInviterBaseInfo) {
        this.allowSeeInviterBaseInfo = allowSeeInviterBaseInfo;
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
}
