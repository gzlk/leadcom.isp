package com.leadcom.android.isp.model.organization;

import com.leadcom.android.isp.model.Model;

/**
 * <b>功能描述：</b>关联的组织<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/16 12:09 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/03/16 12:09 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class RelateGroup extends Model {

    public interface Type {
        /**
         * 加入的组织
         */
        int JOINED = 1;
        /**
         * 关注的组织
         */
        int FOLLOWED = 2;
    }

    private String userId;      //用户ID
    private String groupId;     //组织ID
    private String groupName;   //组织的名称
    private String logo;   // 组织的logo
    private String intro;  // 组织的简介
    private int type;        //关联的类型:1.加入的组织,2.关注的组织
    private Role groRole;    //组织角色

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Role getGroRole() {
        return groRole;
    }

    public void setGroRole(Role groRole) {
        this.groRole = groRole;
    }
}
