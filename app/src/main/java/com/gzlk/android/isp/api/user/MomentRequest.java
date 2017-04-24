package com.gzlk.android.isp.api.user;

import android.support.annotation.NonNull;

import com.gzlk.android.isp.api.BaseApi;
import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnRequestListListener;
import com.gzlk.android.isp.api.listener.OnRequestListener;
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

    private static final String MOMENT = BaseApi.URL + "/user/moment";
    private static final String ADD = MOMENT + "/add";
    private static final String DELETE = MOMENT + "/delete";
    private static final String FIND = MOMENT + "/find";
    private static final String LIST = MOMENT + "/list";
    private static final String GROUPS = MOMENT + "/groList";

    private MomentRequest() {
        super();
    }
//
//    private JsonRequest<SingleMoment> getSingleRequest(String url, String jsonBody, HttpMethods methods) {
//        return new JsonRequest<SingleMoment>(url, SingleMoment.class)
//                .setHttpListener(new OnHttpListener<SingleMoment>() {
//                    @Override
//                    public void onSucceed(SingleMoment data, Response<SingleMoment> response) {
//                        super.onSucceed(data, response);
//                        if (!data.success()) {
//                            ToastHelper.make().showMsg(data.getMsg());
//                        }
//                        if (null != onRequestListener) {
//                            onRequestListener.onResponse(data.getData(), data.success());
//                        }
//                    }
//                }).setHttpBody(new JsonBody(jsonBody), methods);
//    }
//
//    private JsonRequest<MultiMoment> getMultiRequest(String url) {
//        return new JsonRequest<MultiMoment>(url, MultiMoment.class)
//                .setHttpListener(new OnHttpListener<MultiMoment>() {
//                    @Override
//                    public void onSucceed(MultiMoment data, Response<MultiMoment> response) {
//                        super.onSucceed(data, response);
//                        if (!data.success()) {
//                            ToastHelper.make().showMsg(data.getMsg());
//                        }
//                        if (null != onRequestListListener) {
//                            onRequestListListener.onResponse(data.getData().getList(),
//                                    data.success(), data.getData().getTotalPages(), data.getData().getPageSize(),
//                                    data.getData().getTotal(), data.getData().getPageNumber());
//                        }
//                    }
//                });
//    }

    @Override
    public MomentRequest setOnRequestListener(OnRequestListener<Moment> listener) {
        onRequestListener = listener;
        return this;
    }

    @Override
    public MomentRequest setOnRequestListListener(OnRequestListListener<Moment> listListener) {
        onRequestListListener = listListener;
        return this;
    }

    /**
     * 添加Moment
     */
    public void add(@NonNull String userId, String userName, String location, String content, ArrayList<String> image) {

        JSONObject object = new JSONObject();
        try {
            object.put("userId", userId)
                    .put("userName", checkNull(userName))
                    .put("location", checkNull(location))
                    .put("content", checkNull(content));
            JSONArray array = new JSONArray(image);
            object.put("image", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        log(object.toString());

        httpRequest(getRequest(SingleMoment.class, ADD, object.toString(), HttpMethods.Post));
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
        getRequestBy(LIST, QB_USER, MultiMoment.class, userId, "", HttpMethods.Get);
    }

    /**
     * 查找指定id的说说详情
     */
    public void find(@NonNull String momentId) {
        getRequestBy(FIND, QB_MOMENT, MultiMoment.class, momentId, "", HttpMethods.Get);
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

        httpRequest(getRequest(SingleMoment.class, DELETE, object.toString(), HttpMethods.Post));

    }

    /**
     * 查找同一组别的用户发布的说说列表
     */
    public void groupList(@NonNull String userId) {
        getRequestBy(FIND, QB_USER, MultiMoment.class, userId, "", HttpMethods.Get);
    }
}
