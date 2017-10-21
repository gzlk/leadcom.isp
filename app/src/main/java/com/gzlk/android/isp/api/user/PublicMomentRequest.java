package com.gzlk.android.isp.api.user;

import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.query.PaginationQuery;
import com.gzlk.android.isp.model.user.MomentPublic;
import com.litesuits.http.request.param.HttpMethods;

/**
 * <b>功能描述：</b>组织内公开的说说列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/20 10:23 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/20 10:23 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class PublicMomentRequest extends Request<MomentPublic> {

    public static PublicMomentRequest request() {
        return new PublicMomentRequest();
    }

    private static class MultiplePublicMoment extends PaginationQuery<MomentPublic> {
    }

    @Override
    protected String url(String action) {
        return format("/group/groMmtPublic%s", action);
    }

    @Override
    protected Class<MomentPublic> getType() {
        return MomentPublic.class;
    }

    @Override
    public PublicMomentRequest setOnSingleRequestListener(OnSingleRequestListener<MomentPublic> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public PublicMomentRequest setOnMultipleRequestListener(OnMultipleRequestListener<MomentPublic> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 拉取指定组织内的说说列表
     */
    public void list(String groupId, int pageNumber) {
        httpRequest(getRequest(MultiplePublicMoment.class, format("%s?groupId=%s&pageNumber=%d", url(LIST), groupId, pageNumber), "", HttpMethods.Get));
    }
}
