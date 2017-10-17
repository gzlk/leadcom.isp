package com.gzlk.android.isp.fragment.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import com.gzlk.android.isp.R;
import com.hlk.hlklib.lib.inject.ViewId;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能描述：</b>提供ViewPager的fragment<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2016/12/28 12:41 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2016/12/28 12:41 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class BaseViewPagerSupportFragment extends BaseTransparentSupportFragment {

    protected static final String PARAM_SELECTED_INDEX = "bvpf_param_selected_index";
    private static final String PARAM_REMOVABLE = "bvpf_param_removable";

    // UI
    @ViewId(R.id.ui_tool_view_pager)
    public ViewPager mViewPager;
    @ViewId(R.id.ui_tool_view_pager_embedded)
    public ViewPager mViewPagerEmbedded;

    // Data
    /**
     * 当前选中的页面index
     */
    private int viewPagerSelectedPage = 0;
    protected List<BaseTransparentSupportFragment> mFragments = new ArrayList<>();
    private FragmentAdapter mAdapter;
    /**
     * 是否可以动态添加、删除fragment
     */
    protected boolean isRemovable = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragments.clear();
        initializeFragments();
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        viewPagerSelectedPage = bundle.getInt(PARAM_SELECTED_INDEX, 0);
        isRemovable = bundle.getBoolean(PARAM_REMOVABLE, false);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        bundle.putInt(PARAM_SELECTED_INDEX, viewPagerSelectedPage);
        bundle.putBoolean(PARAM_REMOVABLE, isRemovable);
        super.saveParamsToBundle(bundle);
    }

    @Override
    protected void destroyView() {

    }

    @Override
    public void doingInResume() {
        if (null == mRootView || null == mRootView.getParent()) {
            mFragments.clear();
            // 重新初始化fragment列表
            initializeFragments();
        }
        initializeViewPagerAdapter();
    }

    /**
     * 初始化ViewPager的适配器
     */
    private void initializeViewPagerAdapter() {
        if (null == mAdapter) {
            mAdapter = new FragmentAdapter(getChildFragmentManager());
            if (null != mViewPager) {
                mViewPager.setAdapter(mAdapter);
                mViewPager.addOnPageChangeListener(mOnPageChangeListener);
                mViewPager.setCurrentItem(viewPagerSelectedPage, true);
            } else if (null != mViewPagerEmbedded) {
                mViewPagerEmbedded.setAdapter(mAdapter);
                mViewPagerEmbedded.addOnPageChangeListener(mOnPageChangeListener);
                mViewPagerEmbedded.setCurrentItem(viewPagerSelectedPage, true);
            }
        } else {
            mAdapter.notifyDataSetChanged();
        }
        viewPagerSelectionChanged(viewPagerSelectedPage);
    }

    /**
     * 设置默认要显示的页
     */
    protected void setDefaultPage(int defaultPage) {
        viewPagerSelectedPage = defaultPage;
    }

    /**
     * 获取当前显示的页码
     */
    protected int getDisplayedPage() {
        return viewPagerSelectedPage;
    }

    /**
     * 设置需要显示的页码
     */
    protected void setDisplayPage(int position) {
        if (viewPagerSelectedPage != position && position < mFragments.size()) {
            if (null != mViewPager) {
                mViewPager.setCurrentItem(position, true);
            }
            if (null != mViewPagerEmbedded) {
                mViewPagerEmbedded.setCurrentItem(position, true);
            }
        }
    }

    /**
     * 初始化fragment列表
     */
    protected abstract void initializeFragments();

    /**
     * ViewPager当前选中的index更改了
     */
    protected abstract void viewPagerSelectionChanged(int position);

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            viewPagerSelectedPage = position;
            viewPagerSelectionChanged(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * 增加一个 fragment
     */
    protected void addFragment(BaseTransparentSupportFragment fragment) {
        mFragments.add(fragment);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 删除指定 index 的 fragment
     */
    protected void removeFragment(int index) {
        mFragments.remove(index);
        //mAdapter.remove(index);
        mAdapter.notifyDataSetChanged();
    }

    private class FragmentAdapter extends FragmentPagerAdapter {

        FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public long getItemId(int position) {
            // 获取当前数据的hashCode，重写此方法以此为id动态添加、删除fragment
            return mFragments.get(position).hashCode();
        }

    }

}
