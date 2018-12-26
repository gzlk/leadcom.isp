package com.leadcom.android.isp.fragment.organization;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.layoutmanager.CustomGridLayoutManager;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;
import com.hlk.hlklib.lib.view.CorneredEditText;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.org.PaymentRequest;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseImageSelectableSupportFragment;
import com.leadcom.android.isp.fragment.common.ImageViewerFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.helper.popup.DateTimeHelper;
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
import com.leadcom.android.isp.model.organization.SubMember;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;


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
public class PaymentCreatorFragment extends BaseImageSelectableSupportFragment {

    private static final String PARAM_NAME = "pcf_param_name";
    private static final String PARAM_SQUAD = "pcf_param_squad";
    private static final String PARAM_PAYMENT = "pcf_param_payment";

    public static PaymentCreatorFragment newInstance(Bundle bundle) {
        PaymentCreatorFragment pcf = new PaymentCreatorFragment();
        pcf.setArguments(bundle);
        return pcf;
    }

    public static void open(BaseFragment fragment, String groupId, String groupName, String squadId, String paymentId) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, groupId);
        bundle.putString(PARAM_NAME, groupName);
        bundle.putString(PARAM_SQUAD, squadId);
        bundle.putString(PARAM_PAYMENT, paymentId);
        fragment.openActivity(PaymentCreatorFragment.class.getName(), bundle, REQUEST_CREATE, true, true);
    }

    @ViewId(R.id.ui_group_payment_amount)
    private CorneredEditText amountView;
    @ViewId(R.id.ui_group_payment_remark)
    private ClearEditText remarkView;
    @ViewId(R.id.ui_group_payment_time)
    private View timeView;
    @ViewId(R.id.ui_group_payment_user)
    private View userView;
    @ViewId(R.id.ui_archive_creator_rich_editor_attachment_title)
    private TextView attachmentsTitle;
    @ViewId(R.id.ui_archive_creator_rich_editor_template_images_clear)
    private View imageClearView;
    @ViewId(R.id.ui_archive_creator_rich_editor_template_images)
    private RecyclerView templateRecyclerView;

    private ImageAdapter imageAdapter;
    private Payment mPayment;
    private SimpleClickableViewHolder timeHolder, userHolder;
    private String mGroupName, mSquadId, mPaymentId;
    private String[] items;
    private boolean isCreating = false;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setCustomTitle(R.string.ui_group_finance_user_payment_create_title);

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

        if (isEmpty(mPaymentId)) {
            setRightText(R.string.ui_base_text_confirm);
            setRightTitleClickListener(new OnTitleButtonClickListener() {
                @Override
                public void onClick() {
                    tryCreatePayment();
                }
            });
        } else {
            attachmentsTitle.setText(R.string.ui_group_finance_user_payment_create_files_title1);
            imageClearView.setVisibility(View.GONE);
        }
    }

    private void tryCreatePayment() {
        String string = amountView.getValue();
        if (isEmpty(string)) {
            ToastHelper.helper().showMsg(R.string.ui_group_finance_user_payment_create_amount_error);
            return;
        }
        mPayment.setPayAmount(Double.valueOf(string));
        mPayment.setRemark(remarkView.getValue());
        if (isCreating) {
            ToastHelper.helper().showMsg(R.string.ui_group_finance_user_payment_creating);
            return;
        }
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
        PaymentRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Payment>() {
            @Override
            public void onResponse(Payment payment, boolean success, String message) {
                super.onResponse(payment, success, message);
                if (success) {
                    resultData(payment.getId());
                } else {
                    displayLoading(false);
                }
            }
        }).add(mPayment);
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
            ToastHelper.helper().showMsg(StringHelper.getString(R.string.ui_text_archive_creator_editor_attachment_uploading_failed, code, message));
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
        mSquadId = bundle.getString(PARAM_SQUAD, "");
        mPaymentId = bundle.getString(PARAM_PAYMENT, "");
        mPayment = (Payment) bundle.getSerializable(PARAM_JSON);
        tryCreateEmptyPayment();
    }

    private void tryCreateEmptyPayment() {
        if (null == mPayment) {
            mPayment = new Payment();
            mPayment.setId(mPaymentId);
            // 默认当前日期为缴费日期，可以修改
            mPayment.setPayDate(Utils.formatDateOfNow("yyyy-MM-dd 00:00:00"));
            mPayment.setUserId(Cache.cache().userId);
            mPayment.setUserName(Cache.cache().userName);
            mPayment.setGroupId(mQueryId);
            mPayment.setSquadId(mSquadId);
        }
    }

    private OnViewHolderClickListener clickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            if (!isEmpty(mPaymentId)) {
                return;
            }
            switch (index) {
                case 1:
                    // 选择缴费时间
                    openDateDialog();
                    break;
                case 2:
                    // 选择缴费人
                    GroupContactPickFragment.open(PaymentCreatorFragment.this, mQueryId, false, true, null);
                    break;
            }
        }

        private void openDateDialog() {
            DateTimeHelper.helper().setOnDateTimePickListener(new DateTimeHelper.OnDateTimePickListener() {
                @Override
                public void onPicked(Date date) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.set(Calendar.HOUR, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    String time = Utils.format(StringHelper.getString(R.string.ui_base_text_date_time_format), calendar.getTime());
                    mPayment.setPayDate(time);
                    timeHolder.showContent(format(items[0], formatDate(time) + "(可修改)"));
                }
            }).show(PaymentCreatorFragment.this, true, true, true, true, mPayment.getPayDate());
        }
    };

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        switch (requestCode) {
            case REQUEST_MEMBER:
                // 组织成员选择返回了
                String json = getResultedData(data);
                if (!isEmpty(json) && json.length() > 10) {
                    ArrayList<SubMember> subs = SubMember.fromJson(json);
                    SubMember member = subs.get(0);
                    mPayment.setUserId(member.getUserId());
                    mPayment.setUserName(member.getUserName());
                    userHolder.showContent(format(items[1], mPayment.getUserName() + "(必填项)"));
                }
                break;
        }
        super.onActivityResult(requestCode, data);
    }

    @Override
    public void doingInResume() {
        if (null == timeHolder) {
            timeHolder = new SimpleClickableViewHolder(timeView, this);
            timeHolder.addOnViewHolderClickListener(clickListener);
            timeHolder.showContent(format(items[0], formatDate(mPayment.getPayDate())));
        }
        if (null == userHolder) {
            userHolder = new SimpleClickableViewHolder(userView, this);
            userHolder.addOnViewHolderClickListener(clickListener);
            userHolder.showContent(format(items[1], mPayment.getUserName()));
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
        PaymentRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Payment>() {
            @Override
            public void onResponse(Payment payment, boolean success, String message) {
                super.onResponse(payment, success, message);
                displayLoading(false);
                if (success) {
                    mPayment = payment;
                    for (Attachment attachment : mPayment.getImage()) {
                        imageAdapter.add(attachment);
                    }
                    timeHolder.showContent(format(items[0], formatDate(mPayment.getPayDate())));
                    userHolder.showContent(format(items[1], mPayment.getUserName()));
                    amountView.setText(String.valueOf(mPayment.getPayAmount()));
                    amountView.setFocusable(false);
                    remarkView.setValue(mPayment.getRemark());
                    remarkView.setEditable(false);
                } else {
                    finish();
                }
            }
        }).find(mPaymentId);
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_NAME, mGroupName);
        bundle.putString(PARAM_SQUAD, mSquadId);
        bundle.putString(PARAM_PAYMENT, mPaymentId);
        bundle.putSerializable(PARAM_JSON, mPayment);
    }

    @Override
    protected void destroyView() {

    }

    @Click({R.id.ui_archive_creator_rich_editor_template_images_clear})
    private void viewClick(View view) {
        switch (view.getId()) {
            case R.id.ui_archive_creator_rich_editor_template_images_clear:
                if (null != imageAdapter && imageAdapter.getItemCount() > 1) {
                    warningClearSelectedImages();
                }
                break;
        }
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
                ImageViewerFragment.open(PaymentCreatorFragment.this, url);
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
                ImageViewHolder holder = new ImageViewHolder(itemView, PaymentCreatorFragment.this);
                holder.addOnDeleteClickListener(imageDeleteClickListener);
                holder.addOnImageClickListener(imagePreviewClickListener);
                holder.setImageSize(width, height);
                return holder;
            } else {
                return new AttacherItemViewHolder(itemView, PaymentCreatorFragment.this)
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