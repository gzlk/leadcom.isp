package com.leadcom.android.isp.api.activity;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.PaginationQuery;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.model.activity.topic.AppTopic;
import com.leadcom.android.isp.model.common.Attachment;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>活动议题api<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/08/29 15:10 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/08/29 15:10 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class AppTopicRequest extends Request<AppTopic> {

    public static AppTopicRequest request() {
        return new AppTopicRequest();
    }

    private static class SingleTopic extends SingleQuery<AppTopic> {
    }

    private static class MultipleTopic extends PaginationQuery<AppTopic> {
    }

    private static final String TOPIC = "/activity/actTopic";

    @Override
    protected String url(String action) {
        return format("%s%s", TOPIC, action);
    }

    @Override
    protected Class<AppTopic> getType() {
        return AppTopic.class;
    }

    @Override
    public AppTopicRequest setOnSingleRequestListener(OnSingleRequestListener<AppTopic> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public AppTopicRequest setOnMultipleRequestListener(OnMultipleRequestListener<AppTopic> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 新增活动议题
     */
    public void add(String activityId, String title, ArrayList<String> userIds) {
        // actId,title,[userIdList]
        JSONObject object = new JSONObject();
        try {
            object.put("actId", activityId)         // 活动ID
                    .put("title", title)       // 标题;
                    .put("userIdList", new JSONArray(userIds));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        executeHttpRequest(getRequest(SingleTopic.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    public void update(String topicId, String topicTitle) {
        JSONObject object = new JSONObject();
        try {
            object.put("_id", topicId)         // 活动ID
                    .put("title", topicTitle);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        executeHttpRequest(getRequest(SingleTopic.class, url(UPDATE), object.toString(), HttpMethods.Post));
    }

    public void update(String topicId, ArrayList<Attachment> attachments) {
        JSONObject object = new JSONObject();
        try {
            object.put("_id", topicId)         // 活动ID
                    .put("attach", new JSONArray(Attachment.getJson(attachments)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        executeHttpRequest(getRequest(SingleTopic.class, url(UPDATE), object.toString(), HttpMethods.Post));
    }

    public void update(AppTopic topic) {
        // _id,title,[attach]
        JSONObject object = new JSONObject();
        try {
            object.put("_id", topic.getId())         // 活动ID
                    .put("title", topic.getTitle())       // 标题
                    .put("attach", new JSONArray(Attachment.getJson(topic.getAttach())));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        executeHttpRequest(getRequest(SingleTopic.class, url(UPDATE), object.toString(), HttpMethods.Post));
    }

    /**
     * 删除活动议题
     */
    public void delete(String topicId) {
        // id=""
        executeHttpRequest(getRequest(SingleTopic.class, format("%s?id=%s", url(DELETE), topicId), "", HttpMethods.Get));
    }

    /**
     * 查询活动议题
     */
    public void find(String topicId) {
        // ope:操作类型(1.仅查询活动议题,2.查询活动议题和所有活动议题成员)
        executeHttpRequest(getRequest(SingleTopic.class, format("%s?id=%s&ope=2", url(FIND), topicId), "", HttpMethods.Get));
    }

    /**
     * 查询活动议题列表
     */
    public void list(String activityId, int pageNumber) {
        executeHttpRequest(getRequest(MultipleTopic.class, format("%s?actId=%s&pageNumber=%d&pageSize=%d", url(LIST), activityId, pageNumber, MAX_PAGE_SIZE), "", HttpMethods.Get));
    }
}
