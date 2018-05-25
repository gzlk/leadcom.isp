package com.leadcom.android.isp.api.common;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.PaginationQuery;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.model.common.Advice;
import com.litesuits.http.request.param.HttpMethods;

/**
 * <b>功能描述：</b>意见和建议的api<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/07/28 10:29 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/07/28 10:29 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class AdviceRequest extends Request<Advice> {

    public static AdviceRequest request() {
        return new AdviceRequest();
    }

    private static class SingleAdvice extends SingleQuery<Advice> {
    }

    private static class MultipleAdvice extends PaginationQuery<Advice> {
    }

    private static final String URL = "/system/advice";

    @Override
    protected String url(String action) {
        return format("%s%s", URL, action);
    }

    @Override
    protected Class<Advice> getType() {
        return Advice.class;
    }

    @Override
    public AdviceRequest setOnSingleRequestListener(OnSingleRequestListener<Advice> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public AdviceRequest setOnMultipleRequestListener(OnMultipleRequestListener<Advice> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    public void add(String content) {
        directlySave = false;
        executeHttpRequest(getRequest(SingleAdvice.class, format("%s?content=%s", url(ADD), content), "", HttpMethods.Get));
    }
}
