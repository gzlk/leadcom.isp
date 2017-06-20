package com.gzlk.android.isp.fragment.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.daimajia.swipe.util.Attributes;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.SimpleDialogHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.home.SystemMessageViewHolder;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.nim.model.notification.NimMessage;

import java.util.ArrayList;
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
            loadingLocalMessages();
        }
    }

    private void loadingLocalMessages() {
        List<NimMessage> list = new Dao<>(NimMessage.class).query();
        if (null != list) {
            for (NimMessage msg : list) {
                if (!messages.contains(msg)) {
                    messages.add(msg);
                    mAdapter.notifyItemInserted(messages.size() - 1);
                }
            }
        }
        displayNothing(mAdapter.getItemCount() < 1);
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            // 点击查看通知
        }
    };

    private BaseViewHolder.OnHandlerBoundDataListener<NimMessage> handlerBoundDataListener = new BaseViewHolder.OnHandlerBoundDataListener<NimMessage>() {
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
                mAdapter.delete(holder);
                return true;
            }
        }, null);
    }

    private ArrayList<NimMessage> messages = new ArrayList<>();

    private class MessageAdapter extends RecyclerSwipeAdapter<SystemMessageViewHolder> {

        private void delete(SystemMessageViewHolder holder) {
            mItemManger.removeShownLayouts(holder.getSwipeLayout());
            int pos = holder.getAdapterPosition();
            messages.remove(pos);
            mItemManger.closeAllItems();
        }

        @Override
        public SystemMessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            int layout = R.layout.holder_view_system_message_deleteable;
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(layout, viewGroup, false);
            SystemMessageViewHolder holder = new SystemMessageViewHolder(view, SystemMessageFragment.this);
            // 删除
            holder.addOnHandlerBoundDataListener(handlerBoundDataListener);
            // 点击
            holder.addOnViewHolderClickListener(onViewHolderClickListener);
            return holder;
        }

        @Override
        public void onBindViewHolder(SystemMessageViewHolder holder, int i) {
            holder.showContent(messages.get(i));
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        @Override
        public int getSwipeLayoutResourceId(int i) {
            return R.id.ui_holder_view_system_message_swipe_layout;
        }
    }
}
