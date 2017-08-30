package com.gzlk.android.isp.fragment.activity.topic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.activity.BaseActivity;
import com.gzlk.android.isp.fragment.base.BaseDownloadingUploadingSupportFragment;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.holder.common.SimpleClickableViewHolder;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;

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

    public static TopicCreatorFragment newInstance(String params) {
        TopicCreatorFragment tcf = new TopicCreatorFragment();
        Bundle bundle = new Bundle();
        // 传过来的tid
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
        setRightText(R.string.ui_base_text_new);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {

            }
        });
    }

    private void initializeHolder() {
        if (null == memberHolder) {
            memberHolder = new SimpleClickableViewHolder(memberView, this);
            memberHolder.showContent(getString(R.string.ui_activity_topic_creator_member_text));
            memberHolder.addOnViewHolderClickListener(onViewHolderClickListener);
            setTitleEvents();
        }
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            switch (index) {
                case 1:
                    // 选择参与人
                    TopicMemberSelectorFragment.open(TopicCreatorFragment.this, BaseFragment.REQUEST_SELECT, "");
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        switch (requestCode){
            case REQUEST_CHANGE:
                // 从活动中选择
                break;
            case REQUEST_DELETE:
                // 从组织通讯录中选择
                break;
        }
        super.onActivityResult(requestCode, data);
    }
}
