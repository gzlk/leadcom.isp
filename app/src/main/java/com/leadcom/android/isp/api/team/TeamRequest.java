package com.leadcom.android.isp.api.team;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.model.common.TalkTeam;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * <b>功能描述：</b>群聊相关api<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/28 20:56 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class TeamRequest extends Request<TalkTeam> {

    public static TeamRequest request() {
        return new TeamRequest();
    }

    private static class SingleTeam extends SingleQuery<TalkTeam> {
    }

    @Override
    protected String url(String action) {
        return format("/communication/communication%s", action);
    }

    @Override
    protected Class<TalkTeam> getType() {
        return TalkTeam.class;
    }

    @Override
    public TeamRequest setOnSingleRequestListener(OnSingleRequestListener<TalkTeam> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public TeamRequest setOnMultipleRequestListener(OnMultipleRequestListener<TalkTeam> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 创建一个群聊
     */
    public void add(TalkTeam team) {
        JSONObject object = new JSONObject();
        try {
            object.put("title", team.getTitle())
                    .put("userIdList", new JSONArray(team.getUserIdList()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        executeHttpRequest(getRequest(SingleTeam.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 结束一个群聊
     */
    public void end(String tid) {
        executeHttpRequest(getRequest(SingleTeam.class, url(format("/end?tid=%s", tid)), "", HttpMethods.Get));
    }

    /**
     * 更新群聊标题或成员列表
     */
    public void update(String tid, String title, ArrayList<String> userIdList) {
        JSONObject object = new JSONObject();
        try {
            object.put("tid", tid);
            if (!isEmpty(title)) {
                object.put("title", title);
            }
            if (null != userIdList) {
                object.put("userIdList", new JSONArray(userIdList));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        executeHttpRequest(getRequest(SingleTeam.class, url(UPDATE), object.toString(), HttpMethods.Post));
    }
}
