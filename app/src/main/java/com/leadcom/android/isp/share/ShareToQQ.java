package com.leadcom.android.isp.share;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>分享到QQ或QZone<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/09/30 10:29 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/09/30 10:29 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ShareToQQ extends Shareable {

    private static Tencent mTencent;

    @SuppressWarnings("ConstantConditions")
    private static void initTencent(Context context) {
        if (null == mTencent) {
            String appid = StringHelper.getString(R.string.tencent_app_id_qq);
            mTencent = Tencent.createInstance(appid.replace("tencent", ""), context);
        }
    }

    @SuppressWarnings("AccessStaticViaInstance")
    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_QQ_SHARE || requestCode == Constants.REQUEST_QZONE_SHARE) {
            if (resultCode == Constants.ACTIVITY_OK) {
                Tencent.handleResultData(data, new UiListener(TO_QQ));
            }
        }
    }

    private static class UiListener implements IUiListener {
        private int type;

        UiListener(int type) {
            super();
            this.type = type;
        }

        @Override
        public void onComplete(Object o) {
            ToastHelper.make().showMsg(type == TO_QQ ? R.string.ui_base_share_text_share_to_qq_complete : R.string.ui_base_share_text_share_to_qzone_complete);
        }

        @Override
        public void onError(UiError uiError) {
            ToastHelper.make().showMsg(StringHelper.getString(type == TO_QQ ? R.string.ui_base_share_text_share_to_qq_failed : R.string.ui_base_share_text_share_to_qzone_failed, uiError.errorMessage, uiError.errorCode, uiError.errorDetail));
        }

        @Override
        public void onCancel() {
            ToastHelper.make().showMsg(type == TO_QQ ? R.string.ui_base_share_text_share_to_qq_cancel : R.string.ui_base_share_text_share_to_qzone_cancel);
        }
    }

    /**
     * 分享内容到QQ
     *
     * @param type        分享类型，如QQ或QZone
     * @param activity    分享操作的宿主窗口
     * @param title       分享的标题
     * @param summary     分享的摘要
     * @param targetUrl   用户点击分享内容时跳转到的地址
     * @param imageUrl    分享的图片地址（纯图片时为本地图片路径）
     * @param multiImages 分享到QQ空间时的多图内容
     */
    public static void shareToQQ(@ShareType int type, BaseActivity activity, String title, String summary,
                                 String targetUrl, String imageUrl, ArrayList<String> multiImages) {
        initTencent(activity.getApplicationContext());
        if (type == TO_QQ) {
            mTencent.shareToQQ(activity, getBundle(type, title, summary, targetUrl, imageUrl, multiImages), new UiListener(TO_QQ));
        } else if (type == TO_QZONE) {
            mTencent.shareToQzone(activity, getBundle(type, title, summary, targetUrl, imageUrl, multiImages), new UiListener(TO_QZONE));
        }
    }

    private static Bundle getBundle(@ShareType int type, String title, String summary, String targetUrl,
                                    String imageUrl, ArrayList<String> multiImages) {
        Bundle bundle = new Bundle();
        if (type == TO_QQ) {
            // 分享到QQ会话时，需要设置type
            if (isEmpty(title) && isEmpty(summary) && isEmpty(targetUrl)) {
                // 纯图(纯图时必须是本地图片?)
                bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
                if (imageUrl.charAt(0) != '/') {
                    bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, getLocalPath(imageUrl));
                } else {
                    bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imageUrl);
                }
            } else {
                // 图文消息
                bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                bundle.putString(QQShare.SHARE_TO_QQ_TITLE, title);
                bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
                bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, isEmpty(targetUrl) ? "http://www.baidu.com" : targetUrl);
                bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
            }
            // app name
            bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, StringHelper.getString(R.string.app_name_default));
            // 默认可以直接分享到QZone
            bundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
        } else if (type == TO_QZONE) {
            // 分享到QQ空间只能是图文方式
            bundle.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
            bundle.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);//必填
            bundle.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summary);//选填
            bundle.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, targetUrl);//必填
            bundle.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, multiImages);
            bundle.putString(QzoneShare.SHARE_TO_QQ_APP_NAME, StringHelper.getString(R.string.app_name_default));
        }
        return bundle;
    }
}
