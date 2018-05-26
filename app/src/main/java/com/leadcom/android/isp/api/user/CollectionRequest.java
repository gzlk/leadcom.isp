package com.leadcom.android.isp.api.user;

import android.support.annotation.NonNull;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.BoolQuery;
import com.leadcom.android.isp.api.query.PaginationQuery;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.api.query.StringQuery;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.helper.publishable.Collectable;
import com.leadcom.android.isp.model.archive.ArchiveSource;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.user.Collection;
import com.leadcom.android.isp.model.user.Position;
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

    private static class StringCollection extends StringQuery<Collection> {
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

        executeHttpRequest(getRequest(StringCollection.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    public void add(String content) {
        // {type,content,creatorId,creatorName,creatorHeadPhoto,sourceType,sourceId,sourceTitle,[label],{position}}

        int type = Collection.Type.TEXT;
        if (Utils.isUrl(content)) {
            Attachment att = new Attachment();
            att.setUrl(content);
            att.setName(content.substring(content.lastIndexOf('/') + 1));
            att.resetInformation();
            if (att.isOffice()) {
                // 文档
                type = Collection.Type.ARCHIVE;
            } else if (att.isImage()) {
                type = Collection.Type.IMAGE;
                content = content.replace("#.jpg", "");
            } else if (att.isVideo()) {
                type = Collection.Type.VIDEO;
                content = content.replace("#.mp4", "");
            } else if (att.isAudio()) {
                type = Collection.Type.AUDIO;
            } else {
                type = Collection.Type.ATTACHMENT;
            }
        }
        JSONObject object = new JSONObject();
        try {
            object.put("type", type)
                    .put("content", checkNull(content))
                    .put("creatorId", checkNull(Collectable.creatorId))
                    .put("creatorName", checkNull(Collectable.creatorName))
                    .put("creatorHeadPhoto", checkNull(Collectable.creatorHeader))
                    .put("sourceType", Collectable.sourceType)
                    .put("sourceId", Collectable.sourceId)
                    .put("sourceTitle", checkNull(Collectable.sourceTitle))
                    .put("label", new JSONArray("[]"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        executeHttpRequest(getRequest(StringCollection.class, url(ADD), object.toString(), HttpMethods.Post));
    }

    /**
     * 删除收藏
     */
    public void delete(String collectionId) {
        // colId
        executeHttpRequest(getRequest(BoolCollection.class, format("%s?colId=%s", url(DELETE), collectionId), "", HttpMethods.Get));
    }

    /**
     * 更新收藏的标签
     */
    public void update(String collectionId, ArrayList<String> labels) {
        // {_id,content,accessToken}
        JSONObject object = new JSONObject();
        try {
            object.put("_id", collectionId)
                    .put("label", new JSONArray(null == labels ? new ArrayList() : labels));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        executeHttpRequest(getRequest(SingleCollection.class, url(UPDATE), object.toString(), HttpMethods.Post));
    }

    /**
     * 查找某个收藏的详细内容
     */
    public void find(String collectionId) {
        // colId
        executeHttpRequest(getRequest(SingleCollection.class, format("%s?colId=%s", url(FIND), collectionId), "", HttpMethods.Get));
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
        String param = format("%s?%sope=%d&pageNumber=%d", url(LIST), format((type <= 0 ? "" : "type=%d&"), type), ope, pageNumber);
        executeHttpRequest(getRequest(MultipleCollection.class, param, "", HttpMethods.Get));
    }

    /**
     * 搜索指定用户的搜藏内容
     */
    public void search(String info) {
        // info,accessToken
        String params = format("%s?info=%s", url(SEARCH), info);
        executeHttpRequest(getRequest(MultipleCollection.class, params, "", HttpMethods.Get));
    }
}
