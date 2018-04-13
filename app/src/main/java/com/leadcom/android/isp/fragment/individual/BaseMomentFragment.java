package com.leadcom.android.isp.fragment.individual;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.user.MomentRequest;
import com.leadcom.android.isp.fragment.base.BaseCmtLikeColFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.helper.publishable.CollectHelper;
import com.leadcom.android.isp.helper.publishable.listener.OnCollectedListener;
import com.leadcom.android.isp.helper.publishable.listener.OnUncollectedListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.common.Seclusion;
import com.leadcom.android.isp.model.common.ShareInfo;
import com.leadcom.android.isp.model.user.Collection;
import com.leadcom.android.isp.model.user.Moment;
import com.leadcom.android.isp.share.ShareToQQ;
import com.leadcom.android.isp.share.ShareToWeiBo;
import com.leadcom.android.isp.share.ShareToWeiXin;

/**
 * <b>功能描述：</b>单个说说<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/07/26 10:35 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/07/26 10:35 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class BaseMomentFragment extends BaseCmtLikeColFragment {

    protected static final String PARAM_MOMENT = "bmf_moment";

    @Override
    protected void onSwipeRefreshing() {

    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    @Override
    protected void destroyView() {

    }

    protected Moment mMoment;

    /**
     * 拉取远程服务器上的说说
     */
    protected void fetchingMoment() {
        MomentRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Moment>() {
            @Override
            public void onResponse(Moment moment, boolean success, String message) {
                super.onResponse(moment, success, message);
                onFetchingMomentComplete(moment, success);
            }
        }).find(mQueryId);
    }

    /**
     * 拉取远程服务器上的说说完毕
     */
    protected void onFetchingMomentComplete(Moment moment, boolean success) {
    }

    protected void deleteMoment() {
        MomentRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Moment>() {
            @Override
            public void onResponse(Moment moment, boolean success, String message) {
                super.onResponse(moment, success, message);
                onDeleteMomentComplete(moment, success, message);
            }
        }).delete(mQueryId);
    }

    protected void onDeleteMomentComplete(Moment moment, boolean success, String message) {
    }

    protected void handleMomentAuthPublic() {
        final int state = mMoment.getAuthPublic();
        setLoadingText(state == Seclusion.Type.Public ? R.string.ui_text_moment_details_button_privacy : R.string.ui_text_moment_details_button_public);
        displayLoading(true);
        MomentRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Moment>() {
            @Override
            public void onResponse(Moment moment, boolean success, String message) {
                super.onResponse(moment, success, message);
                displayLoading(false);
                if (success) {
                    mMoment.setAuthPublic(state == Seclusion.Type.Public ? 2 : Seclusion.Type.Public);
                }
            }
        }).update(mQueryId, state == Seclusion.Type.Public ? 2 : Seclusion.Type.Public);
    }

    protected void tryCollection() {
        CollectHelper collectHelper = CollectHelper.helper().setModel(mMoment);
        if (mMoment.isCollected()) {
            // 取消收藏
            collectHelper.setUncollectedListener(new OnUncollectedListener() {
                @Override
                public void onUncollected(boolean success, Model model) {
                    if (success) {
                        ToastHelper.make().showMsg(R.string.ui_base_share_to_favorite_canceled);
                    }
                }
            }).uncollect(mMoment.getColId());
        } else {
            // 收藏
            collectHelper.setCollectedListener(new OnCollectedListener() {
                @Override
                public void onCollected(boolean success, Model model) {
                    if (success) {
                        ToastHelper.make().showMsg(R.string.ui_base_share_to_favorite_success);
                    }
                }
            }).collect(Collection.get(mMoment));
        }
    }

    private void buildMomentShareInfo() {
        if (null == mShareInfo) {
            mShareInfo = new ShareInfo();
        }
        mShareInfo.setId(mMoment.getId());
        mShareInfo.setTitle(getString(R.string.ui_nim_app_recent_contact_type_moment));
        mShareInfo.setDescription(mMoment.getContent());
        mShareInfo.setContentType(ShareInfo.ContentType.MOMENT);
        mShareInfo.setDocType(0);
        mShareInfo.setImageUrl(mMoment.getImage().size() > 0 ? mMoment.getImage().get(0) : "");
        mShareInfo.setTargetPath("");
    }

    @Override
    protected void shareToApp() {
        buildMomentShareInfo();
        super.shareToApp();
    }

    @Override
    protected void shareToQQ() {
        ShareToQQ.shareToQQ(ShareToQQ.TO_QQ, Activity(), getString(R.string.ui_nim_app_recent_contact_type_moment), mMoment.getContent(), "", "", null);
    }

    @Override
    protected void shareToQZone() {
        ShareToQQ.shareToQQ(ShareToQQ.TO_QZONE, Activity(), StringHelper.getString(R.string.ui_base_share_title, getString(R.string.ui_nim_app_recent_contact_type_moment)), mMoment.getContent(), "http://www.baidu.com", "", mMoment.getImage());
    }

    @Override
    protected void shareToWeiXinSession() {
        ShareToWeiXin.shareToWeiXin(Activity(), ShareToWeiXin.TO_WX_SESSION, getString(R.string.ui_nim_app_recent_contact_type_moment), mMoment.getContent(), mMoment.getImage());
    }

    @Override
    protected void shareToWeiXinTimeline() {
        ShareToWeiXin.shareToWeiXin(Activity(), ShareToWeiXin.TO_WX_TIMELINE, getString(R.string.ui_nim_app_recent_contact_type_moment), mMoment.getContent(), mMoment.getImage());
    }

    @Override
    protected void shareToWeiBo() {
        ShareToWeiBo.init(Activity()).share(mMoment.getContent(), mMoment.getImage());
    }

}
