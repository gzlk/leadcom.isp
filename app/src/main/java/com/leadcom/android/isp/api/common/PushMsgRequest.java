package com.leadcom.android.isp.api.common;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.BoolQuery;
import com.leadcom.android.isp.api.query.NumericQuery;
import com.leadcom.android.isp.api.query.PageQuery;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.model.common.PushMessage;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

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
public class PushMsgRequest extends Request<PushMessage> {

    public static PushMsgRequest request() {
        return new PushMsgRequest();
    }

    private static class SinglePush extends SingleQuery<PushMessage> {
    }

    private static class BoolPush extends BoolQuery<PushMessage> {
    }

    private static class PagePush extends PageQuery<PushMessage> {
    }

    private static class NumericPush extends NumericQuery<PushMessage> {
    }

    @Override
    protected String url(String action) {
        return format("/system/appPushMessage%s", action);
    }

    @Override
    protected Class<PushMessage> getType() {
        return PushMessage.class;
    }

    @Override
    public PushMsgRequest setOnSingleRequestListener(OnSingleRequestListener<PushMessage> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public PushMsgRequest setOnMultipleRequestListener(OnMultipleRequestListener<PushMessage> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 拉取推送消息列表
     */
    public void list(String templateCode, int pageNumber) {
        directlySave = false;
        String param = format("%s?pageNumber=%d&pageSize=20%s", url(LIST), pageNumber, (isEmpty(templateCode) ? "" : format("&templateCode=%s", pageNumber, templateCode)));
        executeHttpRequest(getRequest(PagePush.class, param, "", HttpMethods.Get));
    }

    /**
     * 查看消息，同时置为已读
     */
    public void find(String msgId) {
        directlySave = false;
        executeHttpRequest(getRequest(SinglePush.class, url(FIND), getIdObject(msgId).toString(), HttpMethods.Post));
    }

    /**
     * 删除指定的推送消息
     */
    public void delete(String msgId) {
        directlySave = false;
        executeHttpRequest(getRequest(BoolPush.class, url(DELETE), getIdObject(msgId).toString(), HttpMethods.Post));
    }

    private JSONObject getIdObject(String msgId) {
        JSONObject object = new JSONObject();
        try {
            object.put("id", msgId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    private JSONObject getTemplateObject(String templateCode) {
        JSONObject object = new JSONObject();
        try {
            object.put("templateCode", checkNull(templateCode));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 查询未读消息数量(可以指定某个类别的消息)
     */
    public void unreadCount(String templateCode) {
        directlySave = false;
        executeHttpRequest(getRequest(NumericPush.class, url("/unreadMessageCount"), getTemplateObject(templateCode).toString(), HttpMethods.Post));
    }

    /**
     * 清空用户的推送消息(templateCode传空值时清空所有消息)
     */
    public void clean(String templateCode) {
        directlySave = false;
        executeHttpRequest(getRequest(BoolPush.class, url(CLEAN), getTemplateObject(templateCode).toString(), HttpMethods.Post));
    }
}
