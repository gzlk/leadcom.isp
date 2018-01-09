package com.leadcom.android.isp.fragment.archive;

import android.content.ActivityNotFoundException;
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
import com.google.gson.reflect.TypeToken;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.archive.ArchiveRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.etc.ImageCompress;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.activity.CoverPickFragment;
import com.leadcom.android.isp.fragment.activity.LabelPickFragment;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.DialogHelper;
import com.leadcom.android.isp.helper.SimpleDialogHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.attachment.AttachmentViewHolder;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.activity.Label;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.archive.ArchiveDraft;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.common.Seclusion;
import com.leadcom.android.isp.model.organization.Organization;
import com.hlk.hlklib.layoutmanager.CustomLinearLayoutManager;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;
import com.hlk.hlklib.lib.view.CustomTextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
    private static final String PARAM_UPLOAD_TYPE = "aecf_upload_type";
    private static final String PARAM_ARCHIVE = "aecf_archive_content";
    private static final String PARAM_EDITOR_TYPE = "aecf_archive_editor_type";

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
    private static boolean editorFocused = false;

    public static ArchiveEditorFragment newInstance(String params) {
        ArchiveEditorFragment aecf = new ArchiveEditorFragment();
        Bundle bundle = new Bundle();
        String[] strings = splitParameters(params);
        // 传过来的组织id，也即要创建的档案所属的组织，为empty时创建的是个人档案
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        // 编辑器方式（附件方式、图文方式）
        bundle.putInt(PARAM_EDITOR_TYPE, Integer.valueOf(strings[1]));
        aecf.setArguments(bundle);
        return aecf;
    }

    public static void open(BaseFragment fragment, String groupId, String attachType) {
        String params = format("%s,%d", groupId, attachType.equals(ATTACHABLE) ? TYPE_ATTACHMENT : TYPE_MULTIMEDIA);
        fragment.openActivity(ArchiveEditorFragment.class.getName(), params, REQUEST_CREATE, true, true);
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        uploadType = bundle.getInt(PARAM_UPLOAD_TYPE, UP_NOTHING);
        editorType = bundle.getInt(PARAM_EDITOR_TYPE, TYPE_MULTIMEDIA);
        String json = bundle.getString(PARAM_ARCHIVE, "");
        if (!isEmpty(json)) {
            mArchive = Archive.fromJson(json);
        } else {
            createNewDraftArchive();
        }
    }

    // 新建一个草稿档案
    private void createNewDraftArchive() {
        mArchive = new Archive();
        // 新建的档案都为临时档案
        mArchive.setId(Archive.getDraftId());
        // 标记是否为组织档案
        mArchive.setGroupId(mQueryId);
        // 设置来源为自己
        mArchive.setUserId(Cache.cache().userId);
        mArchive.setUserName(Cache.cache().userName);
        // 默认为个人普通档案或组织普通档案
        mArchive.setType(Archive.ArchiveType.NORMAL);
        // 档案默认向所有人公开的
        mArchive.setAuthPublic(Seclusion.Type.Public);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_UPLOAD_TYPE, uploadType);
        bundle.putInt(PARAM_EDITOR_TYPE, editorType);
        bundle.putString(PARAM_ARCHIVE, Json.gson().toJson(mArchive, new TypeToken<Archive>() {
        }.getType()));
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
        // 检索是否有未提交的草稿
        List<ArchiveDraft> drafts = ArchiveDraft.getDraft(mQueryId);
        if (null != drafts && drafts.size() > 0) {
            warningDraftExist(drafts.get(0), drafts.size());
        }
        attachmentView.setVisibility(editorType == TYPE_ATTACHMENT ? View.VISIBLE : View.GONE);
        multimediaView.setVisibility(editorType == TYPE_MULTIMEDIA ? View.VISIBLE : View.GONE);

        attachmentRecyclerView.setLayoutManager(new CustomLinearLayoutManager(attachmentRecyclerView.getContext()));
        aAdapter = new AttachmentAdapter();
        attachmentRecyclerView.setAdapter(aAdapter);
    }

    private void warningDraftExist(final ArchiveDraft draft, final int size) {
        int text = size > 1 ? R.string.ui_text_archive_creator_editor_create_draft_more : R.string.ui_text_archive_creator_editor_create_draft_1;
        SimpleDialogHelper.init(Activity()).show(text, size > 1 ? R.string.ui_base_text_have_a_look : R.string.ui_base_text_edit, R.string.ui_text_archive_creator_editor_create_new, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                if (size > 1) {
                    // 打开草稿选择页面
                    ArchiveDraftFragment.open(ArchiveEditorFragment.this, mQueryId);
                } else {
                    String json = draft.getArchiveJson();
                    mArchive = Archive.fromJson(json);
                    titleView.setValue(mArchive.getTitle());
                    mEditor.setHtml(mArchive.getContent());
                }
                return true;
            }
        }, new DialogHelper.OnDialogCancelListener() {
            @Override
            public void onCancel() {
                // 创建新档案
                createNewDraftArchive();
            }
        });
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
        if (mArchive.getLabel().size() < 1) {
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

    private RichEditor.OnTextChangeListener textChangeListener = new RichEditor.OnTextChangeListener() {
        @Override
        public void onTextChange(String s) {
            String text = s.replace("\\u003C", "<");
            // 文本为空或者首字符是 < 时，说明一般为 HTML 字符串
            if (isEmpty(text) || text.contains("html:")) {
                String html = text.replace("html:", "");
                mArchive.setContent(html);
                log("HTML: " + text);
            } else if (text.contains("mark:")) {
                text = Utils.clearContentHtml(text);
                mArchive.setMarkdown(text.replace("mark:", ""));
                log("MARK: " + text);
                mArchive.setContent(Utils.clearContentHtml(mArchive.getContent()));
                createArchive();
            }
        }
    };

    private void createArchive() {
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    // 提交成功，删除草稿
                    ArchiveDraft.delete(mArchive.getId());
                    mArchive = archive;
                    resultSucceededActivity();
                    //mArchive = archive;
                    //resetRightIcons();
                    //mEditor.setInputEnabled(false);
                    //openSettingDialog();
                }
            }
        }).add(mArchive);
    }

    /**
     * 当前档案是否为草稿档案
     */
    private boolean isDraft() {
        return isEmpty(mArchive.getId()) || mArchive.getId().contains("draft_");
    }

    @Override
    public void onStop() {
        saveDraft();
        super.onStop();
    }

    private void saveDraft() {
        mArchive.setTitle(titleView.getValue());
        // 草稿标题可以为空、内容也可以为空，但两者不能同时为空
        if (isDraft() && (!isEmpty(mArchive.getTitle()) || !isEmpty(mArchive.getContent()))) {
            ArchiveDraft draft = new ArchiveDraft();
            draft.setId(mArchive.getId());
            draft.setTitle(mArchive.getTitle());
            draft.setGroupId(mArchive.getGroupId());
            if (!isEmpty(mArchive.getGroupId()) && isEmpty(draft.getGroupName())) {
                Organization group = Organization.get(mArchive.getId());
                if (null != group) {
                    draft.setGroupName(group.getName());
                }
            }
            draft.setArchiveJson(Archive.toJson(mArchive));
            draft.setCreateDate(Utils.formatDateOfNow(StringHelper.getString(R.string.ui_base_text_date_time_format)));
            ArchiveDraft.save(draft);
            ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_create_draft_saved);
        }
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
    private TextView titleText, publicText, labelText, createTime;
    private ClearEditText creatorText;

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
                creatorText.setValue(mArchive.getUserName());
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
                seclusion.setGroupIds(mArchive.getAuthGro());
                seclusion.setUserIds(mArchive.getAuthUser());
                seclusion.setUserNames(mArchive.getAuthUserName());
                // 这一步一定要在最后设置，否则状态会被重置
                seclusion.setStatus(mArchive.getAuthPublic());
                publicText.setText(PrivacyFragment.getPrivacy(seclusion));
            }
        }).addOnEventHandlerListener(new DialogHelper.OnEventHandlerListener() {
            @Override
            public int[] clickEventHandleIds() {
                return new int[]{
                        R.id.ui_popup_rich_editor_setting_public,
                        R.id.ui_popup_rich_editor_setting_label,
                        R.id.ui_popup_rich_editor_setting_cover,
                        R.id.ui_popup_rich_editor_setting_commit};
            }

            @Override
            public boolean onClick(View view) {
                switch (view.getId()) {
                    case R.id.ui_popup_rich_editor_setting_public:
                        openSecuritySetting();
                        break;
                    case R.id.ui_popup_rich_editor_setting_label:
                        openLabelPicker();
                        break;
                    case R.id.ui_popup_rich_editor_setting_cover:
                        if (uploadType != UP_NOTHING) {
                            ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_create_cover_notime);
                        } else {
                            // 选择封面，到封面拾取器
                            CoverPickFragment.open(ArchiveEditorFragment.this, false, mArchive.getCover(), 1, 1);
                        }
                        break;
                    case R.id.ui_popup_rich_editor_setting_commit:
                        tryCreateArchive();
                        break;
                }
                return false;
            }

            private void openSecuritySetting() {
                Seclusion seclusion = PrivacyFragment.getSeclusion("");
                seclusion.setStatus(mArchive.getAuthPublic());
                if (mArchive.getAuthPublic() == Seclusion.Type.Specify) {
                    seclusion.setUserIds(mArchive.getAuthUser());
                } else if (mArchive.getAuthPublic() == Seclusion.Type.Group) {
                    seclusion.setGroupIds(mArchive.getAuthGro());
                }
                String json = PrivacyFragment.getSeclusion(seclusion);
                // 隐私设置
                if (isEmpty(mQueryId)) {
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
                LabelPickFragment.open(ArchiveEditorFragment.this, mQueryId, "", LabelPickFragment.TYPE_ARCHIVE, string);
            }
        }).setAdjustScreenWidth(true).setPopupType(DialogHelper.SLID_IN_RIGHT).show();
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
                return new int[]{R.id.ui_dialog_button_closer, R.id.ui_popup_rich_editor_image_navigate};
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
            if (null != selected && selected.size() > 0) {
                imageUrl.setValue(selected.get(0));
            } else {
                ToastHelper.make().showMsg(R.string.ui_text_archive_creator_editor_image_selected_nothing);
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
                return new int[]{R.id.ui_dialog_button_closer, R.id.ui_popup_rich_editor_music_navigate};
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
                return new int[]{R.id.ui_dialog_button_closer, R.id.ui_popup_rich_editor_video_navigate};
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
                if (!isDraft()) {
                    updateArchive(ArchiveRequest.TYPE_COVER);
                }
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
                mArchive.setAuthGro(seclusion.getGroupIds());
                mArchive.setAuthUser(seclusion.getUserIds());
                mArchive.setAuthUserName(seclusion.getUserNames());
                if (null != publicText) {
                    publicText.setText(PrivacyFragment.getPrivacy(seclusion));
                }
                if (!isDraft()) {
                    updateArchive(ArchiveRequest.TYPE_AUTH);
                }
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
                if (!isDraft()) {
                    updateArchive(ArchiveRequest.TYPE_LABEL);
                }
                break;
            case REQUEST_ATTACHMENT:

                break;
            case REQUEST_DRAFT:
                // 草稿选择完毕
                String draftJson = getResultedData(data);
                mArchive = Archive.fromJson(StringHelper.replaceJson(draftJson, true));
                titleView.setValue(mArchive.getTitle());
                mEditor.setHtml(mArchive.getContent());
                break;
        }
        super.onActivityResult(requestCode, data);
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
        }).addOnEventHandlerListener(new DialogHelper.OnEventHandlerListener() {
            @Override
            public int[] clickEventHandleIds() {
                return new int[]{R.id.ui_dialog_button_closer};
            }

            @Override
            public boolean onClick(View view) {
                return true;
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
                return new int[]{R.id.ui_dialog_button_closer, R.id.ui_popup_rich_editor_attachment_navigate};
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
}