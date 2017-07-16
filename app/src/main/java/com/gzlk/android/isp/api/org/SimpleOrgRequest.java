package com.gzlk.android.isp.api.org;

import com.gzlk.android.isp.api.Output;
import com.gzlk.android.isp.api.Request;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.litesuits.http.request.param.HttpMethods;

/**
 * <b>功能描述：</b>组织列表的简化请求<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/07/12 14:24 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/07/12 14:24 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SimpleOrgRequest extends Request<SimpleOutput> {

    public static SimpleOrgRequest request() {
        return new SimpleOrgRequest();
    }

    private static class SingleRequest extends Output<SimpleOutput> {
    }

    @Override
    protected String url(String action) {
        return null;
    }

    @Override
    protected Class<SimpleOutput> getType() {
        return null;
    }

    @Override
    public SimpleOrgRequest setOnSingleRequestListener(OnSingleRequestListener<SimpleOutput> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public SimpleOrgRequest setOnMultipleRequestListener(OnMultipleRequestListener<SimpleOutput> listListener) {
        throw new IllegalArgumentException("cannot support multiple request yet");
    }

    /**
     * 列举所有组织列表，包括关联的上级、友好、下级组织的成员、小组（包含小组成员）
     */
    public void listAllMember(String groupId) {
        directlySave = false;
        String params = format("/group/groMember/listAllMember?groupId=%s", groupId);
        httpRequest(getRequest(SingleRequest.class, params, "", HttpMethods.Get));
    }
}
