package com.gzlk.android.isp.fragment.individual;

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
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.SimpleDialogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.AttachmentViewHolder;
import com.gzlk.android.isp.holder.SimpleClickableViewHolder;
import com.gzlk.android.isp.holder.SimpleInputableViewHolder;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.archive.Archive;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;

import org.json.JSONObject;

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

public class ArchiveNewFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_TYPE = "dnf_type";
    private static final String PARAM_GROUP = "dnf_group";
    private static final String PARAM_PRIVACY = "dnf_privacy";

    private static final String PARAM_TITLE = "dnf_title";
    private static final String PARAM_SOURCE = "dnf_source";

    public static ArchiveNewFragment newInstance(String params) {
        ArchiveNewFragment dnf = new ArchiveNewFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        bundle.putInt(PARAM_TYPE, Integer.valueOf(strings[0]));
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
        privacy = bundle.getString(PARAM_PRIVACY, "{}");
        title = bundle.getString(PARAM_TITLE, "");
        source = bundle.getString(PARAM_SOURCE, "");
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
    }

    // UI
    @ViewId(R.id.ui_document_new_title)
    private View titleInputView;
    @ViewId(R.id.ui_document_new_source)
    private View sourceView;
    @ViewId(R.id.ui_document_new_time)
    private View timeView;
    @ViewId(R.id.ui_document_new_security)
    private View securityView;
    @ViewId(R.id.ui_document_new_content)
    private ClearEditText contentView;
    @ViewId(R.id.ui_document_new_attachments_layout)
    private LinearLayout attachmentLayout;

    // holder
    private SimpleInputableViewHolder titleHolder;
    private SimpleInputableViewHolder sourceHolder;
    private SimpleClickableViewHolder timeHolder;
    private SimpleClickableViewHolder securityHolder;

    // data
    private String[] strings;
    private FileAdapter mAdapter;
    private int archiveType;
    private String archiveGroup;
    private String privacy, title, source;
    // 文件选择
    private FilePickerDialog filePickerDialog;
    DialogProperties properties;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == PrivacyFragment.REQUEST_SECURITY) {
            // 隐私设置返回了
            privacy = getResultedData(data);
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
                //ToastHelper.make().showMsg("暂时无法上传附件");
                tryCreateDocument();
            }
        });
        loadingArchive();
    }

    private void tryCreateDocument() {
        String title = titleHolder.getValue();
        if (StringHelper.isEmpty(title)) {
            ToastHelper.make().showMsg(R.string.ui_text_document_create_title_invalid);
            return;
        }
        String content = StringHelper.escapeToHtml(contentView.getValue());
        Utils.hidingInputBoard(titleInputView);
        if (StringHelper.isEmpty(mQueryId)) {
            createDocument(title, content);
        } else {
            editDocument(title, content);
        }
    }

    private void createDocument(String title, String content) {
        if (archiveType == Archive.Type.USER) {
            createUserArchive(title, content);
        } else {
            createOrganizationArchive(title, content);
        }
    }

    private void createUserArchive(String title, String content) {
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    ToastHelper.make().showMsg(message);
                    finish();
                }
            }
        }).add(Archive.ArchiveContentType.TEXT, title, content, null, null, null, null);
    }

    private void createOrganizationArchive(String title, String content) {
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    ToastHelper.make().showMsg(message);
                    finish();
                }
            }
        }).add(archiveGroup, sourceHolder.getValue(), Archive.ArchiveContentType.TEXT, title, content, null, null, null, null);
    }

    private void editDocument(String title, String content) {
        if (archiveType == Archive.Type.USER) {
            editUserArchive(title, content);
        } else {
            editOrganizationArchive(title, content);
        }
    }

    private void editUserArchive(String title, String content) {
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    ToastHelper.make().showMsg(message);
                    finish();
                }
            }
        }).update(mQueryId, Archive.Type.USER, title, content, "", null, null, null);
    }

    private void editOrganizationArchive(String title, String content) {
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    ToastHelper.make().showMsg(message);
                    finish();
                }
            }
        }).update(mQueryId, Archive.Type.GROUP, title, content, "", null, null, null);
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
        return R.layout.fragment_document_new;
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

    private String create_date = "";

    private void initializeHolders(Archive archive) {
        create_date = null == archive ? "" : archive.getCreateDate();
        if (null == strings) {
            strings = StringHelper.getStringArray(R.array.ui_individual_new_document);
        }
        // 标题
        if (null == titleHolder) {
            titleHolder = new SimpleInputableViewHolder(titleInputView, this);
        }
        if (StringHelper.isEmpty(title)) {
            title = null == archive ? "" : archive.getTitle();
        }
        titleHolder.showContent(format(strings[0], title));
        titleHolder.focusEnd();

        // 来源，应该不可以更改
        if (null == sourceHolder) {
            sourceHolder = new SimpleInputableViewHolder(sourceView, this);
        }
        sourceHolder.showContent(format(strings[1], source));

        // 时间
        if (null == timeHolder) {
            timeHolder = new SimpleClickableViewHolder(timeView, this);
            timeHolder.addOnViewHolderClickListener(viewHolderClickListener);
        }
        showCreateDate(null == archive ? new Date() : Utils.parseDate(StringHelper.getString(R.string.ui_base_text_date_time_format), archive.getCreateDate()));

        // 隐私
        if (null == securityHolder) {
            securityHolder = new SimpleClickableViewHolder(securityView, this);
            securityHolder.addOnViewHolderClickListener(viewHolderClickListener);
        }
        securityHolder.showContent(format(strings[3], getPrivacy()));

        // 内容
        contentView.setValue(null == archive ? "" : StringHelper.escapeFromHtml(archive.getContent()));

        if (null == filePickerDialog) {
            properties = new DialogProperties();
            // 选择文件
            properties.selection_type = DialogConfigs.FILE_SELECT;
            // 可以多选
            properties.selection_mode = DialogConfigs.MULTI_MODE;
            // 最多可选文件数量
            properties.maximum_count = getMaxSelectable();
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
    }

    private String getPrivacy() {
        if (!StringHelper.isEmpty(privacy)) {
            try {
                JSONObject object = new JSONObject(privacy);
                if (object.has("status")) {
                    int state = object.getInt("status");
                    switch (state) {
                        case 1:
                            return StringHelper.getString(R.string.ui_base_text_public);
                        case 2:
                            return StringHelper.getString(R.string.ui_base_text_private);
                        case 3:
                            return StringHelper.getString(R.string.ui_security_force_to_user, object.get("userName"));
                        case 4:
                            return StringHelper.getString(R.string.ui_security_force_to_group, object.getString("groupName"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return StringHelper.getString(R.string.ui_security_fragment_title);
    }

    private int itemHeight = 0;

    private void resetAttachmentsLayoutHeight() {
        if (itemHeight == 0) {
            itemHeight = attachmentLayout.getHeight() / mAdapter.getItemCount();
        }
        int screenHeight = getScreenHeight();
        int top = attachmentLayout.getTop();
        int bot = attachmentLayout.getBottom();
        int height = attachmentLayout.getMeasuredHeight();
        int items = itemHeight * mAdapter.getItemCount();
        log(format("top:%d,bottom:%d,height:%d/%d,mheight:%d", top, bot, bot - top, attachmentLayout.getHeight(), attachmentLayout.getMeasuredHeight()));
        if (top + items + itemHeight > screenHeight) {
            height = screenHeight - top - itemHeight;
        } else {
            height = items;
        }
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) attachmentLayout.getLayoutParams();
        params.height = height;
        attachmentLayout.setLayoutParams(params);
    }

    private void resetAttachmentsHeight() {
//        Handler().post(new Runnable() {
//            @Override
//            public void run() {
//                resetAttachmentsLayoutHeight();
//            }
//        });
    }

    private OnViewHolderClickListener viewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            switch (index) {
                case 1:
                    // 选择日期
                    openDatePicker();
                    break;
                case 2:
                    // 隐私设置
                    openActivity(PrivacyFragment.class.getName(),
                            String.valueOf(archiveType), PrivacyFragment.REQUEST_SECURITY, true, false);
                    break;
            }
        }
    };

    private DialogSelectionListener dialogSelectionListener = new DialogSelectionListener() {
        @Override
        public void onSelectedFilePaths(String[] strings) {
            mAdapter.update(Arrays.asList(strings));
            resetAttachmentsHeight();
        }
    };

    private void resetSelectedFiles() {
        int size = mAdapter.getItemCount();
        if (size > 0) {
            List<String> tmp = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                tmp.add(mAdapter.get(i));
            }
            filePickerDialog.markFiles(tmp);
        }
    }

    @Click({R.id.ui_tool_attachment_button})
    private void elementClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ui_tool_attachment_button:
                Utils.hidingInputBoard(contentView);
                resetSelectedFiles();
                filePickerDialog.show();
                break;
        }
    }

    private void showCreateDate(Date date) {
        timeHolder.showContent(StringHelper.format(strings[2], Utils.format(StringHelper.getString(R.string.ui_base_text_date_format_chs), date)));
    }

    private void openDatePicker() {
        Utils.hidingInputBoard(contentView);
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
        if (StringHelper.isEmpty(create_date)) {
            tpv.setDate(Calendar.getInstance());
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(Utils.parseDate(StringHelper.getString(R.string.ui_base_text_date_time_format), create_date));
            tpv.setDate(calendar);
        }
        tpv.show();
    }

    private OnViewHolderClickListener attachmentViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            mAdapter.remove(index);
            resetAttachmentsHeight();
        }
    };

    private class FileAdapter extends RecyclerViewAdapter<AttachmentViewHolder, String> {
        @Override
        public AttachmentViewHolder onCreateViewHolder(View itemView, int viewType) {
            AttachmentViewHolder holder = new AttachmentViewHolder(itemView, ArchiveNewFragment.this);
            holder.addOnViewHolderClickListener(attachmentViewHolderClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_attachment;
        }

        @Override
        public void onBindHolderOfView(final AttachmentViewHolder holder, int position, @Nullable String item) {
            holder.showContent(item);
            Handler().post(new Runnable() {
                @Override
                public void run() {
                    if (itemHeight <= 0) {
                        itemHeight = holder.itemView.getMeasuredHeight() + getDimension(R.dimen.ui_static_dp_half);
                    }
                    resetAttachmentsHeight();
                }
            });
        }

        @Override
        protected int comparator(String item1, String item2) {
            return item1.compareTo(item2);
        }
    }
}
