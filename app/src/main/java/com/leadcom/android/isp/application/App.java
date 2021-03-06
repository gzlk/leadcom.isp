package com.leadcom.android.isp.application;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.hlk.hlklib.lib.emoji.EmojiUtility;
import com.leadcom.android.isp.BuildConfig;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.archive.ArchiveRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.user.PermissionRequest;
import com.leadcom.android.isp.api.user.UserRequest;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.main.PersonalityFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.listener.OnTaskCompleteListener;
import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.common.Contact;
import com.leadcom.android.isp.model.organization.RelateGroup;
import com.leadcom.android.isp.model.user.User;
import com.leadcom.android.isp.service.ContactService;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.tencent.bugly.Bugly;
import com.tencent.smtt.sdk.QbSdk;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

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

public class App extends BaseActivityManagedApplication {

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

    public static void openUserInfo(Context context, String userId, String groupId) {
        //if (!Cache.cache().userId.equals(userId)) {
        PersonalityFragment.open(context, userId, groupId);
        //}
    }

    public static void openUserInfo(BaseFragment fragment, String userId) {
        openUserInfo(fragment, userId, "");
    }

    public static void openUserInfo(BaseFragment fragment, String userId, String groupId) {
        //if (!Cache.cache().userId.equals(userId)) {
        PersonalityFragment.open(fragment, userId, groupId);
        //}
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

    /**
     * true=普通正常服务器, false=后台测试自己的服务器
     */
    private boolean isNormalApi = true;

    /**
     * 是否正常api
     */
    public boolean isNormalApi() {
        return isNormalApi;
    }

    /**
     * 设置后台api服务器
     *
     * @param normalApi true=普通正常服务器, false=后台测试自己的服务器
     */
    public void setApiDebug(boolean normalApi) {
        isNormalApi = normalApi;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initOrResumeJPush();
        Bugly.init(getApplicationContext(), StringHelper.getString(R.string.tencent_app_id_bugly), !Cache.isReleasable());
        //initializeX5();
        //initializeNim();
        if (shouldInit()) {
            WbSdk.install(this, new AuthInfo(this, StringHelper.getString(R.string.weibo_app_key), "https://api.weibo.com/oauth2/default.html", ""));
            EmojiUtility.setDefaultTextSize(getResources().getDimensionPixelSize(R.dimen.ui_base_text_size));
            initializeImageLoader();
            initializeDatabase();
            // 读取本地缓存中的联系人列表
            ContactService.start(false);
        }
    }

    private void initOrResumeJPush() {
        if (JPushInterface.isPushStopped(this)) {
            JPushInterface.resumePush(this);
        } else {
            JPushInterface.setDebugMode(true);
            JPushInterface.init(this);
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
            Bugly.setUserId(this, Cache.cache().userPhone + "(" + BuildConfig.BUILD_TYPE + ")");
            Cache.cache().restoreGroups();
            // 初始化个性化数据库
            initializeLiteOrm(Cache.cache().userId);
            // 查询个性化数据库中的个人信息
            if (null == Cache.cache().me || !Cache.cache().userId.equals(Cache.cache().me.getId())) {
                Cache.cache().setCurrentUser(dao().query(Cache.cache().userId));
            }
            // 此时如果用户信息还是为空的话，则需要拉取
            if (null == Cache.cache().me) {
                fetchingMe();
            }
        }
        setJPushAlias();
    }

    private void fetchingMe() {
        UserRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<User>() {
            @Override
            public void onResponse(User user, boolean success, String message) {
                super.onResponse(user, success, message);
                if (success && null != user) {
                    Cache.cache().setCurrentUser(user);
                }
            }
        }).find(Cache.cache().userId, "", true);
    }

    /**
     * 设置极光推送的别名
     */
    public void setJPushAlias() {
        if (!isEmpty(Cache.cache().userId)) {
            JPushInterface.setAlias(App.app(), 0, format("%s%s", (Cache.isReleasable() ? "product" : "test"), Cache.cache().userId));
        }
        initOrResumeJPush();
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
        //NIMClient.getService(AuthService.class).logout();
        JPushInterface.deleteAlias(this, 0);
        JPushInterface.stopPush(this);
        super.logout();
    }

    /**
     * 拉取我的权限列表并缓存下来
     */
    public void fetchPermissions() {
        PermissionRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<RelateGroup>() {
            @Override
            public void onResponse(List<RelateGroup> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    Cache.cache().resetRelatedGroups(list);
                }
            }
        }).list();
    }

    private ArrayList<Contact> contacts = new ArrayList<>();

    public synchronized ArrayList<Contact> getContacts() {
        return contacts;
    }

    private ArrayList<Archive> activities = new ArrayList<>();

    public synchronized ArrayList<Archive> getActivities() {
        return activities;
    }

    public synchronized void fetchingActivities(final String groupId, final OnTaskCompleteListener completeListener) {
        ArchiveRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Archive>() {
            @Override
            public void onResponse(List<Archive> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success && null != list) {
                    for (Archive archive : list) {
                        archive.setDocType(Archive.ArchiveType.ACTIVITY);
                        if (isEmpty(archive.getGroupId())) {
                            archive.setGroupId(groupId);
                        }
                        if (isEmpty(archive.getId())) {
                            archive.setId(archive.getGroActivityId());
                        }
                        if (!activities.contains(archive)) {
                            activities.add(archive);
                        }
                    }
                }
                if (null != completeListener) {
                    completeListener.onComplete();
                }
            }
        }).listActivities(groupId, groupId, 0);
    }

    public synchronized Archive getActivity(String activityId) {
        for (Archive archive : activities) {
            if (archive.getId().equals(activityId)) {
                return archive;
            }
        }
        return null;
    }
}
