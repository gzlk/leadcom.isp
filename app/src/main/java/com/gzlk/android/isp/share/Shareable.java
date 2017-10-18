package com.gzlk.android.isp.share;

import android.support.annotation.IntDef;

import com.gzlk.android.isp.helper.StringHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

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
    public static final int TO_WX_CHAT = 3;
    public static final int TO_WX_MOMENT = 4;
    public static final int TO_WEIBO = 5;

    /**
     * 分享类型
     */
    @IntDef({TO_QQ, TO_QZONE, TO_WX_CHAT, TO_WX_MOMENT, TO_WEIBO})
    public @interface ShareType {

    }

    protected static boolean isEmpty(String string) {
        return StringHelper.isEmpty(string);
    }

    public static String getLocalPath(String imageUrl) {
        File file = ImageLoader.getInstance().getDiskCache().get(imageUrl);
        if (null != file) {
            return file.getPath();
        }
        return null;
    }
}
