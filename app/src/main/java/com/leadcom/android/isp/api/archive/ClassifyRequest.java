package com.leadcom.android.isp.api.archive;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.BoolQuery;
import com.leadcom.android.isp.api.query.PageQuery;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.model.archive.Classify;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <b>功能描述：</b>档案分类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/07/24 15:49 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/07/24 15:49 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ClassifyRequest extends Request<Classify> {

    public static ClassifyRequest request() {
        return new ClassifyRequest();
    }

    private static class BooleanClassify extends BoolQuery<Classify> {
    }

    private static class SingleClassify extends SingleQuery<Classify> {
    }

    private static class PageClassify extends PageQuery<Classify> {
    }

    @Override
    protected String url(String action) {
        return format("/archive/appDocClassify%s", action);
    }

    @Override
    protected Class<Classify> getType() {
        return Classify.class;
    }

    @Override
    public ClassifyRequest setOnSingleRequestListener(OnSingleRequestListener<Classify> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public ClassifyRequest setOnMultipleRequestListener(OnMultipleRequestListener<Classify> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    public void add(String groupId, String name, long parentId) {
        JSONObject object = new JSONObject();
        try {
            object.put("groupId", groupId)
                    .put("name", name);
            if (parentId > 0) {
                object.put("parentId", parentId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        executeHttpRequest(getRequest(SingleClassify.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    public void delete(String classifyId) {
        executeHttpRequest(getRequest(BooleanClassify.class, url(DELETE) + "?id=" + classifyId, "", HttpMethods.Get));
    }

    public void list(String groupId) {
        executeHttpRequest(getRequest(PageClassify.class, url(LIST) + "/groupId?groupId=" + groupId, "", HttpMethods.Get));
    }
}
