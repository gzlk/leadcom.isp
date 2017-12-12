package com.leadcom.android.isp.api.common;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.ListQuery;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.nim.model.notification.NimMessage;
import com.litesuits.http.request.param.HttpMethods;

/**
 * <b>功能描述：</b>系统推送消息<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/12/06 20:53 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/12/06 20:53 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class PushMsgRequest extends Request<NimMessage> {

    public static PushMsgRequest request() {
        return new PushMsgRequest();
    }

    private static class SinglePush extends SingleQuery<NimMessage> {
    }

    private static class MultiplePush extends ListQuery<NimMessage> {
    }

    @Override
    protected String url(String action) {
        return format("/system/appPushMessage%s", action);
    }

    @Override
    protected Class<NimMessage> getType() {
        return NimMessage.class;
    }

    @Override
    public PushMsgRequest setOnSingleRequestListener(OnSingleRequestListener<NimMessage> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public PushMsgRequest setOnMultipleRequestListener(OnMultipleRequestListener<NimMessage> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 拉取推送消息列表
     */
    public void list() {
        httpRequest(getRequest(MultiplePush.class, url(LIST), "", HttpMethods.Get));
    }

    /**
     * 更改已读状态
     */
    public void update(String uuid) {
        httpRequest(getRequest(SingleQuery.class, url(format("%s?uuid=%s", UPDATE, uuid)), "", HttpMethods.Get));
    }

    /**
     * 删除指定的推送消息
     */
    public void delete(String uuid) {
        httpRequest(getRequest(SingleQuery.class, url(format("%s?uuid=%s", DELETE, uuid)), "", HttpMethods.Get));
    }
}
