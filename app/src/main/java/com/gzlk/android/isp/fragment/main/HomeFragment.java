package com.gzlk.android.isp.fragment.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseViewPagerSupportFragment;
import com.gzlk.android.isp.fragment.home.ArchiveFragment;
import com.gzlk.android.isp.fragment.home.MomentFragment;
import com.gzlk.android.isp.fragment.home.SeminarFragment;
import com.gzlk.android.isp.holder.common.HorizontalRecyclerViewHolder;
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

public class HomeFragment extends BaseViewPagerSupportFragment {

    // 顶部固定选项
    @ViewId(R.id.ui_tool_home_top_channel_container)
    private RelativeLayout topChannelView;
    @ViewId(R.id.ui_tool_home_top_channel_1)
    private TextView topChannel1;
    @ViewId(R.id.ui_tool_home_top_channel_2)
    private TextView topChannel2;
    @ViewId(R.id.ui_tool_home_top_channel_3)
    private TextView topChannel3;
    @ViewId(R.id.ui_tool_home_top_channel_4)
    private TextView topChannel4;

    // 全部选项
    @ViewId(R.id.ui_tool_home_top_channel_full_container)
    private LinearLayout fullChannelView;
    @ViewId(R.id.ui_tool_home_top_channel_full_background)
    private LinearLayout fullChannelBackground;

    HorizontalRecyclerViewHolder fullHolder;

    private String[] fullChannel = new String[]{"0|推荐1", "1|推荐2", "2|推荐3", "3|推荐4", "4|推荐5", "5|推荐6", "6|推荐7", "7|推荐8"};

    @Override
    public int getLayout() {
        return R.layout.fragment_main_home;
    }

    @Override
    public void doingInResume() {
        tryPaddingContent(true);
        initializeHolder();
        super.doingInResume();
    }

    @Override
    protected void initializeFragments() {
        mFragments.add(new SeminarFragment());
        mFragments.add(new com.gzlk.android.isp.fragment.home.ActivityFragment());
        mFragments.add(new ArchiveFragment());
        mFragments.add(new MomentFragment());
    }

    @Override
    protected void viewPagerSelectionChanged(int position) {
        int color1 = getColor(R.color.textColorHintDark);
        int color2 = getColor(R.color.colorPrimary);
        topChannel1.setTextColor(position == 0 ? color2 : color1);
        topChannel2.setTextColor(position == 1 ? color2 : color1);
        topChannel3.setTextColor(position == 2 ? color2 : color1);
        topChannel4.setTextColor(position == 3 ? color2 : color1);
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void destroyView() {

    }

    @Click({R.id.ui_tool_home_top_channel_button,
            R.id.ui_tool_home_top_channel_full_title_button,
            R.id.ui_tool_home_top_channel_1, R.id.ui_tool_home_top_channel_2,
            R.id.ui_tool_home_top_channel_3, R.id.ui_tool_home_top_channel_4
    })
    private void elementClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ui_tool_home_top_channel_button:
                // 打开全部栏目
                showFullChannel(true);
                break;
            case R.id.ui_tool_home_top_channel_full_title_button:
                // 隐藏全部栏目
                showFullChannel(false);
                break;
            case R.id.ui_tool_home_top_channel_1:
                setDisplayPage(0);
                break;
            case R.id.ui_tool_home_top_channel_2:
                setDisplayPage(1);
                break;
            case R.id.ui_tool_home_top_channel_3:
                setDisplayPage(2);
                break;
            case R.id.ui_tool_home_top_channel_4:
                setDisplayPage(3);
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

    private void showFullChannel(final boolean shown) {
        fullChannelView.animate()
                .translationY(shown ? 0 : -fullChannelView.getHeight())
                .setDuration(getInteger(R.integer.integer_default_animate_duration))
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (shown) {
                            fullChannelView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (!shown) {
                            fullChannelView.setVisibility(View.GONE);
                        }
                    }
                })
                .start();
        showFullChannelBackground(shown);
    }

    private void initializeHolder() {
        if (fullChannelView.getVisibility() == View.GONE) {
            // 初始化全部选项的位置
            fullChannelView.animate().translationY(-fullChannelView.getHeight()).setDuration(10).start();
        }
        if (null == fullHolder) {
            fullHolder = new HorizontalRecyclerViewHolder(fullChannelView, this, false);
            fullHolder.setItemDecoration(true);
            fullHolder.setDataSources(fullChannel);
        }
    }
}
