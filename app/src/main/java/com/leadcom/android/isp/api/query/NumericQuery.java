package com.leadcom.android.isp.api.query;

import com.leadcom.android.isp.api.Api;

/**
 * <b>功能描述：</b>数值查询<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/05/25 13:50 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/05/25 13:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class NumericQuery<T> extends Api<T> {

    private long data;

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }
}
