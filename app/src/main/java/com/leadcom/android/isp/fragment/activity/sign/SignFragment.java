package com.leadcom.android.isp.fragment.activity.sign;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.activity.AppSignRecordRequest;
import com.leadcom.android.isp.api.activity.AppSigningRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.map.MapHandleableFragment;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.helper.GaodeHelper;
import com.leadcom.android.isp.helper.popup.SimpleDialogHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.activity.sign.AppSignRecord;
import com.leadcom.android.isp.model.activity.sign.AppSigning;
import com.leadcom.android.isp.nim.callback.SignCallback;
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

    public static void open(BaseFragment fragment, String tid, String signId, String recordJson) {
        String params = format("%s,%s,%s", tid, signId, recordJson);
        fragment.openActivity(SignFragment.class.getName(), params, true, false);
    }

    public static SignCallback callback;

    private static final String PARAM_TID = "sf_sign_tid";
    private static final String PARAM_SIGN_ID = "sf_sign_id";
    private static final String PARAM_RECORD = "sf_sign_record";
    private static final String PARAM_LOCATED = "sf_located";
    private static final String PARAM_STARTED = "sf_started";

    private String mTID, mSignId;
    private boolean mStarted = false;
    private AppSignRecord record;
    private AppSigning signing;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mStarted = bundle.getBoolean(PARAM_STARTED, false);
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
        boolean signed = !isEmpty(record.getId());
        mSignButton.setEnabled(!signed);
        mSignButton.setText(signed ? R.string.ui_activity_sign_signed : R.string.ui_nim_action_sign);
        mSignInfo.setVisibility(signed ? View.GONE : View.VISIBLE);
        mSignedAddress.setText(record.getSite());
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putBoolean(PARAM_STARTED, mStarted);
        bundle.putBoolean(PARAM_LOCATED, isLocated);
        bundle.putString(PARAM_TID, mTID);
        bundle.putString(PARAM_SIGN_ID, mSignId);
        bundle.putString(PARAM_RECORD, AppSignRecord.toJson(record));
    }

    @ViewId(R.id.ui_activity_sign_title)
    private TextView mTitleView;
    @ViewId(R.id.ui_activity_sign_time)
    private TextView mTimeView;
    @ViewId(R.id.ui_activity_sign_info)
    private LinearLayout mSignInfo;
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
        super.doingInResume();
        if (isEmpty(mSignId)) {
            ToastHelper.make().showMsg(R.string.ui_activity_sign_invalid_sign_setup);
            mSignButton.setEnabled(false);
        } else if (null == signing) {
            signing = new Dao<>(AppSigning.class).query(mSignId);
            if (null == signing) {
                fetchingSigning();
            }
        }
        initializeSigningInfo();
        //setCustomTitle(R.string.ui_nim_action_sign);

        setRightText(R.string.ui_activity_sign_right_button_text);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                String json = Json.gson().toJson(signing, new TypeToken<AppSigning>() {
                }.getType());
                SignDetailsFragment.open(SignFragment.this, REQUEST_DELETE, mTID, json);
            }
        });

    }

    private void fetchingSigning() {
        AppSigningRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<AppSigning>() {
            @Override
            public void onResponse(AppSigning appSigning, boolean success, String message) {
                super.onResponse(appSigning, success, message);
                if (success) {
                    signing = appSigning;
                    initializeSigningInfo();
                }
            }
        }).find(mSignId, AppSigningRequest.FIND_RECORD);
    }

    private void initializeSigningInfo() {
        if (null != signing) {
            setCustomTitle(signing.getTitle());
            mTitleView.setText(Html.fromHtml(getString(R.string.ui_activity_sign_title_text, signing.getContent())));
            String begin = formatDate(signing.getBeginDate(), R.string.ui_base_text_date_time_format_hhmm);
            String end = formatDate(signing.getEndDate(), R.string.ui_base_text_time_format_hhmm);
            mTimeView.setText(Html.fromHtml(getString(R.string.ui_activity_sign_title_time, begin, end)));
            mEndTime.setText(getString(R.string.ui_activity_sign_end_time, (null == signing ? getString(R.string.ui_base_text_not_set) : signing.getEndDate())));
            getLocalSignRecord();

            // 不是复现签到位置时，开始定位
            if (isEmpty(record.getId()) && !isLocated && hasPermission) {
                startLocation();
            }
            if (!mStarted) {
                mStarted = true;
                beginCheckSigningTime();
            }
        }
    }

    @Override
    protected void onMapLoadedComplete() {
        super.onMapLoadedComplete();
        reduceSignPoint();
    }

    private void beginCheckSigningTime() {
        if (mStarted) {
            Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkSigningTime();
                }
            }, GaodeHelper.SI_3);
        }
    }

    private void checkSigningTime() {
        if (null == signing) {
            signing = new Dao<>(AppSigning.class).query(mSignId);
        }
        long begin = Utils.parseDate(getString(R.string.ui_base_text_date_time_format), signing.getBeginDate()).getTime();
        long end = Utils.parseDate(getString(R.string.ui_base_text_date_time_format), signing.getEndDate()).getTime();
        long now = Utils.timestamp();
        boolean signable = now >= begin && now <= end;
        boolean signed = !isEmpty(record.getId());
        mSignButton.setEnabled(signable && !signed);
        mSignButton.setText(signed ? R.string.ui_activity_sign_signed :
                (signable ? R.string.ui_nim_action_sign :
                        now < begin ? R.string.ui_activity_sign_not_begin :
                                now > end ? R.string.ui_activity_vote_details_status_ended :
                                        R.string.ui_nim_action_sign));
        resetSignableInfo();
        if (signed || now > end) {
            // 已签到或已结束时，不再继续循环判断当前时间
            mStarted = false;
        }
        beginCheckSigningTime();
    }

    @Override
    public void onStop() {
        Handler().removeCallbacksAndMessages(null);
        enableMyLocation(false);
        super.onStop();
    }

    private void reduceSignPoint() {
        if (!isEmpty(record.getId())) {
            isLocated = true;
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
        mStarted = false;
        setLoadingText(R.string.ui_activity_sign_signing);
        displayLoading(true);
        AppSignRecordRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<AppSignRecord>() {
            @Override
            public void onResponse(AppSignRecord record, boolean success, String message) {
                super.onResponse(record, success, message);
                displayLoading(false);
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
            record.setLon(format("%.6f", this.address.getLongitude()));
            record.setLat(format("%.6f", this.address.getLatitude()));
            record.setSite(address);
            record.setAlt(format("%.6f", this.address.getAltitude()));
            resetSignableInfo();
        }
    }

    private void resetSignableInfo() {
        if (isEmpty(record.getId())) {
            record.setCreateDate(Utils.format(getString(R.string.ui_base_text_date_time_format), null));
        }
        mTimerView.setText(formatDateTime(record.getCreateDate()));
        mAddress.setText(record.getSite());
    }
}
