package com.leadcom.android.isp.api.common;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.query.ListQuery;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.model.common.FocusImage;
import com.litesuits.http.request.param.HttpMethods;

/**
 * <b>功能描述：</b>首页推荐图片<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/03 23:26 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/03 23:26 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class FocusImageRequest extends Request<FocusImage> {

    public static FocusImageRequest request() {
        return new FocusImageRequest();
    }

    private static class MultiImage extends ListQuery<FocusImage> {
    }

    @Override
    protected String url(String action) {
        return null;
    }

    @Override
    protected Class<FocusImage> getType() {
        return FocusImage.class;
    }

    @Override
    public FocusImageRequest setOnSingleRequestListener(OnSingleRequestListener<FocusImage> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public FocusImageRequest setOnMultipleRequestListener(OnMultipleRequestListener<FocusImage> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    public void all() {
        // 不保存
        directlySave = false;
        httpRequest(getRequest(MultiImage.class, "/operate/focusImage/list/all", "", HttpMethods.Get));
    }
}
