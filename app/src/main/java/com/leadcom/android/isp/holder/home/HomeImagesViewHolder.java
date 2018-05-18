package com.leadcom.android.isp.holder.home;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.model.archive.Archive;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;

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
    private int imageHeight, imageWidth, dotPadding;
    private ArrayList<CustomTextView> dots = new ArrayList<>();

    public HomeImagesViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        imageWidth = fragment.getScreenWidth();
        dotPadding = getDimension(R.dimen.ui_static_dp_2);
        calculateImageHeight();
        initialize();
        //initializeTitlePosition();
    }

    /**
     * 根据手机屏幕分辨率计算焦点图的高度
     */
    private void calculateImageHeight() {
        float scale = (imageWidth * 1.0F / WIDTH);
        imageHeight = imageWidth * 2 / 5;//(int) (HEIGHT * scale);
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

    private void initializeTitlePosition() {
        int titleBarHeight = fragment().Activity().getActionBarSize();
        int statusHeight = BaseActivity.getStatusHeight(fragment().Activity());
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) titleView.getLayoutParams();
        params.topMargin = titleBarHeight + statusHeight;
        titleView.setLayoutParams(params);
    }

    public void addImages(List<Archive> list) {
        if (null != list && list.size() > 0) {
            indicator.removeAllViews();
            dots.clear();
            archives.clear();
            images.clear();
            archives.addAll(list);
            int size = list.size();
            if (size > 1) {
                archives.add(0, list.get(size - 1));
                archives.add(list.get(0));
            }
            for (int i = 0; i < archives.size(); i++) {
                // 图片
                ImageDisplayer imageDisplayer = new ImageDisplayer(viewPager.getContext());
                imageDisplayer.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
                images.add(imageDisplayer);
                // 指示器
                CustomTextView dot = new CustomTextView(viewPager.getContext());
                dots.add(dot);
                // 第一个和最后一个看不见，其余的可以看见
                //if (i == 0 || i == size - 1) {
                dot.setVisibility(View.INVISIBLE);
                //}
                dot.setText(StringHelper.getString(R.string.ui_icon_radio_disabled));
                dot.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimension(R.dimen.ui_base_text_size_micro));
                dot.setTextColor(getColor(R.color.textColorHintDark));
                indicator.addView(dot);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dot.getLayoutParams();
                params.rightMargin = dotPadding;
                dot.setLayoutParams(params);
            }
        }
        mAdapter.notifyDataSetChanged();
        if (archives.size() > 0) {
            container.setVisibility(View.VISIBLE);
            viewPager.setCurrentItem(archives.size() > 1 ? 1 : 0, false);
            if (archives.size() == 1) {
                onPageSelected(0);
            }
        } else {
            container.setVisibility(View.GONE);
        }
    }

    private ImageAdapter mAdapter;
    private ArrayList<Archive> archives = new ArrayList<>();
    private ArrayList<ImageDisplayer> images = new ArrayList<>();

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPosition = position;
        titleView.setText(archives.get(currentPosition).getTitle());
        titleView.setEllipsize(TextUtils.TruncateAt.END);
        titleView.setSelected(true);
        Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                titleView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            }
        }, fragment().duration());
        //colorChange(position);
        changeDotsColor(position, getColor(R.color.textColorHintDark));
    }

    private void changeDotsColor(int position, int color) {
        for (int i = 0, len = dots.size(); i < len; i++) {
            dots.get(i).setVisibility((i > 0 && i < len - 1) ? View.VISIBLE : View.INVISIBLE);
            if (dots.get(i).getVisibility() == View.VISIBLE) {
                dots.get(i).setTextColor(i == position ? Color.WHITE : color);
            }
        }
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
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(images.get(position));
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageDisplayer displayer = images.get(position);
            Archive archive = archives.get(position);
            String cover = archive.getCover();
            // 有封面显示封面
            if (isEmpty(cover)) {
                int size = archive.getImage().size();
                // 有图片显示第一张图片
                if (size > 0) {
                    cover = archive.getImage().get(0).getUrl();
                } else {
                    // 没有图片设置为档案的id
                    cover = archive.getId();
                }
            }
            displayer.displayImage(cover, imageWidth, imageHeight, false, false);
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
