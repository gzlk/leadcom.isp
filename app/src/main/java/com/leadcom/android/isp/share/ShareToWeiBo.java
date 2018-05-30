package com.leadcom.android.isp.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.helper.FilePreviewHelper;
import com.leadcom.android.isp.helper.LogHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MultiImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoSourceObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.sina.weibo.sdk.utils.Utility;

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
    private Context ctx;

    private ShareToWeiBo(Context context) {
        ctx = context;
        shareHandler = new WbShareHandler((Activity) ctx);
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
            Bundle bundle = intent.getExtras();
            if (null != bundle) {
                int resultCode = bundle.getInt("_weibo_resp_errcode", -1);
                if (resultCode != 0) {
                    String msg = bundle.getString("_weibo_resp_errstr", "");
                    LogHelper.log("Weibo", StringHelper.format("Weibo share failed(%d): %s", resultCode, msg));
                }
            }
        }
    }

    public static void clear() {
        if (null != instance) {
            instance.clear();
            instance = null;
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
        //if (!isEmpty(textContent)) {
        weiboMessage.textObject = getTextObject(textContent, "", "");
        //}
        if (null != images) {
            if (images.size() <= 1) {
                weiboMessage.imageObject = getImageObject(images.get(0));
            } else {
                if (WbSdk.supportMultiImage(ctx)) {
                    weiboMessage.multiImageObject = getImageObject(images);
                } else {
                    ToastHelper.make().showMsg(R.string.ui_base_share_text_share_to_weibo_not_support_multi_image);
                    return;
                }
            }
        }
        shareHandler.shareMessage(weiboMessage, false);
    }

    /**
     * 分享档案到微博
     */
    public void share(String title, String summary, String targetUrl, String image) {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = title;
        mediaObject.description = summary;
        // 设置 Bitmap 类型的图片到视频对象里设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        mediaObject.thumbData = getThumb(image);
        mediaObject.actionUrl = targetUrl;
        mediaObject.defaultText = title;

        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        weiboMessage.mediaObject = mediaObject;
        weiboMessage.textObject = getTextObject(title, title, targetUrl);
        shareHandler.shareMessage(weiboMessage, false);
    }

    private TextObject getTextObject(String text, String title, String action) {
        TextObject textObject = new TextObject();
        textObject.text = StringHelper.getString(R.string.ui_base_share_title, text);// 显示在文本编辑框里的内容
        textObject.title = title;
        textObject.actionUrl = isEmpty(action) ? "" : action;
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

    /**
     * 视频分享
     */
    private VideoSourceObject getVideoObject(String url) {
        if (Utils.isUrl(url)) {
            throw new IllegalArgumentException("Cannot share video with http url, please use local path.");
        }
        VideoSourceObject videoSourceObject = new VideoSourceObject();
        videoSourceObject.videoPath = FilePreviewHelper.getUriFromFile(url);
        return videoSourceObject;
    }
}
