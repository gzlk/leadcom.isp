package com.gzlk.android.isp.api.activity;

import com.gzlk.android.isp.api.Api;

import org.json.JSONObject;

/**
 * <b>功能描述：</b>活动首页信息的拉取<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/04 03:38 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/04 03:38 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class FrontPageActivity extends Api {
    private JSONObject map;

    public JSONObject getMap() {
        return map;
    }

    public void setMap(JSONObject map) {
        this.map = map;
    }
}
