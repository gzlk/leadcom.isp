package com.gzlk.android.isp.etc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import com.gzlk.android.isp.helper.LogHelper;
import com.gzlk.android.isp.helper.StringHelper;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * <b>功能描述：</b>图片压缩类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/15 01:22 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/15 01:22 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public final class ImageCompress {

    private static String TAG = ImageCompress.class.getSimpleName();
    /**
     * 压缩后的文件大小限制(500K)
     */
    public static int MAX_COMPRESSED_SIZE = 500 * 1024;
    /**
     * 默认压缩模式 JPEG
     */
    private static Bitmap.CompressFormat cfJPEG = Bitmap.CompressFormat.JPEG;

    /**
     * 将图片压缩并保存到指定的文件中去
     *
     * @param fromPath        原始图片绝对路径
     * @param toPath          压缩后的图片保存的路径
     * @param prepareToWidth  压缩后图片的最大宽度
     * @param prepareToHeight 压缩后图片的最大高度
     */
    public static void compressBitmap(String fromPath, String toPath, int prepareToWidth, int prepareToHeight) {
        try {
            compress(fromPath, toPath, prepareToWidth, prepareToHeight);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void log(String string) {
        LogHelper.log(TAG, string);
    }

    /**
     * 获取当前需要压缩的尺寸与原始图片之间最大尺寸的缩放比率
     *
     * @param prepareToWidth  期望压缩到的图片宽度
     * @param prepareToHeight 期望压缩到的图片高度
     * @param options         被压缩图片的 options
     */
    private static int getSampleSize(int prepareToWidth, int prepareToHeight, BitmapFactory.Options options) {
        int width = options.outWidth;
        int height = options.outHeight;
        int maxScreen = prepareToWidth > prepareToHeight ? prepareToWidth : prepareToHeight;
        int max = width > height ? width : height;
        int sampleSize = max / maxScreen;
        return sampleSize < 1 ? 1 : sampleSize;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    private static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     *
     * @param angle  要旋转的角度
     * @param bitmap 要旋转的图片
     * @return Bitmap
     */
    private static Bitmap rotatingImageView(int angle, Bitmap bitmap) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 压缩图片并指定缩放比率
     */
    private static Bitmap decodeSampledBitmapFromFile(String filename, int sampleSize) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);

        // Calculate inSampleSize
        options.inSampleSize = sampleSize;
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filename, options);
    }

    private static final String fmt = "compressed \"%s\" in 1/%d(quality(%d), new file size: %d)";

    /**
     * 获取本地图片的尺寸信息
     */
    public static BitmapFactory.Options getBitmapOptions(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        return options;
    }

    /**
     * 指定路径的文件是否为图片格式
     */
    public static boolean isImage(String extension) {
        if (StringHelper.isEmpty(extension)) return false;
        switch (extension.toLowerCase(Locale.getDefault())) {
            case "gif":
            case "jpg":
            case "bmp":
            case "png":
            case "jpeg":
            case "tiff":
                return true;
            default:
                return false;
        }
    }

    /**
     * 指定的文件扩展名是否为视频文件
     */
    public static boolean isVideo(String extension) {
        if (TextUtils.isEmpty(extension)) return false;
        switch (extension.toLowerCase(Locale.getDefault())) {
            case "avi":
            case "rmvb":
            case "rm":
            case "asf":
            case "divx":
            case "mp4":
            case "mpg":
            case "mpeg":
            case "mpe":
            case "wmv":
            case "mkv":
            case "3gp":
            case "vob":
            case "mov":
                return true;
        }
        return false;
    }

    /**
     * 获取视频的缩略图，默认获取视频1s时的帧
     *
     * @param filePath 本地视频文件路径
     */
    public static Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return getVideoThumbnail(filePath, 1000);
    }

    /**
     * 获取视频的缩略图
     *
     * @param filePath 本地视频文件路径
     * @param atTime   指定时间点
     */
    public static Bitmap getVideoThumbnail(String filePath, long atTime) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(atTime);
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 循环压缩图片到指定效果
     */
    private static void compress(String fromPath, String toPath, int prepareToWidth, int prepareToHeight) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 默认原始尺寸大小以及旋转的角度
        int sampleSize, quality = 50, degree = readPictureDegree(fromPath);
        // 原始尺寸获取
        BitmapFactory.Options options = getBitmapOptions(fromPath);

        // 与当前屏幕分辨率对比，获取缩放率
        sampleSize = getSampleSize(prepareToWidth, prepareToHeight, options);

        // if (options.outWidth <= BaseFragment.MAX_THUMB_IMAGE_SIZE
        // || options.outHeight <= BaseFragment.MAX_THUMB_IMAGE_SIZE) {
        // 如果缩放的尺寸在最大缩略图尺寸以下则强制压缩最低比率
        // if (prepareToWidth <= BaseFragment.MAX_THUMB_IMAGE_SIZE
        // || prepareToHeight <= BaseFragment.MAX_THUMB_IMAGE_SIZE) {
        // // 如果是头像大小，则直接指定压缩比率质量为10%以获取更好的网络传输效率
        // quality = 20;
        // } else if (options.outWidth <= BaseFragment.MAX_THUMB_IMAGE_SIZE
        // || options.outHeight <= BaseFragment.MAX_THUMB_IMAGE_SIZE) {
        // // 如果原始图片的大小小于缩略图的限定则说明是缩略图，直接压缩最低比率
        // // quality = 10;
        // }

        // 重新获取新的尺寸的图片
        Bitmap bitmap = decodeSampledBitmapFromFile(fromPath, sampleSize);
        if (null == bitmap) {
            log("Cannot compress null bitmap of url: " + fromPath);
            return;
        }

        if (degree > 0) {
            bitmap = rotatingImageView(degree, bitmap);
        }

        bitmap.compress(cfJPEG, quality, baos);
        log(String.format(Locale.getDefault(), fmt, fromPath, sampleSize, quality, baos.toByteArray().length));
//        while (baos.toByteArray().length > MAX_COMPRESSED_SIZE) {
//            baos.reset();
//            quality -= 10;
//            bitmap.compress(cfJPEG, quality, baos);
//            log(String.format(Locale.getDefault(), fmt, fromPath, sampleSize, quality, baos.toByteArray().length));
//        }
        byte[] data = baos.toByteArray();
        bitmap.recycle();
        FileOutputStream fos = new FileOutputStream(toPath);
        fos.write(data);
        fos.close();
        baos.close();
    }
}
