package com.leadcom.android.isp.fragment.organization;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.layoutmanager.CustomGridLayoutManager;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;
import com.hlk.hlklib.lib.view.CorneredButton;
import com.hlk.hlklib.lib.view.CorneredEditText;
import com.hlk.hlklib.lib.view.CorneredView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.activity.MainActivity;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.org.PaymentRequest;
import com.leadcom.android.isp.api.org.SquadRequest;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseImageSelectableSupportFragment;
import com.leadcom.android.isp.fragment.common.ImageViewerFragment;
import com.leadcom.android.isp.helper.DateTimePicker;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.popup.DeleteDialogHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.attachment.AttacherItemViewHolder;
import com.leadcom.android.isp.holder.common.SimpleClickableViewHolder;
import com.leadcom.android.isp.holder.individual.ImageViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.organization.Payment;
import com.leadcom.android.isp.model.organization.Role;
import com.leadcom.android.isp.model.organization.Squad;
import com.leadcom.android.isp.model.organization.SubMember;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

/**
 * <b>功能描述：</b>缴费记录创建页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/12/25 13:32 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/12/25 13:32  <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class FinanceCreatorFragment extends BaseImageSelectableSupportFragment {

    private static final String PARAM_TYPE = "pcf_param_type";
    private static final String PARAM_NAME = "pcf_param_name";
    private static final String PARAM_SQUAD = "pcf_param_squad";
    private static final String PARAM_PAYMENT = "pcf_param_payment";
    private static final String PARAM_CHOOSE = "pcf_param_choose";
    private static final String PARAM_USER = "pcf_param_user";
    private static final String PARAM_FOREGROUND = "pcf_param_foreground";

    public static FinanceCreatorFragment newInstance(Bundle bundle) {
        FinanceCreatorFragment pcf = new FinanceCreatorFragment();
        pcf.setArguments(bundle);
        return pcf;
    }

    private static Bundle getBundle(int type, String paymentId, boolean foreground) {
        Bundle bundle = new Bundle();
        bundle.putInt(PARAM_TYPE, type);
        bundle.putString(PARAM_PAYMENT, paymentId);
        bundle.putBoolean(PARAM_FOREGROUND, foreground);
        return bundle;
    }

    public static void open(BaseFragment fragment, int type, String groupId, String groupName, String squadId, String paymentId, String userId) {
        Bundle bundle = getBundle(type, paymentId, true);
        bundle.putString(PARAM_QUERY_ID, groupId);
        //bundle.putInt(PARAM_TYPE, type);
        bundle.putString(PARAM_NAME, groupName);
        bundle.putString(PARAM_SQUAD, squadId);
        //bundle.putString(PARAM_PAYMENT, paymentId);
        bundle.putString(PARAM_USER, userId);
        fragment.openActivity(FinanceCreatorFragment.class.getName(), bundle, REQUEST_CREATE, true, true);
    }

    public static void open(Context context, String paymentId, boolean isForeground) {
        Bundle bundle = getBundle(Payment.Type.CHECK, paymentId, isForeground);
        BaseActivity.openActivity(context, FinanceCreatorFragment.class.getName(), bundle, REQUEST_CREATE, true, false);
    }

    @ViewId(R.id.ui_group_payment_amount_text)
    private TextView amountText;
    @ViewId(R.id.ui_group_payment_amount)
    private CorneredEditText amountView;
    @ViewId(R.id.ui_group_payment_remark_title)
    private TextView remarkTitle;
    @ViewId(R.id.ui_group_payment_remark_text)
    private TextView remarkText;
    @ViewId(R.id.ui_group_payment_remark)
    private ClearEditText remarkView;
    @ViewId(R.id.ui_group_payment_time)
    private View timeView;
    @ViewId(R.id.ui_group_payment_user)
    private View userView;
    @ViewId(R.id.ui_group_payment_creator)
    private View creatorView;
    @ViewId(R.id.ui_group_payment_reference)
    private View referenceView;
    @ViewId(R.id.ui_group_payment_approver)
    private View approverView;
    @ViewId(R.id.ui_group_payment_receiver)
    private View receiverView;
    @ViewId(R.id.ui_archive_creator_rich_editor_template_images_layout)
    private View attachmentsView;
    @ViewId(R.id.ui_archive_creator_rich_editor_attachment_title)
    private TextView attachmentsTitle;
    @ViewId(R.id.ui_archive_creator_rich_editor_template_images_clear)
    private View imageClearView;
    @ViewId(R.id.ui_archive_creator_rich_editor_template_images)
    private RecyclerView templateRecyclerView;
    @ViewId(R.id.ui_group_payment_time_picker_background)
    private View timePickerBackground;
    @ViewId(R.id.ui_group_payment_time_picker)
    private CorneredView timePickerRoot;
    @ViewId(R.id.ui_group_payment_amount_controls)
    private View controlView;
    @ViewId(R.id.ui_group_payment_amount_controls_warning)
    private TextView controlTitle;
    @ViewId(R.id.ui_group_payment_amount_controls_agree)
    private CorneredButton agreeButton;
    @ViewId(R.id.ui_group_payment_amount_controls_disagree)
    private CorneredButton rejectButton;

    private ImageAdapter imageAdapter;
    private Payment mPayment;
    private int mType;
    private SimpleClickableViewHolder timeHolder, userHolder, creatorHolder, referHolder, apprHolder, recvrHolder;
    private String mGroupName, mSquadId, mPaymentId, mUserId;
    private String[] items, warnings, itemTitles;
    private boolean isCreating = false, isForeground = true, isLast = false;
    private DateTimePicker dateTimePicker;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!isForeground) {
            // 打开首页
            MainActivity.start(Activity());
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String title = StringHelper.getString(mPayment.isPayment() ? R.string.ui_group_finance_user_payment_create_title_payment : (mPayment.isExpend() ? R.string.ui_group_finance_user_payment_create_title_expend : R.string.ui_group_finance_user_payment_create_title_check));
        if (!isEmpty(mPaymentId)) {
            assert title != null;
            title = title.replace("添加", "");
        }
        setCustomTitle(title);

        isShowLoadingBackground = true;
        // 压缩图片
        isSupportCompress = true;
        // 直接上传图片
        isSupportDirectlyUpload = true;
        // 不需要显示上传进度
        needShowUploading = false;
        // 本地图片选择
        addOnImageSelectedListener(imageSelectedListener);
        // 文件上传完毕
        setOnFileUploadingListener(uploadingListener);
        amountView.setInputTypePassword(false);
        items = StringHelper.getStringArray(R.array.ui_group_finance_user_payment_create_items);
        warnings = StringHelper.getStringArray(R.array.ui_group_finace_user_payment_create_user_control);
        itemTitles = StringHelper.getStringArray(R.array.ui_group_finance_user_payment_create_user_select_title);
        boolean isEmptyId = isEmpty(mPaymentId);
        if (isEmptyId) {
            setRightText(R.string.ui_base_text_commit);
            setRightTitleClickListener(new OnTitleButtonClickListener() {
                @Override
                public void onClick() {
                    tryCreatePayment();
                }
            });
            attachmentsView.setVisibility(View.VISIBLE);
            if (mPayment.isExpend()) {
                // 支出记录的事由为必填项
                remarkView.setMaxLength(10);
            }
            remarkView.setTextHint(mPayment.isPayment() ? R.string.ui_group_finance_user_payment_create_remark_hint : R.string.ui_group_finance_user_payment_create_remark_hint_expend);
        } else {
            attachmentsTitle.setText(R.string.ui_group_finance_user_payment_create_files_title1);
            imageClearView.setVisibility(View.GONE);
        }
        // 显示或隐藏相关选项
        amountText.setVisibility(isEmptyId ? View.GONE : View.VISIBLE);
        amountView.setVisibility(isEmptyId ? View.VISIBLE : View.GONE);
        remarkText.setVisibility(isEmptyId ? View.GONE : View.VISIBLE);
        remarkView.setVisibility(isEmptyId ? View.VISIBLE : View.GONE);
        //controlView.setVisibility(isEmptyId ? View.GONE : (mPayment.isCheck() ? View.VISIBLE : View.GONE));

        boolean isPayment = mPayment.isPayment();
        userView.setVisibility(isPayment ? View.VISIBLE : View.GONE);
        creatorView.setVisibility(isPayment && !isEmptyId ? View.VISIBLE : View.GONE);
        referenceView.setVisibility(!isPayment ? View.VISIBLE : View.GONE);
        approverView.setVisibility(!isPayment ? View.VISIBLE : View.GONE);
        receiverView.setVisibility(!isPayment ? View.VISIBLE : View.GONE);
        remarkTitle.setText(isPayment ? R.string.ui_group_finance_user_payment_item_remark : R.string.ui_group_finance_user_payment_item_remark_expend);
        dateTimePicker = DateTimePicker.picker().setSelectedTitleFormat("yyyy年MM月dd日").setOnDateTimePickedListener(new DateTimePicker.OnDateTimePickedListener() {
            @Override
            public void onPicked(Date date) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.set(Calendar.HOUR, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                String time = Utils.format(StringHelper.getString(R.string.ui_base_text_date_time_format), calendar.getTime());
                mPayment.setPayDate(time);
                mPayment.setExpendDate(time);
                timeHolder.showContent(format(items[mPayment.isPayment() ? 0 : 2], formatDate(time) + itemTitles[8]));
                showTimePicker(false);
            }

            @Override
            public void onReset() {
                showTimePicker(false);
            }
        }).setSelectionType(true, true, true, false, false, false).setCancelText(R.string.ui_base_text_quite);

        Handler().post(new Runnable() {
            @Override
            public void run() {
                dateTimePicker.show(timePickerRoot);
                showTimePicker(false);
            }
        });
        if (isEmpty(mPaymentId) && (Role.isSquadFinanceManager(mQueryId) || Role.isSquadFinance(mQueryId))) {
            // 小组财务或小组管理员，需要拉取小组id
            fetchMySquad();
        }
    }

    @Override
    protected boolean onBackKeyPressed() {
        if (!super.onBackKeyPressed()) {
            if (null != timePickerBackground && timePickerBackground.getVisibility() == View.VISIBLE) {
                showTimePicker(false);
                return true;
            }
            return false;
        }
        return true;
    }

    private void showTimePicker(final boolean shown) {
        //if (shown && timePickerBackground.getVisibility() == View.VISIBLE) return;
        //if (!shown && timePickerBackground.getVisibility() == View.GONE) return;
        timePickerBackground.animate().setDuration(duration()).alpha(shown ? 1.0f : 0f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!shown) {
                    timePickerBackground.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (shown) {
                    timePickerBackground.setVisibility(View.VISIBLE);
                }
            }
        }).start();
        timePickerRoot.animate().setDuration(duration()).alpha(shown ? 1.0f : 0f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!shown) {
                    timePickerRoot.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (shown) {
                    timePickerRoot.setVisibility(View.VISIBLE);
                }
            }
        }).translationY(shown ? 0 : timePickerRoot.getMeasuredHeight() * 1.1f).scaleX(shown ? 1.0f : 0.2f).scaleY(shown ? 1.0f : 0.2f).start();
    }

    private void fetchMySquad() {
        SquadRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Squad>() {
            @Override
            public void onResponse(Squad squad, boolean success, String message) {
                super.onResponse(squad, success, message);
                if (success && null != squad) {
                    if (null == mPayment) {
                        tryCreateEmptyPayment();
                    }
                    if (isEmpty(mPayment.getSquadId())) {
                        mPayment.setSquadId(squad.getId());
                    }
                }
            }
        }).findFirstJoinedSquad(mQueryId);
    }

    private void tryCreatePayment() {
        String string = amountView.getValue();
        if (isEmpty(string)) {
            showWarningText(R.string.ui_group_finance_user_payment_create_amount_error);
            return;
        }
        mPayment.setPayAmount(Double.valueOf(string));
        mPayment.setExpendAmount(Double.valueOf(string));
        mPayment.setRemark(remarkView.getValue());
        if (mPayment.isExpend()) {
            if (isEmpty(mPayment.getRemark()) || mPayment.getRemark().length() <= 1) {
                showWarningText(R.string.ui_group_finance_user_payment_create_remark_error);
                return;
            }
            if (isEmpty(mPayment.getCertifierId())) {
                showWarningText(R.string.ui_group_finance_user_payment_create_reference_error);
                return;
            }
            if (isEmpty(mPayment.getApproverId())) {
                showWarningText(R.string.ui_group_finance_user_payment_create_approver_error);
                return;
            }
            if (isEmpty(mPayment.getReceiverId())) {
                showWarningText(R.string.ui_group_finance_user_payment_create_receiver_error);
                return;
            }
        }
        if (isCreating) {
            showWarningText(R.string.ui_group_finance_user_payment_creating_waiting);
            return;
        }
        showWarningText(0);
        isCreating = true;
        if (waitingFroCompressImages.size() > 0) {
            setLoadingText(R.string.ui_base_text_uploading);
            displayLoading(true);
            compressImage();
        } else {
            createPayment();
        }
    }

    private void createPayment() {
        setLoadingText(R.string.ui_group_finance_user_payment_creating);
        displayLoading(true);
        PaymentRequest request = PaymentRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Payment>() {
            @Override
            public void onResponse(Payment payment, boolean success, String message) {
                super.onResponse(payment, success, message);
                if (success) {
                    resultData(payment.getId());
                } else {
                    displayLoading(false);
                    isCreating = false;
                    showWarningText(R.string.ui_group_finance_user_payment_creating_failed);
                }
            }
        });
        if (mPayment.isPayment()) {
            request.addPayment(mPayment);
        } else if (mPayment.isExpend()) {
            request.addExpend(mPayment);
        }
    }

    private OnImageSelectedListener imageSelectedListener = new OnImageSelectedListener() {
        @Override
        public void onImageSelected(ArrayList<String> selected) {
            resetImages(selected, false);
        }
    };

    private OnFileUploadingListener uploadingListener = new OnFileUploadingListener() {

        @Override
        public void onUploading(int all, int current, String file, long size, long uploaded) {

        }

        @Override
        public void onUploadingComplete(ArrayList<Attachment> uploaded) {
            if (null != uploaded) {
                for (Attachment attachment : uploaded) {
                    if (attachment.isImage()) {
                        mPayment.getImage().add(attachment);
                    }
                }
                createPayment();
            }
        }

        @Override
        public void onUploadingFailed(int code, String message) {
            showWarningText(StringHelper.getString(R.string.ui_text_archive_creator_editor_attachment_uploading_failed, code, message));
            isCreating = false;
            displayLoading(false);
        }
    };

    @Override
    public int getLayout() {
        return R.layout.fragment_group_payment;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mGroupName = bundle.getString(PARAM_NAME, "");
        mType = bundle.getInt(PARAM_TYPE, Payment.Type.PAYMENT);
        mSquadId = bundle.getString(PARAM_SQUAD, "");
        mPaymentId = bundle.getString(PARAM_PAYMENT, "");
        chooseType = bundle.getInt(PARAM_CHOOSE, 0);
        mUserId = bundle.getString(PARAM_USER, "");
        isForeground = bundle.getBoolean(PARAM_FOREGROUND, true);
        mPayment = (Payment) bundle.getSerializable(PARAM_JSON);
        tryCreateEmptyPayment();
    }

    private void tryCreateEmptyPayment() {
        if (null == mPayment) {
            mPayment = new Payment();
            mPayment.setType(mType);
            mPayment.setId(mPaymentId);
            // 默认当前日期为缴费日期，可以修改
            mPayment.setPayDate(Utils.formatDateOfNow("yyyy-MM-dd 00:00:00"));
            mPayment.setExpendDate(mPayment.getPayDate());
            if (isEmpty(mPaymentId)) {
                mPayment.setUserId(Cache.cache().userId);
                mPayment.setUserName(Cache.cache().userName);
            }
            mPayment.setGroupId(mQueryId);
            mPayment.setSquadId(mSquadId);
        }
    }

    private int chooseType;
    private OnViewHolderClickListener clickListener = new OnViewHolderClickListener() {

        @Override
        public void onClick(int index) {
            if (!isEmpty(mPaymentId)) {
                return;
            }
            switch (index) {
                case 1:
                case 3:
                    // 选择缴费时间
                    //openDateDialog();
                    showTimePicker(true);
                    break;
                case 2:// 缴费人
                case 4:// 证明人
                case 5:// 审批人
                case 6:// 收款人
                    // 选择缴费人
                    chooseType = index;
                    GroupSubordinateSquadMemberPickerFragment.open(FinanceCreatorFragment.this, mQueryId, mGroupName, itemTitles[index], false, true, null, null);
                    break;
            }
        }
    };

    private boolean isEquals(String text1, String text2) {
        return !isEmpty(text1) && !isEmpty(text2) && text1.equals(text2);
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SELECT:
            case REQUEST_MEMBER:
                // 组织成员选择返回了
                String json = getResultedData(data);
                if (!isEmpty(json) && json.length() > 10) {
                    ArrayList<SubMember> subs = SubMember.fromJson(json);
                    SubMember member = subs.get(0);
                    if (chooseType == 2) {
                        mPayment.setUserId(member.getUserId());
                        mPayment.setUserName(member.getUserName());
                        userHolder.showContent(format(items[1], mPayment.getUserName() + itemTitles[7]));
                    } else if (chooseType == 4) {
                        // 证明人
                        if (isEquals(member.getUserId(), mPayment.getApproverId())) {
                            // 证明人和审核人不能相同
                            showWarningText(warnings[0]);
                            return;
                        }
                        if (isEquals(member.getUserId(), mPayment.getReceiverId())) {
                            // 证明人和收款人不能相同
                            showWarningText(warnings[1]);
                            return;
                        }
                        showWarningText(0);
                        // 证明人
                        mPayment.setCertifierId(member.getUserId());
                        mPayment.setCertifierName(member.getUserName());
                        referHolder.showContent(format(items[3], mPayment.getCertifierName() + itemTitles[7]));
                    } else if (chooseType == 5) {
                        // 审批人
//                        if (isEquals(member.getUserId(), mPayment.getUserId())) {
//                            // 审核人不能是自己
//                            showWarningText(warnings[2]);
//                            break;
//                        }
                        if (isEquals(member.getUserId(), mPayment.getCertifierId())) {
                            // 审核人和证明人不能是同一人
                            showWarningText(warnings[3]);
                            return;
                        }
                        if (isEquals(member.getUserId(), mPayment.getReceiverId())) {
                            // 审核人和收款人不能是同一人
                            showWarningText(warnings[4]);
                            return;
                        }

                        showWarningText(0);
                        mPayment.setApproverId(member.getUserId());
                        mPayment.setApproverName(member.getUserName());
                        apprHolder.showContent(format(items[4], mPayment.getApproverName() + itemTitles[7]));
                    } else if (chooseType == 6) {
                        // 收款人
                        if (isEquals(member.getUserId(), mPayment.getCertifierId())) {
                            // 收款人和证明人不能相同
                            showWarningText(warnings[5]);
                            return;
                        }
                        if (isEquals(member.getUserId(), mPayment.getApproverId())) {
                            // 收款人和审核人不能相同
                            showWarningText(warnings[6]);
                            return;
                        }
                        showWarningText(0);
                        // 收款人
                        mPayment.setReceiverId(member.getUserId());
                        mPayment.setReceiverName(member.getUserName());
                        recvrHolder.showContent(format(items[5], mPayment.getReceiverName() + itemTitles[7]));
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, data);
    }

    @Override
    public void doingInResume() {
        boolean isEmptyId = isEmpty(mPaymentId);
        if (null == timeHolder) {
            timeHolder = new SimpleClickableViewHolder(timeView, this);
            timeHolder.addOnViewHolderClickListener(clickListener);
            boolean isPayment = mPayment.isPayment();
            String date = isPayment ? mPayment.getPayDate() : mPayment.getExpendDate();
            timeHolder.showContent(format(items[isPayment ? 0 : 2], formatDate(date) + (isEmptyId ? itemTitles[8] : "")));
        }
        if (null == userHolder) {
            userHolder = new SimpleClickableViewHolder(userView, this);
            userHolder.addOnViewHolderClickListener(clickListener);
            userHolder.showContent(format(items[1], mPayment.getUserName() + (isEmptyId ? itemTitles[8] : "")));
        }
        if (null == creatorHolder) {
            creatorHolder = new SimpleClickableViewHolder(creatorView, this);
            creatorHolder.showContent(format(items[6], mPayment.getCreateUserName()));
        }
        if (null == referHolder) {
            referHolder = new SimpleClickableViewHolder(referenceView, this);
            referHolder.addOnViewHolderClickListener(clickListener);
            referHolder.showContent(format(items[3], (isEmptyId ? (itemTitles[4] + itemTitles[7]) : "")));
        }
        if (null == apprHolder) {
            apprHolder = new SimpleClickableViewHolder(approverView, this);
            apprHolder.addOnViewHolderClickListener(clickListener);
            apprHolder.showContent(format(items[4], (isEmptyId ? (itemTitles[5] + itemTitles[7]) : "")));
        }
        if (null == recvrHolder) {
            recvrHolder = new SimpleClickableViewHolder(receiverView, this);
            recvrHolder.addOnViewHolderClickListener(clickListener);
            recvrHolder.showContent(format(items[5], (isEmptyId ? (itemTitles[6] + itemTitles[7]) : "")));
        }
        if (null == imageAdapter) {
            // 模板档案的图片
            templateRecyclerView.setLayoutManager(new CustomGridLayoutManager(templateRecyclerView.getContext(), 4));
            templateRecyclerView.addItemDecoration(new SpacesItemDecoration());
            imageAdapter = new ImageAdapter();
            templateRecyclerView.setAdapter(imageAdapter);
            resetImages(waitingFroCompressImages, true);
            if (!isEmpty(mPaymentId)) {
                loadingPaymentDetails();
            }
        }
    }

    private void loadingPaymentDetails() {
        displayLoading(true);
        PaymentRequest request = PaymentRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Payment>() {

            private String[] colors = StringHelper.getStringArray(R.array.ui_group_finance_status_colors);
            private String[] certs = StringHelper.getStringArray(R.array.ui_group_finance_cert_status);
            private String[] apprs = StringHelper.getStringArray(R.array.ui_group_finance_approve_status);
            private String[] recvs = StringHelper.getStringArray(R.array.ui_group_finance_receive_status);

            @Override
            public void onResponse(Payment payment, boolean success, String message) {
                super.onResponse(payment, success, message);
                displayLoading(false);
                if (success) {
                    mPayment = payment;
                    mPayment.setType(mType);
                    attachmentsView.setVisibility(mPayment.getImage().size() > 0 ? View.VISIBLE : View.GONE);
                    for (Attachment attachment : mPayment.getImage()) {
                        imageAdapter.add(attachment);
                    }
                    boolean isPayment = mPayment.isPayment();
                    String date = isPayment ? mPayment.getPayDate() : mPayment.getExpendDate();
                    timeHolder.showContent(format(items[isPayment ? 0 : 2], (isEmpty(date) ? "-" : formatDate(date))));
                    userHolder.showContent(format(items[1], mPayment.getUserName()));
                    creatorHolder.showContent(format(items[6], mPayment.getCreateUserName()));
                    int state = mPayment.getCertifierState();
                    referHolder.showContent(format(items[3], mPayment.getCertifierName() + format(colors[state], certs[state])));
                    state = mPayment.getApproverState();
                    apprHolder.showContent(format(items[4], mPayment.getApproverName() + format(colors[state], apprs[state])));
                    state = mPayment.getReceiverState();
                    recvrHolder.showContent(format(items[5], mPayment.getReceiverName() + format(colors[state], recvs[state])));
                    double amount = isPayment ? mPayment.getPayAmount() : mPayment.getExpendAmount();
                    amountText.setText(NumberFormat.getCurrencyInstance(Locale.CHINA).format(amount));
                    remarkText.setText(mPayment.getRemark());
                    String me = Cache.cache().userId;
                    if (mPayment.isCheck()) {
                        if (mPayment.isStateHandleable(me)) {
                            String type = mPayment.getUserType(me);
                            controlTitle.setText(Html.fromHtml(StringHelper.getString(R.string.ui_group_finance_user_payment_approve_warning0, type, type.replace("人", ""))));
                            agreeButton.setText(mPayment.isCertificator(me) ? certs[1] : (mPayment.isApprovor(me) ? apprs[1] : recvs[1]));
                            rejectButton.setText(mPayment.isCertificator(me) ? certs[2] : (mPayment.isApprovor(me) ? apprs[2] : recvs[2]));
                            if (mPayment.isCertificator(me)) {
                                // 当前用户是证明人
                                if (mPayment.isRejectApproved() || mPayment.isRejectReceived()) {
                                    showWarningText(R.string.ui_group_finance_user_payment_has_rejected);
                                } else {
                                    controlView.setVisibility(mPayment.isCertified() ? View.GONE : View.VISIBLE);
                                }
                            } else if (mPayment.isApprovor(me)) {
                                if (mPayment.isRejectCertified() || mPayment.isRejectReceived()) {
                                    showWarningText(R.string.ui_group_finance_user_payment_has_rejected);
                                } else {
                                    controlView.setVisibility(mPayment.isApproved() ? View.GONE : View.VISIBLE);
                                }
                            } else if (mPayment.isReceiver(me)) {
                                if (mPayment.isRejectCertified() || mPayment.isRejectApproved()) {
                                    showWarningText(R.string.ui_group_finance_user_payment_has_rejected);
                                } else {
                                    controlView.setVisibility(mPayment.isReceived() ? View.GONE : View.VISIBLE);
                                }
                            }
                        }
                    }
                } else {
                    finish();
                }
            }
        });
        if (mPayment.isPayment()) {
            request.findPayment(mPaymentId);
        } else {
            request.findExpend(mPaymentId);
        }
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_NAME, mGroupName);
        bundle.putInt(PARAM_TYPE, mType);
        bundle.putString(PARAM_SQUAD, mSquadId);
        bundle.putString(PARAM_PAYMENT, mPaymentId);
        bundle.putInt(PARAM_CHOOSE, chooseType);
        bundle.putString(PARAM_USER, mUserId);
        bundle.putBoolean(PARAM_FOREGROUND, isForeground);
        bundle.putSerializable(PARAM_JSON, mPayment);
    }

    @Override
    protected void destroyView() {

    }

    @Click({R.id.ui_archive_creator_rich_editor_template_images_clear,
            R.id.ui_group_payment_time_picker_background,
            R.id.ui_group_payment_amount_controls_agree,
            R.id.ui_group_payment_amount_controls_disagree})
    private void viewClick(View view) {
        switch (view.getId()) {
            case R.id.ui_archive_creator_rich_editor_template_images_clear:
                if (null != imageAdapter && imageAdapter.getItemCount() > 1) {
                    warningClearSelectedImages();
                }
                break;
            case R.id.ui_group_payment_time_picker_background:
                showTimePicker(false);
                break;
            case R.id.ui_group_payment_amount_controls_agree:
                // 同意
                updateExpend(Payment.State.AGREE);
                break;
            case R.id.ui_group_payment_amount_controls_disagree:
                // 拒绝
                updateExpend(Payment.State.REJECT);
                break;
        }
    }

    private void warningUpdateExpend(final int state) {
        agreeButton.setEnabled(false);
        rejectButton.setEnabled(false);
        String text = format("<font color=\"#%s\">%s</font>", (state == Payment.State.AGREE ? "3eb135" : "fe4848"), mPayment.getUnhandledStateText(state));
        String title = StringHelper.getString(R.string.ui_group_finance_user_payment_approve_warning, text);
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                updateExpend(state);
                return true;
            }
        }).setOnDialogCancelListener(new DialogHelper.OnDialogCancelListener() {
            @Override
            public void onCancel() {
                agreeButton.setEnabled(true);
                rejectButton.setEnabled(true);
            }
        }).setTitleText(title).setConfirmText(R.string.ui_base_text_continue).setCancelText(R.string.ui_base_text_cancel).show();
    }

    private void updateExpend(final int state) {
        agreeButton.setEnabled(false);
        rejectButton.setEnabled(false);
        setLoadingText(R.string.ui_group_finance_user_payment_approving);
        displayLoading(true);
        PaymentRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Payment>() {
            @Override
            public void onResponse(Payment payment, boolean success, String message) {
                super.onResponse(payment, success, message);
                displayLoading(false);
                if (success) {
                    String me = Cache.cache().userId;
                    resultData(mPaymentId + (mPayment.isCreator(me) ? Payment.ETC.CREATOR : "") +
                            (state == Payment.State.AGREE ? Payment.ETC.AGREE : "") +
                            (mPayment.isLastHandler(me) ? Payment.ETC.LAST : ""));
                } else {
                    agreeButton.setEnabled(true);
                    rejectButton.setEnabled(true);
                }
            }
        }).updateExpend(mPaymentId, state);
    }

    private void warningClearSelectedImages() {
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                clearTemplateImages();
                return true;
            }
        }).setTitleText(R.string.ui_text_archive_creator_editor_template_clear_images).setConfirmText(R.string.ui_base_text_confirm).show();
    }

    private void clearTemplateImages() {
        // 清除待上传列表
        if (waitingFroCompressImages.size() > 0) {
            for (String image : waitingFroCompressImages) {
                imageAdapter.remove(image);
                removeImageFromArchive(image);
            }
            waitingFroCompressImages.clear();
        }
        // 清除档案原有的已上传列表
        if (mPayment.getImage().size() > 0) {
            Iterator<Attachment> iterator = mPayment.getImage().iterator();
            while (iterator.hasNext()) {
                Attachment attachment = iterator.next();
                imageAdapter.remove(attachment.getUrl());
                iterator.remove();
            }
        }
        resetImages(waitingFroCompressImages, true);
    }

    private void resetImages(ArrayList<String> images, boolean replaceable) {
        if (replaceable) {
            imageAdapter.clear();
        }
        imageAdapter.remove(appender());
        for (String string : images) {
            Model model = new Model();
            model.setId(string);
            imageAdapter.update(model);
        }
        if (isEmpty(mPaymentId)) {
            appendAttacher();
        }
    }

    private Model appender;

    private Model appender() {
        if (null == appender) {
            appender = new Model();
            appender.setId("+");
        }
        return appender;
    }

    private void appendAttacher() {
        if (imageAdapter.getItemCount() < getMaxSelectable()) {
            imageAdapter.add(appender());
        }
    }

    // 需要增加照片
    private OnViewHolderClickListener imagePickClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            maxSelectable = 8;
            // 需要重新再选择图片
            startGalleryForResult();
        }
    };

    // 照片预览点击
    private ImageDisplayer.OnImageClickListener imagePreviewClickListener = new ImageDisplayer.OnImageClickListener() {
        @Override
        public void onImageClick(ImageDisplayer displayer, String url) {
            if (Utils.isUrl(url)) {
                ImageViewerFragment.isCollected = true;
                // 如果是以上传了的图片则调用imageViewer预览
                ImageViewerFragment.open(FinanceCreatorFragment.this, url);
            } else {
                // 相册预览
                startGalleryPreview(waitingFroCompressImages.indexOf(url));
            }
        }
    };

    // 照片删除
    private ImageDisplayer.OnDeleteClickListener imageDeleteClickListener = new ImageDisplayer.OnDeleteClickListener() {
        @Override
        public void onDeleteClick(String url) {
            waitingFroCompressImages.remove(url);
            if (Utils.isUrl(url)) {
                warningUploadedImageRemove(url);
            } else {
                imageAdapter.remove(url);
                appendAttacher();
            }
        }
    };

    private void warningUploadedImageRemove(final String url) {
        DeleteDialogHelper.helper().init(this).setTitleText(R.string.ui_text_archive_creator_editor_template_remove_uploaded_image).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                imageAdapter.remove(url);
                removeImageFromArchive(url);
                appendAttacher();
                return true;
            }
        }).show();
    }

    private void removeImageFromArchive(String url) {
        if (mPayment.getImage().size() > 0) {
            Iterator<Attachment> iterator = mPayment.getImage().iterator();
            while (iterator.hasNext()) {
                Attachment attachment = iterator.next();
                if (attachment.getUrl().equals(url)) {
                    iterator.remove();
                }
            }
        }
    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int dimen = getDimension(R.dimen.ui_base_dimen_margin_padding);

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            outRect.bottom = dimen;
            outRect.left = 0;
            GridLayoutManager manager = (GridLayoutManager) parent.getLayoutManager();
            assert manager != null;
            int spanCount = manager.getSpanCount();
            // 第一行有顶部无空白，其余行顶部有空白
            outRect.top = (position / spanCount == 0) ? 0 : dimen;
            // 最后列右侧无空白，其余列右侧有空白
            outRect.right = (position % spanCount < (spanCount - 1)) ? dimen : 0;
        }
    }

    private class ImageAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {
        private static final int VT_IMAGE = 0, VT_ATTACH = 1;

        private int width, height, margin;

        private void gotSize() {
            if (width == 0) {
                margin = getDimension(R.dimen.ui_base_dimen_margin_padding);
                int _width = getScreenWidth();
                int padding = margin * 5;
                int size = (_width - padding) / 4;
                width = size;
                height = size;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (get(position).getId().equals("+")) {
                return VT_ATTACH;
            } else {
                return VT_IMAGE;
            }
        }

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            gotSize();
            if (viewType == VT_IMAGE) {
                ImageViewHolder holder = new ImageViewHolder(itemView, FinanceCreatorFragment.this);
                holder.addOnDeleteClickListener(imageDeleteClickListener);
                holder.addOnImageClickListener(imagePreviewClickListener);
                holder.setImageSize(width, height);
                return holder;
            } else {
                return new AttacherItemViewHolder(itemView, FinanceCreatorFragment.this)
                        .setSize(width, height).setOnViewHolderClickListener(imagePickClickListener);
            }
        }

        @Override
        public int itemLayout(int viewType) {
            return viewType == VT_IMAGE ? (isEmpty(mPaymentId) ? R.layout.holder_view_image_deletable : R.layout.holder_view_image_normal) : R.layout.holder_view_attach_item;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof ImageViewHolder) {
                assert item != null;
                if (item instanceof Attachment) {
                    ((ImageViewHolder) holder).showContent(((Attachment) item).getUrl());
                } else {
                    ((ImageViewHolder) holder).showContent(item.getId());
                }
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
