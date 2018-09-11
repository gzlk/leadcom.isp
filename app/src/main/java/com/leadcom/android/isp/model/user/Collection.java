package com.leadcom.android.isp.model.user;

import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.common.Attachment;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.Table;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>个人收藏<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/20 00:18 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/20 00:18 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(User.Table.COLLECTION)
public class Collection extends Model {

    public interface Field {
        String SourceType = "sourceType";
        String SourceId = "sourceId";
        String SourceTitle = "sourceTitle";
        String CreatorHeadPhoto = "creatorHeadPhoto";
        String IsLocalStorage = "isLocalStorage";
        String UsedTimes = "usedTimes";
    }

    /**
     * 个人收藏类别
     */
    public interface Type {
        /**
         * 什么类别都不定，查询收藏的时候用
         */
        int NONE = 0;
        /**
         * 文本
         */
        int TEXT = 1;
        /**
         * 文档
         */
        int ARCHIVE = 2;
        /**
         * 图片
         */
        int IMAGE = 3;
        /**
         * 视频
         */
        int VIDEO = 4;
        /**
         * 附件
         */
        int ATTACHMENT = 5;
        /**
         * 音频
         */
        int AUDIO = 6;
        /**
         * 位置
         */
        int POSITION = 7;
        /**
         * 分享
         */
        //int MOMENT = 8;
        /**
         * 连接
         */
        //int LINK = 10;
        /**
         * 个人档案
         */
        int USER_ARCHIVE = 11;
        /**
         * 组织档案
         */
        int GROUP_ARCHIVE = 12;
        /**
         * 个人动态
         */
        int USER_MOMENT = 13;
        /**
         * 所有档案
         */
        int ALL_ARCHIVE = 1112;
    }

    /**
     * 档案的收藏状态
     */
    public interface CollectionType {
        /**
         * 未收藏
         */
        int UN_COLLECT = 0;
        /**
         * 已收藏
         */
        int COLLECTED = 1;
    }

    /**
     * 收藏来源
     */
    public interface SourceType {
        /**
         * 个人档案
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
        /**
         * 活动聊天
         */
        int ACTIVITY = 4;
        /**
         * 议题聊天
         */
        int TOPIC = 5;
    }

    public static Collection get(Model model) {
        if (model instanceof Archive) {
            return get((Archive) model);
        } else if (model instanceof Moment) {
            return get((Moment) model);
        }
        return null;
    }

    public static Collection get(Archive archive) {
        Collection col = new Collection();
        col.setType(isEmpty(archive.getGroupId()) ? Type.USER_ARCHIVE : Type.GROUP_ARCHIVE);
        // 组织档案/个人档案不需要content
        col.setContent("");
        col.setCreatorId(archive.getUserId());
        col.setCreatorName(archive.getUserName());
        col.setCreatorHeadPhoto(archive.getHeadPhoto());

        col.setSourceType(isEmpty(archive.getGroupId()) ? SourceType.USER_ARCHIVE : SourceType.GROUP_ARCHIVE);
        col.setSourceId(archive.getId());
        col.setSourceTitle(archive.getTitle());
        return col;
    }

    public static Collection get(Moment moment) {
        Collection col = new Collection();
        col.setType(Type.USER_MOMENT);
        col.setContent(moment.getContent());

        col.setCreatorId(moment.getUserId());
        col.setCreatorName(moment.getUserName());
        col.setCreatorHeadPhoto(moment.getHeadPhoto());

        col.setSourceType(SourceType.MOMENT);
        col.setSourceId(moment.getId());
        col.setSourceTitle("");
        return col;
    }

    /**
     * 收藏附件、视频、音频、图片
     */
    public static Collection get(String url) {
        Collection col = new Collection();
        Attachment attach = new Attachment();
        attach.setUrl(url);
        attach.resetInformation();
        if (attach.isImage()) {
            col.setType(Type.IMAGE);
        } else if (attach.isVideo()) {
            col.setType(Type.VIDEO);
        } else {
            col.setType(Type.ATTACHMENT);
        }
        col.setContent(url);
        return col;
    }

    //收藏的类型(1.文本,2.文档,3.图片,4.视频,5.附件,6.音频,7.位置,11.个人档案,12.组织档案,13.个人动态)
    @Column(Archive.Field.Type)
    private int type;
    //标签
    @Column(Archive.Field.Label)
    private ArrayList<String> label;
    //来源的模块类型(1.个人档案,2.组织档案,3.个人动态,4.活动聊天,5.议题聊天)
    @Column(Field.SourceType)
    private int sourceType;
    //来源的模块ID(对应来源模块的id:1->个人档案ID,2->组织档案ID,3->个人动态ID,4->活动ID,5->议题ID)
    @Column(Field.SourceId)
    private String sourceId;
    //来源的模块标题(对应来源模块的title,个人动态没有标题,该属性为Null)
    @Column(Field.SourceTitle)
    private String sourceTitle;
    //收藏的内容(文本,文档,图片,视频,附件,音频)
    @Column(Archive.Field.Content)
    private String content;
    //个人档案
    @Ignore
    private Archive userDoc;
    //组织档案
    @Ignore
    private Archive groDoc;
    //个人动态
    @Ignore
    private Moment userMmt;
    @Ignore
    private Position position;         //地理位置(经度,纬度,海拔,位置名称)
    //收藏人ID
    @Column(Model.Field.UserId)
    private String userId;
    //原作者ID
    @Column(Archive.Field.CreatorId)
    private String creatorId;
    //原作者名称
    @Column(Archive.Field.CreatorName)
    private String creatorName;
    @Column(Field.CreatorHeadPhoto)
    private String creatorHeadPhoto;   //原作者用户头像
    //创建日期
    @Column(Model.Field.CreateDate)
    private String createDate;
    //修改日期
    @Column(Archive.Field.LastModifiedDate)
    private String modifiedDate;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ArrayList<String> getLabel() {
        if (null == label) {
            label = new ArrayList<>();
        }
        return label;
    }

    public void setLabel(ArrayList<String> label) {
        this.label = label;
    }

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

    public String getSourceTitle() {
        return sourceTitle;
    }

    public void setSourceTitle(String sourceTitle) {
        this.sourceTitle = sourceTitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Archive getUserDoc() {
        return userDoc;
    }

    public void setUserDoc(Archive userDoc) {
        this.userDoc = userDoc;
    }

    public Archive getGroDoc() {
        return groDoc;
    }

    public void setGroDoc(Archive groDoc) {
        this.groDoc = groDoc;
    }

    public Moment getUserMmt() {
        return userMmt;
    }

    public void setUserMmt(Moment userMmt) {
        this.userMmt = userMmt;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorHeadPhoto() {
        return creatorHeadPhoto;
    }

    public void setCreatorHeadPhoto(String creatorHeadPhoto) {
        this.creatorHeadPhoto = creatorHeadPhoto;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}
