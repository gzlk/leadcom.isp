package com.leadcom.android.isp.fragment.individual;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.hlk.hlklib.lib.inject.ViewId;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.apache.poi.FileUtils;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseTransparentSupportFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.common.SimpleClickableViewHolder;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/04/08 14:27 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/04/08 14:27 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class SettingCacheFragment extends BaseTransparentSupportFragment {

    public static void open(BaseFragment fragment) {
        fragment.openActivity(SettingCacheFragment.class.getName(), "", true, false);
    }

    @ViewId(R.id.ui_setting_cache_file)
    private View fileView;
    @ViewId(R.id.ui_setting_cache_image)
    private View imageView;
    @ViewId(R.id.ui_setting_cache_video)
    private View videoView;
    @ViewId(R.id.ui_setting_cache_other)
    private View otherView;
    private SimpleClickableViewHolder fileHolder, imageHolder, videoHolder, otherHolder;
    private String[] items;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setCustomTitle(R.string.ui_text_setting_cache_fragment_title);
        items = StringHelper.getStringArray(R.array.ui_individual_setting_cache);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_individual_setting_cache;
    }

    @Override
    public void doingInResume() {
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
        if (null == fileHolder) {
            fileHolder = new SimpleClickableViewHolder(fileView, this);
            fileHolder.showContent(format(items[1], ""));
            fileHolder.showProgress(true);
            new CalculateTask().execute(0);
        }
        if (null == imageHolder) {
            imageHolder = new SimpleClickableViewHolder(imageView, this);
            imageHolder.showContent(format(items[1], ""));
            imageHolder.showProgress(true);
            new CalculateTask().execute(1);
        }
        if (null == videoHolder) {
            videoHolder = new SimpleClickableViewHolder(videoView, this);
            videoHolder.showContent(format(items[2], ""));
            videoHolder.showProgress(true);
            new CalculateTask().execute(2);
        }
        if (null == otherHolder) {
            otherHolder = new SimpleClickableViewHolder(otherView, this);
            otherHolder.showContent(format(items[3], ""));
            otherHolder.showProgress(true);
            new CalculateTask().execute(3);
        }
    }

    private class CalculateTask extends AsyncTask<Integer, Void, Void> {

        private int type;
        private long size;

        @Override
        protected Void doInBackground(Integer... integers) {
            type = integers[0];
            switch (type) {
                case 0:
                    size = FileUtils.getFileSize(App.app().getCachePath(App.ARCHIVE_DIR));
                    size += FileUtils.getFileSize(App.app().getCachePath(App.HTML_DIR));
                    break;
                case 1:
                    size = FileUtils.getFileSize(App.app().getCachePath(App.IMAGE_DIR));
                    size += FileUtils.getFileSize(App.app().getCachePath(App.CAMERA_DIR));
                    size += FileUtils.getFileSize(App.app().getCachePath(App.THUMB_DIR));
                    size += FileUtils.getFileSize(App.app().getCachePath(App.CROPPED_DIR));
                    break;
                case 2:
                    size = FileUtils.getFileSize(App.app().getCachePath(App.VIDEO_DIR));
                    break;
                case 3:
                    size = FileUtils.getFileSize(App.app().getCachePath(App.TEMP_DIR));
                    size += FileUtils.getFileSize(App.app().getCachePath(App.OTHER_DIR));
                    break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            switch (type) {
                case 0:
                    fileHolder.showContent(format(items[0], Utils.formatSize(size)));
                    fileHolder.showProgress(false);
                    break;
                case 1:
                    imageHolder.showContent(format(items[1], Utils.formatSize(size)));
                    imageHolder.showProgress(false);
                    break;
                case 2:
                    videoHolder.showContent(format(items[2], Utils.formatSize(size)));
                    videoHolder.showProgress(false);
                    break;
                case 3:
                    otherHolder.showContent(format(items[3], Utils.formatSize(size)));
                    otherHolder.showProgress(false);
                    break;
            }
        }
    }
}
