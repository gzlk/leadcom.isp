package com.gzlk.android.isp.application;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.hlk.hlklib.etc.Cryptography;

import java.io.File;
import java.util.UUID;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/04 20:19 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/04 20:19 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class BaseApplication extends Application {

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * 本地数据库缓存目录
     */
    public static final String DB_DIR = "database";
    /**
     * 本地相机拍照之后照片缓存目录
     */
    public static final String CAMERA_DIR = "camera";
    /**
     * 本地图片缓存目录
     */
    public static final String IMAGE_DIR = "images";
    /**
     * 本地缩略图缓存目录
     */
    public static final String THUMB_DIR = "thumbnails";
    /**
     * 本地语音缓存目录
     */
    public static final String VOICE_DIR = "voices";
    /**
     * 本地其他文件缓存目录
     */
    public static final String OTHER_DIR = "others";
    /**
     * 字体文件目录
     */
    public static final String FONT_DIR = "fonts";

    /**
     * 获取外挂 SD 卡目录下的 data 目录，末尾不包含/<br>
     * 如果外置SD卡不可读则转为获取内置缓存目录
     */
    @SuppressWarnings("ConstantConditions")
    public String gotExternalCacheDir() {
        // return Environment.getExternalStorageDirectory().getAbsolutePath();
        // if (null == StoaApp.getContext()) {
        // throw new RuntimeException("null application context");
        // }
        String path = null;
        try {
            // 获取外置SD卡中的缓存目录
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                path = getExternalCacheDir().toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(path)) {
            // 如果外置SD卡缓存目录获取失败则使用内置私有目录
            path = gotCacheDir();
        }
        return path;
    }

    /**
     * 获取内置SD卡中app私有存储空间
     */
    public String gotCacheDir() {
        return getCacheDir().toString();
    }

    /**
     * 在外置缓存中获取指定的目录路径，末尾包含/
     */
    public String getCachePath(String dir) {
        StringBuilder sb = new StringBuilder();
        if (dir.equals(DB_DIR)) {
            // 缓存数据库和图像都存在内置app私有空间里
            sb.append(gotCacheDir());
        } else {
            sb.append(gotExternalCacheDir());
        }
        sb.append("/").append(dir).append("/");
        createDirs(sb.toString());
        return sb.toString();
    }

    /**
     * 创建指定的文件目录
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private synchronized void createDirs(String dirs) {
        File file = new File(dirs);
        // 查看文件目录是否存在，不存在则创建
        if (!file.exists())
            file.mkdirs();
    }

    /**
     * 通过UUID返回随机字符串
     */
    public static String getRandomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 获取本地Image缓存文件夹路径，末尾包含/
     */
    public String getLocalImageDir() {
        return getCachePath(IMAGE_DIR);
    }

    /**
     * 获取本地照相之后照片缓存目录
     */
    public String getLocalCameraDir() {
        return getCachePath(CAMERA_DIR);
    }

    /**
     * 通过URL获取本地缓存的文件路径
     *
     * @param url 文件的网络URL
     * @param dir 文件本地缓存的目录
     */
    public String getLocalFilePath(String url, String dir) {
        String suffix = url.substring(url.lastIndexOf('.'));
        return getCachePath(dir) + Cryptography.md5(url) + suffix;
    }

}
