package com.gzlk.android.isp.fragment.activity.topic;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.activity.BaseActivity;
import com.gzlk.android.isp.fragment.base.BaseDownloadingUploadingSupportFragment;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.common.SimpleClickableViewHolder;
import com.gzlk.android.isp.holder.common.ToggleableViewHolder;
import com.hlk.hlklib.lib.inject.ViewId;

/**
 * <b>功能描述：</b>议题属性页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/08/30 21:28 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/08/30 21:28 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class TopicPropertyFragment extends BaseDownloadingUploadingSupportFragment {

    public static TopicPropertyFragment newInstance(String params) {
        TopicPropertyFragment tpf = new TopicPropertyFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, params);
        tpf.setArguments(bundle);
        return tpf;
    }

    public static void open(BaseFragment fragment, String tid, int req) {
        fragment.openActivity(TopicPropertyFragment.class.getName(), tid, req, true, false);
    }

    public static void open(Context context, String tid, int req) {
        BaseActivity.openActivity(context, TopicPropertyFragment.class.getName(), tid, req, true, false);
    }

    private String[] items;
    @ViewId(R.id.ui_activity_topic_property_title)
    private View titleView;
    @ViewId(R.id.ui_activity_topic_property_members_title)
    private TextView memberTitle;
    @ViewId(R.id.ui_activity_topic_property_members)
    private FlexboxLayout members;
    @ViewId(R.id.ui_activity_topic_property_history)
    private View historyView;
    @ViewId(R.id.ui_activity_topic_property_files)
    private View fileView;
    @ViewId(R.id.ui_activity_topic_property_mute)
    private View muteView;

    private SimpleClickableViewHolder titleHolder, historyHolder, fileHolder;
    private ToggleableViewHolder muteHolder;

    @Override
    public int getLayout() {
        return R.layout.fratment_activity_topic_property;
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

    private void resetTitleEvent() {
        setCustomTitle(R.string.ui_activity_topic_property_fragment_title);
    }

    private void initializeHolder() {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_activity_topic_property_items);
            resetTitleEvent();
        }
        if (null == titleHolder) {
            titleHolder = new SimpleClickableViewHolder(titleView, this);
        }
        if (null == historyHolder) {
            historyHolder = new SimpleClickableViewHolder(historyView, this);
            historyHolder.showContent(items[2]);
        }
        if (null == fileHolder) {
            fileHolder = new SimpleClickableViewHolder(fileView, this);
            fileHolder.showContent(items[3]);
        }
        if (null == muteHolder) {
            muteHolder = new ToggleableViewHolder(muteView, this);
        }
    }
}
