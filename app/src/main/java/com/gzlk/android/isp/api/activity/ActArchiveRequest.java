package com.gzlk.android.isp.api.activity;

import com.gzlk.android.isp.api.Query;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.model.activity.ActArchive;

/**
 * <b>功能描述：</b>活动文档存档、审核相关接口<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/31 17:53 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/31 17:53 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ActArchiveRequest extends Request<ActArchive> {

    public static ActArchiveRequest request() {
        return new ActArchiveRequest();
    }

    private static class MultipleActivityArchive extends Query<ActArchive> {
    }

    @Override
    protected String url(String action) {
        return null;
    }

    @Override
    protected Class<ActArchive> getType() {
        return ActArchive.class;
    }

    @Override
    public ActArchiveRequest setOnSingleRequestListener(OnSingleRequestListener<ActArchive> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public ActArchiveRequest setOnMultipleRequestListener(OnMultipleRequestListener<ActArchive> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 存档单个档案
     */
    public void archive() {
        // groDocArchiveId,status,accessToken
    }
}
