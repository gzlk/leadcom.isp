package com.leadcom.android.isp.model.common;

import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.activity.Activity;
import com.leadcom.android.isp.model.archive.Archive;
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

    public interface Field {
        String FromUserId = "fromUserId";
        String FromUserName = "fromUserName";
        String ToUserId = "toUserId";
        String ToUserName = "toUserName";
        String MessageType = "messageType";
        String MessageContent = "messageContent";
        String SendDate = "sendDate";
        String IsRead = "isRead";
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
    @Column(Archive.Field.Title)
    private String msgTitle;// 原有属性
    //消息内容
    @Column(Field.MessageContent)
    private String msgContent;
    //发送时间
    @Column(Field.SendDate)
    private String sendDate;
    //uuid，用于查找发给多人的同一个消息
    @Column(Model.Field.UUID)
    private String uuid;
    @Column(Activity.Field.Status)
    private int status;        //状态(0.未读,1.已读,2.已处理)

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
