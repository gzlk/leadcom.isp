package com.leadcom.android.isp.fragment.individual;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.apache.poi.FileUtils;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseTransparentSupportFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.common.SimpleClickableViewHolder;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.task.AsyncExecutableTask;

import java.util.ArrayList;

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
        return true;
    }

    @Override
    protected void destroyView() {

    }

    @Click({R.id.ui_setting_cache_clear})
    private void viewClick(View view) {
        // 清理全部
        new RemoveTask().execute(-1);
    }

    private void initializeHolders() {
        if (null == fileHolder) {
            fileHolder = new SimpleClickableViewHolder(fileView, this);
            fileHolder.showContent(format(items[1], ""));
            fileHolder.showProgress(true);
            fileHolder.addOnViewHolderClickListener(holderClickListener);
            new CalculateTask().execute(0);
        }
        if (null == imageHolder) {
            imageHolder = new SimpleClickableViewHolder(imageView, this);
            imageHolder.showContent(format(items[1], ""));
            imageHolder.showProgress(true);
            imageHolder.addOnViewHolderClickListener(holderClickListener);
            new CalculateTask().execute(1);
        }
        if (null == videoHolder) {
            videoHolder = new SimpleClickableViewHolder(videoView, this);
            videoHolder.showContent(format(items[2], ""));
            videoHolder.showProgress(true);
            videoHolder.addOnViewHolderClickListener(holderClickListener);
            new CalculateTask().execute(2);
        }
        if (null == otherHolder) {
            otherHolder = new SimpleClickableViewHolder(otherView, this);
            otherHolder.showContent(format(items[3], ""));
            otherHolder.showProgress(true);
            otherHolder.addOnViewHolderClickListener(holderClickListener);
            new CalculateTask().execute(3);
        }
    }

    private OnViewHolderClickListener holderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            switch (index) {
                case 0:
                    // 清理文件缓存
                    fileHolder.showProgress(true);
                    new RemoveTask().execute(0);
                    break;
                case 1:
                    // 清理图片缓存
                    imageHolder.showProgress(true);
                    new RemoveTask().execute(1);
                    break;
                case 2:
                    // 清理视频缓存
                    videoHolder.showProgress(true);
                    new RemoveTask().execute(2);
                    break;
                case 3:
                    // 清理其他缓存（比如下载了的app安装文件）
                    otherHolder.showProgress(true);
                    new RemoveTask().execute(3);
                    break;
            }
        }
    };

    // 删除目录
    private class RemoveTask extends AsyncExecutableTask<Integer, Integer, Void> {

        private int type;

        @Override
        protected Void doInTask(Integer... integers) {
            type = integers[0];
            if (type < 0) {
                // 清理全部
                for (int i = 0; i <= 3; i++) {
                    publishProgress(i);
                    clearCache(i);
                }
            } else {
                clearCache(type);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int value = values[0];
            refresh(value);
        }

        private void clearCache(int type) {
            ArrayList<String> paths = new ArrayList<>();
            switch (type) {
                case 0:
                    // 清理文件缓存
                    paths.add(App.app().getCachePath(App.ARCHIVE_DIR));
                    paths.add(App.app().getCachePath(App.HTML_DIR));
                    break;
                case 1:
                    // 清理图片缓存
                    paths.add(App.app().getCachePath(App.IMAGE_DIR));
                    paths.add(App.app().getCachePath(App.CAMERA_DIR));
                    paths.add(App.app().getCachePath(App.THUMB_DIR));
                    paths.add(App.app().getCachePath(App.CROPPED_DIR));
                    break;
                case 2:
                    // 清理视频缓存
                    paths.add(App.app().getCachePath(App.VIDEO_DIR));
                    break;
                case 3:
                    // 清理其他缓存（比如下载了的app安装文件）
                    paths.add(App.app().getCachePath(App.TEMP_DIR));
                    paths.add(App.app().getCachePath(App.OTHER_DIR));
                    break;
            }
            for (String path : paths) {
                FileUtils.removeFile(path);
            }
        }

        private void refresh(int type) {
            switch (type) {
                case 0:
                    fileHolder.showContent(format(items[0], Utils.formatSize(0L)));
                    fileHolder.showProgress(false);
                    break;
                case 1:
                    imageHolder.showContent(format(items[1], Utils.formatSize(0L)));
                    imageHolder.showProgress(false);
                    break;
                case 2:
                    videoHolder.showContent(format(items[2], Utils.formatSize(0L)));
                    videoHolder.showProgress(false);
                    break;
                case 3:
                    otherHolder.showContent(format(items[3], Utils.formatSize(0L)));
                    otherHolder.showProgress(false);
                    break;
            }
        }

        @Override
        protected void doAfterExecute() {
            super.doAfterExecute();
            refresh(type);
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
