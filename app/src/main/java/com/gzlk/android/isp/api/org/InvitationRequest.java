package com.gzlk.android.isp.api.org;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.model.organization.Invitation;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>群/组织邀请<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/19 21:50 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/19 21:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class InvitationRequest extends Request<Invitation> {

    public static InvitationRequest request() {
        return new InvitationRequest();
    }

    private static class SingleInvite extends Output<Invitation> {
    }

    private static class MultipleInvite extends Query<Invitation> {
    }

    // 邀请成员
    private static final String INVITE_GROUP = "/group/groInv";
    private static final String INVITE_SQUAD = "/group/groSquInv";
    private static final String INVITE_ACTIVITY = "/activity/invitation";

    /**
     * 同意
     */
    static final String APPROVE = "/approve";
    /**
     * 拒绝
     */
    static final String REJECT = "/reject";

    @Override
    protected String url(String action) {
        return format("%s%s", INVITE_GROUP, action);
    }

    private String url(String path, String action) {
        return format("%s%s", path, action);
    }

    @Override
    protected Class<Invitation> getType() {
        return Invitation.class;
    }

    @Override
    public InvitationRequest setOnSingleRequestListener(OnSingleRequestListener<Invitation> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public InvitationRequest setOnMultipleRequestListener(OnMultipleRequestListener<Invitation> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 邀请某人加入组织
     *
     * @param groupId   要加入的组织id
     * @param inviteeId 被邀请人的id
     * @param message   申请、审核人的留言
     */
    public void inviteToGroup(String groupId, String inviteeId, String message) {
        //{groupId,inviteeId,msg,accessToken}
        JSONObject object = new JSONObject();
        try {
            object.put("groupId", groupId)
                    .put("inviteeId", inviteeId)
                    .put("msg", checkNull(message));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleInvite.class, url(INVITE_GROUP, ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 邀请某人加入小组
     *
     * @param squadId   要加入的小组的id
     * @param inviteeId 被邀请人的userId
     * @param message   申请、审核人的留言
     */
    public void inviteToSquad(String squadId, String inviteeId, String message) {
        // {groSquId,inviteeId,msg,accessToken}
        JSONObject object = new JSONObject();
        try {
            object.put("groSquId", squadId)
                    .put("inviteeId", inviteeId)
                    .put("msg", checkNull(message));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleInvite.class, url(INVITE_SQUAD, ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 同意接受邀请加入组织
     *
     * @param inviteUUID 客户端从网易云通知接口获取的邀请记录的id
     * @param message    留言
     */
    public void agreeInviteToGroup(String inviteUUID, String message) {
        handle(INVITE_GROUP, APPROVE, inviteUUID, message);
    }

    /**
     * 拒绝加入组织的邀请，只会返回成功与否，不会返回具体的 GroupInvitation 对象
     *
     * @param inviteUUID 客户端从网易云通知接口获取的邀请记录的id
     * @param message    留言
     */
    public void disagreeInviteToGroup(String inviteUUID, String message) {
        handle(INVITE_GROUP, REJECT, inviteUUID, message);
    }

    /**
     * 同意接受加入小组的邀请
     *
     * @param inviteUUID 客户端从网易云通知接口获取的邀请记录的id
     * @param message    留言
     */
    public void agreeInviteToSquad(String inviteUUID, String message) {
        handle(INVITE_SQUAD, APPROVE, inviteUUID, message);
    }

    /**
     * 拒绝加入小组的邀请，只会返回成功与否，不会返回具体的 GroupInvitation 对象
     *
     * @param inviteUUID 客户端从网易云通知接口获取的邀请记录的id
     * @param message    留言
     */
    public void disagreeInviteToSquad(String inviteUUID, String message) {
        handle(INVITE_SQUAD, REJECT, inviteUUID, message);
    }

    private void handle(String path, String action, String uuid, String message) {
        JSONObject object = new JSONObject();
        try {
            object.put("uuid", uuid)
                    .put("msg", checkNull(message));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpRequest(getRequest(SingleInvite.class, url(path, action), object.toString(), HttpMethods.Post));
    }

    /**
     * 批量邀请用户到活动中来
     */
    public void activityInvite(String activityId, ArrayList<String> userIds) {
        // actId,inviteeIds,msg
        JSONObject object = new JSONObject();
        try {
            object.put("actId", activityId)
                    .put("inviteeIdList", new JSONArray(userIds));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpRequest(getRequest(SingleInvite.class, url(INVITE_ACTIVITY, ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 同意加入活动
     */
    public static final int INVITE_HANDLE_AGREE = 1;
    /**
     * 暂不加入活动
     */
    public static final int INVITE_HANDLE_DISAGREE = 2;

    /**
     * 处理活动邀请
     * <br/>(2017/07/14 17:00更改)
     *
     * @param tid 云信聊天群ID
     * @param ope 操作类型(1.同意加入活动,2.暂不加入活动)
     * @param msg 附加消息
     */
    public void activityInviteHandle(String tid, int ope, String msg) {
        // tid,ope,msg
        if (ope < INVITE_HANDLE_AGREE || ope > INVITE_HANDLE_DISAGREE) {
            throw new IllegalArgumentException("cannot support handle ope value: " + ope);
        }
        String param = format("%s?tid=%s&ope=%d%s", url(INVITE_ACTIVITY, "/handle"), tid, ope, (isEmpty(msg) ? "" : format("&msg=%s", msg)));
        httpRequest(getRequest(SingleInvite.class, param, "", HttpMethods.Get));
    }

    /**
     * 查询未处理的活动(活动首页)
     * <br/>(2017/07/14 17:00更改)
     */
    public void activityInviteNotHandled(String groupId) {
        String param = format("%s?groupId=%s", url(INVITE_ACTIVITY, "/list/notHandle"), groupId);
        httpRequest(getRequest(MultipleInvite.class, param, "", HttpMethods.Get));
    }

    /**
     * 查询未参加的活动（包括暂不参加和未处理的）
     * <br/>(2017/07/14 17:00更改)
     */
    @Deprecated
    public void activityInviteNotApproved(String groupId) {
        String param = format("%s?groupId=%s", url(INVITE_ACTIVITY, "/list/notApprove"), groupId);
        httpRequest(getRequest(MultipleInvite.class, param, "", HttpMethods.Get));
    }
}
