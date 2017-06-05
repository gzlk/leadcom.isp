package com.gzlk.android.isp.holder.home;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <b>功能描述：</b>首页会议中的图片轮播<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/02 15:45 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/02 15:45 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class HomeImagesViewHolder extends BaseViewHolder implements ViewPager.OnPageChangeListener {

    @ViewId(R.id.ui_tool_view_pager_embedded)
    private ViewPager viewPager;
    @ViewId(R.id.ui_holder_view_home_image_indicator)
    private LinearLayout indicator;

    private int currentPosition = 0;

    public HomeImagesViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        initialize();
    }

    private void initialize() {
        if (null == mAdapter) {
            mAdapter = new ImageAdapter();
            viewPager.setAdapter(mAdapter);
            viewPager.addOnPageChangeListener(this);
        }
    }

    public void addImages(List<String> list) {
        if (null != list && list.size() > 0) {
            urls.clear();
            images.clear();
            urls.addAll(list);
            int size = list.size();
            if (size > 1) {
                urls.add(0, list.get(size - 1));
                urls.add(list.get(0));
            }
            for (int i = 0; i < urls.size(); i++) {
                ImageDisplayer imageDisplayer = new ImageDisplayer(viewPager.getContext());
                imageDisplayer.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                images.add(imageDisplayer);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private ImageAdapter mAdapter;
    private ArrayList<String> urls = new ArrayList<>();
    private ArrayList<ImageDisplayer> images = new ArrayList<>();

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPosition = position;
//        if (urls.size() > 1) {
//            if (position < 1) {
//                position = urls.size() - 1;
//                viewPager.setCurrentItem(position, false);
//            } else if (position > urls.size() - 2) {
//                position = 1;
//                viewPager.setCurrentItem(position, false);
//            }
//        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // 若viewpager滑动未停止，直接返回
        if (state != ViewPager.SCROLL_STATE_IDLE) return;
        // 若当前为第一张，设置页面为倒数第二张
        if (currentPosition == 0) {
            viewPager.setCurrentItem(images.size() - 2, false);
        } else if (currentPosition == images.size() - 1) {
            //若当前为倒数第一张，设置页面为第二张
            viewPager.setCurrentItem(1, false);
        }
    }

    private class ImageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(images.get(position));
        }

        private void clearParent(View view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (null != parent) {
                parent.removeView(view);
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageDisplayer displayer = images.get(position);
            displayer.displayImage(urls.get(position), fragment().getScreenWidth(), getDimension(R.dimen.ui_static_dp_140), false, false);
            displayer.addOnImageClickListener(onImageClickListener);
            container.addView(displayer);
            return displayer;
        }
    }

    private ImageDisplayer.OnImageClickListener onImageClickListener;

    public void setOnImageClickListener(ImageDisplayer.OnImageClickListener l) {
        onImageClickListener = l;
    }
}
