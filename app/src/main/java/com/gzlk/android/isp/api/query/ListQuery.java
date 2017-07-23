package com.gzlk.android.isp.api.query;

import com.gzlk.android.isp.api.Api;

import java.util.List;

/**
 * <b>功能描述：</b>只有List的查询返回结果<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/23 01:48 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/23 01:48 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ListQuery<T> extends Api<T> {

    private List<T> data;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
