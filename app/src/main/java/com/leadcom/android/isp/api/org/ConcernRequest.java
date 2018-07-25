package com.leadcom.android.isp.api.org;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.BoolQuery;
import com.leadcom.android.isp.api.query.PageQuery;
import com.leadcom.android.isp.api.query.PaginationQuery;
import com.leadcom.android.isp.model.organization.Concern;
import com.litesuits.http.request.param.HttpMethods;

/**
 * <b>功能描述：</b>关注组织相关的api<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/04/09 21:14 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ConcernRequest extends Request<Concern> {

    public static ConcernRequest request() {
        return new ConcernRequest();
    }

    private static class MultiConcern extends PaginationQuery<Concern> {
    }

    private static class BooleanConcern extends BoolQuery<Concern> {
    }

    private static class PageConcern extends PageQuery<Concern> {
    }

    @Override
    protected String url(String action) {
        return format("/group/groConcern%s", action);
    }

    @Override
    protected Class<Concern> getType() {
        return Concern.class;
    }

    @Override
    public ConcernRequest setOnSingleRequestListener(OnSingleRequestListener<Concern> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public ConcernRequest setOnMultipleRequestListener(OnMultipleRequestListener<Concern> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 添加关注
     */
    public void add(String groupId, String concernedGroupId) {
        executeHttpRequest(getRequest(BooleanConcern.class, format("%s?groupId=%s&conGroupId=%s", url(ADD), groupId, concernedGroupId), "", HttpMethods.Get));
    }

    /**
     * 取消关注
     */
    public void delete(String groupId, String concernedGroupId) {
        executeHttpRequest(getRequest(BooleanConcern.class, format("%s?groupId=%s&conGroupId=%s", url(DELETE), groupId, concernedGroupId), "", HttpMethods.Get));
    }

    /**
     * 查询全部？
     */
    public static final int CONCERN_NONE = 0;
    /**
     * 查询我关注的组织
     */
    public static final int CONCERN_TO = 1;
    /**
     * 查询关注我的组织
     */
    public static final int CONCERN_FROM = 2;

    /**
     * 列出指定组织可以关注的组织列表
     */
    public void list(String groupId, int concernType, int pageNumber, String searchingText) {
        String param = format("%s?groupId=%s&pageNumber=%d&pageSize=999", url(LIST), groupId, pageNumber);
        if (concernType > CONCERN_NONE) {
            param = format("%s&concernTo=%d", param, concernType);
        }
        if (!isEmpty(searchingText)) {
            param = format("%s&info=%s", param, searchingText);
        }
        executeHttpRequest(getRequest(MultiConcern.class, param, "", HttpMethods.Get));
    }

    /**
     * 查询关注了本组织的组织列表以及其下的自定义栏目列表
     */
    public void listTransfer(String groupId) {
        executeHttpRequest(getRequest(PageConcern.class, url(LIST) + "/transfer?groupId=" + groupId, "", HttpMethods.Get));
    }
}
