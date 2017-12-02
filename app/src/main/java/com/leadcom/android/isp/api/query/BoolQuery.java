package com.leadcom.android.isp.api.query;

import com.leadcom.android.isp.api.Api;

/**
 * <b>功能描述：</b>返回boolean值的查询结果<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/07/23 23:46 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/07/23 23:46 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class BoolQuery<T> extends Api<T> {

    private boolean data;

    public boolean getData() {
        return data;
    }

    public void setData(boolean data) {
        this.data = data;
    }
}
