package com.gzlk.android.isp.wxapi;

import android.app.Activity;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.helper.ToastHelper;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

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

    @Override
    public void onReq(BaseReq baseReq) {

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
    }
}
