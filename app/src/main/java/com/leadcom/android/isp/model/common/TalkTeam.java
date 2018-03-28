package com.leadcom.android.isp.model.common;

import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.activity.Activity;
import com.leadcom.android.isp.model.activity.vote.AppVote;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.organization.Organization;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

import java.util.ArrayList;


/**
 * <b>功能描述：</b>群聊<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/28 19:56 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(TalkTeam.Table.TALK_TEAM)
public class TalkTeam extends Model {

    /**
     * 群聊相关的表名
     */
    public interface Table {
        String TALK_TEAM = "talkTeam";
    }

    public interface Field {
        String TeamId = "commId";
        String RoleId="commRoleId";
    }

    /**
     * 群聊活动状态
     */
    public interface TeamStatus {
        /**
         * 进行中
         */
        int LIVING = 1;
        /**
         * 结束
         */
        int ENDED = 2;
    }

    @Column(Activity.Field.NimId)
    private String tid;
    @Column(Activity.Field.Status)
    private int status;
    @Column(Archive.Field.Title)
    private String title;
    @Column(Archive.Field.CreatorId)
    private String creatorId;
    @Column(Archive.Field.CreatorName)
    private String creatorName;
    @Column(AppVote.Field.CreatorHeadPhoto)
    private String creatorHeadPhoto;
    @Column(Model.Field.CreateDate)
    private String createDate;
    @Column(Organization.Field.ModifyDate)
    private String modifyDate;
    @Column(Activity.Field.UserIdList)
    private ArrayList<String> userIdList;

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public ArrayList<String> getUserIdList() {
        if (null == userIdList) {
            userIdList = new ArrayList<>();
        }
        return userIdList;
    }

    public void setUserIdList(ArrayList<String> userIdList) {
        this.userIdList = userIdList;
    }
}
