package com.gzlk.android.isp.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.gzlk.android.isp.BuildConfig;
import com.gzlk.android.isp.helper.LogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.hlk.hlklib.etc.Cryptography;

import java.io.File;
import java.io.IOException;
import java.util.List;
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

    protected static final String TAG = "Application";

    protected static BaseApplication getApplicationUsingReflectionOfActivityThread() throws Exception {
        return (BaseApplication) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null, (Object[]) null);
    }

    protected static BaseApplication getApplicationUsingReflectionOfAppGlobals() throws Exception {
        return (BaseApplication) Class.forName("android.app.AppGlobals").getMethod("getInitialApplication").invoke(null, (Object[]) null);
    }

    /**
     * 获取application中配置的meta-data值
     */
    public String getMetadata(String name) {
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            return appInfo.metaData.getString(name);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取当前apk的版本号
     */
    public String version() {
        String version = "";
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            version = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    protected boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        boolean ret = false;
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                ret = true;
                break;
            }
        }
        return ret;
    }

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

    protected static boolean isEmpty(String text) {
        return StringHelper.isEmpty(text);
    }

    protected static void log(String text) {
        LogHelper.log(TAG, text);
    }

    protected static String format(String fmt, Object... objects) {
        return StringHelper.format(fmt, objects);
    }

    public static final String ROOT_DIR = "leadcom";

    public static final String NOMEDIA = ".nomedia";
    /**
     * 本地缓存路径
     */
    public static final String CACHE_DIR = "cache";
    /**
     * 本地Office文档缓存路径
     */
    public static final String ARCHIVE_DIR = "archive";
    /**
     * 本地数据库缓存目录
     */
    public static final String DB_DIR = "database";
    /**
     * 本地相机拍照之后照片缓存目录
     */
    public static final String CAMERA_DIR = "camera";
    /**
     * 本地剪切之后的照片缓存目录
     */
    public static final String CROPPED_DIR = "cropped";
    /**
     * 本地图片缓存目录
     */
    public static final String IMAGE_DIR = "images";
    /**
     * 本地UIL图片缓存目录
     */
    public static final String IMAGE_UIL = "uil-images";
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
     * word 转换成 html 的目录
     */
    public static final String HTML_DIR = "html";

    /**
     * 获取外挂 SD 卡目录下的 data 目录，末尾不包含/<br>
     * 如果外置SD卡不可读则转为获取内置缓存目录
     */
    public String gotExternalCacheDir() {
        String path = null;
        try {
            // 获取外置SD卡中的缓存目录
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                path = Environment.getExternalStorageDirectory().getPath();
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
        if (BuildConfig.RELEASEABLE && dir.equals(DB_DIR)) {
            // release时，db文件放在内置私有目录下
            sb.append(gotCacheDir());
        } else {
            //if (dir.equals(DB_DIR)) {
            // 缓存数据库和图像都存在内置app私有空间里
            //    sb.add(gotCacheDir());
            //} else {
            sb.append(gotExternalCacheDir());
            //}
        }
        sb.append("/").append(ROOT_DIR).append("/").append(dir).append("/");
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
        if (!file.exists()) {
            file.mkdirs();
        }
        if (dirs.endsWith(IMAGE_DIR + "/") || dirs.endsWith(HTML_DIR + "/") || dirs.endsWith(CROPPED_DIR + "/") || dirs.endsWith(CAMERA_DIR + "/")) {
            // 本地图片文件夹增加 .nomedia
            String path = dirs + NOMEDIA;
            File f = new File(path);
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 通过UUID返回随机字符串
     */
    public static String getRandomUUID() {
        return UUID.randomUUID().toString();
    }

    public String getTempLocalPath(String path) {
        return getCachePath(path) + "/" + getRandomUUID();
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
     * 获取本地剪切之后照片缓存目录
     */
    public String getLocalCroppedDir() {
        return getCachePath(CROPPED_DIR);
    }

    /**
     * 通过URL获取本地缓存的文件路径
     *
     * @param url 文件的网络URL
     * @param dir 文件本地缓存的目录
     */
    public String getLocalFilePath(String url, String dir) {
        int dotIndex = url.lastIndexOf('.');
        if (dotIndex < url.length() - 5) {
            // 网易云信的文档url没有后缀名
            dotIndex = -1;
        }
        String suffix = "";
        if (dotIndex >= 0) {
            suffix = url.substring(url.lastIndexOf('.'));
        }
        return getCachePath(dir) + Cryptography.md5(url) + suffix;
    }

}
