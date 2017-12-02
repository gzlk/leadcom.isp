package com.leadcom.android.isp.model.organization;

import com.leadcom.android.isp.model.Model;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>组织列表里简化的小组信息<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/07/13 17:36 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/07/13 17:36 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SimpleSquad extends Model {

    private String name;
    private String groupId;
    private ArrayList<SimpleMember> memberList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public ArrayList<SimpleMember> getMemberList() {
        return memberList;
    }

    public void setMemberList(ArrayList<SimpleMember> memberList) {
        this.memberList = memberList;
    }
}
