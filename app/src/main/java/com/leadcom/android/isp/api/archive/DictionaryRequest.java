package com.leadcom.android.isp.api.archive;

import com.leadcom.android.isp.api.Request;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.query.ListQuery;
import com.leadcom.android.isp.model.archive.Dictionary;
import com.litesuits.http.request.param.HttpMethods;

/**
 * <b>功能描述：</b>字典管理api<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/01/27 18:15 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class DictionaryRequest extends Request<Dictionary> {

    public static DictionaryRequest request() {
        return new DictionaryRequest();
    }

    private static class ListDictionary extends ListQuery<Dictionary> {
    }

    @Override
    protected String url(String action) {
        return null;
    }

    @Override
    protected Class<Dictionary> getType() {
        return Dictionary.class;
    }

    @Override
    public DictionaryRequest setOnSingleRequestListener(OnSingleRequestListener<Dictionary> listener) {
        onSingleRequestListener = listener;
        return this;
    }

    @Override
    public DictionaryRequest setOnMultipleRequestListener(OnMultipleRequestListener<Dictionary> listListener) {
        onMultipleRequestListener = listListener;
        return this;
    }

    /**
     * 列举指定类型的字典列表
     */
    public void list(String typeCode) {
        String params = format("/system/dictionary/getDictionaryList?typeCode=%s", typeCode);
        httpRequest(getRequest(ListDictionary.class, params, "", HttpMethods.Get));
    }
}
