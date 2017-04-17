package com.gzlk.android.isp.listener;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.helper.LogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.response.Response;

/**
 * <b>功能描述：</b>网络活动监听<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/17 21:35 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/17 21:35 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class OnHttpListener<T> extends HttpListener<T> {

    @Override
    public void onSuccess(T data, Response<T> response) {
        super.onSuccess(data, response);
        LogHelper.log("HTTP status", response.getHttpStatus().toString());
        onSucceed(data, response);
    }

    /**
     * 网络调用成功之后的处理
     */
    public void onSucceed(T data, Response<T> response) {
    }

    /**
     * 网络调用失败后的处理
     */
    public void onFailed() {
    }

    @Override
    public void onFailure(HttpException e, Response<T> response) {
        super.onFailure(e, response);
        ToastHelper.make(null).showMsg(StringHelper.format("网咯操作失败: %d", response.getHttpStatus().getCode()));
        onFailed();
    }

    @Override
    public void onCancel(T t, Response<T> response) {
        super.onCancel(t, response);
        ToastHelper.make(null).showMsg(StringHelper.format("网咯操作已取消: %d", response.getHttpStatus().getCode()));
        onFailed();
    }
}
