package com.leadcom.android.isp.nim.model.extension;

import com.leadcom.android.isp.lib.Json;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>投票消息类实体<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/30 23:37 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/30 23:37 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class VoteAttachment extends CustomAttachment {

    public VoteAttachment() {
        super(AttachmentType.VOTE);
        voteItems = new ArrayList<>();
    }

    private String voteId;
    private String title;
    private int maxVote;
    private ArrayList<String> voteItems;

    public String getVoteId() {
        if (isEmpty(voteId)) {
            voteId = getCustomId();
        }
        return voteId;
    }

    public void setVoteId(String voteId) {
        this.voteId = voteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getMaxVote() {
        return maxVote;
    }

    public void setMaxVote(int maxVote) {
        this.maxVote = maxVote;
    }

    public ArrayList<String> getVoteItems() {
        return voteItems;
    }

    public void setVoteItems(ArrayList<String> voteItems) {
        this.voteItems = voteItems;
    }

    @Override
    protected void parseData(JSONObject data) {
        super.parseData(data);
        try {
            if (data.has("voteId")) {
                voteId = data.getString("voteId");
            }
            if (data.has("title")) {
                title = data.getString("title");
            }
            if (data.has("maxVote")) {
                maxVote = data.getInt("maxVote");
            }
            voteItems = new ArrayList<>();
            if (data.has("voteItems")) {
                JSONArray array = data.getJSONArray("voteItems");
                for (int i = 0, len = array.length(); i < len; i++) {
                    voteItems.add(array.getString(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject packData() {
        JSONObject object = super.packData();
        try {
            object.put("voteId", voteId)
                    .put("title", title)
                    .put("maxVote", maxVote);
            String json = Json.gson().toJson(voteItems);
            object.put("voteItems", new JSONArray(json));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }
}
