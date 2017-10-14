package com.gzlk.android.isp.api;

import com.gzlk.android.isp.BuildConfig;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.helper.StringHelper;

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

    /**
     * 所有网络调用的网址前缀
     */
    static final String URL = BuildConfig.RELEASEABLE ? "http://120.24.87.21:8888" : "http://113.108.144.2:8889";
    //"http://113.108.144.2:"+(BuildConfig.RELEASEABLE ?"8044":"8889");

    static final String API_VER = StringHelper.getString(BuildConfig.RELEASEABLE ? R.string.app_api_version_release : R.string.app_api_version);
    /**
     * 网络调用成功时的状态码
     */
    static final String SUCCEED = "000000";
}
