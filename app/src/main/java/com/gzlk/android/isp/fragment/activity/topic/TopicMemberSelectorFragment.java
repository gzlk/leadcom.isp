package com.gzlk.android.isp.fragment.activity.topic;

import android.os.Bundle;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseDownloadingUploadingSupportFragment;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.common.SimpleClickableViewHolder;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.hlk.hlklib.lib.inject.ViewId;

/**
 * <b>功能描述：</b>选择议题的参与人<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/08/30 16:12 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/08/30 16:12 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class TopicMemberSelectorFragment extends BaseDownloadingUploadingSupportFragment {

    public static TopicMemberSelectorFragment newInstance(String params) {
        TopicMemberSelectorFragment tmsf = new TopicMemberSelectorFragment();
        Bundle bundle = new Bundle();
        // 活动的tid
        bundle.putString(PARAM_QUERY_ID, params);
        tmsf.setArguments(bundle);
        return tmsf;
    }

    public static void open(BaseFragment fragment, int req, String tid) {
        fragment.openActivity(TopicMemberSelectorFragment.class.getName(), tid, req, true, false);
    }

    @ViewId(R.id.ui_activity_topic_member_selector_from_activity)
    private View fromActivity;
    @ViewId(R.id.ui_activity_topic_member_selector_from_group)
    private View fromGroup;

    private SimpleClickableViewHolder toActivity, toGroup;
    private String[] items;

    @Override
    public int getLayout() {
        return R.layout.fragment_activity_topic_member_selector;
    }

    @Override
    public void doingInResume() {
        initializeHolders();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    private void initializeTitleEvents() {
        setCustomTitle(R.string.ui_activity_create_member_select_title);
    }

    private void initializeHolders() {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_activity_topic_member_selector_items);
        }
        if (null == toActivity) {
            toActivity = new SimpleClickableViewHolder(fromActivity, this);
            toActivity.showContent(items[0]);
            toActivity.addOnViewHolderClickListener(holderClickListener);
            initializeTitleEvents();
        }
        if (null == toGroup) {
            toGroup = new SimpleClickableViewHolder(fromGroup, this);
            toGroup.showContent(items[1]);
            toGroup.addOnViewHolderClickListener(holderClickListener);
        }
    }

    private OnViewHolderClickListener holderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            switch (index) {
                case 0:
                    // 从当前活动中选择
                    resultData(String.valueOf(REQUEST_CHANGE));
                    break;
                case 1:
                    // 从组织通讯录中选择
                    resultData(String.valueOf(REQUEST_DELETE));
                    break;
            }
        }
    };
}
