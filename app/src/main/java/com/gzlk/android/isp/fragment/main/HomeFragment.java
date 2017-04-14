package com.gzlk.android.isp.fragment.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.holder.HorizontalRecyclerViewHolder;
import com.gzlk.android.isp.holder.TextViewHolder;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;

/**
 * <b>功能描述：</b>主页<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/12 20:01 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/12 20:01 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class HomeFragment extends BaseSwipeRefreshSupportFragment {

    @ViewId(R.id.ui_tool_home_top_channel_container)
    private RelativeLayout topChannelView;

    @ViewId(R.id.ui_tool_home_top_channel_full_container)
    private LinearLayout fullChannelView;
    @ViewId(R.id.ui_tool_home_top_channel_full_background)
    private LinearLayout fullChannelBackground;

    HorizontalRecyclerViewHolder topChannelHolder;

    private String[] channel = new String[]{"0|会议1", "1|会议2", "2|会议3", "3|会议4", "4|会议5", "5|会议6", "6|会议7"};
    private String[] fullChannel = new String[]{"0|推荐选项1", "1|推荐选项2", "2|推荐选项3", "3|推荐选项4", "4|推荐选项5", "5|推荐选项6", "6|推荐选项7"};

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public int getLayout() {
        return R.layout.fragment_main_home;
    }

    @Override
    protected void onSwipeRefreshing() {

    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    public void doingInResume() {
        tryPaddingContent(true);
        displayNothing(true);
        initializeHolder();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void destroyView() {

    }

    @Click({R.id.ui_tool_home_top_channel_button, R.id.ui_tool_home_top_channel_full_title_button})
    private void elementClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ui_tool_home_top_channel_button:
                // 打开全部栏目
                openFullChannel();
                break;
            case R.id.ui_tool_home_top_channel_full_title_button:
                closeFullChannel();
                break;
        }
    }

    /**
     * 显示或隐藏背景
     */
    private void showFullChannelBackground(final boolean show) {
        fullChannelBackground.animate()
                .alpha(show ? 1 : 0)
                .setDuration(getInteger(R.integer.integer_default_animate_duration))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        if (show) {
                            // 显示背景时，动画开始之初visibility设置为可见
                            fullChannelBackground.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (!show) {
                            // 隐藏背景时，动画结束之后visibility设置为不可见
                            fullChannelBackground.setVisibility(View.GONE);
                        }
                    }
                }).start();
    }

    private void openFullChannel() {
        fullChannelView.animate()
                .translationY(0)
                .setDuration(getInteger(R.integer.integer_default_animate_duration))
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        fullChannelView.setVisibility(View.VISIBLE);
                    }
                })
                .start();
        showFullChannelBackground(true);
    }

    private void closeFullChannel() {
        fullChannelView.animate()
                .translationY(-fullChannelView.getHeight())
                .setDuration(getInteger(R.integer.integer_default_animate_duration))
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        fullChannelView.setVisibility(View.GONE);
                    }
                })
                .start();
        showFullChannelBackground(false);
    }

    private void initializeHolder() {
        if (null == topChannelHolder) {
            topChannelHolder = new HorizontalRecyclerViewHolder(topChannelView, HomeFragment.this);
            topChannelHolder.addOnItemClickListener(topChannelItemClickListener);
            topChannelHolder.displaySelectedEffect(true);
            topChannelHolder.setDataSources(channel);
        }
        if (fullChannelView.getVisibility() == View.GONE) {
            // 初始化全部选项的位置
            fullChannelView.animate().translationY(-fullChannelView.getHeight()).setDuration(10).start();
        }
    }

    private TextViewHolder.OnItemClickListener topChannelItemClickListener = new TextViewHolder.OnItemClickListener() {
        @Override
        public void onItemClick(int index) {

        }
    };
}
