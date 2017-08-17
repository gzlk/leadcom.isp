package com.gzlk.android.isp.fragment.map;

import android.os.Bundle;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.SimpleDialogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.model.common.Address;
import com.hlk.hlklib.lib.inject.ViewId;
import com.netease.nim.uikit.LocationProvider;

/**
 * <b>功能描述：</b>百度地图地址定位页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/16 16:45 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/16 16:45 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class AddressMapPickerFragment extends MapHandleableFragment {

    public static AddressMapPickerFragment newInstance(String params) {
        AddressMapPickerFragment ampf = new AddressMapPickerFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        bundle.putBoolean(PARAM_REDUCE, Boolean.valueOf(strings[0]));
        bundle.putString(PARAM_ADDRESS, StringHelper.replaceJson(strings[1], true));
        ampf.setArguments(bundle);
        return ampf;
    }

    public static LocationProvider.Callback callback;

    private static final String PARAM_REDUCE = "ampf_reduce";

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        isReduce = bundle.getBoolean(PARAM_REDUCE, false);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putBoolean(PARAM_REDUCE, isReduce);
    }

    @ViewId(R.id.ui_map_picker_address)
    private TextView addressView;

    private boolean isReduce = false;

    @Override
    public int getLayout() {
        return R.layout.fragment_map_picker;
    }

//    @Click({R.id.ui_map_picker_relocation})
//    private void elementClick(View view) {
//        tryFetchingLocation();
//    }

    @Override
    public void doingInResume() {
        setCustomTitle(isReduce ? R.string.ui_nim_action_location : R.string.ui_activity_sign_map_picker_fragment_title);
        setRightText(isReduce ? 0 : R.string.ui_base_text_confirm);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                if (!isReduce) {
                    tryResultAddress();
                }
            }
        });
        // 重现地图时不需要中间的定位图标
        //centerPointer.setVisibility(isReduce ? View.GONE : View.VISIBLE);
        // 重现地图时不需要右上角的重新定位图标
        //relocation.setVisibility(isReduce ? View.GONE : View.VISIBLE);
        super.doingInResume();
        if (isReduce) {
            if (!isLocated) {
                isLocated = true;
                // 重现位置信息
                reduceLocation();
                // 重现位置时，不能拖动地图
                setMapScrollEnable(false);
            }
        } else {
            if (!isLocated) {
                startLocation();
            }
        }
    }

    @Override
    protected void reduceLocation() {
        super.reduceLocation();
        addressView.setText(address.getAddress());
    }

    private void tryResultAddress() {
        if (!isLocated) {
            SimpleDialogHelper.init(Activity()).show(R.string.ui_activity_sing_map_picker_locate_failure, R.string.ui_activity_sign_map_picker_locate_button_return, R.string.ui_activity_sign_map_picker_locate_button_relocate, new DialogHelper.OnDialogConfirmListener() {
                @Override
                public boolean onConfirm() {
                    finish();
                    return true;
                }
            }, new DialogHelper.OnDialogCancelListener() {
                @Override
                public void onCancel() {
                    // 获取定位信息
                    //tryFetchingLocation();
                }
            });
        } else {
            if (null != callback) {
                callback.onSuccess(address.getLongitude(), address.getLatitude(), address.getAddress());
            }
            resultData(Address.toJson(address));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        callback = null;
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void onReverseGeoCodeComplete(String address) {
        addressView.setText(address);
    }
}
