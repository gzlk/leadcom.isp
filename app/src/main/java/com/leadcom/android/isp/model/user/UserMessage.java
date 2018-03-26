package com.leadcom.android.isp.model.user;

import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.Archive;
import com.litesuits.orm.db.annotation.Table;

/**
 * <b>功能描述：</b>个人消息<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/19 09:58 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/19 09:58 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

@Table(Archive.Table.USER_MESSAGE)
public class UserMessage extends Model {

    /**
     * 消息来源
     */
    public interface SourceType {
        /**
         * 用户个人档案
         */
        int USER_ARCHIVE = 1;
        /**
         * 组织档案
         */
        int GROUP_ARCHIVE = 2;
        /**
         * 个人动态
         */
        int MOMENT = 3;
    }

    /**
     * 消息类型
     */
    public interface Type {
        /**
         * 直接评论
         */
        int COMMENT = 1;
        /**
         * 评论别人的评论
         */
        int COMMENT_USER = 2;
        /**
         * 点赞
         */
        int LIKE = 3;
    }

    /**
     * 消息状态
     */
    public interface Status {
        /**
         * 未读
         */
        int UN_READ = 1;
        /**
         * 已读
         */
        int READ = 2;
    }

    private int sourceType;  //消息来源类型(1.个人档案,2.组织档案,3.个人动态)
    private String sourceId;    //消息来源ID(个人档案ID,组织档案ID,个人动态ID)
    private int type;        //消息类型(1.直接评论,2.对人评论,3.点赞)
    private int status;      //消息状态(1.未读,2.已读)
    private String userId;      //消息发送者的用户ID
    private String userName;    //消息发送者的用户名称
    private String headPhoto;   //消息发送者的用户头像
    private String toUserId;    //消息接收者的用户ID
    private String content;     //消息内容
    private String createDate;  //消息发送时间
    private Archive groDoc;      //组织档案
    private Archive userDoc;    //个人档案
    private Moment userMmt;    //个人动态

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public String getHeadPhoto() {
        return headPhoto;
    }

    public void setHeadPhoto(String headPhoto) {
        this.headPhoto = headPhoto;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Archive getGroDoc() {
        return groDoc;
    }

    public void setGroDoc(Archive groDoc) {
        this.groDoc = groDoc;
    }

    public Archive getUserDoc() {
        return userDoc;
    }

    public void setUserDoc(Archive userDoc) {
        this.userDoc = userDoc;
    }

    public Moment getUserMmt() {
        return userMmt;
    }

    public void setUserMmt(Moment userMmt) {
        this.userMmt = userMmt;
    }
}
