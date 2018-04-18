package com.leadcom.android.isp.api.common;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.ListQuery;
import com.leadcom.android.isp.api.query.PaginationQuery;
import com.leadcom.android.isp.model.common.CoverTemplate;
import com.litesuits.http.request.param.HttpMethods;


/**
 * <b>功能描述：</b>预定义封面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/04/16 19:53 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class CoverRequest extends Request<CoverTemplate> {

    public static CoverRequest request() {
        return new CoverRequest();
    }

    private static class ListCover extends PaginationQuery<CoverTemplate> {
    }

    @Override
    protected String url(String action) {
        return format("/system/systemImageTemplate%s", action);
    }

    @Override
    protected Class<CoverTemplate> getType() {
        return CoverTemplate.class;
    }

    @Override
    public CoverRequest setOnSingleRequestListener(OnSingleRequestListener<CoverTemplate> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public CoverRequest setOnMultipleRequestListener(OnMultipleRequestListener<CoverTemplate> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    public void list(int pageNumber) {
        httpRequest(getRequest(ListCover.class, format("%s?pageNumber=%d&pageSize=99", url(LIST), pageNumber), "", HttpMethods.Get));
    }
}
