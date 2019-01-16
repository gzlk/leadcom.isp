package com.leadcom.android.isp.api;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.helper.StringHelper;

import java.io.Serializable;

/**
 * <b>功能描述：</b>所有Model网络请求的基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/15 19:46 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/15 19:46 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

abstract class BaseApi implements Serializable {

    protected static final String VERSION = StringHelper.getString(R.string.app_internal_version);
    /**
     * 所有网络调用的网址前缀
     */
    static final String URL = StringHelper.getString(R.string.app_api_http_host);
    static final String URL1 = "http://172.16.10.120:8889";

    static final String API_VER = StringHelper.getString(Cache.isReleasable() ? R.string.app_api_version_release : R.string.app_api_version);
    /**
     * 网络调用成功时的状态码
     */
    static final String SUCCEED = "000000";
    /**
     * 验证失败，需要重新登录
     */
    static final String TOKEN_TIMEOUT = "110000";
    /**
     * 小组未查询到
     */
    static final String SQUAD_NOT_EXIT = "40001";
}
