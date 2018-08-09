package com.leadcom.android.isp.model.archive;

import com.google.gson.reflect.TypeToken;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.helper.LogHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.activity.Activity;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.common.Seclusion;
import com.leadcom.android.isp.model.organization.Organization;
import com.leadcom.android.isp.model.user.User;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
         * 封面
         */
        String Cover = "coverTemplate";
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
        String AbstractContent = "abstractContent";
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
        String ReadNumber = "readNumber";
        String LikeNumber = "likeNumber";
        String CommentNumber = "commentNumber";
        String CollectNumber = "collectNumber";
        String ArchiveId = "archiveId";
        String ArchiveType = "archiveType";
        String OwnType = "ownType";
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
        String Property = "property";
        String Category = "category";
        String Participant = "participant";
        String TypeName = "typeName";
        String Code = "code";
        String Description = "description";
        String ParentId = "parentId";
        String TypeCode = "typeCode";
        String Topic = "topic";
        String Resolution = "resolution";
        String Branch = "branch";
        String ShowImage = "showImage";
        String Watermark = "watermark";
    }

    /**
     * 档案类型
     */
    public interface Type {
        /**
         * 个人
         */
        int USER = 2;
        /**
         * 组织、活动
         */
        int GROUP = 1;
        /**
         * 自定义的草稿
         */
        int DRAFT = 4;
        /**
         * 组织和个人都有
         */
        int ALL = 3;
    }

    /**
     * 档案性质(1.普通,2.个人,3.活动)
     */
    public interface ArchiveType {
        /**
         * 图文
         */
        int MULTIMEDIA = 1;
        /**
         * 附件档案
         */
        int ATTACHMENT = 2;
        /**
         * 模板档案
         */
        int TEMPLATE = 3;
        /**
         * 意见和建议
         */
        int SUGGEST = 4;
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
     * 获奖情况
     */
    public interface AwardType {
        /**
         * 获奖作品
         */
        int AWARDED = 1;
        /**
         * 未获奖
         */
        int NONE = 0;
    }

    /**
     * 档案推荐状态
     */
    public interface RecommendType {
        /**
         * 未推荐
         */
        int UN_RECOMMEND = 0;
        /**
         * 已推荐
         */
        int RECOMMENDED = 1;
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
        if (isEmpty(json) || json.equals(EMPTY_JSON)) {
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
        //switch (status) {
        //case ArchiveApproveStatus.APPROVING:
        // 不是活动档案时为未审核，活动档案为未存档
        //return type == ArchiveType.ACTIVITY ? "待存档" : "待审核";
        //return "待审核";
        //case ArchiveApproveStatus.APPROVED:
        // 不是活动档案时为已审核，活动档案为已存档
        //return type == ArchiveType.ACTIVITY ? "已存档" : "已审核";
        //return "已审核";
        //case ArchiveApproveStatus.FAILURE:
        //return type == ArchiveType.ACTIVITY ? "存档失败" : "审核失败";
        //return "未通过审核";
        //default:
        //return type == ArchiveType.ACTIVITY ? "" : "未通过审核";
        return "未知";
        //}
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

    // 档案基本信息 **********************************************************************************
    //档案封面
    @Column(Field.Cover)
    private String cover;
    //档案名称
    @Column(Field.Title)
    private String title;
    //档案内容(html)
    @Column(Field.Content)
    private String content;
    @Column(Field.AbstractContent)
    private String abstrContent;       //档案摘要(html)
    @Column(Field.Source)
    private String source;
    //档案发生时间
    @Column(Field.HappenDate)
    private String happenDate;
    // 档案发生地点
    @Column(Activity.Field.Site)
    private String site;
    // 档案参与人
    @Column(Field.Participant)
    private String participant;
    //授权公开(0.私密，自己可以看,1.公开，所有人都能查看)，个人档案的属性
    @Column(Field.AuthPublic)
    private int authPublic;
    //档案类型：1图文2附件3模板档案
    @Column(Field.ArchiveType)
    private int docType;
    // 档案归属类型：1组织档案，2个人档案
    @Column(Field.OwnType)
    private int ownType;
    @Column(Field.ArchiveId)
    private String docId;
    // 标签
    @Column(Field.Label)
    private ArrayList<String> label;
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

    // 创建者相关信息 ********************************************************************************

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

    // 组织档案相关 *********************************************************************************
    // 组织ID
    @Column(Organization.Field.GroupId)
    private String groupId;
    // 组织名称
    @Column(Organization.Field.GroupName)
    private String groupName;
    // 议题
    @Column(Field.Topic)
    private String topic;      //AppContants.docType.MB
    // 纪要
    @Column(Field.Resolution)
    private String resolution; //AppContants.docType.MB
    // 所属分支
    @Column(Field.Branch)
    private String branch;
    // 档案属性
    @Column(Field.Property)
    private String property;
    // 档案分类ID
    private String docClassifyId;
    // 档案类型
    @Column(Field.Category)
    private String category;

    // 是否获奖档案
    private int awardable;

    // 排序
    @Column(Field.Sort)
    private int sort;
    // 首页焦点图
    @Column(Field.ShowImage)
    private String showImage;
    // 首页焦点图"考试"的url地址
    private String h5;
    // 水印
    @Column(Field.Watermark)
    private String watermark;

    // 分享到的用户id列表
    @Column(Field.ToUserId)
    private ArrayList<String> shareUserIds;

    private String fromGroupId;
    private String fromGroupName;
    private ArrayList<Suggest> suggest;

    public boolean isPublic() {
        return authPublic == Seclusion.Type.Public;
    }

    public int getDocType() {
        return docType;
    }

    public void setDocType(int docType) {
        this.docType = docType;
    }

    /**
     * 是否图文模板档案
     */
    public boolean isMultimediaArchive() {
        return docType <= ArchiveType.MULTIMEDIA;
    }

    /**
     * 是否附件模板档案
     */
    public boolean isAttachmentArchive() {
        return docType == ArchiveType.ATTACHMENT;
    }

    /**
     * 是否活动模板档案
     */
    public boolean isTemplateArchive() {
        return docType == ArchiveType.TEMPLATE;
    }

    public int getOwnType() {
        return ownType;
    }

    public void setOwnType(int ownType) {
        this.ownType = ownType;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getShowImage() {
        return showImage;
    }

    public void setShowImage(String showImage) {
        this.showImage = showImage;
    }

    public String getH5() {
        return h5;
    }

    public void setH5(String h5) {
        this.h5 = h5;
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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
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

    public String getContent() {
        if (!isEmpty(content) && content.contains("pre")) {
            resetPreToDiv();
        }
        return content;
    }

    /**
     * 检测内容是否是从其他平台复制过来的内容
     * src=(?:(?:'([^']*)')|(?:"([^"]*)")|([^\s]*))
     */
    public boolean isContentPasteFromOtherPlatform() {
        if (isEmpty(content)) return false;
        Matcher matcher = Pattern.compile("<img[^>]*?(/>|></img>|>)", Pattern.CASE_INSENSITIVE).matcher(content);
        while (matcher.find()) {
            String image = matcher.group(0);
            Matcher srcMatcher = Pattern.compile("src=(?:(?:'([^']*)')|(?:\"([^\"]*)\")|([^\\s]*))", Pattern.CASE_INSENSITIVE).matcher(image);
            if (srcMatcher.find()) {
                String src = srcMatcher.group(0);
                if (!isLocalServerUrl(src)) {
                    // 如果不是树脉自己文件服务器上的连接，则说明是从别的地方粘贴过来的
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 删除粘贴过来的内容里的图片标记
     */
    public void clearPastedContentImages() {
        if (!isEmpty(content)) {
            Matcher matcher = Pattern.compile("<img[^>]*?(/>|></img>|>)", Pattern.CASE_INSENSITIVE).matcher(content);
            while (matcher.find()) {
                String image = matcher.group(0);
                Matcher srcMatcher = Pattern.compile("src=(?:(?:'([^']*)')|(?:\"([^\"]*)\")|([^\\s]*))", Pattern.CASE_INSENSITIVE).matcher(image);
                if (srcMatcher.find()) {
                    String src = srcMatcher.group(0);
                    LogHelper.log("archive src", src);
                    if (!isLocalServerUrl(src)) {
                        // 如果不是树脉自己文件服务器上的连接，则说明是从别的地方粘贴过来的，将img标签重置为空
                        content = content.replace(image, "");
                    }
                }
            }
        }
    }

    public void resetImageStyle() {
        if (!isEmpty(content)) {
            Matcher matcher = Pattern.compile("<img[^>]*?(/>|></img>|>)", Pattern.CASE_INSENSITIVE).matcher(content);
            while (matcher.find()) {
                String image = matcher.group(0);
                if (!image.contains("style=\"width: 100%;\"")) {
                    content = content.replace(image, image.replace("<img", "<img style=\"width: 100%;\""));
                }
            }
        }
    }

    /**
     * 重置pre为div
     */
    private void resetPreToDiv() {
        if (!isEmpty(content)) {
            Matcher matcher = Pattern.compile("<pre[^>]*?>[\\s\\S]*?</pre>", Pattern.CASE_INSENSITIVE).matcher(content);
            while (matcher.find()) {
                String pre = matcher.group(0);
                content = content.replace(pre, pre.replace("pre", "div"));
            }
        }
    }

    /**
     * 是否是树脉文件服务器上的链接
     * ((http[s]{0,1})|ftp)://(120.25.124.199|image.py17w.net)
     */
    private boolean isLocalServerUrl(String url) {
        return Pattern.compile("(http[s]{0,1})://(120.25.124.199|image.py17w.net)", Pattern.CASE_INSENSITIVE).matcher(url).find();
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

    public String getAbstrContent() {
        return abstrContent;
    }

    public void setAbstrContent(String abstrContent) {
        this.abstrContent = abstrContent;
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
            //getLocalAttachments();
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
            //getLocalAttachments();
        }
        return image;
    }

    public void setImage(ArrayList<Attachment> image) {
        this.image = image;
    }

    public ArrayList<Attachment> getVideo() {
        if (null == video) {
            video = new ArrayList<>();
            //getLocalAttachments();
        }
        return video;
    }

    public void setVideo(ArrayList<Attachment> video) {
        this.video = video;
    }

    public ArrayList<Attachment> getAttach() {
        if (null == attach) {
            attach = new ArrayList<>();
            //getLocalAttachments();
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

    public String getDocClassifyId() {
        return docClassifyId;
    }

    public void setDocClassifyId(String docClassifyId) {
        this.docClassifyId = docClassifyId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getAwardable() {
        return awardable;
    }

    public void setAwardable(int awardable) {
        this.awardable = awardable;
    }

    public boolean awarded() {
        return awardable == AwardType.AWARDED;
    }

    public String getParticipant() {
        return participant;
    }

    public void setParticipant(String participant) {
        this.participant = participant;
    }

    public String getWatermark() {
        return watermark;
    }

    public void setWatermark(String watermark) {
        this.watermark = watermark;
    }

    public ArrayList<String> getShareUserIds() {
        if (null == shareUserIds) {
            shareUserIds = new ArrayList<>();
        }
        return shareUserIds;
    }

    public void setShareUserIds(ArrayList<String> shareUserIds) {
        this.shareUserIds = shareUserIds;
    }

    public String getFromGroupId() {
        return fromGroupId;
    }

    public void setFromGroupId(String fromGroupId) {
        this.fromGroupId = fromGroupId;
    }

    public String getFromGroupName() {
        return fromGroupName;
    }

    public void setFromGroupName(String fromGroupName) {
        this.fromGroupName = fromGroupName;
    }

    public ArrayList<Suggest> getSuggest() {
        if (null == suggest) {
            suggest = new ArrayList<>();
        }
        return suggest;
    }

    public void setSuggest(ArrayList<Suggest> suggest) {
        this.suggest = suggest;
    }
}
