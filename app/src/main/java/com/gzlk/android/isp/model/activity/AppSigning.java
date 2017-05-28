package com.gzlk.android.isp.model.activity;

import com.gzlk.android.isp.model.organization.Organization;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

/**
 * <b>功能描述：</b>活动应用：签到<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/26 00:18 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/26 00:18 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(Sign.Table.SIGNING)
public class AppSigning extends Sign {

    //活动Id
    @Column(Activity.Field.ActivityId)
    private String actId;
    //修改时间
    @Column(Organization.Field.ModifyDate)
    private String modifyDate;
    //签到开始时间
    @Column(Vote.Field.BeginTime)
    private String beginTime;
    //签到结束时间
    @Column(Vote.Field.EndTime)
    private String endTime;
    //签到人数(按user对象的id过滤，避免一个用户多次签到后被重复计数)
    @Column(Field.SignInNum)
    private String signInNum;

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getSignInNum() {
        return signInNum;
    }

    public void setSignInNum(String signInNum) {
        this.signInNum = signInNum;
    }
}
