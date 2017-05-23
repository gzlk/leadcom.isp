package com.gzlk.android.isp.api.user;

import android.support.annotation.NonNull;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.user.Moment;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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

    public static MomentRequest request() {
        return new MomentRequest();
    }

    private static class SingleMoment extends Output<Moment> {
    }

    private static class MultiMoment extends Query<Moment> {
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
    protected Class<Moment> getType() {
        return Moment.class;
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

    @Override
    protected void save(List<Moment> list) {
        if (null != list && list.size() > 0) {
            for (Moment moment : list) {
                moment.resetAdditional(moment.getAddition());
            }
        }
        super.save(list);
    }

    @Override
    protected void save(Moment moment) {
        if (null != moment) {
            moment.resetAdditional(moment.getAddition());
        }
        super.save(moment);
    }

    /**
     * 添加Moment
     */
    public void add(String location, String content, ArrayList<String> image) {
        // {location,content,[image],accessToken}
        JSONObject object = new JSONObject();
        try {
            object.put("location", checkNull(location))
                    .put("content", checkNull(content))
                    .put("accessToken", Cache.cache().accessToken);
            JSONArray array = new JSONArray(image);
            object.put("image", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleMoment.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    private static final String QB_TOKEN = "accessToken";
    private static final String QB_MOMENT = "momentId";

    private void getRequestBy(String baseUrl, String queryBy, Type resultType, String queryId, String body, HttpMethods methods) {
        httpRequest(getRequest(resultType, StringHelper.format("%s?%s=%s", baseUrl, queryBy, queryId), body, methods));
    }

    /**
     * 查询指定用户id的说说列表
     */
    public void list(String accessToken) {
        getRequestBy(url(LIST), QB_TOKEN, MultiMoment.class, accessToken, "", HttpMethods.Get);
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
        getRequestBy(url(DELETE), QB_MOMENT, SingleMoment.class, momentId, "", HttpMethods.Post);
    }

    /**
     * 查找同一组别的用户发布的说说列表
     */
    public void groupList(@NonNull String accessToken) {
        getRequestBy(url(GROUPS), QB_TOKEN, MultiMoment.class, accessToken, "", HttpMethods.Get);
    }
}
