package com.gzlk.android.isp.api.org;

import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;

import org.json.JSONObject;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/07/12 14:24 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/07/12 14:24 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SimpleRequest extends Request<JSONObject> {

    @Override
    protected String url(String action) {
        return null;
    }

    @Override
    protected Class<JSONObject> getType() {
        return null;
    }

    @Override
    public Request<JSONObject> setOnSingleRequestListener(OnSingleRequestListener<JSONObject> listener) {
        return null;
    }

    @Override
    public Request<JSONObject> setOnMultipleRequestListener(OnMultipleRequestListener<JSONObject> listListener) {
        return null;
    }
}
