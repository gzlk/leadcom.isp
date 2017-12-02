package com.leadcom.android.isp.api.activity;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.litesuits.http.request.param.HttpMethods;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/09/01 10:59 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/09/01 10:59 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class AppMinutesRequest extends Request<String> {

    public static AppMinutesRequest request() {
        return new AppMinutesRequest();
    }

    private static class SingleMinutes extends SingleQuery<String> {
    }

    @Override
    protected String url(String action) {
        return format("/activity/minutes%s", action);
    }

    @Override
    protected Class<String> getType() {
        return String.class;
    }

    @Override
    public AppMinutesRequest setOnSingleRequestListener(OnSingleRequestListener<String> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public AppMinutesRequest setOnMultipleRequestListener(OnMultipleRequestListener<String> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    public void summary(String groupId, String activityId) {
        directlySave = false;
        // groupId,activityId
        httpRequest(getRequest(SingleMinutes.class, format("%s?groupId=%s&activityId=%s", url("/summary"), groupId, activityId), "", HttpMethods.Get));
    }
}
