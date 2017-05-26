package com.gzlk.android.isp.fragment.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.base.BaseTransparentSupportFragment;
import com.gzlk.android.isp.fragment.base.BaseViewPagerSupportFragment;
import com.gzlk.android.isp.fragment.individual.SettingFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CustomTextView;

/**
 * <b>功能描述：</b>首页<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/06 16:43 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/06 16:43 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MainFragment extends BaseViewPagerSupportFragment {
    /**
     * 新建MainFragment的时候传入的参数，以此当作初始化显示的页面（也即ViewPager当前显示的页面的index），int型
     */
    public static final String PARAM_SELECTED = "mf_param1";
    private static final String PARAM_OLD_TITLE = "mf_old_title";

    @ViewId(R.id.ui_main_tool_bar_container)
    private RelativeLayout toolBar;
    @ViewId(R.id.ui_ui_custom_title_left_icon)
    private CustomTextView leftIcon;
    @ViewId(R.id.ui_ui_custom_title_left_text)
    private TextView leftText;
    @ViewId(R.id.ui_main_tool_bar_background)
    private View toolBarBackground;
    @ViewId(R.id.ui_ui_custom_title_text)
    private TextView toolBarTitleText;
    // 个人的设置按钮
    @ViewId(R.id.ui_ui_custom_title_right_icon_1)
    private CustomTextView rightSettingIcon;
    @ViewId(R.id.ui_ui_custom_title_right_icon_2_container)
    private RelativeLayout rightChatIconContainer;
    @ViewId(R.id.ui_ui_custom_title_right_icon_2_flag)
    private LinearLayout rightChatIconFlag;
    /**
     * 最右侧菜单栏的 + 按钮，平时隐藏
     */
    @ViewId(R.id.ui_ui_custom_title_right_container)
    private View rightIconContainer;

    @ViewId(R.id.ui_tool_main_bottom_icon_1)
    private CustomTextView iconView1;
    @ViewId(R.id.ui_tool_main_bottom_icon_2)
    private CustomTextView iconView2;
    @ViewId(R.id.ui_tool_main_bottom_icon_3)
    private CustomTextView iconView3;
    @ViewId(R.id.ui_tool_main_bottom_icon_4)
    private CustomTextView iconView4;
    @ViewId(R.id.ui_tool_main_bottom_text_1)
    private TextView textView1;
    @ViewId(R.id.ui_tool_main_bottom_text_2)
    private TextView textView2;
    @ViewId(R.id.ui_tool_main_bottom_text_3)
    private TextView textView3;
    @ViewId(R.id.ui_tool_main_bottom_text_4)
    private TextView textView4;

    private String oldTitleText = "";

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        oldTitleText = bundle.getString(PARAM_OLD_TITLE, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_OLD_TITLE, oldTitleText);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_main;
    }

    @Override
    public void doingInResume() {
        Activity().setRootViewPadding(toolBar, true);
        super.doingInResume();
        leftIcon.setText(R.string.ui_icon_query);
        leftText.setText(null);
        rightIconContainer.setVisibility(View.GONE);
        ((IndividualFragmentMultiType) mFragments.get(3)).setToolBar(toolBarBackground).setToolBarTextView(toolBarTitleText);
    }

    @Override
    protected boolean onBackKeyPressed() {
        return getDisplayedPage() == 3 && mFragments.get(3).onBackKeyEvent();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void initializeFragments() {
        if (mFragments.size() <= 0) {
            mFragments.add(new HomeFragment());
            mFragments.add(new ActivityFragment());
            mFragments.add(new OrganizationFragment());
            mFragments.add(new IndividualFragmentMultiType());
            ((ActivityFragment) mFragments.get(1)).mainFragment = this;
            ((OrganizationFragment) mFragments.get(2)).mainFragment = this;
        }
    }

    public void setTitleText(String text) {
        if (StringHelper.isEmpty(oldTitleText)) {
            oldTitleText = toolBarTitleText.getText().toString();
        }
        toolBarTitleText.setText(text);
    }

    public void restoreTitleText() {
        if (!StringHelper.isEmpty(oldTitleText)) {
            toolBarTitleText.setText(oldTitleText);
            oldTitleText = "";
        }
    }

    @Override
    protected void viewPagerSelectionChanged(int position) {
        int color1 = getColor(R.color.textColorHintDark);
        int color2 = getColor(R.color.colorPrimary);

        iconView1.setTextColor(position == 0 ? color2 : color1);
        textView1.setTextColor(position == 0 ? color2 : color1);

        iconView2.setTextColor(position == 1 ? color2 : color1);
        textView2.setTextColor(position == 1 ? color2 : color1);

        iconView3.setTextColor(position == 2 ? color2 : color1);
        textView3.setTextColor(position == 2 ? color2 : color1);

        iconView4.setTextColor(position == 3 ? color2 : color1);
        textView4.setTextColor(position == 3 ? color2 : color1);

        if (position != 2) {
            restoreTitleText();
            // 活动页面也需要显示右上角的 + 用来显示活动管理菜单
            showRightIcon(position == 1);
        } else {
            ((OrganizationFragment) mFragments.get(2)).needChangeTitle();
        }

        boolean needHandleTitleBar = true;
        for (int i = 0, len = mFragments.size(); i < len; i++) {
            BaseTransparentSupportFragment fragment = mFragments.get(i);
            if (i == 2 || fragment instanceof IndividualFragmentMultiType) {
                // 个人界面已经显示了，此时不再需要改变标题栏背景
                needHandleTitleBar = !((IndividualFragmentMultiType) mFragments.get(3)).isTitleBarShown();
            }
            fragment.setViewPagerDisplayedCurrent(position == i);
        }
        if (needHandleTitleBar) {
            handleTitleBar(position);
        } else if (position != 3) {
            transparentTitleText(false);
        }
    }

    @Override
    protected void destroyView() {

    }

    @Click({R.id.ui_tool_main_bottom_clickable_1, R.id.ui_tool_main_bottom_clickable_2,
            R.id.ui_tool_main_bottom_clickable_3, R.id.ui_tool_main_bottom_clickable_4,
            R.id.ui_ui_custom_title_right_icon_1, R.id.ui_ui_custom_title_right_icon_2_container,
            R.id.ui_ui_custom_title_left_container, R.id.ui_ui_custom_title_right_container})
    private void elementClick(View view) {
        log(view.toString());
        int id = view.getId();
        switch (id) {
            case R.id.ui_tool_main_bottom_clickable_1:
                setDisplayPage(0);
                break;
            case R.id.ui_tool_main_bottom_clickable_2:
                setDisplayPage(1);
                break;
            case R.id.ui_tool_main_bottom_clickable_3:
                setDisplayPage(2);
                break;
            case R.id.ui_tool_main_bottom_clickable_4:
                setDisplayPage(3);
                break;
            case R.id.ui_ui_custom_title_right_icon_1:
                // 打开个人设置
                openActivity(SettingFragment.class.getName(), "", true, false);
                break;
            case R.id.ui_ui_custom_title_right_icon_2_container:
                // 打开消息页面
                break;
            case R.id.ui_ui_custom_title_left_container:
                // 搜索
                break;
            case R.id.ui_ui_custom_title_right_container:
                // + 号的点击
                BaseFragment fragment = mFragments.get(getDisplayedPage());
                if (fragment instanceof ActivityFragment) {
                    ((ActivityFragment) fragment).rightIconClick(view);
                } else if (fragment instanceof OrganizationFragment) {
                    ((OrganizationFragment) fragment).rightIconClick(view);
                }
                break;
        }
    }

    private void handleTitleBar(int position) {
        switch (position) {
            default:
                transparentTitleBar(false);
                break;
            case 3:
                transparentTitleBar(true);
                break;
        }
    }

    public void showRightIcon(final boolean shown) {
        if (shown && rightIconContainer.getVisibility() == View.VISIBLE) return;
        if (!shown && rightIconContainer.getVisibility() == View.GONE) return;
        //rightIconContainer.setVisibility(shown ? View.VISIBLE : View.GONE);
        rightIconContainer.animate().setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!shown) {
                    rightIconContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (shown) {
                    rightIconContainer.setVisibility(View.VISIBLE);
                }
            }
        }).alpha(shown ? 1 : 0)
                .translationX(shown ? 0 : rightIconContainer.getWidth())
                .setDuration(duration())
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
        translationChatIcon(shown);
    }

    private void translationChatIcon(final boolean shown) {
        final int width = rightChatIconContainer.getWidth();
        rightChatIconContainer.animate().setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                rightChatIconContainer.setTranslationX(0);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        }).setDuration(duration())
                .translationXBy(shown ? 0 : width)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void transparentTitleBar(boolean transparent) {
        toolBarBackground.animate()
                .alpha(transparent ? 0 : 1)
                .setDuration(duration())
                .setInterpolator(new AccelerateDecelerateInterpolator()).start();
        transparentTitleText(transparent);
        displayRightIcon(transparent);
    }

    private void transparentTitleText(boolean transparent) {
        toolBarTitleText.animate()
                .alpha(transparent ? 0 : 1)
                .setDuration(duration())
                .setInterpolator(new AccelerateDecelerateInterpolator()).start();
    }

    private void displayRightIcon(final boolean show) {
        rightSettingIcon.animate().alpha(show ? 1 : 0)
                .setDuration(duration())
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (!show) {
                            rightSettingIcon.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        if (show) {
                            rightSettingIcon.setVisibility(View.VISIBLE);
                        }
                    }
                }).start();
    }
}
