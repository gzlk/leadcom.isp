package com.gzlk.android.isp.fragment.individual;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseTransparentSupportFragment;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CustomTextView;

import java.util.List;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/17 10:46 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/17 10:46 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MomentDetailsFragment extends BaseTransparentSupportFragment {

    private static final String PARAM_ID = "mdf_moment_id";
    private static final String PARAM_SELECTED = "mdf_moment_selected";

    public static MomentDetailsFragment newInstance(String params) {
        MomentDetailsFragment mdf = new MomentDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_ID, params);
        mdf.setArguments(bundle);
        return mdf;
    }

    private int selected;
    private String queryId;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        selected = bundle.getInt(PARAM_SELECTED, 0);
        queryId = bundle.getString(PARAM_ID, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_SELECTED, selected);
        bundle.putString(PARAM_ID, queryId);
    }

    // UI
    @ViewId(R.id.ui_moment_details_title_container)
    private LinearLayout titleContainer;
    @ViewId(R.id.ui_ui_custom_title_text)
    private TextView titleTextView;
    @ViewId(R.id.ui_ui_custom_title_right_icon)
    private CustomTextView titleRightIcon;
    @ViewId(R.id.ui_moment_details_title_image_counter)
    private TextView titleCountTextView;
    @ViewId(R.id.ui_moment_details_image_content)
    private ViewPager detailImageContent;
    @ViewId(R.id.ui_moment_detail_content_toggle)
    private TextView toggleTextView;
    @ViewId(R.id.ui_moment_detail_content_text)
    private TextView detailContentTextView;

    private List<String> images;

    @Override
    public int getLayout() {
        return R.layout.fragment_moment_details;
    }

    @Override
    public void doingInResume() {
        tryPaddingContent(titleContainer, false);
        titleRightIcon.setText(R.string.ui_icon_more);
        if (null == images) {
            String json = StringHelper.getString(R.string.temp_json_moment_list);
            images = Json.gson().fromJson(json, new TypeToken<List<String>>() {
            }.getType());
        }
        titleTextView.setText("动态详情");
        detailContentTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        initializeAdapter();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void destroyView() {

    }

    @Click({R.id.ui_ui_custom_title_left_container, R.id.ui_ui_custom_title_right_container})
    private void elementClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ui_ui_custom_title_left_container:
                finish();
                break;
            case R.id.ui_ui_custom_title_right_container:
                showMoreButtons();
                break;
        }
    }

    private View dialogView;

    private void showMoreButtons() {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == dialogView) {
                    dialogView = View.inflate(Activity(), R.layout.popup_dialog_moment_details, null);
                }
                return dialogView;
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {

            }
        }).addOnEventHandlerListener(new DialogHelper.OnEventHandlerListener() {
            @Override
            public int[] clickEventHandleIds() {
                return new int[]{R.id.ui_dialog_moment_details_button_privacy,
                        R.id.ui_dialog_moment_details_button_favorite,
                        R.id.ui_dialog_moment_details_button_save,
                        R.id.ui_dialog_moment_details_button_delete};
            }

            @Override
            public boolean onClick(View view) {
                return false;
            }
        }).setPopupType(DialogHelper.TYPE_SLID).show();
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            detailImageContent.addOnPageChangeListener(mOnPageChangeListener);
            mAdapter = new MomentDetailsAdapter();
            detailImageContent.setAdapter(mAdapter);
        }
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            changedPosition(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void changedPosition(int position) {
        titleCountTextView.setText(format("%d/%d", position + 1, images.size()));
    }

    private MomentDetailsAdapter mAdapter;

    private class MomentDetailsAdapter extends PagerAdapter {

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
