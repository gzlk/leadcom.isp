package com.gzlk.android.isp.api.org;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.model.organization.Invitation;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * <b>功能描述：</b>入群邀请<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/19 21:50 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/19 21:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class GroupInviteRequest extends Request<Invitation> {

    public static GroupInviteRequest request() {
        return new GroupInviteRequest();
    }

    private static class SingleInvite extends Output<Invitation> {
    }

    // 邀请成员
    private static final String INVITE = "/group/groInv";

    /**
     * 同意
     */
    static final String APPROVE = "/aprove";
    /**
     * 拒绝
     */
    static final String REJECT = "/reject";

    @Override
    protected String url(String action) {
        return format("%s%s", INVITE, action);
    }

    @Override
    public GroupInviteRequest setOnSingleRequestListener(OnSingleRequestListener<Invitation> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public GroupInviteRequest setOnMultipleRequestListener(OnMultipleRequestListener<Invitation> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 邀请某人加入组织
     *
     * @param groupId     要加入的组织id
     * @param groupName   要加入的组织名称
     * @param inviteeId   被邀请人的id
     * @param inviteeName 被邀请人的名字
     * @param message     申请、审核人的留言
     */
    public void invite(String groupId, String groupName, String inviteeId, String inviteeName, String message) {
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
        log(object.toString());

        httpRequest(getRequest(SingleInvite.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 同意邀请
     *
     * @param inviteUUID 客户端从网易云通知接口获取的邀请记录的id
     * @param message    留言
     */
    public void approveInvite(String inviteUUID, String message) {
        handle(SingleInvite.class, APPROVE, inviteUUID, message);
    }

    /**
     * 拒绝邀请，只会返回成功与否，不会返回具体的 GroupInvitation 对象
     *
     * @param inviteUUID 客户端从网易云通知接口获取的邀请记录的id
     * @param message    留言
     */
    public void rejectInvite(String inviteUUID, String message) {
        handle(SingleInvite.class, REJECT, inviteUUID, message);
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
        log(object.toString());
        httpRequest(getRequest(resultType, url(action), object.toString(), HttpMethods.Post));
    }

}
