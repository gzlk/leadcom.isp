package com.leadcom.android.isp.model.organization;

import com.google.gson.reflect.TypeToken;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.model.Model;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.Table;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>群内小组<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/07 09:13 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/07 09:13 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(Organization.Table.SQUAD)
public class Squad extends Model {

    public static String toJson(Squad squad) {
        return Json.gson().toJson(squad, new TypeToken<Squad>() {
        }.getType());
    }

    public static Squad fromJson(String json) {
        return Json.gson().fromJson(isEmpty(json) ? "{}" : json, new TypeToken<Squad>() {
        }.getType());
    }

    @Column(Organization.Field.GroupId)
    private String groupId;        //所属群ID

    @Column(Model.Field.Name)
    private String name;           //小组名称

    @Column(Organization.Field.Introduction)
    private String intro;          //介绍

    @Column(Model.Field.CreateDate)
    private String createDate;     //创建时间

    @Column(Model.Field.CreatorId)
    private String creatorId;      //创建者ID

    @Column(Model.Field.CreatorName)
    private String creatorName;    //创建者姓名

    @Column(Organization.Field.MemberNumber)
    private int memberNum;      //成员数
    @Ignore
    private Role groRole;      // 当前登录者在组织里的角色
    @Ignore
    private ArrayList<Member> groSquMemberList;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getCreateDate() {
        if (isEmpty(createDate)) {
            createDate = DFT_DATE;
        }
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public int getMemberNum() {
        return memberNum;
    }

    public void setMemberNum(int memberNum) {
        this.memberNum = memberNum;
    }

    public Role getGroRole() {
        return groRole;
    }

    public void setGroRole(Role groRole) {
        this.groRole = groRole;
    }

    public ArrayList<Member> getGroSquMemberList() {
        if (null == groSquMemberList) {
            groSquMemberList = new ArrayList<>();
        }
        return groSquMemberList;
    }

    public void setGroSquMemberList(ArrayList<Member> groSquMemberList) {
        this.groSquMemberList = groSquMemberList;
    }
}
