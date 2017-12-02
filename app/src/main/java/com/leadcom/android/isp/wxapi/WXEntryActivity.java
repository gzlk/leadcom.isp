package com.leadcom.android.isp.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.MainActivity;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.helper.LogHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.share.ShareToWeiXin;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * <b>功能描述：</b>接收发送到微信请求的响应结果<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/21 12:36 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/21 12:36 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册API
        api = WXAPIFactory.createWXAPI(this, ShareToWeiXin.APP_ID, false);
        api.handleIntent(getIntent(), this);
        LogHelper.log("savedInstanceState", " sacvsa" + api.handleIntent(getIntent(), this));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    /**
     * 微信主动请求我们
     **/
    @Override
    public void onReq(BaseReq baseReq) {
        try {
            Intent intent = new Intent(App.app(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            App.app().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResp(BaseResp baseResp) {
        // 微信分享回调
        int res = 0;
        if (null != baseResp) {
            switch (baseResp.errCode) {
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    res = R.string.ui_base_share_text_share_to_wx_auth_denied;
                    break;
                case BaseResp.ErrCode.ERR_BAN:
                    break;
                case BaseResp.ErrCode.ERR_COMM:
                    break;
                case BaseResp.ErrCode.ERR_OK:
                    res = R.string.ui_base_share_text_share_to_wx_complete;
                    break;
                case BaseResp.ErrCode.ERR_SENT_FAILED:
                    res = R.string.ui_base_share_text_share_to_wx_sent_failed;
                    break;
                case BaseResp.ErrCode.ERR_UNSUPPORT:
                    res = R.string.ui_base_share_text_share_to_wx_unsupported;
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    res = R.string.ui_base_share_text_share_to_wx_cancel;
                    break;
            }
        }
        if (res != 0) {
            ToastHelper.make().showMsg(res);
        } else {
            ToastHelper.make().showMsg(getString(R.string.ui_base_share_text_share_to_wx_unknown, null == baseResp ? 0 : baseResp.errCode));
        }
        finish();
    }
}
