package com.leadcom.android.isp.api.common;

import com.leadcom.android.isp.api.query.PaginationQuery;
import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.activity.Activity;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.common.RecommendContent;
import com.litesuits.http.request.param.HttpMethods;

import java.util.List;

/**
 * <b>功能描述：</b>首页推荐内容<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/03 23:42 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/03 23:42 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class RecommendRequest extends Request<RecommendContent> {

    public static RecommendRequest request() {
        return new RecommendRequest();
    }

    private static class MultiRecommend extends PaginationQuery<RecommendContent> {
    }

    @Override
    protected String url(String action) {
        return null;
    }

    @Override
    protected Class<RecommendContent> getType() {
        return RecommendContent.class;
    }

    @Override
    public RecommendRequest setOnSingleRequestListener(OnSingleRequestListener<RecommendContent> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public RecommendRequest setOnMultipleRequestListener(OnMultipleRequestListener<RecommendContent> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    private Dao<Activity> activityDao = new Dao<>(Activity.class);
    private Dao<Archive> archiveDao = new Dao<>(Archive.class);

    private void saveContent(RecommendContent content) {
        switch (content.getSourceType()) {
//            case RecommendContent.SourceType.ACTIVITY:
//                // 保存活动
//                activityDao.save(content.getActivity());
//                break;
            case RecommendContent.SourceType.ARCHIVE:
                archiveDao.save(content.getGroDocRcmd().getGroDoc());
                break;
        }
    }

    @Override
    protected void save(List<RecommendContent> list) {
        if (null != list && list.size() > 0) {
            for (RecommendContent rec : list) {
                saveContent(rec);
            }
        }
        // 内容保存之后本类不保存
        directlySave = false;
        super.save(list);
    }

    @Override
    protected void save(RecommendContent recommendContent) {
        saveContent(recommendContent);
        // 内容保存之后本类不保存
        directlySave = false;
        super.save(recommendContent);
    }

    /**
     * 获取后台推送的档案或活动列表
     */
    public void list(int pageNumber) {
        // 不保存
        directlySave = false;
        String params = format("%s&pageNumber=%d", SUMMARY, pageNumber);
        httpRequest(getRequest(MultiRecommend.class, format("/operate/recommend/list?%s", params), "", HttpMethods.Get));
    }
}
