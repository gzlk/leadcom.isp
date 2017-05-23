package com.gzlk.android.isp.model;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

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
@Table("message")
public class Message extends Model {

    public static class Field {
        public static final String FromUserId = "fromUserId";
        public static final String FromUserName = "fromUserName";
        public static final String ToUserId = "toUserId";
        public static final String ToUserName = "toUserName";
        public static final String MessageType = "messageType";
        public static final String MessageContent = "messageContent";
        public static final String SendDate = "sendDate";
        public static final String IsRead = "isRead";
    }

    //消息来源
    @Column(Field.FromUserId)
    private String fromUserId;
    @Column(Field.FromUserName)
    private String fromUserName;
    //消息目的
    @Column(Field.ToUserId)
    private String toUserId;
    @Column(Field.ToUserName)
    private String toUserName;
    //消息类型 1.用户聊天短消息 2.发起的活动变更消息  3.参与的活动变更消息   4.发起的群体变更消息  5.参与的群体变更消息 6.新成员入群申请消息（消息常量在Constant中定义）
    @Column(Field.MessageType)
    private int msgType;
    //消息内容
    @Column(Field.MessageContent)
    private String msgContent;
    //发送时间
    @Column(Field.SendDate)
    private String sendDate;
    //是否已读
    @Column(Field.IsRead)
    private String isRead;
    //uuid，用于查找发给多人的同一个消息
    @Column(Model.Field.UUID)
    private String uuid;

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
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
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

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
