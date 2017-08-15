package com.gzlk.android.isp.api.activity;

import com.gzlk.android.isp.api.query.SingleQuery;
import com.gzlk.android.isp.api.query.PaginationQuery;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.model.activity.AppNotice;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

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

    private static class SingleNotice extends SingleQuery<AppNotice> {
    }

    private static class MultipleNotice extends PaginationQuery<AppNotice> {
    }

    @Override
    protected String url(String action) {
        return format("/activity/actNotice%s", action);
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

    @Override
    protected void save(AppNotice notice) {
        AppNotice old = AppNotice.get(notice.getId());
        notice.setRead(null != old && old.isRead());
        super.save(notice);
    }

    @Override
    protected void save(List<AppNotice> list) {
        if (null != list) {
            for (AppNotice notice : list) {
                AppNotice old = AppNotice.get(notice.getId());
                notice.setRead(null != old && old.isRead());
            }
        }
        super.save(list);
    }

    /**
     * 增加通知
     *
     * @param activityId 所属活动的id
     * @param title      通知标题
     * @param content    通知内容
     */
    public void add(String activityId, String title, String content) {
        // {title:"",content:"",actId:""}

        JSONObject object = new JSONObject();
        try {
            object.put("actId", activityId)
                    .put("title", title)
                    .put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleNotice.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 删除一个通知
     */
    public void delete(String noticeId) {
        httpRequest(getRequest(SingleNotice.class, format("%s?id=%s", url(DELETE), noticeId), "", HttpMethods.Get));
    }

    /**
     * 查找通知详情
     */
    public void find(String noticeId) {
        // id=""
        httpRequest(getRequest(SingleNotice.class, format("%s?id=%s", url(FIND), noticeId), "", HttpMethods.Get));
    }

    /**
     * 通过tid查找活动详情
     */
    public void findByTid(String tid) {
        // tid
        httpRequest(getRequest(SingleNotice.class, format("%s?tid=%s", "", tid), "", HttpMethods.Get));
    }

    /**
     * 查找活动里的通知列表
     */
    public void list(String activityId, int pageNumber) {
        // actId,pageSize,pageNumber
        httpRequest(getRequest(MultipleNotice.class, format("%s?actId=%s&pageNumber=%d", url(LIST), activityId, pageNumber), "", HttpMethods.Get));
    }
}
