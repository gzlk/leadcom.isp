package com.gzlk.android.isp.api.activity;

import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.listener.OnHttpListener;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.request.StringRequest;
import com.litesuits.http.response.Response;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/04 03:42 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/04 03:42 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class FrontPageRequest extends Request<String> {

    @Override
    protected String url(String action) {
        return null;
    }

    @Override
    protected Class<String> getType() {
        return String.class;
    }

    @Override
    public FrontPageRequest setOnSingleRequestListener(OnSingleRequestListener<String> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    @SuppressWarnings("notused")
    public FrontPageRequest setOnMultipleRequestListener(OnMultipleRequestListener<String> listListener) {
        throw new IllegalArgumentException("cannot support multiple listener");
        //onMultipleRequestListener = listListener;
        //return this;
    }

    public void list(String groupId) {
        String url = "";//format("%s/activity/frontPageActs?groupId=%s&accessToken=%s", URL, groupId, Cache.cache().accessToken);
        liteHttp.executeAsync(new StringRequest(url).setHttpListener(
                new OnHttpListener<String>() {
                    @Override
                    public void onSucceed(String data, Response<String> response) {
                        super.onSucceed(data, response);
                        if (null != onSingleRequestListener) {
                            onSingleRequestListener.onResponse(data, true, "");
                        }
                    }

                    @Override
                    public void onFailed() {
                        super.onFailed();
                        ToastHelper.make().showMsg("获取活动首页信息失败");
                    }
                }
        ));
    }
}
