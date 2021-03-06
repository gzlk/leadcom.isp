package com.leadcom.android.isp.share;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.IntDef;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.hlk.hlklib.etc.Cryptography;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.etc.ImageCompress;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.helper.LogHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.ExecutionException;

/**
 * <b>功能描述：</b>各类分享的实现方法<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/09/30 10:40 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/09/30 10:40 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class Shareable {

    public static final int TO_QQ = 1;
    public static final int TO_QZONE = 2;
    public static final int TO_WX_SESSION = 3;
    public static final int TO_WX_TIMELINE = 4;
    public static final int TO_WX_FAVORITE = 5;
    public static final int TO_WEIBO = 6;

    protected static String TAG = "Shareable";

    // 严格模式的缩略图大小
    private static final int THUMB_WIDTH = 150, THUMB_HEIGHT = 150;
    /**
     * 缩略图最大不超过32K
     */
    private static final int MAX_THUMB_FILE_SIZE = 32 * 1024;

    /**
     * 分享类型
     */
    @IntDef({TO_QQ, TO_QZONE, TO_WX_SESSION, TO_WX_TIMELINE, TO_WX_FAVORITE, TO_WEIBO})
    public @interface ShareType {

    }

    protected static void log(String string) {
        LogHelper.log(TAG, string);
    }

    protected static boolean isEmpty(String string) {
        return StringHelper.isEmpty(string);
    }

    protected static String getString(int res) {
        return StringHelper.getString(res);
    }

    protected static boolean hasPermission(Context context) {
        return Build.VERSION.SDK_INT < 23 ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    protected static void requestPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
    }

    public static String getLocalPath(String imageUrl) {
        File file = ImageLoader.getInstance().getDiskCache().get(imageUrl);
        if (null != file && file.exists() && file.length() > 100) {
            return file.getPath();
        }
        return null;
    }

    public static void getLocalPathGlide(final Context context, final String imageUrl, final OnGlideFetchingCompleteListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FutureTarget<File> target = Glide.with(context).downloadOnly().load(imageUrl).submit();
                    File file = target.get();
                    if (null != file) {
                        String path = file.getAbsolutePath();
                        if (null != listener) {
                            listener.onComplete(path);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static byte[] getAppIcon() {
        Bitmap bitmap = BitmapFactory.decodeResource(App.app().getResources(), R.drawable.img_default_app_icon);
        return bmpToByteArray(bitmap, true);
    }

    static byte[] getThumb(String imageUrl) {
        // url为空时返回app的默认图标
        if (isEmpty(imageUrl)) {
            return getAppIcon();
        }
        String localPath = imageUrl.charAt(0) == '/' ? imageUrl : getLocalPath(imageUrl);

        Bitmap source = null;
        try {
            source = BitmapFactory.decodeFile(localPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null == source) {
            return getAppIcon();
        }

        Bitmap thumb = Bitmap.createScaledBitmap(source, THUMB_WIDTH, THUMB_HEIGHT, true);
        byte[] data = bmpToByteArray(thumb, true);
        thumb.recycle();
        if (data.length > MAX_THUMB_FILE_SIZE) {
            // 如果缩略图的大小超过了32K则重新生成缩略图
            log("WX thumb size > 32K(" + Utils.formatSize(data.length) + "), now re-scale image.");
            return compressThumb(localPath);
        }
        return data;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static byte[] readFile(String path) {
        File file = new File(path);
        byte[] temp = new byte[(int) file.length()];
        try {
            FileInputStream fis = new FileInputStream(file);
            fis.read(temp, 0, temp.length);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }

    /**
     * 压缩图片到32K以下
     */
    @SuppressWarnings("ConstantConditions")
    private static byte[] compressThumb(String localPath) {
        String tempThumb = App.app().getTempLocalPath(App.THUMB_DIR);
        ImageCompress.PREPARE_COMPRESSED_SIZE = 32 * 1024;
        ImageCompress.compressBitmap(localPath, tempThumb, THUMB_WIDTH, THUMB_HEIGHT);
        ImageCompress.PREPARE_COMPRESSED_SIZE = 0;
        return readFile(tempThumb);
    }

    /**
     * 压缩原始图片到手机屏幕分辨率大小
     */
    static String compressSources(String localPath, String ext) {
        // 压缩原始图片到屏幕分辨率大小
        String name = Cryptography.md5(localPath);
        String temp = App.app().getCachePath(App.TEMP_DIR) + name + "." + ext;
        File file = new File(temp);
        if (!file.exists() || file.length() < 100) {
            DisplayMetrics dm = App.app().getResources().getDisplayMetrics();
            ImageCompress.compressBitmap(localPath, temp, dm.widthPixels, dm.heightPixels);

            ContentValues values = new ContentValues(2);
            values.put(MediaStore.Images.Media.MIME_TYPE, "jpg");
            values.put(MediaStore.Images.Media.DATA, temp);
            App.app().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
        return temp;
    }

    private static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
