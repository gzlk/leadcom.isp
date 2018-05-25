package com.leadcom.android.isp.api.user;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.ListQuery;
import com.leadcom.android.isp.model.organization.RelateGroup;
import com.litesuits.http.request.param.HttpMethods;


/**
 * <b>功能描述：</b>组织权限api<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/04/04 21:36 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class PermissionRequest extends Request<RelateGroup> {

    public static PermissionRequest request() {
        return new PermissionRequest();
    }

    private static class ListPermission extends ListQuery<RelateGroup> {
    }

    @Override
    protected String url(String action) {
        return format("/user/userRelateGroup%s", action);
    }

    @Override
    protected Class<RelateGroup> getType() {
        return RelateGroup.class;
    }

    @Override
    public PermissionRequest setOnSingleRequestListener(OnSingleRequestListener<RelateGroup> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public PermissionRequest setOnMultipleRequestListener(OnMultipleRequestListener<RelateGroup> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 拉取我在所有已加入的组织内的角色权限
     */
    public void list() {
        directlySave = false;
        executeHttpRequest(getRequest(ListPermission.class, url(LIST), "", HttpMethods.Get));
    }
}
