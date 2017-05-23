package com.gzlk.android.isp.api.org;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.model.organization.Squad;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <b>功能描述：</b>小组相关api<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/10 08:56 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/10 08:56 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SquadRequest extends Request<Squad> {

    public static SquadRequest request() {
        return new SquadRequest();
    }

    private static class SingleSquad extends Output<Squad> {
    }

    private static class MultipleSquad extends Query<Squad> {
    }

    private static final String SQUAD = "/group/groSquad";

    @Override
    protected String url(String action) {
        return SQUAD + action;
    }

    @Override
    protected Class<Squad> getType() {
        return Squad.class;
    }

    @Override
    public SquadRequest setOnSingleRequestListener(OnSingleRequestListener<Squad> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public SquadRequest setOnMultipleRequestListener(OnMultipleRequestListener<Squad> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    public void add(String groupId, String squadName, String squadIntroduction) {
        //{groupId,name,intro,accessToken}
        JSONObject object = new JSONObject();
        try {
            object.put("groupId", groupId)
                    .put("name", squadName)
                    .put("intro", checkNull(squadIntroduction))
                    .put("accessToken", Cache.cache().accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleSquad.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    public void delete(String squadId) {
        httpRequest(getRequest(SingleSquad.class, format("%s?squadId=%s", url(DELETE), squadId), "", HttpMethods.Post));
    }

    public void update(String squadId, String squadName, String squadIntroduction) {
        //{_id,name,intro,accessToken}
        JSONObject object = new JSONObject();
        try {
            object.put("squadId", squadId)
                    .put("name", squadName)
                    .put("intro", checkNull(squadIntroduction))
                    .put("accessToken", Cache.cache().accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleSquad.class, url(UPDATE), object.toString(), HttpMethods.Post));
    }

    public void find(String squadId) {
        httpRequest(getRequest(SingleSquad.class, format("%s?squadId=%s", url(FIND), squadId), "", HttpMethods.Get));
    }

    /**
     * 查询指定组织的小组列表
     */
    public void list(String groupId) {
        httpRequest(getRequest(MultipleSquad.class, format("%s?groupId=%s", url(LIST), groupId), "", HttpMethods.Get));
    }

    /**
     * 在组织中搜索小组名称
     */
    public void search(String groupId, String squadName) {
        httpRequest(getRequest(MultipleSquad.class, format("%s?groupId=%s&info=%s", url(SEARCH), groupId, squadName), "", HttpMethods.Get));
    }

}
