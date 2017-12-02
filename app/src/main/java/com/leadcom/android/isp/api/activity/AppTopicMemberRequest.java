package com.leadcom.android.isp.api.activity;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.ListQuery;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.model.organization.Member;
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

public class AppTopicMemberRequest extends Request<Member> {

    public static AppTopicMemberRequest request() {
        return new AppTopicMemberRequest();
    }

    private static class SingleMember extends SingleQuery<Member> {
    }

    private static class MultipleMember extends ListQuery<Member> {
    }

    private static final String MEMBER = "/activity/actTopicMember";

    @Override
    protected String url(String action) {
        return format("%s%s", MEMBER, action);
    }

    @Override
    protected Class<Member> getType() {
        return Member.class;
    }

    @Override
    public AppTopicMemberRequest setOnSingleRequestListener(OnSingleRequestListener<Member> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public AppTopicMemberRequest setOnMultipleRequestListener(OnMultipleRequestListener<Member> listListener) {
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
                    .put("userIdList", new JSONArray(userIds));
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
        httpRequest(getRequest(MultipleMember.class, format("%s?actTopicId=%s&pageNumber=%d&pageSize=%d", url(LIST), topicId, pageNumber, MAX_PAGE_SIZE), "", HttpMethods.Get));
    }
}
