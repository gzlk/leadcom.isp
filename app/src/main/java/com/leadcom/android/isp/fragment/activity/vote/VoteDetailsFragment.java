package com.leadcom.android.isp.fragment.activity.vote;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.activity.AppNoticeRequest;
import com.leadcom.android.isp.api.activity.AppVoteRecordRequest;
import com.leadcom.android.isp.api.activity.AppVoteRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.helper.popup.SimpleDialogHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.activity.VoteOptionViewHolder;
import com.leadcom.android.isp.holder.activity.VoteViewHolder;
import com.leadcom.android.isp.listener.OnHandleBoundDataListener;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.activity.Activity;
import com.leadcom.android.isp.model.activity.AppNotice;
import com.leadcom.android.isp.model.activity.vote.AppVote;
import com.leadcom.android.isp.model.activity.vote.AppVoteItem;
import com.leadcom.android.isp.model.activity.vote.AppVoteRecord;
import com.leadcom.android.isp.nim.model.extension.NoticeAttachment;
import com.hlk.hlklib.layoutmanager.CustomLinearLayoutManager;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredButton;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>投票详情页<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/30 16:14 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/30 16:14 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class VoteDetailsFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_TID = "vdf_param_tid";

    public static VoteDetailsFragment newInstance(String params) {
        VoteDetailsFragment vdf = new VoteDetailsFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        // 投票应用的id
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        // tid
        bundle.putString(PARAM_TID, strings[1]);
        vdf.setArguments(bundle);
        return vdf;
    }

    public static void open(BaseFragment fragment, String tid, String setupId) {
        fragment.openActivity(VoteDetailsFragment.class.getName(), format("%s,%s", setupId, tid), true, false);
    }

    public static void open(Context context, int requestCode, String tid, String setupId) {
        BaseActivity.openActivity(context, VoteDetailsFragment.class.getName(), format("%s,%s", setupId, tid), requestCode, true, false);
    }

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

    @ViewId(R.id.ui_tool_swipe_refreshable_recycler_view)
    private RecyclerView voteOptions;
    @ViewId(R.id.ui_activity_vote_details_end_time)
    private TextView endTime;
    @ViewId(R.id.ui_activity_vote_details_refused)
    private TextView refused;
    @ViewId(R.id.ui_tool_view_bottom_buttons_1)
    private CorneredButton bottomButton1;
    @ViewId(R.id.ui_tool_view_bottom_buttons_2)
    private CorneredButton bottomButton2;

    private VoteViewHolder voteViewHolder;
    private AppVote mAppVote;
    private String activityId = "", tid;

    private VoteItemAdapter mAdapter;

    @Override
    public int getLayout() {
        return R.layout.fragment_activity_vote_details;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bottomButton1.setVisibility(View.GONE);
        bottomButton2.setVisibility(View.GONE);
    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

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

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_activity_vote_details_fragment_title);
        initializeHolder();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    @Click({R.id.ui_tool_view_bottom_buttons_1, R.id.ui_tool_view_bottom_buttons_2})
    private void elementClick(View view) {
        switch (view.getId()) {
            case R.id.ui_tool_view_bottom_buttons_1:
                voting(new ArrayList<String>());
                break;
            case R.id.ui_tool_view_bottom_buttons_2:
                vote();
                break;
        }
    }

    private void fetchingActivity() {
        if (isEmpty(activityId)) {
            Activity act = Activity.getByTid(tid);
            if (null != act) {
                activityId = act.getId();
            }
        }
    }

    private void vote() {
        fetchingActivity();
        if (isEmpty(activityId)) {
            ToastHelper.make().showMsg(R.string.ui_activity_vote_creator_invalid_activity);
            return;
        }
        ArrayList<String> ids = selectedItems();
        if (ids.size() > 0) {
            showImageHandlingDialog(R.string.ui_activity_vote_details_voting);
            voting(ids);
        } else {
            warningRefuseVote();
        }
    }

    // 提醒是否是要弃权
    private void warningRefuseVote() {
        SimpleDialogHelper.init(Activity()).show(R.string.ui_activity_vote_details_nothing_selected, R.string.ui_base_text_yes, R.string.ui_base_text_cancel, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                bottomButton1.performClick();
                return true;
            }
        }, null);
    }

    private ArrayList<String> selectedItems() {
        ArrayList<String> ids = new ArrayList<>();
        for (int i = 0, size = mAdapter.getItemCount(); i < size; i++) {
            AppVoteItem item = mAdapter.get(i);
            if (item.isSelected()) {
                ids.add(item.getId());
            }
        }
        return ids;
    }

    private void voting(ArrayList<String> itemIds) {
        AppVoteRecordRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<AppVoteRecord>() {
            @Override
            public void onResponse(AppVoteRecord appVoteRecord, boolean success, String message) {
                super.onResponse(appVoteRecord, success, message);
                hideImageHandlingDialog();
                if (success) {
                    // 投票完毕
                    loadingVoteDetails();
                } else {
                    ToastHelper.make().showMsg(R.string.ui_activity_vote_details_voting_failed);
                }
            }
        }).add(mQueryId, itemIds);
    }

    private void initializeHolder() {
        if (null == voteViewHolder) {
            voteViewHolder = new VoteViewHolder(mRootView, this);
            voteOptions.setLayoutManager(new CustomLinearLayoutManager(voteOptions.getContext()));
            voteOptions.setNestedScrollingEnabled(false);
            bottomButton1.setText(R.string.ui_activity_vote_details_button_reject);
            bottomButton2.setText(R.string.ui_activity_vote_details_button_vote);
        }
        if (null == mAdapter) {
            mAdapter = new VoteItemAdapter();
            voteOptions.setAdapter(mAdapter);
            loadingVoteDetails();
        }
    }

    private void loadingVoteDetails() {
        showImageHandlingDialog(R.string.ui_activity_vote_details_loading);
        AppVoteRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<AppVote>() {
            @Override
            public void onResponse(AppVote appVote, boolean success, String message) {
                super.onResponse(appVote, success, message);
                hideImageHandlingDialog();
                if (success) {
                    mAppVote = appVote;
                    showDetails();
                } else {
                    ToastHelper.make().showMsg(message);
                    finish();
                }
            }
        }).find(mQueryId, AppVoteRequest.FIND_MY, remotePageNumber);
    }

    private boolean hasVoted = false;

    private int getRefusedCount() {
        int ret = 0;
        if (null != mAppVote.getActVoteList()) {
            for (AppVoteRecord record : mAppVote.getActVoteList()) {
                if (record.getStatus() == AppVote.Status.REFUSED) {
                    ret++;
                }
            }
        }
        return ret;
    }

    private void showDetails() {
        voteViewHolder.showContent(mAppVote);
        voteViewHolder.showVoteType(mAppVote);
        hasVoted = null != mAppVote.getActVote() && !mAppVote.getActVote().haventVote();
        if (hasVoted || mAppVote.isEnded()) {
            AppVoteItem refused = AppVoteItem.getRefuseItem();
            refused.setNum(getRefusedCount());
            if (!mAppVote.getActVoteItemList().contains(refused)) {
                mAppVote.getActVoteItemList().add(refused);
            } else {
                int index = mAppVote.getActVoteItemList().indexOf(refused);
                mAppVote.getActVoteItemList().set(index, refused);
            }
        }
        refused.setVisibility(hasVoted && mAppVote.getActVote().hasRefused() ? View.VISIBLE : View.GONE);
        setCustomTitle(hasVoted || mAppVote.isEnded() ? R.string.ui_activity_vote_details_fragment_title1 : R.string.ui_activity_vote_details_fragment_title);
        mAdapter.update(mAppVote.getActVoteItemList(), false);
        endTime.setText(getString(R.string.ui_activity_vote_details_end_time, formatDateTime(mAppVote.getEndDate())));
        // 已投过票或者已结束时，不显示投票按钮
        bottomButton1.setVisibility((mAppVote.isEnded() || hasVoted) ? View.GONE : View.VISIBLE);
        bottomButton2.setVisibility((mAppVote.isEnded() || hasVoted) ? View.GONE : View.VISIBLE);
        boolean isMe = !isEmpty(mAppVote.getCreatorId()) && mAppVote.getCreatorId().equals(Cache.cache().userId);
        if (isMe) {
            resetRightIcon();
        }
    }

    private void resetRightIcon() {
        setRightIcon(R.string.ui_icon_more);
        //setRightText(R.string.ui_base_text_delete);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                openDetailsDialog();
            }
        });
    }

    private void openDetailsDialog() {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                return View.inflate(voteOptions.getContext(), R.layout.popup_dialog_activity_vote_details, null);
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {

            }
        }).addOnEventHandlerListener(new DialogHelper.OnEventHandlerListener() {
            @Override
            public int[] clickEventHandleIds() {
                return new int[]{R.id.ui_dialog_button_activity_vote_publish, R.id.ui_dialog_button_activity_vote_delete};
            }

            @Override
            public boolean onClick(View view) {
                switch (view.getId()) {
                    case R.id.ui_dialog_button_activity_vote_publish:
                        publishVote();
                        break;
                    case R.id.ui_dialog_button_activity_vote_delete:
                        warningDelete();
                        break;
                }
                return true;
            }
        }).setPopupType(DialogHelper.SLID_IN_BOTTOM).setAdjustScreenWidth(true).show();
    }

    private void publishVote() {
        showImageHandlingDialog(R.string.ui_activity_vote_details_publish_details_warning);
        String content = "";
        if (null != mAppVote.getActVoteItemList()) {
            int index = 1;
            for (AppVoteItem item : mAppVote.getActVoteItemList()) {
                content += (isEmpty(content) ? "" : "\n") + getString(R.string.ui_activity_vote_details_publish_content, index, item.getContent(), item.getNum());
                index++;
            }
        }
        if (isEmpty(content)) {
            content = getString(R.string.ui_activity_vote_details_publish_content_no_option);
        }
        final String finalContent = content;
        AppNoticeRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<AppNotice>() {
            @Override
            public void onResponse(AppNotice notice, boolean success, String message) {
                super.onResponse(notice, success, message);
                hideImageHandlingDialog();
                if (success) {
                    if (null != notice) {
                        NoticeAttachment attachment = new NoticeAttachment();
                        attachment.setContent(finalContent);
                        attachment.setCustomId(notice.getId());
                        attachment.setActId(mAppVote.getActId());
                        attachment.setTitle(getString(R.string.ui_activity_vote_details_publish_content_title, mAppVote.getTitle()));
                        IMMessage msg = MessageBuilder.createCustomMessage(tid, SessionTypeEnum.Team, notice.getTitle(), attachment);
                        NIMClient.getService(MsgService.class).sendMessage(msg, false);
                    }
                }
            }
        }).add(mAppVote.getActId(), mAppVote.getTitle(), content);
    }

    private void warningDelete() {
        SimpleDialogHelper.init(Activity()).show(R.string.ui_activity_vote_details_delete, R.string.ui_base_text_delete, R.string.ui_base_text_cancel, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                deleteVote();
                return true;
            }
        }, null);
    }

    private void deleteVote() {
        showImageHandlingDialog(R.string.ui_activity_vote_details_deleting);
        AppVoteRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<AppVote>() {
            @Override
            public void onResponse(AppVote appVote, boolean success, String message) {
                super.onResponse(appVote, success, message);
                hideImageHandlingDialog();
                if (success) {
                    finish();
                }
            }
        }).delete(mQueryId);
    }

    private int getSelectedCount() {
        int count = 0;
        for (int i = 0, size = mAdapter.getItemCount(); i < size; i++) {
            count += (mAdapter.get(i).isSelected()) ? 1 : 0;
        }
        return count;
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            if (mAppVote.isEnded()) {
                return;
            }
            if (mAppVote.getMaxSelectable() <= 1) {
                // 单选
                for (int i = 0, size = mAdapter.getItemCount(); i < size; i++) {
                    AppVoteItem item = mAdapter.get(i);
                    item.setSelected(i == index);
                    mAdapter.notifyItemChanged(i);
                }
            } else {
                AppVoteItem item = mAdapter.get(index);
                if (item.isSelected()) {
                    // 取消已选择了的选项
                    item.setSelected(!item.isSelected());
                    mAdapter.notifyItemChanged(index);
                } else {
                    // 多选
                    if (getSelectedCount() >= mAppVote.getMaxSelectable()) {
                        ToastHelper.make().showMsg(getString(R.string.ui_activity_vote_details_max_selected, mAppVote.getMaxSelectable()));
                    } else {
                        item.setSelected(!item.isSelected());
                        mAdapter.notifyItemChanged(index);
                    }
                }
            }
        }
    };

    private OnHandleBoundDataListener<AppVoteItem> onHandlerBoundDataListener = new OnHandleBoundDataListener<AppVoteItem>() {
        @Override
        public AppVoteItem onHandlerBoundData(BaseViewHolder holder) {
            AppVoteItem item = mAdapter.get(holder.getAdapterPosition());
            VoteItemDetailsFragment.open(VoteDetailsFragment.this, mAppVote.getId(), item.getId());
            return null;
        }
    };

    private class VoteItemAdapter extends RecyclerViewAdapter<VoteOptionViewHolder, AppVoteItem> {

        @Override
        public VoteOptionViewHolder onCreateViewHolder(View itemView, int viewType) {
            VoteOptionViewHolder holder = new VoteOptionViewHolder(itemView, VoteDetailsFragment.this);
            holder.addOnViewHolderClickListener(onViewHolderClickListener);
            holder.addOnHandlerBoundDataListener(onHandlerBoundDataListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_vote_option;
        }

        @Override
        public void onBindHolderOfView(VoteOptionViewHolder holder, int position, @Nullable AppVoteItem item) {
            holder.showContent(item, mAppVote);
            holder.showVoted(hasVoted || mAppVote.isEnded());
        }

        @Override
        protected int comparator(AppVoteItem item1, AppVoteItem item2) {
            return 0;
        }
    }
}
