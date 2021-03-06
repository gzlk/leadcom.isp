package com.leadcom.android.isp.fragment.individual.moment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.hlk.hlklib.lib.emoji.EmojiUtility;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.FilePreviewHelper;
import com.leadcom.android.isp.helper.HttpHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.popup.MomentMoreHelper;
import com.leadcom.android.isp.lib.view.ExpandableView;
import com.leadcom.android.isp.listener.OnTaskCompleteListener;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.Comment;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.common.Seclusion;
import com.leadcom.android.isp.model.user.Moment;
import com.leadcom.android.isp.share.ShareToQQ;
import com.leadcom.android.isp.share.ShareToWeiXin;
import com.leadcom.android.isp.share.Shareable;
import com.leadcom.android.isp.task.CopyLocalFileTask;

import java.io.File;
import java.util.ArrayList;

/**
 * <b>功能描述：</b>说说中的图片列表(有图片时打开本页面)<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/17 10:46 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/17 10:46 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MomentImagesFragment extends BaseMomentFragment {

    /**
     * 是否是显示已经收藏了的内容
     */
    public static boolean isCollected = false;
    private static final String PARAM_SELECTED = "mdf_moment_selected";
    private static final String PARAM_USER_ID = "mdf_moment_user_id";
    private static final String PARAM_USER_NAME = "mdf_moment_user_name";

    public static MomentImagesFragment newInstance(Bundle bundle) {
        MomentImagesFragment mdf = new MomentImagesFragment();
        mdf.setArguments(bundle);
        return mdf;
    }

    private static Bundle getBundle(String momentId, int selectedIndex) {
        Bundle bundle = new Bundle();
        // 动态的id
        bundle.putString(PARAM_QUERY_ID, momentId);
        // 选中的图片索引
        bundle.putInt(PARAM_SELECTED, selectedIndex);
        return bundle;
    }

    public static void open(BaseFragment fragment, Moment moment) {
        Bundle bundle = getBundle(moment.getId(), 0);
        bundle.putSerializable(PARAM_MOMENT, moment);
        fragment.openActivity(MomentImagesFragment.class.getName(), bundle, REQUEST_DELETE, true, false);
    }

    public static void open(BaseFragment fragment, String momentId, int displayIndex) {
        fragment.openActivity(MomentImagesFragment.class.getName(), getBundle(momentId, displayIndex), REQUEST_DELETE, true, false);
    }

    private int selected;
    private String momentUser = "", momentName = "";

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        selected = bundle.getInt(PARAM_SELECTED, 0);
        momentUser = bundle.getString(PARAM_USER_ID, "");
        momentName = bundle.getString(PARAM_USER_NAME, "");
        mMoment = (Moment) bundle.getSerializable(PARAM_MOMENT);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_SELECTED, selected);
        bundle.putString(PARAM_USER_ID, momentUser);
        bundle.putString(PARAM_USER_NAME, momentName);
        bundle.putSerializable(PARAM_MOMENT, mMoment);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        INTERNAL_SHAREABLE = false;
        contentTextView.setOnExpandStateChangeListener(new ExpandableView.OnExpandStateChangeListener() {

            @Override
            public void onStateChanged(int state) {
                boolean isExpanded = state == ExpandableView.STATE_EXPANDED;
                indicator.setVisibility(state <= ExpandableView.STATE_NOT_OVERFLOW ? View.GONE : View.VISIBLE);
                indicator.animate()
                        .rotation(isExpanded ? 90 : -90)
                        .setDuration(duration())
                        .start();
            }
        });
    }

    @Override
    public void onDestroy() {
        isCollected = false;
        super.onDestroy();
    }

    // UI
    @ViewId(R.id.ui_tool_view_pager)
    private ViewPager imageViewPager;
    @ViewId(R.id.ui_moment_detail_content_text)
    private ExpandableView contentTextView;
    @ViewId(R.id.expandable_view_handler_indicator)
    private View indicator;
    // 附加UI
    @ViewId(R.id.ui_moment_detail_praise_icon)
    private CustomTextView praiseIcon;
    @ViewId(R.id.ui_moment_detail_praise_text)
    private TextView praiseText;
    @ViewId(R.id.ui_moment_detail_praise_num)
    private TextView praiseNum;
    @ViewId(R.id.ui_moment_detail_comment_num)
    private TextView commentNum;

    private ArrayList<String> images;

    @Override
    public int getLayout() {
        return R.layout.fragment_individual_moment_images;
    }

    @Override
    public void doingInResume() {
        setRightIcon(R.string.ui_icon_more);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                showMoreButtons();
            }
        });
        if (null == images) {
            images = new ArrayList<>();
            displayMomentDetails();
        }
        initializeAdapter();
    }

    private void displayMomentDetails() {
        if (null == mMoment || isEmpty(mMoment.getId())) {
            fetchingMoment();
        } else {
            momentUser = mMoment.getUserId();
            momentName = mMoment.getUserName();
            for (String string : mMoment.getImage()) {
                if (!images.contains(string)) {
                    images.add(string);
                }
            }
            if (null != mAdapter) {
                mAdapter.notifyDataSetChanged();
                imageViewPager.setCurrentItem(selected, true);
                changedPosition(selected);
            }
            setCustomTitle(formatDate(mMoment.getCreateDate(), R.string.ui_base_text_date_time_format_chs_hhmm));
            boolean empty = isEmpty(mMoment.getContent());
            if (!empty) {
                String content = mMoment.getContent().replace("\n", "<br/>");
                contentTextView.setText(EmojiUtility.getEmojiString(contentTextView.getContext(), content, true));
            } else {
                contentTextView.setVisibility(View.GONE);
                indicator.setVisibility(View.GONE);
            }
            resetPraiseStatus();
        }
    }

    @Override
    protected void onFetchingMomentComplete(Moment moment, boolean success) {
        if (success) {
            // 拉取回来之后立即显示
            mMoment = moment;
            displayMomentDetails();
        }
    }

    @Override
    protected void onDeleteMomentComplete(Moment moment, boolean success, String message) {
        if (success) {
            // 本地已删除
            Dao<Moment> dao = new Dao<>(Moment.class);
            Moment deleted = dao.query(mQueryId);
            if (deleted != null) {
                dao.delete(deleted);
            }
            resultData(mQueryId);
        }
    }

    private void resetPraiseStatus() {
        // 已赞、赞
        praiseText.setText(mMoment.isLiked() ? R.string.ui_base_text_praised : R.string.ui_base_text_praise);
        praiseIcon.setTextColor(getColor(mMoment.isLiked() ? R.color.colorCaution : R.color.transparent_ff_white));
        praiseNum.setText(format("%d", mMoment.getLikeNum()));
        commentNum.setText(format("%d", mMoment.getCmtNum()));
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Click({R.id.ui_moment_detail_praise_container,
            R.id.ui_moment_detail_comment_container,
            R.id.ui_moment_detail_switch_more_container})
    private void elementClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ui_moment_detail_praise_container:
                if (isCollected) {
                    return;
                }
                like(mMoment);
                break;
            case R.id.ui_moment_detail_comment_container:
                if (isCollected) {
                    return;
                }
                // 评论
                MomentCommentFragment.open(MomentImagesFragment.this);
                break;
            case R.id.ui_moment_detail_switch_more_container:
                // 打开更多详情页面
                MomentDetailsFragment.isCollected = isCollected;
                MomentDetailsFragment.open(MomentImagesFragment.this, mMoment);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == REQUEST_COMMENT) {
            // 发布对本说说的评论
            String result = getResultedData(data);
            if (!isEmpty(result)) {
                comment(mMoment, result, "");
            }
        }
        super.onActivityResult(requestCode, data);
    }

    private void showMoreButtons() {
        MomentMoreHelper.helper().init(this).setOnButtonClickListener(new MomentMoreHelper.OnButtonClickListener() {
            @Override
            public boolean onClick(View view) {
                handlePopupClick(view.getId());
                return true;
            }
        }).showPrivacy(mMoment.isMine()).showFavorite(!isCollected && !mMoment.isMine()).showShare(!isCollected).showSave(mMoment.getImage().size() > 0)
                .showDelete(mMoment.isMine())
                .setPrivacyText(mMoment.getAuthPublic() == Seclusion.Type.Public ? R.string.ui_text_moment_details_button_privacy : R.string.ui_text_moment_details_button_public)
                .setCollectText(mMoment.isCollected() ? R.string.ui_text_moment_details_button_favorited : R.string.ui_text_moment_details_button_favorite)
                .show();
    }

    private void handlePopupClick(int id) {
        switch (id) {
            case R.id.ui_dialog_moment_details_button_privacy:
                // 设为公开或私密
                handleMomentAuthPublic();
                break;
            case R.id.ui_dialog_moment_details_button_favorite:
                // 收藏单张图片
                tryCollection();
                break;
            case R.id.ui_dialog_moment_details_button_share:
                openShareDialog();
                break;
            case R.id.ui_dialog_moment_details_button_save:
                // 保存单张图片到本地
                save();
                break;
            case R.id.ui_dialog_moment_details_button_delete:
                deleteMoment();
                break;
        }
    }

    @Override
    protected void shareToQQ() {
        ShareToQQ.shareToQQ(ShareToQQ.TO_QQ, Activity(), "", "", "", images.get(selected), null);
    }

    @Override
    protected void shareToQZone() {
        ArrayList<String> img = new ArrayList<>();
        img.add(images.get(selected));
        ShareToQQ.shareToQQ(Shareable.TO_QZONE, Activity(), StringHelper.getString(R.string.ui_base_share_title, "分享图片"), mMoment.getContent(), "http://www.baidu.com", "", img);
    }

    @Override
    protected void shareToWeiXinSession() {
        ArrayList<String> img = new ArrayList<>();
        img.add(images.get(selected));
        ShareToWeiXin.shareToWeiXin(Activity(), Shareable.TO_WX_SESSION, StringHelper.getString(R.string.ui_base_share_title, "分享图片"), mMoment.getContent(), img);
    }

//    @Override
//    protected void shareToWeiXinTimeline() {
//        ArrayList<String> img = new ArrayList<>();
//        img.add(images.get(selected));
//        ShareToWeiXin.shareToWeiXin(Activity(), Shareable.TO_WX_TIMELINE, StringHelper.getString(R.string.ui_base_share_title, "分享图片"), mMoment.getContent(), img);
//    }

    private void save() {
        String url = images.get(selected);
        String local = HttpHelper.helper().getLocalFilePath(url, App.IMAGE_DIR);
        File file = new File(local);
        if (file.exists()) {
            new CopyLocalFileTask().setOnTaskCompleteListener(new OnTaskCompleteListener() {
                @Override
                public void onComplete() {

                }
            }).exec(url, local);
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

    private void initializeAdapter() {
        if (null == mAdapter) {
            imageViewPager.addOnPageChangeListener(mOnPageChangeListener);
            mAdapter = new MomentDetailsAdapter();
            imageViewPager.setAdapter(mAdapter);
            changedPosition(selected);
        }
        imageViewPager.setCurrentItem(selected, true);
    }

    private ViewPager.SimpleOnPageChangeListener mOnPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            selected = position;
            changedPosition(position);
        }
    };

    private void changedPosition(int position) {
        setSubTitle(format("%d/%d", position + 1, images.size()));
    }

    private MomentDetailsAdapter mAdapter;

    @Override
    protected void onLikeComplete(boolean success, Model model) {
        if (success) {
            resetPraiseStatus();
        }
    }

    @Override
    protected void onCollectComplete(boolean success, Model model) {

    }

    @Override
    protected void onCommentComplete(boolean success, Comment comment, Model model) {
        resetPraiseStatus();
        // 评论成功，转到说收详情页查看评论
        MomentDetailsFragment.open(MomentImagesFragment.this, mQueryId);
    }

    private class MomentDetailsAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        private RequestListener<Drawable> listener = new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                displayLoading(false);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                displayLoading(false);
                return false;
            }
        };

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            String image = images.get(position);
            String ext = Attachment.getExtension(image);
            assert ext != null;
            String local = Shareable.getLocalPath(image);
            if (!isEmpty(local)) {
                // 获取ImageLoader下载了的本地图片大图
                assert local != null;
                File file = new File(local);
                if (file.exists()) {
                    // 本地大图文件存在才读取本地大图文件，否则直接下载
                    image = local;
                }
            }
            displayLoading(true);
            if (!isEmpty(ext) && ext.contains("gif")) {
                // 动图
                ImageView imageView = new ImageView(App.app());
                container.addView(imageView);
                Glide.with(MomentImagesFragment.this).load(image).listener(listener).into(imageView);
                return imageView;
            }
            final SubsamplingScaleImageView ssiv = new SubsamplingScaleImageView(App.app());
            container.addView(ssiv);
            Glide.with(MomentImagesFragment.this).downloadOnly().load(image).into(new SimpleTarget<File>() {
                @Override
                public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                    displayLoading(false);
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
