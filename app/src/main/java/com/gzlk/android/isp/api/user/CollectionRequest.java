package com.gzlk.android.isp.api.user;

import android.support.annotation.NonNull;

import com.gzlk.android.isp.api.query.SingleQuery;
import com.gzlk.android.isp.api.query.PaginationQuery;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.model.archive.ArchiveSource;
import com.gzlk.android.isp.model.user.Collection;
import com.litesuits.http.request.param.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

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

    @Override
    protected void save(Collection collection) {
        if (null != collection) {
            collection.compound();
        }
        super.save(collection);
    }

    @Override
    protected void save(List<Collection> list) {
        if (null != list && list.size() > 0) {
            for (Collection col : list) {
                col.compound();
            }
        }
        super.save(list);
    }

    /**
     * 添加个人收藏
     *
     * @param type        收藏类型，参考 {@link Collection.Type}
     * @param source      来源(module:模块类型,id:模块ID) {@link ArchiveSource}
     * @param content     内容
     * @param creatorId   作者id
     * @param creatorName 作者名字
     * @see Collection.Type
     * @see ArchiveSource
     */
    public void add(int type, ArchiveSource source, String content, @NonNull String creatorId, String creatorName) {
        // {type,content,creatorId,creatorName,accessToken}

        JSONObject object = new JSONObject();
        try {
            object.put("type", type)
                    .put("source", new JSONObject(source.toString()))
                    .put("content", checkNull(content))
                    .put("creatorId", checkNull(creatorId))
                    .put("creatorName", checkNull(creatorName));
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
        httpRequest(getRequest(SingleCollection.class, format("%s?colId=%s", url(DELETE), collectionId), "", HttpMethods.Get));
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
     * 查找指定用户的收藏内容
     */
    public void list(int pageNumber) {
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
