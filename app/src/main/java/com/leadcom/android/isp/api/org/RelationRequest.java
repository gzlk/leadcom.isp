package com.leadcom.android.isp.api.org;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.BoolQuery;
import com.leadcom.android.isp.api.query.PageQuery;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.model.organization.RelateGroup;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * <b>功能描述：</b>组织关系api<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/09/13 12:07 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/09/13 12:07  <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class RelationRequest extends Request<RelateGroup> {

    public static RelationRequest request() {
        return new RelationRequest();
    }

    private static class SingleRelation extends SingleQuery<RelateGroup> {
    }

    private static class BoolRelation extends BoolQuery<RelateGroup> {
    }

    private static class PageRelation extends PageQuery<RelateGroup> {
    }

    @Override
    protected String url(String action) {
        return "/group/groRelation" + action;
    }

    @Override
    protected Class<RelateGroup> getType() {
        return RelateGroup.class;
    }

    @Override
    public RelationRequest setOnSingleRequestListener(OnSingleRequestListener<RelateGroup> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public RelationRequest setOnMultipleRequestListener(OnMultipleRequestListener<RelateGroup> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    public void add(String groupId, String targetGroupId, int relationType) {
        directlySave = false;
        JSONObject object = new JSONObject();
        try {
            object.put("groupId", groupId)
                    .put("relationGroupId", targetGroupId)
                    .put("relationType", relationType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        executeHttpRequest(getRequest(SingleRelation.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    public void delete(String relationId) {
        directlySave = false;
        executeHttpRequest(getRequest(BoolRelation.class, url(DELETE) + "?id=" + relationId, "", HttpMethods.Get));
    }

    public void list(String groupId, int relationType) {
        directlySave = false;
        String param = format("%s?groupId=%s&relationType=%d", url(LIST), groupId, relationType);
        executeHttpRequest(getRequest(PageRelation.class, param, "", HttpMethods.Get));
    }

    public void search(String groupId, int relationType, String info) {
        directlySave = false;
        String param = format("%s?groupId=%s&relationType=%d&pageSize=999&info=%s", url(SEARCH), groupId, relationType, info);
        executeHttpRequest(getRequest(PageRelation.class, param, "", HttpMethods.Get));
    }
}
