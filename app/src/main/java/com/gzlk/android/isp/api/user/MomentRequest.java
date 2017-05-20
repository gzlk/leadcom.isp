package com.gzlk.android.isp.api.user;

import android.support.annotation.NonNull;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.user.moment.Moment;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * <b>功能描述：</b>提供说说、动态相关api的集合<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/24 15:57 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/24 15:57 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MomentRequest extends Request<Moment> {

    private static MomentRequest request;

    public static MomentRequest request() {
        if (null == request) {
            request = new MomentRequest();
        }
        return request;
    }

    static class SingleMoment extends Output<Moment> {
    }

    static class MultiMoment extends Query<Moment> {
    }

    private static final String MOMENT = "/user/moment";
    private static final String GROUPS = "/groList";

    @Override
    protected String url(String action) {
        return MOMENT + action;
    }

    private MomentRequest() {
        super();
    }

    @Override
    public MomentRequest setOnSingleRequestListener(OnSingleRequestListener<Moment> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public MomentRequest setOnMultipleRequestListener(OnMultipleRequestListener<Moment> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 添加Moment
     */
    public void add(String location, String content, ArrayList<String> image) {

        JSONObject object = new JSONObject();
        try {
            object.put("userId", Cache.cache().userId)
                    .put("userName", checkNull(Cache.cache().userName))
                    .put("location", checkNull(location))
                    .put("content", checkNull(content));
            JSONArray array = new JSONArray(image);
            object.put("image", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        log(object.toString());

        httpRequest(getRequest(SingleMoment.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    private static final String QB_USER = "userId";
    private static final String QB_MOMENT = "momentId";

    private void getRequestBy(String baseUrl, String queryBy, Type resultType, String queryId, String body, HttpMethods methods) {
        httpRequest(getRequest(resultType,
                StringHelper.format("%s?%s=%s", baseUrl, queryBy, queryId),
                body, methods));
    }

    /**
     * 查询指定用户id的说说列表
     */
    public void list(@NonNull String userId) {
        getRequestBy(url(LIST), QB_USER, MultiMoment.class, userId, "", HttpMethods.Get);
    }

    /**
     * 查找指定id的说说详情
     */
    public void find(@NonNull String momentId) {
        getRequestBy(url(FIND), QB_MOMENT, MultiMoment.class, momentId, "", HttpMethods.Get);
    }

    /**
     * 删除一条说说，需要POST
     */
    public void delete(@NonNull String momentId) {

        JSONObject object = new JSONObject();
        try {
            object.put(QB_MOMENT, momentId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        log(object.toString());

        httpRequest(getRequest(SingleMoment.class, url(DELETE), object.toString(), HttpMethods.Post));

    }

    /**
     * 查找同一组别的用户发布的说说列表
     */
    public void groupList(@NonNull String groupId) {
        getRequestBy(url(FIND), QB_USER, MultiMoment.class, groupId, "", HttpMethods.Get);
    }
}
