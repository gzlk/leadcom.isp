package com.gzlk.android.isp.api.activity;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.model.activity.AppNotice;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <b>功能描述：</b>活动应用：通知<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/26 08:47 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/26 08:47 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class AppNoticeRequest extends Request<AppNotice> {

    public static AppNoticeRequest request() {
        return new AppNoticeRequest();
    }

    private static class SingleNotice extends Output<AppNotice> {
    }

    private static class MultipleNotice extends Query<AppNotice> {
    }

    @Override
    protected String url(String action) {
        return format("/activity/notice%s", action);
    }

    @Override
    protected Class<AppNotice> getType() {
        return AppNotice.class;
    }

    @Override
    public AppNoticeRequest setOnSingleRequestListener(OnSingleRequestListener<AppNotice> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public AppNoticeRequest setOnMultipleRequestListener(OnMultipleRequestListener<AppNotice> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 增加通知
     */
    public void add(String activityId, String title, String content) {
        // {title:"",content:"",accessToken:"",actId:""}

        JSONObject object = new JSONObject();
        try {
            object.put("actId", activityId)
                    .put("title", title)
                    .put("content", content)
                    .put("accessToken", Cache.cache().accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleNotice.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 更改通知
     */
    public void update(String noticeId, String activityId, String title, String content) {
        // {id:"",title:"",content:"",accessToken:"",actId:""}
        JSONObject object = new JSONObject();
        try {
            object.put("id", noticeId)
                    .put("actId", activityId)
                    .put("title", title)
                    .put("content", content)
                    .put("accessToken", Cache.cache().accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleNotice.class, url(UPDATE), object.toString(), HttpMethods.Post));
    }

    /**
     * 查找通知详情
     */
    public void find(String noticeId) {
        // id=""
        httpRequest(getRequest(SingleNotice.class, format("%s?id=%s", url(UPDATE), noticeId), "", HttpMethods.Get));
    }

    /**
     * 查找活动里的通知列表
     */
    public void list(String activityId) {
        // actId=""
        httpRequest(getRequest(SingleNotice.class, format("%s?actId=%s", url(UPDATE), activityId), "", HttpMethods.Get));
    }
}
