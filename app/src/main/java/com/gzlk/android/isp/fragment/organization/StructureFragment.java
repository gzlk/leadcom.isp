package com.gzlk.android.isp.fragment.organization;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseTransparentSupportFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.SimpleClickableViewHolder;
import com.gzlk.android.isp.view.OrganizationConcerned;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;

/**
 * <b>功能描述：</b>组织架构<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/05 10:36 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/05 10:36 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class StructureFragment extends BaseTransparentSupportFragment {

    // View
    @ViewId(R.id.ui_organization_structure_concerned_container)
    private RelativeLayout viewPagerContainer;
    @ViewId(R.id.ui_organization_structure_concerned_list)
    private ViewPager viewPager;
    @ViewId(R.id.ui_organization_structure_concerned)
    private View concernedView;
    @ViewId(R.id.ui_organization_structure_interesting)
    private View interestedView;
    @ViewId(R.id.ui_organization_structure_squad_title)
    private View squadTitleView;
    @ViewId(R.id.ui_organization_structure_squad_none)
    private View squadNoneView;

    // Holder
    private SimpleClickableViewHolder concernedHolder, interestedHolder, squadTitleHolder, noneHolder;

    private DepthAdapter mAdapter;
    private String[] items;

    @Override
    public int getLayout() {
        return R.layout.fragment_organization_structure;
    }

    @Override
    public void doingInResume() {
        initializeAdapter();
        initializeHolders();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void destroyView() {

    }

    private void initializeHolders() {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_organization_structure_items);
        }
        if (null == concernedHolder) {
            concernedHolder = new SimpleClickableViewHolder(concernedView, this);
        }
        concernedHolder.showContent(format(items[0], ""));
        if (null == interestedHolder) {
            interestedHolder = new SimpleClickableViewHolder(interestedView, this);
        }
        interestedHolder.showContent(format(items[1], ""));
        if (null == squadTitleHolder) {
            squadTitleHolder = new SimpleClickableViewHolder(squadTitleView, this);
        }
        squadTitleHolder.showContent(format(items[2], 0));
        if (null == noneHolder) {
            noneHolder = new SimpleClickableViewHolder(squadNoneView, this);
        }
        noneHolder.showContent(format(items[3], "目前没有下级小组"));
    }

    @Click({R.id.ui_holder_view_squad_add_container})
    private void elementClick(View view) {
        switch (view.getId()) {
            case R.id.ui_holder_view_squad_add_container:
                // 添加小组
                ToastHelper.make().showMsg("添加小组");
                break;
        }
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

    private class DepthAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            OrganizationConcerned concerned = new OrganizationConcerned(viewPager.getContext());
            concerned.showContent(position);
            container.addView(concerned);
            return concerned;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            OrganizationConcerned concerned = (OrganizationConcerned) object;
            container.removeView(concerned);
        }
    }
}
