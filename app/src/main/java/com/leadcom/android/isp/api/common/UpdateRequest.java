package com.leadcom.android.isp.api.common;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.SingleQuery;
import com.leadcom.android.isp.model.common.SystemUpdate;
import com.litesuits.http.request.param.HttpMethods;

/**
 * <b>功能描述：</b>更新检测<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/07/31 11:30 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/07/31 11:30 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class UpdateRequest extends Request<SystemUpdate> {

    public static UpdateRequest request() {
        return new UpdateRequest();
    }

    private static class SingleUpdate extends SingleQuery<SystemUpdate> {
    }

    private static final String VERSION = SystemRequest.SYSTEM + "/getClieVersion";

    @Override
    protected String url(String action) {
        return null;
    }

    @Override
    protected Class<SystemUpdate> getType() {
        return SystemUpdate.class;
    }

    @Override
    public UpdateRequest setOnSingleRequestListener(OnSingleRequestListener<SystemUpdate> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public UpdateRequest setOnMultipleRequestListener(OnMultipleRequestListener<SystemUpdate> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 获取服务器上客户端的最新版本
     */
    public void getClientVersion() {
        directlySave = false;
        httpRequest(getRequest(SingleUpdate.class, VERSION, "", HttpMethods.Get));
    }
}
