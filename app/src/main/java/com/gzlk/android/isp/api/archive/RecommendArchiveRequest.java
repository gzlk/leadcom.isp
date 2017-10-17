package com.gzlk.android.isp.api.archive;

import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.query.PaginationQuery;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.archive.RecommendArchive;
import com.litesuits.http.request.param.HttpMethods;

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

    private static class MultipleRecommend extends PaginationQuery<RecommendArchive> {
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
     * 查询组织的待推荐档案列表
     */
    public void list(String groupId, int pageNumber) {
        // groupId,ope,pageSize,pageNumber
        String params = format("%s?%s&pageNumber=%d&groupId=%s", url(LIST), SUMMARY, pageNumber, groupId);
        httpRequest(getRequest(MultipleRecommend.class, params, "", HttpMethods.Get));
    }
}
