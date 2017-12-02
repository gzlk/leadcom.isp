package com.leadcom.android.isp.api.activity;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.model.activity.vote.AppVoteItem;
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
    public void add(String voteId, String content) {
        // {setupId:"",desc:""}

        JSONObject object = new JSONObject();
        try {
            object.put("setupId", voteId)
                    .put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpRequest(getRequest(SingleVoteItem.class, url(ADD), object.toString(), HttpMethods.Post));
    }
}
