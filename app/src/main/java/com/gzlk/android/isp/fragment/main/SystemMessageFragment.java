package com.gzlk.android.isp.fragment.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.daimajia.swipe.util.Attributes;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.activity.MainActivity;
import com.gzlk.android.isp.adapter.RecyclerViewSwipeAdapter;
import com.gzlk.android.isp.application.NimApplication;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.SimpleDialogHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.home.SystemMessageViewHolder;
import com.gzlk.android.isp.listener.NotificationChangeHandleCallback;
import com.gzlk.android.isp.listener.OnHandleBoundDataListener;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.nim.model.notification.NimMessage;
import com.gzlk.android.isp.nim.session.NimSessionHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.TeamMember;

import java.util.List;

/**
 * <b>功能描述：</b>系统消息页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/04 02:45 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/04 02:45 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SystemMessageFragment extends BaseSwipeRefreshSupportFragment {

    private MessageAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NimApplication.addNotificationChangeCallback(callback);
    }

    @Override
    public void onDestroy() {
        NimApplication.removeNotificationChangeCallback(callback);
        super.onDestroy();
    }

    private NotificationChangeHandleCallback callback = new NotificationChangeHandleCallback() {
        @Override
        public void onChanged() {
            loadingLocalMessages();
        }
    };

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public int getLayout() {
        return R.layout.fragment_top_paddingable_swipe_recycler_view;
    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_system_message_fragment_title);
        setNothingText(R.string.ui_system_message_nothing);
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
        stopRefreshing();
    }

    @Override
    protected void onLoadingMore() {
        isLoadingComplete(true);
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new MessageAdapter();
            mAdapter.setMode(Attributes.Mode.Single);
            mRecyclerView.setAdapter(mAdapter);
        }
        loadingLocalMessages();
    }

    private void loadingLocalMessages() {
        List<NimMessage> list = NimMessage.query();
        if (null != list) {
            mAdapter.update(list, false);
        }
        displayNothing(mAdapter.getItemCount() < 1);
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            // 点击查看通知
            NimMessage msg = mAdapter.get(index);
            if (!isEmpty(msg.getMsgTitle()) && !msg.isHandled()) {
                msg.setHandled(true);
                NimMessage.save(msg);
                NimApplication.dispatchCallbacks();
            }
            if (msg.getType() == NimMessage.Type.ACTIVITY_INVITE && msg.isHandled()) {
                // 活动邀请且已处理过的话
                if (msg.isHandleState()) {
                    checkTeamMember(msg.getTid());
                } else {
                    MainActivity.handleNimMessageDetails(Activity(), msg);
                }
            } else {
                MainActivity.handleNimMessageDetails(Activity(), msg);
            }
        }
    };

    private void checkTeamMember(final String tid) {
        displayLoading(true);
        NIMClient.getService(TeamService.class).queryTeamMember(tid, Cache.cache().userId).setCallback(new RequestCallback<TeamMember>() {
            @Override
            public void onSuccess(TeamMember teamMember) {
                displayLoading(false);
                // 查找当前用户是否已经在群内
                if (null != teamMember) {
                    if (teamMember.isInTeam()) {
                        // 已经是群内的成员, 直接打开群聊页面
                        NimSessionHelper.startTeamSession(Activity(), tid);
                    } else {
                        // 已经退出群了
                        ToastHelper.make().showMsg(R.string.ui_activity_property_exited);
                    }
                }
            }

            @Override
            public void onFailed(int i) {
                displayLoading(false);
            }

            @Override
            public void onException(Throwable throwable) {
                displayLoading(false);
            }
        });
    }

    private OnHandleBoundDataListener<NimMessage> handlerBoundDataListener = new OnHandleBoundDataListener<NimMessage>() {
        @Override
        public NimMessage onHandlerBoundData(BaseViewHolder holder) {
            // 删除通知
            warningDelete((SystemMessageViewHolder) holder);
            return null;
        }
    };

    private void warningDelete(final SystemMessageViewHolder holder) {
        SimpleDialogHelper.init(Activity()).show(R.string.ui_system_message_delete_warning, R.string.ui_base_text_yes, R.string.cancel, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                long id = mAdapter.get(holder.getAdapterPosition()).getId();
                mAdapter.delete(holder);
                removeCache(id);
                return true;
            }
        }, null);
    }

    private void removeCache(long id) {
        NimMessage.delete(id);
        NimApplication.dispatchCallbacks();
    }

    private class MessageAdapter extends RecyclerViewSwipeAdapter<SystemMessageViewHolder, NimMessage> {

        private void delete(SystemMessageViewHolder holder) {
            mItemManger.removeShownLayouts(holder.getSwipeLayout());
            int pos = holder.getAdapterPosition();
            remove(pos);
            notifyItemRemoved(pos);
            mItemManger.closeAllItems();
        }

        @Override
        public SystemMessageViewHolder onCreateViewHolder(View itemView, int viewType) {
            SystemMessageViewHolder holder = new SystemMessageViewHolder(itemView, SystemMessageFragment.this);
            // 删除
            holder.addOnHandlerBoundDataListener(handlerBoundDataListener);
            // 点击
            holder.addOnViewHolderClickListener(onViewHolderClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_system_message_deleteable;
        }

        @Override
        public void onBindHolderOfView(SystemMessageViewHolder holder, int position, @Nullable NimMessage item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(NimMessage item1, NimMessage item2) {
            return 0;
        }

        @Override
        public int getSwipeLayoutResourceId(int i) {
            return R.id.ui_holder_view_system_message_swipe_layout;
        }
    }
}
