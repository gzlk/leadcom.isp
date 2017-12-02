package com.leadcom.android.isp.api.common;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.model.query.FullTextQuery;
import com.litesuits.http.request.param.HttpMethods;

/**
 * <b>功能描述：</b>全文检索请求对象<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/08/11 15:32 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/08/11 15:32 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class FullTextQueryRequest extends Request<FullTextQuery> {

    public static FullTextQueryRequest request() {
        return new FullTextQueryRequest();
    }

    private class SingleFullTextQuery extends SingleQuery<FullTextQuery> {
    }

    @Override
    protected String url(String action) {
        return format("/textIndex%s", action);
    }

    @Override
    protected Class<FullTextQuery> getType() {
        return FullTextQuery.class;
    }

    @Override
    public FullTextQueryRequest setOnSingleRequestListener(OnSingleRequestListener<FullTextQuery> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public FullTextQueryRequest setOnMultipleRequestListener(OnMultipleRequestListener<FullTextQuery> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 搜索
     */
    public void search(String keyword) {
        directlySave = false;
        httpRequest(getRequest(SingleFullTextQuery.class, format("%s?keyword=%s", url(SEARCH), keyword), "", HttpMethods.Get));
    }
}
