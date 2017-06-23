package com.gzlk.android.isp.holder.home;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.common.FocusImage;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;

import java.util.ArrayList;
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

    @ViewId(R.id.ui_holder_view_home_images)
    private RelativeLayout container;
    @ViewId(R.id.ui_tool_view_pager_embedded)
    private ViewPager viewPager;
    @ViewId(R.id.ui_holder_view_home_image_title)
    private TextView titleView;
    @ViewId(R.id.ui_holder_view_home_image_indicator)
    private LinearLayout indicator;

    /**
     * 默认图片宽高尺寸
     */
    private static final int WIDTH = 750, HEIGHT = 370;
    private int currentPosition = 0;
    private int imageHeight, imageWidth;

    public HomeImagesViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        imageWidth = fragment.getScreenWidth();
        calculateImageHeight();
        initialize();
    }

    /**
     * 根据手机屏幕分辨率计算焦点图的高度
     */
    private void calculateImageHeight() {
        float scale = (imageWidth * 1.0F / WIDTH);
        imageHeight = (int) (HEIGHT * scale);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) container.getLayoutParams();
        params.height = imageHeight;
        container.setLayoutParams(params);
    }

    private void initialize() {
        if (null == mAdapter) {
            mAdapter = new ImageAdapter();
            viewPager.setAdapter(mAdapter);
            viewPager.addOnPageChangeListener(this);
        }
    }

    public void addImages(List<FocusImage> list) {
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
        if (urls.size() > 0) {
            container.setVisibility(View.VISIBLE);
            viewPager.setCurrentItem(urls.size() > 1 ? 1 : 0, false);
            if (urls.size() == 1) {
                onPageSelected(0);
            }
        } else {
            container.setVisibility(View.GONE);
        }
    }

    private ImageAdapter mAdapter;
    private ArrayList<FocusImage> urls = new ArrayList<>();
    private ArrayList<ImageDisplayer> images = new ArrayList<>();

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPosition = position;
        titleView.setText(urls.get(currentPosition).getTitle());
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

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageDisplayer displayer = images.get(position);
            displayer.displayImage(urls.get(position).getImageUrl(), imageWidth, imageHeight, false, false);
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
