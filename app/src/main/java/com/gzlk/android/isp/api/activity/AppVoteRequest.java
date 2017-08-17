package com.gzlk.android.isp.api.activity;

import com.gzlk.android.isp.api.query.SingleQuery;
import com.gzlk.android.isp.api.query.PaginationQuery;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.model.activity.vote.AppVote;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONArray;
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

    private static class SingleVote extends SingleQuery<AppVote> {
    }

    private static class MultipleVote extends PaginationQuery<AppVote> {
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
        appVote.saveVoteRecords();
        super.save(appVote);
    }

    @Override
    protected void save(List<AppVote> list) {
        for (AppVote vote : list) {
            vote.saveVoteItems();
            vote.saveVoteRecords();
        }
        super.save(list);
    }

    /**
     * 添加一个投票应用
     */
    public void add(AppVote appVote) {
        // {actId,title,endDate,maxSelectable,anonymity,authPublic,[itemContentList]}

        JSONObject object = new JSONObject();
        try {
            object.put("actId", appVote.getActId())         // 活动ID
                    .put("title", appVote.getTitle())       // 投票标题
                    //.put("content", appVote.getContent())   // 投票描述
                    //.put("type", appVote.getType())         // 类型(1.单选,2.多选)
                    .put("maxSelectable", appVote.getMaxSelectable())   // 投票选项的最大可选数(单选默认为1)
                    .put("anonymity", appVote.getAnonymity())           // 投票是否记名(0.不记名,1.记名)
                    .put("authPublic", appVote.getAuthPublic())         // 投票是否公开结果(0.不公开,1.公开)
                    .put("endDate", appVote.getEndDate())
                    .put("itemContentList", new JSONArray(appVote.getItemContentList()));
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
     * 查询投票设置
     */
    public static final int FIND_VOTE = 1;
    /**
     * 查询投票设置和投票选项
     */
    public static final int FIND_ITEM = 2;
    /**
     * 查询投票设置、选项、投票记录
     */
    public static final int FIND_ALL = 3;
    /**
     * 4.投票设置信息,投票选项列表和当前用户投票记录
     */
    public static final int FIND_MY = 4;

    /**
     * 查询单个投票设置(包括其所有投票选项和选项得票数)
     *
     * @param voteId     投票应用id
     * @param ope        <ul>
     *                   <li>1.表示只查询投票设置对象</li>
     *                   <li>2.表示同时查询投票设置对象和投票选项</li>
     *                   <li>3.表示查询投票设置对象、投票选项和所有投票人的投票记录</li>
     *                   <li>4.投票设置信息,投票选项列表和当前用户投票记录</li>
     *                   </ul>
     * @param pageNumber pageNum:第几页;仅当ope为3时，分页入参有效
     */
    public void find(String voteId, int ope, int pageNumber) {
        // id="",ope="",pageSize="",pageNum=""
        httpRequest(getRequest(SingleVote.class, format("%s?id=%s&ope=%d&&pageNumber=%d", url(FIND), voteId, ope, pageNumber), "", HttpMethods.Get));
    }

    /**
     * 查询活动中的所有投票
     */
    public static final int LIST_ALL = 1;
    /**
     * 查询活动中正在进行中的投票
     */
    public static final int LIST_ACT = 2;
    /**
     * 查询活动中已结束的投票
     */
    public static final int LIST_END = 3;

    /**
     * 查询某个活动中的投票（标题、名称）列表
     *
     * @param activityId 活动ID
     * @param ope        操作类型(1.所有活动投票设置,2.进行中的活动投票设置,3.已结束的活动投票设置)
     * @param pageNumber 页码
     */
    public void list(String activityId, int ope, int pageNumber) {
        // actId,ope,pageSize,pageNumber
        httpRequest(getRequest(MultipleVote.class, format("%s?actId=%s&ope=%d&pageNumber=%d", url(LIST), activityId, ope, pageNumber), "", HttpMethods.Get));
    }
}
