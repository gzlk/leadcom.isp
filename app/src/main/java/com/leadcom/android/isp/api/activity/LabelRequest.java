package com.leadcom.android.isp.api.activity;

import android.support.annotation.NonNull;

import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.api.query.PaginationQuery;
import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.query.ListQuery;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.model.archive.Label;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <b>功能描述：</b>活动标签<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/26 00:08 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/26 00:08 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class LabelRequest extends Request<Label> {

    public static LabelRequest request() {
        return new LabelRequest();
    }

    private static class SingleLabel extends SingleQuery<Label> {
    }

    private static class MultipleLabel extends PaginationQuery<Label> {
    }

    private static class ListQueryLabel extends ListQuery<Label> {
    }

    @Override
    protected String url(String action) {
        return format("/activity/label%s", action);
    }

    @Override
    protected Class<Label> getType() {
        return Label.class;
    }

    @Override
    public LabelRequest setOnSingleRequestListener(OnSingleRequestListener<Label> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public LabelRequest setOnMultipleRequestListener(OnMultipleRequestListener<Label> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 增加活动标签
     */
    public void add(@NonNull String name, @NonNull String activityId, @NonNull String groupId) {
        // {name:"",actId:"",groupId:""}

        JSONObject object = new JSONObject();
        try {
            object.put("name", name)
                    .put("actId", activityId)
                    .put("groupId", groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        executeHttpRequest(getRequest(SingleLabel.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 列举活动标签
     */
    public void list(@NonNull String activityId) {
        // actId=""
        executeHttpRequest(getRequest(MultipleLabel.class, format("%s?actId=%s", url(LIST), activityId), "", HttpMethods.Get));
    }

    /**
     * 获取服务器上的热门标签
     */
    public void getTopLabels(int topSet) {
        executeHttpRequest(getRequest(ListQueryLabel.class, format("%s?top=%d", "/ontolog/getLabelTop", topSet), "", HttpMethods.Get));
    }

    /**
     * 查询个人常用标签
     */
    public void getUserLabels(int topSet) {
        executeHttpRequest(getRequest(ListQueryLabel.class, format("%s?top=%d", "/user/userLabel/top", topSet), "", HttpMethods.Get));
    }
}
