package com.gzlk.android.isp.fragment.individual;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.user.CollectionRequest;
import com.gzlk.android.isp.application.App;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.HttpHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.lib.view.ExpandableTextView;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.archive.ArchiveLike;
import com.gzlk.android.isp.model.archive.ArchiveSource;
import com.gzlk.android.isp.model.archive.Comment;
import com.gzlk.android.isp.model.user.Collection;
import com.gzlk.android.isp.model.user.Moment;
import com.hlk.hlklib.lib.emoji.EmojiUtility;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredButton;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.hlk.hlklib.tasks.AsyncedTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    private static final String PARAM_SELECTED = "mdf_moment_selected";
    private static final String PARAM_USER_ID = "mdf_moment_user_id";
    private static final String PARAM_USER_NAME = "mdf_moment_user_name";

    public static MomentImagesFragment newInstance(String params) {
        MomentImagesFragment mdf = new MomentImagesFragment();
        Bundle bundle = new Bundle();
        String[] strings = splitParameters(params);
        // 动态的id
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        // 选中的图片索引
        bundle.putInt(PARAM_SELECTED, Integer.valueOf(strings[1]));
        mdf.setArguments(bundle);
        return mdf;
    }

    private int selected;
    private String momentUser = "", momentName = "";

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        selected = bundle.getInt(PARAM_SELECTED, 0);
        momentUser = bundle.getString(PARAM_USER_ID, "");
        momentName = bundle.getString(PARAM_USER_NAME, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_SELECTED, selected);
        bundle.putString(PARAM_USER_ID, momentUser);
        bundle.putString(PARAM_USER_NAME, momentName);
    }

    // UI
    @ViewId(R.id.ui_tool_view_pager)
    private ViewPager detailImageContent;
    @ViewId(R.id.ui_moment_detail_content_text)
    private ExpandableTextView detailContentTextView;
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
            //mMoment = new Dao<>(Moment.class).query(mQueryId);
            displayMomentDetails();
        }
        detailContentTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        initializeAdapter();
    }

    private void displayMomentDetails() {
        if (null == mMoment) {
            fetchingMoment();
        } else {
            momentUser = mMoment.getUserId();
            momentName = mMoment.getUserName();
            if (null != mMoment.getImage()) {
                images.addAll(mMoment.getImage());
            }
            if (null != mAdapter) {
                mAdapter.notifyDataSetChanged();
            }
            setCustomTitle(Utils.format(mMoment.getCreateDate(), StringHelper.getString(R.string.ui_base_text_date_time_format), "yyyy年MM月dd日HH:mm"));
            detailContentTextView.setText(EmojiUtility.getEmojiString(detailContentTextView.getContext(), mMoment.getContent(), true));
            detailContentTextView.makeExpandable();
            checkIsMyPraised();
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
            finish();
        }
    }

    private void resetPraiseStatus() {
        // 已赞、赞
        praiseText.setText(mMoment.isMyPraised() ? R.string.ui_base_text_praised : R.string.ui_base_text_praise);
        praiseIcon.setTextColor(getColor(mMoment.isMyPraised() ? R.color.colorCaution : R.color.transparent_ff_white));
        praiseNum.setText(format("%d", mMoment.getLikeNum()));
        commentNum.setText(format("%d", mMoment.getCmtNum()));
    }

    @Override
    protected void onCheckIsMyPraisedComplete(boolean success) {
        mMoment.setMyPraised(success);
        resetPraiseStatus();
    }

    @Override
    protected void onPraiseMomentComplete(ArchiveLike archiveLike, boolean success) {
        if (success) {
            // 赞+1
            mMoment.setMyPraised(true);
            fetchingMoment();
        }
    }

    @Override
    protected void onDeletePraiseMomentComplete(boolean success) {
        if (success) {
            mMoment.setMyPraised(false);
            fetchingMoment();
        }
    }

    @Override
    protected void onCommentMomentComplete(Comment comment, boolean success) {
        if (success) {
            mMoment.setCmtNum(mMoment.getCmtNum() + 1);
            resetPraiseStatus();
            // 评论成功，转到说收详情页查看评论
            MomentDetailsFragment.open(MomentImagesFragment.this, mQueryId);
        }
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    private boolean isPraising = false;

    @Click({R.id.ui_moment_detail_praise_container,
            R.id.ui_moment_detail_comment_container,
            R.id.ui_moment_detail_switch_more_container})
    private void elementClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ui_moment_detail_praise_container:
                if (!isPraising) {
                    isPraising = true;
                    // 赞、取消赞
                    if (mMoment.isMyPraised()) {
                        deletePraiseMoment();
                    } else {
                        praiseMoment();
                    }
                    Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isPraising = false;
                        }
                    }, 1000);
                }
                break;
            case R.id.ui_moment_detail_comment_container:
                // 评论
                MomentCommentFragment.open(MomentImagesFragment.this);
                break;
            case R.id.ui_moment_detail_switch_more_container:
                // 打开更多详情页面
                MomentDetailsFragment.open(MomentImagesFragment.this, mQueryId);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == MomentCommentFragment.REQ_COMMENT) {
            // 发布对本说说的评论
            String result = getResultedData(data);
            if (!isEmpty(result)) {
                commentMoment(result);
            }
        }
        super.onActivityResult(requestCode, data);
    }

    private View dialogView;
    private CorneredButton toPrivacy, toDelete;

    private void showMoreButtons() {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == dialogView) {
                    dialogView = View.inflate(Activity(), R.layout.popup_dialog_moment_details, null);
                    toPrivacy = (CorneredButton) dialogView.findViewById(R.id.ui_dialog_moment_details_button_privacy);
                    toDelete = (CorneredButton) dialogView.findViewById(R.id.ui_dialog_moment_details_button_delete);

                    // 不是我自己时，不显示设为私密和删除按钮
                    toPrivacy.setVisibility(momentUser.equals(Cache.cache().userId) ? View.VISIBLE : View.GONE);
                    toDelete.setVisibility(momentUser.equals(Cache.cache().userId) ? View.VISIBLE : View.GONE);
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
                handlePopupClick(view.getId());
                return true;
            }
        }).setPopupType(DialogHelper.TYPE_SLID).setAdjustScreenWidth(true).show();
    }

    private void handlePopupClick(int id) {
        switch (id) {
            case R.id.ui_dialog_moment_details_button_privacy:
                break;
            case R.id.ui_dialog_moment_details_button_favorite:
                // 收藏单张图片
                tryCollection();
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

    @SuppressWarnings("ConstantConditions")
    private void tryCollection() {
        // 收藏当前显示的图片
        String url = images.get(selected);
        ArchiveSource as = new ArchiveSource();
        as.setModule(Collection.Module.MOMENT);
        as.setId(mQueryId);
        CollectionRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Collection>() {
            @Override
            public void onResponse(Collection collection, boolean success, String message) {
                super.onResponse(collection, success, message);
                if (success) {
                    if (null != collection && !StringHelper.isEmpty(collection.getId())) {
                        new Dao<>(Collection.class).save(collection);
                    }
                    ToastHelper.make().showMsg(message);
                }
            }
        }).add(Collection.Type.IMAGE, as, url, momentUser, momentName);
    }

    private void save() {
        String url = images.get(selected);
        String local = HttpHelper.helper().getLocalFilePath(url, App.IMAGE_DIR);
        File file = new File(local);
        if (file.exists()) {
            new CopyTo().exec(url, local);
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

    private class CopyTo extends AsyncedTask<String, Integer, Boolean> {

        private String error = "";

        @Override
        protected Boolean doInBackground(String... params) {
            String url = params[0];
            String local = params[1];
            String name = url.substring(url.lastIndexOf('/'));
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/" + App.ROOT_DIR;
            try {
                File f = new File(path);
                if (!f.exists()) {
                    if (!f.mkdirs()) {
                        error = StringHelper.getString(R.string.ui_base_text_dictionary_create_fail);
                        return false;
                    }
                }

                File file = new File(local);
                if (file.exists()) {
                    long totalLength = file.length();
                    InputStream inputStream = new FileInputStream(local);
                    String out = path + name;
                    FileOutputStream fos = new FileOutputStream(out);
                    byte[] buffer = new byte[4096];
                    int handled = 0;
                    int read;
                    while ((read = inputStream.read(buffer)) != -1) {
                        handled += read;
                        fos.write(buffer, 0, read);
                        publishProgress((int) (handled * 1.0 / totalLength * 100));
                    }
                    fos.close();
                    inputStream.close();
                    return true;
                } else {
                    error = StringHelper.getString(R.string.ui_base_text_file_not_exists);
                }
            } catch (IOException e) {
                e.printStackTrace();
                error = e.getMessage();
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                ToastHelper.make().showMsg(R.string.ui_base_text_downloading_image_completed);
            } else {
                ToastHelper.make().showMsg(StringHelper.getString(R.string.ui_base_text_downloading_fail, error));
            }
        }
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            detailImageContent.addOnPageChangeListener(mOnPageChangeListener);
            mAdapter = new MomentDetailsAdapter();
            detailImageContent.setAdapter(mAdapter);
            changedPosition(selected);
        }
        detailImageContent.setCurrentItem(selected, true);
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
            displayer.setLargeImageSupport(true);
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
