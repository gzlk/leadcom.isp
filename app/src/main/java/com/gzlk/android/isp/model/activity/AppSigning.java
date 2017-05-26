package com.gzlk.android.isp.model.activity;

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

    //签到开始时间
    @Column(Vote.Field.BeginTime)
    private String beginTime;
    //签到结束时间
    @Column(Vote.Field.EndTime)
    protected String endTime;
    //应签到人数
    @Column(Field.ExceptedNum)
    private String expectedSignInNum;
    //实际签到人数
    @Column(Field.RealSignNum)
    protected String readSignInNum;
    //未签到人数
    @Column(Field.NotSignNum)
    private String toBeSignInNum;

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

    public String getExpectedSignInNum() {
        return expectedSignInNum;
    }

    public void setExpectedSignInNum(String expectedSignInNum) {
        this.expectedSignInNum = expectedSignInNum;
    }

    public String getReadSignInNum() {
        return readSignInNum;
    }

    public void setReadSignInNum(String readSignInNum) {
        this.readSignInNum = readSignInNum;
    }

    public String getToBeSignInNum() {
        return toBeSignInNum;
    }

    public void setToBeSignInNum(String toBeSignInNum) {
        this.toBeSignInNum = toBeSignInNum;
    }
}
