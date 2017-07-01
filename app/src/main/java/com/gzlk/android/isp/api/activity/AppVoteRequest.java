package com.gzlk.android.isp.api.activity;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.model.activity.AppVote;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * <b>功能描述：</b>活动应用：投票<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/29 16:28 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/29 16:28 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class AppVoteRequest extends Request<AppVote> {

    public static AppVoteRequest request() {
        return new AppVoteRequest();
    }

    private static class SingleVote extends Output<AppVote> {
    }

    private static class MultipleVote extends Query<AppVote> {
    }

    private static final String VOTE = "/activity/actVoteSetup";

    @Override
    protected String url(String action) {
        return format("%s%s", VOTE, action);
    }

    @Override
    protected Class<AppVote> getType() {
        return AppVote.class;
    }

    @Override
    public AppVoteRequest setOnSingleRequestListener(OnSingleRequestListener<AppVote> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public AppVoteRequest setOnMultipleRequestListener(OnMultipleRequestListener<AppVote> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    @Override
    protected void save(AppVote appVote) {
        appVote.saveVoteItems();
        super.save(appVote);
    }

    @Override
    protected void save(List<AppVote> list) {
        for (AppVote vote : list) {
            vote.saveVoteItems();
        }
        super.save(list);
    }

    /**
     * 添加一个投票应用
     */
    public void add(AppVote appVote) {
        // {actId:"",title:"",desc:"",endTime:"",type:""}

        JSONObject object = new JSONObject();
        try {
            object.put("actId", appVote.getActId())
                    .put("title", appVote.getTitle())
                    .put("desc", appVote.getDesc())
                    .put("type", appVote.getType())
                    .put("endTime", appVote.getEndTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpRequest(getRequest(SingleVote.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 删除一个投票应用
     */
    public void delete(String voteId) {
        // id=""
        httpRequest(getRequest(SingleVote.class, format("%s?id=%s", url(DELETE), voteId), "", HttpMethods.Get));
    }

    /**
     * 查询单个投票设置(包括其所有投票选项和选项得票数)
     *
     * @param voteId     投票应用id
     * @param ope        ope：1.表示只查询投票设置对象，2.表示同时查询投票设置对象和投票选项；3.表示查询投票设置对象、投票选项和所有投票人的投票记录
     * @param pageNumber pageNum:第几页;仅当ope为3时，分页入参有效
     */
    public void find(String voteId, int ope, int pageNumber) {
        // id="",ope="",pageSize="",pageNum=""
        httpRequest(getRequest(SingleVote.class, format("%s?id=%s&ope=%d&&pageNum=%d", url(FIND), voteId, ope, pageNumber), "", HttpMethods.Get));
    }

    /**
     * 查询某个活动中的投票（标题、名称）列表
     */
    public void list(String activityId) {
        // actId=""
        httpRequest(getRequest(MultipleVote.class, format("%s?actId=%s", url(LIST), activityId), "", HttpMethods.Get));
    }
}
