package com.leadcom.android.isp.api.org;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.PageQuery;
import com.leadcom.android.isp.model.organization.MemberClassify;
import com.leadcom.android.isp.model.organization.MemberNature;
import com.leadcom.android.isp.model.organization.SimpleNature;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * <b>功能描述：</b>组织成员属性相关api<br />
 * <b>创建作者：</b>SYSTEM <br />
 * <b>创建时间：</b>2018/07/30 09:40 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class NatureRequest extends Request<MemberClassify> {

    public static NatureRequest request() {
        return new NatureRequest();
    }

    private static class PageNature extends PageQuery<MemberClassify> {
    }

    @Override
    protected String url(String action) {
        return "/user/appUserNatureClassify" + action;
    }

    @Override
    protected Class<MemberClassify> getType() {
        return MemberClassify.class;
    }

    @Override
    public NatureRequest setOnSingleRequestListener(OnSingleRequestListener<MemberClassify> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public NatureRequest setOnMultipleRequestListener(OnMultipleRequestListener<MemberClassify> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 列举组织成员的属性信息
     */
    public void listBy(String groupId, String userId, String type) {
        boolean user = !isEmpty(userId);
        String tp = type.equals(MemberNature.NatureType.TEXT) ? (user ? "/userByText" : "/groupByText") : (user ? "/userByTime" : "/groupByTime");
        executeHttpRequest(getRequest(PageNature.class, url(LIST) + tp + "?groupId=" + groupId + (user ? ("&userId=" + userId) : ""), "", HttpMethods.Get));
    }

    /**
     * 更新组织成员的属性
     */
    public void updateUserNatures(String groupId, String userId, ArrayList<SimpleNature> list) {
        JSONObject object = new JSONObject();
        try {
            object.put("groupId", groupId)
                    .put("userId", userId)
                    .put("natureList", new JSONArray(SimpleNature.toJson(list)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        executeHttpRequest(getRequest(PageNature.class, "/user/appUserNature/add", object.toString(), HttpMethods.Post));
    }
}
