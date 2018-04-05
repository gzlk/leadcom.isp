package com.leadcom.android.isp.fragment.activity.notice;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.activity.AppNoticeRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.popup.DeleteDialogHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.model.activity.AppNotice;

/**
 * <b>功能描述：</b>通知相关的基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/04/05 08:11 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public abstract class BaseNoticeFragment extends BaseSwipeRefreshSupportFragment {

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    protected void warningDelete(final String noticeId) {
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                delete(noticeId);
                return true;
            }
        }).setTitleText(R.string.ui_activity_notice_details_delete).show();
    }

    private void delete(final String noticeId) {
        AppNoticeRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<AppNotice>() {
            @Override
            public void onResponse(AppNotice notice, boolean success, String message) {
                super.onResponse(notice, success, message);
                onDeleteNoticeComplete(success, noticeId);
            }
        }).deleteTeamNotice(noticeId);
    }

    protected void onDeleteNoticeComplete(boolean success, String noticeId) {
    }
}
