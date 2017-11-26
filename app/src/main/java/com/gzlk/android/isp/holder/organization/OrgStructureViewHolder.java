package com.gzlk.android.isp.holder.organization;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.organization.OrganizationPropertiesFragment;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.lib.DepthViewPager;
import com.gzlk.android.isp.lib.view.WrapContentViewPager;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.organization.Organization;
import com.gzlk.android.isp.view.OrganizationConcerned;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * <b>功能描述：</b>组织机构中已关注组织列表（画廊模式）<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/08 14:29 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/08 14:29 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class OrgStructureViewHolder extends BaseViewHolder {

    // View
    @ViewId(R.id.ui_organization_structure_concerned_container)
    private RelativeLayout viewPagerContainer;
    @ViewId(R.id.ui_organization_structure_concerned_blank)
    private TextView blankTextView;
    @ViewId(R.id.ui_organization_structure_concerned_list)
    private WrapContentViewPager viewPager;

    private DepthAdapter mAdapter;

    public OrgStructureViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        initializeAdapter();
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            viewPagerContainer.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (viewPager.getCurrentItem() > 0 || viewPager.getCurrentItem() < mAdapter.getCount() - 1) {
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return viewPager.dispatchTouchEvent(event);
                    } else {
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        return viewPager.dispatchTouchEvent(event);
                    }
                }
            });
            mAdapter = new DepthAdapter();
            viewPager.setOffscreenPageLimit(5);
            //viewPager.setPageTransformer(true, new DepthTransformer());
            viewPager.setPageTransformer(true, new ScaleDepthTransformer());
            viewPager.setPageMargin(-getDimension(R.dimen.ui_static_dp_40));
            viewPager.setAdapter(mAdapter);
        }
    }

    private DepthViewPager.OnPageChangeListener onPageChangeListener;

    public void setPageChangeListener(DepthViewPager.OnPageChangeListener listener) {
        onPageChangeListener = listener;
        viewPager.addOnPageChangeListener(onPageChangeListener);
    }

    public void setSelected(int selected) {
        viewPager.setCurrentItem(selected, true);
    }

    public int getSelected() {
        return viewPager.getCurrentItem();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class ScaleDepthTransformer implements DepthViewPager.PageTransformer {

        private int currentItem = 0;
        private boolean is3D = true;
        private static final int LEFT = -1, MIDDLE = 0, RIGHT = 1;

        @Override
        public void transformPage(View page, float position) {
            // 获取ViewPager当前显示的页面
            currentItem = viewPager.getCurrentItem();
            setPositionViewAnimation(page, position);
        }

        /**
         * 对View进行动画效果处理
         */
        private void setPositionViewAnimation(View view, float position) {
            //View所在页面
            int pos = (int) view.getTag(R.id.hlklib_ids_custom_view_click_tag);
            //是否在当前显示页的右边
            int location = getLocation(currentItem, pos);
            //缩放比例
            float scaleFactor = getScaleCoefficient(currentItem, pos);
            //位移比例
            float transFactor = getTranslationCoefficient(currentItem, pos);
            //位移距离
            float translationFactor = 10 * getTranslationSize(location, transFactor, position);
            //缩放大小
            float scale = getScaleSize(scaleFactor, position);
            //对View进行动画处理
            //view.setAlpha(0.5f + (scale - scaleFactor) / (1 - scaleFactor) * (1 - 0.5f));
            view.setScaleX(scale);
            view.setScaleY(scale);
            //setViewRotation(view, location);
            view.setTranslationX(translationFactor);
        }

        /**
         * 设置view旋转角度（3D效果）
         */
        public void setViewRotation(View view, int location) {
            if (!is3D) {
                return;
            }
            if (location == LEFT) {
                view.setRotationY(28);
            } else if (location == RIGHT) {
                view.setRotationY(-28);
            } else {
                view.setRotationY(0);
            }
        }

        /**
         * 获取页面所在的位置
         * 当前页的右边、左边或者是当前页
         *
         * @param position    view所在页面
         * @param currentItem Viewpager当前显示的页面
         */
        private int getLocation(int currentItem, int position) {
            if (position == currentItem) {
                return MIDDLE;
            } else if (position < currentItem) {
                return LEFT;
            } else {
                return RIGHT;
            }
        }

        /**
         * 获取缩放比例系数
         *
         * @param position    view所在页面
         * @param currentItem Viewpager当前显示的页面
         */
        private float getScaleCoefficient(int currentItem, int position) {
            //右左边相邻的第1个Item
            if (position == currentItem - 1 || position == currentItem + 1) {
                return 0.8f;
            }
            //右左边相邻的第2个Item
            else if (position == currentItem - 2 || position == currentItem + 2) {
                return 0.6f;
            }
            //右左边相邻的第3个Item
            else if (position == currentItem - 3 || position == currentItem + 3) {
                return 0.4f;
            }
            //当前显示的item
            else {
                return 0.8f;
            }
        }

        /**
         * 获取缩放大小
         * <p>
         * 注：这里的float position，参数对应transformPage方法中的参数，因为我们要实现的效果是慢慢缩小或者，慢慢放大，所以缩放的最终大小在滑动的过程中是不固定的，所以需要根据该参数来计算。
         * </p>
         *
         * @param max
         * @param position
         */
        private float getScaleSize(float max, float position) {
            return Math.max(max, 1 - Math.abs(position));
        }

        /**
         * 获取偏移量比例系数
         *
         * @param position    view所在页面
         * @param currentItem Viewpager当前显示的页面
         */
        private float getTranslationCoefficient(int currentItem, int position) {
            //右左边相邻的第1个Item
            if (position == currentItem - 1 || position == currentItem + 1) {
                return 1.2f;
            }
            //右左边相邻的第2个Item
            else if (position == currentItem - 2 || position == currentItem + 2) {
                return 2.5f;
            }
            //右左边相邻的第3个Item
            else if (position == currentItem - 3 || position == currentItem + 3) {
                return 4f;
            }
            //当前显示的item
            else {
                return 0f;
            }
        }

        /**
         * 获计算最小偏移量
         *
         * @param location 页面所在位置
         * @param min
         * @param position viewpager滑动时 区间数值变化
         */
        private float getTranslationSize(int location, float min, float position) {
            if (location == RIGHT) {
                return -Math.min(min, min * Math.abs(position));
            }
            return Math.min(min, min * Math.abs(position));
        }
    }

    private class DepthTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.8f;
        private final int margin = getDimension(R.dimen.ui_static_dp_25);

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void transformPage(View page, float position) {
            float scaleFactor;
            if (position < -1) {//看不到的一页 *
                scaleFactor = (1 - MIN_SCALE) * (0 - position);
                page.setScaleX(1 - scaleFactor);
                page.setScaleY(1 - scaleFactor);
                page.setTranslationZ(1 - scaleFactor);
                //page.setTranslationX(margin);
            } else if (position <= 1) {
                if (position < 0) {//滑出的页 0.0 ~ -1 *
                    scaleFactor = (1 - MIN_SCALE) * (0 - position);
                    page.setScaleX(1 - scaleFactor);
                    page.setScaleY(1 - scaleFactor);
                    page.setTranslationZ(1 - scaleFactor);
                    //float marg = margin * (0 - position);
                    //page.setTranslationX(marg);
                } else {//滑进的页 1 ~ 0.0 *
                    scaleFactor = (1 - MIN_SCALE) * (1 - position);
                    page.setScaleX(MIN_SCALE + scaleFactor);
                    page.setScaleY(MIN_SCALE + scaleFactor);
                    page.setTranslationZ(MIN_SCALE + scaleFactor);
                    //float marg = -(margin * (1 - position));
                    //page.setTranslationX(marg);
                }
            } else {//看不到的另一页 *
                scaleFactor = (1 - MIN_SCALE) * (1 - position);
                page.setScaleX(MIN_SCALE + scaleFactor);
                page.setScaleY(MIN_SCALE + scaleFactor);
                page.setTranslationZ(MIN_SCALE + scaleFactor);
                //page.setTranslationX(-margin);
            }
        }
    }

    private List<Organization> organizations = new ArrayList<>();

    /**
     * 尝试加载本地群列表
     */
    public void loadingLocal() {
        List<Organization> temp = new Dao<>(Organization.class).query();
        if (null != temp && temp.size() > 0) {
            add(temp);
        }
    }

    /**
     * 获取index处的组织详细信息
     */
    public Organization get(int position) {
        if (position >= 0 && position < organizations.size()) {
            return organizations.get(position);
        }
        return null;
    }

    /**
     * 指定id的组织是否是我关注的组织
     */
    public boolean isConcerned(String id) {
        for (Organization org : organizations) {
            if (org.getId().equals(id) && org.isConcerned()) {
                return true;
            }
        }
        return false;
    }

    public void add(List<Organization> list) {
        blankTextView.setVisibility(View.GONE);
        for (Organization organization : list) {
            if (!organizations.contains(organization)) {
                organizations.add(organization);
            } else {
                int index = organizations.indexOf(organization);
                organizations.set(index, organization);
            }
        }
        mAdapter.notifyDataSetChanged();
        // 第一次加载时自动触发pageChange事件
        if (null != onPageChangeListener) {
            viewPager.post(new Runnable() {
                @Override
                public void run() {
                    onPageChangeListener.onPageSelected(viewPager.getCurrentItem());
                }
            });
        }
    }

    private HashMap<String, OrganizationConcerned> map = new HashMap<>();

    private class DepthAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return organizations.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        private void clearParent(View view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (null != parent) {
                parent.removeView(view);
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Organization org = organizations.get(position);
            String id = org.getId();
            OrganizationConcerned concerned;
            if (map.containsKey(id)) {
                concerned = map.get(id);
            } else {
                concerned = new OrganizationConcerned(viewPager.getContext());
                concerned.setContentLayout(R.layout.tool_view_organziation_concerned_pager);
                concerned.setOnContainerClickListener(containerClickListener);
                concerned.showOrganization(org);
                concerned.setTag(R.id.hlklib_ids_custom_view_click_tag, position);
                map.put(id, concerned);
            }
            clearParent(concerned);
            container.addView(concerned);
            return concerned;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            OrganizationConcerned concerned = (OrganizationConcerned) object;
            container.removeView(concerned);
        }
    }

    private OrganizationConcerned.OnContainerClickListener containerClickListener = new OrganizationConcerned.OnContainerClickListener() {
        @Override
        public void onClick(OrganizationConcerned concerned, String id) {
            openActivity(OrganizationPropertiesFragment.class.getName(), id, false, false, true);
        }
    };
}
