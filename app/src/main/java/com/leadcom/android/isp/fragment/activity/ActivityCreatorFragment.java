package com.leadcom.android.isp.fragment.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.bigkoo.pickerview.TimePickerView;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.gson.reflect.TypeToken;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.activity.ActRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.org.InvitationRequest;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.fragment.common.CoverPickFragment;
import com.leadcom.android.isp.fragment.organization.GroupsContactPickerFragment;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.attachment.AttachmentViewHolder;
import com.leadcom.android.isp.holder.common.SimpleClickableViewHolder;
import com.leadcom.android.isp.holder.common.SimpleInputableViewHolder;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.activity.Activity;
import com.leadcom.android.isp.model.activity.Label;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.organization.Invitation;
import com.leadcom.android.isp.model.organization.SubMember;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

public class ActivityCreatorFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_GROUP = "caf_group_id_params";
    private static final String PARAM_MEMBERS = "caf_members";
    private static final String PARAM_COVER = "caf_cover";
    private static final String PARAM_LABEL = "caf_label";
    private static final String PARAM_HAPPEN = "caf_happen_date";
    private static final String PARAM_TITLE = "caf_title";
    private static final String PARAM_ADDR = "caf_address";
    private static final String PARAM_CONTENT = "caf_content";
    private static final String PARAM_OPEN_STATUS = "caf_open_status";
    private static final String PARAM_ATTACHMENTS = "caf_attachments";

    public static ActivityCreatorFragment newInstance(String params) {
        ActivityCreatorFragment caf = new ActivityCreatorFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        // 活动id，如果不为空则说明是修改活动的属性
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        // 组织的id，本活动所属的组织
        bundle.putString(PARAM_GROUP, strings[1]);
        caf.setArguments(bundle);
        return caf;
    }

    public static void open(BaseFragment fragment, String groupId, String activityId) {
        fragment.openActivity(ActivityCreatorFragment.class.getName(), format("%s,%s", activityId, groupId), REQUEST_CREATE, true, true);
    }

    private String memberJson = "[]", labelJson = "[]";
    private String cover = "", title = "", address = "", intro = "";
    // 默认只向组织内部开放
    private int authPublic = Activity.OpenStatus.NONE;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mGroupId = bundle.getString(PARAM_GROUP, "");
        memberJson = bundle.getString(PARAM_MEMBERS, "[]");
        resetMembers();
        cover = bundle.getString(PARAM_COVER, "");
        labelJson = bundle.getString(PARAM_LABEL, "[]");
        resetLabels();
        happenDate = bundle.getString(PARAM_HAPPEN, "");
        title = bundle.getString(PARAM_TITLE, "");
        address = bundle.getString(PARAM_ADDR, "");
        intro = bundle.getString(PARAM_CONTENT, "");
        authPublic = bundle.getInt(PARAM_OPEN_STATUS, Activity.OpenStatus.NONE);
        String json = bundle.getString(PARAM_ATTACHMENTS, "[]");
        attachments = Json.gson().fromJson(json, new TypeToken<ArrayList<Attachment>>() {
        }.getType());
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_GROUP, mGroupId);
        bundle.putString(PARAM_MEMBERS, memberJson);
        bundle.putString(PARAM_COVER, cover);
        bundle.putString(PARAM_LABEL, labelJson);
        bundle.putString(PARAM_HAPPEN, happenDate);
        title = titleHolder.getValue();
        bundle.putString(PARAM_TITLE, title);
        address = addressHolder.getValue();
        bundle.putString(PARAM_ADDR, address);
        intro = introView.getValue();
        bundle.putString(PARAM_CONTENT, intro);
        bundle.putInt(PARAM_OPEN_STATUS, authPublic);
        bundle.putString(PARAM_ATTACHMENTS, Json.gson().toJson(attachments, new TypeToken<ArrayList<Attachment>>() {
        }.getType()));
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        switch (requestCode) {
            case REQUEST_MEMBER:
                // 活动成员选择返回了
                memberJson = getResultedData(data);
                resetMembers();
                break;
            case REQUEST_COVER:
                // 封面选择了
                cover = getResultedData(data);
                break;
            case REQUEST_LABEL:
                labelJson = getResultedData(data);
                resetLabels();
                break;
        }
        super.onActivityResult(requestCode, data);
    }

    private void resetMembers() {
        selectedMembers = Json.gson().fromJson(memberJson, new TypeToken<ArrayList<SubMember>>() {
        }.getType());
        if (null == selectedMembers) {
            selectedMembers = new ArrayList<>();
        }
    }

//    private void updateMember(Member member) {
//        boolean exist = false;
//        for (Member m : selectedMembers) {
//            if (m.getUserId().equals(member.getUserId())) {
//                exist = true;
//                break;
//            }
//        }
//        if (!exist) {
//            selectedMembers.add(member);
//        }
//    }

    private void resetLabels() {
        labelsNames = Json.gson().fromJson(labelJson, new TypeToken<ArrayList<String>>() {
        }.getType());
        if (null == labelsNames) {
            labelsNames = new ArrayList<>();
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
    private ClearEditText introView;

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
    private ArrayList<SubMember> selectedMembers;
    private ArrayList<String> labelsNames;
    private String[] items, openStates;

    private FileAdapter mAdapter;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        maxSelectable = 1;
        setCustomTitle(isEmpty(mQueryId) ? R.string.ui_activity_create_fragment_title : R.string.ui_activity_create_fragment_title_edit);
        setRightText(isEmpty(mQueryId) ? R.string.ui_base_text_publish : R.string.ui_base_text_commit);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                tryPublishActivity();
            }
        });
        enableSwipe(!isEmpty(mQueryId));
        setOnFileUploadingListener(mOnFileUploadingListener);
        // 编辑活动属性时，不要显示成员添加选项
        memberView.setVisibility(isEmpty(mQueryId) ? View.VISIBLE : View.GONE);
        tryLoadActivity();
    }

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

    @Override
    protected boolean checkStillEditing() {
        return !isEmpty(titleHolder.getValue()) || !isEmpty(addressHolder.getValue()) || !isEmpty(introView.getValue());
    }

    private void initializeHolder(final Activity activity) {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_activity_create_items);
            mRecyclerView.setNestedScrollingEnabled(false);
        }
        if (null == openStates) {
            openStates = StringHelper.getStringArray(R.array.ui_activity_open_status);
        }
        // cover
        if (null == coverHolder) {
            coverHolder = new SimpleClickableViewHolder(coverView, this);
            coverHolder.addOnViewHolderClickListener(onViewHolderClickListener);
        }
        boolean non = null == activity;
        if (isEmpty(cover)) {
            if (!non) {
                cover = activity.getCover();
            }
        }
        String value;// = format(items[0], (isEmpty(cover) ? none : ""));
        coverHolder.showContent(items[0]);
        coverHolder.showImage(cover);

        // title
        if (null == titleHolder) {
            titleHolder = new SimpleInputableViewHolder(titleView, this);
        }
        if (isEmpty(title)) {
            if (!non) {
                title = activity.getTitle();
            }
        }
        value = format(items[1], title);
        titleHolder.showContent(value);

        // time
        if (null == timeHolder) {
            timeHolder = new SimpleClickableViewHolder(timeView, this);
            timeHolder.addOnViewHolderClickListener(onViewHolderClickListener);
        }
        if (isEmpty(happenDate)) {
            if (!non) {
                happenDate = activity.getCreateDate();
            }
        }
        value = format(items[2], isEmpty(happenDate) ? "" : formatDateTime(happenDate));
        timeHolder.showContent(value);

        // address
        if (null == addressHolder) {
            addressHolder = new SimpleInputableViewHolder(addressView, this);
        }
        if (isEmpty(address)) {
            if (!non) {
                address = activity.getSite();
            }
        }
        value = format(items[3], address);
        addressHolder.showContent(value);

        // type
        if (null == typeHolder) {
            typeHolder = new SimpleClickableViewHolder(typeView, this);
            typeHolder.addOnViewHolderClickListener(onViewHolderClickListener);
            if (!non) {
                labelsNames = Label.getLabelNames(activity.getLabel());
            }
        }
        String tmp = labelsNames.size() < 1 ? "选择标签" : Label.getLabelDesc(labelsNames);
        value = format(items[4], tmp);
        typeHolder.showContent(value);

        // privacy
        if (null == privacyHolder) {
            privacyHolder = new SimpleClickableViewHolder(privacyView, this);
            privacyHolder.addOnViewHolderClickListener(onViewHolderClickListener);
        }
        if (authPublic == Activity.OpenStatus.NONE) {
            if (!non) {
                authPublic = activity.getAuthPublic();
            }
        }
        value = format(items[5], openStates[authPublic]);
        privacyHolder.showContent(value);

        // member
        if (null == memberHolder) {
            memberHolder = new SimpleClickableViewHolder(memberView, this);
            memberHolder.addOnViewHolderClickListener(onViewHolderClickListener);
//            if (!non) {
//                if (null != activity.getMemberIdArray() && activity.getMemberIdArray().size() > 0) {
//                    ArrayList<String> names = activity.getMemberNameArray();
//                    int i = 0;
//                    for (final String id : activity.getMemberIdArray()) {
//                        final String name = (null != names && names.size() >= i + 1) ? names.get(i) : "";
//                        Member member = new Member();
//                        member.setUserId(id);
//                        member.setUserName(name);
//                        updateMember(member);
//                        i++;
//                    }
//                }
//            }
        }
        value = format(items[6], SubMember.getMemberInfo(selectedMembers));
        memberHolder.showContent(value);

        if (isEmpty(intro)) {
            if (!non) {
                intro = activity.getIntro();
            }
        }
        introView.setValue(intro);
        if (null == mAdapter) {
            mAdapter = new FileAdapter();
            setSupportLoadingMore(false);
            mRecyclerView.setAdapter(mAdapter);
        }
        updateActivityAttachment(activity);
        createFilePickerDialog();
    }

    private ArrayList<Attachment> attachments;

    // 更新活动里已经存在的文件
    private void updateActivityAttachment(Activity activity) {
        if (null == activity) return;

        if (null == attachments || attachments.size() < 1) {
            attachments = new ArrayList<>();
            if (null != activity.getAttachList()) {
                for (Attachment attachment : activity.getAttachList()) {
                    // 将活动的id存入附件的宿主id中
                    attachment.setArchiveId(activity.getId());
                    attachments.add(attachment);
                }
                mAdapter.update(attachments, false);
            }
        }
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            switch (index) {
                case 0:
                    // 到活动封面拾取器
                    CoverPickFragment.open(ActivityCreatorFragment.this, true, cover, 1, 1);
                    //openActivity(CoverPickFragment.class.getName(), format("%s,true,2,1", cover), REQ_COVER, true, false);
                    break;
                case 1:
                    // 选择活动时间
                    openDatePicker();
                    break;
                case 2:
                    // 选择活动标签
                    labelJson = Json.gson().toJson(labelsNames);
                    String json = replaceJson(labelJson, false);
                    LabelPickFragment.open(ActivityCreatorFragment.this, mGroupId, mQueryId, LabelPickFragment.TYPE_ACTIVITY, json);
                    //openActivity(LabelPickFragment.class.getName(), string, REQ_LABEL, true, false);
                    break;
                case 3:
                    // 选择公开范围
                    openActivityOpenStatus();
                    break;
                case 4:
                    if (!isEmpty(mQueryId)) {
                        ToastHelper.make().showMsg(R.string.ui_activity_create_member_select_blocked);
                    } else {
                        //memberJson = Json.gson().toJson(selectedMembers, new TypeToken<List<SubMember>>() {
                        //}.getType());
                        ////String params = format("%s,false,false,%s", mGroupId, replaceJson(memberJson, false));
                        ////openActivity(GroupContactPickFragment.class.getName(), params, REQ_MEMBER, true, false);
                        //String params = format("%s,false,%s", mGroupId, replaceJson(memberJson, false));
                        //openActivity(GroupSquadContactPickerFragment.class.getName(), params, REQ_MEMBER, true, false);

                        GroupsContactPickerFragment.open(ActivityCreatorFragment.this, mGroupId);
                    }
                    break;
            }
        }
    };

    @Click({R.id.ui_tool_attachment_button})
    private void attachmentClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ui_tool_attachment_button:
                Utils.hidingInputBoard(introView);
                resetSelectedFiles();
                filePickerDialog.show();
                break;
        }
    }

    // 文件选择
    private FilePickerDialog filePickerDialog;

    private void createFilePickerDialog() {
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
    }

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

    private View openStatusView;

    private void openActivityOpenStatus() {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == openStatusView) {
                    openStatusView = View.inflate(Activity(), R.layout.popup_dialog_activity_open_status, null);
                }
                return openStatusView;
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {

            }
        }).addOnEventHandlerListener(new DialogHelper.OnEventHandlerListener() {
            @Override
            public int[] clickEventHandleIds() {
                return new int[]{R.id.ui_dialog_button_activity_open_status_open, R.id.ui_dialog_button_activity_open_status_close};
            }

            @Override
            public boolean onClick(View view) {
                switch (view.getId()) {
                    case R.id.ui_dialog_button_activity_open_status_open:
                        authPublic = Activity.OpenStatus.OPEN;
                        privacyHolder.showContent(format(items[5], format("(%s)", openStates[authPublic])));
                        break;
                    case R.id.ui_dialog_button_activity_open_status_close:
                        authPublic = Activity.OpenStatus.GROUP;
                        privacyHolder.showContent(format(items[5], format("(%s)", openStates[authPublic])));
                        break;
                }
                return true;
            }
        }).setAdjustScreenWidth(true).setPopupType(DialogHelper.SLID_IN_BOTTOM).show();
    }

    private String happenDate;

    private void showCreateDate(Date date) {
        happenDate = Utils.format(StringHelper.getString(R.string.ui_base_text_date_time_format), date);
        timeHolder.showContent(format(items[2], "(" + Utils.format(StringHelper.getString(R.string.ui_base_text_date_time_format_chs_min), date) + ")"));
    }

    private void openDatePicker() {
        Utils.hidingInputBoard(introView);
        TimePickerView tpv = new TimePickerView.Builder(Activity(), new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                showCreateDate(date);
            }
        }).setType(new boolean[]{true, true, true, true, true, false})
                .setTitleBgColor(getColor(R.color.colorPrimary))
                .setSubmitColor(Color.WHITE)
                .setCancelColor(Color.WHITE)
                .setContentSize(getFontDimension(R.dimen.ui_base_text_size))
                .setOutSideCancelable(false)
                .isCenterLabel(true).isDialog(false).build();
        if (isEmpty(happenDate)) {
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
        String address = addressHolder.getValue();
        if (isEmpty(address)) {
            ToastHelper.make().showMsg(R.string.ui_activity_create_address_invalid);
            return;
        }
        String content = introView.getValue();
        if (isEmpty(content)) {
            ToastHelper.make().showMsg(R.string.ui_activity_create_content_invalid);
            return;
        }
        // 预不预选活动成员暂时无所谓
//        if (selectedMembers.size() < 1) {
//            ToastHelper.make().showMsg(R.string.ui_activity_create_member_invalid);
//            return;
//        }
        Utils.hidingInputBoard(introView);
        if (getWaitingForUploadFiles().size() > 0) {
            uploadFiles();
        } else {
            publishActivity();
        }
    }

    private void publishActivity() {
        showImageHandlingDialog(R.string.ui_activity_create_handing_warning);
        handleUploadAttachments();
        String title = titleHolder.getValue().trim();
        String address = addressHolder.getValue().trim();
        String content = introView.getValue().trim();
        String beginDate = isEmpty(happenDate) ? Model.DFT_DATE : happenDate;
        if (isEmpty(mQueryId)) {
            ActRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Activity>() {
                @Override
                public void onResponse(Activity activity, boolean success, String message) {
                    super.onResponse(activity, success, message);
                    if (success) {
                        // 成功之后设置mQuery为新建的活动的id
                        if (null != activity && selectedMembers.size() > 0) {
                            invitePreSelectedMembers(activity.getId());
                        } else {
                            successToClose();
                        }
                    } else {
                        hideImageHandlingDialog();
                    }
                }
            }).add(title, content, authPublic, address, beginDate, mGroupId, cover, labelsNames, attachments);
        } else {
            ActRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Activity>() {
                @Override
                public void onResponse(Activity activity, boolean success, String message) {
                    super.onResponse(activity, success, message);
                    if (success) {
                        successToClose();
                    } else {
                        hideImageHandlingDialog();
                    }
                }
            }).update(mQueryId, title, content, authPublic, address, beginDate, cover, labelsNames, attachments);
        }
    }

    /**
     * 邀请预选择的成员到新建的活动里
     */
    private void invitePreSelectedMembers(String activityId) {
        setLoadingText(R.string.ui_activity_create_member_inviting);
        displayLoading(true);
        ArrayList<String> ids = new ArrayList<>();
        for (SubMember member : selectedMembers) {
            ids.add(member.getUserId());
        }
        InvitationRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Invitation>() {
            @Override
            public void onResponse(Invitation invitation, boolean success, String message) {
                super.onResponse(invitation, success, message);
                if (success) {
                    ToastHelper.make().showMsg(R.string.ui_activity_create_member_invited);
                }
                displayLoading(false);
                successToClose();
            }
        }).activityInvite(activityId, ids);
    }

    private void successToClose() {
        hideImageHandlingDialog();
        Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                resultSucceededActivity();
            }
        }, 300);
    }

    // 处理上传之后的文件列表
    private void handleUploadAttachments() {
        // 上传的原始文件
        if (getUploadedFiles().size() > 0) {
            for (int i = 0, len = getUploadedFiles().size(); i < len; i++) {
                Attachment attachment = getUploadedFiles().get(i);
                //attachment.setArchiveId(mQueryId);
                attachments.add(attachment);
            }
        }
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
        // 从附件里删除
        attachments.remove(attachment);
        // 从待上传的列表里删除
        if (attachment.isLocalFile()) {
            getWaitingForUploadFiles().remove(attachment.getFullPath());
        }
        // 从本地缓存中删除
        Attachment.delete(attachment.getId());
        //filePickerDialog.getProperties().maximum_count = getMaxSelectable() - images.size() - names.size();
    }

    private class FileAdapter extends RecyclerViewAdapter<AttachmentViewHolder, Attachment> {
        @Override
        public AttachmentViewHolder onCreateViewHolder(View itemView, int viewType) {
            AttachmentViewHolder holder = new AttachmentViewHolder(itemView, ActivityCreatorFragment.this);
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
