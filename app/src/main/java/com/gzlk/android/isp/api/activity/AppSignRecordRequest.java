package com.gzlk.android.isp.api.activity;

import android.support.annotation.NonNull;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.model.activity.AppSignRecord;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * <b>功能描述：</b>活动应用：签到的记录<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/26 00:52 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/26 00:52 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class AppSignRecordRequest extends Request<AppSignRecord> {

    public static AppSignRecordRequest request() {
        return new AppSignRecordRequest();
    }

    private static class SingleRecord extends Output<AppSignRecord> {
    }

    private static class MultipleRecord extends Query<AppSignRecord> {
    }

    @Override
    protected String url(String action) {
        return format("/activity/actSignIn%s", action);
    }

    @Override
    protected Class<AppSignRecord> getType() {
        return AppSignRecord.class;
    }

    @Override
    public AppSignRecordRequest setOnSingleRequestListener(OnSingleRequestListener<AppSignRecord> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public AppSignRecordRequest setOnMultipleRequestListener(OnMultipleRequestListener<AppSignRecord> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 成员签到
     *
     * @param signId    活动中签到应用的id
     * @param longitude 目的地经度
     * @param latitude  目的地纬度
     * @param altitude  目的地海拔高度
     * @param imsi      手机识别码
     */
    public void add(@NonNull String signId, double longitude, double latitude, double altitude, String imsi) {
        // {actId:"",setupId:"",lon:"",lat:"",alt:"",imsi:""accessToken：""}

        JSONObject object = new JSONObject();
        try {
            object.put("setupId", signId)
                    .put("lon", longitude)
                    .put("lat", latitude)
                    .put("alt", altitude)
                    .put("imsi", imsi)
                    .put("accessToken", Cache.cache().accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleRecord.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 查询单个签到记录
     */
    public void find(@NonNull String signRecordId) {
        // id=""
        httpRequest(getRequest(SingleRecord.class, format("%s?id=%s", url(FIND), signRecordId), "", HttpMethods.Get));
    }

    /**
     * 查询某个签到设置下的所有签到记录
     */
    public void list(@NonNull String signId) {
        // setupId=""
        httpRequest(getRequest(MultipleRecord.class, format("%s?setupId=%s", url(LIST), signId), "", HttpMethods.Get));
    }
}
