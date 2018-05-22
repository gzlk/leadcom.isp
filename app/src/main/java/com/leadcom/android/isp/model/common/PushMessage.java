package com.leadcom.android.isp.model.common;

import com.google.gson.reflect.TypeToken;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.Archive;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/05/22 09:49 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/05/22 09:49 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(PushMessage.Field.TABLE)
public class PushMessage extends Model {

    public interface Field {
        String TABLE = "pushMessage";
        String MessageCode = "msgCode";
    }

    public static PushMessage fromJson(String json) {
        return Json.gson().fromJson(isEmpty(json) ? "{}" : json, new TypeToken<PushMessage>() {
        }.getType());
    }

    public static void save(PushMessage msg) {
        new Dao<>(PushMessage.class).save(msg);
    }

    /**
     * 消息代码
     */
    public interface MsgCode {
        /**
         * 用户档案被评论
         */
        String USER_DOC_COMMENT = "USERDOC_COMMENT_CODE";
        /**
         * 组织档案被评论
         */
        String GROUP_DOC_COMMENT = "GRODOC_COMMENT_CODE";
        /**
         * 组织档案推送
         */
        String GROUP_DOC_TRANSPORT = "GRODOC_TRANSPOND_CODE";
        /**
         * 组织档案草稿分享
         */
        String GROUP_DOC_SHARE = "GRODOC_SHARE_CODE";
        /**
         * 组织被关注
         */
        String GROUP_ATTENTION = "GROUP_ATTENTION_CODE";
    }

    @Column(Archive.Field.ArchiveId)
    private String docId;
    @Column(Archive.Field.ArchiveType)
    private int docType;
    @Column(Archive.Field.UserArchiveId)
    private String docUserId;
    @Column(Field.MessageCode)
    private String messageCode;
    @Column(Archive.Field.Title)
    private String title;
    @Column(Archive.Field.Content)
    private String content;

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public int getDocType() {
        return docType;
    }

    public void setDocType(int docType) {
        this.docType = docType;
    }

    public String getDocUserId() {
        return docUserId;
    }

    public void setDocUserId(String docUserId) {
        this.docUserId = docUserId;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
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
}
