package com.gzlk.android.isp.fragment.activity.sign;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.activity.AppSignRecordRequest;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.map.MapHandleableFragment;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.SimpleDialogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.activity.sign.AppSignRecord;
import com.gzlk.android.isp.model.activity.sign.AppSigning;
import com.gzlk.android.isp.nim.callback.SignCallback;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredButton;

/**
 * <b>功能描述：</b>签到页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/19 23:05 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/19 23:05 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SignFragment extends MapHandleableFragment {

    public static SignFragment newInstance(String params) {
        SignFragment sf = new SignFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        // 群聊的tid
        bundle.putString(PARAM_TID, strings[0]);
        // 签到应用app的id
        bundle.putString(PARAM_SIGN_ID, strings[1]);
        // 签到内容
        bundle.putString(PARAM_RECORD, strings[2]);
        sf.setArguments(bundle);
        return sf;
    }

    public static SignCallback callback;
    private static boolean hasPermission = false;

    private static final String PARAM_TID = "sf_sign_tid";
    private static final String PARAM_SIGN_ID = "sf_sign_id";
    private static final String PARAM_RECORD = "sf_sign_record";
    private static final String PARAM_LOCATED = "sf_located";

    private String mTID, mSignId;
    private AppSignRecord record;
    private AppSigning signing;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        hasPermission = false;
        checkPermission();
        super.onActivityCreated(savedInstanceState);
    }

    // 尝试读取手机通讯录
    private void checkPermission() {
        if (!hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            String text = StringHelper.getString(R.string.ui_text_permission_location_request);
            String denied = StringHelper.getString(R.string.ui_text_permission_location_denied);
            tryGrantPermission(Manifest.permission.ACCESS_FINE_LOCATION, GRANT_LOCATION, text, denied);
        } else {
            hasPermission = true;
        }
    }

    @Override
    public void permissionGranted(String[] permissions, int requestCode) {
        if (requestCode == GRANT_LOCATION) {
            hasPermission = true;
        }
        super.permissionGranted(permissions, requestCode);
    }

    @Override
    public void permissionGrantFailed(int requestCode) {
        super.permissionGrantFailed(requestCode);
        if (requestCode == GRANT_CONTACTS) {
            setNothingText(R.string.ui_phone_contact_no_permission);
            displayNothing(true);
            finish();
        }
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        isLocated = bundle.getBoolean(PARAM_LOCATED, false);
        mTID = bundle.getString(PARAM_TID, "");
        mSignId = bundle.getString(PARAM_SIGN_ID, "");
        String json = bundle.getString(PARAM_RECORD, "");
        if (!isEmpty(json)) {
            record = AppSignRecord.fromJson(json);
        }
        createRecord();
    }

    private void createRecord() {
        if (null == record) {
            record = new AppSignRecord();
            record.setUserName(Cache.cache().userName);
            record.setSetupId(mSignId);
        }
    }

    private void getLocalSignRecord() {
        record = AppSignRecord.getMyRecord(mSignId);
        createRecord();
        // 未签到时按钮可用
        mSignButton.setEnabled(isEmpty(record.getId()));
        mSignedAddress.setText(record.getSite());
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putBoolean(PARAM_LOCATED, isLocated);
        bundle.putString(PARAM_TID, mTID);
        bundle.putString(PARAM_SIGN_ID, mSignId);
        bundle.putString(PARAM_RECORD, AppSignRecord.toJson(record));
    }

    @ViewId(R.id.ui_activity_sign_timer)
    private TextView mTimerView;
    @ViewId(R.id.ui_activity_sign_address)
    private TextView mAddress;
    @ViewId(R.id.ui_activity_signed_address)
    private TextView mSignedAddress;
    @ViewId(R.id.ui_activity_sign_button)
    private CorneredButton mSignButton;
    @ViewId(R.id.ui_activity_sign_end)
    private TextView mEndTime;

    @Override
    public int getLayout() {
        return R.layout.fragment_activity_sign;
    }

    @Override
    public void doingInResume() {
        if (isEmpty(mSignId)) {
            ToastHelper.make().showMsg(R.string.ui_activity_sign_invalid_sign_setup);
            mSignButton.setEnabled(false);
        } else {
            signing = new Dao<>(AppSigning.class).query(mSignId);
        }
        getLocalSignRecord();
        setCustomTitle(R.string.ui_nim_action_sign);

        setRightText(R.string.ui_activity_sign_right_button_text);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                String json = Json.gson().toJson(signing, new TypeToken<AppSigning>() {
                }.getType());
                SignDetailsFragment.open(SignFragment.this, REQUEST_DELETE, mTID, json);
            }
        });

        mEndTime.setText(getString(R.string.ui_activity_sign_end_time, (null == signing ? "" : signing.getEndDate())));
        super.doingInResume();
        // 不是复现签到位置时，开始定位
        if (isEmpty(record.getId()) && !isLocated && hasPermission) {
            startLocation();
        }
        reduceSignPoint();
    }

    private void reduceSignPoint() {
        if (!isEmpty(record.getId())) {
            address.setLatitude(Double.valueOf(record.getLat()));
            address.setLongitude(Double.valueOf(record.getLon()));
            reduceLocation();
        }
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == REQUEST_DELETE) {
            // 删除之后的返回，直接返回上一页
            finish();
        }
        super.onActivityResult(requestCode, data);
    }

    @Click({R.id.ui_activity_sign_button})
    private void elementClick(View view) {
        if (view.getId() == R.id.ui_activity_sign_button) {
            stopFetchingLocation();
            if (!isLocated) {
                ToastHelper.make().showMsg(R.string.ui_activity_sign_map_picker_location_invalid);
            } else if (signing.couldSignable(record.getCreateDate())) {
                // 将当前定位的位置当作签到位置
                warningSignPosition();
            }
        }
    }

    private void warningSignPosition() {
        SimpleDialogHelper.init(Activity()).show(R.string.ui_activity_sign_warning, R.string.ui_base_text_yes, R.string.ui_base_text_cancel, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                sign();
                return true;
            }
        }, null);
    }

    private void sign() {
        setLoadingText(R.string.ui_activity_sign_signing);
        displayLoading(true);
        AppSignRecordRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<AppSignRecord>() {
            @Override
            public void onResponse(AppSignRecord record, boolean success, String message) {
                super.onResponse(record, success, message);
                if (success) {
                    if (null != callback) {
                        callback.onSuccess();
                    }
                    finish();
                }
            }
        }).add(record);
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
        mAddress.setText(address);
        if (!isEmpty(address)) {
            createRecord();
            record.setCreateDate(Utils.format(getString(R.string.ui_base_text_date_time_format), null));
            record.setLon(format("%.6f", this.address.getLongitude()));
            record.setLat(format("%.6f", this.address.getLatitude()));
            record.setSite(address);
            record.setAlt(format("%.6f", this.address.getAltitude()));
            mTimerView.setText(formatDateTime(record.getCreateDate()));
            mAddress.setText(record.getSite());
        }
    }
}
