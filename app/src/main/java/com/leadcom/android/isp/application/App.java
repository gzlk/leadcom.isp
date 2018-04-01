package com.leadcom.android.isp.application;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.hlk.hlklib.lib.emoji.EmojiUtility;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.crash.AppCrashHandler;
import com.leadcom.android.isp.crash.storage.StorageUtil;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.main.PersonalityFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.user.User;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.tencent.smtt.sdk.QbSdk;

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

    private static App instance;

    /**
     * 获取全局Application实例
     */
    public static App app() {
        return instance;
//        try {
//            App app = (App) getApplicationUsingReflectionOfAppGlobals();
//            if (null == app) {
//                app = (App) getApplicationUsingReflectionOfActivityThread();
//            }
//            return app;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
    }

    /**
     * 点击UI中的动画
     */
    public static Animation clickAnimation() {
        return AnimationUtils.loadAnimation(app(), R.anim.item_click);
    }

    public static void openUserInfo(Context context, String userId) {
        if (!Cache.cache().userId.equals(userId)) {
            PersonalityFragment.open(context, userId);
        }
    }

    public static void openUserInfo(BaseFragment fragment, String userId) {
        if (!Cache.cache().userId.equals(userId)) {
            PersonalityFragment.open(fragment, userId);
        }
    }

    private static final int MAX_FAILED_TIME = 10;
    private int okHttpFailedTimes = 0;

    public synchronized void clearOkHttpFailedTimes() {
        if (okHttpFailedTimes > 5) {
            okHttpFailedTimes = 5;
        }
        okHttpFailedTimes--;
        if (okHttpFailedTimes <= 0) {
            okHttpFailedTimes = 0;
        }
    }

    public synchronized int getOkHttpFailedTimes() {
        return okHttpFailedTimes;
    }

    public synchronized void increaseOkHttpFailedTimes() {
        okHttpFailedTimes++;
    }

    /**
     * 是否需要设置 http 连接的 close 表头
     */
    public boolean needSetConnectionCloseHeader() {
        return okHttpFailedTimes >= MAX_FAILED_TIME;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        AppCrashHandler.getInstance(this);
        initializeX5();
        initializeNim();
        if (shouldInit()) {
            WbSdk.install(this, new AuthInfo(this, StringHelper.getString(R.string.weibo_app_key), "https://api.weibo.com/oauth2/default.html", ""));
            StorageUtil.init(this, null);
            EmojiUtility.setDefaultTextSize(getResources().getDimensionPixelSize(R.dimen.ui_base_text_size));
            initializeImageLoader();
            initializeDatabase();
        }
    }

    private boolean x5Usable = false;

    /**
     * 疼熏内核文件浏览是否可用
     */
    public boolean isX5Usable() {
        return x5Usable;
    }

    private void initializeX5() {
        // 初始化疼熏X5浏览器内核
        QbSdk.setDownloadWithoutWifi(true);
        QbSdk.initX5Environment(this, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                log("X5 environment core initialize finished.");
                //x5Usable = true;
            }

            @Override
            public void onViewInitFinished(boolean b) {
                log("X5 onViewInitFinished: " + b);
                x5Usable = b;
            }
        });
    }

    private void initializeImageLoader() {

        DisplayImageOptions option = new DisplayImageOptions.Builder()
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
                .defaultDisplayImageOptions(option)
                //.writeDebugLogs()
                .build();

        ImageLoader.getInstance().init(config);
    }

    /**
     * 初始化本地缓存数据库
     */
    private void initializeDatabase() {
        if (isEmpty(Cache.cache().userId)) {
            Cache.cache().restoreCached();
        }
        if (!isEmpty(Cache.cache().userId)) {
            Cache.cache().restoreGroups();
            // 初始化个性化数据库
            initializeLiteOrm(Cache.cache().userId);
            // 查询个性化数据库中的个人信息
            if (null == Cache.cache().me || !Cache.cache().userId.equals(Cache.cache().me.getId())) {
                Cache.cache().setCurrentUser(dao().query(Cache.cache().userId));
            }
        }
    }

    private Dao<User> userDao;

    private Dao<User> dao() {
        // dao 为空或者 orm 跟当前 app 里的 orm 不是同一个实例时，要重新初始化 dao
        if (null == userDao || userDao.orm != Orm) {
            userDao = new Dao<>(User.class);
        }
        return userDao;
    }

    public void saveCurrentUser(User user) {
        if (null != user) {
            // 尝试可能因用户id不同而重新初始化缓存数据库
            initializeLiteOrm(user.getId());
        }
        dao().save(user);
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
