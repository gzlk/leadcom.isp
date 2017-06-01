package com.gzlk.android.isp.model.organization;

import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.Table;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>组织内角色<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/07 09:18 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/07 09:18 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(Organization.Table.ROLE)
public class Role extends Model {

    public interface Field {
        String RoleName = "roleName";
        String RoleCode = "roleCode";
        String PermissionIds = "permissionIds";
    }

    /**
     * 通过角色id查找角色的详细信息
     */
    public static Role getRole(String roleId) {
        return new Dao<>(Role.class).query(roleId);
    }

    //角色名称
    @Column(Field.RoleName)
    private String roleName;
    //角色编码
    @Column(Field.RoleCode)
    private String rolCode;
    //角色所拥有的权限
    @Ignore
    private ArrayList<Permission> perList;
    @Column(Field.PermissionIds)
    private ArrayList<String> permissionIds;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRolCode() {
        return rolCode;
    }

    public void setRolCode(String rolCode) {
        this.rolCode = rolCode;
    }

    public ArrayList<Permission> getPerList() {
        getPermissions();
        return perList;
    }

    public void setPerList(ArrayList<Permission> perList) {
        this.perList = perList;
        savePermissionIds();
    }

    /**
     * 保存角色的权限列表以便以后再查询
     */
    public void savePermissionIds() {
        if (null != perList) {
            permissionIds = new ArrayList<>();
            for (Permission per : perList) {
                permissionIds.add(per.getId());
            }
            new Dao<>(Permission.class).save(perList);
        }
    }

    private void getPermissions() {
        if (null == perList) {
            perList = (ArrayList<Permission>) Permission.getPermissions(permissionIds);
        }
    }

    public ArrayList<String> getPermissionIds() {
        getPermissions();
        return permissionIds;
    }

    public void setPermissionIds(ArrayList<String> permissionIds) {
        this.permissionIds = permissionIds;
    }
}
