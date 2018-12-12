package com.leadcom.android.isp.helper;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;

import com.leadcom.android.isp.BuildConfig;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.VideoPlayerActivity;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.etc.ImageCompress;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.common.ImageViewerFragment;
import com.leadcom.android.isp.fragment.common.InnerWebViewFragment;
import com.leadcom.android.isp.fragment.common.OfficeOnlinePreviewFragment;
import com.leadcom.android.isp.fragment.common.PdfViewerFragment;
import com.leadcom.android.isp.helper.popup.DeleteDialogHelper;
import com.leadcom.android.isp.model.common.Attachment;

import java.io.File;
import java.util.Locale;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/05/30 14:37 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/05/30 14:37 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class FilePreviewHelper {

    private static final String NIM = "netease.com";
    private static final String NIM_ATTACH = "https://nim";

    /**
     * 判断是否是云信附件
     */
    public static boolean isNimFile(String url) {
        return !StringHelper.isEmpty(url, true) && (url.contains(NIM) || url.contains(NIM_ATTACH));
    }

    /**
     * 根据文件类型打开相应的文件预览
     */
    public static void previewFile(Context context, String path, String fileName, String extension) {
        Context activity = getActivity(context);
        if (null == activity && context instanceof Service) {
            activity = context;
        }
        if (null == activity) {
            throw new IllegalArgumentException("cannot fetching Activity from context: " + context.toString());
        }

        if (!TextUtils.isEmpty(extension)) {
            String ext = extension.toLowerCase(Locale.getDefault());

            if (ImageCompress.isImage(ext)) {
                previewImage(activity, path);
                return;
            } else if (ext.equals("pdf")) {
                previewPdf(activity, path, fileName);
                return;
            } else if (Attachment.isOffice(ext)) {
                previewOnlineOffice(activity, path, fileName, ext);
                return;
            } else if (ImageCompress.isVideo(ext)) {
                previewVideo(activity, path, fileName, extension);
                return;
            } else if (extension.contains("txt")) {
                // 文本文件的在线预览方式
                previewOnline(activity, path, fileName);
                return;
            }
            // 如果path是网址，则打开内置在线预览
            if (Utils.isUrl(path)) {
                previewOnline(activity, path, fileName);
            } else {
                // 如果是本地文件，则尝试使用第三方app打开
                previewMimeFile(activity, path, extension);
            }
        } else {
            ToastHelper.helper().showMsg(R.string.ui_base_text_file_ext_not_valid);
            // 强制用text方式打开文件
            previewMimeFile(activity, path, "txt");
        }
    }

    /**
     * try get host activity from view.
     * views hosted on floating window like dialog and toast will sure return null.
     *
     * @return host activity; or null if not available
     */
    public static Activity getActivityFromView(View view) {
        return getActivity(view.getContext());
    }

    /**
     * try get host activity from view.
     * views hosted on floating window like dialog and toast will sure return null.
     *
     * @return host activity; or null if not available
     */
    public static Activity getActivity(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    /**
     * 尝试在线预览方式打开其余类型的文件
     */
    private static void previewOnline(Context context, String path, String fileName) {
        // 尝试在线预览方式
        InnerWebViewFragment.open(context, fileName, path);
    }

    /**
     * 根据指定文件扩展名用本地第三方app打开文档
     */
    public static void previewMimeFile(Context context, String path, String extension) {
        try {
            String mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            if (StringHelper.isEmpty(mimetype) && extension.contains("log")) {
                // log文件用纯文本方式打开
                mimetype = "text/plain";
            }
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(getUriFromFile(path), mimetype);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            warningPreviewFileFail(context, extension);
        }
    }

    private static void warningPreviewFileFail(Context context, String extension) {
        DeleteDialogHelper.helper().init((AppCompatActivity) context)
                .setLayout(R.layout.popup_dialog_rich_editor_paste)
                .setCancelText(R.string.ui_base_text_known)
                .setTitleText(StringHelper.getString(R.string.ui_nim_attachment_open_failure, extension)).show();
    }

    private static final String fileProvider = StringHelper.format("%s.fileProvider", BuildConfig.APPLICATION_ID);

    // 处理Android 7.0+的Uri问题
    public static Uri getUriFromFile(String filePath) {
        if (Build.VERSION.SDK_INT >= 24) {
            File tempFile = new File(filePath);
            return FileProvider.getUriForFile(App.app(), fileProvider, tempFile);
        } else {
            return Uri.fromFile(new File(filePath));
        }
    }

    /**
     * 预览在线或本地PDF文档
     */
    private static void previewPdf(Context context, String path, String fileName) {
        PdfViewerFragment.open(context, path, fileName);
    }

    /**
     * 预览在线或本地图片
     */
    private static void previewImage(Context context, String path) {
        ImageViewerFragment.open(context, path);
    }

    /**
     * 预览在线视频
     */
    private static void previewVideo(Context context, String onlinePath, String fileName, String extension) {
        VideoPlayerActivity.start(context, onlinePath);
    }

    /**
     * 预览在线Office文档
     */
    private static void previewOnlineOffice(Context context, String path, String fileName, String extension) {
        //String url = path.contains(NIM) ? StringHelper.format("%s%s", OFFICE_PREVIEW, path) : path;
        boolean minutes = !StringHelper.isEmpty(fileName) && fileName.equals(StringHelper.getString(R.string.ui_nim_action_minutes));
        OfficeOnlinePreviewFragment.open(context, BaseFragment.ACTIVITY_BASE_REQUEST, path, fileName, extension, minutes);
    }
}
