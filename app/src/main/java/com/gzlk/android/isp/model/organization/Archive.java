package com.gzlk.android.isp.model.organization;

import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.user.document.Document;
import com.gzlk.android.isp.model.user.moment.Moment;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

/**
 * <b>功能描述：</b>组织内档案<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/07 09:51 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/07 09:51 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

@Table(Archive.Table.ARCHIVE)
public class Archive extends Model {

    public static class Table {
        public static final String ARCHIVE = "archive";
        public static final String ADDITIONAL = "archiveAdditional";
        public static final String COMMENT = "archiveComment";
        public static final String LIKE = "archiveLike";
    }

    public static class Field {
        public static final String Source = "source";
        public static final String GroupArchiveId = "groupArchiveId";
    }

    @Column(Organization.Field.GroupId)
    private String groupId;            //群体ID
    @Column(Document.Field.Title)
    private String title;              //档案名称
    @Column(Moment.Field.Content)
    private String content;            //档案内容
    @Column(Document.Field.Type)
    private String type;               //档案类型(1.文本,2.引用链接,3.个人,4.活动)
    @Column(Field.Source)
    private String source;             //档案来源
    @Column(Model.Field.UserId)
    private String userId;               //档案发起者ID
    @Column(Model.Field.UserName)
    private String userName;           //档案发起者姓名
    @Column(Model.Field.CreateDate)
    private String createDate;           //档案发生时间
    @Column(Document.Field.LastModifiedDate)
    private String lastModifiedDate;   //最后一次修改时间

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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
