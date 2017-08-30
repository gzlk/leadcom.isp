package com.gzlk.android.isp.model.activity.topic;

import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.activity.Activity;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.common.Attachment;
import com.gzlk.android.isp.model.organization.Organization;
import com.gzlk.android.isp.model.user.User;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.Table;

import java.util.ArrayList;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/08/29 15:01 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/08/29 15:01 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(Activity.Table.TOPIC)
public class AppTopic extends Model {

    public interface Field {
        String TopicId = "topicId";
    }

    public static AppTopic queryByTid(String tid) {
        return new Dao<>(AppTopic.class).querySingle(Activity.Field.NimId, tid);
    }

    public static String toJson(AppTopic topic) {
        return Json.gson().toJson(topic, new TypeToken<AppTopic>() {
        }.getType());
    }

    public static AppTopic fromJson(String json) {
        return Json.gson().fromJson(json, new TypeToken<AppTopic>() {
        }.getType());
    }

    @Column(Activity.Field.ActivityId)
    private String actId;                               //活动ID
    @Column(Activity.Field.NimId)
    private String tid;                                 //云信高级群ID
    @Column(Archive.Field.Title)
    private String title;                               //标题
    @Column(Model.Field.CreatorId)
    private String creatorId;                           //创建者用户ID
    @Column(Model.Field.CreatorName)
    private String creatorName;                         //创建者用户名称
    @Column(User.Field.HeadPhoto)
    private String creatorHeadPhoto;                    //创建者用户头像
    @Column(Model.Field.CreateDate)
    private String createDate;                          //创建时间
    @Column(Organization.Field.ModifyDate)
    private String modifyDate;                          //修改时间
    @Ignore
    private ArrayList<Attachment> attach;                    //附件
    @Ignore
    private ArrayList<String> userIdList;                    //邀请的用户ID列表
    @Ignore
    private ArrayList<String> headPhotoList;                 //成员头像列表(最多九张)
    @Ignore
    private ArrayList<AppTopicMember> actTopicMemberList;    //活动议题成员列表

    // 自定义属性
    private int unReadNum;

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
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

    public ArrayList<Attachment> getAttach() {
        return attach;
    }

    public void setAttach(ArrayList<Attachment> attach) {
        this.attach = attach;
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

    public ArrayList<String> getHeadPhotoList() {
        if (null == headPhotoList) {
            headPhotoList = new ArrayList<>();
        }
        return headPhotoList;
    }

    public void setHeadPhotoList(ArrayList<String> headPhotoList) {
        this.headPhotoList = headPhotoList;
    }

    public ArrayList<AppTopicMember> getActTopicMemberList() {
        if (null == actTopicMemberList) {
            actTopicMemberList = new ArrayList<>();
        }
        return actTopicMemberList;
    }

    public void setActTopicMemberList(ArrayList<AppTopicMember> actTopicMemberList) {
        this.actTopicMemberList = actTopicMemberList;
    }

    public int getUnReadNum() {
        return unReadNum;
    }

    public void setUnReadNum(int unReadNum) {
        this.unReadNum = unReadNum;
    }
}
