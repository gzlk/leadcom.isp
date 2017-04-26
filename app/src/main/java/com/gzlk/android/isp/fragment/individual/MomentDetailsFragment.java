package com.gzlk.android.isp.fragment.individual;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.listener.OnRequestListener;
import com.gzlk.android.isp.api.user.MomentRequest;
import com.gzlk.android.isp.application.App;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.base.BaseDelayRefreshSupportFragment;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.HttpHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.user.moment.Moment;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.hlk.hlklib.tasks.AsyncedTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

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

public class MomentDetailsFragment extends BaseDelayRefreshSupportFragment {

    private static final String PARAM_ID = "mdf_moment_id";
    private static final String PARAM_SELECTED = "mdf_moment_selected";

    public static MomentDetailsFragment newInstance(String params) {
        MomentDetailsFragment mdf = new MomentDetailsFragment();
        Bundle bundle = new Bundle();
        String[] strings = splitParameters(params);
        bundle.putString(PARAM_ID, strings[0]);
        if (strings.length > 1) {
            bundle.putInt(PARAM_SELECTED, Integer.valueOf(strings[1]));
        }
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

    private ArrayList<String> images;

    @Override
    public int getLayout() {
        return R.layout.fragment_moment_details;
    }

    @Override
    public void doingInResume() {
        tryPaddingContent(titleContainer, false);
        titleRightIcon.setText(R.string.ui_icon_more);
        if (null == images) {
            images = new ArrayList<>();
            Moment moment = new Dao<>(Moment.class).query(queryId);
            displayMomentDetails(moment);
        }
        detailContentTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        initializeAdapter();
    }

    private void displayMomentDetails(Moment moment) {
        if (null == moment) {
            fetchingMoment();
        } else {
            images.addAll(moment.getImage());
            titleTextView.setText(Utils.format(moment.getCreateDate(), StringHelper.getString(R.string.ui_base_text_date_time_format), "yyyy年m月d日HH:mm"));
            toggleTextView.setText(moment.getContent());
        }
    }

    /**
     * 拉取远程服务器上的说说
     */
    private void fetchingMoment() {
        MomentRequest.request().setOnRequestListener(new OnRequestListener<Moment>() {
            @Override
            public void onResponse(Moment moment, boolean success, String message) {
                super.onResponse(moment, success, message);
                if (success) {
                    // 保存拉取回来的说说记录
                    new Dao<>(Moment.class).save(moment);
                    // 拉取回来之后立即显示
                    displayMomentDetails(moment);
                }
            }
        }).find(queryId);
    }

    private void deleteMoment() {
        MomentRequest.request().setOnRequestListener(new OnRequestListener<Moment>() {
            @Override
            public void onResponse(Moment moment, boolean success, String message) {
                super.onResponse(moment, success, message);
                if (success) {
                    // 本地已删除
                    Dao<Moment> dao = new Dao<>(Moment.class);
                    Moment deleted = dao.query(queryId);
                    if (deleted != null) {
                        deleted.setLocalDeleted(true);
                        dao.save(deleted);
                    }
                    finish();
                }
            }
        }).delete(queryId);
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
                handlePopupClick(view.getId());
                return false;
            }
        }).setPopupType(DialogHelper.TYPE_SLID).setAdjustScreenWidth(true).show();
    }

    private void handlePopupClick(int id) {
        switch (id) {
            case R.id.ui_dialog_moment_details_button_privacy:
                break;
            case R.id.ui_dialog_moment_details_button_favorite:
                fetchingMoment();
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
    protected void onFileDownloadingComplete(String url, boolean success) {
        super.onFileDownloadingComplete(url, success);
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
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/" + App.CACHE_DIR;
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
        titleCountTextView.setText(format("%d/%d", position + 1, images.size()));
    }

    private MomentDetailsAdapter mAdapter;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

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
