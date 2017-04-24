package com.gzlk.android.isp.api.listener;

import java.util.List;

/**
 * <b>功能描述：</b>网络请求成功的回调<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/24 20:32 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/24 20:32 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class OnRequestListListener<Data> {
    /**
     * 网络调用成功
     */
    public void onResponse(List<Data> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
    }
}
