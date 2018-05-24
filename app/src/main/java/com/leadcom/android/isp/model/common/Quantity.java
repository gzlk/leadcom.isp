package com.leadcom.android.isp.model.common;

import com.leadcom.android.isp.model.Model;


/**
 * <b>功能描述：</b>组织的统计信息<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/16 22:08 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class Quantity extends Model {
    // 关联组织数量
    private long conGroupNum;
    // 文档数量
    private long docNum;
    // 成员数量
    private long memberNum;
    // 小组数量
    private long squadNum;
    // 所属组织id
    private String groupId;

    // 收藏数量
    private long colNum;
    // 用户数量
    private long userNum;
    // 动态数量
    private long mmtNum;
    // 所属用户id
    private String userId;

    public long getConGroupNum() {
        return conGroupNum;
    }

    public void setConGroupNum(long conGroupNum) {
        this.conGroupNum = conGroupNum;
    }

    public long getDocNum() {
        return docNum;
    }

    public void setDocNum(long docNum) {
        this.docNum = docNum;
    }

    public long getMemberNum() {
        return memberNum;
    }

    public void setMemberNum(long memberNum) {
        this.memberNum = memberNum;
    }

    public long getSquadNum() {
        return squadNum;
    }

    public void setSquadNum(long squadNum) {
        this.squadNum = squadNum;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public long getColNum() {
        return colNum;
    }

    public void setColNum(long colNum) {
        this.colNum = colNum;
    }

    public long getUserNum() {
        return userNum;
    }

    public void setUserNum(long userNum) {
        this.userNum = userNum;
    }

    public long getMmtNum() {
        return mmtNum;
    }

    public void setMmtNum(long mmtNum) {
        this.mmtNum = mmtNum;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
