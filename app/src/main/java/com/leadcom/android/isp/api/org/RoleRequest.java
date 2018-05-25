package com.leadcom.android.isp.api.org;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.ListQuery;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.model.organization.Role;
import com.litesuits.http.request.param.HttpMethods;

import java.util.List;


/**
 * <b>功能描述：</b>组织角色相关api<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/02/01 21:16 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class RoleRequest extends Request<Role> {

    public static RoleRequest request() {
        return new RoleRequest();
    }

    private static class SingleRole extends SingleQuery<Role> {
    }

    private static class ListRole extends ListQuery<Role> {
    }

    @Override
    protected String url(String action) {
        return format("/group/role%s", action);
    }

    @Override
    protected Class<Role> getType() {
        return Role.class;
    }

    @Override
    protected void save(Role role) {
        Role.save(role);
    }

    @Override
    protected void save(List<Role> list) {
        Role.save(list);
    }

    @Override
    public RoleRequest setOnSingleRequestListener(OnSingleRequestListener<Role> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public RoleRequest setOnMultipleRequestListener(OnMultipleRequestListener<Role> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 查询单个角色信息
     */
    public void find(String roleId) {
        executeHttpRequest(getRequest(SingleRole.class, format("%s?groRoleId=%s", url(FIND), roleId), "", HttpMethods.Get));
    }

    /**
     * 列出所有角色信息
     */
    public void list() {
        executeHttpRequest(getRequest(ListRole.class, url(LIST), "", HttpMethods.Get));
    }
}
