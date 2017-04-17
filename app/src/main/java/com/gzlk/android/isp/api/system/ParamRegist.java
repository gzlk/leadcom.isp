package com.gzlk.android.isp.api.system;

import com.gzlk.android.isp.api.BaseApi;
import com.litesuits.http.annotation.HttpMethod;
import com.litesuits.http.annotation.HttpUri;
import com.litesuits.http.request.param.HttpMethods;
import com.litesuits.http.request.param.HttpRichParamModel;

/**
 * <b>功能描述：</b>注册<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/17 23:24 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/17 23:24 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

@HttpUri(BaseApi.URL + "/system/regist")
@HttpMethod(HttpMethods.Post)
public class ParamRegist extends HttpRichParamModel<Regist> {
    private String phone;
    private String password;
    private String name;

    public ParamRegist(String phone, String password, String name) {
        this.password = password;
        this.phone = phone;
        this.name = name;
    }
}
