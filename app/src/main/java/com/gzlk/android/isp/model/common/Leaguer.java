package com.gzlk.android.isp.model.common;

import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.organization.Organization;
import com.gzlk.android.isp.model.user.User;
import com.litesuits.orm.db.annotation.Column;

/**
 * <b>功能描述：</b>成员基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/25 22:36 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/25 22:36 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class Leaguer extends Model {

    //用户Id
    @Column(Field.UserId)
    private String userId;
    //用户姓名
    @Column(Field.UserName)
    private String userName;
    //角色Id
    @Column(Organization.Field.RoleId)
    private String roleId;
    //角色名称
    @Column(Organization.Field.RoleName)
    private String roleName;
    //创建时间
    @Column(Field.CreateDate)
    private String createDate;

    @Column(User.Field.Spell)
    private String spell;           // 名字的拼音

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
        spell = Utils.transformPinyin(this.userName);
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

    public String getSpell() {
        if (StringHelper.isEmpty(spell)) {
            spell = Utils.transformPinyin(userName);
        }
        return spell;
    }

    public void setSpell(String spell) {
        this.spell = spell;
    }
}
