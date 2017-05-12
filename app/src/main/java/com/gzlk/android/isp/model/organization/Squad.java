package com.gzlk.android.isp.model.organization;

import com.gzlk.android.isp.model.Model;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

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

    public static class Field {
        public static final String Introduce = "introduce";
    }

    @Column(Organization.Field.GroupId)
    private String groupId;        //所属群ID
    @Column(Model.Field.Name)
    private String name;           //小组名称
    @Column(Field.Introduce)
    private String intro;          //介绍
    @Column(Model.Field.CreateDate)
    private String createDate;     //创建时间
    @Column(Model.Field.CreatorId)
    private String creatorId;      //创建者ID
    @Column(Model.Field.CreatorName)
    private String creatorName;    //创建者姓名
    @Column(Organization.Field.MemberNumber)
    private String memberNum;      //成员数
    @Column(Model.Field.AccessToken)
    private String accessToken;    //更改此记录的用户的令牌环

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

    public String getMemberNum() {
        return memberNum;
    }

    public void setMemberNum(String memberNum) {
        this.memberNum = memberNum;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
