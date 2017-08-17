package com.gzlk.android.isp.fragment.activity.sign;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.amap.api.maps2d.model.LatLng;
import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.activity.AppSignRecordRequest;
import com.gzlk.android.isp.api.activity.AppSigningRequest;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.fragment.map.AddressMapPickerFragment;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.GaodeHelper;
import com.gzlk.android.isp.helper.SimpleDialogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.activity.SingingViewHolder;
import com.gzlk.android.isp.holder.common.SimpleClickableViewHolder;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.lib.view.ExpandableTextView;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.activity.sign.AppSignRecord;
import com.gzlk.android.isp.model.activity.sign.AppSigning;
import com.gzlk.android.isp.model.common.Address;
import com.gzlk.android.isp.nim.constant.SigningNotifyType;
import com.gzlk.android.isp.nim.model.extension.SigningNotifyAttachment;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.Date;
import java.util.List;

/**
 * <b>功能描述：</b>签到详情页面，可以提醒、查看签到人员列表等<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/19 13:45 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/19 13:45 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SignDetailsFragment extends BaseSwipeRefreshSupportFragment {

    public static SignDetailsFragment newInstance(String params) {
        SignDetailsFragment sdf = new SignDetailsFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        // 签到应用的群聊tid
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        // 签到应用的pojo对象
        bundle.putString(PARAM_POJO, strings[1]);
        sdf.setArguments(bundle);
        return sdf;
    }

    public static void open(BaseFragment fragment, int req, String tid, String voteJson) {
        fragment.openActivity(SignDetailsFragment.class.getName(),
                format("%s,%s", tid, StringHelper.replaceJson(voteJson, false)),
                req, true, false);
    }

    private static final String PARAM_POJO = "sdf_param_pojo";
    private static final String PARAM_TYPE = "sdf_param_type";

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        pojo = bundle.getString(PARAM_POJO, "");
        notifyType = bundle.getInt(PARAM_TYPE, 1);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_POJO, pojo);
        bundle.putInt(PARAM_TYPE, notifyType);
    }

    @ViewId(R.id.ui_activity_sign_details_title)
    private View titleView;
    @ViewId(R.id.ui_activity_sign_details_content)
    private ExpandableTextView contentView;
    @ViewId(R.id.ui_activity_sign_details_notify)
    private LinearLayout notifyContainer;
    @ViewId(R.id.ui_activity_sign_details_notify_end_icon_1)
    private CustomTextView endIcon1;
    @ViewId(R.id.ui_activity_sign_details_notify_end_icon_2)
    private CustomTextView endIcon2;
    @ViewId(R.id.ui_activity_sign_details_notify_end_icon_3)
    private CustomTextView endIcon3;
    @ViewId(R.id.ui_activity_sign_details_notify_end_icon_4)
    private CustomTextView endIcon4;
    @ViewId(R.id.ui_activity_sign_details_notify_content)
    private ClearEditText notifyContent;

    private SimpleClickableViewHolder titleHolder;

    private int notifyType = 1;
    private String pojo = "";
    private AppSigning signing;
    private SignRecordAdapter mAdapter;

    @Override
    public int getLayout() {
        return R.layout.fragment_activity_sign_details;
    }

    @Override
    protected void onSwipeRefreshing() {
        fetchingSignRecords();
    }

    @Override
    protected void onLoadingMore() {
        fetchingSignRecords();
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_activity_sign_creator_fragment_title);
        setNothingText(R.string.ui_activity_sign_details_no_sign_records);
        initializeHolder();
        initializeAdapter();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    @Click({R.id.ui_activity_sign_details_notify_button,
            R.id.ui_activity_sign_details_notify_end_selector_1, R.id.ui_activity_sign_details_notify_end_selector_2,
            R.id.ui_activity_sign_details_notify_end_selector_3, R.id.ui_activity_sign_details_notify_end_selector_4})
    private void elementClick(View view) {
        switch (view.getId()) {
            case R.id.ui_activity_sign_details_notify_end_selector_1:
                notifyType = SigningNotifyType.ALMOST_START;
                resetNotifyContent();
                break;
            case R.id.ui_activity_sign_details_notify_end_selector_2:
                notifyType = SigningNotifyType.STARTED;
                resetNotifyContent();
                break;
            case R.id.ui_activity_sign_details_notify_end_selector_3:
                notifyType = SigningNotifyType.ALMOST_END;
                resetNotifyContent();
                break;
            case R.id.ui_activity_sign_details_notify_end_selector_4:
                notifyType = SigningNotifyType.ENDED;
                resetNotifyContent();
                break;
            case R.id.ui_activity_sign_details_notify_button:
                // 发布消息提醒
                publishNotify();
                break;
        }
    }

    private void resetNotifyContent() {
        int color1 = getColor(R.color.colorPrimary);
        int color2 = getColor(R.color.textColorHintLightLight);
        endIcon1.setTextColor(notifyType == SigningNotifyType.ALMOST_START ? color1 : color2);
        endIcon2.setTextColor(notifyType == SigningNotifyType.STARTED ? color1 : color2);
        endIcon3.setTextColor(notifyType == SigningNotifyType.ALMOST_END ? color1 : color2);
        endIcon4.setTextColor(notifyType == SigningNotifyType.ENDED ? color1 : color2);

        notifyContent.setValue(getDefaultNotifyContent());
        notifyContent.focusEnd();
    }

    private String getDefaultNotifyContent() {
        switch (notifyType) {
            case SigningNotifyType.ALMOST_START:
                return getString(R.string.ui_activity_sign_details_notify_text_1);
            case SigningNotifyType.STARTED:
                return getString(R.string.ui_activity_sign_details_notify_text_2);
            case SigningNotifyType.ALMOST_END:
                return getString(R.string.ui_activity_sign_details_notify_text_3);
            default:
                return getString(R.string.ui_activity_sign_details_notify_text_4);
        }
    }

    // 在群内发布签到开始或结束提醒
    private void publishNotify() {
        SigningNotifyAttachment notify = new SigningNotifyAttachment();
        notify.setTitle(signing.getTitle());
        notify.setContent(notifyContent.getValue());
        notify.setAddress(signing.getSite());
        notify.setNotifyType(notifyType);
        notify.setSetupId(signing.getId());
        notify.setTid(mQueryId);
        notify.setBeginTime(signing.getBeginDate());
        notify.setEndTime(signing.getEndDate());
        IMMessage message;
        message = MessageBuilder.createCustomMessage(mQueryId, SessionTypeEnum.Team, notify);
        NIMClient.getService(MsgService.class).sendMessage(message, false);
    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    private void fetchingSignRecords() {
        setLoadingText(R.string.ui_activity_sign_details_loading_sign_records);
        displayLoading(true);
        displayNothing(false);
        AppSignRecordRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<AppSignRecord>() {
            @Override
            public void onResponse(List<AppSignRecord> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    if (null != list) {
                        if (list.size() >= pageSize) {
                            remotePageNumber++;
                            isLoadingComplete(false);
                        } else {
                            isLoadingComplete(true);
                        }
                        mAdapter.update(list, false);
                    } else {
                        isLoadingComplete(true);
                    }
                } else {
                    isLoadingComplete(true);
                }
                displayLoading(false);
                displayNothing(mAdapter.getItemCount() < 1);
                stopRefreshing();
            }
        }).list(signing.getId());
    }

    private void setRightIconEvent() {
        setRightText(R.string.ui_base_text_delete);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                warningDelete();
            }
        });
    }

    private void warningDelete() {
        SimpleDialogHelper.init(Activity()).show(R.string.ui_activity_sing_details_delete_warning, R.string.ui_base_text_yes, R.string.ui_base_text_cancel, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                delete();
                return true;
            }
        }, null);
    }

    private void delete() {
        final String id = signing.getId();
        AppSigningRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<AppSigning>() {
            @Override
            public void onResponse(AppSigning signing, boolean success, String message) {
                super.onResponse(signing, success, message);
                if (success) {
                    resultData(id);
                }
            }
        }).delete(id);
    }

    private void initializeHolder() {
        if (null == signing) {
            signing = Json.gson().fromJson(StringHelper.replaceJson(pojo, true), new TypeToken<AppSigning>() {
            }.getType());
            setCustomTitle(signing.getTitle());
            resetNotifyContent();
        }
        boolean isMe = signing.getCreatorId().equals(Cache.cache().userId);
        //notifyContainer.setVisibility(isMe && signing.couldSignable(Utils.formatDateTime(new Date())) ? View.VISIBLE : View.GONE);

        if (null == titleHolder) {
            titleHolder = new SimpleClickableViewHolder(titleView, SignDetailsFragment.this);
            if (isMe) {
                setRightIconEvent();
            }
        }
        titleHolder.showContent(format(getString(R.string.ui_activity_sign_details_title), signing.getTitle()));

        contentView.setText(signing.getContent());
        contentView.makeExpandable();
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new SignRecordAdapter();
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setNestedScrollingEnabled(true);
            fetchingSignRecords();
        }
    }

    private double stringToDouble(String string, double dftValue) {
        if (isEmpty(string)) {
            return dftValue;
        }
        try {
            return Double.valueOf(string);
        } catch (Exception e) {
            e.printStackTrace();
            return dftValue;
        }
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            // 个人签到详情
            AppSignRecord record = mAdapter.get(index);
            Address addr = new Address();
            // 默认北京天安门
            addr.setLongitude(stringToDouble(record.getLon(), 116.400244));
            addr.setLatitude(stringToDouble(record.getLat(), 39.963175));
            addr.setAltitude(stringToDouble(record.getAlt(), 0.0));
            addr.setAddress(record.getSite());
            String params = StringHelper.format("true,%s", StringHelper.replaceJson(Address.toJson(addr), false));
            // 重现用户的签到地址
            openActivity(AddressMapPickerFragment.class.getName(), params, true, false);
        }
    };

    private class SignRecordAdapter extends RecyclerViewAdapter<SingingViewHolder, AppSignRecord> {

        @Override
        public SingingViewHolder onCreateViewHolder(View itemView, int viewType) {
            SingingViewHolder holder = new SingingViewHolder(itemView, SignDetailsFragment.this);
            holder.addOnViewHolderClickListener(onViewHolderClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_activity_signing_item;
        }

        @Override
        public void onBindHolderOfView(SingingViewHolder holder, int position, AppSignRecord item) {
//            if (isEmpty(item.getDistance())) {
//                item.setDistance(calculateDistance(item.getLat(), item.getLon()));
//            }
            holder.showContent(item);
        }

        private String calculateDistance(String latitude, String longitude) {
            double lat1 = Double.valueOf(latitude), lon1 = Double.valueOf(longitude);
            double lat0 = Double.valueOf(signing.getLat()), lon0 = Double.valueOf(signing.getLon());
            double distance = GaodeHelper.getDistance(new LatLng(lat1, lon1), new LatLng(lat0, lon0));
            return Utils.formatDistance(distance);
        }

        @Override
        protected int comparator(AppSignRecord item1, AppSignRecord item2) {
            return 0;
        }
    }
}
