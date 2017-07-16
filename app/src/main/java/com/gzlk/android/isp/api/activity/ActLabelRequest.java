package com.gzlk.android.isp.api.activity;

import android.support.annotation.NonNull;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.OnlyQueryList;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.model.activity.Label;
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

public class ActLabelRequest extends Request<Label> {

    public static ActLabelRequest request() {
        return new ActLabelRequest();
    }

    private static class SingleLabel extends Output<Label> {
    }

    private static class MultipleLabel extends Query<Label> {
    }

    private static class OnlyQueryListLabel extends OnlyQueryList<Label> {
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
    public ActLabelRequest setOnSingleRequestListener(OnSingleRequestListener<Label> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public ActLabelRequest setOnMultipleRequestListener(OnMultipleRequestListener<Label> listListener) {
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

        httpRequest(getRequest(SingleLabel.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 列举活动标签
     */
    public void list(@NonNull String activityId) {
        // actId=""
        httpRequest(getRequest(MultipleLabel.class, format("%s?actId=%s", url(LIST), activityId), "", HttpMethods.Get));
    }

    /**
     * 获取服务器上的热门标签
     */
    public void getTopLabels(int topSet) {
        httpRequest(getRequest(OnlyQueryListLabel.class, format("%s?top=%d", "/ontolog/getLabelTop", topSet), "", HttpMethods.Get));
    }
}
