package com.gzlk.android.isp.model.organization;

import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能描述：</b>组织成员权限<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/07 09:27 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/07 09:27 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(Organization.Table.PERMISSION)
public class Permission extends Model {

    public static class Field {
        public static final String PermissionName = "permissionName";
        public static final String PermissionCode = "permissionCode";
    }

    /**
     * 根据权限id集查询权限列表
     */
    public static List<Permission> getPermissions(ArrayList<String> ids) {
        if (null == ids || ids.size() < 1) {
            return new ArrayList<>();
        }
        QueryBuilder<Permission> builder = new QueryBuilder<>(Permission.class)
                .whereIn(Model.Field.Id, ids.toArray());
        return new Dao<>(Permission.class).query(builder);
    }

    //权限名称
    @Column(Field.PermissionName)
    private String perName;
    //权限编码
    @Column(Field.PermissionCode)
    private String perCode;

    public String getPerName() {
        return perName;
    }

    public void setPerName(String perName) {
        this.perName = perName;
    }

    public String getPerCode() {
        return perCode;
    }

    public void setPerCode(String perCode) {
        this.perCode = perCode;
    }
}
