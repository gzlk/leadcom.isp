package com.leadcom.android.isp.nim.action;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import com.leadcom.android.isp.R;
import com.netease.nim.uikit.LocationProvider;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.session.actions.BaseAction;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * <b>功能描述：</b>发送位置信息<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/14 09:26 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/14 09:26 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class LocationAction extends BaseAction {

    /**
     * 位置
     */
    public LocationAction() {
        super(R.drawable.nim_action_location, R.string.ui_nim_action_location);
    }

    @Override
    public void onClick() {
        // 是否有定位权限
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            requestLocation();
        } else {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void requestLocation() {
        if (NimUIKit.getLocationProvider() != null) {
            NimUIKit.getLocationProvider().requestLocation(getActivity(), new LocationProvider.Callback() {
                @Override
                public void onSuccess(double longitude, double latitude, String address) {
                    IMMessage message = MessageBuilder.createLocationMessage(getAccount(), getSessionType(), latitude, longitude, address);
                    sendMessage(message);
                }
            });
        }
    }
}
