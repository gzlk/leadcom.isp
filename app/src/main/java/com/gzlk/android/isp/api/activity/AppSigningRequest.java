package com.gzlk.android.isp.api.activity;

import android.support.annotation.NonNull;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.model.activity.AppSigning;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <b>功能描述：</b>活动应用：签到<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/26 00:37 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/26 00:37 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class AppSigningRequest extends Request<AppSigning> {

    public static AppSigningRequest request() {
        return new AppSigningRequest();
    }

    private static class SingleSigning extends Output<AppSigning> {
    }

    private static class MultipleSigning extends Query<AppSigning> {
    }

    @Override
    protected String url(String action) {
        return format("/activity/actSignInSetup%s", action);
    }

    @Override
    protected Class<AppSigning> getType() {
        return AppSigning.class;
    }

    @Override
    public AppSigningRequest setOnSingleRequestListener(OnSingleRequestListener<AppSigning> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public AppSigningRequest setOnMultipleRequestListener(OnMultipleRequestListener<AppSigning> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 保存签到应用
     *
     * @param signId      签到应用的id(为null则add，否则update)
     * @param activityId  活动id
     * @param title       签到应用的标题
     * @param description 描述
     * @param longitude   目的地经度
     * @param latitude    目的地纬度
     * @param altitude    目的地海拔高度
     * @param beginTime   签到开始时间
     * @param endTime     签到结束时间
     */
    public void save(String signId, @NonNull String activityId, String title, String description,
                     double longitude, double latitude, double altitude, String beginTime, String endTime) {
        // {id:"",actId:"",title:"",desc:"",lon:"",lat:"",alt:"",beginTime:"",endTime:"",accessToken：""}

        JSONObject object = new JSONObject();
        try {
            object.put("id", checkNull(signId))
                    .put("actId", activityId)
                    .put("title", title)
                    .put("desc", description)
                    .put("lon", longitude)
                    .put("lat", latitude)
                    .put("alt", altitude)
                    .put("beginTime", beginTime)
                    .put("endTime", endTime)
                    .put("accessToken", Cache.cache().accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleSigning.class, url(SAVE), object.toString(), HttpMethods.Post));
    }

    /**
     * 查找单个签到应用的详情
     */
    public void find(@NonNull String signId) {
        httpRequest(getRequest(SingleSigning.class, format("%s?id=%s", url(FIND), signId), "", HttpMethods.Get));
    }

    /**
     * 查询活动中的签到应用列表
     */
    public void list(@NonNull String activityId) {
        // actId=""
        httpRequest(getRequest(SingleSigning.class, format("%s?actId=%s", url(LIST), activityId), "", HttpMethods.Get));
    }
}
