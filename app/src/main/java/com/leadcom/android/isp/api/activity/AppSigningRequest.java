package com.leadcom.android.isp.api.activity;

import android.support.annotation.NonNull;

import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.api.query.PaginationQuery;
import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.model.activity.sign.AppSignRecord;
import com.leadcom.android.isp.model.activity.sign.AppSigning;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

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

    private static class SingleSigning extends SingleQuery<AppSigning> {
    }

    private static class MultipleSigning extends PaginationQuery<AppSigning> {
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

    @Override
    protected void save(AppSigning appSigning) {
        AppSignRecord.save(appSigning.getActSignInList());
        super.save(appSigning);
    }

    @Override
    protected void save(List<AppSigning> list) {
        if (null != list) {
            for (AppSigning signing : list) {
                AppSignRecord.save(signing.getActSignInList());
            }
        }
        super.save(list);
    }

    /**
     * 添加新的活动签到条目
     */
    public void add(AppSigning signing) {
        // {actId,title,content,lon,lat,alt,site,beginDate,endDate}
        JSONObject object = new JSONObject();
        try {
            object.put("actId", signing.getActId())
                    .put("title", signing.getTitle())
                    .put("content", signing.getContent())
                    .put("lon", signing.getLon())
                    .put("lat", signing.getLat())
                    .put("alt", signing.getAlt())
                    .put("site", signing.getSite())
                    .put("beginDate", signing.getBeginDate())
                    .put("endDate", signing.getEndDate());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpRequest(getRequest(SingleSigning.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 增加活动签到条目
     *
     * @param activityId  活动id
     * @param title       签到应用的标题
     * @param description 描述
     * @param longitude   目的地经度
     * @param latitude    目的地纬度
     * @param altitude    目的地海拔高度
     * @param beginTime   签到开始时间
     * @param endTime     签到结束时间
     */
    @Deprecated
    public void add(@NonNull String activityId, String title, String description,
                    double longitude, double latitude, double altitude, String beginTime, String endTime) {
        // {actId:"",title:"",desc:"",lon:"",lat:"",alt:"",beginTime:"",endTime:"",accessToken：""}

        JSONObject object = new JSONObject();
        try {
            object.put("actId", activityId)
                    .put("title", title)
                    .put("desc", description)
                    .put("lon", longitude)
                    .put("lat", latitude)
                    .put("alt", altitude)
                    .put("beginTime", beginTime)
                    .put("endTime", endTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpRequest(getRequest(SingleSigning.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 删除签到应用
     */
    public void delete(String signingId) {
        //id=""
        httpRequest(getRequest(SingleSigning.class, format("%s?id=%s", url(DELETE), signingId), "", HttpMethods.Get));
    }

    /**
     * 更新签到应用详情
     *
     * @param signingId   签到应用的id
     * @param title       签到应用的标题
     * @param description 描述
     * @param longitude   目的地经度
     * @param latitude    目的地纬度
     * @param altitude    目的地海拔高度
     * @param beginTime   签到开始时间
     * @param endTime     签到结束时间
     */
    public void update(@NonNull String signingId, String title, String description,
                       double longitude, double latitude, double altitude, String beginTime, String endTime) {
        // {id:"",title:"",desc:"",lon:"",lat:"",alt:"",beginTime:"",endTime:"",accessToken：""}

        JSONObject object = new JSONObject();
        try {
            object.put("id", signingId)
                    .put("title", title)
                    .put("desc", description)
                    .put("lon", longitude)
                    .put("lat", latitude)
                    .put("alt", altitude)
                    .put("beginTime", beginTime)
                    .put("endTime", endTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleSigning.class, url(UPDATE), object.toString(), HttpMethods.Post));
    }

    /**
     * 只查询签到内容
     */
    public static final int FIND_SIGN = 1;
    /**
     * 同时查询签到内容和签到记录
     */
    public static final int FIND_RECORD = 2;

    /**
     * 查找单个签到应用的详情
     *
     * @param signId 签到应用的id
     * @param option 操作类型(1.查询签到设置,2:查询签到设置和所有签到记录)
     */
    public void find(@NonNull String signId, int option) {
        if (option <= FIND_SIGN) {
            option = FIND_SIGN;
        }
        if (option >= FIND_RECORD) {
            option = FIND_RECORD;
        }
        httpRequest(getRequest(SingleSigning.class, format("%s?id=%s&ope=%d", url(FIND), signId, option), "", HttpMethods.Get));
    }

    /**
     * 查询活动中的签到应用列表
     */
    public void list(@NonNull String activityId, int pageNumber) {
        // actId,pageSize,pageNumber
        httpRequest(getRequest(MultipleSigning.class, format("%s?actId=%s&pageNumber=%d", url(LIST), activityId, pageNumber), "", HttpMethods.Get));
    }
}
