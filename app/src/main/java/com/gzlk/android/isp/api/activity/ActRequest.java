package com.gzlk.android.isp.api.activity;

import android.support.annotation.NonNull;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.model.activity.Activity;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>活动相关api<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/25 23:41 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/25 23:41 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ActRequest extends Request<Activity> {

    public static ActRequest request() {
        return new ActRequest();
    }

    private static class SingleActivity extends Output<Activity> {
    }

    private static class MultipleActivity extends Query<Activity> {
    }

    private static final String JOINED = "/list/joined";
    private static final String CREATED = "/list/created";
    private static final String ENDED = "/list/ended";
    private static final String REFRESH = "/list/refresh";
    private static final String END = "/end";

    @Override
    protected String url(String action) {
        return format("/activity%s", action);
    }

    @Override
    protected Class<Activity> getType() {
        return Activity.class;
    }

    @Override
    public ActRequest setOnSingleRequestListener(OnSingleRequestListener<Activity> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public ActRequest setOnMultipleRequestListener(OnMultipleRequestListener<Activity> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**新增活动*/
    public void add(@NonNull String title, String content, @NonNull String groupId, ArrayList<String> members) {
        // {title:"",content:"",groupId:"",accessToken:"",memberIdArray:""}
        JSONObject object = new JSONObject();
        try {
            object.put("title", title)
                    .put("content", checkNull(content))
                    .put("groupId", groupId)
                    .put("accessToken", Cache.cache().accessToken)
                    .put("memberIdArray", new JSONArray(members));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleActivity.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**修改活动*/
    public void update(@NonNull String activityId, String title, String content, ArrayList<String> members) {
        // {id:"",title:"",content:"",accessToken:"",memberIdArray:""}
        JSONObject object = new JSONObject();
        try {
            object.put("id", activityId)
                    .put("title", title)
                    .put("content", checkNull(content))
                    .put("accessToken", Cache.cache().accessToken)
                    .put("memberIdArray", new JSONArray(members));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleActivity.class, url(UPDATE), object.toString(), HttpMethods.Post));
    }

    /**(创建者或后台管理员)删除活动*/
    public void delete(@NonNull String activityId) {
        // id=""
        httpRequest(getRequest(SingleActivity.class, format("%s?id=%s", url(DELETE), activityId), "", HttpMethods.Post));
    }

    /**查询单个活动*/
    public void find(@NonNull String activityId) {
        // id=""
        httpRequest(getRequest(SingleActivity.class, format("%s?id=%s", url(FIND), activityId), "", HttpMethods.Get));
    }

    /**查看某组织内的活动列表(只显示当前用户被授权范围内的记录)*/
    public void list(@NonNull String groupId) {
        // groupId="",accessToken=""
        String params = format("groupId=%s&accessToken=%s", groupId, Cache.cache().accessToken);
        httpRequest(getRequest(MultipleActivity.class, format("%s?%s", url(LIST), params), "", HttpMethods.Get));
    }

    /**查询我参加的活动(非实时的缓存数据)*/
    public void joined() {
        httpRequest(getRequest(MultipleActivity.class, format("%s?accessToken=%s", url(JOINED), Cache.cache().accessToken), "", HttpMethods.Get));
    }

    /**查询我发起的活动(非实时的缓存数据)*/
    public void created() {
        httpRequest(getRequest(MultipleActivity.class, format("%s?accessToken=%s", url(CREATED), Cache.cache().accessToken), "", HttpMethods.Get));
    }

    /**查询我参与的已结束的活动*/
    public void ended() {
        httpRequest(getRequest(MultipleActivity.class, format("%s?accessToken=%s", url(ENDED), Cache.cache().accessToken), "", HttpMethods.Get));
    }

    /**刷新我参加的和我发起的活动(手机端在同意加入活动时调用)*/
    public void refresh() {
        httpRequest(getRequest(MultipleActivity.class, format("%s?accessToken=%s", url(REFRESH), Cache.cache().accessToken), "", HttpMethods.Get));
    }

    /**结束活动*/
    public void end(@NonNull String activityId) {
        String params = format("id=%s&accessToken=%s", activityId, Cache.cache().accessToken);
        httpRequest(getRequest(SingleActivity.class, format("%s?%s", url(END), params), "", HttpMethods.Post));
    }
}
