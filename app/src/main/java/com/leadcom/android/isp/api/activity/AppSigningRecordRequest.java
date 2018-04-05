package com.leadcom.android.isp.api.activity;

import android.support.annotation.NonNull;

import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.api.query.PaginationQuery;
import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.model.activity.sign.AppSignRecord;
import com.litesuits.http.request.param.HttpMethods;

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

public class AppSigningRecordRequest extends Request<AppSignRecord> {

    public static AppSigningRecordRequest request() {
        return new AppSigningRecordRequest();
    }

    private static class SingleRecord extends SingleQuery<AppSignRecord> {
    }

    private static class MultipleRecord extends PaginationQuery<AppSignRecord> {
    }

    private static final String TEAM = "/communication/commSignInRecord";

    @Override
    protected String url(String action) {
        return format("/activity/actSignIn%s", action);
    }

    @Override
    protected Class<AppSignRecord> getType() {
        return AppSignRecord.class;
    }

    @Override
    public AppSigningRecordRequest setOnSingleRequestListener(OnSingleRequestListener<AppSignRecord> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public AppSigningRecordRequest setOnMultipleRequestListener(OnMultipleRequestListener<AppSignRecord> listListener) {
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
     * @param content   留言内容
     * @param address   签到的详细地址
     */
    @Deprecated
    public void add(@NonNull String signId, double longitude, double latitude, double altitude, String imsi,
                    String content, String address) {
        // {actId:"",setupId:"",lon:"",lat:"",alt:"",imsi:""accessToken：""}

        JSONObject object = new JSONObject();
        try {
            object.put("setupId", signId)
                    .put("lon", longitude)
                    .put("lat", latitude)
                    .put("alt", altitude)
                    .put("imsi", imsi)
                    .put("creatorName", Cache.cache().userName)
                    .put("title", content)
                    .put("desc", address);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleRecord.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 成员签到
     */
    public void add(AppSignRecord record) {
        // {setupId,lon,lat,alt,site}

        JSONObject object = new JSONObject();
        try {
            object.put("setupId", record.getSetupId())
                    .put("lon", record.getLon())
                    .put("lat", record.getLat())
                    .put("alt", record.getAlt())
                    // 签到地点名称
                    .put("site", record.getSite());
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


    // **********************************群聊

    /**
     * 成员签到
     */
    public void addTeamSignRecord(AppSignRecord record) {
        // {setupId,lon,lat,alt,site}

        JSONObject object = new JSONObject();
        try {
            object.put("signInId", record.getSignInId())
                    .put("lon", record.getLon())
                    .put("lat", record.getLat())
                    //.put("alt", record.getAlt())
                    // 签到地点名称
                    .put("site", record.getSite());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleRecord.class, format("%s%s", TEAM, ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 查询某个签到设置下的所有签到记录
     */
    public void listTeamSignRecord(@NonNull String signId) {
        // setupId=""
        httpRequest(getRequest(MultipleRecord.class, format("%s%s?signInId=%s", TEAM, LIST, signId), "", HttpMethods.Get));
    }
}
