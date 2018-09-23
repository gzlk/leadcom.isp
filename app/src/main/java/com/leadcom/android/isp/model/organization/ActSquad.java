package com.leadcom.android.isp.model.organization;

import com.leadcom.android.isp.model.Model;

import java.util.ArrayList;


/**
 * <b>功能描述：</b>活动报名小组统计<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/09/23 18:19 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ActSquad extends Model {

    private String squadId, squadName;
    private ArrayList<Member> groActivityMemberList;

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

    public ArrayList<Member> getGroActivityMemberList() {
        if (null == groActivityMemberList) {
            groActivityMemberList = new ArrayList<>();
        }
        return groActivityMemberList;
    }

    public void setGroActivityMemberList(ArrayList<Member> groActivityMemberList) {
        this.groActivityMemberList = groActivityMemberList;
    }
}
