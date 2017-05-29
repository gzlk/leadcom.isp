package com.gzlk.android.isp.fragment.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.bigkoo.pickerview.TimePickerView;
import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.activity.ActRequest;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.fragment.organization.OrganizationContactPickFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.SimpleClickableViewHolder;
import com.gzlk.android.isp.holder.SimpleInputableViewHolder;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.activity.Activity;
import com.gzlk.android.isp.model.common.Attachment;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * <b>功能描述：</b>新增活动<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/27 10:00 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/27 10:00 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class CreateActivityFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_GROUP = "caf_group_id_params";
    private static final String PARAM_MEMBERS = "caf_members";

    public static CreateActivityFragment newInstance(String params) {
        CreateActivityFragment caf = new CreateActivityFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        bundle.putString(PARAM_GROUP, strings[1]);
        caf.setArguments(bundle);
        return caf;
    }

    private String members = "[]";
    private static final int REQ_MEMBER = ACTIVITY_BASE_REQUEST + 10;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mGroupId = bundle.getString(PARAM_GROUP, "");
        members = bundle.getString(PARAM_MEMBERS, "[]");
        resetMembers();
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_GROUP, mGroupId);
        bundle.putString(PARAM_MEMBERS, members);
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == REQ_MEMBER) {
            members = getResultedData(data);
            resetMembers();
        }
        super.onActivityResult(requestCode, data);
    }

    private void resetMembers() {
        membersId = Json.gson().fromJson(members, new TypeToken<ArrayList<String>>() {
        }.getType());
        if (null == membersId) {
            membersId = new ArrayList<>();
        }
    }

    // view
    @ViewId(R.id.ui_activity_creator_cover)
    private View coverView;
    @ViewId(R.id.ui_activity_creator_title)
    private View titleView;
    @ViewId(R.id.ui_activity_creator_time)
    private View timeView;
    @ViewId(R.id.ui_activity_creator_address)
    private View addressView;
    @ViewId(R.id.ui_activity_creator_type)
    private View typeView;
    @ViewId(R.id.ui_activity_creator_privacy)
    private View privacyView;
    @ViewId(R.id.ui_activity_creator_member)
    private View memberView;
    @ViewId(R.id.ui_activity_creator_content)
    private ClearEditText contentView;
    @ViewId(R.id.ui_tool_attachment_button)
    private View attachmentView;

    // holder
    private SimpleClickableViewHolder coverHolder;
    private SimpleInputableViewHolder titleHolder;
    private SimpleClickableViewHolder timeHolder;
    private SimpleInputableViewHolder addressHolder;
    private SimpleClickableViewHolder typeHolder;
    private SimpleClickableViewHolder privacyHolder;
    private SimpleClickableViewHolder memberHolder;

    /**
     * 活动所属的组织id
     */
    private String mGroupId = "";
    private ArrayList<String> membersId;
    private String[] items;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        maxSelectable = 1;
        setCustomTitle(R.string.ui_activity_create_fragment_title);
        setRightText(R.string.ui_base_text_publish);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                tryPublishActivity();
            }
        });
        enableSwipe(!isEmpty(mQueryId));
        addOnImageSelectedListener(onImageSelectedListener);
        setOnFileUploadingListener(mOnFileUploadingListener);
        tryLoadActivity();
    }

    private OnImageSelectedListener onImageSelectedListener = new OnImageSelectedListener() {
        @Override
        public void onImageSelected(ArrayList<String> selected) {
            // 只能选择一张图片，因此可以直接显示图片
            coverHolder.showImage(selected.get(0));
        }
    };

    private OnFileUploadingListener mOnFileUploadingListener = new OnFileUploadingListener() {

        @Override
        public void onUploading(int all, int current, String file, long size, long uploaded) {

        }

        @Override
        public void onUploadingComplete(ArrayList<Attachment> uploaded) {
            publishActivity();
        }
    };

    @Override
    protected boolean onBackKeyPressed() {
        return super.onBackKeyPressed();
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_activity_creator;
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    protected void onSwipeRefreshing() {
        fetchingActivity(true);
    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    // 加载本地活动记录
    private void tryLoadActivity() {
        if (isEmpty(mQueryId)) {
            initializeHolder(null);
        } else {
            fetchingActivity(false);
        }
    }

    private void fetchingActivity(boolean fromRemote) {
        ActRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Activity>() {
            @Override
            public void onResponse(Activity activity, boolean success, String message) {
                super.onResponse(activity, success, message);
                if (success) {
                    if (null != activity) {
                        initializeHolder(activity);
                    }
                }
                stopRefreshing();
            }
        }).find(mQueryId, fromRemote);
    }

    private void initializeHolder(Activity activity) {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_activity_create_items);
        }
        // cover
        if (null == coverHolder) {
            coverHolder = new SimpleClickableViewHolder(coverView, this);
            coverHolder.addOnViewHolderClickListener(onViewHolderClickListener);
        }
        String none = StringHelper.getString(R.string.ui_base_text_not_set);
        boolean non = null == activity;
        String value = format(items[0], non ? none : (isEmpty(activity.getImg()) ? none : activity.getImg()));
        coverHolder.showContent(value);

        // title
        if (null == titleHolder) {
            titleHolder = new SimpleInputableViewHolder(titleView, this);
        }
        value = format(items[1], non ? "" : activity.getTitle());
        titleHolder.showContent(value);

        // time
        if (null == timeHolder) {
            timeHolder = new SimpleClickableViewHolder(timeView, this);
            timeHolder.addOnViewHolderClickListener(onViewHolderClickListener);
        }
        value = format(items[2], non ? "" : activity.getCreateDate());
        timeHolder.showContent(value);

        // address
        if (null == addressHolder) {
            addressHolder = new SimpleInputableViewHolder(addressView, this);
        }
        value = format(items[3], "");
        addressHolder.showContent(value);

        // type
        if (null == typeHolder) {
            typeHolder = new SimpleClickableViewHolder(typeView, this);
            typeHolder.addOnViewHolderClickListener(onViewHolderClickListener);
        }
        value = format(items[4], "");
        typeHolder.showContent(value);

        // privacy
        if (null == privacyHolder) {
            privacyHolder = new SimpleClickableViewHolder(privacyView, this);
            privacyHolder.addOnViewHolderClickListener(onViewHolderClickListener);
        }
        value = format(items[5], "未设置");
        privacyHolder.showContent(value);

        // member
        if (null == memberHolder) {
            memberHolder = new SimpleClickableViewHolder(memberView, this);
            memberHolder.addOnViewHolderClickListener(onViewHolderClickListener);
            if (!non) {
                if (null != activity.getMemberIdArray() && activity.getMemberIdArray().size() > 0) {
                    for (String id : activity.getMemberIdArray()) {
                        if (!membersId.contains(id)) {
                            membersId.add(id);
                        }
                    }
                }
            }
        }
        value = format(items[6], membersId.size());
        memberHolder.showContent(value);
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            switch (index) {
                case 0:
                    // 选择活动封面
                    openImageSelector();
                    break;
                case 1:
                    // 选择活动时间
                    openDatePicker();
                    break;
                case 4:
                    openActivity(OrganizationContactPickFragment.class.getName(), format("%s,%s", mGroupId, members.replace(',', '@')), REQ_MEMBER, true, false);
                    break;
            }
        }
    };

    private String happenDate;

    private void showCreateDate(Date date) {
        happenDate = Utils.format(StringHelper.getString(R.string.ui_base_text_date_time_format), date);
        timeHolder.showContent(StringHelper.format(items[2], "(" + Utils.format(StringHelper.getString(R.string.ui_base_text_date_format_chs_min), date) + ")"));
    }

    private void openDatePicker() {
        Utils.hidingInputBoard(contentView);
        TimePickerView tpv = new TimePickerView.Builder(Activity(), new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                showCreateDate(date);
            }
        }).setType(new boolean[]{true, true, true, true, true, false})
                .setTitleBgColor(getColor(R.color.colorPrimary))
                .setSubmitColor(Color.WHITE)
                .setCancelColor(Color.WHITE)
                .setContentSize(getFontDimension(R.dimen.ui_static_sp_20))
                .setOutSideCancelable(false)
                .isCenterLabel(true).isDialog(false).build();
        if (StringHelper.isEmpty(happenDate)) {
            tpv.setDate(Calendar.getInstance());
            happenDate = Utils.format(StringHelper.getString(R.string.ui_base_text_date_time_format), Calendar.getInstance().getTime());
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(Utils.parseDate(StringHelper.getString(R.string.ui_base_text_date_time_format), happenDate));
            tpv.setDate(calendar);
        }
        tpv.show();
    }

    private void tryPublishActivity() {
        String title = titleHolder.getValue();
        if (isEmpty(title)) {
            ToastHelper.make().showMsg(R.string.ui_activity_create_title_invalid);
            return;
        }
//        String address = addressHolder.getValue();
//        if (isEmpty(address)) {
//            ToastHelper.make().showMsg(R.string.ui_activity_create_address_invalid);
//            return;
//        }
        String content = contentView.getValue();
        if (isEmpty(content)) {
            ToastHelper.make().showMsg(R.string.ui_activity_create_content_invalid);
            return;
        }
        Utils.hidingInputBoard(contentView);
        if (getWaitingForUploadFiles().size() > 0) {
            uploadFiles();
        } else {
            publishActivity();
        }
    }

    private void publishActivity() {
        String title = titleHolder.getValue();
        String address = addressHolder.getValue();
        String content = contentView.getValue();
        String logo = null;
        if (getUploadedFiles().size() > 0) {
            logo = getUploadedFiles().get(0).getUrl();
        }
        ActRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Activity>() {
            @Override
            public void onResponse(Activity activity, boolean success, String message) {
                super.onResponse(activity, success, message);
                if (success) {
                    Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    });
                }
            }
        }).add(title, content, mGroupId, logo, membersId);
    }
}
