package com.leadcom.android.isp.api.common;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.model.common.Quantity;
import com.litesuits.http.request.param.HttpMethods;


/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/16 22:15 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class QuantityRequest extends Request<Quantity> {

    public static QuantityRequest request() {
        return new QuantityRequest();
    }

    private static class SingleRequest extends SingleQuery<Quantity> {
    }

    @Override
    protected String url(String action) {
        return null;
    }

    @Override
    protected Class<Quantity> getType() {
        return Quantity.class;
    }

    @Override
    public QuantityRequest setOnSingleRequestListener(OnSingleRequestListener<Quantity> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public QuantityRequest setOnMultipleRequestListener(OnMultipleRequestListener<Quantity> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 查询当前用户的统计信息
     */
    public void findUser() {
        directlySave = false;
        httpRequest(getRequest(SingleRequest.class, "/user/userCount/find", "", HttpMethods.Get));
    }

    /**
     * 查询指定组织的统计信息
     */
    public void findGroup(String groupId) {
        directlySave = false;
        httpRequest(getRequest(SingleRequest.class, format("/group/groCount/find?groupId=%s", groupId), "", HttpMethods.Get));
    }
}
