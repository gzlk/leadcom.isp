package com.gzlk.android.isp.fragment.archive;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.bigkoo.pickerview.TimePickerView;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.archive.ArchiveRequest;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.activity.CoverPickFragment;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.SimpleDialogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.attachment.AttachmentViewHolder;
import com.gzlk.android.isp.holder.common.SimpleClickableViewHolder;
import com.gzlk.android.isp.holder.common.SimpleInputableViewHolder;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.common.Attachment;
import com.gzlk.android.isp.model.common.Seclusion;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <b>功能描述：</b>个人新增或编辑档案<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/25 09:50 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/25 09:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveCreatorFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_TYPE = "dnf_type";
    private static final String PARAM_GROUP = "dnf_group";
    private static final String PARAM_PRIVACY = "dnf_privacy";

    private static final String PARAM_TITLE = "dnf_title";
    private static final String PARAM_SOURCE = "dnf_source";
    private static final String PARAM_COVER = "anf_cover";

    public static ArchiveCreatorFragment newInstance(String params) {
        ArchiveCreatorFragment dnf = new ArchiveCreatorFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        // 档案类型，群档案或个人档案
        bundle.putInt(PARAM_TYPE, Integer.valueOf(strings[0]));
        // 档案id，编辑档案时传入，为空时表示新建档案
        bundle.putString(PARAM_QUERY_ID, strings[1]);
        if (strings.length > 2) {
            // 要发布到的组织id
            bundle.putString(PARAM_GROUP, strings[2]);
        }
        dnf.setArguments(bundle);
        return dnf;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        archiveType = bundle.getInt(PARAM_TYPE, Archive.Type.USER);
        archiveGroup = bundle.getString(PARAM_GROUP, "");
        privacy = bundle.getString(PARAM_PRIVACY, null);
        title = bundle.getString(PARAM_TITLE, "");
        source = bundle.getString(PARAM_SOURCE, "");
        cover = bundle.getString(PARAM_COVER, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_TYPE, archiveType);
        bundle.putString(PARAM_GROUP, archiveGroup);
        bundle.putString(PARAM_PRIVACY, privacy);
        title = titleHolder.getValue();
        bundle.putString(PARAM_TITLE, title);
        source = sourceHolder.getValue();
        bundle.putString(PARAM_SOURCE, source);
        bundle.putString(PARAM_COVER, cover);
    }

    // UI
    @ViewId(R.id.ui_archive_creator_cover)
    private View coverView;
    @ViewId(R.id.ui_archive_creator_title)
    private View titleInputView;
    @ViewId(R.id.ui_archive_creator_source)
    private View sourceView;
    @ViewId(R.id.ui_archive_creator_happen_date)
    private View timeView;
    @ViewId(R.id.ui_archive_creator_security)
    private View securityView;
    @ViewId(R.id.ui_archive_creator_introduction)
    private ClearEditText introductionView;
    @ViewId(R.id.ui_archive_creator_attachments_layout)
    private LinearLayout attachmentLayout;

    // holder
    private SimpleClickableViewHolder coverHolder;
    private SimpleInputableViewHolder titleHolder;
    private SimpleInputableViewHolder sourceHolder;
    private SimpleClickableViewHolder timeHolder;
    private SimpleClickableViewHolder securityHolder;

    // data
    private String[] strings;
    private FileAdapter mAdapter;
    private int archiveType;
    private String archiveGroup;
    private String privacy, title, source, cover;
    // 文件选择
    private FilePickerDialog filePickerDialog;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    private static final int REQ_COVER = ACTIVITY_BASE_REQUEST + 11;

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == PrivacyFragment.REQUEST_SECURITY) {
            // 隐私设置返回了
            privacy = getResultedData(data);
        } else if (requestCode == REQ_COVER) {
            cover = getResultedData(data);
        }
        super.onActivityResult(requestCode, data);
    }

    @Override
    public void doingInResume() {
        setCustomTitle(StringHelper.isEmpty(mQueryId) ? R.string.ui_text_document_create_fragment_title : R.string.ui_text_document_create_fragment_title_edit);
        setRightText(R.string.ui_base_text_save);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                tryCreateDocument();
            }
        });
        loadingArchive();
        setOnFileUploadingListener(mOnFileUploadingListener);
    }

    private OnFileUploadingListener mOnFileUploadingListener = new OnFileUploadingListener() {

        @Override
        public void onUploading(int all, int current, String file, long size, long uploaded) {

        }

        @Override
        public void onUploadingComplete(ArrayList<Attachment> uploaded) {
            createArchive();
        }
    };

    private void tryCreateDocument() {
        String title = titleHolder.getValue();
        if (isEmpty(title)) {
            ToastHelper.make().showMsg(R.string.ui_text_document_create_title_invalid);
            return;
        }
        String introduction = introductionView.getValue();
        if (isEmpty(introduction)) {
            ToastHelper.make().showMsg(R.string.ui_text_document_create_introduction_invalid);
            return;
        }
        Utils.hidingInputBoard(titleInputView);
        if (getWaitingForUploadFiles().size() > 0) {
            uploadFiles();
        } else {
            createArchive();
        }
    }

    private void createArchive() {
        handleUploadedItems();
        String title = titleHolder.getValue();
        String intro = introductionView.getValue();
        if (StringHelper.isEmpty(mQueryId)) {
            showImageHandlingDialog(R.string.ui_text_document_create_creating);
            createDocument(title, intro);
        } else {
            showImageHandlingDialog(R.string.ui_text_document_create_editing);
            editDocument(title, intro);
        }
    }

    private void createDocument(String title, String intro) {
        if (archiveType == Archive.Type.USER) {
            createUserArchive(title, intro);
        } else {
            createOrganizationArchive(title, intro);
        }
    }

    private Seclusion getSeclusion() {
        return PrivacyFragment.getSeclusion(privacy);
    }

    private void createUserArchive(String title, String intro) {
        Seclusion seclusion = getSeclusion();
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                hideImageHandlingDialog();
                if (success) {
                    ToastHelper.make().showMsg(message);
                    finish();
                }
            }
        }).add(cover, title, intro, seclusion.getStatus(), labels, office, images, video, attach);
    }

    private ArrayList<String> labels = new ArrayList<>();
    private ArrayList<Attachment> office = new ArrayList<>();
    private ArrayList<Attachment> images = new ArrayList<>();
    private ArrayList<Attachment> video = new ArrayList<>();
    private ArrayList<Attachment> attach = new ArrayList<>();

    // 处理上传之后的文件列表
    private void handleUploadedItems() {
        // 上传的原始文件
        if (getUploadedFiles().size() > 0) {
            for (int i = 0, len = getUploadedFiles().size(); i < len; i++) {
                Attachment attachment = getUploadedFiles().get(i);
                attachment.setArchiveId(mQueryId);
                if (attachment.isImage()) {
                    images.add(attachment);
                } else if (attachment.isOffice()) {
                    office.add(attachment);
                } else if (attachment.isVideo()) {
                    video.add(attachment);
                } else {
                    attach.add(attachment);
                }
            }
        }
    }

    private void createOrganizationArchive(String title, String intro) {
        Seclusion sec = getSeclusion();
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                hideImageHandlingDialog();
                if (success) {
                    ToastHelper.make().showMsg(message);
                    finish();
                }
            }
        }).add(archiveGroup, Archive.ArchiveType.NORMAL, cover, title, intro,
                labels, sec.getUserIds(), sec.getStatus(), office, images, video, attach);
    }

    private void editDocument(String title, String content) {
        if (archiveType == Archive.Type.USER) {
            editUserArchive(title, content);
        } else {
            editOrganizationArchive(title, content);
        }
    }

    private void editUserArchive(String title, String content) {
        Seclusion seclusion = getSeclusion();
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                hideImageHandlingDialog();
                if (success) {
                    ToastHelper.make().showMsg(message);
                    finish();
                }
            }
        }).update(mQueryId, cover, title, content, seclusion.getStatus(), labels, office, images, video, attach);
    }

    private void editOrganizationArchive(String title, String content) {
        Seclusion seclusion = getSeclusion();
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                hideImageHandlingDialog();
                if (success) {
                    ToastHelper.make().showMsg(message);
                    finish();
                }
            }
        }).update(mQueryId, cover, title, content, labels, seclusion.getUserIds(), seclusion.getStatus(), office, images, video, attach);
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    public int getLayout() {
        return R.layout.fragment_archive_creator;
    }

    @Override
    protected void onSwipeRefreshing() {

    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    private void loadingArchive() {
        if (StringHelper.isEmpty(mQueryId)) {
            // 空的queryId表示要新建档案
            initializeHolders(null);
        } else {
            fetchingArchive();
        }
    }

    private void fetchingArchive() {
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    if (null != archive) {
                        initializeHolders(archive);
                    } else {
                        warningEditBlank();
                    }
                }
            }
        }).find(archiveType, mQueryId, true);
    }

    private void warningEditBlank() {
        SimpleDialogHelper.init(Activity()).show(R.string.ui_text_document_edit_not_exist, R.string.ui_base_text_yes, R.string.ui_base_text_no_need, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                // 新建档案
                mQueryId = "";
                initializeHolders(null);
                return true;
            }
        }, new DialogHelper.OnDialogCancelListener() {
            @Override
            public void onCancel() {
                // 取消时返回上一页
                finish();
            }
        });
    }

    /**
     * 档案发生时间
     */
    private String happenDate = "";

    private void initializeHolders(Archive archive) {
        happenDate = null == archive ? "" : archive.getHappenDate();
        if (null != archive) {
            // 保存已有的隐私设置
            Seclusion seclusion = getSeclusion();
            if (isEmpty(seclusion.getArchiveId())) {
                // 新建fragment时才赋值
                seclusion.setArchiveId(archive.getId());
                if (isEmpty(archive.getGroupId())) {
                    seclusion.setStatus(archive.getAuthPublic());
                } else {
                    // 组织公开程度
                    seclusion.setUserIds(archive.getAuthUser());
                }
                privacy = PrivacyFragment.getSeclusion(seclusion);
            }
        }
        if (null == strings) {
            strings = StringHelper.getStringArray(R.array.ui_individual_new_document);
        }
        if (null == coverHolder) {
            coverHolder = new SimpleClickableViewHolder(coverView, this);
            coverHolder.addOnViewHolderClickListener(viewHolderClickListener);
        }
        if (isEmpty(cover)) {
            cover = null == archive ? "" : archive.getCover();
        }
        coverHolder.showContent(strings[0]);
        coverHolder.showImage(cover);
        // 标题
        if (null == titleHolder) {
            titleHolder = new SimpleInputableViewHolder(titleInputView, this);
        }
        if (isEmpty(title)) {
            title = null == archive ? "" : archive.getTitle();
        }
        titleHolder.showContent(format(strings[1], title));
        titleHolder.focusEnd();

        // 来源，应该不可以更改
        if (null == sourceHolder) {
            sourceHolder = new SimpleInputableViewHolder(sourceView, this);
        }
        sourceHolder.showContent(format(strings[2], source));

        // 时间
        if (null == timeHolder) {
            timeHolder = new SimpleClickableViewHolder(timeView, this);
            timeHolder.addOnViewHolderClickListener(viewHolderClickListener);
        }
        showCreateDate(null == archive ? new Date() : Utils.parseDate(StringHelper.getString(R.string.ui_base_text_date_time_format), archive.getHappenDate()));

        // 隐私
        if (null == securityHolder) {
            securityHolder = new SimpleClickableViewHolder(securityView, this);
            securityHolder.addOnViewHolderClickListener(viewHolderClickListener);
        }
        securityHolder.showContent(format(strings[4], PrivacyFragment.getPrivacy(PrivacyFragment.getSeclusion(privacy))));

        // 简介
        introductionView.setValue(null == archive ? "" : StringHelper.escapeFromHtml(archive.getIntro()));

        if (null == filePickerDialog) {
            DialogProperties properties = new DialogProperties();
            // 选择文件
            properties.selection_type = DialogConfigs.FILE_SELECT;
            // 可以多选
            properties.selection_mode = DialogConfigs.MULTI_MODE;
            // 最多可选文件数量
            properties.maximum_count = 0;
            // 文件扩展名过滤
            //properties.extensions = StringHelper.getStringArray(R.array.ui_base_file_pick_types);
            filePickerDialog = new FilePickerDialog(Activity(), properties);
            filePickerDialog.setTitle(StringHelper.getString(R.string.ui_text_document_picker_title));
            filePickerDialog.setPositiveBtnName(StringHelper.getString(R.string.ui_base_text_confirm));
            filePickerDialog.setNegativeBtnName(StringHelper.getString(R.string.ui_base_text_cancel));
            filePickerDialog.setDialogSelectionListener(dialogSelectionListener);
        }
        if (null == mAdapter) {
            mAdapter = new FileAdapter();
            setSupportLoadingMore(false);
            mRecyclerView.setAdapter(mAdapter);
        }
        updateArchiveAttachment(archive);
    }

    private void updateArchiveAttachment(Archive archive) {
        if (null == archive) return;
        // 显示已有的office文件列表
        if (null != archive.getOffice()) {
            office.addAll(archive.getOffice());
            mAdapter.update(archive.getOffice(), false);
        }
        // 显示已有的图片列表
        if (null != archive.getImage()) {
            // 最大可选择的文件数量要去掉原有的图片数量
            //filePickerDialog.getProperties().maximum_count = getMaxSelectable() - archive.getImage().size();
            images.addAll(archive.getImage());
            mAdapter.update(archive.getImage(), false);
        }
        // 显示已有的视频文件列表
        if (null != archive.getVideo()) {
            video.addAll(archive.getVideo());
            mAdapter.update(archive.getVideo(), false);
        }
        // 显示已有的文件列表
        if (null != archive.getAttach()) {
            // 最大可选文件数量要去掉已有的文件数量
            //filePickerDialog.getProperties().maximum_count = getMaxSelectable() - archive.getAttachName().size();
            // 已有的文件列表
            attach.addAll(archive.getAttach());

            mAdapter.update(archive.getAttach(), false);
        }
    }

    private OnViewHolderClickListener viewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            switch (index) {
                case 0:
                    // 选择封面
                    // 到封面拾取器
                    openActivity(CoverPickFragment.class.getName(), format("%s,false,1,1", cover), REQ_COVER, true, false);
                    break;
                case 1:
                    // 选择日期
                    openDatePicker();
                    break;
                case 2:
                    Seclusion seclusion = getSeclusion();
                    String json = PrivacyFragment.getSeclusion(seclusion);
                    // 隐私设置
                    if (archiveType == Archive.Type.USER) {
                        // 个人隐私设置
                        openActivity(UserPrivacyFragment.class.getName(), json, PrivacyFragment.REQUEST_SECURITY, true, false);
                    } else {
                        // 组织档案隐私设置
                        openActivity(PrivacyFragment.class.getName(), json, PrivacyFragment.REQUEST_SECURITY, true, false);
                    }
                    break;
            }
        }
    };

    private DialogSelectionListener dialogSelectionListener = new DialogSelectionListener() {
        @Override
        public void onSelectedFilePaths(String[] strings) {
            // 更新待上传文件列表
            getWaitingForUploadFiles().clear();
            getWaitingForUploadFiles().addAll(Arrays.asList(strings));
            for (String string : getWaitingForUploadFiles()) {
                Attachment attachment = new Attachment(string);
                mAdapter.update(attachment);
            }
        }
    };

    private void resetSelectedFiles() {
        int size = mAdapter.getItemCount();
        if (size > 0) {
            List<String> tmp = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                Attachment att = mAdapter.get(i);
                if (att.isLocalFile()) {
                    tmp.add(att.getFullPath());
                }
            }
            filePickerDialog.markFiles(tmp);
        }
    }

    @Click({R.id.ui_tool_attachment_button})
    private void elementClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ui_tool_attachment_button:
                Utils.hidingInputBoard(introductionView);
                resetSelectedFiles();
                filePickerDialog.show();
                break;
        }
    }

    private void showCreateDate(Date date) {
        happenDate = Utils.format(StringHelper.getString(R.string.ui_base_text_date_time_format), date);
        timeHolder.showContent(StringHelper.format(strings[3], formatDate(happenDate)));
    }

    private void openDatePicker() {
        Utils.hidingInputBoard(introductionView);
        TimePickerView tpv = new TimePickerView.Builder(Activity(), new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                showCreateDate(date);
            }
        }).setType(new boolean[]{true, true, true, false, false, false})
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

    private OnViewHolderClickListener attachmentViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Attachment attachment = mAdapter.get(index);
            removeItems(attachment);
            mAdapter.remove(attachment);
        }
    };

    private void removeItems(Attachment attachment) {
        // 尝试删除office文档列表
        office.remove(attachment);
        // 尝试删除图片列表（这个列表是编辑档案时档案原有的列表）
        images.remove(attachment);
        // 从视频列表里删除
        video.remove(attachment);
        // 从其他附件里删除
        attach.remove(attachment);
        // 从待上传的列表里删除
        if (attachment.isLocalFile()) {
            getWaitingForUploadFiles().remove(attachment.getFullPath());
        }
        //filePickerDialog.getProperties().maximum_count = getMaxSelectable() - images.size() - names.size();
    }

    private class FileAdapter extends RecyclerViewAdapter<AttachmentViewHolder, Attachment> {
        @Override
        public AttachmentViewHolder onCreateViewHolder(View itemView, int viewType) {
            AttachmentViewHolder holder = new AttachmentViewHolder(itemView, ArchiveCreatorFragment.this);
            holder.addOnViewHolderClickListener(attachmentViewHolderClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_attachment;
        }

        @Override
        public void onBindHolderOfView(final AttachmentViewHolder holder, int position, @Nullable Attachment item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(Attachment item1, Attachment item2) {
            return 0;
        }
    }
}
