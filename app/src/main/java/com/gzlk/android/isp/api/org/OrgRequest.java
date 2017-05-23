package com.gzlk.android.isp.api.org;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.model.organization.Organization;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <b>功能描述：</b>组织相关api集合<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/10 08:13 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/10 08:13 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class OrgRequest extends Request<Organization> {

    public static OrgRequest request() {
        return new OrgRequest();
    }

    private static class SingleGroup extends Output<Organization> {
    }

    private static class MultipleGroup extends Query<Organization> {
    }

    private static final String ORG = "/group/group";
    private static final String SEARCH_ALL = "/searchAll";
    private static final String FIND_UPPER = "/findUpGroup";

    @Override
    protected String url(String action) {
        return ORG + action;
    }

    @Override
    protected Class<Organization> getType() {
        return Organization.class;
    }

    @Override
    public OrgRequest setOnSingleRequestListener(OnSingleRequestListener<Organization> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public OrgRequest setOnMultipleRequestListener(OnMultipleRequestListener<Organization> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    @SuppressWarnings("ConstantConditions")
    public void add(String groupName, String groupLogo) {
        //{name,logo,accessToken}

        JSONObject object = new JSONObject();
        try {
            object.put("name", groupName)
                    .put("logo", checkNull(groupLogo))
                    .put("accessToken", Cache.cache().accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        log(object.toString());

        httpRequest(getRequest(SingleGroup.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    public void update(String groupId, String groupName, String groupLogo, String introduction) {
        //{_id,name,logo,intro,accessToken}
        JSONObject object = new JSONObject();
        try {
            object.put("_id", groupId)
                    .put("name", checkNull(groupName))
                    .put("logo", checkNull(groupLogo))
                    .put("intro", checkNull(introduction))
                    .put("accessToken", Cache.cache().accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        log(object.toString());

        httpRequest(getRequest(SingleGroup.class, url(UPDATE), object.toString(), HttpMethods.Post));
    }

    public void delete(String groupId) {
        httpRequest(getRequest(SingleGroup.class, format("%s?groupId=%s", url(DELETE), groupId), "", HttpMethods.Post));
    }

    public void find(String groupId) {
        httpRequest(getRequest(SingleGroup.class, format("%s?groupId=%s", url(FIND), groupId), "", HttpMethods.Get));
    }

    /**
     * 默认查询当前用户授权范围内的组织列表
     */
    public void list() {
        httpRequest(getRequest(MultipleGroup.class, format("%s?accessToken=%s", url(LIST), Cache.cache().accessToken), "", HttpMethods.Get));
    }

    /**
     * 在所有组织中搜索组织名称
     */
    public void searchAll(String groupName) {
        httpRequest(getRequest(MultipleGroup.class, format("%s?info=%s", url(SEARCH_ALL), groupName), "", HttpMethods.Get));
    }

    /**
     * 在已参加组织中搜索组织名称
     */
    public void search(String groupName) {
        httpRequest(getRequest(MultipleGroup.class, format("%s?info=%s&accessToken=%s", url(SEARCH), groupName, Cache.cache().accessToken), "", HttpMethods.Get));
    }

    /**
     * 查询上级组织，返回单个组织
     */
    public void findUpper(String groupId) {
        httpRequest(getRequest(SingleGroup.class, format("%s?upId=%s", url(FIND_UPPER), groupId), "", HttpMethods.Get));
    }
}
