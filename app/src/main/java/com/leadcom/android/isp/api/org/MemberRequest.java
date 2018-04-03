package com.leadcom.android.isp.api.org;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.ListQuery;
import com.leadcom.android.isp.api.query.PaginationQuery;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.model.organization.Member;
import com.leadcom.android.isp.model.organization.Role;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    public static MemberRequest request() {
        return new MemberRequest();
    }

    private static class SingleMember extends SingleQuery<Member> {
    }

    private static class MultipleMember extends PaginationQuery<Member> {
    }

    private static class ListQueryMember extends ListQuery<Member> {
    }

    // 成员
    private static final String GROUP_MEMBER = "/group/groMember";
    private static final String SQUAD_MEMBER = "/group/groSquMember";
    private static final String ACTIVITY_MEMBER = "/activity/actMember";
    private static final String TOPIC_MEMBER = "/activity/actTopicMember";
    private static final String TEAM_MEMBER = "/communication/commMember";

    @Override
    protected String url(String action) {
        return GROUP_MEMBER + action;
    }

    private String url(int type, String action) {
        String api;
        switch (type) {
            case Member.Type.SQUAD:
                api = SQUAD_MEMBER;
                break;
            case Member.Type.ACTIVITY:
                api = ACTIVITY_MEMBER;
                break;
            case Member.Type.TOPIC:
                api = TOPIC_MEMBER;
                break;
            case Member.Type.TEAM:
                api = TEAM_MEMBER;
                break;
            case Member.Type.GROUP:
            default:
                api = GROUP_MEMBER;
                break;
        }
        return format("%s%s", api, action);
    }

    private void saveMemberRole(Member member) {
        if (null != member) {
            if (null != member.getGroRole()) {
                Role.save(member.getGroRole());
                Role role = member.getGroRole();
                member.setRoleId(role.getId());
                member.setRoleName(role.getRoleName());
            }
        }
    }

    @Override
    protected void save(Member member) {
        saveMemberRole(member);
        super.save(member);
    }

    @Override
    protected void save(List<Member> list) {
        if (null != list && list.size() > 0) {
            for (Member member : list) {
                saveMemberRole(member);
            }
        }
        super.save(list);
    }

    @Override
    protected Class<Member> getType() {
        return Member.class;
    }

    @Override
    public MemberRequest setOnSingleRequestListener(OnSingleRequestListener<Member> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public MemberRequest setOnMultipleRequestListener(OnMultipleRequestListener<Member> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    private String getOrgId(int type) {
        switch (type) {
            case Member.Type.SQUAD:
                return "squadId";
            case Member.Type.ACTIVITY:
                return "actId";
            case Member.Type.TOPIC:
                return "actTopicId";
            default:
                return "groupId";
        }
    }

    private String getMemberId(int type) {
        switch (type) {
            case Member.Type.ACTIVITY:
            case Member.Type.TOPIC:
                return "id";
            default:
                return "memberId";
        }
    }

    /**
     * 更改组织成员的属性
     *
     * @param memberId 组织成员ID
     * @param toRole   要变更成的角色
     */
    public void groupMemberUpdate(String memberId, Role toRole) {
        // _id,{groRole{_id:角色ID,rolCode:角色编码,rolName:角色名称}}

        JSONObject object = new JSONObject();
        try {
            object.put("_id", memberId);
            JSONObject role = new JSONObject();
            role.put("_id", toRole.getId())
                    .put("rolCode", toRole.getRolCode())
                    .put("rolName", toRole.getRoleName());
            object.put("groRole", role);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleMember.class, url(UPDATE), object.toString(), HttpMethods.Post));
    }

    /**
     * 删除组织成员
     */
    public void groupMemberDelete(String memberId) {
        // memberId,groupId
        String param = format("%s?memberId=%s", url(Member.Type.GROUP, DELETE), memberId);
        httpRequest(getRequest(SingleMember.class, param, "", HttpMethods.Get));
    }

    /**
     * 删除小组成员
     */
    public void squadMemberDelete(String memberId) {
        String param = format("%s?memberId=%s", url(Member.Type.SQUAD, DELETE), memberId);
        httpRequest(getRequest(SingleMember.class, param, "", HttpMethods.Get));
    }

    /**
     * 查找指定组织和指定小组内的成员列表
     *
     * @param type       要查询的类型，参见 {@link Member.Type}
     * @param id         组织的id
     * @param pageNumber 页码
     * @see Member.Type
     */
    public void list(int type, String id, int pageNumber) {
        String param = format("%s?%s=%s&pageNumber=%d", url(type, LIST), getOrgId(type), id, pageNumber);
        httpRequest(getRequest(MultipleMember.class, param, "", HttpMethods.Get));
    }

    /**
     * 查询单个成员的详细信息
     */
    public void find(int type, String memberId) {
        httpRequest(getRequest(SingleMember.class, format("%s?%s=%s", url(type, FIND), getMemberId(type), memberId), "", HttpMethods.Get));
    }

    /**
     * 在指定组织或小组内查看用户的信息
     *
     * @param type   成员类型（组织成员或小组成员）
     * @param orgId  组织id或小组id
     * @param userId 用户id
     */
    public void find(int type, String orgId, String userId) {
        String action = type == Member.Type.SQUAD ? "/squAndUserId" : "/groAndUserId";
        String url = format("%s%s?%s=%s&userId=%s", url(type, FIND), action, getOrgId(type), orgId, userId);
        httpRequest(getRequest(SingleMember.class, url, "", HttpMethods.Get));
    }

    /**
     * 在指定组织和指定小组内搜索成员名字
     */
    public void search(int type, String id, String memberName, int pageNumber) {
        String param = format("%s?%s=%s&info=%s&pageNumber=%d", url(type, SEARCH), getOrgId(type), id, memberName, pageNumber);
        httpRequest(getRequest(MultipleMember.class, param, "", HttpMethods.Get));
    }

    /**
     * 加入公开的活动
     */
    public void joinPublicActivity(String activityId) {
        String param = format("%s?actId=%s", url(Member.Type.ACTIVITY, "/joinPublicAct"), activityId);
        httpRequest(getRequest(SingleMember.class, param, "", HttpMethods.Get));
    }

    /**
     * 活动中踢人
     */
    public void activityKickOut(String activityId, String userId) {
        String param = format("%s?actId=%s&userId=%s", url(Member.Type.ACTIVITY, DELETE), activityId, userId);
        httpRequest(getRequest(SingleMember.class, param, "", HttpMethods.Get));
    }

    /**
     * 成员退出活动
     */
    public void activityExit(String activityId) {
        String param = format("%s?id=%s", url(Member.Type.ACTIVITY, "/exit"), activityId);
        httpRequest(getRequest(SingleMember.class, param, "", HttpMethods.Get));
    }

    /**
     * 从组织里添加小组成员且不需要对方同意或拒绝(2017-06-26 21:34新增)
     */
    public void addToSquadFromGroup(String userId, String squadId) {
        ArrayList<String> ids = new ArrayList<>();
        ids.add(userId);
        addToSquadFromGroup(squadId, ids);
    }

    /**
     * 从组织里添加小组成员且不需要对方同意或拒绝(可同时添加多个)<br/>
     * 更改方法为POST，参数改为json(2017-09-12 08:00)
     */
    public void addToSquadFromGroup(String squadId, ArrayList<String> userIdList) {
        // {squadId,[userIdList]}

        JSONObject object = new JSONObject();
        try {
            object.put("squadId", squadId);
            object.put("userIdList", new JSONArray(userIdList));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleMember.class, url(Member.Type.SQUAD, ADD), object.toString(), HttpMethods.Post));
    }


    /**
     * 拉取我的联系人列表，全部已关注的组织成员
     */
    public void listAllGroup() {
        httpRequest(getRequest(ListQueryMember.class, format("/user/user/list/allGroup", url(LIST)), "", HttpMethods.Get));
    }

    /**
     * 为群聊增加成员
     */
    public void addTeamMember(String teamId, ArrayList<String> memberIds) {

        JSONObject object = new JSONObject();
        try {
            object.put("tid", teamId);
            object.put("userIdList", new JSONArray(memberIds));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleMember.class, url(Member.Type.TEAM, ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 管理员移除成员
     */
    public void removeTeamMember(String tid, String userId) {
        String param = format("%s?tid=%s&userId=%s", url(Member.Type.TEAM, DELETE), tid, userId);
        httpRequest(getRequest(SingleMember.class, param, "", HttpMethods.Get));
    }

    /**
     * 成员退出群聊
     */
    public void exitTeam(String tid) {
        String param = format("%s?tid=%s", url(Member.Type.TEAM, EXIT), tid);
        httpRequest(getRequest(SingleMember.class, param, "", HttpMethods.Get));
    }

    /**
     * 转让群聊管理权
     */
    public void grantManager(String tid, String userId) {
        String param = format("%s/manager?tid=%s&userId=%s", url(Member.Type.TEAM, UPDATE), tid, userId);
        httpRequest(getRequest(SingleMember.class, param, "", HttpMethods.Get));
    }
}
