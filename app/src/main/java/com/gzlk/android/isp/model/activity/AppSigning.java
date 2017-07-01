package com.gzlk.android.isp.model.activity;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.model.organization.Organization;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
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
@Table(Activity.Table.SIGN)
public class AppSigning extends Sign {

    //活动Id
    @Column(Activity.Field.ActivityId)
    private String actId;
    //修改时间
    @Column(Organization.Field.ModifyDate)
    private String modifyDate;
    //签到开始时间
    @Column(AppVote.Field.BeginTime)
    private String beginTime;
    //签到结束时间
    @Column(AppVote.Field.EndTime)
    private String endTime;
    //签到人数(按user对象的id过滤，避免一个用户多次签到后被重复计数)
    @Column(Field.SignInNum)
    private int signInNum;
    @Ignore
    private int notifyBeginTime;

    /**
     * 是否可以签到
     */
    public boolean couldSignable(String date) {
        String fmt = StringHelper.getString(R.string.ui_base_text_date_time_format);
        long posTime = Utils.parseDate(fmt, date).getTime();
        long beginTime = Utils.parseDate(fmt, getBeginTime()).getTime();
        long endTime = Utils.parseDate(fmt, getEndTime()).getTime();
        if (posTime < beginTime) {
            ToastHelper.make().showMsg(R.string.ui_activity_sign_not_start);
            return false;
        } else if (posTime > endTime) {
            ToastHelper.make().showMsg(R.string.ui_activity_sign_has_been_ended);
            return false;
        }
        return true;
    }

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

    public int getSignInNum() {
        return signInNum;
    }

    public void setSignInNum(int signInNum) {
        this.signInNum = signInNum;
    }

    public int getNotifyBeginTime() {
        return notifyBeginTime;
    }

    public void setNotifyBeginTime(int notifyBeginTime) {
        this.notifyBeginTime = notifyBeginTime;
    }
}
