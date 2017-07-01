package com.gzlk.android.isp.model.activity;

import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.archive.Archive;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

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

    //活动Id
    @Column(Activity.Field.ActivityId)
    private String actId;
    //投票设置的id(用于把签到记录和签到设置关联起来)
    @Column(AppVote.Field.VoteId)
    private String setupId;
    //投票选择项的id
    @Column(AppVote.Field.VoteItemId)
    private String itemId;
    //手机设备号
    @Column(AppVote.Field.IMSI)
    private String imsi;
    //创建者名称
    @Column(Field.UserName)
    private String userName;
    //创建者的id
    @Column(Field.UserId)
    private String userId;
    //创建时间
    @Column(Field.CreateDate)
    protected String createDate;

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public String getSetupId() {
        return setupId;
    }

    public void setSetupId(String setupId) {
        this.setupId = setupId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
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

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
