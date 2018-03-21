package com.leadcom.android.isp.api.archive;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.ListQuery;
import com.leadcom.android.isp.api.query.PaginationQuery;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.archive.RecommendArchive;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * <b>功能描述：</b>组织的推荐档案<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/17 13:47 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/17 13:47 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class RecommendArchiveRequest extends Request<RecommendArchive> {

    public static RecommendArchiveRequest request() {
        return new RecommendArchiveRequest();
    }

    private static class SingleRecommend extends SingleQuery<RecommendArchive> {
    }

    private static class MultipleRecommend extends PaginationQuery<RecommendArchive> {
    }

    private static class ListRecommend extends ListQuery<RecommendArchive> {
    }

    private static final String RECOMMEND = "/group/groDocRcmd";

    @Override
    protected String url(String action) {
        return format("%s%s", RECOMMEND, action);
    }

    @Override
    protected Class<RecommendArchive> getType() {
        return RecommendArchive.class;
    }

    @Override
    public RecommendArchiveRequest setOnSingleRequestListener(OnSingleRequestListener<RecommendArchive> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public RecommendArchiveRequest setOnMultipleRequestListener(OnMultipleRequestListener<RecommendArchive> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    private Dao<Archive> dao;

    private void saveArchive(RecommendArchive recommend) {
        if (null != recommend) {
            if (null != recommend.getGroDoc()) {
                saveArchive(recommend.getGroDoc());
            }
            if (null != recommend.getUserDoc()) {
                saveArchive(recommend.getUserDoc());
            }
        }
    }

    private void saveArchive(Archive archive) {
        if (null != archive) {
            archive.resetAdditional(archive.getAddition());
            if (null == dao) {
                dao = new Dao<>(Archive.class);
            }
            dao.save(archive);
        }
    }

    @Override
    protected void save(List<RecommendArchive> list) {
        if (null != list) {
            for (RecommendArchive recommend : list) {
                saveArchive(recommend);
            }
        }
        super.save(list);
    }

    @Override
    protected void save(RecommendArchive recommendArchive) {
        saveArchive(recommendArchive);
        super.save(recommendArchive);
    }

    /**
     * 推荐档案
     *
     * @param type             组织档案推荐类型(1.组织档案,2.个人档案)
     * @param groupId          推荐档案的组织ID
     * @param archiveId        档案ID(组织档案ID或个人档案ID)
     * @param archiveCreatorId 档案创建者的用户ID
     */
    public void recommend(int type, String groupId, String archiveId, String archiveCreatorId) {
        // {type,groupId,userId,docId}
        JSONObject object = new JSONObject();
        try {
            object.put("type", type)
                    .put("groupId", groupId)
                    .put("userId", archiveCreatorId)
                    .put("docId", archiveId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpRequest(getRequest(SingleRecommend.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 取消推荐档案
     *
     * @param recommendedId 档案推荐的id
     */
    public void unRecommend(String recommendedId) {
        // groDocRcmdId
        String param = format("%s?groDocRcmdId=%s", url(DELETE), recommendedId);
        httpRequest(getRequest(SingleRecommend.class, param, "", HttpMethods.Get));
    }

    /**
     * 查询组织的待推荐档案列表
     */
    public void list(String groupId, int pageNumber) {
        // groupId,ope,pageSize,pageNumber
        String params = format("%s?%s&pageNumber=%d&groupId=%s", url(LIST), SUMMARY, pageNumber, groupId);
        httpRequest(getRequest(MultipleRecommend.class, params, "", HttpMethods.Get));
    }

    /**
     * 查询首页推荐的档案列表
     */
    public void list(int pageNumber) {
        String params = format("%s&pageNumber=%d", SUMMARY, pageNumber);
        httpRequest(getRequest(MultipleRecommend.class, format("/operate/recommend/list?%s", params), "", HttpMethods.Get));
    }

    /**
     * 首页推荐档案列表
     */
    public void front(int pageNumber) {
        String params = format("%s?%s&pageNumber=%d", url(LIST + "/front"), SUMMARY, pageNumber);
        httpRequest(getRequest(MultipleRecommend.class, params, "", HttpMethods.Get));
    }

    /**
     * 首页 - 推荐的档案列表
     */
    public void listHomeFeatured(int pageNumber) {
        String params = format("/group/groDocRcmd/list/front?pageNumber=%d", pageNumber);
        httpRequest(getRequest(MultipleRecommend.class, params, "", HttpMethods.Get));
    }

    /**
     * 首页头条推荐内容
     */
    public void focusImage() {
        directlySave = false;
        httpRequest(getRequest(ListRecommend.class, "/operate/focusImage/list/all", "", HttpMethods.Get));
    }
}
