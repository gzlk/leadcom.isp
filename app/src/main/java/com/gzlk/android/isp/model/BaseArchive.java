package com.gzlk.android.isp.model;

import com.gzlk.android.isp.model.user.User;
import com.gzlk.android.isp.model.user.document.Document;
import com.gzlk.android.isp.model.user.moment.Moment;
import com.litesuits.orm.db.annotation.Column;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>档案相关基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/15 10:09 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/15 10:09 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class BaseArchive extends Model {

    public static class Field {
        public static final String Source = "source";
        public static final String GroupArchiveId = "groupArchiveId";
        public static final String Markdown = "markdown";
        public static final String Attach = "attach";
        public static final String AttachName = "attachName";
    }

    /**
     * 档案类型
     */
    public static class Type {
        /**
         * 个人
         */
        public static final int INDIVIDUAL = 0;
        /**
         * 组织、活动
         */
        public static final int ORGANIZATION = 1;
    }

    @Column(Field.Source)
    private String source;             //档案来源

    @Column(Document.Field.Type)
    private String type;               //档案类型(1.文本,2.引用链接,3.个人,4.活动)

    @Column(Document.Field.Title)
    private String title;              //档案名称

    @Column(Moment.Field.Content)
    private String content;            //档案内容(html)

    @Column(Field.Markdown)
    private String markdown;           //档案内容(markdown)

    @Column(Moment.Field.Image)
    private ArrayList<String> image;   //图片地址

    @Column(Field.Attach)
    private ArrayList<String> attach;  //附件地址

    @Column(Field.AttachName)
    private ArrayList<String> attachName;//附件名称

    @Column(Model.Field.UserId)
    private String userId;             //档案发起者ID

    @Column(Model.Field.UserName)
    private String userName;           //档案发起者姓名

    @Column(User.Field.HeadPhoto)
    private String headPhoto;          //创建者头像

    @Column(Model.Field.CreateDate)
    private String createDate;         //档案发生时间

    @Column(Document.Field.LastModifiedDate)
    private String lastModifiedDate;   //最后一次修改时间

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

    public String getHeadPhoto() {
        return headPhoto;
    }

    public void setHeadPhoto(String headPhoto) {
        this.headPhoto = headPhoto;
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

    public String getMarkdown() {
        return markdown;
    }

    public void setMarkdown(String markdown) {
        this.markdown = markdown;
    }

    public ArrayList<String> getImage() {
        return image;
    }

    public void setImage(ArrayList<String> image) {
        this.image = image;
    }

    public ArrayList<String> getAttach() {
        return attach;
    }

    public void setAttach(ArrayList<String> attach) {
        this.attach = attach;
    }

    public ArrayList<String> getAttachName() {
        return attachName;
    }

    public void setAttachName(ArrayList<String> attachName) {
        this.attachName = attachName;
    }
}
