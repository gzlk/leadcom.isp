package com.gzlk.android.isp.model.activity.vote;

import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.activity.Activity;
import com.gzlk.android.isp.model.user.User;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能描述：</b>投票记录<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/25 23:30 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/25 23:30 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(Activity.Table.VOTE_RECORD)
public class AppVoteRecord extends Model {

    // 查找指定的投票里是否已经投过票
    public static AppVoteRecord getRecord(String voteId) {
        QueryBuilder<AppVoteRecord> builder = new QueryBuilder<>(AppVoteRecord.class)
                .whereEquals(AppVote.Field.VoteId, voteId)
                .whereAppendAnd()
                .whereEquals(Field.UserId, Cache.cache().userId);
        List<AppVoteRecord> list = new Dao<>(AppVoteRecord.class).query(builder);
        return (null == list || list.size() < 1) ? null : list.get(0);
    }

    //投票设置的id(用于把签到记录和签到设置关联起来)
    @Column(AppVote.Field.VoteId)
    private String setupId;
    //投票选择项的id
    @Column(AppVote.Field.VoteItemId)
    private ArrayList<String> itemIdList;
    //创建者名称
    @Column(Field.UserName)
    private String userName;
    //创建者的id
    @Column(Field.UserId)
    private String userId;
    //用户头像
    @Column(User.Field.HeadPhoto)
    private String headPhoto;
    //投票状态(1.未投票,2.已投票,3.弃权)
    @Column(Activity.Field.Status)
    private int status;
    //创建时间
    @Column(Field.CreateDate)
    protected String createDate;

    public boolean haventVote() {
        return status == AppVote.Status.NOT_VOTE;
    }

    public boolean hasVoted() {
        return status == AppVote.Status.HAS_VOTED;
    }

    public boolean hasRefused() {
        return status == AppVote.Status.REFUSED;
    }

    public String getSetupId() {
        return setupId;
    }

    public void setSetupId(String setupId) {
        this.setupId = setupId;
    }

    public ArrayList<String> getItemIdList() {
        return itemIdList;
    }

    public void setItemIdList(ArrayList<String> itemIdList) {
        this.itemIdList = itemIdList;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHeadPhoto() {
        return headPhoto;
    }

    public void setHeadPhoto(String headPhoto) {
        this.headPhoto = headPhoto;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
