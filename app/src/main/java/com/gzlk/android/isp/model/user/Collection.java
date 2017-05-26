package com.gzlk.android.isp.model.user;

import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.archive.Archive;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

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

    /**
     * 个人收藏类别
     */
    public interface Type {
        /**
         * 文本
         */
        int TEXT = 1;
        /**
         * 图片
         */
        int IMAGE = 2;
        /**
         * 声音
         */
        int VOICE = 3;
        /**
         * 附件
         */
        int ATTACHMENT = 4;
        /**
         * 连接
         */
        int LINK = 5;
    }

    //收藏的类型(1->文本, 2->图片, 3->语音, 4->附件, 5->链接)
    @Column(Archive.Field.Type)
    private int type;
    //收藏的内容(文本,图片,语音,附件,链接)
    @Column(Archive.Field.Content)
    private String content;
    //收藏人ID
    @Column(Field.UserId)
    private String userId;
    //原作者ID
    @Column(Archive.Field.CreatorId)
    private String creatorId;
    //原作者名称
    @Column(Archive.Field.CreatorName)
    private String creatorName;
    //创建日期
    @Column(Field.CreateDate)
    private String createDate;
    //修改日期
    @Column(Archive.Field.LastModifiedDate)
    private String lastModifiedDate;

    public int getType() {
        return type;
    }

    public void setType(int type) {
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
