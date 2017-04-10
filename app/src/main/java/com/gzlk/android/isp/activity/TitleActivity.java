package com.gzlk.android.isp.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;

/**
 * <b>功能描述：</b>提供标题栏的Activity<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/06 13:00 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/06 13:00 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class TitleActivity extends BaseActivity {

    // 这些UI貌似必须要用public才能访问到
    @ViewId(R.id.activity_app_bar_layout)
    public AppBarLayout appBarLayout;
    @ViewId(R.id.activity_toolbar)
    public Toolbar mToolbar;

    @ViewId(R.id.ui_ui_custom_title_left_container)
    public LinearLayout mLeftContainer;
    @ViewId(R.id.ui_ui_custom_title_left_text)
    public TextView mLeftText;
    @ViewId(R.id.ui_ui_custom_title_left_icon)
    public CustomTextView mLeftIcon;
    @ViewId(R.id.ui_ui_custom_title_right_text)
    public TextView mRightText;
    @ViewId(R.id.ui_ui_custom_title_right_icon)
    public CustomTextView mRightIcon;
    @ViewId(R.id.ui_ui_custom_title_text)
    public TextView mTitle;

    /**
     * 是否支持Toolbar
     */
    protected boolean isToolbarSupported = true;
    /**
     * 是否需要手动输入
     */
    protected boolean isInputSupported = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 无输入处理事件时，可以显示透明的状态栏，无toolbar的需要在fragment里自己设定padding
        if (isToolbarSupported && !isInputSupported) {
            //transparentStatusBar();
        }
        // 是否有默认的toolbar布局，否则需要自己加载toolbar布局
        setContentView(R.layout.activity_container);
        ViewUtility.bind(this);
        if (isToolbarSupported) {
            setSupportActionBar(mToolbar);
            resetLeftIconMargin();
            if (!isInputSupported) {
                setRootViewPadding(mToolbar, true);
            }
        }
    }

    // 重置左侧按钮的边距
    private void resetLeftIconMargin() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLeftContainer.getLayoutParams();
        // Toolbar有自己的边距，所以这里把左侧icon的左边距清零
        params.leftMargin = 0;
        mLeftContainer.setLayoutParams(params);
    }

    /**
     * 设置是否为沉浸式状态栏留出空白
     */
    public void adjustStatusBar(boolean adjust) {
        //setRootViewPadding(mAppBarLayout, adjust);
    }

    @Click({R.id.ui_ui_custom_title_left_container, R.id.ui_ui_custom_title_right_container})
    public void onTitleIconClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ui_ui_custom_title_left_container:
                if (null != mLeftClick) {
                    mLeftClick.onClick();
                }
                break;
            case R.id.ui_ui_custom_title_right_container:
                if (null != mRightClick) {
                    mRightClick.onClick();
                }
                break;
        }
    }

    /**
     * 显示或隐藏标题栏
     */
    public void showToolbar(boolean shown) {
        if (null != mToolbar) {
            mToolbar.setVisibility(shown ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 显示或隐藏左侧按钮
     *
     * @param shown 显示或隐藏
     */
    public void showLeftIcon(boolean shown) {
        mLeftContainer.setVisibility(shown ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * 设置主 frame
     */
    public void setMainFrameLayout(Fragment fragment) {
        if (!isFinishing()) {
            try {
                int id = R.id.activity_content_container;
                String tag = fragment.getClass().getName();
                getSupportFragmentManager().beginTransaction().replace(id, fragment, tag).commitAllowingStateLoss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置标题栏左侧图标
     */
    public void setLeftIcon(int resId) {
        if (resId == 0) {
            setLeftIcon(null);
        } else {
            setLeftIcon(getString(resId));
        }
    }

    /**
     * 设置标题栏左侧图标
     */
    public void setLeftIcon(String text) {
        if (null == mLeftIcon) {
            ViewUtility.bind(this);
        }
        setLeftIconText(text);
    }

    /**
     * 设置标题栏左侧图标
     */
    private void setLeftIconText(String text) {
        if (null != mLeftIcon) {
            mLeftIcon.setText(text);
        }
    }

    /**
     * 设置菜单栏左边文字
     */
    public void setLeftText(int resId) {
        if (resId == 0) {
            setLeftTextString(null);
        } else {
            setLeftText(getString(resId));
        }
    }

    /**
     * 设置菜单栏左边文字
     */
    public void setLeftText(String text) {
        if (null == mLeftText) {
            ViewUtility.bind(this);
        }
        setLeftTextString(text);
    }

    /**
     * 设置菜单栏左边文字
     */
    private void setLeftTextString(String text) {
        if (null != mLeftText) {
            mLeftText.setText(text);
        }
    }

    /**
     * 设置左边文字大小
     */
    public void setLeftTextSize(float size) {
        if (null == mLeftText) {
            ViewUtility.bind(this);
        }
        if (null != mLeftText) {
            mLeftText.setTextSize(size);
        }
    }

    /**
     * 设置中间标题文字
     */
    public void setCustomTitle(int resId) {
        if (resId == 0) {
            setTitleText(null);
        } else
            setCustomTitle(getString(resId));
    }

    /**
     * 设置中间标题文字
     */
    public void setCustomTitle(String title) {
        if (null == mTitle)
            ViewUtility.bind(this);
        setTitleText(title);
    }

    /**
     * 设置中间标题文字
     */
    private void setTitleText(String text) {
        if (null != mTitle) {
            mTitle.setText(text);
        }
    }

    /**
     * 设置菜单栏右边文字
     */
    public void setRightText(int resId) {
        if (resId == 0)
            setRightTextString(null);
        else
            setRightText(getString(resId));
    }

    /**
     * 设置菜单栏右边文字
     */
    public void setRightText(String text) {
        if (null == mRightText)
            ViewUtility.bind(this);
        setRightTextString(text);
    }

    /**
     * 设置菜单栏右边文字
     */
    private void setRightTextString(String text) {
        if (null != mRightText) {
            mRightText.setText(text);
        }
    }

    /**
     * 设置右边文字大小
     */
    public void setRightTextSize(float size) {
        if (null == mRightText)
            ViewUtility.bind(this);
        mRightText.setTextSize(size);
    }

    /**
     * 设置标题栏右侧图标
     */
    public void setRightIcon(int resId) {
        if (resId == 0) {
            setRightIcon(null);
        } else {
            setRightIcon(getString(resId));
        }
    }

    /**
     * 设置标题栏右侧图标
     */
    public void setRightIcon(String text) {
        if (null == mRightIcon) {
            ViewUtility.bind(this);
        }
        setRightIconText(text);
    }

    /**
     * 设置标题栏右侧图标
     */
    private void setRightIconText(String text) {
        if (null != mRightIcon) {
            mRightIcon.setText(text);
        }
    }
//    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            if (v == mLeftText) {
//                if (null != mLeftClick) {
//                    mLeftClick.onClick();
//                }
//            } else if (v == mRightText) {
//                if (null != mRightClick) {
//                    mRightClick.onClick();
//                }
//            }
//        }
//    };

    private OnTitleButtonClickListener mLeftClick, mRightClick;

    /**
     * 设置左边菜单栏点击事件回调
     */
    public void setLeftTitleClickListener(OnTitleButtonClickListener l) {
        if (null == mLeftText)
            ViewUtility.bind(this);
        mLeftClick = l;
    }

    public View getRightButton() {
        return mRightText;
    }

    /**
     * 设置右边菜单栏点击事件回调
     */
    public void setRightTitleClickListener(OnTitleButtonClickListener l) {
        if (null == mRightText)
            ViewUtility.bind(this);
        mRightClick = l;
    }
}
