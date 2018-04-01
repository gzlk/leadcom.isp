package com.leadcom.android.isp.model.common;

import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.activity.Activity;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.nim.model.notification.NimMessage;
import com.litesuits.orm.db.annotation.Column;

/**
 * <b>功能描述：</b>消息类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/19 21:38 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/19 21:38 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class Message extends Model {

    @Override
    public boolean equals(Object object) {
        return null != object && (getClass() == object.getClass()) && (object instanceof NimMessage) && equals((NimMessage) object);
    }

    public boolean equals(NimMessage msg) {
        return null != msg && !isEmpty(msg.getId()) && msg.getId().equals(getId());
    }

    public interface Field {
        String FromUserId = "fromUserId";
        String FromUserName = "fromUserName";
        String ToUserId = "toUserId";
        String ToUserName = "toUserName";
        String MessageType = "messageType";
        String MessageContent = "messageContent";
        String SendDate = "sendDate";
    }

    /**
     * 消息的读取状态
     */
    public interface Status {
        /**
         * 未读
         */
        int UNREAD = 0;
        /**
         * 已读
         */
        int READ = 1;
        /**
         * 已处理
         */
        int HANDLED = 2;
    }

    /**
     * 自定义消息类型
     */
    public interface Type {
        /**
         * 用户聊天短消息
         */
        int USER_CHAT = 1;
        /**
         * 活动回复消息
         */
        int ACTIVITY_REPLY = 2;
        /**
         * 事件回复消息
         */
        int EVENT_REPLY = 3;
        /**
         * 新成员申请加入组织
         */
        int GROUP_JOIN = 4;
        /**
         * 批准新成员加入
         */
        int GROUP_JOIN_APPROVE = 6;
        /**
         * 不批准新成员加入
         */
        int GROUP_JOIN_DISAPPROVE = 7;
        /**
         * 邀请新成员加入组织
         */
        int GROUP_INVITE = 8;
        /**
         * 新成员同意加入组织
         */
        int GROUP_INVITE_AGREE = 9;
        /**
         * 新成员不同意加入组织
         */
        int GROUP_INVITE_DISAGREE = 10;
        /**
         * 被踢出组织
         */
        int GROUP_KICK_OUT = 11;
        /**
         * 主动加入活动
         */
        int ACTIVITY_JOIN = 14;
        /**
         * 主动加入活动的回复
         */
        int ACTIVITY_JOIN_REPLY = 15;
        /**
         * 活动成员邀请
         */
        int ACTIVITY_INVITE = 16;
        /**
         * 活动邀请的回复
         */
        int ACTIVITY_INVITE_REPLY = 17;
        /**
         * 被踢出活动
         */
        int ACTIVITY_KICK_OUT = 18;
        /**
         * 小组成员邀请小组外人员加入小组
         */
        int SQUAD_INVITE = 19;
        /**
         * 被邀请者同意加入小组
         */
        int SQUAD_INVITE_AGREE = 20;
        /**
         * 被邀请者拒绝加入小组
         */
        int SQUAD_INVITE_DISAGREE = 21;
        /**
         * 活动通知(对所有活动成员)
         */
        int ACTIVITY_ALERT_ALL = 22;
        /**
         * 活动通知(对选定成员)
         */
        int ACTIVITY_ALERT_SELECTED = 23;
        /**
         * 邀请到小组（仅通知）
         */
        int SQUAD_INVITE_ALERT = 24;
        /**
         * 成员退出活动
         */
        int ACTIVITY_EXIT = 25;
        /**
         * 活动结束通知
         */
        int ACTIVITY_END = 26;
        /**
         * 邀请活动议题成员
         */
        int TOPIC_INVITE = 27;
        /**
         * 退出活动议题
         */
        int TOPIC_EXIT = 28;
        /**
         * 踢出活动议题成员
         */
        int TOPIC_KICK_OUT = 29;
        /**
         * 结束活动议题
         */
        int TOPIC_END = 30;
        /**
         * 个人档案点赞
         */
        int USER_ARCHIVE_LIKE = 31;
        /**
         * 个人档案评论
         */
        int USER_ARCHIVE_COMMENT = 32;
        /**
         * 组织档案点赞
         */
        int GROUP_ARCHIVE_LIKE = 33;
        /**
         * 组织档案评论
         */
        int GROUP_ARCHIVE_COMMENT = 34;
        /**
         * 个人动态点赞
         */
        int MOMENT_LIKE = 35;
        /**
         * 个人动态评论
         */
        int MOMENT_COMMENT = 36;
        /**
         * 活动中发送的通知
         */
        int ACTIVITY_NOTIFY = 37;
        /**
         * 群聊解散
         */
        int TALK_TEAM_DISMISS = 40;
        /**
         * 群聊成员加入
         */
        int TALK_TEAM_MEMBER_JOIN = 41;
        /**
         * 群聊成员退出
         */
        int TALK_TEAM_MEMBER_QUIT = 42;
        /**
         * 群聊成员踢出
         */
        int TALK_TEAM_MEMBER_REMOVE = 43;
    }

    /**
     * 获取类型文字
     */
    public static String getMsgType(int type) {
        switch (type) {
            case Type.GROUP_JOIN:
                return "申请加入组织";
            case Type.GROUP_INVITE:
                return "邀请您加入组织";
            case Type.ACTIVITY_INVITE:
                return "邀请您加入活动";
            case Type.ACTIVITY_END:
                return "活动结束";
            case Type.ACTIVITY_EXIT:
                return "退出活动";
            case Type.ACTIVITY_KICK_OUT:
                return "踢出活动";
            case Type.SQUAD_INVITE:
                return "邀请您加入小组";
            case Type.ACTIVITY_ALERT_ALL:
                return "活动通知";
            case Type.ACTIVITY_ALERT_SELECTED:
                return "系统通知";
            case Type.SQUAD_INVITE_ALERT:
                return "加入小组";
            case Type.TOPIC_INVITE:
                return "邀请您加入议题";
            case Type.TOPIC_EXIT:
                return "退出议题";
            case Type.TOPIC_KICK_OUT:
                return "踢出议题";
            case Type.TOPIC_END:
                return "结束议题";
            case Type.ACTIVITY_NOTIFY:
                return "活动通知";
            case Type.TALK_TEAM_DISMISS:
                return "群聊解散";
            case Type.TALK_TEAM_MEMBER_JOIN:
                return "加入群聊";
            case Type.TALK_TEAM_MEMBER_QUIT:
                return "退出群聊";
            case Type.TALK_TEAM_MEMBER_REMOVE:
                return "被移出群聊";
            default:
                return StringHelper.format("不晓得是什么通知(%d)", type);
        }
    }

    // 推送类型(0.点对点自定义推送,1.群消息自定义推送)
    @Column(Archive.Field.Type)
    private int type;// 原有属性
    //消息来源
    @Column(Field.FromUserId)
    private String fromUserId;// 原有属性
    @Column(Field.FromUserName)
    private String fromUserName;// 原有属性
    //消息目的
    @Column(Field.ToUserId)
    private String toUserId;// 原有属性
    @Column(Field.ToUserName)
    private String toUserName;// 原有属性
    //消息类型 1.用户聊天短消息 2.发起的活动变更消息  3.参与的活动变更消息   4.发起的群体变更消息  5.参与的群体变更消息 6.新成员入群申请消息（消息常量在Constant中定义）
    @Column(Field.MessageType)
    private int msgType;// 原有属性
    @Column(Archive.Field.Title)
    private String msgTitle;// 原有属性
    //消息内容
    @Column(Field.MessageContent)
    private String msgContent;// 原有属性
    //发送时间
    @Column(Field.SendDate)
    private String sendDate;// 原有属性
    //uuid，用于查找发给多人的同一个消息
    @Column(Model.Field.UUID)
    private String uuid;// 原有属性
    @Column(Activity.Field.Status)
    private int status;        //状态(0.未读,1.已读,2.已处理)

    public int getType() {
        return type;
    }

    /**
     * 查看本条消息是否可以保存
     */
    public boolean isSavable() {
        return msgType > 0 && (msgType < Type.USER_ARCHIVE_LIKE || getMsgType() > Type.MOMENT_COMMENT);
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public int getMsgType() {
        if (1 > msgType) {
            return type;
        }
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
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

    public String getSendDate() {
        return sendDate;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isRead() {
        return status >= Status.READ;
    }

    public boolean isHandled() {
        return status == Status.HANDLED;
    }
}
