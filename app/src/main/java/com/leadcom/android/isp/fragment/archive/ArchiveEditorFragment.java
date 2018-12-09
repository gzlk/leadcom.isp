package com.leadcom.android.isp.fragment.archive;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.hlk.hlklib.layoutmanager.CustomGridLayoutManager;
import com.hlk.hlklib.layoutmanager.CustomLinearLayoutManager;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;
import com.hlk.hlklib.lib.view.CorneredEditText;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.archive.ArchiveQueryRequest;
import com.leadcom.android.isp.api.archive.ArchiveRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.org.SquadRequest;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.etc.ImageCompress;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.fragment.common.ImageViewerFragment;
import com.leadcom.android.isp.fragment.organization.GroupSubordinateSquadMemberPickerFragment;
import com.leadcom.android.isp.helper.PreferenceHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.helper.popup.DateTimeHelper;
import com.leadcom.android.isp.helper.popup.DeleteDialogHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.helper.popup.EditableDialogHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.attachment.AttacherItemViewHolder;
import com.leadcom.android.isp.holder.attachment.AttachmentViewHolder;
import com.leadcom.android.isp.holder.common.LabelViewHolder;
import com.leadcom.android.isp.holder.common.SimpleClickableViewHolder;
import com.leadcom.android.isp.holder.common.SimpleInputableViewHolder;
import com.leadcom.android.isp.holder.individual.ImageViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.archive.ArchiveQuery;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.common.Seclusion;
import com.leadcom.android.isp.model.organization.ActivityOption;
import com.leadcom.android.isp.model.organization.Squad;
import com.leadcom.android.isp.model.organization.SubMember;
import com.leadcom.android.isp.service.DraftService;
import com.leadcom.android.isp.task.AsyncExecutableTask;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import jp.wasabeef.richeditor.RichEditor;

/**
 * <b>功能描述：</b>通过编辑器新建档案<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/04 18:50 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ArchiveEditorFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_UPLOAD_TYPE = "aecf_upload_type";
    private static final String PARAM_EDITOR_TYPE = "aecf_archive_editor_type";
    private static final String PARAM_PASTE_CONTENT = "aecf_paste_content";

    private static final int UP_NOTHING = 0;
    private static final int UP_IMAGE = 1;
    private static final int UP_MUSIC = 2;
    private static final int UP_VIDEO = 3;
    private static final int UP_ATTACH = 4;
    private static final int UP_TEMPLATE = 5;

    private static boolean editorFocused = false;

    public static ArchiveEditorFragment newInstance(Bundle bundle) {
        ArchiveEditorFragment aecf = new ArchiveEditorFragment();
        aecf.setArguments(bundle);
        return aecf;
    }

    private static Bundle getBundle(String remoteDraftId, int archiveType) {
        Bundle bundle = new Bundle();
        // 传过来的档案id（草稿档案），需要从服务器上拉取草稿内容再编辑
        bundle.putString(PARAM_QUERY_ID, remoteDraftId);
        // 编辑器方式（附件方式、图文方式）
        bundle.putInt(PARAM_EDITOR_TYPE, archiveType);
        return bundle;
    }

    public static void open(BaseFragment fragment, String remoteDraftId, int archiveType) {
        fragment.openActivity(ArchiveEditorFragment.class.getName(), getBundle(remoteDraftId, archiveType), REQUEST_CREATE, true, true);
    }

    public static void open(Context context, String remoteDraftId, int archiveType) {
        BaseActivity.openActivity(context, ArchiveEditorFragment.class.getName(), getBundle(remoteDraftId, archiveType), REQUEST_CREATE, true, true);
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        uploadType = bundle.getInt(PARAM_UPLOAD_TYPE, UP_NOTHING);
        editorType = bundle.getInt(PARAM_EDITOR_TYPE, Archive.ArchiveType.MULTIMEDIA);
        isPasteContent = bundle.getBoolean(PARAM_PASTE_CONTENT, false);
        String json = bundle.getString(PARAM_JSON, Model.EMPTY_JSON);
        if (!isEmpty(json)) {
            mArchive = Archive.fromJson(json);
            if (isEmpty(mArchive.getId())) {
                createNewDraftArchive();
            }
        } else {
            createNewDraftArchive();
        }
    }

    // 新建一个草稿档案
    private void createNewDraftArchive() {
        mArchive = new Archive();
        // 新建的档案都为临时档案
        //mArchive.setId(Archive.getDraftId());
        // 标记是否为组织档案
        if (editorType == Archive.ArchiveType.ACTIVITY) {
            mArchive.setGroupId(mQueryId);
            mQueryId = "";
        }
        // 档案默认向所有人公开的
        mArchive.setAuthPublic(Seclusion.Type.Public);
        // 默认草稿作者为当前登录用户
        mArchive.setUserId(Cache.cache().userId);
        mArchive.setUserName(Cache.cache().userName);
        // 档案来源
        mArchive.setSource(Cache.cache().userName);
        // 档案模板设置为图文或附件
        mArchive.setDocType(editorType);
        // 默认草稿的创建日期为当前日期
        mArchive.setCreateDate(Utils.format(getString(R.string.ui_base_text_date_time_format), Utils.timestamp()));
        // 默认草稿的发生日期
        mArchive.setHappenDate(Utils.format(getString(R.string.ui_base_text_date_time_format), Utils.timestamp()));
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_UPLOAD_TYPE, uploadType);
        bundle.putInt(PARAM_EDITOR_TYPE, editorType);
        bundle.putString(PARAM_JSON, Archive.toJson(mArchive));
        bundle.putBoolean(PARAM_PASTE_CONTENT, isPasteContent);
    }

    @Override
    protected boolean checkStillEditing() {
        if (mArchive.isAttachmentArchive() || isEmpty(mArchive.getGroupId())) {
            // 附件方式需要检测是否真要放弃
            String title = titleView.getValue();
            String content = Utils.clearContentHtml(mArchive.getContent());
            if (!isEmpty(title)) {
                if (isEmpty(mArchive.getTitle())) {
                    return true;
                } else if (!title.equals(mArchive.getTitle())) {
                    return true;
                }
            } else if (!isEmpty(content)) {
                return true;
            }
        }
        return super.checkStillEditing();
    }

    @Override
    protected void warningStillInEditing() {
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                finish();
                return true;
            }
        }).setTitleText(R.string.ui_text_archive_creator_still_edit).setCancelText(R.string.ui_base_text_continue_edit).setConfirmText(R.string.ui_base_text_abandon).show();
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

    @ViewId(R.id.ui_archive_creator_rich_editor_title)
    private ClearEditText titleView;
    @ViewId(R.id.ui_archive_creator_rich_editor_content)
    private RichEditor mEditor;
    @ViewId(R.id.ui_archive_creator_toolbar_top_line)
    private View toolbarTopLine;
    @ViewId(R.id.ui_archive_creator_action_image)
    private CustomTextView mImageIcon;
    @ViewId(R.id.ui_archive_creator_action_font)
    private CustomTextView mFontIcon;
    @ViewId(R.id.ui_archive_creator_action_attachment)
    private CustomTextView mAttachmentIcon;
    @ViewId(R.id.ui_archive_creator_action_video)
    private CustomTextView mVideoIcon;
    @ViewId(R.id.ui_archive_creator_action_audio)
    private CustomTextView mAudioIcon;
    @ViewId(R.id.ui_archive_creator_action_link)
    private CustomTextView mLinkIcon;
    @ViewId(R.id.ui_archive_creator_font_style_layout)
    private View fontStyleLayout;
    @ViewId(R.id.ui_archive_creator_rich_editor_uploader)
    private View updatingIndicator;
    @ViewId(R.id.ui_archive_creator_controls_layout)
    private View multimediaControlView;
    @ViewId(R.id.ui_archive_creator_rich_editor_multimedia)
    private View multimediaView;
    @ViewId(R.id.ui_archive_creator_rich_editor_attachment)
    private View attachmentView;
    @ViewId(R.id.ui_archive_creator_rich_editor_participant1)
    private View participantView1;
    @ViewId(R.id.ui_tool_attachment_button_upload)
    private View attachmentUploadView;
    @ViewId(R.id.ui_tool_attachment_upload_used_times)
    private View attachmentUploadUsedTimes;
    @ViewId(R.id.ui_tool_attachment_upload_used_times_text)
    private TextView attachmentUploadUsedTimer;
    @ViewId(R.id.ui_archive_creator_rich_editor_template)
    private View templateView;
    @ViewId(R.id.ui_tool_swipe_refreshable_recycler_view)
    private RecyclerView attachmentRecyclerView;

    private SimpleInputableViewHolder participantHolder1;
    // 创建成功的档案信息
    private Archive mArchive;
    /**
     * 当前上传的文件类型：1=图片，2=音乐，3=视频
     */
    private int uploadType = UP_NOTHING;
    /**
     * 默认图文编辑方式
     */
    private int editorType = Archive.ArchiveType.MULTIMEDIA;
    /**
     * 是否是组织档案，默认个人档案
     */
    private boolean isGroupArchive = false;
    private boolean isLongClickEditor = false, isOpenOther = false;
    /**
     * 是否是粘贴了内容
     */
    private boolean isPasteContent = false, isShareDraft = false, isCreating = false;

    private DraftSavingReceiver draftSavingReceiver;

    private void registerDraftSavingReceiver() {
        if (null == draftSavingReceiver) {
            draftSavingReceiver = new DraftSavingReceiver();
        }
        IntentFilter intent = new IntentFilter();
        intent.addAction(DraftService.ACTION_DRAFT);
        Activity().registerReceiver(draftSavingReceiver, intent);
    }

    @Override
    public void onDestroy() {
        Activity().unregisterReceiver(draftSavingReceiver);
        super.onDestroy();
    }

    private boolean isActivity() {
        return editorType == Archive.ArchiveType.ACTIVITY;
    }

    private boolean isAttachment() {
        return editorType == Archive.ArchiveType.ATTACHMENT;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        editorFocused = false;
        super.onActivityCreated(savedInstanceState);
        if (null == templateItems) {
            templateItems = StringHelper.getStringArray(R.array.ui_text_archive_creator_editor_template_values);
        }
        // 组织id默认当前首页选中的组织
        String groupId = PreferenceHelper.get(Cache.get(R.string.pf_last_login_user_group_current, R.string.pf_last_login_user_group_current_beta), "");
        if (isEmpty(groupId)) {
            ToastHelper.make().showMsg(0);
            // 不必要保存草稿
            isOpenOther = true;
            finish();
            return;
        }
        if (null == mArchive) {
            createNewDraftArchive();
        }
        mArchive.setGroupId(groupId);
        registerDraftSavingReceiver();
        mEditor.setPadding(10, 10, 10, 10);
        mEditor.setBackgroundColor(Color.WHITE);
        mEditor.setPlaceholder(StringHelper.getString(R.string.ui_text_archive_creator_content_hint));
        mEditor.setOnTextChangeListener(textChangeListener);
        mEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editorFocused = true;
            }
        });
        mEditor.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isLongClickEditor = true;
                // 长按了编辑器
                return false;
            }
        });
        // 每次最大选取1张图片
        maxSelectable = 1;
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

        if (Build.VERSION.SDK_INT < 19) {
            toolbarTopLine.setVisibility(View.VISIBLE);
        }
        //mEditor.focusEditor();
        // 如果不是传入的服务器草稿id则判断是否有草稿
        if (isEmpty(mQueryId)) {
            // 图文模式下检索是否有未提交的草稿
            if (!isActivity() && !isAttachment()) {
                fetchingDraft();
            }
        } else {
            fetchingSingleDraft();
        }
        if (Cache.cache().isMe()) {
            mVideoIcon.setVisibility(View.VISIBLE);
            mAudioIcon.setVisibility(View.VISIBLE);
        }
        resetEditorLayout();
        attachmentRecyclerView.setLayoutManager(new CustomLinearLayoutManager(attachmentRecyclerView.getContext()));
        aAdapter = new AttachmentAdapter();
        attachmentRecyclerView.setAdapter(aAdapter);
        participantHolder1 = new SimpleInputableViewHolder(participantView1, this);
        participantHolder1.setOnViewHolderElementClickListener(elementClickListener);
        participantHolder1.showContent(format(templateItems[2], ""));
        participantHolder1.setEditable(false);
        resetTitle();
        // 根据组织id拉取支部列表，并且查看当前用户所在的第一个支部
        resetSquadInfo();
    }

    private void resetTitle() {
        String title = "";
        switch (editorType) {
            case Archive.ArchiveType.ACTIVITY:
                title = StringHelper.getString(R.string.ui_group_activity_launch_title);
                break;
            case Archive.ArchiveType.ATTACHMENT:
                title = StringHelper.getString(R.string.ui_archive_creator_selector_text_attachment);
                break;
            case Archive.ArchiveType.MULTIMEDIA:
                title = StringHelper.getString(R.string.ui_archive_creator_selector_text_image);
                break;
            case Archive.ArchiveType.SUGGEST:
                break;
            case Archive.ArchiveType.TEMPLATE:
                title = StringHelper.getString(R.string.ui_archive_creator_selector_text_template);
                break;
        }
        if (isAttachment() || isActivity()) {
            setCustomTitle(title);
        } else {
            setCustomTitle(StringHelper.getString(R.string.ui_text_document_create_fragment_title, title));
        }
        setRightIcon(0);
        setRightText(isActivity() ? R.string.ui_base_text_publish : R.string.ui_base_text_commit);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                if (isActivity()) {
                    // 发布
                    if (resetActivityParameters()) {
                        if (!tryUploadingTemplateImages()) {
                            createActivity();
                        }
                    }
                } else {
                    // 显示附加菜单信息
                    tryCreateArchive();
                    //openSettingDialog();
                }
            }
        });
    }

    private void resetSquadInfo() {
        if (isEmpty(mArchive.getGroupId())) {
            return;
        }
        // 拉取当前用户加入的第一个组织支部
        SquadRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Squad>() {
            @Override
            public void onResponse(Squad squad, boolean success, String message) {
                super.onResponse(squad, success, message);
                if (success && null != squad && !isEmpty(squad.getId())) {
                    mArchive.setSquadId(squad.getId());
                    mArchive.setBranch(squad.getName());
                }
            }
        }).findFirstJoinedSquad(mArchive.getGroupId());
    }

    private void createActivity() {
        if (isCreating) {
            ToastHelper.make().showMsg(R.string.ui_group_activity_editor_still_creating);
            return;
        }
        isCreating = true;
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    resultData(archive.getId());
                } else {
                    // 创建失败后可以再次尝试创建一下活动
                    isCreating = false;
                }
            }
        }).createActivity(mArchive);
    }

    private void resetEditorLayout() {
        if (null == mArchive) {
            createNewDraftArchive();
        }
        attachmentView.setVisibility(mArchive.isAttachmentArchive() ? View.VISIBLE : View.GONE);
        multimediaControlView.setVisibility(mArchive.isMultimediaArchive() ? View.VISIBLE : View.GONE);
        multimediaView.setVisibility(mArchive.isTemplateArchive() || mArchive.isActivity() ? View.GONE : View.VISIBLE);
        templateView.setVisibility(mArchive.isTemplateArchive() || mArchive.isActivity() ? View.VISIBLE : View.GONE);
        if (mArchive.isTemplateArchive() || mArchive.isActivity()) {
            isGroupArchive = true;
        }
    }

    private void fetchingSingleDraft() {
        ArchiveQueryRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<ArchiveQuery>() {
            @Override
            public void onResponse(ArchiveQuery archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success && null != archive) {
                    mArchive = archive.getDocDraft();
                    restoreArchiveToEdit();
                } else {
                    ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_create_draft_not_exists);
                    finish();
                }
            }
        }).find(Archive.Type.DRAFT, mQueryId);
    }

    private void restoreArchiveToEdit() {
        if (isEmpty(mArchive.getGroupId())) {
            // 草稿重新编辑时，重置组织信息
            mArchive.setGroupId(PreferenceHelper.get(Cache.get(R.string.pf_last_login_user_group_current, R.string.pf_last_login_user_group_current_beta), ""));
            resetSquadInfo();
        }
        resetEditorLayout();
        isGroupArchive = !isEmpty(mArchive.getGroupId());
        titleView.setValue(mArchive.getTitle());
        if (mArchive.isMultimediaArchive()) {
            mArchive.resetImageStyle();
            mEditor.setHtml(mArchive.getContent());
        } else if (mArchive.isTemplateArchive()) {
            isGroupArchive = true;
            maxSelectable = 8;
            initializeTemplate();
            timeHolder.showContent(format(templateItems[0], (mArchive.isDefaultHappenDate() ? "选择时间(必填)" : formatDate(mArchive.getHappenDate()))));
            addressHolder.showContent(format(templateItems[1], (isEmpty(mArchive.getSite()) ? "" : mArchive.getSite())));
            participantHolder.showContent(format(templateItems[2], (isEmpty(mArchive.getParticipant()) ? "" : mArchive.getParticipant())));
            authorHolder.showContent(format(templateItems[3], mArchive.getUserName()));
            topicContent.setText(mArchive.getTopic());
            minuteContent.setText(mArchive.getResolution());
            if (mArchive.getImage().size() > 0) {
                resetAttachmentImages(mArchive.getImage());
            }
        }
        setCustomTitle(R.string.ui_text_document_create_fragment_title_edit);
    }

    private void fetchingDraft() {
        ArchiveRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Archive>() {
            @Override
            public void onResponse(List<Archive> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success && null != list && list.size() > 0) {
                    if (list.size() <= 1) {
                        warningSingleDraft(list.get(0));
                    } else {
                        warningDraftExist();
                    }
                }
            }
        }).listDraft(remotePageNumber);
    }

    private void warningSingleDraft(final Archive draft) {
        final String json = Archive.toJson(draft);
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                // 继续编辑则直接停留在本页面
                mArchive = Archive.fromJson(json);
                restoreArchiveToEdit();
                return true;
            }
        }).setOnDialogCancelListener(new DialogHelper.OnDialogCancelListener() {
            @Override
            public void onCancel() {
                // 打开到草稿详情页查看评论，无论是个人档案草稿还是组织档案草稿都是组织档案
                ArchiveDetailsFragment.open(ArchiveEditorFragment.this, draft, true);
                finish();
            }
        }).setTitleText(R.string.ui_text_archive_creator_editor_create_draft_1)
                .setConfirmText(R.string.ui_text_archive_creator_editor_create_draft_right_button_text).setCancelText(R.string.ui_text_archive_creator_editor_create_draft_to_details).show();
    }

    private void warningDraftExist() {
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                // 打开草稿选择页面
                ArchiveDraftFragment.open(ArchiveEditorFragment.this);
                finish();
                return true;
            }
        }).setTitleText(R.string.ui_text_archive_creator_editor_create_draft_more).setConfirmText(R.string.ui_base_text_have_a_look).show();
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        switch (requestCode) {
            case REQUEST_COVER:
                break;
            case REQUEST_VIDEO:
                // 视频选择返回了
                String videoPath = getGalleryResultedPath(data);
                videoUrl.setValue(videoPath);
                showFileSize(true, videoPath, videoSize);
                break;
            case REQUEST_MUSIC:
                // 音乐文件选择返回了
                String musicPath = getGalleryResultedPath(data);
                musicUrl.setValue(musicPath);
                showFileSize(false, musicPath, musicSize);
                break;
            case REQUEST_ATTACHMENT:
                if (null != data) {
                    mediaFiles.clear();
                    mediaFiles.addAll(data.<MediaFile>getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES));
                    //aAdapter.clear();
                    for (MediaFile file : mediaFiles) {
                        Attachment attachment = new Attachment(file.getPath());
                        attachment.setUrl(file.getPath());
                        aAdapter.add(attachment);
                    }
                }
                attachmentUploadView.setVisibility(aAdapter.getItemCount() > 0 ? View.VISIBLE : View.GONE);
                break;
            case REQUEST_DRAFT:
                // 草稿选择完毕
                String draftJson = getResultedData(data);
                mArchive = Archive.fromJson(StringHelper.replaceJson(draftJson, true));
                restoreArchiveToEdit();
                break;
            case REQUEST_SELECT:
                // 档案参与人选择完毕
                ArrayList<SubMember> members = SubMember.fromJson(getResultedData(data));
                String names = "";
                String old = "";
                if (null != participantHolder) {
                    old = participantHolder.getValue();
                }
                List<String> oNames = null;
                if (!isEmpty(old)) {
                    oNames = Arrays.asList(old.split("、"));
                }
                if (null != oNames) {
                    for (String name : oNames) {
                        names += (isEmpty(names) ? "" : "、") + name;
                    }
                }
                if (mArchive.isActivity() || mArchive.isTemplateArchive() || mArchive.isAttachmentArchive()) {
                    // 活动、记录、附件档案，参与人选择之后直接覆盖原有的选择记录
                    mArchive.getGroupIdList().clear();
                    mArchive.getGroSquMemberList().clear();
                    names = "";
                    oNames = null;
                }
                if (null != members && members.size() > 0) {
                    for (SubMember member : members) {
                        if (isEmpty(member.getUserName())) {
                            member.setUserName(StringHelper.getString(R.string.ui_base_text_no_name));
                        }
                        if (null == oNames || !oNames.contains(member.getUserName())) {
                            if (!names.contains(member.getUserName())) {
                                names += (isEmpty(names) ? "" : "、") + member.getUserName();
                            }
                        }
                        if (mArchive.isActivity()) {
                            if (member.isGroup()) {
                                if (!mArchive.getGroupIdList().contains(member.getUserId())) {
                                    mArchive.getGroupIdList().add(member.getUserId());
                                }
                            } else if (member.isMember()) {
                                if (!mArchive.getGroSquMemberList().contains(member)) {
                                    mArchive.getGroSquMemberList().add(member);
                                }
                            }
                        } else {
                            if (!mArchive.getParticipantIdList().contains(member.getUserId())) {
                                mArchive.getParticipantIdList().add(member.getUserId());
                            }
                        }
                    }
                }
                mArchive.setParticipant(names);
                mArchive.setParticipator(names);
                if (null != participantHolder) {
                    participantHolder.showContent(format(templateItems[2], names));
                }
                if (null != participantHolder1) {
                    participantHolder1.showContent(format(templateItems[2], names));
                }
                break;
            case REQUEST_MEMBER:
                ArrayList<SubMember> subMembers = SubMember.fromJson(getResultedData(data));
                warningShareDraftTo(SubMember.getMemberNames(subMembers), SubMember.getUserIds(subMembers));
                break;
        }
        isOpenOther = false;
        super.onActivityResult(requestCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        isOpenOther = false;
    }

    @Override
    public void onStop() {
        if (!mArchive.isAttachmentArchive() && !mArchive.isActivity() && !isOpenOther) {
            // 图文的组织档案才保存草稿
            saveDraft();
        }
        super.onStop();
    }

    private void saveDraft() {
        mArchive.setTitle(titleView.getValue());
        // docType有时候为0，不知道什么原因，这样修改不知道是否正确
        if (mArchive.getDocType() < Archive.ArchiveType.MULTIMEDIA) {
            if (editorType < Archive.ArchiveType.MULTIMEDIA) {
                mArchive.setDocType(Archive.ArchiveType.MULTIMEDIA);
            } else {
                mArchive.setDocType(editorType);
            }
        }
//        if (null != siteText) {
//            mArchive.setSite(siteText.getValue());
//            //mArchive.setSource(creatorText.getValue());
//            // 保存可能手动输入添加的参与人
//            mArchive.setParticipant(participantText.getValue());
//        }
        if (mArchive.isTemplateArchive()) {
            resetTemplateArchive(false);
            // 保留用户选择了的图片
            if (waitingFroCompressImages.size() > 0) {
                for (String image : waitingFroCompressImages) {
                    Attachment attachment = new Attachment(image);
                    if (!mArchive.getImage().contains(attachment)) {
                        mArchive.getImage().add(attachment);
                    }
                }
            }
        }
        // 草稿标题可以为空、内容也可以为空，但两者不能同时为空
        if (!isEmpty(mArchive.getTitle()) || !isEmpty(mArchive.getContent())) {
            if (isEmpty(mArchive.getTitle())) {
                mArchive.setTitle(StringHelper.getString(R.string.ui_text_archive_creator_editor_title_blank));
            }
            if (isPasteContent && mArchive.isContentPasteFromOtherPlatform()) {
                // 如果是粘贴过来的内容，则清理里面所有非树脉自有的img标签
                mArchive.clearPastedContentImages();
            }
            DraftService.start(mArchive, mCompressedImageWidth, mCompressedImageHeight);
        }
    }

    private class DraftSavingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                String action = intent.getAction();
                log(action);
                assert action != null;
                if (action.equals(DraftService.ACTION_DRAFT)) {
                    try {
                        Archive archive = (Archive) intent.getSerializableExtra(DraftService.TAG_ARCHIVE);
                        if (null != archive && !isEmpty(archive.getId())) {
                            mArchive = archive;
                            if (mArchive.getImage().size() > 0) {
                                resetAttachmentImages(mArchive.getImage());
                            }
                        }
                        log(Archive.toJson(archive));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_archive_creator_richeditor;
    }

    @Override
    public void doingInResume() {
        if (mArchive.isTemplateArchive() || mArchive.isActivity()) {
            isGroupArchive = true;
            initializeTemplate();
        }
        //resetRightIcons();
    }

    /**
     * 设置活动档案的必须内容
     */
    private boolean resetTemplateArchive(boolean returnAble) {
        mArchive.setOwnType(Archive.Type.GROUP);
        // 设置模板档案的各个属性值
        if (returnAble) {
            if (mArchive.isDefaultHappenDate()) {
                ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_template_happen_date_null);
                return false;
            }
        }
        if (null != addressHolder) {
            mArchive.setSite(addressHolder.getValue());
        }
        if (returnAble) {
            if (isEmpty(mArchive.getSite())) {
                ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_template_site_null);
                return false;
            }
        }
        if (returnAble) {
            if (null != participantHolder) {
                mArchive.setParticipant(participantHolder.getValue());
            }
            if (isEmpty(mArchive.getParticipant())) {
                ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_template_participant_null);
                return false;
            }
        }
        if (null != topicContent) {
            mArchive.setTopic(topicContent.getValue());
        }
        if (returnAble) {
            if (isEmpty(mArchive.getTopic())) {
                ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_template_topic_null);
                return false;
            }
        }
        if (null != minuteContent) {
            mArchive.setResolution(minuteContent.getValue());
        }
        if (returnAble) {
            if (isEmpty(mArchive.getResolution())) {
                ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_template_minute_null);
                return false;
            }
        }
//            if (isEmpty(mArchive.getBranch())) {
//                ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_template_branch_null);
//                return false;
//            }
//        if (returnAble) {
//            if (isEmpty(mArchive.getProperty())) {
//                ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_template_property_null);
//                return false;
//            }
//        }
//        if (returnAble) {
//            if (isEmpty(mArchive.getCategory())) {
//                ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_template_category_null);
//                return false;
//            }
//        }
        return true;
    }

    private boolean resetActivityParameters() {
        mArchive.setTitle(titleView.getValue());
        if (isEmpty(mArchive.getTitle())) {
            ToastHelper.make().showMsg(R.string.ui_group_activity_editor_title_is_blank);
            return false;
        }
        if (mArchive.isDefaultHappenDate()) {
            ToastHelper.make().showMsg(R.string.ui_group_activity_editor_happen_date_is_blank);
            return false;
        } else {
            Date date = Utils.parseDate(StringHelper.getString(R.string.ui_base_text_date_time_format), mArchive.getHappenDate());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            if (!checkActivityDate(calendar.getTime())) {
                return false;
            }
//            if (isDateLessThanNow(date, 0)) {
//                String time = Utils.format("MM月dd日HH时", date);
//                ToastHelper.make().showMsg(StringHelper.getString(R.string.ui_group_activity_editor_time_limit_less_than_now, time));
//                return false;
//            }
            // 去掉 24 小时时间间隔的判断
//            if (isDateLessThanNow(date, 24)) {
//                ToastHelper.make().showMsg(R.string.ui_group_activity_editor_time_limit_less_than_24h_after_now);
//                return false;
//            }
        }
        mArchive.setSite(addressHolder.getValue());
        if (isEmpty(mArchive.getSite())) {
            ToastHelper.make().showMsg(R.string.ui_group_activity_editor_site_is_blank);
            return false;
        }
        mArchive.setParticipator(participantHolder.getValue());
        if (isEmpty(mArchive.getParticipator())) {
            ToastHelper.make().showMsg(R.string.ui_group_activity_editor_participator_is_blank);
            return false;
        }
        if (null != optionsAdapter) {
            Iterator<ActivityOption> iterator = optionsAdapter.iterator();
            while (iterator.hasNext()) {
                ActivityOption option = iterator.next();
                if (option.isSelected() && !mArchive.getAdditionalOptions().contains(option)) {
                    mArchive.getAdditionalOptions().add(option);
                }
            }
        }
        mArchive.setRecorder(authorHolder.getValue());
        // 去掉活动议题（2018-11-28）
        //mArchive.setTopic(topicContent.getValue());
        mArchive.setContent(minuteContent.getValue());
        return true;
    }

    private void tryCreateArchive() {
        String title = titleView.getValue();
        if (isEmpty(title)) {
            ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_title_invalid);
            return;
        }
        mArchive.setTitle(title);
        // docType有时候为0，不知道什么原因，这样修改不知道是否正确
        if (mArchive.getDocType() < Archive.ArchiveType.MULTIMEDIA) {
            if (editorType < Archive.ArchiveType.MULTIMEDIA) {
                mArchive.setDocType(Archive.ArchiveType.MULTIMEDIA);
            } else {
                mArchive.setDocType(editorType);
            }
        }
        if (!mArchive.isTemplateArchive()) {
            if (isEmpty(mArchive.getContent())) {
                ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_content_invalid);
                return;
            }
        }
        if (mArchive.isTemplateArchive()) {
            if (!resetTemplateArchive(true)) {
                return;
            }
        }
        mArchive.setOwnType(Archive.Type.ALL);
//        if (isGroupArchive) {
//            if (isUserArchive) {
//                mArchive.setOwnType(Archive.Type.ALL);
//            } else {
//                mArchive.setOwnType(Archive.Type.GROUP);
//            }
//            if (isEmpty(mArchive.getGroupId())) {
//                ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_create_group_null);
//                return;
//            }
//            if (mArchive.isDefaultHappenDate()) {
//                ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_create_happen_date_null);
//                return;
//            }
//            if (isEmpty(mArchive.getProperty())) {
//                ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_create_group_property_null);
//                return;
//            }
//            if (isEmpty(mArchive.getCategory())) {
//                ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_create_group_category_null);
//                return;
//            }
//        } else {
//            mArchive.setOwnType(Archive.Type.USER);
//            // 个人档案需要清空组织id
//            mArchive.setGroupId("");
//        }
        // 个人档案需要标签
//        if (isEmpty(mArchive.getGroupId()) && mArchive.getLabel().size() < 1) {
//            ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_create_label_null);
//            return;
//        }
        String author = mArchive.isTemplateArchive() ? authorHolder.getValue() : Cache.cache().userName;
        if (isEmpty(author)) {
            ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_create_author_null);
            return;
        }
        mArchive.setSource(author);
        if (mArchive.isTemplateArchive()) {
            if (!tryUploadingTemplateImages()) {
                createArchive();
            }
        } else {
            mEditor.getMarkdown();
        }
    }

    private boolean tryUploadingTemplateImages() {
        // 如果选择了图片，则压缩图片然后上传
        if (waitingFroCompressImages.size() > 0) {
            // 不需要显示上传进度
            needShowUploading = true;
            uploadType = UP_TEMPLATE;
            compressImage();
            return true;
        }
        return false;
    }

    private int lastEditorContentLength = 0;
    private RichEditor.OnTextChangeListener textChangeListener = new RichEditor.OnTextChangeListener() {
        @Override
        public void onTextChange(String s) {
            String text = s.replace("\\u003C", "<");
            // 文本为空或者首字符是 < 时，说明一般为 HTML 字符串
            if (isEmpty(text) || text.contains("html:")) {
                String html = text.replace("html:", "");
                mArchive.setContent(html);
                log("HTML: " + text);
                int len = text.length() - lastEditorContentLength;
                if (isLongClickEditor && len > 300) {
                    // 标记是复制来的内容
                    isPasteContent = true;
                    // 恢复长按监控
                    isLongClickEditor = false;
                    //mArchive.isContentPasteFromOtherPlatform();
                    warningPaste();
                }
                lastEditorContentLength = len;
            } else if (text.contains("mark:")) {
                text = Utils.clearContentHtml(text);
                //mArchive.setMarkdown(text.replace("mark:", ""));
                log("MARK: " + text);
                mArchive.setContent(Utils.clearContentHtml(mArchive.getContent()));
                // 重置 img 的 style
                mArchive.resetImageStyle();
                if (isPasteContent && mArchive.isContentPasteFromOtherPlatform()) {
                    // 如果是粘贴过来的内容，则清理里面所有非树脉自有的img标签
                    mArchive.clearPastedContentImages();
                }
                createArchive();
            }
        }
    };

    private void warningPaste() {
        DeleteDialogHelper.helper().init(this)
                .setLayout(R.layout.popup_dialog_rich_editor_paste)
                .setTitleText(R.string.ui_text_archive_creator_editor_create_paste_warning).setCancelText(0).show();
    }

    private void createArchive() {
        if (isCreating) {
            ToastHelper.make().showMsg(R.string.ui_group_archive_editor_still_creating);
            return;
        }
        isCreating = true;
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    // 提交成功，判断是否需要再提交个人档案
                    mArchive = archive;
                    createSuccess();
                } else {
                    isCreating = false;
                }
            }
        }).save(mArchive, false, false);
    }

    private void createSuccess() {
        if (null != mArchive && !isEmpty(mArchive.getId())) {
            isOpenOther = true;
            ArchiveDetailsFragment.open(ArchiveEditorFragment.this, mArchive);
        }
        resultSucceededActivity();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    private static boolean isDateLessThanNow(Date date, int addHours) {
        // 需要比较的时间，不需要比较分、秒
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (cal.get(Calendar.MINUTE) > 0 || cal.get(Calendar.SECOND) > 0) {
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
        }

        // 必须在当前时间之后的时间，不需要比较分、秒两个属性
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, addHours);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return cal.getTime().getTime() < calendar.getTime().getTime();
    }

    static boolean checkActivityDate(Date date) {
        if (isDateLessThanNow(date, 0)) {
            String time = Utils.format("MM月dd日HH时", date);
            ToastHelper.make().showMsg(StringHelper.getString(R.string.ui_group_activity_editor_time_limit_less_than_now, time));
            return false;
        }
        return true;
    }

    private void openDateTimePicker() {
        // 发生时间
        DateTimeHelper.helper().setOnDateTimePickListener(new DateTimeHelper.OnDateTimePickListener() {
            @Override
            public void onPicked(Date date) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                if (mArchive.isActivity()) {
                    // 去掉分、秒属性
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    if (!checkActivityDate(calendar.getTime())) {
                        return;
                    }
                    // 去掉必须24小时之后的间隔  2018-12-07
//                    if (isDateLessThanNow(date, 24)) {
//                        ToastHelper.make().showMsg(R.string.ui_group_activity_editor_time_limit_less_than_24h_after_now);
//                        return;
//                    }
                }
                String fullTime = Utils.format(StringHelper.getString(R.string.ui_base_text_date_time_format), calendar.getTime());
                mArchive.setHappenDate(fullTime);
                String time = mArchive.isActivity() ? formatDateTime(fullTime) : formatDate(fullTime);
                if (null != timeHolder) {
                    timeHolder.showContent(format(templateItems[0], time));
                }
            }
        }).show(ArchiveEditorFragment.this, true, true, true, mArchive.isActivity(), false, false, !mArchive.isTemplateArchive(), mArchive.getHappenDate());
    }

    private void insertImage(String url, String alt) {
        mEditor.insertImage(url, alt);
    }

    private OnImageSelectedListener imageSelectedListener = new OnImageSelectedListener() {
        @Override
        public void onImageSelected(ArrayList<String> selected) {
            if (mArchive.isTemplateArchive() || mArchive.isActivity()) {
                resetImages(selected, false);
            } else {
                if (null != selected && selected.size() > 0) {
                    // 上传多张图片并且放到编辑器中
                    uploadType = UP_IMAGE;
                    showUploading(true);
                    compressImage();
                } else {
                    ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_image_selected_nothing);
                }
            }
        }
    };

    private void showUploading(boolean shown) {
        updatingIndicator.setVisibility(shown ? View.VISIBLE : View.GONE);
    }

    private void stopUploadTimer() {
        if (null != uploadTimer) {
            uploadTimer.isStopped = true;
        }
    }

    private void resetWaitingUploadFiles() {
        if (aAdapter.getItemCount() > 0) {
            Iterator<Attachment> iterator = aAdapter.iterator();
            while (iterator.hasNext()) {
                Attachment attachment = iterator.next();
                if (attachment.isLocalFile()) {
                    attachment.setSelected(false);
                    aAdapter.update(attachment);
                }
            }
        }
    }

    /**
     * 上传完毕之后清理列表中的本地文件
     */
    private void clearUploadedFiles() {
        if (aAdapter.getItemCount() > 0) {
            Iterator<Attachment> iterator = aAdapter.iterator();
            while (iterator.hasNext()) {
                Attachment attachment = iterator.next();
                if (attachment.isLocalFile()) {
                    // 清理所有本地文件
                    iterator.remove();
                    aAdapter.remove(attachment);
                }
            }
        }
    }

    private OnFileUploadingListener uploadingListener = new OnFileUploadingListener() {

        @Override
        public void onUploading(int all, int current, String file, long size, long uploaded) {

        }

        @Override
        public void onUploadingComplete(ArrayList<Attachment> uploaded) {
            showUploading(false);
            stopUploadTimer();
            switch (uploadType) {
                case UP_IMAGE:
                    // 图片上传完毕，插入图片
                    for (Attachment attachment : uploaded) {
                        String url = attachment.getUrl();
                        // 如果上传完毕的是图片，则插入图片
                        if (ImageCompress.isImage(Attachment.getExtension(url))) {
                            insertImage(url, "");
                            //mArchive.getImage().add(uploaded.get(0));
                        }
                    }
                    break;
                case UP_MUSIC:
                    // 音频上传完毕，插入音频到编辑框中
                    String music = uploaded.get(0).getUrl();
                    mEditor.insertAudio(music);
                    musicUrl.setValue("");
                    musicSize.setVisibility(View.GONE);
                    //mArchive.getAttach().add(uploaded.get(0));
                    break;
                case UP_VIDEO:
                    // 视频上传完毕，插入视频到编辑框中
                    String video = uploaded.get(0).getUrl();
                    mEditor.insertVideo("", video);
                    videoUrl.setValue("");
                    videoSize.setVisibility(View.GONE);
                    //mArchive.getVideo().add(uploaded.get(0));
                    break;
                case UP_ATTACH:
                    // 上传了多个附件（一次最多9个）
                    //mEditor.insertHtml("");
                    if (null != uploaded) {
                        // 上传成功之后本地已选中的记录清空
                        mediaFiles.clear();
                        clearUploadedFiles();
                        //aAdapter.clear();
                        for (Attachment attachment : uploaded) {
                            if (attachment.isImage()) {
                                mArchive.getImage().add(attachment);
                            } else if (attachment.isOffice()) {
                                mArchive.getOffice().add(attachment);
                            } else if (attachment.isVideo()) {
                                mArchive.getVideo().add(attachment);
                            } else {
                                mArchive.getAttach().add(attachment);
                            }
                            aAdapter.update(attachment);
                        }
                        //mAdapter.clear();
                    }
                    break;
                case UP_TEMPLATE:
                    // 模板档案上传了图片
                    if (null != uploaded) {
                        mArchive.getImage().addAll(uploaded);
                    }
                    // 不是分享活动草稿时，直接创建活动档案
                    if (!isShareDraft) {
                        if (mArchive.isActivity()) {
                            createActivity();
                        } else {
                            // 模板档案上传图片完毕之后尝试发布档案
                            createArchive();
                        }
                    }
                    break;
            }
            // 上传完毕，设置上传方式为nothing
            uploadType = UP_NOTHING;
            // 插入完毕之后清空已上传的文件列表
            getUploadedFiles().clear();
        }

        @Override
        public void onUploadingFailed(int code, String message) {
            showUploading(false);
            ToastHelper.make().showMsg(StringHelper.getString(R.string.ui_text_archive_creator_editor_attachment_uploading_failed, code, message));
            if (null != uploadTimer) {
                final String text = format("失败(%d)，用时 ", code) + Utils.format("mm:ss", System.currentTimeMillis() - uploadTimer.startTicker);
                stopUploadTimer();
                Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        attachmentUploadUsedTimer.setText(text);
                    }
                }, 300);
            }
            resetWaitingUploadFiles();

            // 还原成什么都未上传的状态
            uploadType = UP_NOTHING;
        }
    };

    // 插入音乐的对话框
    private View musicDialogView;
    private ClearEditText musicUrl;
    private TextView musicSize;

    private void openMusicDialog() {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == musicDialogView) {
                    musicDialogView = View.inflate(Activity(), R.layout.popup_dialog_rich_editor_music, null);
                    musicUrl = musicDialogView.findViewById(R.id.ui_popup_rich_editor_music_url);
                    musicSize = musicDialogView.findViewById(R.id.ui_popup_rich_editor_music_size);
                }
                return musicDialogView;
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {

            }
        }).addOnEventHandlerListener(new DialogHelper.OnEventHandlerListener() {
            @Override
            public int[] clickEventHandleIds() {
                return new int[]{R.id.ui_popup_rich_editor_music_navigate};
            }

            @Override
            public boolean onClick(View view) {
                switch (view.getId()) {
                    case R.id.ui_popup_rich_editor_music_navigate:
                        // 打开文件选择器
                        chooseVideoFromLocalBeforeKitKat(REQUEST_MUSIC);
                        return false;
                }
                return true;
            }
        }).addOnDialogDismissListener(new DialogHelper.OnDialogDismissListener() {
            @Override
            public void onDismiss() {
                log("audio dialog dismissed");
                mAudioIcon.setTextColor(getColor(R.color.textColorHint));
            }
        }).addOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                String url = musicUrl.getValue();
                if (isEmpty(url)) {
                    ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_music_select_dialog_url_empty);
                    return false;
                }
                if (Utils.isUrl(url)) {
                    mEditor.insertAudio(url);
                } else {
                    uploadType = UP_MUSIC;
                    showUploading(true);
                    // 上传本地视频文件
                    getWaitingForUploadFiles().clear();
                    getWaitingForUploadFiles().add(url);
                    uploadFiles();
                }
                return true;
            }
        }).setPopupType(DialogHelper.FADE).show();
    }

    // 视频选择对话框
    private View videoDialogView;
    private ClearEditText videoUrl;
    private TextView videoSize;

    private void openVideoDialog() {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == videoDialogView) {
                    videoDialogView = View.inflate(Activity(), R.layout.popup_dialog_rich_editor_video, null);
                    //videoCover =  videoDialogView.findViewById(R.id.ui_popup_rich_editor_video_cover);
                    videoUrl = videoDialogView.findViewById(R.id.ui_popup_rich_editor_video_url);
                    videoSize = videoDialogView.findViewById(R.id.ui_popup_rich_editor_video_size);
                }
                return videoDialogView;
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {

            }
        }).addOnEventHandlerListener(new DialogHelper.OnEventHandlerListener() {
            @Override
            public int[] clickEventHandleIds() {
                return new int[]{R.id.ui_popup_rich_editor_video_navigate};
            }

            @Override
            public boolean onClick(View view) {
                switch (view.getId()) {
                    case R.id.ui_popup_rich_editor_video_navigate:
                        // 打开文件选择器选择视频文件
                        //chooseLocalVideo();
                        chooseVideoFromLocalBeforeKitKat(REQUEST_VIDEO);
                        return false;
                }
                return true;
            }
        }).addOnDialogDismissListener(new DialogHelper.OnDialogDismissListener() {
            @Override
            public void onDismiss() {
                log("video dialog dismissed");
                mVideoIcon.setTextColor(getColor(R.color.textColorHint));
            }
        }).addOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                String url = videoUrl.getValue();
                if (isEmpty(url)) {
                    ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_video_select_dialog_url_empty);
                    return false;
                }
                if (Utils.isUrl(url)) {
                    mEditor.insertVideo("", url);
                } else {
                    uploadType = UP_VIDEO;
                    showUploading(true);
                    // 上传本地视频文件
                    getWaitingForUploadFiles().clear();
                    getWaitingForUploadFiles().add(url);
                    uploadFiles();
                }
                return true;
            }
        }).setPopupType(DialogHelper.FADE).show();
    }

    private void warningShareDraftTo(String names, final ArrayList<String> userIds) {
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                shareDraftTo(userIds);
                return true;
            }
        }).setTitleText(getString(R.string.ui_text_archive_details_editor_setting_share_names, names, userIds.size())).setConfirmText(R.string.ui_base_text_share).show();
    }

    private void shareDraftTo(ArrayList<String> userIds) {
        mArchive.getShareUserIds().clear();
        mArchive.getShareUserIds().addAll(userIds);
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    ToastHelper.make().showMsg(R.string.ui_text_archive_details_editor_setting_share_draft);
                    isOpenOther = true;
                    if (null != archive) {
                        mArchive = archive;
                    }
                    ArchiveDetailsFragment.open(ArchiveEditorFragment.this, mArchive, true);
                    finish();
                }
            }
        }).save(mArchive, true, true);
    }

    private void showFileSize(boolean video, String path, TextView view) {
        File file = new File(path);
        view.setText(StringHelper.getString((video ? R.string.ui_text_archive_creator_editor_video_select_dialog_file_size : R.string.ui_text_archive_creator_editor_music_select_dialog_file_size), Utils.formatSize(file.length())));
        view.setVisibility(View.VISIBLE);
    }

    private String getPickType(int request) {
        switch (request) {
            case REQUEST_VIDEO:
                return "video/*";
            case REQUEST_MUSIC:
                return "audio/*";
            case REQUEST_ATTACHMENT:
            default:
                return "*/*";
        }
    }

    /**
     * API19 之前选择视频
     */
    protected void chooseVideoFromLocalBeforeKitKat(int request) {
        Intent mIntent = new Intent(Intent.ACTION_GET_CONTENT);
        mIntent.setType(getPickType(request));
        mIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        if (request == REQUEST_ATTACHMENT && Build.VERSION.SDK_INT >= 18) {
            // 设置可以多选
            mIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        try {
            isOpenOther = true;
            startActivityForResult(mIntent, request);
        } catch (ActivityNotFoundException e) {
            ToastHelper.make().showMsg("您的手机没有相册应用");
        }
    }

    // 插入超链接
    private View linkDialogView;
    private ClearEditText linkLabel, linkUrl;

    private void openLinkDialog() {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == linkDialogView) {
                    linkDialogView = View.inflate(Activity(), R.layout.popup_dialog_rich_editor_link, null);
                    linkLabel = linkDialogView.findViewById(R.id.ui_popup_rich_editor_link_label);
                    linkUrl = linkDialogView.findViewById(R.id.ui_popup_rich_editor_link_url);
                }
                return linkDialogView;
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {

            }
        }).addOnDialogDismissListener(new DialogHelper.OnDialogDismissListener() {
            @Override
            public void onDismiss() {
                log("link dialog dismissed");
                mLinkIcon.setTextColor(getColor(R.color.textColorHint));
            }
        }).addOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                String label = linkLabel.getValue();
                String url = linkUrl.getValue();
                if (isEmpty(label) || isEmpty(url)) {
                    ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_link_dialog_invalid_input);
                    return false;
                }
                if (!Utils.isUrl(url)) {
                    ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_link_dialog_invalid_url);
                    return false;
                }
                mEditor.insertLink(url, label);
                return true;
            }
        }).setPopupType(DialogHelper.FADE).show();
    }

    private AttachmentAdapter aAdapter;

    private OnViewHolderClickListener uploadedAttachmentViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Attachment attachment = aAdapter.get(index);
            mArchive.getAttach().remove(attachment);
            mArchive.getImage().remove(attachment);
            mArchive.getOffice().remove(attachment);
            mArchive.getVideo().remove(attachment);
            aAdapter.remove(attachment);
            attachmentUploadView.setVisibility(aAdapter.getItemCount() > 0 ? View.VISIBLE : View.GONE);
            removeSelectedFile(attachment.getFullPath());
        }
    };

    private void removeSelectedFile(String path) {
        Iterator<MediaFile> iterator = mediaFiles.iterator();
        while (iterator.hasNext()) {
            MediaFile file = iterator.next();
            if (file.getPath().equals(path)) {
                iterator.remove();
            }
        }
    }

    private View fileTypeDialogView;

    private void openFileTypeChooserDialog() {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == fileTypeDialogView) {
                    fileTypeDialogView = View.inflate(Activity(), R.layout.popup_dialog_rich_editor_attachment_chooser, null);
                }
                return fileTypeDialogView;
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {

            }
        }).addOnEventHandlerListener(new DialogHelper.OnEventHandlerListener() {
            @Override
            public int[] clickEventHandleIds() {
                return new int[]{
                        R.id.ui_popup_rich_editor_attachment_audios,
                        R.id.ui_popup_rich_editor_attachment_documents,
                        R.id.ui_popup_rich_editor_attachment_images,
                        R.id.ui_popup_rich_editor_attachment_videos
                };
            }

            @Override
            public boolean onClick(View view) {
                switch (view.getId()) {
                    case R.id.ui_popup_rich_editor_attachment_audios:
                        chooseAttachment(getIntent(UP_MUSIC));
                        break;
                    case R.id.ui_popup_rich_editor_attachment_documents:
                        chooseAttachment(getIntent(UP_ATTACH));
                        break;
                    case R.id.ui_popup_rich_editor_attachment_images:
                        chooseAttachment(getIntent(UP_IMAGE));
                        break;
                    case R.id.ui_popup_rich_editor_attachment_videos:
                        chooseAttachment(getIntent(UP_VIDEO));
                        break;
                }
                return true;
            }
        }).setAdjustScreenWidth(true).setPopupType(DialogHelper.SLID_IN_BOTTOM).show();
    }

    private ArrayList<MediaFile> mediaFiles = new ArrayList<>();

    private Intent getIntent(int type) {
        if (maxSelectable != 9) {
            maxSelectable = 9;
        }
        Intent intent = new Intent(Activity(), FilePickerActivity.class);
        Configurations configurations = new Configurations.Builder()
                .setCheckPermission(true)
                .setSelectedMediaFiles(mediaFiles)
                .enableVideoCapture(type == UP_VIDEO)
                .enableImageCapture(type == UP_IMAGE)
                .setShowAudios(type == UP_MUSIC)
                .setShowVideos(type == UP_VIDEO)
                .setShowImages(type == UP_IMAGE)
                .setShowFiles(type == UP_ATTACH)
                .setSkipZeroSizeFiles(true)
                .setMaxVideoFileSize(30 * 1024 * 1024)
                .setMaxSelection(getMaxSelectable() - aAdapter.getItemCount() + mediaFiles.size())
                .setSuffixes("txt", "pdf", "zip", "rar", "doc", "docx", "ppt", "pptx", "xls", "xlsx")
                .build();
        intent.putExtra(FilePickerActivity.CONFIGS, configurations);
        return intent;
    }

    private void chooseAttachment(Intent intent) {
        if (aAdapter.getItemCount() >= getMaxSelectable()) {
            ToastHelper.make().showMsg(StringHelper.getString(R.string.ui_archive_label_picker_picked_max, getMaxSelectable(), "附件"));
            return;
        }
        startActivityForResult(intent, REQUEST_ATTACHMENT);
    }

    private void prepareUploadAttachment() {
        if (uploadType == UP_ATTACH) {
            ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_attachment_dialog_uploading);
            return;
        }
        if (aAdapter.getItemCount() > 0) {
            Iterator<Attachment> iterator = aAdapter.iterator();
            //getWaitingForUploadFiles().clear();
            waitingFroCompressImages.clear();
            long maxLength = 0;
            while (iterator.hasNext()) {
                Attachment attachment = iterator.next();
                if (attachment.isLocalFile()) {
                    // 是本地文件时，放入待上传列表
                    waitingFroCompressImages.add(attachment.getFullPath());
                    //getWaitingForUploadFiles().add(attachment.getFullPath());
                    attachment.setSelected(true);
                    maxLength += attachment.getSize();
                    aAdapter.update(attachment);
                }
            }
            if (waitingFroCompressImages.size() > 0) {
                uploadType = UP_ATTACH;
                if (null == uploadTimer) {
                    uploadTimer = new UploadTimer(attachmentUploadUsedTimes, attachmentUploadUsedTimer);
                    uploadTimer.totalLength = maxLength;
                    uploadTimer.exec();
                }
                showUploading(true);
                compressImage();
            }
        }
    }

    private UploadTimer uploadTimer;

    private static class UploadTimer extends AsyncExecutableTask<Void, Integer, Void> {

        UploadTimer(View view, TextView textView) {
            viewSoftReference = new SoftReference<>(view);
            textViewSoftReference = new SoftReference<>(textView);
        }

        private long startTicker, totalLength;
        private boolean isStopped = false;
        private SoftReference<View> viewSoftReference;
        private SoftReference<TextView> textViewSoftReference;

        @Override
        protected void doBeforeExecute() {
            super.doBeforeExecute();
            viewSoftReference.get().setVisibility(View.VISIBLE);
            startTicker = System.currentTimeMillis();
            showTimer();
        }

        @Override
        protected void doProgress(Integer... values) {
            showTimer();
            super.doProgress(values);
        }

        @Override
        protected Void doInTask(Void... voids) {
            try {
                while (!isStopped) {
                    Thread.sleep(200);
                    publishProgress(50);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void showTimer() {
            long nowTicker = System.currentTimeMillis();
            TextView textView = textViewSoftReference.get();
            if (null != textView) {
                String text = format("共%s，已用时 ", Utils.formatSize(totalLength)) + Utils.format("mm:ss", nowTicker - startTicker);
                textView.setText(text);
            }
        }

        @Override
        protected void doAfterExecute() {
            //showTimer();
            super.doAfterExecute();
        }
    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    private class AttachmentAdapter extends RecyclerViewAdapter<AttachmentViewHolder, Attachment> {

        @Override
        public AttachmentViewHolder onCreateViewHolder(View itemView, int viewType) {
            AttachmentViewHolder holder = new AttachmentViewHolder(itemView, ArchiveEditorFragment.this);
            holder.addOnViewHolderClickListener(uploadedAttachmentViewHolderClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_attachment;
        }

        @Override
        public void onBindHolderOfView(AttachmentViewHolder holder, int position, @Nullable Attachment item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(Attachment item1, Attachment item2) {
            return 0;
        }
    }

    private void tryFocusEditor() {
        if (!editorFocused) {
            mEditor.focusEditor();
            editorFocused = true;
        }
    }

    @Click({R.id.ui_archive_creator_action_undo, R.id.ui_archive_creator_action_redo,
            R.id.ui_archive_creator_action_image, R.id.ui_archive_creator_action_font,
            R.id.ui_archive_creator_action_attachment, R.id.ui_archive_creator_action_video,
            R.id.ui_archive_creator_action_audio, R.id.ui_archive_creator_action_quote,
            R.id.ui_archive_creator_action_link, R.id.ui_archive_creator_action_ordered_list,
            R.id.ui_archive_creator_action_unordered_list, R.id.ui_tool_attachment_button,
            R.id.ui_archive_creator_action_multi_image,
            // 以下为字体格式设置
            R.id.ui_archive_creator_action_bold, R.id.ui_archive_creator_action_italic,
            R.id.ui_archive_creator_action_underline, R.id.ui_archive_creator_action_strike_through,
            R.id.ui_archive_creator_action_superscript, R.id.ui_archive_creator_action_subscript,
            R.id.ui_archive_creator_action_align_left, R.id.ui_archive_creator_action_align_center,
            R.id.ui_archive_creator_action_align_right,
            R.id.ui_archive_creator_action_heading1, R.id.ui_archive_creator_action_heading2,
            R.id.ui_archive_creator_action_heading3, R.id.ui_archive_creator_action_heading4,
            R.id.ui_archive_creator_action_heading5, R.id.ui_archive_creator_action_heading6,
            // 以下为模板方式点击事件
            R.id.ui_archive_creator_rich_editor_template_images_clear,
            // 附件模板中的上传按钮
            R.id.ui_tool_attachment_button_upload})
    private void elementClick(View view) {
        switch (view.getId()) {
            case R.id.ui_archive_creator_action_undo:
                mEditor.undo();
                break;
            case R.id.ui_archive_creator_action_redo:
                mEditor.redo();
                break;
            case R.id.ui_archive_creator_action_image:
                mImageIcon.setTextColor(getColor(R.color.colorAccent));
                tryFocusEditor();
                // 单张图片最大可以选取1张
                maxSelectable = 1;
                //openImageDialog();
                break;
            case R.id.ui_archive_creator_action_multi_image:
                maxSelectable = 9;
                isOpenOther = true;
                tryFocusEditor();
                waitingFroCompressImages.clear();
                // 需要重新再选择图片

                startGalleryForResult();
                break;
            case R.id.ui_archive_creator_action_font:
                fontStyleLayout.setVisibility(fontStyleLayout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                mFontIcon.setTextColor(getColor(fontStyleLayout.getVisibility() == View.GONE ? R.color.textColorHint : R.color.colorAccent));
                break;
            case R.id.ui_archive_creator_action_attachment:
            case R.id.ui_tool_attachment_button:
                mAttachmentIcon.setTextColor(getColor(R.color.colorAccent));
                // 插入一个附件
                // 附件的图标地址(24x24)http://120.25.124.199:8008/group1/M00/00/13/eBk66lngZ0-AW_1aAAAJiqCeKro517.png
                //openAttachmentDialog();
                openFileTypeChooserDialog();
                break;
            case R.id.ui_tool_attachment_button_upload:
                // 准备上传附件
                prepareUploadAttachment();
                break;
            case R.id.ui_archive_creator_action_video:
                mVideoIcon.setTextColor(getColor(R.color.colorAccent));
                tryFocusEditor();
                // 插入或上传一段视频
                // 视频封面地址：http://120.25.124.199:8008/group1/M00/00/13/eBk66lngcsOAUoWUAAAcAlhYhMk172.png
                openVideoDialog();
                break;
            case R.id.ui_archive_creator_action_audio:
                mAudioIcon.setTextColor(getColor(R.color.colorAccent));
                tryFocusEditor();
                // 插入或上传一段音乐
                openMusicDialog();
                break;
            case R.id.ui_archive_creator_action_quote:
                mEditor.setBlockquote();
                break;
            case R.id.ui_archive_creator_action_link:
                mLinkIcon.setTextColor(getColor(R.color.colorAccent));
                // 插入一个超链接
                openLinkDialog();
                break;
            case R.id.ui_archive_creator_action_ordered_list:
                mEditor.setOrderedList();
                break;
            case R.id.ui_archive_creator_action_unordered_list:
                mEditor.setUnorderedList();
                break;

            case R.id.ui_archive_creator_action_bold:
                mEditor.setBold();
                break;
            case R.id.ui_archive_creator_action_italic:
                mEditor.setItalic();
                break;
            case R.id.ui_archive_creator_action_underline:
                mEditor.setUnderline();
                break;
            case R.id.ui_archive_creator_action_strike_through:
                mEditor.setStrikeThrough();
                break;
            case R.id.ui_archive_creator_action_superscript:
                mEditor.setSuperscript();
                break;
            case R.id.ui_archive_creator_action_subscript:
                mEditor.setSubscript();
                break;
            case R.id.ui_archive_creator_action_align_left:
                mEditor.setAlignLeft();
                break;
            case R.id.ui_archive_creator_action_align_center:
                mEditor.setAlignCenter();
                break;
            case R.id.ui_archive_creator_action_align_right:
                mEditor.setAlignRight();
                break;
            case R.id.ui_archive_creator_action_heading1:
                mEditor.setHeading(1);
                break;
            case R.id.ui_archive_creator_action_heading2:
                mEditor.setHeading(2);
                break;
            case R.id.ui_archive_creator_action_heading3:
                mEditor.setHeading(3);
                break;
            case R.id.ui_archive_creator_action_heading4:
                mEditor.setHeading(4);
                break;
            case R.id.ui_archive_creator_action_heading5:
                mEditor.setHeading(5);
                break;
            case R.id.ui_archive_creator_action_heading6:
                mEditor.setHeading(6);
                break;
            case R.id.ui_archive_creator_rich_editor_template_images_clear:
                if (null != imageAdapter && imageAdapter.getItemCount() > 1) {
                    warningClearSelectedImages();
                }
                break;
        }
    }

    // 模板档案UI元素
    @ViewId(R.id.ui_archive_creator_rich_editor_time)
    private View timeView;
    @ViewId(R.id.ui_archive_creator_rich_editor_address)
    private View addressView;
    @ViewId(R.id.ui_archive_creator_rich_editor_participant)
    private View participantView;
    @ViewId(R.id.ui_archive_creator_rich_editor_author)
    private View authorView;
    @ViewId(R.id.ui_archive_creator_topic_layout)
    private View topicView;
    @ViewId(R.id.ui_holder_view_simple_inputable_topic)
    private CorneredEditText topicContent;
    @ViewId(R.id.ui_archive_creator_topic_layout)
    private View minuteView;
    @ViewId(R.id.ui_archive_creator_rich_editor_minute_title)
    private TextView minuteTitle;
    @ViewId(R.id.ui_holder_view_simple_inputable_minute)
    private CorneredEditText minuteContent;
    @ViewId(R.id.ui_archive_creator_additional_layout)
    private View additionalView;
    @ViewId(R.id.ui_archive_creator_additional_options)
    private RecyclerView additionalOptions;
    @ViewId(R.id.ui_archive_creator_rich_editor_template_images_layout)
    private View templateImages;
    @ViewId(R.id.ui_archive_creator_rich_editor_attachment_title)
    private TextView imageTitle;
    @ViewId(R.id.ui_archive_creator_rich_editor_template_images)
    private RecyclerView templateRecyclerView;
    private SimpleClickableViewHolder timeHolder;
    private SimpleInputableViewHolder addressHolder, participantHolder, authorHolder;

    private ImageAdapter imageAdapter;
    private OptionsAdapter optionsAdapter;
    private String[] templateItems;

    private void initializeTemplate() {
        if (null == imageAdapter) {
            if (mArchive.isActivity()) {
                templateItems[0] = templateItems[0].replace("时间", "报名截止于");
            }
            topicContent.setOnTouchListener(onTouchListener);
            minuteContent.setOnTouchListener(onTouchListener);
            // 模板档案的图片
            templateRecyclerView.setLayoutManager(new CustomGridLayoutManager(templateRecyclerView.getContext(), 4));
            templateRecyclerView.addItemDecoration(new SpacesItemDecoration());
            imageAdapter = new ImageAdapter();
            templateRecyclerView.setAdapter(imageAdapter);
            resetImages(waitingFroCompressImages, true);
        }
        if (null == timeHolder) {
            timeHolder = new SimpleClickableViewHolder(timeView, this);
            timeHolder.showContent(format(templateItems[0], mArchive.isActivity() ? "截止日期(必填)" : "选择时间(必填)"));
            timeHolder.addOnViewHolderClickListener(holderClickListener);
        }
        if (null == addressHolder) {
            addressHolder = new SimpleInputableViewHolder(addressView, this);
            addressHolder.showContent(format(templateItems[1], ""));
        }
        if (null == participantHolder) {
            participantHolder = new SimpleInputableViewHolder(participantView, this);
            participantHolder.showContent(format(templateItems[2], ""));
            participantHolder.setOnViewHolderElementClickListener(elementClickListener);
            //if (mArchive.isActivity()) {
            participantHolder.setEditable(false);
            //}
        }
        if (null == authorHolder) {
            authorHolder = new SimpleInputableViewHolder(authorView, this);
            authorHolder.showContent(format(templateItems[3], Cache.cache().userName));
        }
        if (mArchive.isActivity()) {
            // 去掉活动议题一栏(2018-11-28)
            topicView.setVisibility(View.GONE);
            // 增加附加选项
            additionalView.setVisibility(View.VISIBLE);
            if (null == optionsAdapter) {
                FlexboxLayoutManager manager = new FlexboxLayoutManager(additionalOptions.getContext(), FlexDirection.ROW, FlexWrap.WRAP);
                additionalOptions.setLayoutManager(manager);
                optionsAdapter = new OptionsAdapter();
                additionalOptions.setAdapter(optionsAdapter);
                ActivityOption option = new ActivityOption();
                option.setSelectable(true);
                option.setId("坐车");
                option.setAdditionalOptionName("坐车");
                optionsAdapter.add(option);
                option = new ActivityOption();
                option.setSelectable(true);
                option.setId("住宿");
                option.setAdditionalOptionName("住宿");
                optionsAdapter.add(option);
                option = new ActivityOption();
                option.setSelectable(true);
                option.setId("就餐");
                option.setAdditionalOptionName("就餐");
                optionsAdapter.add(option);
                //optionsAdapter.add(optionAdd);
            }
            //templateImages.setVisibility(View.GONE);
            titleView.setMaxLength(20);
            minuteTitle.setText(StringHelper.getString(R.string.ui_group_activity_editor_minute_title));
            imageTitle.setText(StringHelper.getString(R.string.ui_group_activity_editor_files_title));
        }
    }

    private ActivityOption optionAdd = new ActivityOption() {{
        setId("+");
        setAdditionalOptionName("+");
    }};

    private class OptionsAdapter extends RecyclerViewAdapter<LabelViewHolder, ActivityOption> {
        @Override
        public LabelViewHolder onCreateViewHolder(View itemView, int viewType) {
            LabelViewHolder holder = new LabelViewHolder(itemView, ArchiveEditorFragment.this);
            holder.setOnViewHolderElementClickListener(optionsClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_activity_label;
        }

        @Override
        public void onBindHolderOfView(LabelViewHolder holder, int position, @Nullable ActivityOption item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(ActivityOption item1, ActivityOption item2) {
            return 0;
        }
    }

    private OnViewHolderElementClickListener optionsClickListener = new OnViewHolderElementClickListener() {

        private int selectedIndex = -1;
        private EditableDialogHelper helper;

        @Override
        public void onClick(View view, int index) {
            ActivityOption option = optionsAdapter.get(index);
            switch (view.getId()) {
                case R.id.ui_holder_view_activity_label_container:
                    if (option.getAdditionalOptionName().equals("+")) {
                        selectedIndex = -1;
                        // 添加新的
                        showEditor(true);
                    } else {
                        option.setSelected(!option.isSelected());
                        optionsAdapter.update(option);
                    }
                    break;
                case R.id.ui_holder_view_activity_label_edit:
                    selectedIndex = index;
                    showEditor(false);
                    break;
            }
        }

        private void showEditor(boolean appendable) {
            ActivityOption option = selectedIndex >= 0 ? optionsAdapter.get(selectedIndex) : null;
            final String name = null != option ? option.getAdditionalOptionName() : "";
            final boolean selected = null != option && option.isSelected();
            if (null == helper) {
                helper = EditableDialogHelper.helper().init(ArchiveEditorFragment.this)
                        .setTitleText((appendable ? "添加" : "修改") + "附加选项")
                        .setInputHint(R.string.ui_group_activity_editor_additional_creator_dialog_title).setMaxInputableLength(5);
            }
            helper.setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
                @Override
                public boolean onConfirm() {
                    String inputValue = helper.getInputValue();
                    if (!isEmpty(inputValue) && inputValue.length() < 2) {
                        ToastHelper.make().showMsg(R.string.ui_group_activity_editor_additional_name_too_short);
                        return false;
                    }
                    if (!isEmpty(inputValue) && !inputValue.equals(name) && !inputValue.equals("+")) {
                        ActivityOption opt = new ActivityOption();
                        opt.setAdditionalOptionName(inputValue);
                        opt.setId(inputValue);
                        opt.setSelected(selected);
                        opt.setSelectable(true);
                        if (selectedIndex >= 0) {
                            optionsAdapter.replace(opt, selectedIndex);
                        } else if (optionsAdapter.exist(opt)) {
                            optionsAdapter.update(opt);
                        } else {
                            int index = optionsAdapter.indexOf(optionAdd);
                            if (index < 4) {
                                optionsAdapter.add(opt, index);
                            } else {
                                optionsAdapter.replace(opt, index);
                            }
                        }
                    }
                    helper.hideKeyboard();
                    selectedIndex = -1;
                    return true;
                }
            }).setInputValue(name).show();
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (view instanceof CorneredEditText) {
                if (view.canScrollVertically(-1) || view.canScrollVertically(0)) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
            }
            return false;
        }

        /**
         * EditText竖直方向能否够滚动
         * @param editText  须要推断的EditText
         * @return true：能够滚动   false：不能够滚动
         */
        @SuppressWarnings("unused")
        private boolean canVerticalScroll(CorneredEditText editText) {
            //滚动的距离
            int scrollY = editText.getScrollY();
            //控件内容的总高度
            int scrollRange = editText.getLayout().getHeight();
            //控件实际显示的高度
            int scrollExtent = editText.getHeight() - editText.getCompoundPaddingTop() - editText.getCompoundPaddingBottom();
            //控件内容总高度与实际显示高度的差值
            int scrollDifference = scrollRange - scrollExtent;

            if (scrollDifference == 0) {
                return false;
            }

            return (scrollY > 0) || (scrollY < scrollDifference - 1);
        }
    };

    private OnViewHolderClickListener holderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            openDateTimePicker();
        }
    };

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            String groupId = PreferenceHelper.get(Cache.get(R.string.pf_last_login_user_group_current, R.string.pf_last_login_user_group_current_beta), "");
            String groupName = PreferenceHelper.get(Cache.get(R.string.pf_last_login_user_group_current_name, R.string.pf_last_login_user_group_current_name_beta), "");
            if (mArchive.isActivity()) {
                GroupSubordinateSquadMemberPickerFragment.open(ArchiveEditorFragment.this, groupId, groupName, "", true,
                        mArchive.getGroupIdList(), mArchive.getGroSquMemberList());
                // 组织、下级组织、成员拾取器
                //GroupAllPickerFragment.open(ArchiveEditorFragment.this, mArchive.getGroupId(), "参与人",
                //        mArchive.getGroupIdList(), mArchive.getGroSquMemberList());
            } else {
                // 参与者
                isOpenOther = true;
                GroupSubordinateSquadMemberPickerFragment.open(ArchiveEditorFragment.this, groupId, groupName, "", false, null, null);
                //GroupAllPickerFragment.IS_FOR_DELIVER = true;
                //GroupAllPickerFragment.open(ArchiveEditorFragment.this, mArchive.getGroupId(), "参与人", null, null);
            }
        }
    };

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
        if (mArchive.getImage().size() > 0) {
            Iterator<Attachment> iterator = mArchive.getImage().iterator();
            while (iterator.hasNext()) {
                Attachment attachment = iterator.next();
                imageAdapter.remove(attachment.getUrl());
                iterator.remove();
            }
        }
        resetImages(waitingFroCompressImages, true);
    }

    private void resetAttachmentImages(ArrayList<Attachment> images) {
        imageAdapter.clear();
        for (Attachment attachment : images) {
            Model model = new Model();
            model.setId(attachment.getUrl());
            imageAdapter.add(model);
        }
        appendAttacher();
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
        appendAttacher();
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
            isOpenOther = true;
            // 需要重新再选择图片
            startGalleryForResult();
        }
    };

    // 照片预览点击
    private ImageDisplayer.OnImageClickListener imagePreviewClickListener = new ImageDisplayer.OnImageClickListener() {
        @Override
        public void onImageClick(ImageDisplayer displayer, String url) {
            isOpenOther = true;
            if (Utils.isUrl(url)) {
                ImageViewerFragment.isCollected = true;
                // 如果是以上传了的图片则调用imageViewer预览
                ImageViewerFragment.open(ArchiveEditorFragment.this, url);
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

    private void removeImageFromArchive(String url) {
        if (mArchive.getImage().size() > 0) {
            Iterator<Attachment> iterator = mArchive.getImage().iterator();
            while (iterator.hasNext()) {
                Attachment attachment = iterator.next();
                if (attachment.getUrl().equals(url)) {
                    iterator.remove();
                }
            }
        }
    }

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
                ImageViewHolder holder = new ImageViewHolder(itemView, ArchiveEditorFragment.this);
                holder.addOnDeleteClickListener(imageDeleteClickListener);
                holder.addOnImageClickListener(imagePreviewClickListener);
                // 这里是要尝试删除选择的文件
                //holder.addOnHandlerBoundDataListener(handlerBoundDataListener);
                holder.setImageSize(width, height);
                return holder;
            } else {
                return new AttacherItemViewHolder(itemView, ArchiveEditorFragment.this)
                        .setSize(width, height).setOnViewHolderClickListener(imagePickClickListener);
            }
        }

        @Override
        public int itemLayout(int viewType) {
            return viewType == VT_IMAGE ? R.layout.holder_view_image : R.layout.holder_view_attach_item;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof ImageViewHolder) {
                assert item != null;
                ((ImageViewHolder) holder).showContent(item.getId());
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
