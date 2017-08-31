package com.gzlk.android.isp.api.activity;

import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.query.PaginationQuery;
import com.gzlk.android.isp.api.query.SingleQuery;
import com.gzlk.android.isp.model.activity.topic.AppTopicMember;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>活动议题成员api<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/08/29 15:28 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/08/29 15:28 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class AppTopicMemberRequest extends Request<AppTopicMember> {

    public static AppTopicMemberRequest request() {
        return new AppTopicMemberRequest();
    }

    private static class SingleMember extends SingleQuery<AppTopicMember> {
    }

    private static class MultipleMember extends PaginationQuery<AppTopicMember> {
    }

    private static final String MEMBER = "/activity/actTopicMember";

    @Override
    protected String url(String action) {
        return format("%s%s", MEMBER, action);
    }

    @Override
    protected Class<AppTopicMember> getType() {
        return AppTopicMember.class;
    }

    @Override
    public AppTopicMemberRequest setOnSingleRequestListener(OnSingleRequestListener<AppTopicMember> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public AppTopicMemberRequest setOnMultipleRequestListener(OnMultipleRequestListener<AppTopicMember> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 新增活动议题成员
     */
    public void add(String topicId, ArrayList<String> userIds) {
        // actTopicId,[userIdList]
        JSONObject object = new JSONObject();
        try {
            object.put("actTopicId", topicId)         // 活动ID
                    .put("itemContentList", new JSONArray(userIds));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpRequest(getRequest(SingleMember.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 踢出议题成员
     */
    public void delete(String topicId, String userId) {
        // actTopicId,userId
        httpRequest(getRequest(SingleMember.class, format("%s?actTopicId=%s&userId=%s", url(DELETE), topicId, userId), "", HttpMethods.Get));
    }

    /**
     * 退出议题
     */
    public void exit(String topicId) {
        // actTopicId
        httpRequest(getRequest(SingleMember.class, format("%s?actTopicId=%s", url(EXIT), topicId), "", HttpMethods.Get));
    }

    /**
     * 查询活动议题成员
     */
    public void find(String memberId) {
        httpRequest(getRequest(SingleMember.class, format("%s?id=%s", url(FIND), memberId), "", HttpMethods.Get));
    }

    /**
     * 查询活动议题成员列表
     */
    public void list(String topicId, int pageNumber) {
        // actTopicId,pageSize,pageNumber
        httpRequest(getRequest(SingleMember.class, format("%s?actTopicId=%s&pageNumber=%d", url(FIND), topicId, pageNumber), "", HttpMethods.Get));
    }
}
