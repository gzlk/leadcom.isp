package com.gzlk.android.isp.fragment.common;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.user.CollectionRequest;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.base.BaseTransparentSupportFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.user.Collection;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CustomTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能描述：</b>图片预览<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/15 09:24 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/15 09:24 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ImageViewerFragment extends BaseTransparentSupportFragment implements ViewPager.OnPageChangeListener {

    protected static final String PARAM_SELECTED = "ivf_param_selected";

    public static ImageViewerFragment newInstance(String params) {
        ImageViewerFragment ivf = new ImageViewerFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        // 选中的图片
        bundle.putInt(PARAM_SELECTED, Integer.valueOf(strings[0]));
        // 要显示的图片列表
        bundle.putString(PARAM_QUERY_ID, StringHelper.replaceJson(strings[1], true));
        ivf.setArguments(bundle);
        return ivf;
    }

    public static void open(BaseFragment fragment, int selectedIndex, ArrayList<String> urls) {
        String json = StringHelper.replaceJson(Json.gson().toJson(urls, new TypeToken<ArrayList<String>>() {
        }.getType()), false);
        fragment.openActivity(ImageViewerFragment.class.getName(), format("%d,%s", selectedIndex, json), false, false, true);
    }

    private int selectedIndex = 0;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        selectedIndex = bundle.getInt(PARAM_SELECTED, 0);
        if (isEmpty(mQueryId)) {
            mQueryId = EMPTY_ARRAY;
        }
        if (mQueryId.charAt(0) == '[') {
            images = Json.gson().fromJson(StringHelper.replaceJson(mQueryId, true), new TypeToken<ArrayList<String>>() {
            }.getType());
            if (null == images) {
                images = new ArrayList<>();
            }
        } else {
            images.add(mQueryId);
        }
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_SELECTED, selectedIndex);
    }

    @ViewId(R.id.ui_viewer_image_title_container)
    private LinearLayout titleContainer;
    @ViewId(R.id.ui_ui_custom_title_text)
    private TextView titleTextView;
    @ViewId(R.id.ui_ui_custom_title_left_text)
    private TextView titleLeftText;
    @ViewId(R.id.ui_ui_custom_title_right_icon)
    private CustomTextView titleRightIcon;
    @ViewId(R.id.ui_ui_custom_title_right_text)
    private TextView titleRightText;
    @ViewId(R.id.ui_tool_view_pager)
    private ViewPager viewPager;

    private ImageAdapter mAdapter;

    @Override
    public int getLayout() {
        return R.layout.fragment_viewer_image;
    }

    @Click({R.id.ui_ui_custom_title_left_container, R.id.ui_ui_custom_title_right_container})
    private void elementClick(View view) {
        switch (view.getId()) {
            case R.id.ui_ui_custom_title_left_container:
                finish();
                break;
            case R.id.ui_ui_custom_title_right_container:
                // 收藏
                tryCollectImage();
                break;
        }
    }

    private void tryCollectImage() {
        if (images.size() < 1) return;
        CollectionRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Collection>() {
            @Override
            public void onResponse(Collection collection, boolean success, String message) {
                super.onResponse(collection, success, message);
                if (success) {
                    ToastHelper.make().showMsg(message);
                }
            }
        }).add(images.get(selectedIndex));
    }

    @Override
    public void doingInResume() {
        tryPaddingContent(titleContainer, false);
        titleLeftText.setText(null);
        titleTextView.setText(null);
        //titleRightIcon.setText(R.string.ui_icon_more);
        if (images.size() < 1) {
            ToastHelper.make().showMsg(R.string.ui_text_viewer_image_nothing);
        } else {
            titleRightText.setText(R.string.ui_base_text_favorite);
            initializeAdapter();
        }
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void destroyView() {

    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new ImageAdapter();
            viewPager.setAdapter(mAdapter);
            viewPager.addOnPageChangeListener(this);
            changePosition();
        }
        viewPager.setCurrentItem(selectedIndex, false);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        selectedIndex = position;
        changePosition();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private List<String> images = new ArrayList<>();

    private void changePosition() {
        if (images.size() > 1) {
            titleTextView.setText(format("%d/%d", selectedIndex + 1, images.size()));
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
        public Object instantiateItem(ViewGroup container, int position) {
            ImageDisplayer displayer = new ImageDisplayer(Activity());
            displayer.displayImage(images.get(position), getScreenWidth(), getScreenHeight(), false, false);
            container.addView(displayer);
            return displayer;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ImageDisplayer displayer = (ImageDisplayer) object;
            container.removeView(displayer);
        }
    }
}
