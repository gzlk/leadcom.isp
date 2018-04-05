package com.leadcom.android.isp.fragment.activity.sign;

import android.os.Bundle;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.activity.AppSigningRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.popup.DeleteDialogHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.model.activity.sign.AppSigning;


/**
 * <b>功能描述：</b>签到的基类fragment<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/04/05 18:33 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public abstract class BaseSignFragment extends BaseSwipeRefreshSupportFragment {

    protected static Bundle getBundle(String tid) {
        Bundle bundle = new Bundle();
        // 网易云传过来的活动的tid
        bundle.putString(PARAM_QUERY_ID, tid);
        return bundle;
    }

    protected void warningDelete(final String signingId) {
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                deleteSigning(signingId);
                return true;
            }
        }).setTitleText(R.string.ui_activity_sing_details_delete_warning).show();
    }

    private void deleteSigning(final String signingId) {
        AppSigningRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<AppSigning>() {
            @Override
            public void onResponse(AppSigning signing, boolean success, String message) {
                super.onResponse(signing, success, message);
                onDeleteSigningComplete(success, signingId);
            }
        }).deleteTeamSigning(signingId);
    }

    protected void onDeleteSigningComplete(boolean success, String signingId) {
    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    protected void onSwipeRefreshing() {
        remotePageNumber = 1;
    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

}
