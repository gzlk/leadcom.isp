package com.leadcom.android.isp.fragment.common;

import android.animation.Animator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.user.CollectionRequest;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.fragment.base.BaseDownloadingUploadingSupportFragment;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.HttpHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.helper.popup.MomentMoreHelper;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.user.Collection;
import com.leadcom.android.isp.nim.file.FilePreviewHelper;
import com.leadcom.android.isp.share.ShareToQQ;
import com.leadcom.android.isp.share.ShareToWeiBo;
import com.leadcom.android.isp.share.ShareToWeiXin;
import com.leadcom.android.isp.task.CopyLocalFileTask;

import java.io.File;
import java.util.ArrayList;

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

public class ImageViewerFragment extends BaseDownloadingUploadingSupportFragment implements ViewPager.OnPageChangeListener {

    /**
     * 是否是显示已经收藏了的内容
     */
    public static boolean isCollected = false;
    protected static final String PARAM_SELECTED = "ivf_param_selected";

    public static ImageViewerFragment newInstance(Bundle bundle) {
        ImageViewerFragment ivf = new ImageViewerFragment();
        ivf.setArguments(bundle);
        return ivf;
    }

    private static Bundle getBundle(int selectedIndex, ArrayList<String> ids) {
        Bundle bundle = new Bundle();
        // 选中的图片
        bundle.putInt(PARAM_SELECTED, selectedIndex);
        // 要显示的图片列表
        bundle.putStringArrayList(PARAM_QUERY_ID, ids);
        return bundle;
    }

    public static void open(BaseFragment fragment, String url) {
        ArrayList<String> arr = new ArrayList<>();
        arr.add(url);
        open(fragment, 0, arr);
    }

    public static void open(BaseFragment fragment, int selectedIndex, ArrayList<String> urls) {
        fragment.openActivity(ImageViewerFragment.class.getName(), getBundle(selectedIndex, urls), false, false, true);
    }

    public static void open(Context context, String url) {
        ArrayList<String> arr = new ArrayList<>();
        arr.add(url);
        open(context, 0, arr);
    }

    public static void open(Context context, int selectedIndex, ArrayList<String> urls) {
        BaseActivity.openActivity(context, ImageViewerFragment.class.getName(), getBundle(selectedIndex, urls), false, false, true);
    }

    private int selectedIndex = 0;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        selectedIndex = bundle.getInt(PARAM_SELECTED, 0);
        images = bundle.getStringArrayList(PARAM_QUERY_ID);
        if (null == images) {
            images = new ArrayList<>();
        }
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_SELECTED, selectedIndex);
        bundle.putStringArrayList(PARAM_QUERY_ID, images);
    }

    @Override
    public void onDestroy() {
        isCollected = false;
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        INTERNAL_SHAREABLE = false;
        int color = getColor(R.color.textColorLight);
        titleLeftIcon.setTextColor(color);
        titleTextView.setTextColor(color);
        titleRightIcon.setTextColor(color);
    }

    @ViewId(R.id.ui_viewer_image_title_container)
    private LinearLayout titleContainer;
    @ViewId(R.id.ui_ui_custom_title_text)
    private TextView titleTextView;
    @ViewId(R.id.ui_ui_custom_title_left_icon)
    private CustomTextView titleLeftIcon;
    @ViewId(R.id.ui_ui_custom_title_left_text)
    private TextView titleLeftText;
    @ViewId(R.id.ui_ui_custom_title_right_icon)
    private CustomTextView titleRightIcon;
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
                showMoreDialog();
                break;
        }
    }

    private void showMoreDialog() {
        MomentMoreHelper.helper().init(this).setOnButtonClickListener(new MomentMoreHelper.OnButtonClickListener() {
            @Override
            public boolean onClick(View view) {
                handlePopupClick(view.getId());
                return true;
            }
        }).showPrivacy(false)
                .showShare(true)
                .showSave(true)
                .showDelete(false)
                .showFavorite(!isCollected)
                .show();
    }

    private void handlePopupClick(int id) {
        switch (id) {
            case R.id.ui_dialog_moment_details_button_favorite:
                // 收藏单张图片
                tryCollectImage();
                break;
            case R.id.ui_dialog_moment_details_button_share:
                openShareDialog();
                break;
            case R.id.ui_dialog_moment_details_button_save:
                // 保存单张图片到本地
                save();
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
        }).add(images.get(selectedIndex) + "#.jpg");
    }

    @Override
    protected void shareToQQ() {
        ShareToQQ.shareToQQ(ShareToQQ.TO_QQ, Activity(), "", "", "", images.get(selectedIndex), null);
    }

    @Override
    protected void shareToQZone() {
        ArrayList<String> img = new ArrayList<>();
        img.add(images.get(selectedIndex));
        ShareToQQ.shareToQQ(ShareToQQ.TO_QZONE, Activity(), StringHelper.getString(R.string.ui_base_share_title, "分享图片"), "分享图片c", "http://www.baidu.com", "", img);
    }

    @Override
    protected void shareToWeiXinSession() {
        ArrayList<String> img = new ArrayList<>();
        img.add(images.get(selectedIndex));
        ShareToWeiXin.shareToWeiXin(Activity(), ShareToWeiXin.TO_WX_SESSION, "分享图片t", "分享图片c", img);
    }

    @Override
    protected void shareToWeiXinTimeline() {
        ArrayList<String> img = new ArrayList<>();
        img.add(images.get(selectedIndex));
        ShareToWeiXin.shareToWeiXin(Activity(), ShareToWeiXin.TO_WX_TIMELINE, "分享图片t", "分享图片c", img);
    }

    @Override
    protected void shareToWeiBo() {
        ArrayList<String> img = new ArrayList<>();
        img.add(images.get(selectedIndex));
        ShareToWeiBo.init(Activity()).share("分享图片", img);
    }

    private void save() {
        String url = images.get(selectedIndex);
        String local = HttpHelper.helper().getLocalFilePath(url, App.IMAGE_DIR);
        File file = new File(local);
        if (file.exists()) {
            new CopyLocalFileTask().exec(url, local);
        } else {
            // 文件不存在则重新下载
            downloadFile(url);
        }
    }

    @Override
    protected void onFileDownloadingComplete(String url, String local, boolean success) {
        super.onFileDownloadingComplete(url, local, success);
        if (success) {
            // 下载成功之后重新另存到外置SD卡公共Picture目录
            save();
        }
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
            titleRightIcon.setText(R.string.ui_icon_more);
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

    private View.OnClickListener imageViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            animateTitle(titleContainer.getVisibility() != View.VISIBLE);
        }
    };

    private void animateTitle(final boolean shown) {
        float y = BaseActivity.getStatusHeight(Activity()) + Activity().getActionBarSize();
        titleContainer.animate().translationY(shown ? 0 : -y).alpha(shown ? 1 : -1).setDuration(duration()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (shown) {
                    titleContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!shown) {
                    titleContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    private ArrayList<String> images = new ArrayList<>();

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
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            String image = images.get(position);
            String ext = Attachment.getExtension(image);
            if (!isEmpty(ext) && ext.equals("gif")) {
                // 动图
                ImageView imageView = new ImageView(App.app());
                container.addView(imageView);
                Glide.with(ImageViewerFragment.this)
                        .load(image).into(imageView);
                return imageView;
            }
            final SubsamplingScaleImageView ssiv = new SubsamplingScaleImageView(App.app());
            container.addView(ssiv);
            Glide.with(ImageViewerFragment.this)
                    .load(images.get(position)).downloadOnly(new SimpleTarget<File>() {
                @Override
                public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                    ssiv.setImage(ImageSource.uri(FilePreviewHelper.getUriFromFile(resource.getAbsolutePath())));
                }
            });
            return ssiv;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}
