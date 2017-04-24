package com.gzlk.android.isp.model.user.document;

import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.user.moment.Moment;
import com.litesuits.orm.db.annotation.Column;


/**
 * <b>功能描述：</b>个人档案<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/15 14:35 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/15 14:35 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class Document extends Model {

    public static class Field {
        public static final String Title = "title";
        public static final String Type = "type";
        public static final String Creator = "userId";
        public static final String CreatorName = "userName";
        public static final String LastModifiedDate = "lastModifiedDate";
    }

    /**
     * 个人档案类型
     */
    public static class Type {
        /**
         * 文本
         */
        public static final String TEXT = "1";
        /**
         * 连接引用
         */
        public static final String LINK = "2";
    }

    //档案标题
    @Column(Field.Title)
    private String title;
    //档案内容
    @Column(Moment.Field.Content)
    private String content;
    //档案类型
    @Column(Field.Type)
    private String type;
    //创建者ID
    @Column(Model.Field.UserId)
    private String userId;
    //创建者名称
    @Column(Model.Field.UserName)
    private String userName;
    //创建时间
    @Column(Model.Field.CreateDate)
    private String createDate;
    //最后修改时间
    @Column(Field.LastModifiedDate)
    private String lastModifiedDate;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
