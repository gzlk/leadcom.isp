package com.gzlk.android.isp.fragment.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.activity.ActRequest;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.org.InvitationRequest;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.SimpleDialogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.attachment.AttachmentViewHolder;
import com.gzlk.android.isp.holder.common.SimpleClickableViewHolder;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.activity.Activity;
import com.gzlk.android.isp.model.activity.Label;
import com.gzlk.android.isp.model.common.Attachment;
import com.gzlk.android.isp.model.organization.Invitation;
import com.gzlk.android.isp.nim.model.notification.NimMessage;
import com.gzlk.android.isp.nim.session.NimSessionHelper;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;

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

    public static ActivityEntranceFragment newInstance(String params) {
        ActivityEntranceFragment siaf = new ActivityEntranceFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        // 活动的id
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        // 活动的tid
        bundle.putString(PARAM_TID, strings[1]);
        siaf.setArguments(bundle);
        return siaf;
    }

    private String tid = "";

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        tid = bundle.getString(PARAM_TID, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_TID, tid);
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
                reject();
                break;
            case R.id.ui_activity_entrance_agree:
                agree();
                break;
        }
    }

    private void reject() {
        showImageHandlingDialog(R.string.ui_activity_entrance_disagreeing);
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
        }).activityReject(tid);
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
        }).activityApprove(tid);
    }

    private void handleHandledActivity(boolean agree) {
        Dao<NimMessage> dao = new Dao<>(NimMessage.class);
        NimMessage msg = dao.querySingle(Activity.Field.NimId, tid);
        if (null != msg) {
            msg.setHandled(true);
            msg.setHandleState(agree);
            dao.save(msg);
        }
    }

    private void warningApproveSuccess() {
        SimpleDialogHelper.init(Activity()).show(R.string.ui_activity_entrance_agreed, R.string.ui_base_text_yes, R.string.ui_base_text_cancel, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                finish();
                // 报名成功之后打开群聊页面
                NimSessionHelper.startTeamSession(Activity(), tid);
                return true;
            }
        }, new DialogHelper.OnDialogCancelListener() {
            @Override
            public void onCancel() {
                finish();
            }
        });
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
                        initializeAttachments(activity.getAttUrlArray());
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
                        initializeAttachments(activity.getAttUrlArray());
                    }
                }
                displayLoading(false);
            }
        }).findByTid(tid);
    }

    private void initializeHolders(Activity activity) {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_activity_entrance_items);
        }
        String img = (null != activity && !isEmpty(activity.getImg())) ? activity.getImg() : ("drawable://" + R.mipmap.img_image_loading_fail);
        imageView.displayImage(img, getScreenWidth(), getDimension(R.dimen.ui_static_dp_200), false, false);

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
