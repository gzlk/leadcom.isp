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
public class VoteRecord extends Model {

    //活动Id
    @Column(Activity.Field.ActivityId)
    private String actId;
    //投票设置的id(用于把签到记录和签到设置关联起来)
    @Column(Vote.Field.VoteId)
    private String setupId;
    //投票选择项的id
    @Column(Vote.Field.VoteItemId)
    private String choiceId;
    //手机设备号
    @Column(Vote.Field.IMSI)
    private String imsi;
    //创建者名称
    @Column(Archive.Field.CreatorName)
    private String creatorName;
    //创建者的id
    @Column(Archive.Field.CreatorId)
    private String creatorId;
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

    public String getChoiceId() {
        return choiceId;
    }

    public void setChoiceId(String choiceId) {
        this.choiceId = choiceId;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
