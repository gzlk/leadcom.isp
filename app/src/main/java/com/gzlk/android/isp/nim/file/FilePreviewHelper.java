package com.gzlk.android.isp.nim.file;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.activity.BaseActivity;
import com.gzlk.android.isp.application.App;
import com.gzlk.android.isp.etc.ImageCompress;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.common.ImageViewerFragment;
import com.gzlk.android.isp.fragment.common.InnerWebViewFragment;
import com.gzlk.android.isp.fragment.common.OfficeOnlinePreviewFragment;
import com.gzlk.android.isp.fragment.common.PdfViewerFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.model.common.Attachment;
import com.gzlk.android.isp.nim.activity.VideoPlayerActivity;
import com.netease.nim.uikit.NimUIKit;

import java.util.Locale;

/**
 * <b>功能描述：</b>文件预览<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/14 14:03 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/14 14:03 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class FilePreviewHelper {

    public static final String NIM = "netease.com";
    private static final String OFFICE_PREVIEW = "https://view.officeapps.live.com/op/view.aspx?src=";

    /**
     * 根据文件类型打开相应的文件预览
     */
    public static void previewFile(Context context, String path, String fileName, String extension) {
        if (!TextUtils.isEmpty(extension)) {
            String ext = extension.toLowerCase(Locale.getDefault());
            if (ext.equals("pdf")) {
                previewPdf(context, path, fileName);
                return;
            } else if (ImageCompress.isImage(ext)) {
                previewImage(context, path);
                return;
            } else if (Attachment.isOffice(ext)) {
                previewOnlineOffice(context, path, fileName, ext);
                return;
            } else if (ImageCompress.isVideo(ext)) {
                previewVideo(context, path, fileName, extension);
                return;
            } else if (path.contains(NIM) && extension.contains("txt")) {
                // 文本文件的在线预览方式
                //BaseActivity.openActivity(context, InnerWebViewFragment.class.getName(), StringHelper.format("%s,%s", path, fileName), true, false);
                previewOnline(context, path, fileName);
                return;
            }
            // 如果path是网址，则打开内置在线预览
            if (Utils.isUrl(path)) {
                previewOnline(context, path, fileName);
            } else {
                // 如果是本地文件，则尝试使用第三方app打开
                previewMimeFile(context, path, extension);
            }
        }
    }

    /**
     * 尝试在线预览方式打开其余类型的文件
     */
    private static void previewOnline(Context context, String path, String fileName) {
        // 尝试在线预览方式
        BaseActivity.openActivity(context, InnerWebViewFragment.class.getName(), StringHelper.format("%s,%s", path, fileName), true, false);
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
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            ToastHelper.make().showMsg(StringHelper.getString(R.string.ui_nim_attachment_open_failure));
        }
    }

    // 处理Android 7.0+的Uri问题
    public static Uri getUriFromFile(String filePath) {
        return NimUIKit.getUriFromFile(App.app(), filePath);
    }

    /**
     * 预览在线或本地PDF文档
     */
    private static void previewPdf(Context context, String path, String fileName) {
        String param = StringHelper.format("%s,%s", path, fileName);
        BaseActivity.openActivity(context, PdfViewerFragment.class.getName(), param, true, false);
    }

    /**
     * 预览在线或本地图片
     */
    private static void previewImage(Context context, String path) {
        BaseActivity.openActivity(context, ImageViewerFragment.class.getName(), StringHelper.format("0,%s", path), false, false, true);
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
        String param = StringHelper.format("%s,%s,%s", path, fileName, extension);
        BaseActivity.openActivity(context, OfficeOnlinePreviewFragment.class.getName(), param, true, false);
    }
}
