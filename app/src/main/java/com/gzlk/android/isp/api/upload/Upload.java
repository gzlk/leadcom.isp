package com.gzlk.android.isp.api.upload;

import com.gzlk.android.isp.api.Api;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <b>功能描述：</b>文件上传返回的结果<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/23 16:42 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/23 16:42 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class Upload extends Api {

    private JSONObject result;
    private String name;
    private String url;

    /**
     * 分离文件名和url
     */
    void departData() {
        if (null != result) {
            try {
                name = result.names().getString(0);
                url = result.getString(name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 文件上传之后的url
     */
    public JSONObject getResult() {
        return result;
    }

    /**
     * 文件上传之后的url
     */
    public void setResult(JSONObject result) {
        this.result = result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
