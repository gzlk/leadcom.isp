package com.gzlk.android.isp.model.activity.topic;

import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.activity.Activity;
import com.gzlk.android.isp.model.user.User;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能描述：</b>议题的成员<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/08/29 15:02 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/08/29 15:02 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(Activity.Table.TOPIC_MEMBER)
public class AppTopicMember extends Model {

    public static boolean isMeMemberOf(String tid) {
        AppTopic topic = AppTopic.queryByTid(tid);
        return null != topic && null != getMyMemberOf(topic.getId());
    }

    public static AppTopicMember getMyMemberOf(String topicId) {
        QueryBuilder<AppTopicMember> builder = new QueryBuilder<>(AppTopicMember.class)
                .whereEquals(AppTopic.Field.TopicId, topicId)
                .whereAppendAnd()
                .whereEquals(Field.UserId, Cache.cache().userId);
        List<AppTopicMember> list = new Dao<>(AppTopicMember.class).query(builder);
        return (null == list || list.size() < 1) ? null : list.get(0);
    }

    /**
     * 从本地议题成员里删除指定议题的所有成员(退出议题、解散议题时用到)
     */
    public static void removeMemberOfTopicId(String topicId) {
        WhereBuilder builder = new WhereBuilder(AppTopicMember.class)
                .where(AppTopic.Field.TopicId + " = ?", topicId);
        new Dao<>(AppTopicMember.class).delete(builder);
    }

    @Column(AppTopic.Field.TopicId)
    private String actTopicId;          //活动议题ID
    @Column(Field.UserId)
    private String userId;              //用户ID
    @Column(Field.UserName)
    private String userName;            //用户名称
    @Column(User.Field.HeadPhoto)
    private String headPhoto;           //用户头像
    @Column(Field.CreateDate)
    private String createDate;          //创建时间
    @Ignore
    private ArrayList<String> userIdList;    //邀请的用户ID

    public String getActTopicId() {
        return actTopicId;
    }

    public void setActTopicId(String actTopicId) {
        this.actTopicId = actTopicId;
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
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
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
