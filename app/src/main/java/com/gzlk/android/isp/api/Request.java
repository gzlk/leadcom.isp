package com.gzlk.android.isp.api;

import com.gzlk.android.isp.api.listener.OnRequestListListener;
import com.gzlk.android.isp.api.listener.OnRequestListener;
import com.gzlk.android.isp.application.App;
import com.gzlk.android.isp.helper.LogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.listener.OnHttpListener;
import com.litesuits.http.LiteHttp;
import com.litesuits.http.request.JsonRequest;
import com.litesuits.http.request.content.JsonBody;
import com.litesuits.http.request.param.HttpMethods;
import com.litesuits.http.response.Response;

import java.lang.reflect.Type;

/**
 * <b>功能描述：</b>所有网络请求的基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/24 16:03 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/24 16:03 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class Request<T> {

    private static final String URL = BaseApi.URL;
    /**
     * 新增
     */
    protected static final String ADD = "/add";
    /**
     * 删除
     */
    protected static final String DELETE = "/delete";
    /**
     * 更新
     */
    protected static final String UPDATE = "/update";
    /**
     * 单查找
     */
    protected static final String FIND = "/find";
    /**
     * 列表
     */
    protected static final String LIST = "/list";
    /**
     * 搜索
     */
    protected static final String SEARCH = "/search";

    /**
     * 组合url
     */
    protected abstract String url(String action);

    /**
     * http网络访问层
     */
    private LiteHttp liteHttp;

    public Request() {
        liteHttp = LiteHttp.build(App.app()).create();
        // 10秒网络超时
        liteHttp.getConfig().setConnectTimeout(10000);
    }

    protected String format(String fmt, Object... args) {
        return StringHelper.format(fmt, args);
    }

    protected void log(String log) {
        LogHelper.log(this.getClass().getSimpleName(), log);
    }

    /**
     * 检测给定值是否为null，是则返回""字符串，而不是null
     */
    protected String checkNull(String value) {
        return StringHelper.isEmpty(value) ? "" : value;
    }

    /**
     * 发起网络请求
     */
    protected void httpRequest(JsonRequest request) {
        liteHttp.executeAsync(request);
    }

    /**
     * 组合请求
     */
    protected JsonRequest<Output<T>> getRequest(Type resultType, String action, String body, HttpMethods methods) {
        return new JsonRequest<Output<T>>(StringHelper.format("%s%s", URL, action), resultType)
                .setHttpListener(new OnHttpListener<Output<T>>() {

                    @Override
                    public void onSucceed(Output<T> data, Response<Output<T>> response) {
                        super.onSucceed(data, response);
                        if (data.success()) {
                            if (data instanceof Query) {
                                if (null != onRequestListListener) {
                                    Query<T> query = (Query<T>) data;
                                    Pagination<T> pagination = query.getData();
                                    onRequestListListener.onResponse(pagination.getList(), data.success(),
                                            pagination.getTotalPages(), pagination.getPageSize(),
                                            pagination.getTotal(), pagination.getPageNumber());
                                }
                            } else {
                                if (null != onRequestListener) {
                                    onRequestListener.onResponse(data.getData(), data.success(), data.getMsg());
                                }
                            }
                        } else {
                            ToastHelper.make().showMsg(data.getMsg());
                            fireFailedListenerEvents(data.getMsg());
                        }
                    }

                    @Override
                    public void onFailed() {
                        super.onFailed();
                        fireFailedListenerEvents("");
                    }
                }).setHttpBody(new JsonBody(body), methods);
    }

    /**
     * 通知失败
     */
    private void fireFailedListenerEvents(String message) {
        if (null != onRequestListener) {
            onRequestListener.onResponse(null, false, message);
        }
        if (null != onRequestListListener) {
            onRequestListListener.onResponse(null, false, 0, 0, 0, 0);
        }
    }

    protected OnRequestListener<T> onRequestListener;

    /**
     * 设置网络调用成功之后的回调
     */
    public abstract Request<T> setOnRequestListener(OnRequestListener<T> listener);

    protected OnRequestListListener<T> onRequestListListener;

    /**
     * 添加请求列表时的处理回调
     */
    public abstract Request<T> setOnRequestListListener(OnRequestListListener<T> listListener);

}
