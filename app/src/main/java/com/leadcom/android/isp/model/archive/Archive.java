package com.leadcom.android.isp.model.archive;

import com.google.gson.reflect.TypeToken;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.activity.Activity;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.organization.Organization;
import com.leadcom.android.isp.model.user.User;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.Table;

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
    public interface Table {
        /**
         * 档案
         */
        String ARCHIVE = "archive";
        /**
         * 用户档案
         */
        String USER_ARCHIVE = "userArchive";
        /**
         * 组织档案
         */
        String GROUP_ARCHIVE = "groupArchive";
        /**
         * 用户说说
         */
        String USER_MOMENT = "userMoment";
        /**
         * 用户消息
         */
        String USER_MESSAGE = "userMsg";
        /**
         * 评论
         */
        String COMMENT = "archiveComment";
        /**
         * 赞
         */
        String LIKE = "archiveLike";
        /**
         * 组织的公开档案
         */
        String PUBLIC_ARCHIVE = "publicArchive";
        /**
         * 推荐档案
         */
        String RECOMMEND_ARCHIVE = "recommendArchive";
        /**
         * 草稿
         */
        String ARCHIVE_DRAFT = "archiveDraft";
        /**
         * 档案类型
         */
        String ARCHIVE_CATEGORY = "archiveCategory";
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
        String AuthUserName = "authUserName";
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
        String ReferrerId = "referrerId";
        String ReferrerName = "referrerName";
        String ReferrerHeadPhoto = "referrerHeadPhoto";
        String Recommend = "recommend";
        String RecommendId = "recommendId";
        String Check = "checked";
        String Sort = "sortNumber";
        String Handle = "handleStatus";
        String LikeId = "likeId";
        String Liked = "liked";
        String ToUserId = "toUserId";
        String ToUserName = "toUserName";
        String ToHeadPhoto = "toHeadPhoto";
        String DraftJson = "draftJson";
        String Property = "property";
        String Category = "category";
        String Participant = "participant";
        String TypeName = "typeName";
        String Code = "code";
        String Description = "description";
        String ParentId = "parentId";
        String TypeCode = "typeCode";
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
     * 点赞状态
     */
    public interface LikeType {
        /**
         * 未赞
         */
        int UN_LIKE = 0;
        /**
         * 已赞
         */
        int LIKED = 1;
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
    public interface ArchiveApproveStatus {
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

    /**
     * 草稿类型
     */
    public interface DraftType {
        /**
         * 正常档案
         */
        int NORMAL = 1;
        /**
         * 草稿档案
         */
        int DRAFT = 2;
    }

    /**
     * 获取临时草稿id
     */
    public static String getDraftId() {
        return format("draft_%d", Utils.timestamp());
    }

    public static Archive fromJson(String json) {
        if (isEmpty(json)) {
            return new Archive();
        }
        return Json.gson().fromJson(json, new TypeToken<Archive>() {
        }.getType());
    }

    public static String toJson(Archive archive) {
        if (null == archive) return "{}";
        return Json.gson().toJson(archive, new TypeToken<Archive>() {
        }.getType());
    }

    /**
     * 获取档案的审核状态
     */
    public String getArchiveApproveStatus() {
        switch (status) {
            case ArchiveApproveStatus.APPROVING:
                // 不是活动档案时为未审核，活动档案为未存档
                return type == ArchiveType.ACTIVITY ? "待存档" : "待审核";
            //return "待审核";
            case ArchiveApproveStatus.APPROVED:
                // 不是活动档案时为已审核，活动档案为已存档
                return type == ArchiveType.ACTIVITY ? "已存档" : "已审核";
            //return "已审核";
            case ArchiveApproveStatus.FAILURE:
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
                    if (!office.contains(attachment)) {
                        office.add(attachment);
                    }
                } else if (attachment.isImage()) {
                    if (null == image) {
                        image = new ArrayList<>();
                    }
                    if (!image.contains(attachment)) {
                        image.add(attachment);
                    }
                } else if (attachment.isVideo()) {
                    if (null == video) {
                        video = new ArrayList<>();
                    }
                    if (!video.contains(attachment)) {
                        video.add(attachment);
                    }
                } else {
                    if (null == attach) {
                        attach = new ArrayList<>();
                    }
                    if (!attach.contains(attachment)) {
                        attach.add(attachment);
                    }
                }
            }
        }
    }

    public static Archive get(String archiveId) {
        return new Dao<>(Archive.class).query(archiveId);
    }

    /**
     * 当前登录者是否是本档案的管理员
     */
    public boolean isAuthor() {
        return Cache.cache().userId.equals(userId);
    }

    /**
     * 是否是附件类型的档案
     */
    public boolean isAttachmentArchive() {
        return image.size() > 0 || video.size() > 0 || office.size() > 0 || attach.size() > 0;
    }

    @Column(Organization.Field.GroupId)
    private String groupId;            //群ID

    //状态:1.正式,2.草稿
    @Column(Activity.Field.Status)
    private int status;

    /**
     * 档案是否是草稿
     */
    public boolean isDraft() {
        return status == DraftType.DRAFT;
    }

    /**
     * 档案类型
     * <p>
     * 个人档案时：(1.普通个人档案,2.个人转到组织的档案,3.活动)
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
    @Column(Field.AuthUserName)
    private ArrayList<String> authUserName;
    //档案发生时间
    @Column(Model.Field.CreateDate)
    private String createDate;
    //档案发生时间
    @Column(Field.HappenDate)
    private String happenDate;
    //最后一次修改时间
    @Column(Field.LastModifiedDate)
    private String lastModifiedDate;

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
    //档案审核人用户ID
    @Column(Field.ApproverId)
    private String approverId;
    //审核时间
    @Column(Field.ApproveDate)
    private String approveDate;
    @Ignore
    private Organization groEntity;
    @Column(Field.Source)
    private String source;
    // 档案发生地点
    @Column(Activity.Field.Site)
    private String site;
    // 档案性质
    @Column(Field.Property)
    private String property;
    // 档案类型
    @Column(Field.Category)
    private String category;
    // 档案参与人
    @Column(Field.Participant)
    private String participant;

    // 当前组织是否推荐：0.未推荐，1.已推荐
    @Column(Field.Recommend)
    private int recommend;
    // 当前组织推荐该档案后的组织档案推荐ID
    @Column(Field.RecommendId)
    private String rcmdId;

    /**
     * 档案是否已经推荐到首页
     */
    public boolean isRecommend() {
        return recommend == RecommendArchive.RecommendStatus.RECOMMENDED;
    }

    @Override
    public void resetAdditional(Additional additional) {
        super.resetAdditional(additional);
        if (null != additional) {
            additional.setLike(getLike());
            additional.setLikeId(getLikeId());
            additional.setColId(getColId());
            additional.setCollection(getCollection());
        }
    }

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

    public String getSharableSummary() {
        if (isEmpty(content)) {
            return StringHelper.getString(R.string.ui_base_share_text_share_summary_blank);
        } else {
            String noHtml = Utils.clearHtml(content);
            return noHtml.length() > 100 ? noHtml.substring(0, 100) : noHtml;
        }
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

    public ArrayList<String> getLabel() {
        if (null == this.label) {
            this.label = new ArrayList<>();
        }
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

    /**
     * 是否可以推荐(档案内容是否符合要求)
     */
    public boolean isRecommendable() {
        return getHtmlClearedLength() >= 70 ||
                Utils.hasImage(content) ||
                Utils.hasVideo(content) ||
                getImage().size() > 0 ||
                getVideo().size() > 0;
    }

    public int getHtmlClearedLength() {
        String html = Utils.clearHtml(content);
        if (isEmpty(html)) return 0;
        return html.length();
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

    public ArrayList<String> getAuthUserName() {
        return authUserName;
    }

    public void setAuthUserName(ArrayList<String> authUserName) {
        this.authUserName = authUserName;
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

    public Organization getGroEntity() {
        return groEntity;
    }

    public void setGroEntity(Organization groEntity) {
        this.groEntity = groEntity;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getParticipant() {
        return participant;
    }

    public void setParticipant(String participant) {
        this.participant = participant;
    }

    public int getRecommend() {
        return recommend;
    }

    public void setRecommend(int recommend) {
        this.recommend = recommend;
    }

    public String getRcmdId() {
        return rcmdId;
    }

    public void setRcmdId(String rcmdId) {
        this.rcmdId = rcmdId;
    }
}
