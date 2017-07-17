package com.gzlk.android.isp.nim.model.notification;

import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.activity.Activity;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.organization.Organization;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;

/**
 * <b>功能描述：</b>自定义网易云信消息<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/18 23:18 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/18 23:18 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

@Table(NimMessage.PARAM.TABLE)
public class NimMessage implements MsgAttachment {

    public interface PARAM {
        String TABLE = "notification";
        String HANDLED = "handled";
        String HANDLE_STATE = "handleState";
    }

    @Override
    public boolean equals(Object object) {
        return null != object && (getClass() == object.getClass()) && (object instanceof NimMessage) && equals((NimMessage) object);
    }

    public boolean equals(NimMessage msg) {
        return null != msg && msg.getId() == getId();
    }

    /**
     * 自定义消息类型
     */
    public interface Type {
        /**
         * 新成员申请加入组织
         */
        int JOIN_TO_GROUP = 4;
        /**
         * 批准新成员加入
         */
        int APPROVE_JOIN_GROUP = 6;
        /**
         * 不批准新成员加入
         */
        int DISAPPROVE_JOIN_GROUP = 7;
        /**
         * 邀请新成员加入组织
         */
        int INVITE_TO_GROUP = 8;
        /**
         * 新成员同意加入组织
         */
        int AGREE_TO_GROUP = 9;
        /**
         * 新成员不同意加入组织
         */
        int DISAGREE_TO_GROUP = 10;
        /**
         * 活动成员邀请
         */
        int ACTIVITY_INVITE = 16;
        /**
         * 小组成员邀请小组外人员加入小组
         */
        int INVITE_TO_SQUAD = 19;
        /**
         * 被邀请者同意加入小组
         */
        int AGREE_TO_SQUAD = 20;
        /**
         * 被邀请者拒绝加入小组
         */
        int DISAGREE_TO_SQUAD = 21;
        /**
         * 活动通知
         */
        int ACTIVITY_NOTIFICATION = 22;
        /**
         * 自定义系统通知
         */
        int SYSTEM_NOTIFICATION = 23;
    }

    /**
     * 获取类型文字
     */
    public static String getType(int type) {
        switch (type) {
            case Type.JOIN_TO_GROUP:
                return "申请加入组织";
            case Type.INVITE_TO_GROUP:
                return "邀请您加入组织";
            case Type.ACTIVITY_INVITE:
                return "邀请您加入活动";
            case Type.INVITE_TO_SQUAD:
                return "邀请您加入小组";
            case Type.ACTIVITY_NOTIFICATION:
                return "活动通知";
            case Type.SYSTEM_NOTIFICATION:
                return "系统通知";
            default:
                return StringHelper.format("不晓得什么通知(%d)", type);
        }
    }

    @Column(Model.Field.Id)
    private long id = Utils.timestamp();
    // 自定义消息类型
    @Column(Archive.Field.Type)
    private int type;// 原有属性
    // 自定义消息标题
    @Column(Archive.Field.Title)
    private String msgTitle;// 原有属性
    // 自定义消息内容
    @Column(Archive.Field.Content)
    private String msgContent;// 原有属性
    // 自定义消息id
    @Column(Model.Field.UUID)
    private String uuid;// 原有属性
    // 活动的tid
    @Column(Activity.Field.NimId)
    private String tid;// 原有属性
    // 活动所属的组织
    @Column(Organization.Field.GroupId)
    private String groupId;// 原有属性
    // 是否已处理
    @Column(PARAM.HANDLED)
    private boolean handled;
    // 处理状态，true=已处理，false=已拒绝
    @Column(PARAM.HANDLE_STATE)
    private boolean handleState;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMsgTitle() {
        return msgTitle;
    }

    public void setMsgTitle(String msgTitle) {
        this.msgTitle = msgTitle;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public boolean isHandled() {
        return handled;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }

    public boolean isHandleState() {
        return handleState;
    }

    public void setHandleState(boolean handleState) {
        this.handleState = handleState;
    }

    @Override
    public String toJson(boolean b) {
        return NimMessageParser.packData(this);
    }
}
