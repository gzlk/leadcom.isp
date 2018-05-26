package com.leadcom.android.isp.api.archive;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.model.common.ArchivePermission;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * <b>功能描述：</b>档案权限控制<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/05/26 22:31 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ArchivePermissionRequest extends Request<ArchivePermission> {

    public static ArchivePermissionRequest request() {
        return new ArchivePermissionRequest();
    }

    private static class SinglePermission extends SingleQuery<ArchivePermission> {
    }

    @Override
    protected String url(String action) {
        return format("");
    }

    @Override
    protected Class<ArchivePermission> getType() {
        return ArchivePermission.class;
    }

    @Override
    public ArchivePermissionRequest setOnSingleRequestListener(OnSingleRequestListener<ArchivePermission> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public ArchivePermissionRequest setOnMultipleRequestListener(OnMultipleRequestListener<ArchivePermission> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    public void permission(String archiveId) {
        directlySave = false;
        JSONObject object = new JSONObject();
        try {
            object.put("docId", archiveId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        executeHttpRequest(getRequest(SinglePermission.class, "/archive/permit", object.toString(), HttpMethods.Post));
    }
}
