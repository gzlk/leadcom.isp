package com.gzlk.android.isp.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.nim.file.FilePreviewHelper;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MultiImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

/**
 * <b>功能描述：</b>分享到微博<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/21 18:46 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/21 18:46 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ShareToWeiBo extends Shareable implements WbShareCallback {

    private WbShareHandler shareHandler;

    @Override
    public void onWbShareSuccess() {
        ToastHelper.make().showMsg(R.string.ui_base_share_text_share_to_weibo_complete);
    }

    @Override
    public void onWbShareCancel() {
        ToastHelper.make().showMsg(R.string.ui_base_share_text_share_to_weibo_cancel);
    }

    @Override
    public void onWbShareFail() {
        ToastHelper.make().showMsg(R.string.ui_base_share_text_share_to_weibo_failed);
    }

    public static ShareToWeiBo init(Context activityContext) {
        return new ShareToWeiBo(activityContext);
    }

    private static SoftReference<ShareToWeiBo> instance;

    private ShareToWeiBo(Context context) {
        shareHandler = new WbShareHandler((Activity) context);
        shareHandler.registerApp();
        shareHandler.setProgressColor(0xff33b5e5);
        instance = new SoftReference<>(this);
    }

    public static ShareToWeiBo instance() {
        if (null != instance) {
            return instance.get();
        }
        return null;
    }

    public void onNewInstance(Intent intent) {
        if (null != instance && null != instance.get()) {
            instance.get().shareHandler.doResultIntent(intent, this);
        }
    }

    /**
     * 分享纯文字或图文
     */
    public void share(String textContent, ArrayList<String> images) {
        if (isEmpty(textContent) && (null == images || images.size() < 1)) {
            throw new IllegalArgumentException("Cannot share blank text and empty image to WeiBo.");
        }
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        if (!isEmpty(textContent)) {
            weiboMessage.textObject = getTextObject(textContent);
        }
        if (null != images) {
            if (images.size() <= 1) {
                weiboMessage.imageObject = getImageObject(images.get(0));
            } else {
                weiboMessage.multiImageObject = getImageObject(images);
            }
        }
        shareHandler.shareMessage(weiboMessage, false);
    }

    private TextObject getTextObject(String text) {
        TextObject textObject = new TextObject();
        textObject.text = text;
        textObject.title = "xxxx";
        textObject.actionUrl = "http://www.baidu.com";
        return textObject;
    }

    private ImageObject getImageObject(String url) {
        String local = getLocalUrl(url);
        ImageObject imageObject = new ImageObject();
        Bitmap bitmap = BitmapFactory.decodeFile(local);
        imageObject.setImageObject(bitmap);
        return imageObject;
    }

    private String getLocalUrl(String url) {
        if (Utils.isUrl(url)) {
            return getLocalPath(url);
        }
        return url;
    }

    private MultiImageObject getImageObject(ArrayList<String> urls) {
        MultiImageObject multiImageObject = new MultiImageObject();
        ArrayList<Uri> uris = new ArrayList<>();
        for (String url : urls) {
            String local = getLocalUrl(url);
            Uri uri = FilePreviewHelper.getUriFromFile(local);
            uris.add(uri);
        }
        multiImageObject.setImageList(uris);
        return multiImageObject;
    }
}
