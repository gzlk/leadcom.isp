package com.leadcom.android.isp.model.activity;

import com.google.gson.reflect.TypeToken;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.activity.vote.AppVote;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.common.TalkTeam;
import com.leadcom.android.isp.model.organization.Organization;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

/**
 * <b>功能描述：</b>活动的通知<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/25 22:44 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/25 22:44 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(Activity.Table.NOTICE)
public class AppNotice extends Model {

    public static AppNotice get(String noticeId) {
        return isEmpty(noticeId) ? null : new Dao<>(AppNotice.class).query(noticeId);
    }

    public static void save(AppNotice notice) {
        new Dao<>(AppNotice.class).save(notice);
    }

    public static String toJson(AppNotice notice) {
        return null == notice ? "{}" : Json.gson().toJson(notice, new TypeToken<AppNotice>() {
        }.getType());
    }

    public static AppNotice fromJson(String json) {
        return Json.gson().fromJson(isEmpty(json) ? "{}" : json, new TypeToken<AppNotice>() {
        }.getType());
    }

    //活动Id
    @Column(Activity.Field.ActivityId)
    private String actId;
    @Column(TalkTeam.Field.TeamId)
    private String commId;      //活动ID
    @Column(Activity.Field.NimId)
    private String tid;         //云信高级群ID
    //标题
    @Column(Archive.Field.Title)
    private String title;
    //内容
    @Column(Archive.Field.Content)
    private String content;
    //创建者Id
    @Column(Archive.Field.CreatorId)
    private String creatorId;
    //创建者名称
    @Column(Archive.Field.CreatorName)
    private String creatorName;
    @Column(AppVote.Field.CreatorHeadPhoto)
    private String creatorHeadPhoto;//创建者的用户头像
    //创建时间
    @Column(Field.CreateDate)
    private String createDate;
    //修改时间
    @Column(Organization.Field.ModifyDate)
    private String modifyDate;

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public String getCommId() {
        return commId;
    }

    public void setCommId(String commId) {
        this.commId = commId;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
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

    public String getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }
}
