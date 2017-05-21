package com.gzlk.android.isp.fragment.organization;

import android.os.Bundle;
import android.webkit.WebView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.org.ArchiveRequest;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.fragment.base.BaseTransparentSupportFragment;
import com.gzlk.android.isp.fragment.individual.DocumentNewFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.model.BaseArchive;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.organization.archive.Archive;

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

public class ArchiveDetailsFragment extends BaseTransparentSupportFragment {

    public static ArchiveDetailsFragment newInstance(String params) {
        ArchiveDetailsFragment adf = new ArchiveDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, params);
        isLoaded = false;
        adf.setArguments(bundle);
        return adf;
    }

    private static boolean isLoaded = false;
    private WebView webView;

    @Override
    public int getLayout() {
        return R.layout.fragment_archive_details;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {

    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_text_document_details_fragment_title);
        if (StringHelper.isEmpty(mQueryId)) {
            closeWithWarning(R.string.ui_text_document_details_not_exists);
        } else {
            loadingArchive();
            if (null == webView) {
                webView = (WebView) mRootView;
                webView.loadUrl("https://www.baidu.com");
            }
        }
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {

    }

    @Override
    protected void destroyView() {

    }

    private void loadingArchive() {
        if (!isLoaded) {
            ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
                @Override
                public void onResponse(Archive archive, boolean success, String message) {
                    super.onResponse(archive, success, message);
                    if (success) {
                        if (null != archive && !StringHelper.isEmpty(archive.getId())) {
                            isLoaded = true;
                            // 网络数据返回的结果，需要保持到缓存中
                            if (!StringHelper.isEmpty(message)) {
                                new Dao<>(Archive.class).save(archive);
                            }
                            if (Cache.cache().userId.equals(archive.getUserId())) {
                                // 创建者可以编辑
                                initailizeRightTitle();
                            }
                        }
                    }
                }
            }).find(mQueryId);
        }
    }

    private void initailizeRightTitle() {
        setRightText(R.string.ui_base_text_edit);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                openActivity(DocumentNewFragment.class.getName(), format("%d,%s", BaseArchive.Type.ORGANIZATION, mQueryId), true, true);
            }
        });
    }
}
