package com.gzlk.android.isp.fragment.archive;

import android.os.Bundle;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.WebViewFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.model.archive.Archive;

/**
 * <b>功能描述：</b>档案详情，通过WebView打开网页<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/21 10:10 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/21 10:10 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveDetailsWebViewFragment extends WebViewFragment {

    private static final String PARAM_TYPE = "adwvf_type";
    private static final String PARAM_MANAGER = "adwvf_manager";

    public static ArchiveDetailsWebViewFragment newInstance(String params) {
        ArchiveDetailsWebViewFragment adf = new ArchiveDetailsWebViewFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        // 档案类型：个人、组织，
        bundle.putInt(PARAM_TYPE, Integer.valueOf(strings[0]));
        // 当前登录者是否是档案的创始人或管理者
        bundle.putBoolean(PARAM_MANAGER, Boolean.valueOf(strings[1]));
        // 档案id
        bundle.putString(PARAM_QUERY_ID, strings[2]);
        adf.setArguments(bundle);
        return adf;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mType = bundle.getInt(PARAM_TYPE, Archive.Type.GROUP);
        isManager = bundle.getBoolean(PARAM_MANAGER, false);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_TYPE, mType);
        bundle.putBoolean(PARAM_MANAGER, isManager);
    }

    private int mType = Archive.Type.GROUP;
    private boolean isManager = false;

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_text_document_details_fragment_title);
        if (StringHelper.isEmpty(mQueryId)) {
            closeWithWarning(R.string.ui_text_document_details_not_exists);
        } else {
            if (isManager) {
                initializeRightTitle();
            }
            super.doingInResume();
        }
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    private static final String BASE_URL = "http://113.108.144.2:8045/lcbase-manage/editor/md_view.html";

    @Override
    protected String loadingUrl() {
        String url = format("%s?%sDocId=%s", BASE_URL, (mType == Archive.Type.GROUP ? "gro" : "user"), mQueryId);
        log("loading archive content from inner WebView: " + url);
        return url;
    }

    private void initializeRightTitle() {
        setRightText(R.string.ui_base_text_edit);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                openActivity(ArchiveNewFragment.class.getName(), format("%d,%s", Archive.Type.GROUP, mQueryId), true, true);
            }
        });
    }
}
