package com.gzlk.android.isp.fragment.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gzlk.android.isp.R;
import com.hlk.hlklib.lib.inject.ViewUtility;

import java.lang.reflect.Field;

/**
 * <b>功能描述：</b>提供布局相关的fragment基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/10 21:02 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/10 21:02 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class BaseLayoutSupportFragment extends BaseTitleSupportFragment {

    protected boolean DEBUG = false;
    /**
     * 当前 fragment 的 layout 资源 id
     */
    private int mLayout;

    /**
     * 设置当前 fragment 的 layout
     */
    public void setLayout(int resId) {
        mLayout = resId;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (DEBUG) {
            log("onAttach");
        }
    }

    /**
     * 获取默认的动画时间长度
     */
    public int duration() {
        return getInteger(R.integer.integer_default_animate_duration);
    }

    /**
     * 获取fragment的layout
     */
    public abstract int getLayout();

    /**
     * 从Bundle中获取初始化fragment的参数列表
     */
    protected abstract void getParamsFromBundle(Bundle bundle);

    /**
     * 获取UI控件<br />
     * 由于此方法是在onCreateView的return之前调用的，所以使用注解时需要传入_rootView进行注解，
     * 否则所有的注解属性都会是null(因为此时rootView还未加到fragment里去)
     */
    //protected abstract void findViews();

    /**
     * Fragment onResume过程中需要执行的方法
     */
    public abstract void doingInResume();

    /**
     * 是否支持设置默认的标题栏信息
     */
    protected abstract boolean shouldSetDefaultTitleEvents();

    /**
     * 暂存fragment的初始化参数列表
     */
    protected abstract void saveParamsToBundle(Bundle bundle);

    /**
     * 销毁View时
     */
    protected abstract void destroyView();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG) {
            log("onCreate");
        }
        mLayout = getLayout();
        if (null == savedInstanceState) {
            Bundle b = getArguments();
            if (null != b) {
                getParamsFromBundle(b);
            }
        } else {
            getParamsFromBundle(savedInstanceState);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mRootView) {
            mRootView = inflater.inflate(mLayout, container, false);
        }
        ViewUtility.bind(this, mRootView);
        if (DEBUG) {
            log("onCreateView, View: " + mRootView);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (DEBUG) {
            log("onViewCreated, view: " + view);
        }
        getScreenSize();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (DEBUG) {
            log("onActivityCreated");
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (DEBUG) {
            log("onViewStateRestored, bundle: " + savedInstanceState);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (DEBUG) {
            log("onStart");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DEBUG) {
            log("onResume");
        }
        if (shouldSetDefaultTitleEvents()) {
            setDefaultTitleClickEvent();
        }
        doingInResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        ;
        if (DEBUG) {
            log("onPause");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (DEBUG) {
            log("onSaveInstanceState");
        }
        if (null == outState) outState = new Bundle();
        saveParamsToBundle(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (DEBUG) {
            log("onStop");
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (DEBUG) {
            log("onStop");
        }
    }

    @Override
    public void onDestroyView() {
        if (DEBUG) {
            log("onDestroyView");
        }
        destroyView();
        super.onDestroyView();
        clearDirectParent(mRootView);
    }

    @Override
    public void onDestroy() {
        if (DEBUG) {
            log("onDestroy");
        }
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        if (DEBUG) {
            log("onDetach");
        }
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 当前设备屏幕宽度像素
     */
    protected int mScreenWidth;
    /**
     * 当前设备屏幕高度像素
     */
    protected int mScreenHeight;

    /**
     * 获取当前设备的屏幕尺寸
     */
    private void getScreenSize() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
    }

    public int getScreenWidth() {
        return mScreenWidth;
    }

    public int getScreenHeight() {
        return mScreenHeight;
    }

    /**
     * 清空指定View的父级容器
     */
    public void clearDirectParent(View view) {
        if (null != view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (null != parent) {
                parent.removeView(view);
            }
        }
    }

    /**
     * 为指定的View开始背景景深变化的动画
     */
    protected void startTranslationAnimation(View view) {
        int max = getDimension(R.dimen.ui_static_dp_5);
        int min = getDimension(R.dimen.ui_static_dp_3);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationZ", max, min);
        animator.addListener(mAnimatorListenerAdapter);
        animator.setTarget(view);
        animator.start();
    }

    private AnimatorListenerAdapter mAnimatorListenerAdapter = new AnimatorListenerAdapter() {

        @Override
        public void onAnimationEnd(Animator animation) {
            ObjectAnimator oa = (ObjectAnimator) animation;
            onTranslationAnimationComplete((View) oa.getTarget());
        }
    };

    /**
     * 背景景深动画执行完毕
     */
    protected void onTranslationAnimationComplete(View view) {
    }
}
