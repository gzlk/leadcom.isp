package com.leadcom.android.isp.fragment.common;

import android.os.Bundle;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.helper.StringHelper;

/**
 * <b>功能描述：</b>内置web view浏览器<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/05 23:47 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/05 23:47 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class InnerWebViewFragment extends BaseWebViewFragment {

    private static final String PARAM_TITLE = "iwvf_title";

    public static InnerWebViewFragment newInstance(String params) {
        InnerWebViewFragment iwvf = new InnerWebViewFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        // url地址
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        // 标题
        bundle.putString(PARAM_TITLE, strings[1]);
        iwvf.setArguments(bundle);
        return iwvf;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mTitle = bundle.getString(PARAM_TITLE, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_TITLE, mTitle);
    }

    private String mTitle;

    @Override
    public void doingInResume() {
        setCustomTitle(mTitle);
        if (StringHelper.isEmpty(mQueryId)) {
            closeWithWarning(R.string.ui_text_home_inner_web_view_invalid_url);
        } else {
            super.doingInResume();
        }
    }

    @Override
    protected String loadingUrl() {
        log("loading url from inner WebView: " + mQueryId);
        return mQueryId;
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }
}
