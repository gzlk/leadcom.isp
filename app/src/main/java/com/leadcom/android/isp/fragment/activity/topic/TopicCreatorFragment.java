package com.leadcom.android.isp.fragment.activity.topic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.api.activity.AppTopicRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.fragment.activity.ActivityMemberFragment;
import com.leadcom.android.isp.fragment.base.BaseDownloadingUploadingSupportFragment;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.helper.popup.SimpleDialogHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.common.SimpleClickableViewHolder;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.activity.Activity;
import com.leadcom.android.isp.model.activity.topic.AppTopic;
import com.leadcom.android.isp.model.organization.SubMember;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>新建活动议题<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/08/30 15:58 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/08/30 15:58 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class TopicCreatorFragment extends BaseDownloadingUploadingSupportFragment {

    private static final String PARAM_PICKED = "tcf_picked_member";
    private static final String PARAM_CREATING = "tcf_is_creating";

    public static TopicCreatorFragment newInstance(String params) {
        TopicCreatorFragment tcf = new TopicCreatorFragment();
        Bundle bundle = new Bundle();
        // 传过来的活动的tid
        bundle.putString(PARAM_QUERY_ID, params);
        tcf.setArguments(bundle);
        return tcf;
    }

    public static void open(BaseFragment fragment, int req, String tid) {
        fragment.openActivity(TopicCreatorFragment.class.getName(), tid, req, true, true);
    }

    public static void open(Context context, int req, String tid) {
        BaseActivity.openActivity(context, TopicCreatorFragment.class.getName(), tid, req, true, true);
    }

    @ViewId(R.id.ui_activity_topic_creator_title)
    private ClearEditText titleView;
    @ViewId(R.id.ui_activity_topic_creator_member)
    private View memberView;

    private SimpleClickableViewHolder memberHolder;
    private boolean isCreating = false;
    private ArrayList<SubMember> selectedMembers = new ArrayList<>();

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        isCreating = bundle.getBoolean(PARAM_CREATING, false);
        String json = bundle.getString(PARAM_PICKED, EMPTY_ARRAY);
        resetSelectedMembers(json);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putBoolean(PARAM_CREATING, isCreating);
        bundle.putString(PARAM_PICKED, SubMember.toJson(selectedMembers));
    }

    private void resetSelectedMembers(String json) {
        selectedMembers = SubMember.fromJson(json);
        if (null == selectedMembers) {
            selectedMembers = new ArrayList<>();
        }
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_activity_topic_creator;
    }

    @Override
    public void doingInResume() {
        initializeHolder();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    protected boolean checkStillEditing() {
        return !isEmpty(titleView.getValue());
    }

    private void setTitleEvents() {
        setCustomTitle(R.string.ui_activity_topic_list_right_title_text);
        setRightText(R.string.ui_base_text_launch);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                tryCreateTopic();
            }
        });
    }

    private void tryCreateTopic() {
        if (isEmpty(titleView.getValue())) {
            ToastHelper.make().showMsg(R.string.ui_activity_topic_creator_title_invalid);
            return;
        }
        if (selectedMembers.size() < 1) {
            warningNoMember();
        } else {
            createTopic();
        }
    }

    private void warningNoMember() {
        SimpleDialogHelper.init(Activity()).show(R.string.ui_activity_topic_creator_no_member, R.string.ui_base_text_confirm, R.string.ui_base_text_cancel, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                createTopic();
                return true;
            }
        }, null);
    }

    private void createTopic() {
        if (isCreating) {
            ToastHelper.make().showMsg("正在创建议题中，请不要重复创建");
            return;
        }
        Activity act = Activity.getByTid(mQueryId);
        if (null == act) {
            ToastHelper.make().showMsg(R.string.ui_activity_property_not_exist);
            finish();
        } else {
            showImageHandlingDialog(R.string.ui_activity_topic_creator_creating);
            isCreating = true;
            AppTopicRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<AppTopic>() {
                @Override
                public void onResponse(AppTopic appTopic, boolean success, String message) {
                    super.onResponse(appTopic, success, message);
                    hideImageHandlingDialog();
                    if (success) {
                        resultData(AppTopic.toJson(appTopic));
                    } else {
                        isCreating = false;
                    }
                }
            }).add(act.getId(), titleView.getValue(), SubMember.getUserIds(selectedMembers));
        }
    }

    private void initializeHolder() {
        if (null == memberHolder) {
            memberHolder = new SimpleClickableViewHolder(memberView, this);
            memberHolder.showContent(getString(R.string.ui_activity_topic_creator_member_text, getString(R.string.ui_activity_create_member_select_title)));
            memberHolder.addOnViewHolderClickListener(onViewHolderClickListener);
            setTitleEvents();
        }
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            switch (index) {
                case 1:
                    Activity act = Activity.getByTid(mQueryId);
                    if (null != act) {
                        // 从活动中选择
                        ActivityMemberFragment.open(TopicCreatorFragment.this, REQUEST_SELECT, act.getId(), act.getGroupId(), true, true);
                        // 选择参与人
                        //TopicMemberSelectorFragment.open(TopicCreatorFragment.this, REQUEST_SELECT, "");
                    } else {
                        ToastHelper.make().showMsg(R.string.ui_activity_property_not_exist);
                        finish();
                    }
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SELECT:
//                Activity act = Activity.getByTid(mQueryId);
//                if (null != act) {
//                    int req = Integer.valueOf(getResultedData(data));
//                    if (req == REQUEST_CHANGE) {
//                        // 从活动中选择
//                        ActivityMemberFragment.open(TopicCreatorFragment.this, REQUEST_CHANGE, act.getId(), act.getGroupId(), true, true);
//                    } else if (req == REQUEST_DELETE) {
//                        // 从组织通讯录中选择
//                        GroupContactPickFragment.open(TopicCreatorFragment.this, REQUEST_CHANGE, act.getGroupId(), false, false, "");
//                    }
//                } else {
//                    ToastHelper.make().showMsg(R.string.ui_activity_property_not_exist);
//                    finish();
//                }
//                break;
//            case REQUEST_CHANGE:
                // 活动或组织成员选择之后的返回内容
                String json = getResultedData(data);
                resetSelectedMembers(json);
                memberHolder.showContent(getString(R.string.ui_activity_topic_creator_member_text, SubMember.getMemberInfo(selectedMembers)));
                break;
        }
        super.onActivityResult(requestCode, data);
    }
}
