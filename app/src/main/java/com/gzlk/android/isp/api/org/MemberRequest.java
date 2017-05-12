package com.gzlk.android.isp.api.org;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnRequestListListener;
import com.gzlk.android.isp.api.listener.OnRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.organization.Invitation;
import com.gzlk.android.isp.model.organization.JoinGroup;
import com.gzlk.android.isp.model.organization.Member;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * <b>功能描述：</b>组织成员相关api<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/10 09:11 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/10 09:11 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MemberRequest extends Request<Member> {
    private static MemberRequest request;

    public static MemberRequest request() {
        if (null == request) {
            request = new MemberRequest();
        }
        return request;
    }

    static class SingleMember extends Output<Member> {
    }

    static class MultipleMember extends Query<Member> {
    }

    static class SingleInvite extends Output<Invitation> {
    }

    static class SingleJoin extends Output<JoinGroup> {
    }

    // 成员
    private static final String MEMBER = "/group/groMember";
    // 邀请成员
    private static final String INVITE = "/group/groInv/";
    // 主动加入组织
    private static final String JOIN = "/user/appToJoinGroup";
    /**
     * 同意
     */
    private static final String APPROVE = "/aprove";
    /**
     * 拒绝
     */
    private static final String REJECT = "/reject";

    @Override
    protected String url(String action) {
        return MEMBER + action;
    }

    private String url(String path, String action) {
        return path + action;
    }

    private String invite(String action) {
        return INVITE + action;
    }

    private String join(String action) {
        return JOIN + action;
    }

    @Override
    public MemberRequest setOnRequestListener(OnRequestListener<Member> listener) {
        onRequestListener = listener;
        return this;
    }

    @Override
    public MemberRequest setOnRequestListListener(OnRequestListListener<Member> listListener) {
        onRequestListListener = listListener;
        return this;
    }

    private String compound(String action, String groupId, String squadId) {
        String params = format("%s?groupId=%s", url(action), groupId);
        if (!StringHelper.isEmpty(squadId)) {
            params += format("&squadId=%s", squadId);
        }
        return params;
    }

    /**
     * 查找指定组织和指定小组内的成员列表
     */
    public void list(String groupId, String squadId) {
        httpRequest(getRequest(MultipleMember.class, compound(LIST, groupId, squadId), "", HttpMethods.Get));
    }

    /**
     * 在指定组织和指定小组内搜索成员名字
     */
    public void search(String groupId, String squadId, String memberName) {
        httpRequest(getRequest(MultipleMember.class, format("%s&info=%s", compound(SEARCH, groupId, squadId), memberName), "", HttpMethods.Get));
    }

    /**
     * 查询单个成员的详细信息
     */
    public void find(String memberId) {
        httpRequest(getRequest(SingleMember.class, format("%s?memberId=%s", url(FIND), memberId), "", HttpMethods.Get));
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
        add(SingleInvite.class, INVITE, groupId, groupName, inviteeId, inviteeName, message);
//        JSONObject object = new JSONObject();
//        try {
//            object.put("groupId", groupId)
//                    .put("groupName", groupName)
//                    .put("inviteeId", inviteeId)
//                    .put("inviteeName", inviteeName)
//                    .put("msg", checkNull(message))
//                    .put("accessToken", userToken);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        log(object.toString());
//
//        httpRequest(getRequest(SingleInvite.class, invite(ADD), object.toString(), HttpMethods.Post));
    }

    private void add(Type resultType, String path, String groupId, String groupName, String inviteeId, String inviteeName, String message) {
        JSONObject object = new JSONObject();
        try {
            object.put("groupId", groupId)
                    .put("groupName", groupName)
                    .put("inviteeId", inviteeId)
                    .put("inviteeName", inviteeName)
                    .put("msg", checkNull(message))
                    .put("accessToken", Cache.cache().userToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        log(object.toString());

        httpRequest(getRequest(resultType, url(path, ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 同意邀请
     *
     * @param inviteUUID 客户端从网易云通知接口获取的邀请记录的id
     * @param message    留言
     */
    public void approveInvite(String inviteUUID, String message) {
        handle(SingleInvite.class, INVITE, APPROVE, inviteUUID, message);
    }

    /**
     * 拒绝邀请，只会返回成功与否，不会返回具体的 GroupInvitation 对象
     *
     * @param inviteUUID 客户端从网易云通知接口获取的邀请记录的id
     * @param message    留言
     */
    public void rejectInvite(String inviteUUID, String message) {
        handle(SingleInvite.class, INVITE, REJECT, inviteUUID, message);
    }

    private void handle(Type resultType, String path, String action, String uuid, String message) {
        JSONObject object = new JSONObject();
        try {
            object.put("uuid", uuid)
                    .put("msg", checkNull(message))
                    .put("accessToken", Cache.cache().userToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        log(object.toString());
        httpRequest(getRequest(resultType, url(path, action), object.toString(), HttpMethods.Post));
    }

    /**
     * 申请加入组织，申请成功之后返回的是一个组织成员json对象
     */
    public void join(String groupId, String groupName, String inviteeId, String inviteeName, String message) {
        //{groupId:"",groupName:"",inviteeId:"",inviteeName:"",msg:"",accessToken:""}
        add(SingleMember.class, JOIN, groupId, groupName, inviteeId, inviteeName, message);
//        JSONObject object = new JSONObject();
//        try {
//            object.put("groupId", groupId)
//                    .put("groupName", groupName)
//                    .put("inviteeId", inviteeId)
//                    .put("inviteeName", inviteeName)
//                    .put("msg", checkNull(message))
//                    .put("accessToken", userToken);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        log(object.toString());
//
//        httpRequest(getRequest(SingleMember.class, join(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 同意成员加入组织，只返回成功与否
     *
     * @param requestUUID 客户端从网易云通知接口获取的申请记录的id
     * @param message     留言
     */
    public void approveJoin(String requestUUID, String message) {
        handle(SingleJoin.class, JOIN, APPROVE, requestUUID, message);
    }

    /**
     * 拒绝成员加入组织，只返回成功或失败
     *
     * @param requestUUID 客户端从网易云通知接口获取的申请记录的id
     * @param message     留言
     */
    public void rejectJoin(String requestUUID, String message) {
        handle(SingleJoin.class, JOIN, REJECT, requestUUID, message);
    }
}
