package com.gzlk.android.isp.model.archive;

import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.user.User;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.Table;

/**
 * <b>功能描述：</b>评论<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/22 23:38 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/22 23:38 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

@Table(Archive.Table.COMMENT)
public class Comment extends ArchiveInfo {

    /**
     * 评论类别
     */
    public static class Type {
        /**
         * 用户档案评论
         */
        public static final int USER = 1;
        /**
         * 组织档案评论
         */
        public static final int GROUP = 2;
        /**
         * 用户说说评论
         */
        public static final int MOMENT = 3;
    }

    @Column(Archive.Field.Content)
    private String content;     //组织档案内容
    @Column(Model.Field.UserId)
    private String userId;      //评论人ID
    @Column(Model.Field.UserName)
    private String userName;    //评论人名称
    @Column(User.Field.HeadPhoto)
    private String headPhoto;
    @Column(Model.Field.CreateDate)
    private String createDate;  //评论时间
    @Column(Archive.Field.ToUserId)
    private String toUserId;
    @Column(Archive.Field.ToUserName)
    private String toUserName;
    @Column(Archive.Field.ToHeadPhoto)
    private String toHeadPhoto;
    @Ignore
    private boolean isLast;

    public boolean isMine() {
        return !isEmpty(userId) && userId.equals(Cache.cache().userId);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getCreateDate() {
        if (isEmpty(createDate)) {
            createDate = DFT_DATE;
        }
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
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

    public String getToHeadPhoto() {
        return toHeadPhoto;
    }

    public void setToHeadPhoto(String toHeadPhoto) {
        this.toHeadPhoto = toHeadPhoto;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }
}
