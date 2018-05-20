package com.leadcom.android.isp.api.archive;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.archive.ArchiveQuery;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/05/20 20:44 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ArchiveQueryRequest extends Request<ArchiveQuery> {

    public static ArchiveQueryRequest request() {
        return new ArchiveQueryRequest();
    }

    private static class SingleArchive extends SingleQuery<ArchiveQuery> {
    }

    private static final String USER = "/user/userDoc";
    private static final String GROUP = "/group/groDoc";

    @Override
    protected String url(String action) {
        return format("%s%s", USER, action);
    }

    private String group(String action) {
        return format("%s%s", GROUP, action);
    }

    private String url(int type, String action) {
        return type == Archive.Type.USER ? url(action) : group(action);
    }

    @Override
    protected Class<ArchiveQuery> getType() {
        return ArchiveQuery.class;
    }

    @Override
    public ArchiveQueryRequest setOnSingleRequestListener(OnSingleRequestListener<ArchiveQuery> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public ArchiveQueryRequest setOnMultipleRequestListener(OnMultipleRequestListener<ArchiveQuery> listListener) {
        return null;
    }

    public void find(int type, String archiveId) {
        directlySave = false;
        JSONObject object = new JSONObject();
        try {
            object.put("docId", archiveId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpRequest(getRequest(SingleArchive.class, url(type, FIND), object.toString(), HttpMethods.Post));
    }
}
