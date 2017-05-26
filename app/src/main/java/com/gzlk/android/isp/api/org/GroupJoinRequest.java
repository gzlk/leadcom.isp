package com.gzlk.android.isp.api.org;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.model.organization.JoinGroup;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/19 21:56 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/19 21:56 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class GroupJoinRequest extends Request<JoinGroup> {

    public static GroupJoinRequest request(){
        return new GroupJoinRequest();
    }

    private static class SingleJoin extends Output<JoinGroup> {
    }

    // 主动加入组织
    private static final String JOIN = "/user/appToJoinGroup";

    @Override
    protected String url(String action) {
        return format("%s%s", JOIN, action);
    }

    @Override
    protected Class<JoinGroup> getType() {
        return JoinGroup.class;
    }

    @Override
    public GroupJoinRequest setOnSingleRequestListener(OnSingleRequestListener<JoinGroup> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public GroupJoinRequest setOnMultipleRequestListener(OnMultipleRequestListener<JoinGroup> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 申请加入组织，申请成功之后返回的是一个组织成员json对象
     */
    public void join(String groupId, String groupName, String inviteeId, String inviteeName, String message) {
        //{groupId:"",groupName:"",inviteeId:"",inviteeName:"",msg:"",accessToken:""}
        JSONObject object = new JSONObject();
        try {
            object.put("groupId", groupId)
                    .put("groupName", groupName)
                    .put("inviteeId", inviteeId)
                    .put("inviteeName", inviteeName)
                    .put("msg", checkNull(message))
                    .put("accessToken", Cache.cache().accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleJoin.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 同意成员加入组织，只返回成功与否
     *
     * @param requestUUID 客户端从网易云通知接口获取的申请记录的id
     * @param message     留言
     */
    public void approveJoin(String requestUUID, String message) {
        handle(SingleJoin.class, InvitationRequest.APPROVE, requestUUID, message);
    }

    /**
     * 拒绝成员加入组织，只返回成功或失败
     *
     * @param requestUUID 客户端从网易云通知接口获取的申请记录的id
     * @param message     留言
     */
    public void rejectJoin(String requestUUID, String message) {
        handle(SingleJoin.class, InvitationRequest.REJECT, requestUUID, message);
    }

    private void handle(Type resultType, String action, String uuid, String message) {
        JSONObject object = new JSONObject();
        try {
            object.put("uuid", uuid)
                    .put("msg", checkNull(message))
                    .put("accessToken", Cache.cache().accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpRequest(getRequest(resultType, url(action), object.toString(), HttpMethods.Post));
    }
}
