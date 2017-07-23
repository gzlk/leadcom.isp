package com.gzlk.android.isp.api.activity;

import com.gzlk.android.isp.api.query.SingleQuery;
import com.gzlk.android.isp.api.query.PaginationQuery;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.model.activity.vote.AppVoteItem;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/29 21:28 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/29 21:28 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class AppVoteItemRequest extends Request<AppVoteItem> {

    public static AppVoteItemRequest request() {
        return new AppVoteItemRequest();
    }

    private static class SingleVoteItem extends SingleQuery<AppVoteItem> {
    }

    private static class MultipleVoteItem extends PaginationQuery<AppVoteItem> {
    }

    private static final String VOTE = "/activity/actVoteItem";

    @Override
    protected String url(String action) {
        return format("%s%s", VOTE, action);
    }

    @Override
    protected Class<AppVoteItem> getType() {
        return AppVoteItem.class;
    }

    @Override
    public AppVoteItemRequest setOnSingleRequestListener(OnSingleRequestListener<AppVoteItem> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public AppVoteItemRequest setOnMultipleRequestListener(OnMultipleRequestListener<AppVoteItem> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 新增投票选项
     */
    public void add(String voteId, String desc) {
        // {setupId:"",desc:""}

        JSONObject object = new JSONObject();
        try {
            object.put("setupId", voteId)
                    .put("desc", desc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpRequest(getRequest(SingleVoteItem.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 更新指定投票选项
     */
    public void update(String itemId, String desc) {
        // {id:"",desc:""}
        JSONObject object = new JSONObject();
        try {
            object.put("id", itemId)
                    .put("desc", desc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpRequest(getRequest(SingleVoteItem.class, url(UPDATE), object.toString(), HttpMethods.Post));
    }

    /**
     * 删除指定的投票选项
     */
    public void delete(String itemId) {
        // id=""
        httpRequest(getRequest(SingleVoteItem.class, format("%s?id=%s", url(DELETE), itemId), "", HttpMethods.Get));
    }

    /**
     * 投票
     */
    public void vote(String itemId) {
        // {itemId:""}
        JSONObject object = new JSONObject();
        try {
            object.put("itemId", itemId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpRequest(getRequest(SingleVoteItem.class, "/activity/actVote/add", object.toString(), HttpMethods.Post));
    }
}
