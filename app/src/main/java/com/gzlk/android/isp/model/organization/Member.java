package com.gzlk.android.isp.model.organization;

import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.user.User;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

/**
 * <b>功能描述：</b>组织内成员<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/07 09:29 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/07 09:29 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(Group.Table.MEMBER)
public class Member extends Model {
    @Column(Group.Field.GroupId)
    private String groupId;        //群体ID
    @Column(Group.Field.SquadId)
    private String squadId;        //小组ID
    @Column(Field.UserId)
    private String userId;           //用户ID
    @Column(Field.UserName)
    private String userName;       //用户姓名
    @Column(User.Field.Phone)
    private String phone;          //用户手机
    @Column(Group.Field.RoleId)
    private String roleId;         //角色ID
    @Column(Group.Field.RoleName)
    private String roleName;       //角色名称
    @Column(Field.CreateDate)
    private String createDate;     //加入时间

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getSquadId() {
        return squadId;
    }

    public void setSquadId(String squadId) {
        this.squadId = squadId;
    }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
