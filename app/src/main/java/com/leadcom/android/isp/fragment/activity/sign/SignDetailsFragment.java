package com.leadcom.android.isp.fragment.activity.sign;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.activity.AppSigningRecordRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.map.AddressMapPickerFragment;
import com.leadcom.android.isp.holder.activity.SingingViewHolder;
import com.leadcom.android.isp.holder.common.SimpleClickableViewHolder;
import com.leadcom.android.isp.lib.view.ExpandableTextView;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.activity.sign.AppSignRecord;
import com.leadcom.android.isp.model.activity.sign.AppSigning;
import com.leadcom.android.isp.model.common.Address;
import com.leadcom.android.isp.nim.constant.SigningNotifyType;
import com.leadcom.android.isp.nim.model.extension.SigningNotifyAttachment;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

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

public class SignDetailsFragment extends BaseSignFragment {

    public static SignDetailsFragment newInstance(Bundle bundle) {
        SignDetailsFragment sdf = new SignDetailsFragment();
        sdf.setArguments(bundle);
        return sdf;
    }

    private static Bundle getBundle(String tid, String voteJson) {
        Bundle bundle = new Bundle();
        // 签到应用的群聊tid
        bundle.putString(PARAM_QUERY_ID, tid);
        // 签到应用的pojo对象
        bundle.putString(PARAM_POJO, voteJson);
        return bundle;
    }

    public static void open(Context context, int req, String tid, String voteJson) {
        BaseActivity.openActivity(context, SignDetailsFragment.class.getName(), getBundle(tid, voteJson), req, true, false);
    }

    public static void open(BaseFragment fragment, int req, String tid, String voteJson) {
        fragment.openActivity(SignDetailsFragment.class.getName(), getBundle(tid, voteJson), req, true, false);
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
    @ViewId(R.id.ui_activity_sign_details_count)
    private TextView countView;

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
    public void doingInResume() {
        setCustomTitle(R.string.ui_activity_sign_right_button_text);
        setNothingText(R.string.ui_activity_sign_details_no_sign_records);
        initializeHolder();
        initializeAdapter();
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
        notify.setCustomId(signing.getId());
        notify.setTid(mQueryId);
        notify.setBeginTime(signing.getBeginDate());
        notify.setEndTime(signing.getEndDate());
        IMMessage message;
        message = MessageBuilder.createCustomMessage(mQueryId, SessionTypeEnum.Team, notify);
        NIMClient.getService(MsgService.class).sendMessage(message, false);
    }

    private void fetchingSignRecords() {
        setLoadingText(R.string.ui_activity_sign_details_loading_sign_records);
        displayLoading(true);
        displayNothing(false);
        AppSigningRecordRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<AppSignRecord>() {
            @Override
            public void onResponse(List<AppSignRecord> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                int size = null == list ? 0 : list.size();
                isLoadingComplete(size < pageSize);
                remotePageNumber += size < pageSize ? 0 : 1;
                if (success && null != list) {
                    mAdapter.update(list, false);
                }
                displayLoading(false);
                displayNothing(mAdapter.getItemCount() < 1);
                stopRefreshing();
                countView.setText(format("%s(%d)", getString(R.string.ui_activity_sign_signed), mAdapter.getItemCount()));
                //setCustomTitle(format("%s(%d人次)", getString(R.string.ui_activity_sign_creator_fragment_title), mAdapter.getItemCount()));
            }
        }).listTeamSignRecord(signing.getId());
    }

    private void setRightIconEvent() {
        setRightText(R.string.ui_base_text_delete);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                warningDelete(signing.getId());
            }
        });
    }

    @Override
    protected void onDeleteSigningComplete(boolean success, String signingId) {
        if (success) {
            resultData(signingId);
        }
    }

    private void initializeHolder() {
        if (null == signing) {
            signing = AppSigning.fromJson(pojo);
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

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            // 个人签到详情
            AppSignRecord record = mAdapter.get(index);
            Address addr = new Address();
            // 默认北京天安门
            addr.setLongitude(stringToDouble(record.getLon(), 116.400244));
            addr.setLatitude(stringToDouble(record.getLat(), 39.963175));
            addr.setAltitude(stringToDouble(record.getAlt(), 0.0));
            addr.setAddress(record.getSite());
            // 重现用户的签到地址
            AddressMapPickerFragment.open(SignDetailsFragment.this, true, Address.toJson(addr));
        }
    };

    private class SignRecordAdapter extends RecyclerViewAdapter<SingingViewHolder, AppSignRecord> {

        @Override
        public SingingViewHolder onCreateViewHolder(View itemView, int viewType) {
            SingingViewHolder holder = new SingingViewHolder(itemView, SignDetailsFragment.this);
            holder.setOnViewHolderElementClickListener(elementClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_activity_signing_item;
        }

        @Override
        public void onBindHolderOfView(SingingViewHolder holder, int position, AppSignRecord item) {
            holder.showContent(item, item.getDistance(signing.getLat(), signing.getLon()));
        }

        @Override
        protected int comparator(AppSignRecord item1, AppSignRecord item2) {
            return 0;
        }
    }
}
