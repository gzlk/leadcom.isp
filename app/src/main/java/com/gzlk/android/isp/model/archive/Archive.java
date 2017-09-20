package com.gzlk.android.isp.model.archive;

import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.activity.Activity;
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

    public interface Field {
        String Type = "type";
        String Title = "title";
        String Cover = "cover";
        String Content = "content";
        String Image = "image";
        String Source = "source";
        String Location = "location";
        String UserArchiveId = "userArchiveId";
        String UserMomentId = "userMomentId";
        String GroupArchiveId = "groupArchiveId";
        String Markdown = "markdown";
        String LastModifiedDate = "lastModifiedDate";
        String CreatorId = "creatorId";
        String CreatorName = "creatorName";
        String Label = "label";
        String Collected = "collected";
        String CollectionId = "collectionId";
        String Office = "office";
        String Pdf = "pdf";
        String Video = "video";
        String Attach = "attach";
        String HappenDate = "happenDate";
        String AuthPublic = "authPublic";
        String AuthGroup = "authGroup";
        String AuthUser = "authUser";
        String ApproverId = "approverId";
        String ApproveDate = "approveDate";
        String ReadNumber = "readNumber";
        String LikeNumber = "likeNumber";
        String CommentNumber = "commentNumber";
        String CollectNumber = "collectNumber";
        String AttachmentNum = "attachmentNum";
        String PassedNum = "passedNum";
        String ArchiveDate = "archiveDate";
        String ArchiveId = "archiveId";
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
         * 普通档案（个人档案、组织档案通用）
         */
        int NORMAL = 1;
        /**
         * 组织档案（个人档案有效）
         */
        int GROUP = 2;
        /**
         * 个人档案（组织档案有效）
         */
        int INDIVIDUAL = 2;
        /**
         * 活动档案（组织档案有效）
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

    /**
     * 档案审核状态
     */
    public interface ArchiveStatus {
        /**
         * 未审核
         */
        int APPROVING = 1;
        /**
         * 已审核通过
         */
        int APPROVED = 2;
        /**
         * 审核失败（未审核通过）
         */
        int FAILURE = 3;
    }

    public String getArchiveStatus() {
        switch (status) {
            case ArchiveStatus.APPROVING:
                // 不是活动档案时为未审核，活动档案为未存档
                return type == ArchiveType.ACTIVITY ? "待存档" : "待审核";
            //return "待审核";
            case ArchiveStatus.APPROVED:
                // 不是活动档案时为已审核，活动档案为已存档
                return type == ArchiveType.ACTIVITY ? "已存档" : "已审核";
            //return "已审核";
            case ArchiveStatus.FAILURE:
                return type == ArchiveType.ACTIVITY ? "存档失败" : "审核失败";
            //return "未通过审核";
            default:
                //return type == ArchiveType.ACTIVITY ? "" : "未通过审核";
                return "未知(" + String.valueOf(status) + ")";
        }
    }

    private void getLocalAttachments() {
        if (!isLocalDeleted()) {
            setLocalDeleted(true);
            List<Attachment> list = Attachment.getAttachments(getId());
            if (null == list) {
                return;
            }
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

    /**
     * 当前登录者是否是本档案的管理员
     */
    public boolean isManager() {
        return Cache.cache().userId.equals(userId);
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
    // 标签
    @Column(Field.Label)
    private ArrayList<String> label;
    //档案封面
    @Column(Field.Cover)
    private String cover;
    //档案名称
    @Column(Field.Title)
    private String title;
    //档案简介
    @Column(Organization.Field.Introduction)
    private String intro;
    //档案内容(html)
    @Column(Field.Content)
    private String content;
    //档案内容(markdown)
    @Column(Field.Markdown)
    private String markdown;
    // Office 文档地址
    @Ignore
    private ArrayList<Attachment> office;
    // 图片地址
    @Ignore
    private ArrayList<Attachment> image;
    // 视频地址
    @Ignore
    private ArrayList<Attachment> video;
    //附件地址
    @Ignore
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
    @Column(Model.Field.CreateDate)
    private String createDate;
    //档案发生时间
    @Column(Field.HappenDate)
    private String happenDate;
    //最后一次修改时间
    @Column(Field.LastModifiedDate)
    private String lastModifiedDate;
    //档案附加信息
    @Ignore
    private Additional addition;
    //当前用户是否收藏(0.未收藏,1.已收藏)
    @Column(Field.Collected)
    private int collection;
    @Column(Field.CollectionId)
    private String colId;          //当前用户收藏该动态后的收藏ID

    // 存档相关
    @Column(Activity.Field.ActivityId)
    private String actId;              //活动ID
    @Column(Field.AttachmentNum)
    private String attachNum;          //活动档案附件总数量
    @Column(Field.PassedNum)
    private String passNum;            //通过审核的活动档案附件数量
    @Column(Field.ArchiveDate)
    private String archiveDate;        //存档时间
    @Column(Field.AttachmentNum)
    private String archiverId;         //存档人用户ID

    // 审核相关
    //存档状态(1.未存档,2.存档成功,3.存档失败)
    //1.未审核,2.审核成功,3.审核失败
    @Column(Activity.Field.Status)
    private int status;
    //档案审核人用户ID
    @Column(Field.ApproverId)
    private String approverId;
    //审核时间
    @Column(Field.ApproveDate)
    private String approveDate;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getTitle() {
        return title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 档案类型
     * <p>
     * 个人档案时：(1.普通个人档案,2.个人转到组织的档案)
     * </p>
     * <p>
     * 组织档案时：(1.普通组织档案,2.个人转到组织的档案,3.活动存档)
     * </p>
     */
    public int getType() {
        return type;
    }

    /**
     * 档案类型
     * <p>
     * 个人档案时：(1.普通个人档案,2.个人转到组织的档案)
     * </p>
     * <p>
     * 组织档案时：(1.普通组织档案,2.个人转到组织的档案,3.活动存档)
     * </p>
     */
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
        if (isEmpty(createDate)) {
            createDate = DFT_DATE;
        }
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
        if (isEmpty(happenDate)) {
            happenDate = DFT_DATE;
        }
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getApproverId() {
        return approverId;
    }

    public void setApproverId(String approverId) {
        this.approverId = approverId;
    }

    public String getApproveDate() {
        return approveDate;
    }

    public void setApproveDate(String approveDate) {
        this.approveDate = approveDate;
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public String getAttachNum() {
        return attachNum;
    }

    public void setAttachNum(String attachNum) {
        this.attachNum = attachNum;
    }

    public String getPassNum() {
        return passNum;
    }

    public void setPassNum(String passNum) {
        this.passNum = passNum;
    }

    public String getArchiveDate() {
        return archiveDate;
    }

    public void setArchiveDate(String archiveDate) {
        this.archiveDate = archiveDate;
    }

    public String getArchiverId() {
        return archiverId;
    }

    public void setArchiverId(String archiverId) {
        this.archiverId = archiverId;
    }

    /**
     * 当前用户是否收藏(0.未收藏,1.已收藏)
     */
    public int getCollection() {
        return collection;
    }

    /**
     * 当前用户是否收藏(0.未收藏,1.已收藏)
     */
    public void setCollection(int collection) {
        this.collection = collection;
    }

    public String getColId() {
        return colId;
    }

    public void setColId(String colId) {
        this.colId = colId;
    }
}
