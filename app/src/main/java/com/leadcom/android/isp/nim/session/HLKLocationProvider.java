package com.leadcom.android.isp.nim.session;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.map.AddressMapPickerFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.helper.popup.SimpleDialogHelper;
import com.leadcom.android.isp.model.common.Address;
import com.netease.nim.uikit.api.model.location.LocationProvider;
import com.netease.nim.uikit.common.util.log.LogUtil;

/**
 * <b>功能描述：</b>百度地图定位提供者<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/17 18:30 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/17 18:30 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class HLKLocationProvider implements LocationProvider {

    @Override
    public void requestLocation(final Context context, Callback callback) {
        if (!isLocationEnable(context)) {
            SimpleDialogHelper.init((AppCompatActivity) context).show(R.string.ui_nim_location_service_invalid, R.string.ui_base_text_setting, R.string.ui_base_text_cancel, new DialogHelper.OnDialogConfirmListener() {
                @Override
                public boolean onConfirm() {
                    openLocationServiceSetting(context);
                    return true;
                }
            }, null);
            return;
        }
        AddressMapPickerFragment.callback = callback;
        AddressMapPickerFragment.open(context, false, "");
    }

    @Override
    public void openMap(Context context, double longitude, double latitude, String address) {
        if (Double.isNaN(longitude) || Double.isNaN(latitude) || StringHelper.isEmpty(address)) {
            ToastHelper.make().showMsg(R.string.ui_activity_sign_map_picker_location_invalid);
            return;
        }
        // 打开地图页面重现地图位置
        Address addr = new Address();
        addr.setAddress(address);
        addr.setLatitude(latitude);
        addr.setLongitude(longitude);
        AddressMapPickerFragment.open(context, true, Address.toJson(addr));
    }

    // 打开位置服务设置页面
    private void openLocationServiceSetting(Context context) {
        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            LogUtil.e("LOC", "start ACTION_LOCATION_SOURCE_SETTINGS error");
        }
    }

    public static boolean isLocationEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria cri = new Criteria();
        cri.setAccuracy(Criteria.ACCURACY_COARSE);
        cri.setAltitudeRequired(false);
        cri.setBearingRequired(false);
        cri.setCostAllowed(false);
        String bestProvider = locationManager.getBestProvider(cri, true);
        return !TextUtils.isEmpty(bestProvider);

    }
}
