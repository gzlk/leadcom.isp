package com.gzlk.android.isp.model.user;

import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.user.document.Document;
import com.gzlk.android.isp.model.user.moment.Moment;
import com.litesuits.orm.db.annotation.Column;

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

public class Collection extends Model {

    /**
     * 个人收藏类别
     */
    public static class Type {
        /**
         * 文本
         */
        public static final String TEXT = "1";
        /**
         * 图片
         */
        public static final String IMAGE = "2";
        /**
         * 声音
         */
        public static final String VOICE = "3";
        /**
         * 附件
         */
        public static final String ATTACHMENT = "4";
        /**
         * 连接
         */
        public static final String LINK = "5";
    }

    //收藏的类型(1->文本, 2->图片, 3->语音, 4->附件, 5->链接)
    @Column(Document.Field.Type)
    private String type;
    //收藏的内容(文本,图片,语音,附件,链接)
    @Column(Moment.Field.Content)
    private String content;
    //收藏人ID
    @Column(Field.UserId)
    private String userId;
    //原作者ID
    @Column(Document.Field.Creator)
    private String creatorId;
    //原作者名称
    @Column(Document.Field.CreatorName)
    private String creatorName;
    //创建日期
    @Column(Field.CreateDate)
    private String createDate;
    //修改日期
    @Column(Document.Field.LastModifiedDate)
    private String lastModifiedDate;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
