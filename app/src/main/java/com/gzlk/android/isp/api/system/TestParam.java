package com.gzlk.android.isp.api.system;

import com.litesuits.http.annotation.HttpMethod;
import com.litesuits.http.annotation.HttpUri;
import com.litesuits.http.request.param.HttpMethods;
import com.litesuits.http.request.param.HttpRichParamModel;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/15 22:17 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/15 22:17 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@HttpUri("http://10.141.130.17/ajax/test.ashx")
@HttpMethod(HttpMethods.Get)
public class TestParam extends HttpRichParamModel<Testing> {
    public String type = "list";
    private String adf = "adf";
}
