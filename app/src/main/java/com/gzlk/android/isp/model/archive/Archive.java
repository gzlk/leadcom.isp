package com.gzlk.android.isp.model.archive;

import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.common.Attachment;
import com.gzlk.android.isp.model.organization.Organization;
import com.gzlk.android.isp.model.user.User;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能描述：</b>档案基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/15 10:09 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/15 10:09 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(Archive.Table.ARCHIVE)
public class Archive extends Additional {

    /**
     * 档案相关的表
     */
    public static class Table {
        /**
         * 档案
         */
        public static final String ARCHIVE = "archive";
        /**
         * 用户档案
         */
        public static final String USER_ARCHIVE = "userArchive";
        /**
         * 组织档案
         */
        public static final String GROUP_ARCHIVE = "groupArchive";
        /**
         * 用户说说
         */
        public static final String USER_MOMENT = "userMoment";
        /**
         * 评论
         */
        public static final String COMMENT = "archiveComment";
        /**
         * 赞
         */
        public static final String LIKE = "archiveLike";
    }

    public static class Field {
        public static final String Type = "type";
        public static final String Title = "title";
        public static final String Content = "content";
        public static final String Image = "image";
        public static final String Source = "source";
        public static final String Location = "location";
        public static final String UserArchiveId = "userArchiveId";
        public static final String UserMomentId = "userMomentId";
        public static final String GroupArchiveId = "groupArchiveId";
        public static final String Markdown = "markdown";
        public static final String LastModifiedDate = "lastModifiedDate";
        public static final String CreatorId = "creatorId";
        public static final String CreatorName = "creatorName";
        public static final String Label = "label";
        public static final String Office = "office";
        public static final String Pdf = "pdf";
        public static final String Video = "video";
        public static final String HappenDate = "happenDate";
        public static final String AuthPublic = "authPublic";
        public static final String AuthGroup = "authGroup";
        public static final String AuthUser = "authUser";
        public static final String Attach = "attach";
        public static final String AttachName = "attachName";
        public static final String ReadNumber = "readNumber";
        public static final String LikeNumber = "likeNumber";
        public static final String CommentNumber = "commentNumber";
        public static final String CollectNumber = "collectNumber";
    }

    /**
     * 档案类型
     */
    public static class Type {
        /**
         * 个人
         */
        public static final int USER = 0;
        /**
         * 组织、活动
         */
        public static final int GROUP = 1;
    }

    /**
     * 档案性质(1.普通,2.个人,3.活动)
     */
    public interface ArchiveType {
        /**
         * 普通档案
         */
        int NORMAL = 1;
        /**
         * 个人档案
         */
        int INDIVIDUAL = 2;
        /**
         * 活动档案
         */
        int ACTIVITY = 3;
    }

    /**
     * 档案内容类型
     */
    public interface ArchiveContentType {
        /**
         * 文本
         */
        int TEXT = 1;
        /**
         * 连接引用
         */
        int LINK = 2;
        /**
         * 个人
         */
        int INDIVIDUAL = 3;
        /**
         * 活动
         */
        int ACTIVITY = 4;
    }

    private void getLocalAttachments() {
        if (!isLocalDeleted()) {
            setLocalDeleted(true);
            Dao<Attachment> dao = new Dao<>(Attachment.class);
            QueryBuilder<Attachment> builder = new QueryBuilder<>(Attachment.class)
                    .whereEquals(Attachment.Field.ArchiveId, getId());
            List<Attachment> list = dao.query(builder);
            for (Attachment attachment : list) {
                if (attachment.isOffice()) {
                    if (null == office) {
                        office = new ArrayList<>();
                    }
                    office.add(attachment);
                } else if (attachment.isImage()) {
                    if (null == image) {
                        image = new ArrayList<>();
                    }
                    image.add(attachment);
                } else if (attachment.isVideo()) {
                    if (null == video) {
                        video = new ArrayList<>();
                    }
                    video.add(attachment);
                } else {
                    if (null == attach) {
                        attach = new ArrayList<>();
                    }
                    attach.add(attachment);
                }
            }
        }
    }

    @Column(Organization.Field.GroupId)
    private String groupId;            //群ID

    /**
     * 档案类型
     * <p>
     * 个人档案时：(1.普通个人档案,2.个人转到组织的档案)
     * </p>
     * <p>
     * 组织档案时：(1.普通组织档案,2.个人转到组织的档案,3.活动存档)
     * </p>
     */
    @Column(Field.Type)
    private int type;
    //档案名称
    @Column(Field.Title)
    private String title;
    //档案内容(html)
    @Column(Field.Content)
    private String content;
    //档案内容(markdown)
    @Column(Field.Markdown)
    private String markdown;
    // 标签
    @Column(Field.Label)
    private ArrayList<String> label;
    // Office 文档地址
    @Column(Field.Office)
    private ArrayList<Attachment> office;
    // 图片地址
    @Column(Field.Image)
    private ArrayList<Attachment> image;
    // 视频地址
    @Column(Field.Video)
    private ArrayList<Attachment> video;
    //附件地址
    @Column(Field.Attach)
    private ArrayList<Attachment> attach;
    //档案发起者ID
    @Column(Model.Field.UserId)
    private String userId;
    //档案发起者姓名
    @Column(Model.Field.UserName)
    private String userName;
    //创建者头像
    @Column(User.Field.HeadPhoto)
    private String headPhoto;
    //档案发生时间
    @Column(Model.Field.CreateDate)
    private String createDate;
    //最后一次修改时间
    @Column(Field.LastModifiedDate)
    private String lastModifiedDate;
    //授权公开(0.私密，自己可以看,1.公开，所有人都能查看)，个人档案的属性
    @Column(Field.AuthPublic)
    private int authPublic;
    //授权组织(组织ID Json数组)，授权的组织才能查看
    @Column(Field.AuthGroup)
    private ArrayList<String> authGro;
    //授权个人(用户ID Json数组)，授权的用户才能查看
    @Column(Field.AuthUser)
    private ArrayList<String> authUser;
    //档案发生时间
    @Column(Field.HappenDate)
    private String happenDate;
    //档案附加信息
    @Ignore
    private Additional addition;

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
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

    public Additional getAddition() {
        return addition;
    }

    public void setAddition(Additional addition) {
        this.addition = addition;
        // 重置附加信息
        resetAdditional(this.addition);
    }

    public ArrayList<String> getLabel() {
        return label;
    }

    public void setLabel(ArrayList<String> label) {
        this.label = label;
    }

    public ArrayList<Attachment> getOffice() {
        if (null == office) {
            office = new ArrayList<>();
            getLocalAttachments();
        }
        return office;
    }

    public void setOffice(ArrayList<Attachment> office) {
        this.office = office;
    }

    public ArrayList<Attachment> getImage() {
        if (null == image) {
            image = new ArrayList<>();
            getLocalAttachments();
        }
        return image;
    }

    public void setImage(ArrayList<Attachment> image) {
        this.image = image;
    }

    public ArrayList<Attachment> getVideo() {
        if (null == video) {
            video = new ArrayList<>();
            getLocalAttachments();
        }
        return video;
    }

    public void setVideo(ArrayList<Attachment> video) {
        this.video = video;
    }

    public ArrayList<Attachment> getAttach() {
        if (null == attach) {
            attach = new ArrayList<>();
            getLocalAttachments();
        }
        return attach;
    }

    public void setAttach(ArrayList<Attachment> attach) {
        this.attach = attach;
    }

    public String getHappenDate() {
        return happenDate;
    }

    public void setHappenDate(String happenDate) {
        this.happenDate = happenDate;
    }

    public int getAuthPublic() {
        return authPublic;
    }

    public void setAuthPublic(int authPublic) {
        this.authPublic = authPublic;
    }

    public ArrayList<String> getAuthGro() {
        return authGro;
    }

    public void setAuthGro(ArrayList<String> authGro) {
        this.authGro = authGro;
    }

    public ArrayList<String> getAuthUser() {
        return authUser;
    }

    public void setAuthUser(ArrayList<String> authUser) {
        this.authUser = authUser;
    }
}
