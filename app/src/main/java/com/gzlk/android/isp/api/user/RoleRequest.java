package com.gzlk.android.isp.api.user;

import com.gzlk.android.isp.api.query.SingleQuery;
import com.gzlk.android.isp.api.query.PaginationQuery;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.model.organization.Role;
import com.litesuits.http.request.param.HttpMethods;

/**
 * <b>功能描述：</b>角色相关api<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/10 14:54 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/10 14:54 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class RoleRequest extends Request<Role> {

    public static RoleRequest request() {
        return new RoleRequest();
    }

    private static class SingleRole extends SingleQuery<Role> {
    }

    private static class MultipleRole extends PaginationQuery<Role> {
    }

    @Override
    protected String url(String action) {
        return null;
    }

    private String url(String path, String action) {
        return format("%s%s", path, action);
    }

    @Override
    protected Class<Role> getType() {
        return Role.class;
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

    /**拉取组织角色列表*/
    public void listGroupRoles() {
        httpRequest(getRequest(MultipleRole.class, url("/group/role", LIST), "", HttpMethods.Get));
    }

    public void listActivityRoles(){
        httpRequest(getRequest(MultipleRole.class, url("/activity/actRole", LIST), "", HttpMethods.Get));
    }
}
