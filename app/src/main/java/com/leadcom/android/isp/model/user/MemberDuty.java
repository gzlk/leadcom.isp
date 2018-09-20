package com.leadcom.android.isp.model.user;

import com.leadcom.android.isp.model.Model;


/**
 * <b>功能描述：</b>用户履职统计数据<br />
 * <b>创建作者：</b>SYSTEM <br />
 * <b>创建时间：</b>2018/08/14 18:33 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class MemberDuty extends Model {

    private String userId, userName, squadId, squadName;
    private long docNum, activityNum;

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

    public String getSquadId() {
        return squadId;
    }

    public void setSquadId(String squadId) {
        this.squadId = squadId;
    }

    public String getSquadName() {
        return squadName;
    }

    public void setSquadName(String squadName) {
        this.squadName = squadName;
    }

    public long getDocNum() {
        return docNum;
    }

    public void setDocNum(long docNum) {
        this.docNum = docNum;
    }

    public long getActivityNum() {
        return activityNum;
    }

    public void setActivityNum(long activityNum) {
        this.activityNum = activityNum;
    }
}
