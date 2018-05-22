package com.leadcom.android.isp.fragment.archive;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.gson.reflect.TypeToken;
import com.hlk.hlklib.layoutmanager.CustomLinearLayoutManager;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.archive.ArchiveQueryRequest;
import com.leadcom.android.isp.api.archive.ArchiveRequest;
import com.leadcom.android.isp.api.common.ShareRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.org.OrgRequest;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.etc.ImageCompress;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.activity.LabelPickFragment;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.fragment.common.CoverPickFragment;
import com.leadcom.android.isp.fragment.organization.GroupContactPickFragment;
import com.leadcom.android.isp.fragment.organization.GroupPickerFragment;
import com.leadcom.android.isp.fragment.organization.GroupsContactPickerFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.helper.popup.DateTimeHelper;
import com.leadcom.android.isp.helper.popup.DeleteDialogHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.helper.popup.DictionaryHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.attachment.AttacherItemViewHolder;
import com.leadcom.android.isp.holder.attachment.AttachmentViewHolder;
import com.leadcom.android.isp.holder.common.SimpleClickableViewHolder;
import com.leadcom.android.isp.holder.common.SimpleInputableViewHolder;
import com.leadcom.android.isp.holder.individual.ImageViewHolder;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.activity.Label;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.archive.ArchiveQuery;
import com.leadcom.android.isp.model.archive.Dictionary;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.common.Seclusion;
import com.leadcom.android.isp.model.common.ShareInfo;
import com.leadcom.android.isp.model.organization.Organization;
import com.leadcom.android.isp.model.organization.RelateGroup;
import com.leadcom.android.isp.model.organization.SubMember;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

    public static final String MULTIMEDIA = "multimedia";
    public static final String ATTACHABLE = "attachable";
    /**
     * 模板档案
     */
    public static final String TEMPLATE = "template";
    public static final String MOMENT = "moment";
    private static final String PARAM_UPLOAD_TYPE = "aecf_upload_type";
    private static final String PARAM_EDITOR_TYPE = "aecf_archive_editor_type";
    private static final String PARAM_PASTE_CONTENT = "aecf_paste_content";

    private static final int UP_NOTHING = 0;
    private static final int UP_IMAGE = 1;
    private static final int UP_MUSIC = 2;
    private static final int UP_VIDEO = 3;
    private static final int UP_ATTACH = 4;
    /**
     * 图文方式
     */
    private static final int TYPE_MULTIMEDIA = 1;
    /**
     * 附件方式
     */
    private static final int TYPE_ATTACHMENT = 2;
    /**
     * 模板档案
     */
    private static final int TYPE_TEMPLATE = 3;
    private static boolean editorFocused = false;

    public static ArchiveEditorFragment newInstance(Bundle bundle) {
        ArchiveEditorFragment aecf = new ArchiveEditorFragment();
        aecf.setArguments(bundle);
        return aecf;
    }

    private static int getType(String type) {
        switch (type) {
            case ATTACHABLE:
                return TYPE_ATTACHMENT;
            case MULTIMEDIA:
                return TYPE_MULTIMEDIA;
            default:
                return TYPE_TEMPLATE;
        }
    }

    private static Bundle getBundle(String remoteDraftId, String attachType) {
        Bundle bundle = new Bundle();
        // 传过来的档案id（草稿档案），需要从服务器上拉取草稿内容再编辑
        bundle.putString(PARAM_QUERY_ID, remoteDraftId);
        // 编辑器方式（附件方式、图文方式）
        bundle.putInt(PARAM_EDITOR_TYPE, getType(attachType));
        return bundle;
    }

    public static void open(BaseFragment fragment, String remoteDraftId, String attachType) {
        fragment.openActivity(ArchiveEditorFragment.class.getName(), getBundle(remoteDraftId, attachType), REQUEST_CREATE, true, true);
    }

    public static void open(Context context, String remoteDraftId, String attachType) {
        BaseActivity.openActivity(context, ArchiveEditorFragment.class.getName(), getBundle(remoteDraftId, attachType), REQUEST_CREATE, true, true);
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        uploadType = bundle.getInt(PARAM_UPLOAD_TYPE, UP_NOTHING);
        editorType = bundle.getInt(PARAM_EDITOR_TYPE, TYPE_MULTIMEDIA);
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
        //mArchive.setGroupId(mQueryId);
        // 默认为个人普通档案或组织普通档案
        //mArchive.setType(Archive.Type.USER);
        // 档案默认向所有人公开的
        mArchive.setAuthPublic(Seclusion.Type.Public);
        // 默认草稿作者为当前登录用户
        mArchive.setUserId(Cache.cache().userId);
        mArchive.setUserName(Cache.cache().userName);
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
        if (editorType == TYPE_ATTACHMENT || isEmpty(mArchive.getGroupId())) {
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
    private View multimediaView;
    @ViewId(R.id.ui_archive_creator_rich_editor_attachment)
    private View attachmentView;
    @ViewId(R.id.ui_archive_creator_rich_editor_template)
    private View templateView;
    @ViewId(R.id.ui_tool_swipe_refreshable_recycler_view)
    private RecyclerView attachmentRecyclerView;
    // 创建成功的档案信息
    private Archive mArchive;
    /**
     * 当前上传的文件类型：1=图片，2=音乐，3=视频
     */
    private int uploadType = UP_NOTHING;
    /**
     * 默认图文编辑方式
     */
    private int editorType = TYPE_MULTIMEDIA;
    /**
     * 是否是组织档案，默认个人档案
     */
    private boolean isGroupArchive = false, isUserArchive = true;
    private boolean isLongClickEditor = false;
    /**
     * 是否是粘贴了内容
     */
    private boolean isPasteContent = false;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        editorFocused = false;
        super.onActivityCreated(savedInstanceState);
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
        // 如果不是传入的服务器草稿id则判断本地是否有草稿
        if (isEmpty(mQueryId)) {
            // 图文模式下检索是否有未提交的草稿
            if (editorType == TYPE_MULTIMEDIA) {
                fetchingDraft();
            }
//            List<ArchiveDraft> drafts = ArchiveDraft.getDraft("");
//            if (null != drafts && drafts.size() > 0) {
//                warningDraftExist(drafts.get(0), drafts.size());
//            }
        } else {
            fetchingSingleDraft();
        }
        attachmentView.setVisibility(editorType == TYPE_ATTACHMENT ? View.VISIBLE : View.GONE);
        multimediaView.setVisibility(editorType == TYPE_MULTIMEDIA ? View.VISIBLE : View.GONE);
        templateView.setVisibility(editorType == TYPE_TEMPLATE ? View.VISIBLE : View.GONE);

        attachmentRecyclerView.setLayoutManager(new CustomLinearLayoutManager(attachmentRecyclerView.getContext()));
        aAdapter = new AttachmentAdapter();
        attachmentRecyclerView.setAdapter(aAdapter);
    }

    private void fetchingSingleDraft() {
        ArchiveQueryRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<ArchiveQuery>() {
            @Override
            public void onResponse(ArchiveQuery archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success && null != archive) {
                    mArchive = archive.getDocDraft();
                    isGroupArchive = !isEmpty(mArchive.getGroupId());
                    isUserArchive = !isGroupArchive;
                    titleView.setValue(mArchive.getTitle());
                    mArchive.resetImageStyle();
                    mEditor.setHtml(mArchive.getContent());
                } else {
                    ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_create_draft_not_exists);
                    finish();
                }
            }
        }).find(Archive.Type.DRAFT, mQueryId);
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

    private void warningSingleDraft(Archive draft) {
        final String json = Archive.toJson(draft);
        final String id = draft.getId();
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                // 继续编辑则直接停留在本页面
                mArchive = Archive.fromJson(json);
                isGroupArchive = !isEmpty(mArchive.getGroupId());
                isUserArchive = !isGroupArchive;
                titleView.setValue(mArchive.getTitle());
                mArchive.resetImageStyle();
                mEditor.setHtml(mArchive.getContent());
                return true;
            }
        }).setOnDialogCancelListener(new DialogHelper.OnDialogCancelListener() {
            @Override
            public void onCancel() {
                // 打开到草稿详情页查看评论，无论是个人档案草稿还是组织档案草稿都是组织档案
                ArchiveDetailsWebViewFragment.open(ArchiveEditorFragment.this, id, Archive.Type.GROUP);
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
    public int getLayout() {
        return R.layout.fragment_archive_creator_richeditor;
    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_text_document_create_fragment_title);
        setRightIcon(R.string.ui_icon_more);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                // 显示附加菜单信息
                openSettingDialog();
            }
        });
        if (editorType == TYPE_TEMPLATE) {
            initializeTemplate();
        }
        //resetRightIcons();
    }

    private void tryCreateArchive() {
        String title = titleView.getValue();
        if (isEmpty(title)) {
            ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_title_invalid);
            return;
        }
        mArchive.setTitle(title);
        if (isEmpty(mArchive.getContent())) {
            ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_content_invalid);
            return;
        }
        if (editorType == TYPE_MULTIMEDIA) {
            // 图文模式下需要检测
            if (isEmpty(mArchive.getCover())) {
                ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_create_cover_null);
                return;
            } else {
                String ext = Attachment.getExtension(mArchive.getCover());
                if (!ImageCompress.isImage(ext)) {
                    ToastHelper.make().showMsg(getString(R.string.ui_text_archive_creator_editor_create_cover_invalid, ext));
                }
            }
        }
        // 档案模板设置为图文或附件
        mArchive.setDocType(editorType == TYPE_MULTIMEDIA ? Archive.ArchiveType.MULTIMEDIA : Archive.ArchiveType.ATTACHMENT);
        if (isGroupArchive) {
            //mArchive.setType(Archive.Type.GROUP);
            if (isEmpty(mArchive.getGroupId())) {
                ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_create_group_null);
                return;
            }
        } else {
            //mArchive.setType(Archive.Type.USER);
            // 个人档案需要清空组织id
            mArchive.setGroupId("");
            //mArchive.setType(Archive.ArchiveType.INDIVIDUAL);
        }
        // 个人档案需要标签
        if (isEmpty(mArchive.getGroupId()) && mArchive.getLabel().size() < 1) {
            ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_create_label_null);
            return;
        }
        String author = creatorText.getValue();
        if (isEmpty(author)) {
            ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_create_author_null);
            return;
        }
        mArchive.setSource(author);
        mEditor.getMarkdown();
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
                if (isLongClickEditor && len > 200) {
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
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    // 提交成功，判断是否需要再提交个人档案
                    if (isGroupArchive) {
                        // 去掉组织档案需求
                        isGroupArchive = false;
                        if (isUserArchive) {
                            mArchive.setId("");
                            // 如果选择了还要存为个人档案，则还要再调用一次
                            tryCreateArchive();
                        } else {
                            mArchive = archive;
                            createSuccess();
                        }
                    } else {
                        //ArchiveDraft.delete(mArchive.getId());
                        mArchive = archive;
                        createSuccess();
                        //mArchive = archive;
                        //resetRightIcons();
                        //mEditor.setInputEnabled(false);
                        //openSettingDialog();
                    }
                }
            }
        }).addFormal(mArchive);
    }

    private void createSuccess() {
        if (null != mArchive && !isEmpty(mArchive.getId())) {
            ArchiveDetailsWebViewFragment.open(ArchiveEditorFragment.this, mArchive);
        }
        resultSucceededActivity();
    }

    @Override
    public void onStop() {
        if (editorType == TYPE_MULTIMEDIA) {
            saveDraft();
        }
        super.onStop();
    }

    private void saveDraft() {
        mArchive.setTitle(titleView.getValue());
        if (null != siteText) {
            mArchive.setSite(siteText.getValue());
            mArchive.setSource(creatorText.getValue());
            // 保存可能手动输入添加的参与人
            mArchive.setParticipant(participantText.getValue());
        }
        // 草稿标题可以为空、内容也可以为空，但两者不能同时为空
        if (!isEmpty(mArchive.getTitle()) || !isEmpty(mArchive.getContent())) {
            if (isPasteContent && mArchive.isContentPasteFromOtherPlatform()) {
                // 如果是粘贴过来的内容，则清理里面所有非树脉自有的img标签
                mArchive.clearPastedContentImages();
            }
            savingDraft();
        }
    }

    private void savingDraft() {
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    if (null != archive && !isEmpty(archive.getId())) {
                        mArchive = archive;
                    }
                    ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_create_draft_saved);
                }
            }
        }).addDraft(mArchive);
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    private View settingDialogView;
    private ImageDisplayer coverView;
    private TextView titleText, publicText, labelText, createTime, happenDate, propertyText, categoryText, groupNameText;
    private CustomTextView userIcon, groupIcon, publicIcon, privateIcon;
    private ClearEditText participantText, siteText;
    private ClearEditText creatorText;
    private View archiveTypeUser, shareDraftButton;

    private void openSettingDialog() {
        mArchive.setTitle(titleView.getValue());
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == settingDialogView) {
                    settingDialogView = View.inflate(Activity(), R.layout.popup_dialog_rich_editor_archive_setting, null);
                    titleText = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_title);
                    publicText = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_public_text);
                    labelText = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_label_text);
                    creatorText = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_creator);
                    createTime = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_create_time);
                    coverView = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_cover_image);
                    propertyText = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_property_text);
                    categoryText = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_category_text);
                    participantText = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_participant_text);
                    siteText = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_site_text);
                    happenDate = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_time_text);
                    userIcon = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_type_user_icon);
                    groupIcon = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_type_group_icon);
                    groupNameText = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_group_picker_text);
                    archiveTypeUser = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_type_user);
                    publicIcon = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_public_public_icon);
                    privateIcon = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_public_private_icon);
                    shareDraftButton = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_share_draft);
                    //archiveTypeGroup = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_type_group);
                    // 根据个人档案和组织档案显示某些元素
//                    if (isEmpty(mQueryId)) {
//                        settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_time).setVisibility(View.GONE);
//                        settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_site).setVisibility(View.GONE);
//                        settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_property).setVisibility(View.GONE);
//                        settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_category).setVisibility(View.GONE);
//                        settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_participant).setVisibility(View.GONE);
//                        settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_share).setVisibility(View.GONE);
//                        settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_label).setVisibility(View.VISIBLE);
//                        settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_createtime).setVisibility(View.VISIBLE);
//                    }
                }
                return settingDialogView;
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {
                if (isEmpty(mArchive.getTitle())) {
                    titleText.setText(Html.fromHtml(getString(R.string.ui_text_archive_creator_editor_title_empty)));
                } else {
                    titleText.setText(mArchive.getTitle());
                }
                String source = mArchive.getSource();
                creatorText.setValue(isEmpty(source) ? mArchive.getUserName() : source);
                creatorText.focusEnd();
                String text = mArchive.getCreateDate();
                if (!isEmpty(text) && !text.equals(Model.DFT_DATE)) {
                    text = formatDate(mArchive.getCreateDate(), "yyyy.MM.dd");
                } else {
                    text = Utils.formatDateOfNow("yyyy.MM.dd");
                }
                createTime.setText(text);
                coverView.displayImage(mArchive.getCover(), getDimension(R.dimen.ui_base_user_header_image_size_big), false, false);
                labelText.setText(Label.getLabelDesc(mArchive.getLabel()));
                Seclusion seclusion = new Seclusion();
                //seclusion.setGroupIds(mArchive.getAuthGro());
                //seclusion.setUserIds(mArchive.getAuthUser());
                //seclusion.setUserNames(mArchive.getAuthUserName());
                // 这一步一定要在最后设置，否则状态会被重置
                seclusion.setStatus(mArchive.getAuthPublic());
                publicText.setText(PrivacyFragment.getPrivacy(seclusion));

                siteText.setValue(mArchive.getSite());
                siteText.focusEnd();

                if (isEmpty(mArchive.getProperty())) {
                    propertyText.setText(R.string.ui_text_archive_details_editor_setting_property_title);
                } else {
                    propertyText.setText(mArchive.getProperty());
                }
                if (isEmpty(mArchive.getCategory())) {
                    categoryText.setText(R.string.ui_text_archive_details_editor_setting_category_title);
                } else {
                    categoryText.setText(mArchive.getCategory());
                }
                if (isEmpty(mArchive.getParticipant())) {
                    participantText.setValue("");
                } else {
                    participantText.setValue(mArchive.getParticipant());
                }
                participantText.focusEnd();
                if (isEmpty(mArchive.getHappenDate())) {
                    happenDate.setText(R.string.ui_text_archive_details_editor_setting_time_title);
                } else {
                    happenDate.setText(mArchive.getHappenDate().substring(0, 10));
                }

                if (editorType == TYPE_ATTACHMENT) {
                    isGroupArchive = true;
                    isUserArchive = false;
                }

                if (isEmpty(mArchive.getGroupId())) {
                    // 如果用户只有一个组织则直接填入组织id和名字
                    if (isGroupArchive && Cache.cache().getGroups().size() == 1) {
                        mArchive.setGroupId(Cache.cache().getGroups().get(0).getGroupId());
                        resetGroupInfo(mArchive.getGroupId());
                    } else {
                        groupNameText.setText(R.string.ui_text_archive_details_editor_setting_group_desc);
                    }
                } else {
                    resetGroupInfo(mArchive.getGroupId());
                }

                archiveTypeUser.setVisibility(editorType == TYPE_ATTACHMENT ? View.GONE : View.VISIBLE);
                if (editorType == TYPE_ATTACHMENT) {
                    shareDraftButton.setVisibility(View.GONE);
                }
                resetGroupArchiveOrUser();
            }

            private void resetGroupInfo(String groupId) {
                Organization group = Organization.get(groupId);
                if (null != group) {
                    groupNameText.setText(Html.fromHtml(group.getName()));
                    mArchive.setGroupName(group.getName());
                } else {
                    fetchingGroup(mArchive.getGroupId());
                }
            }

            private void fetchingGroup(String groupId) {
                OrgRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Organization>() {
                    @Override
                    public void onResponse(Organization organization, boolean success, String message) {
                        super.onResponse(organization, success, message);
                        if (success && null != organization) {
                            groupNameText.setText(Html.fromHtml(organization.getName()));
                            mArchive.setGroupName(organization.getName());
                        }
                    }
                }).find(groupId);
            }
        }).addOnEventHandlerListener(new DialogHelper.OnEventHandlerListener() {
            @Override
            public int[] clickEventHandleIds() {
                return new int[]{
                        R.id.ui_popup_rich_editor_setting_cover,
                        R.id.ui_popup_rich_editor_setting_type_user,
                        R.id.ui_popup_rich_editor_setting_type_group,
                        R.id.ui_popup_rich_editor_setting_group_picker,
                        R.id.ui_popup_rich_editor_setting_time,
                        R.id.ui_popup_rich_editor_setting_property,
                        R.id.ui_popup_rich_editor_setting_category,
                        R.id.ui_popup_rich_editor_setting_participant,
                        //R.id.ui_popup_rich_editor_setting_public,
                        R.id.ui_popup_rich_editor_setting_public_public,
                        R.id.ui_popup_rich_editor_setting_public_private,
                        R.id.ui_popup_rich_editor_setting_label,
                        R.id.ui_popup_rich_editor_setting_share,
                        R.id.ui_popup_rich_editor_setting_share_draft,
                        R.id.ui_popup_rich_editor_setting_commit};
            }

            @Override
            public boolean onClick(View view) {
                switch (view.getId()) {
                    case R.id.ui_popup_rich_editor_setting_cover:
                        if (uploadType != UP_NOTHING) {
                            ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_create_cover_notime);
                        } else {
                            // 选择封面，到封面拾取器
                            CoverPickFragment.open(ArchiveEditorFragment.this, false, mArchive.getCover(), 1, 1);
                        }
                        break;
                    case R.id.ui_popup_rich_editor_setting_type_user:
                        // 是否选中个人档案标签
                        isUserArchive = !isUserArchive;
                        if (!isUserArchive && !isGroupArchive) {
                            // 如果不是选择用户文档，且也不是组织文档，则默认选中组织文档
                            isGroupArchive = true;
                        }
                        //mArchive.setType(Archive.Type.USER);
                        resetGroupArchiveOrUser();
                        break;
                    case R.id.ui_popup_rich_editor_setting_type_group:
                        // 选择新建组织档案，只有图文模式下才可以组织、个人之间互相转换，附件模式下不可以
                        if (editorType == TYPE_MULTIMEDIA) {
                            isGroupArchive = !isGroupArchive;
                            if (!isGroupArchive && !isUserArchive) {
                                // 如果不是选择组织文档，且也不是个人文档，则默认选中个人文档
                                isUserArchive = true;
                            }
                            //mArchive.setType(Archive.Type.GROUP);
                            resetGroupArchiveOrUser();
                        }
                        break;
                    case R.id.ui_popup_rich_editor_setting_group_picker:
                        GroupPickerFragment.open(ArchiveEditorFragment.this, mArchive.getGroupId(), false);
                        break;
                    case R.id.ui_popup_rich_editor_setting_time:
                        // 发生时间
                        DateTimeHelper.helper().setOnDateTimePickListener(new DateTimeHelper.OnDateTimePickListener() {
                            @Override
                            public void onPicked(Date date) {
                                mArchive.setHappenDate(Utils.format(StringHelper.getString(R.string.ui_base_text_date_time_format), date));
                                happenDate.setText(mArchive.getHappenDate().substring(0, 10));
                            }
                        }).show(ArchiveEditorFragment.this, true, true, true, true, mArchive.getHappenDate());
                        break;
                    case R.id.ui_popup_rich_editor_setting_property:
                        // 档案性质
                        DictionaryHelper.helper(ArchiveEditorFragment.this).setOnDictionarySelectedListener(selectedListener).showDialog(Dictionary.Type.ARCHIVE_NATURE, mArchive.getProperty());
                        break;
                    case R.id.ui_popup_rich_editor_setting_category:
                        // 档案类型
                        DictionaryHelper.helper(ArchiveEditorFragment.this).setOnDictionarySelectedListener(selectedListener).showDialog(Dictionary.Type.ARCHIVE_TYPE, mArchive.getCategory());
                        break;
                    case R.id.ui_popup_rich_editor_setting_participant:
                        GroupsContactPickerFragment.open(ArchiveEditorFragment.this, mArchive.getGroupId());
                        break;
                    case R.id.ui_popup_rich_editor_setting_public:
                        openSecuritySetting();
                        break;
                    case R.id.ui_popup_rich_editor_setting_public_public:
                        mArchive.setAuthPublic(Seclusion.Type.Public);
                        resetPublicStatus();
                        break;
                    case R.id.ui_popup_rich_editor_setting_public_private:
                        mArchive.setAuthPublic(isEmpty(mArchive.getGroupId()) ? Seclusion.Type.Private : Seclusion.Type.Group);
                        resetPublicStatus();
                        break;
                    case R.id.ui_popup_rich_editor_setting_label:
                        openLabelPicker();
                        break;
                    case R.id.ui_popup_rich_editor_setting_share:
                    case R.id.ui_popup_rich_editor_setting_share_draft:
                        if (!isEmpty(mArchive.getGroupId())) {
                            // 组织档案才能分享草稿
                            mArchive.setTitle(titleView.getValue());
                            if (isEmpty(mArchive.getTitle())) {
                                ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_title_invalid);
                            } else if (isEmpty(mArchive.getContent())) {
                                ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_content_invalid);
                            } else {
                                GroupContactPickFragment.open(ArchiveEditorFragment.this, mArchive.getGroupId(), false, false, Model.EMPTY_ARRAY);
                            }
                            //getDraftShareInfo();
                        } else {
                            ToastHelper.make().showMsg(R.string.ui_text_archive_details_editor_setting_group_empty);
                        }
                        break;
                    case R.id.ui_popup_rich_editor_setting_commit:
                        if (isUserArchive && isGroupArchive) {
                            warningSaveUserAndGroupArchive();
                        } else {
                            tryCreateArchive();
                        }
                        break;
                }
                return false;
            }

            private void warningSaveUserAndGroupArchive() {
                DeleteDialogHelper.helper().init(ArchiveEditorFragment.this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
                    @Override
                    public boolean onConfirm() {
                        tryCreateArchive();
                        return true;
                    }
                }).setTitleText(R.string.ui_text_archive_creator_editor_create_both).setConfirmText(R.string.ui_base_text_confirm).show();
            }

            private void resetPublicStatus() {
                boolean isPublic = mArchive.getAuthPublic() == Seclusion.Type.Public;
                publicIcon.setTextColor(getColor(isPublic ? R.color.colorPrimary : R.color.textColorHintLight));
                privateIcon.setTextColor(getColor(!isPublic ? R.color.colorPrimary : R.color.textColorHintLight));
            }

            private DictionaryHelper.OnDictionarySelectedListener selectedListener = new DictionaryHelper.OnDictionarySelectedListener() {
                @Override
                public void onSelected(String selectedType, String selectedName) {
                    switch (selectedType) {
                        case Dictionary.Type.ARCHIVE_NATURE:
                            mArchive.setProperty(selectedName);
                            propertyText.setText(selectedName);
                            break;
                        case Dictionary.Type.ARCHIVE_TYPE:
                            mArchive.setCategory(selectedName);
                            categoryText.setText(selectedName);
                            break;
                    }
                }
            };

            private void openSecuritySetting() {
                Seclusion seclusion = PrivacyFragment.getSeclusion("");
                seclusion.setStatus(mArchive.getAuthPublic());
                //if (mArchive.getAuthPublic() == Seclusion.Type.Specify) {
                //    seclusion.setUserIds(mArchive.getAuthUser());
                //} else if (mArchive.getAuthPublic() == Seclusion.Type.Group) {
                //    seclusion.setGroupIds(mArchive.getAuthGro());
                //}
                String json = PrivacyFragment.getSeclusion(seclusion);
                // 隐私设置
                if (!isGroupArchive) {
                    // 个人隐私设置
                    PrivacyFragment.open(ArchiveEditorFragment.this, StringHelper.replaceJson(json, false), true);
                } else {
                    // 组织档案隐私设置
                    PrivacyFragment.open(ArchiveEditorFragment.this, StringHelper.replaceJson(json, false), false);
                }
            }

            private void openLabelPicker() {
                String json = Json.gson().toJson(mArchive.getLabel());
                String string = replaceJson(json, false);
                LabelPickFragment.open(ArchiveEditorFragment.this, "", "", LabelPickFragment.TYPE_ARCHIVE, string);
            }
        }).setAdjustScreenWidth(true).setPopupType(DialogHelper.SLID_IN_RIGHT).show();
    }

    /**
     * 获取草稿档案的分享内容
     */
    private void getDraftShareInfo() {
        if (null == mShareInfo) {
            ShareRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<ShareInfo>() {
                @Override
                public void onResponse(ShareInfo info, boolean success, String message) {
                    super.onResponse(info, success, message);
                    if (success && null != info) {
                        mShareInfo = info;
                        openShareDialog();
                    }
                }
            }).getDraftShareInfo(mArchive, isEmpty(mArchive.getGroupId()) ? 3 : 4);
        } else {
            openShareDialog();
        }
    }

    /**
     * 重置当前选择是组织档案还是个人档案
     */
    private void resetGroupArchiveOrUser() {
        userIcon.setTextColor(getColor(isUserArchive ? R.color.colorPrimary : R.color.textColorHintLight));
        groupIcon.setTextColor(getColor(isGroupArchive ? R.color.colorPrimary : R.color.textColorHintLight));
        int groupVisibility = isGroupArchive ? View.VISIBLE : View.GONE;
        // 组织档案需要选择组织
        settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_group_picker).setVisibility(groupVisibility);
        // 组织档案需要设置档案的性质
        settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_property).setVisibility(groupVisibility);
        // 组织档案需要设置档案的类型
        settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_category).setVisibility(groupVisibility);
        // 个人档案需要选择标签
        settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_label).setVisibility(isUserArchive ? View.VISIBLE : View.GONE);
        // 个人档案不需要分享草稿
        if (editorType != TYPE_ATTACHMENT) {
            settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_share_draft).setVisibility(isGroupArchive ? View.VISIBLE : View.GONE);
        }
    }

    // 插入图片的对话框
    private View imageDialogView;
    private ClearEditText imageAlt, imageUrl;

    private void openImageDialog() {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == imageDialogView) {
                    imageDialogView = View.inflate(Activity(), R.layout.popup_dialog_rich_editor_image, null);
                    imageAlt = imageDialogView.findViewById(R.id.ui_popup_rich_editor_image_alt);
                    imageUrl = imageDialogView.findViewById(R.id.ui_popup_rich_editor_image_url);
                }
                return imageDialogView;
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {

            }
        }).addOnEventHandlerListener(new DialogHelper.OnEventHandlerListener() {
            @Override
            public int[] clickEventHandleIds() {
                return new int[]{R.id.ui_popup_rich_editor_image_navigate};
            }

            @Override
            public boolean onClick(View view) {
                switch (view.getId()) {
                    case R.id.ui_popup_rich_editor_image_navigate:
                        openImageSelector(true);
                        // 图片选择时，不需要关闭对话框，返回之后还要上传
                        return false;
                }
                return true;
            }
        }).addOnDialogDismissListener(new DialogHelper.OnDialogDismissListener() {
            @Override
            public void onDismiss() {
                log("image dialog dismissed");
                mImageIcon.setTextColor(getColor(R.color.textColorHint));
            }
        }).addOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                String url = imageUrl.getValue();
                if (!isEmpty(url)) {
                    if (Utils.isUrl(url)) {
                        // 如果是粘贴的网络地址，则直接插入图片
                        insertImage(url, imageAlt.getValue());
                    } else {
                        // 压缩并上传图片，然后插入
                        uploadType = UP_IMAGE;
                        showUploading(true);
                        compressImage();
                    }
                } else {
                    ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_image_select_url_error);
                }
                return true;
            }
        }).setPopupType(DialogHelper.FADE).show();
    }

    private void insertImage(String url, String alt) {
        mEditor.insertImage(url, alt);
        if (null != imageUrl) {
            imageUrl.setValue("");
            imageAlt.setValue("");
        }
    }

    private OnImageSelectedListener imageSelectedListener = new OnImageSelectedListener() {
        @Override
        public void onImageSelected(ArrayList<String> selected) {
            if (editorType == TYPE_TEMPLATE) {
                resetImages(selected);
            } else {
                if (null != selected && selected.size() > 0) {
                    imageUrl.setValue(selected.get(0));
                } else {
                    ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_image_selected_nothing);
                }
            }
        }
    };

    private void showUploading(boolean shown) {
        updatingIndicator.setVisibility(shown ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onUploadingFailed() {
        showUploading(false);
        ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_attachment_uploading_failed);
    }

    private OnFileUploadingListener uploadingListener = new OnFileUploadingListener() {
        @Override
        public void onUploading(int all, int current, String file, long size, long uploaded) {

        }

        @Override
        public void onUploadingComplete(ArrayList<Attachment> uploaded) {
            showUploading(false);
            switch (uploadType) {
                case UP_IMAGE:
                    // 图片上传完毕，插入图片
                    String url = uploaded.get(0).getUrl();
                    // 如果上传完毕的是图片，则插入图片
                    if (ImageCompress.isImage(Attachment.getExtension(url))) {
                        insertImage(url, imageAlt.getValue());
                        mArchive.addFileId(uploaded.get(0).getId());
                        //mArchive.getImage().add(uploaded.get(0));
                    }
                    break;
                case UP_MUSIC:
                    // 音频上传完毕，插入音频到编辑框中
                    String music = uploaded.get(0).getUrl();
                    mEditor.insertAudio(music);
                    musicUrl.setValue("");
                    musicSize.setVisibility(View.GONE);
                    mArchive.addFileId(uploaded.get(0).getId());
                    //mArchive.getAttach().add(uploaded.get(0));
                    break;
                case UP_VIDEO:
                    // 视频上传完毕，插入视频到编辑框中
                    String video = uploaded.get(0).getUrl();
                    mEditor.insertVideo("", video);
                    videoUrl.setValue("");
                    videoSize.setVisibility(View.GONE);
                    mArchive.addFileId(uploaded.get(0).getId());
                    //mArchive.getVideo().add(uploaded.get(0));
                    break;
                case UP_ATTACH:
                    // 上传了多个附件（一次最多9个）
                    //mEditor.insertHtml("");
                    if (null != uploaded) {
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
                            mArchive.addFileId(uploaded.get(0).getId());
                        }
                        mAdapter.clear();
                    }
                    break;
            }
            // 上传完毕，设置上传方式为nothing
            uploadType = UP_NOTHING;
            // 插入完毕之后清空已上传的文件列表
            getUploadedFiles().clear();
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

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        switch (requestCode) {
            case REQUEST_COVER:
                mArchive.setCover(getResultedData(data));
                if (null != coverView) {
                    Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            coverView.displayImage(mArchive.getCover(), getDimension(R.dimen.ui_base_user_header_image_size_big), false, false);
                        }
                    }, 2000);
                }
                updateArchive(ArchiveRequest.TYPE_COVER);
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
            case REQUEST_SECURITY:
                String json = getResultedData(data);
                Seclusion seclusion = PrivacyFragment.getSeclusion(json);
                mArchive.setAuthPublic(seclusion.getStatus());
                //mArchive.setAuthGro(seclusion.getGroupIds());
                //mArchive.setAuthUser(seclusion.getUserIds());
                //mArchive.setAuthUserName(seclusion.getUserNames());
                if (null != publicText) {
                    publicText.setText(PrivacyFragment.getPrivacy(seclusion));
                }
                updateArchive(ArchiveRequest.TYPE_AUTH);
                break;
            case REQUEST_LABEL:
                String labelJson = getResultedData(data);
                ArrayList<String> list = Json.gson().fromJson(labelJson, new TypeToken<ArrayList<String>>() {
                }.getType());
                if (null != list) {
                    mArchive.getLabel().clear();
                    mArchive.getLabel().addAll(list);
                }
                if (null != labelText) {
                    labelText.setText(Label.getLabelDesc(mArchive.getLabel()));
                }
                updateArchive(ArchiveRequest.TYPE_LABEL);
                break;
            case REQUEST_ATTACHMENT:

                break;
            case REQUEST_DRAFT:
                // 草稿选择完毕
                String draftJson = getResultedData(data);
                mArchive = Archive.fromJson(StringHelper.replaceJson(draftJson, true));
                isGroupArchive = !isEmpty(mArchive.getGroupId());
                titleView.setValue(mArchive.getTitle());
                mArchive.resetImageStyle();
                mEditor.setHtml(mArchive.getContent());
                break;
            case REQUEST_MEMBER:
                ArrayList<SubMember> members = SubMember.fromJson(getResultedData(data));
                warningShareDraftTo(SubMember.getMemberNames(members), SubMember.getUserIds(members));
//                String names = "";
//                if (null != members && members.size() > 0) {
//                    for (SubMember member : members) {
//                        names += (isEmpty(names) ? "" : "、") + member.getUserName();
//                    }
//                }
                //mArchive.setParticipant(names);
                //participantText.setValue(names);
                //getDraftShareInfo();
//                if (!isEmpty(names)) {
//                    participantText.focusEnd();
//                }
                break;
            case REQUEST_GROUP:
                ArrayList<RelateGroup> groups = RelateGroup.from(getResultedData(data));
                if (null != groups && groups.size() > 0) {
                    // 设置组织id
                    mArchive.setGroupId(groups.get(0).getGroupId());
                    // 设置类别为组织档案
                    //mArchive.setType(Archive.Type.GROUP);
                    if (null != groupNameText) {
                        groupNameText.setText(Html.fromHtml(groups.get(0).getGroupName()));
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, data);
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
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    ToastHelper.make().showMsg(R.string.ui_text_archive_details_editor_setting_share_draft);
                    finish();
                    ArchiveDetailsWebViewFragment.openDraft(ArchiveEditorFragment.this, mArchive.getId(), Archive.Type.DRAFT);
                }
            }
        }).shareDraft(mArchive.getId(), userIds);
    }

    private void updateArchive(int type) {
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                ToastHelper.make().showMsg(message);
            }
        }).update(mArchive, type);
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
            startActivityForResult(mIntent, request);
        } catch (ActivityNotFoundException e) {
            ToastHelper.make().showMsg(com.netease.nim.uikit.R.string.gallery_invalid);
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

    private View attachmentDialogView;
    private RecyclerView recyclerView;
    private TextView attachmentDesc;
    private FileAdapter mAdapter;
    private AttachmentAdapter aAdapter;

    private void openAttachmentDialog() {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == attachmentDialogView) {
                    attachmentDialogView = View.inflate(Activity(), R.layout.popup_dialog_rich_editor_attachment, null);
                    attachmentDesc = attachmentDialogView.findViewById(R.id.ui_popup_rich_editor_attachment_description);
                    recyclerView = attachmentDialogView.findViewById(R.id.ui_tool_swipe_refreshable_recycler_view);
                    recyclerView.setLayoutManager(new CustomLinearLayoutManager(recyclerView.getContext()));
                    mAdapter = new FileAdapter();
                    recyclerView.setAdapter(mAdapter);
                }
                return attachmentDialogView;
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {
                attachmentDesc.setVisibility(mAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
            }
        }).addOnEventHandlerListener(new DialogHelper.OnEventHandlerListener() {
            @Override
            public int[] clickEventHandleIds() {
                return new int[]{R.id.ui_popup_rich_editor_attachment_navigate};
            }

            @Override
            public boolean onClick(View view) {
                switch (view.getId()) {
                    case R.id.ui_popup_rich_editor_attachment_navigate:
                        // 浏览本地文件
                        openFilePickDialog();
                        //chooseVideoFromLocalBeforeKitKat(REQUEST_ATTACHMENT);
                        return false;
                }
                return true;
            }
        }).addOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                uploadType = UP_ATTACH;
                showUploading(true);
                uploadFiles();
                return true;
            }
        }).addOnDialogDismissListener(new DialogHelper.OnDialogDismissListener() {
            @Override
            public void onDismiss() {
                log("attachment dialog dismissed");
                mAttachmentIcon.setTextColor(getColor(R.color.textColorHint));
            }
        }).setConfirmText(R.string.ui_text_archive_creator_editor_attachment_dialog_confirm_text).setPopupType(DialogHelper.FADE).show();
    }

    // 文件选择
    private FilePickerDialog filePickerDialog;

    private void openFilePickDialog() {
        if (null == filePickerDialog) {
            DialogProperties properties = new DialogProperties();
            // 选择文件
            properties.selection_type = DialogConfigs.FILE_SELECT;
            // 可以多选
            properties.selection_mode = DialogConfigs.MULTI_MODE;
            // 最多可选文件数量
            properties.maximum_count = 9;
            // 文件扩展名过滤
            //properties.extensions = StringHelper.getStringArray(R.array.ui_base_file_pick_types);
            filePickerDialog = new FilePickerDialog(Activity(), properties);
            filePickerDialog.setTitle(StringHelper.getString(R.string.ui_text_document_picker_title));
            filePickerDialog.setPositiveBtnName(StringHelper.getString(R.string.ui_base_text_confirm));
            filePickerDialog.setNegativeBtnName(StringHelper.getString(R.string.ui_base_text_cancel));
            filePickerDialog.setDialogSelectionListener(dialogSelectionListener);
        }
        resetSelectedFiles();
        filePickerDialog.show();
    }

    private DialogSelectionListener dialogSelectionListener = new DialogSelectionListener() {
        @Override
        public void onSelectedFilePaths(String[] strings) {
            attachmentDesc.setVisibility((null == strings || strings.length < 1) ? View.VISIBLE : View.GONE);
            // 更新待上传文件列表
            getWaitingForUploadFiles().clear();
            if (null != strings) {
                getWaitingForUploadFiles().addAll(Arrays.asList(strings));
                for (String string : getWaitingForUploadFiles()) {
                    Attachment attachment = new Attachment(string);
                    mAdapter.update(attachment);
                }
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

    private OnViewHolderClickListener selectedAttachmentViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Attachment attachment = mAdapter.get(index);
            mArchive.getAttach().remove(attachment);
            mAdapter.remove(attachment);
        }
    };

    private OnViewHolderClickListener uploadedAttachmentViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Attachment attachment = aAdapter.get(index);
            mArchive.getAttach().remove(attachment);
            mArchive.getImage().remove(attachment);
            mArchive.getOffice().remove(attachment);
            mArchive.getVideo().remove(attachment);
            aAdapter.remove(attachment);
        }
    };

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    private class FileAdapter extends RecyclerViewAdapter<AttachmentViewHolder, Attachment> {
        @Override
        public AttachmentViewHolder onCreateViewHolder(View itemView, int viewType) {
            AttachmentViewHolder holder = new AttachmentViewHolder(itemView, ArchiveEditorFragment.this);
            holder.addOnViewHolderClickListener(selectedAttachmentViewHolderClickListener);
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
            // 一下为字体格式设置
            R.id.ui_archive_creator_action_bold, R.id.ui_archive_creator_action_italic,
            R.id.ui_archive_creator_action_underline, R.id.ui_archive_creator_action_strike_through,
            R.id.ui_archive_creator_action_superscript, R.id.ui_archive_creator_action_subscript,
            R.id.ui_archive_creator_action_align_left, R.id.ui_archive_creator_action_align_center,
            R.id.ui_archive_creator_action_align_right,
            R.id.ui_archive_creator_action_heading1, R.id.ui_archive_creator_action_heading2,
            R.id.ui_archive_creator_action_heading3, R.id.ui_archive_creator_action_heading4,
            R.id.ui_archive_creator_action_heading5, R.id.ui_archive_creator_action_heading6})
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
                openImageDialog();
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
                openAttachmentDialog();
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
    @ViewId(R.id.ui_holder_view_simple_inputable_topic)
    private ClearEditText topicContent;
    @ViewId(R.id.ui_holder_view_simple_inputable_minute)
    private ClearEditText minuteContent;
    @ViewId(R.id.ui_archive_creator_rich_editor_template_images)
    private RecyclerView templateRecyclerView;
    private SimpleClickableViewHolder timeHolder;
    private SimpleInputableViewHolder addressHolder, participantHolder, authorHolder;

    private ImageAdapter imageAdapter;
    private String[] templateItems;

    private void initializeTemplate() {
        if (null == templateItems) {
            templateItems = StringHelper.getStringArray(R.array.ui_text_archive_creator_editor_template_values);
        }
        if (null == imageAdapter) {
            // 模板档案的图片
            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(templateRecyclerView.getContext());
            layoutManager.setFlexDirection(FlexDirection.ROW);
            layoutManager.setFlexWrap(FlexWrap.WRAP);
            templateRecyclerView.setLayoutManager(layoutManager);
            imageAdapter = new ImageAdapter();
            templateRecyclerView.setAdapter(imageAdapter);
            resetImages(waitingFroCompressImages);
        }
        if (null == timeHolder) {
            timeHolder = new SimpleClickableViewHolder(timeView, this);
            timeHolder.showContent(format(templateItems[0], ""));
        }
        if (null == addressHolder) {
            addressHolder = new SimpleInputableViewHolder(addressView, this);
            addressHolder.showContent(format(templateItems[1], ""));
        }
    }

    private void resetImages(ArrayList<String> images) {
        imageAdapter.clear();
        for (String string : images) {
            Model model = new Model();
            model.setId(string);
            imageAdapter.add(model);
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
            // 需要重新再选择图片
            startGalleryForResult();
        }
    };

    private class ImageAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {
        private static final int VT_IMAGE = 0, VT_ATTACH = 1;

        private int width, height;

        private void gotSize() {
            if (width == 0) {
                int _width = getScreenWidth();
                int padding = getDimension(R.dimen.ui_base_dimen_margin_padding) * 5;
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
                //holder.addOnDeleteClickListener(imageDeleteClickListener);
                //holder.addOnImageClickListener(imagePreviewClickListener);
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
                ((ImageViewHolder) holder).showContent(item.getId());
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
