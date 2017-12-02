package com.leadcom.android.isp.api.archive;

import android.support.annotation.NonNull;

import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.api.query.PaginationQuery;
import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.model.user.Privacy;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <b>功能描述：</b>隐私设置<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/17 00:09 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/17 00:09 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class PrivacyRequest extends Request<Privacy> {

    public static PrivacyRequest request() {
        return new PrivacyRequest();
    }

    private static class SinglePrivacy extends SingleQuery<Privacy> {
    }

    private static class MultiplePrivacy extends PaginationQuery<Privacy> {
    }

    @Override
    protected String url(String action) {
        return format("/user/userPri%s", action);
    }

    @Override
    protected Class<Privacy> getType() {
        return Privacy.class;
    }

    @Override
    public PrivacyRequest setOnSingleRequestListener(OnSingleRequestListener<Privacy> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public PrivacyRequest setOnMultipleRequestListener(OnMultipleRequestListener<Privacy> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 保存隐私设置
     *
     * @param status   隐私类型 1.公开 2.不公开 3.对某人公开 4.对某群体公开 {@link Privacy.Status}
     * @param source   1.个人资料；2.个人档案；3.个人日志 {@link Privacy.Source}
     * @param sourceId 个人资料\档案\日志记录的id
     * @param groupId  被公开的组织id（当档案向某组织公开时有用）
     * @param userId   公开的用户id（当档案向某用户公开时有用）
     * @see Privacy.Status
     * @see Privacy.Source
     */
    public void save(int status, int source, @NonNull String sourceId, String groupId, String userId) {
        // {accessToken:"",status:"",source:"",sourceId:"",groupId:"",userId:""}

        JSONObject object = new JSONObject();
        try {
            object.put("status", status)
                    .put("source", source)
                    .put("sourceId", sourceId)
                    .put("groupId", checkNull(groupId))
                    .put("userId", checkNull(userId));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SinglePrivacy.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 查询我的所有隐私设置记录
     */
    public void list() {
        // accessToken:""
        httpRequest(getRequest(MultiplePrivacy.class, url(LIST), "", HttpMethods.Get));
    }

    /**
     * 查找指定文档的隐私设置
     */
    public void find(String sourceId, String groupId, String userId) {

    }
}
