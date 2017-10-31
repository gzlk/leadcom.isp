package com.gzlk.android.isp.api.user;

import android.support.annotation.NonNull;

import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.query.BoolQuery;
import com.gzlk.android.isp.api.query.PaginationQuery;
import com.gzlk.android.isp.api.query.SingleQuery;
import com.gzlk.android.isp.model.archive.ArchiveSource;
import com.gzlk.android.isp.model.user.Collection;
import com.gzlk.android.isp.model.user.Position;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>个人收藏相关api<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/04 11:01 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/04 11:01 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class CollectionRequest extends Request<Collection> {

    public static CollectionRequest request() {
        return new CollectionRequest();
    }

    private static class SingleCollection extends SingleQuery<Collection> {
    }

    private static class BoolCollection extends BoolQuery<Collection> {
    }

    private static class MultipleCollection extends PaginationQuery<Collection> {
    }

    private static final String COL = "/user/userCol";

    @Override
    protected String url(String action) {
        return COL + action;
    }

    @Override
    protected Class<Collection> getType() {
        return Collection.class;
    }

    @Override
    public CollectionRequest setOnSingleRequestListener(OnSingleRequestListener<Collection> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public CollectionRequest setOnMultipleRequestListener(OnMultipleRequestListener<Collection> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 添加个人收藏
     *
     * @param type             收藏类型，参考 {@link Collection.Type}(1.文本,2.文档,3.图片,4.视频,5.附件,6.音频,7.位置,11.个人档案,12.组织档案,13.个人动态)
     * @param content          收藏内容(文本或文件的URL,type=11/12/13时不传该参数)
     * @param creatorId        原作者用户ID
     * @param creatorName      原作者用户名称
     * @param creatorHeadPhoto 原作者用户头像
     * @param sourceType       收藏来源的类型(1.个人档案,2.组织档案,3.个人动态,4.活动聊天,5.议题聊天)
     * @param sourceId         收藏来源的ID(个人档案:个人档案ID,组织档案:组织档案ID,个人动态:个人动态ID,活动聊天:活动ID,议题聊天:议题ID)
     * @param sourceTitle      收藏来源的标题(title字段，个人动态不传)
     * @param label            标签
     * @see Collection.Type
     * @see ArchiveSource
     */
    public void add(int type, String content, @NonNull String creatorId, String creatorName, String creatorHeadPhoto,
                    int sourceType, String sourceId, String sourceTitle, ArrayList<String> label, Position position) {
        // {type,content,creatorId,creatorName,creatorHeadPhoto,sourceType,sourceId,sourceTitle,[label],{position}}

        JSONObject object = new JSONObject();
        try {
            object.put("type", type)
                    .put("content", checkNull(content))
                    .put("creatorId", checkNull(creatorId))
                    .put("creatorName", checkNull(creatorName))
                    .put("creatorHeadPhoto", checkNull(creatorHeadPhoto))
                    .put("sourceType", sourceType)
                    .put("sourceId", sourceId)
                    .put("sourceTitle", checkNull(sourceTitle))
                    .put("label", new JSONArray(null == label ? new ArrayList() : label))
                    .put("position", new JSONObject(Position.toJson(position)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleCollection.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    public void add(Collection collection, Position position) {
        // {type,content,creatorId,creatorName,creatorHeadPhoto,sourceType,sourceId,sourceTitle,[label],{position}}

        JSONObject object = new JSONObject();
        try {
            object.put("type", collection.getType())
                    .put("content", checkNull(collection.getContent()))
                    .put("creatorId", checkNull(collection.getCreatorId()))
                    .put("creatorName", checkNull(collection.getCreatorName()))
                    .put("creatorHeadPhoto", checkNull(collection.getCreatorHeadPhoto()))
                    .put("sourceType", collection.getSourceType())
                    .put("sourceId", collection.getSourceId())
                    .put("sourceTitle", checkNull(collection.getSourceTitle()))
                    .put("label", new JSONArray(collection.getLabel()))
                    .put("position", new JSONObject(Position.toJson(position)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleCollection.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 删除收藏
     */
    public void delete(String collectionId) {
        // colId
        httpRequest(getRequest(BoolCollection.class, format("%s?colId=%s", url(DELETE), collectionId), "", HttpMethods.Get));
    }

    /**
     * 更新收藏内容（只针对文本型收藏内容）
     */
    public void update(String collectionId, String content) {
        // {_id,content,accessToken}
        JSONObject object = new JSONObject();
        try {
            object.put("_id", collectionId)
                    .put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpRequest(getRequest(SingleCollection.class, url(UPDATE), object.toString(), HttpMethods.Post));
    }

    /**
     * 查找某个收藏的详细内容
     */
    public void find(String collectionId) {
        // colId
        httpRequest(getRequest(SingleCollection.class, format("%s?colId=%s", url(FIND), collectionId), "", HttpMethods.Get));
    }

    /**
     * 查询最近一周的收藏
     */
    public static final int OPE_WEEK = 1;
    /**
     * 查询最近一月的收藏
     */
    public static final int OPE_MONTH = 2;
    /**
     * 查询最近一年的收藏
     */
    public static final int OPE_YEAR = 3;

    /**
     * 查找指定用户的收藏内容
     *
     * @param type       收藏类型，参考 {@link Collection.Type}
     * @param ope        ope:操作类型(1.查询最近一周的收藏,2.查询最近一月的收藏,3.查询最近一年的收藏)
     * @param pageNumber 页码
     */
    public void list(int type, int ope, int pageNumber) {
        // accessToken
        httpRequest(getRequest(MultipleCollection.class,
                format("%s?pageNumber=%d", url(LIST), pageNumber), "", HttpMethods.Get));
    }

    /**
     * 搜索指定用户的搜藏内容
     */
    public void search(String info) {
        // info,accessToken
        String params = format("%s?info=%s", url(SEARCH), info);
        httpRequest(getRequest(MultipleCollection.class, params, "", HttpMethods.Get));
    }
}
