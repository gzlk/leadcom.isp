package com.leadcom.android.isp.fragment.activity.sign;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.google.gson.reflect.TypeToken;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.activity.ActRequest;
import com.leadcom.android.isp.api.activity.AppSigningRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseTransparentSupportFragment;
import com.leadcom.android.isp.fragment.map.AddressMapPickerFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.common.SimpleClickableViewHolder;
import com.leadcom.android.isp.holder.common.SimpleInputableViewHolder;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.activity.Activity;
import com.leadcom.android.isp.model.activity.sign.AppSigning;
import com.leadcom.android.isp.model.common.Address;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * <b>功能描述：</b>新建签到<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/16 18:08 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/16 18:08 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SignCreatorFragment extends BaseTransparentSupportFragment {

    private static final String PARAM1 = "scf_param_sign_content";

    public static SignCreatorFragment newInstance(String params) {
        SignCreatorFragment scf = new SignCreatorFragment();
        Bundle bundle = new Bundle();
        // 网易云传过来的活动的tid
        bundle.putString(PARAM_QUERY_ID, params);
        scf.setArguments(bundle);
        return scf;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        String json = bundle.getString(PARAM1, "");
        if (!isEmpty(json)) {
            signing = Json.gson().fromJson(json, new TypeToken<AppSigning>() {
            }.getType());
        }
        if (null == signing) {
            signing = new AppSigning();
        }
    }

    @Override
    protected boolean checkStillEditing() {
        // 标题和内容不为空时，返回上一页的时候询问是否放弃
        return (!isEmpty(titleHolder.getValue()) || !isEmpty(contentView.getValue()));
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        saveSigning();
        bundle.putString(PARAM1, Json.gson().toJson(signing, new TypeToken<AppSigning>() {
        }.getType()));
    }

    private void saveSigning() {
        signing.setTitle(titleHolder.getValue());
        signing.setContent(contentView.getValue());
    }

    private AppSigning signing;

    @ViewId(R.id.ui_activity_sign_creator_title)
    private View titleView;
    @ViewId(R.id.ui_activity_sign_creator_content)
    private ClearEditText contentView;
    @ViewId(R.id.ui_activity_sign_creator_address)
    private View addressView;
    @ViewId(R.id.ui_activity_sign_creator_begin)
    private View beginView;
    @ViewId(R.id.ui_activity_sign_creator_end)
    private View endView;
    @ViewId(R.id.ui_activity_sign_creator_notify)
    private View notifyView;

    private SimpleInputableViewHolder titleHolder;
    private SimpleClickableViewHolder addressHolder, beginHolder, endHolder, notifyHolder;

    private String[] items, ntfOptions;

    @Override
    public int getLayout() {
        return R.layout.fragment_activity_sing_creator;
    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_activity_sign_creator_fragment_title);
        setRightText(R.string.ui_base_text_publish);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                tryPublishSign();
            }
        });
        initializeHolders();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    private void tryPublishSign() {
        if (isEmpty(signing.getActId())) {
            ToastHelper.make().showMsg(R.string.ui_activity_details_invalid_parameter);
            return;
        }
        String title = titleHolder.getValue();
        if (isEmpty(title)) {
            ToastHelper.make().showMsg(R.string.ui_activity_sign_creator_title_invalid);
            return;
        }
        String content = contentView.getValue();
        if (isEmpty(content)) {
            ToastHelper.make().showMsg(R.string.ui_activity_sign_creator_content_invalid);
            return;
        }
        if (isEmpty(signing.getBeginDate())) {
            ToastHelper.make().showMsg(R.string.ui_activity_sign_creator_begin_time_invalid);
            return;
        }
        if (isEmpty(signing.getEndDate())) {
            ToastHelper.make().showMsg(R.string.ui_activity_sign_creator_end_time_invalid);
            return;
        }
        long begin = Utils.parseDate(StringHelper.getString(R.string.ui_base_text_date_time_format), signing.getBeginDate()).getTime();
        long end = Utils.parseDate(StringHelper.getString(R.string.ui_base_text_date_time_format), signing.getEndDate()).getTime();
        if (end < begin) {
            ToastHelper.make().showMsg(R.string.ui_activity_sign_creator_end_time_invalid1);
            return;
        }
        publishSign();
    }

    private void publishSign() {
        setLoadingText(R.string.ui_activity_sign_creator_publishing);
        displayLoading(true);
        AppSigningRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<AppSigning>() {
            @Override
            public void onResponse(AppSigning signing, boolean success, String message) {
                super.onResponse(signing, success, message);
                if (success) {
                    ToastHelper.make().showMsg(R.string.ui_activity_sign_creator_published);
                    if (null != signing) {
                        resultData(Json.gson().toJson(signing, new TypeToken<AppSigning>() {
                        }.getType()));
                    }
                } else {
                    ToastHelper.make().showMsg(message);
                }
                displayLoading(false);
            }
        }).add(signing);
    }

    private void fetchingActivity() {
        ActRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Activity>() {
            @Override
            public void onResponse(Activity activity, boolean success, String message) {
                super.onResponse(activity, success, message);
                if (success && null != activity) {
                    signing.setActId(activity.getId());
                } else {
                    ToastHelper.make().showMsg(R.string.ui_activity_details_invalid_parameter);
                }
            }
        }).findByTid(mQueryId);
    }

    private void initializeHolders() {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_activity_sign_creator_items);
            fetchingActivity();
        }
        if (null == titleHolder) {
            titleHolder = new SimpleInputableViewHolder(titleView, this);
        }
        titleHolder.showContent(format(items[0], signing.getTitle()));
        if (null == addressHolder) {
            addressHolder = new SimpleClickableViewHolder(addressView, this);
            addressHolder.addOnViewHolderClickListener(onViewHolderClickListener);
        }
        addressHolder.showContent(format(items[2], signing.getSite()));
        if (null == beginHolder) {
            beginHolder = new SimpleClickableViewHolder(beginView, this);
            beginHolder.addOnViewHolderClickListener(onViewHolderClickListener);
        }
        beginHolder.showContent(format(items[3], formatDateTime(signing.getBeginDate())));
        if (null == endHolder) {
            endHolder = new SimpleClickableViewHolder(endView, this);
            endHolder.addOnViewHolderClickListener(onViewHolderClickListener);
        }
        endHolder.showContent(format(items[4], formatDateTime(signing.getEndDate())));
        if (null == notifyHolder) {
            notifyHolder = new SimpleClickableViewHolder(notifyView, this);
            notifyHolder.addOnViewHolderClickListener(onViewHolderClickListener);
        }
        notifyHolder.showContent(format(items[5], getNotifyBeginTime()));
    }

    private String getNotifyBeginTime() {
        if (null == ntfOptions) {
            ntfOptions = StringHelper.getStringArray(R.array.ui_activity_sign_notify_time);
        }
        switch (signing.getNotifyBeginTime()) {
            case 0:
                return ntfOptions[4];
            case 10:
                return ntfOptions[0];
            case 20:
                return ntfOptions[1];
            case 30:
                return ntfOptions[2];
            case 60:
                return ntfOptions[3];
            default:
                return ntfOptions[4];
        }
    }

    private static final int REQ_ADDRESS = ACTIVITY_BASE_REQUEST + 10;

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            switch (index) {
                case 0:
                    // 选择签到地址
                    openActivity(AddressMapPickerFragment.class.getName(), "false,", REQ_ADDRESS, true, false);
                    break;
                case 1:
                    // 选择签到开始时间
                    openDateTimePickerBegin();
                    break;
                case 2:
                    // 签到结束时间
                    openDateTimePickerEnd();
                    break;
                case 3:
                    // 签到通知时间
                    openOptionsPicker();
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == REQ_ADDRESS) {
            Address address = Address.fromJson(getResultedData(data));
            signing.setSite(address.getAddress());
            signing.setLat(String.valueOf(address.getLatitude()));
            signing.setLon(String.valueOf(address.getLongitude()));
            initializeHolders();
        }
        super.onActivityResult(requestCode, data);
    }

    private void showDateTime(Date date, boolean forStart) {
        String string = Utils.format(StringHelper.getString(R.string.ui_base_text_date_time_format), date);
        if (forStart) {
            signing.setBeginDate(string);
        } else {
            signing.setEndDate(string);
        }
        initializeHolders();
    }

    private TimePickerView tpvStart;

    private void openDateTimePickerBegin() {
        Utils.hidingInputBoard(contentView);
        if (null == tpvStart) {
            tpvStart = new TimePickerView.Builder(Activity(), new TimePickerView.OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    showDateTime(date, true);
                }
            }).setType(new boolean[]{true, true, true, true, true, false})
                    .setTitleBgColor(getColor(R.color.colorPrimary))
                    .setSubmitColor(Color.WHITE)
                    .setCancelColor(Color.WHITE)
                    .setContentSize(getFontDimension(R.dimen.ui_base_text_size))
                    .setOutSideCancelable(false)
                    .isCenterLabel(true).isDialog(false).build();
            if (isEmpty(signing.getBeginDate())) {
                tpvStart.setDate(Calendar.getInstance());
                String string = Utils.format(StringHelper.getString(R.string.ui_base_text_date_time_format), Calendar.getInstance().getTime());
                signing.setBeginDate(string);
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(Utils.parseDate(StringHelper.getString(R.string.ui_base_text_date_time_format), signing.getBeginDate()));
                tpvStart.setDate(calendar);
            }
        }
        tpvStart.show();
    }

    private TimePickerView tpvEnd;

    private void openDateTimePickerEnd() {
        Utils.hidingInputBoard(contentView);
        if (null == tpvEnd) {
            tpvEnd = new TimePickerView.Builder(Activity(), new TimePickerView.OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    showDateTime(date, false);
                }
            }).setType(new boolean[]{true, true, true, true, true, false})
                    .setTitleBgColor(getColor(R.color.colorPrimary))
                    .setSubmitColor(Color.WHITE)
                    .setCancelColor(Color.WHITE)
                    .setContentSize(getFontDimension(R.dimen.ui_base_text_size))
                    .setOutSideCancelable(false)
                    .isCenterLabel(true).isDialog(false).build();
            if (isEmpty(signing.getEndDate())) {
                tpvEnd.setDate(Calendar.getInstance());
                String string = Utils.format(StringHelper.getString(R.string.ui_base_text_date_time_format), Calendar.getInstance().getTime());
                signing.setEndDate(string);
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(Utils.parseDate(StringHelper.getString(R.string.ui_base_text_date_time_format), signing.getEndDate()));
                tpvEnd.setDate(calendar);
            }
        }
        tpvEnd.show();
    }

    private OptionsPickerView optionsPickerView;
    private ArrayList<String> options;

    private int getNotifyTimes(int index) {
        switch (index) {
            case 0:
                return 10;
            case 1:
                return 20;
            case 2:
                return 30;
            case 3:
                return 60;
            default:
                return 0;
        }
    }

    @SuppressWarnings("unchecked")
    private void openOptionsPicker() {
        if (null == ntfOptions) {
            ntfOptions = StringHelper.getStringArray(R.array.ui_activity_sign_notify_time);
        }
        if (null == options) {
            options = new ArrayList<>();
            options.addAll(Arrays.asList(ntfOptions));
        }
        if (null == optionsPickerView) {
            optionsPickerView = new OptionsPickerView.Builder(Activity(), new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int i, int i1, int i2, View view) {
                    signing.setNotifyBeginTime(getNotifyTimes(i));
                    initializeHolders();
                }
            }).setTitleBgColor(getColor(R.color.colorPrimary))
                    .setSubmitColor(Color.WHITE)
                    .setCancelColor(Color.WHITE)
                    .setContentTextSize(getFontDimension(R.dimen.ui_base_text_size))
                    .setOutSideCancelable(true).setSelectOptions(4)
                    .isCenterLabel(true).isDialog(false).setLabels("", "", "").build();
            optionsPickerView.setPicker(options);
        }
        optionsPickerView.show();
    }
}
