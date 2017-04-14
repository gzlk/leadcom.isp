package com.gzlk.android.isp.fragment.individual;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseTransparentSupportFragment;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredView;

/**
 * <b>功能描述：</b>我的二维码页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/14 13:42 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/14 13:42 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class QRCodeFragment extends BaseTransparentSupportFragment {

    @ViewId(R.id.ui_2d_code_title_container)
    private LinearLayout titleContainer;
    @ViewId(R.id.ui_ui_custom_title_text)
    private TextView titleText;
    @ViewId(R.id.ui_ui_custom_title_right_text)
    private TextView rightTextView;
    @ViewId(R.id.ui_2d_code_background)
    private LinearLayout background;
    @ViewId(R.id.ui_2d_code_buttons)
    private CorneredView buttons;

    @Override
    public int getLayout() {
        return R.layout.fragment_individual_2d_code;
    }

    @Override
    public void doingInResume() {
        tryPaddingContent(titleContainer, false);
        initializeElements();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void destroyView() {

    }

    @Click({R.id.ui_ui_custom_title_left_container, R.id.ui_ui_custom_title_right_container,
            R.id.ui_2d_code_button_scanner, R.id.ui_2d_code_button_cancel})
    private void elementClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ui_ui_custom_title_left_container:
                finish();
                break;
            case R.id.ui_ui_custom_title_right_container:
                showButtons(true);
                break;
            case R.id.ui_2d_code_button_scanner:
                break;
            case R.id.ui_2d_code_button_cancel:
                showButtons(false);
                break;
        }
    }

    private void initializeElements() {
        if (buttons.getVisibility() == View.GONE) {
            rightTextView.setText(R.string.ui_text_2d_code_right_title);
            titleText.setText(R.string.ui_text_2d_code_fragment_title);
            buttons.animate().alpha(0).translationY(buttons.getHeight() * 1.1f).setDuration(0).start();
        }
    }

    private void showButtons(final boolean show) {
        buttons.animate().setDuration(duration()).alpha(show ? 1 : 0).translationY(show ? 0 : buttons.getHeight())
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (!show) {
                            buttons.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        if (show) {
                            buttons.setVisibility(View.VISIBLE);
                        }
                    }
                }).start();
        showBackground(show);
    }

    private void showBackground(final boolean show) {
        background.animate().setDuration(duration()).alpha(show ? 1 : 0)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (!show) {
                            background.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        if (show) {
                            background.setVisibility(View.VISIBLE);
                        }
                    }
                }).start();
    }
}
