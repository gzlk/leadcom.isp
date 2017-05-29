package com.gzlk.android.isp.application;

import android.graphics.Bitmap;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.crash.AppCrashHandler;
import com.gzlk.android.isp.crash.storage.StorageUtil;
import com.gzlk.android.isp.helper.PreferenceHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.user.User;
import com.hlk.hlklib.lib.emoji.EmojiUtility;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.File;

/**
 * <b>功能描述：</b>Application类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/04 19:21 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/04 19:21 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class App extends NimApplication {

    /**
     * 获取全局Application实例
     */
    public static App app() {
        try {
            App app = (App) getApplicationUsingReflectionOfAppGlobals();
            if (null == app) {
                app = (App) getApplicationUsingReflectionOfActivityThread();
            }
            return app;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppCrashHandler.getInstance(this);
        initializeNim();
        if (shouldInit()) {
            StorageUtil.init(this, null);
            EmojiUtility.setDefaultTextSize(getResources().getDimensionPixelSize(R.dimen.ui_base_text_size));
            initializeImageLoader();
            initializeDatabase();
        }
    }

    private DisplayImageOptions smallOption, largeOption;

    public DisplayImageOptions getLargeOption() {
        return largeOption;
    }

    private void initializeImageLoader() {
        if (null == smallOption) {
            smallOption = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.mipmap.img_image_default)
                    .showImageForEmptyUri(R.mipmap.img_image_loading_fail)
                    .showImageOnFail(R.mipmap.img_image_loading_fail)
                    //.delayBeforeLoading(100)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    //.displayer(new FadeInBitmapDisplayer(100))
                    .build();
        }
        if (null == largeOption) {
            largeOption = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.mipmap.img_image_default)
                    .showImageForEmptyUri(R.mipmap.img_image_loading_fail)
                    .showImageOnFail(R.mipmap.img_image_loading_fail)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .displayer(new FadeInBitmapDisplayer(100))
                    .build();
        }
        File cacheDir = new File(getCachePath(IMAGE_UIL));
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(this)
                .denyCacheImageMultipleSizesInMemory()
                // 缓存图片的最大尺寸为480x800
                .memoryCacheExtraOptions(480, 800)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCache(new UnlimitedDiskCache(cacheDir))
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .defaultDisplayImageOptions(smallOption)
                //.writeDebugLogs()
                .build();

        ImageLoader.getInstance().init(config);
    }

    /**
     * 初始化本地缓存数据库
     */
    private void initializeDatabase() {
        if (StringHelper.isEmpty(Cache.cache().userId)) {
            Cache.cache().userId = PreferenceHelper.get(R.string.pf_last_login_user_id);
        }
        if (!StringHelper.isEmpty(Cache.cache().userId)) {
            // 初始化个性化数据库
            initializeLiteOrm(Cache.cache().userId);
            // 查询个性化数据库中的个人信息
            if (null == Cache.cache().me || !Cache.cache().userId.equals(Cache.cache().me.getId())) {
                Cache.cache().setCurrentUser(new Dao<>(User.class).query(Cache.cache().userId));
            }
        }
    }

    @Override
    public void logout() {
        // 清空当前登录的用户信息
        Cache.cache().clear();
        // 同时退出网易云
        NIMClient.getService(AuthService.class).logout();
        super.logout();
    }
}
