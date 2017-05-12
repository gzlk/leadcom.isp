package com.gzlk.android.isp.holder;

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
import com.gzlk.android.isp.fragment.organization.OrganizationDetailsFragment;
import com.gzlk.android.isp.lib.view.WrapContentViewPager;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.organization.Organization;
import com.gzlk.android.isp.view.OrganizationConcerned;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;

import java.util.ArrayList;
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

public class OrganizationStructureConcernedViewHolder extends BaseViewHolder {

    // View
    @ViewId(R.id.ui_organization_structure_concerned_container)
    private RelativeLayout viewPagerContainer;
    @ViewId(R.id.ui_organization_structure_concerned_blank)
    private TextView blankTextView;
    @ViewId(R.id.ui_organization_structure_concerned_list)
    private WrapContentViewPager viewPager;

    private DepthAdapter mAdapter;

    public OrganizationStructureConcernedViewHolder(View itemView, BaseFragment fragment) {
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
            viewPager.setPageTransformer(true, new DepthTransformer());
            viewPager.setPageMargin(-getDimension(R.dimen.ui_static_dp_40));
            viewPager.setAdapter(mAdapter);
        }
    }

    private ViewPager.OnPageChangeListener onPageChangeListener;

    public void setPageChangeListener(ViewPager.OnPageChangeListener listener) {
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
    private class DepthTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.8f;
        private final int margin = getDimension(R.dimen.ui_static_dp_25);

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

    private boolean firstInitialize = true;

    public void add(List<Organization> list) {
        blankTextView.setVisibility(View.GONE);
        for (Organization organization : list) {
            if (!organizations.contains(organization)) {
                organizations.add(organization);
            }
        }
        mAdapter.notifyDataSetChanged();
        // 第一次加载时自动触发pageChange事件
        if (firstInitialize) {
            if (null != onPageChangeListener) {
                firstInitialize = false;
                viewPager.post(new Runnable() {
                    @Override
                    public void run() {
                        onPageChangeListener.onPageSelected(viewPager.getCurrentItem());
                    }
                });
            }
        }
    }

    private class DepthAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return organizations.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            OrganizationConcerned concerned = new OrganizationConcerned(viewPager.getContext());
            concerned.setOnContainerClickListener(containerClickListener);
            concerned.showOrganization(organizations.get(position));
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
        public void onClick(int position) {
            openActivity(OrganizationDetailsFragment.class.getName(), String.valueOf(position), false, false, true);
        }
    };
}
