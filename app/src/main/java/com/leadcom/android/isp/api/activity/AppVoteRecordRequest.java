package com.leadcom.android.isp.api.activity;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.model.activity.vote.AppVoteRecord;
import com.litesuits.http.request.param.HttpMethods;
import com.netease.nim.uikit.business.contact.core.item.ItemTypes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>投票记录<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/07/01 17:45 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/07/01 17:45 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class AppVoteRecordRequest extends Request<AppVoteRecord> {

    public static AppVoteRecordRequest request() {
        return new AppVoteRecordRequest();
    }

    private class SingleVoteRecord extends SingleQuery<AppVoteRecord> {
    }

    private static final String RECORD = "/activity/actVote";
    private static final String TEAM = "/communication/commVoteRecord";

    @Override
    protected String url(String action) {
        return format("%s%s", RECORD, action);
    }

    @Override
    protected Class<AppVoteRecord> getType() {
        return AppVoteRecord.class;
    }

    @Override
    public AppVoteRecordRequest setOnSingleRequestListener(OnSingleRequestListener<AppVoteRecord> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public AppVoteRecordRequest setOnMultipleRequestListener(OnMultipleRequestListener<AppVoteRecord> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 投票
     */
    public void add(String setupId, ArrayList<String> itemIdList) {
        // {setupId,itemId}
        JSONObject object = new JSONObject();
        try {
            object.put("itemIdList", new JSONArray(itemIdList))
                    .put("setupId", setupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpRequest(getRequest(SingleVoteRecord.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 投票
     */
    public void add(AppVoteRecord record) {
        JSONObject object = new JSONObject();
        try {
            object.put("itemIdList", new JSONArray(record.getItemIdList()))
                    .put("setupId", record.getSetupId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpRequest(getRequest(SingleVoteRecord.class, url(ADD), object.toString(), HttpMethods.Post));
    }


    /**
     * 投票
     */
    public void addTeamVoteRecord(AppVoteRecord record) {
        JSONObject object = new JSONObject();
        try {
            object.put("itemIdList", new JSONArray(record.getItemIdList()))
                    .put("voteId", record.getVoteId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpRequest(getRequest(SingleVoteRecord.class, format("%s%s", TEAM, ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 投票
     */
    public void addTeamVoteRecord(String voteId, ArrayList<String> itemIdList) {
        // {setupId,itemId}
        JSONObject object = new JSONObject();
        try {
            object.put("itemIdList", new JSONArray(itemIdList))
                    .put("voteId", voteId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpRequest(getRequest(SingleVoteRecord.class, format("%s%s", TEAM, ADD), object.toString(), HttpMethods.Post));
    }

}
