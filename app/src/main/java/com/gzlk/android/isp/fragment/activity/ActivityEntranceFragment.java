package com.gzlk.android.isp.fragment.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.activity.ActRequest;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.org.InvitationRequest;
import com.gzlk.android.isp.application.NimApplication;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.SimpleDialogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.attachment.AttachmentViewHolder;
import com.gzlk.android.isp.holder.common.SimpleClickableViewHolder;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.activity.Activity;
import com.gzlk.android.isp.model.activity.Label;
import com.gzlk.android.isp.model.common.Attachment;
import com.gzlk.android.isp.model.organization.Invitation;
import com.gzlk.android.isp.nim.model.notification.NimMessage;
import com.gzlk.android.isp.nim.session.NimSessionHelper;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能描述：</b>活动报名页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/04 13:27 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/04 13:27 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ActivityEntranceFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_TID = "aef_param_tid";
    private static final String PARAM_MSG_ID = "aef_param_msg_id";

    public static ActivityEntranceFragment newInstance(String params) {
        ActivityEntranceFragment siaf = new ActivityEntranceFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        // 活动的id
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        // 活动的tid
        bundle.putString(PARAM_TID, strings[1]);
        // nim 消息id
        if (strings.length > 2) {
            bundle.putLong(PARAM_MSG_ID, Long.valueOf(strings[2]));
        }
        siaf.setArguments(bundle);
        return siaf;
    }

    private String tid = "";
    private long nimId = 0L;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        tid = bundle.getString(PARAM_TID, "");
        nimId = bundle.getLong(PARAM_MSG_ID, 0L);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_TID, tid);
        bundle.putLong(PARAM_MSG_ID, nimId);
    }

    // view
    @ViewId(R.id.ui_activity_entrance_image)
    private ImageDisplayer imageView;
    @ViewId(R.id.ui_activity_entrance_title)
    private View titleView;
    @ViewId(R.id.ui_activity_entrance_time)
    private View timeView;
    @ViewId(R.id.ui_activity_entrance_address)
    private View addressView;
    @ViewId(R.id.ui_activity_entrance_label)
    private View labelView;
    @ViewId(R.id.ui_activity_entrance_creator)
    private View creatorView;
    // holder
    private SimpleClickableViewHolder titleHolder, timeHolder, addressHolder, labelHolder, creatorHolder;

    private String[] items;
    private int imageWidth, imageHeight;
    private AttachmentAdapter mAdapter;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public int getLayout() {
        return R.layout.fragment_activity_entrance;
    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_activity_enter_fragment_title);
        initializeAdapter();
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
        fetchingActivity();
    }

    @Override
    protected void onLoadingMore() {
        isLoadingComplete(true);
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    @Click({R.id.ui_activity_entrance_reject,
            R.id.ui_activity_entrance_agree})
    private void elementClick(View view) {
        switch (view.getId()) {
            case R.id.ui_activity_entrance_reject:
                checkIsInTeam(false);
//                if (isMeInTeamNow()) {
//                    handleHandledActivity(false);
//                    // 如果已经是活动的成员，则提示不能继续操作
//                    ToastHelper.make().showMsg(R.string.ui_activity_entrance_disagree_failed);
//                } else {
//                    reject();
//                }
                break;
            case R.id.ui_activity_entrance_agree:
                checkIsInTeam(true);
//                if (isMeInTeamNow()) {
//                    handleHandledActivity(true);
//                    // 如果已经是活动内的成员，则直接打开活动群聊窗口
//                    openTeamSession();
//                } else {
//                    agree();
//                }
                break;
        }
    }

    private void checkIsInTeam(final boolean agree) {
        showImageHandlingDialog(agree ? R.string.ui_activity_entrance_agreeing : R.string.ui_activity_entrance_disagreeing);
        NIMClient.getService(TeamService.class).queryTeamMember(tid, Cache.cache().userId).setCallback(new RequestCallback<TeamMember>() {
            @Override
            public void onSuccess(TeamMember teamMember) {
                // 查找当前用户是否已经在群内
                if (null != teamMember) {
                    hideImageHandlingDialog();
                    handleHandledActivity(agree);
                    if (teamMember.isInTeam()) {
                        // 已经是群内的成员
                        if (agree) {
                            // 如果已经是活动内的成员，则直接打开活动群聊窗口
                            openTeamSession();
                        } else {
                            // 如果已经是活动的成员，则提示不能继续操作
                            ToastHelper.make().showMsg(R.string.ui_activity_entrance_disagree_failed);
                        }
                    } else {
                        // 已经退出群了
                        if (agree) {
                            agree();
                        } else {
                            reject();
//                            ToastHelper.make().showMsg(R.string.ui_activity_property_exited);
//                            finish();
                        }
                    }
                } else {
                    if (agree) {
                        agree();
                    } else {
                        reject();
                    }
                }
            }

            @Override
            public void onFailed(int i) {
                hideImageHandlingDialog();
                if (i == 404) {
                    // 找不到成员是404？
                    if (agree) {
                        agree();
                    } else {
                        reject();
                    }
                }
            }

            @Override
            public void onException(Throwable throwable) {
                hideImageHandlingDialog();
            }
        });
    }

    private void reject() {
        InvitationRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Invitation>() {
            @Override
            public void onResponse(Invitation invitation, boolean success, String message) {
                super.onResponse(invitation, success, message);
                hideImageHandlingDialog();
                ToastHelper.make().showMsg(message);
                if (success) {
                    handleHandledActivity(false);
                    finish();
                }
            }
        }).activityInviteHandle(tid, InvitationRequest.INVITE_DISAGREE, "");
    }

    private void agree() {
        showImageHandlingDialog(R.string.ui_activity_entrance_agreeing);
        InvitationRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Invitation>() {
            @Override
            public void onResponse(Invitation invitation, boolean success, String message) {
                super.onResponse(invitation, success, message);
                hideImageHandlingDialog();
                if (success) {
                    handleHandledActivity(true);
                    ToastHelper.make().showMsg(message);
                    warningApproveSuccess();
                }
            }
        }).activityInviteHandle(tid, InvitationRequest.INVITE_AGREE, "");
    }

    private void handleHandledActivity(boolean agree) {
        Dao<NimMessage> dao = new Dao<>(NimMessage.class);
        NimMessage msg = dao.querySingle(Model.Field.Id, nimId);
        if (null != msg) {
            msg.setHandled(true);
            msg.setHandleState(agree);
            dao.save(msg);
            NimApplication.dispatchCallbacks();
        }
    }

    private void warningApproveSuccess() {
        SimpleDialogHelper.init(Activity()).show(R.string.ui_activity_entrance_agreed, R.string.ui_base_text_yes, R.string.ui_base_text_cancel, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                openTeamSession();
                return true;
            }
        }, new DialogHelper.OnDialogCancelListener() {
            @Override
            public void onCancel() {
                finish();
            }
        });
    }

    private void openTeamSession() {
        finish();
        // 报名成功之后打开群聊页面
        NimSessionHelper.startTeamSession(Activity(), tid);
    }

    private void fetchingActivity() {
        if (isEmpty(mQueryId)) {
            fetchingActivityByTid();
        } else {
            fetchActivity();
        }
    }

    private void fetchActivity() {
        displayLoading(true);
        ActRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Activity>() {
            @Override
            public void onResponse(Activity activity, boolean success, String message) {
                super.onResponse(activity, success, message);
                if (success) {
                    if (null != activity) {
                        initializeHolders(activity);
                        initializeAttachments(activity.getAttachList());
                    }
                }
                displayLoading(false);
            }
        }).find(mQueryId);
    }

    private void fetchingActivityByTid() {
        displayLoading(true);
        ActRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Activity>() {
            @Override
            public void onResponse(Activity activity, boolean success, String message) {
                super.onResponse(activity, success, message);
                if (success) {
                    if (null != activity) {
                        initializeHolders(activity);
                        initializeAttachments(activity.getAttachList());
                    }
                }
                displayLoading(false);
            }
        }).findTid(tid);
    }

    private void resetImageViewSize() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        imageWidth = getScreenWidth();
        imageHeight = imageWidth / 2;
        params.height = imageHeight;
        imageView.setLayoutParams(params);
    }

    private void initializeHolders(Activity activity) {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_activity_entrance_items);
            resetImageViewSize();
        }
        String img = (null != activity && !isEmpty(activity.getCover())) ? activity.getCover() : ("drawable://" + R.drawable.img_activity_cover_1);
        imageView.displayImage(img, imageWidth, imageHeight, false, false);

        if (null == titleHolder) {
            titleHolder = new SimpleClickableViewHolder(titleView, this);
        }
        img = null == activity ? "" : activity.getTitle();
        titleHolder.showContent(format(items[0], img));

        if (null == timeHolder) {
            timeHolder = new SimpleClickableViewHolder(timeView, this);
        }
        img = (null != activity && !isEmpty(activity.getBeginDate())) ? activity.getBeginDate() : "";
        timeHolder.showContent(format(items[1], (isEmpty(img) ? "" : formatDateTime(img))));

        if (null == addressHolder) {
            addressHolder = new SimpleClickableViewHolder(addressView, this);
        }
        img = null == activity ? "" : activity.getSite();
        addressHolder.showContent(format(items[2], img));

        if (null == labelHolder) {
            labelHolder = new SimpleClickableViewHolder(labelView, this);
        }
        labelHolder.showContent(format(items[3], getLabels(null == activity ? null : activity.getLabels())));

        if (null == creatorHolder) {
            creatorHolder = new SimpleClickableViewHolder(creatorView, this);
        }
        creatorHolder.showContent(format(items[4], null == activity ? "" : activity.getCreatorName()));
    }

    private String getLabels(List<Label> list) {
        if (null == list || list.size() < 1) {
            return StringHelper.getString(R.string.ui_activity_entrance_labels_nothing);
        }
        int size = list.size();
        return size == 1 ? list.get(0).getName() :
                format("%s、%s", list.get(0).getName(), list.get(1).getName());
    }

    private void initializeAttachments(ArrayList<Attachment> list) {
        if (null != list) {
            mAdapter.update(list);
        }
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new AttachmentAdapter();
            mRecyclerView.setAdapter(mAdapter);
            initializeHolders(null);
            fetchingActivity();
        }
    }

    private class AttachmentAdapter extends RecyclerViewAdapter<AttachmentViewHolder, Attachment> {

        @Override
        public AttachmentViewHolder onCreateViewHolder(View itemView, int viewType) {
            return new AttachmentViewHolder(itemView, ActivityEntranceFragment.this);
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
}
