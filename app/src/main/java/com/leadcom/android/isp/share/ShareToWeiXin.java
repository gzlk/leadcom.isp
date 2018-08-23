package com.leadcom.android.isp.share;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.helper.FilePreviewHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.organization.Concern;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>分享内容到微信<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/21 11:02 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/21 11:02 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ShareToWeiXin extends Shareable {

    public static final String APP_ID = StringHelper.getString(R.string.tencent_app_id_wx);

    private static IWXAPI wxapi;


    private static boolean regToWX(Context context) {
        TAG = "WXShare";
        wxapi = WXAPIFactory.createWXAPI(context, APP_ID);
        if (wxapi.isWXAppInstalled()) {
            if (wxapi.isWXAppSupportAPI()) {
                return wxapi.registerApp(APP_ID);
            } else {
                ToastHelper.make().showMsg(R.string.ui_base_share_text_share_to_wx_not_support_api);
                return false;
            }
        } else {
            ToastHelper.make().showMsg(R.string.ui_base_share_text_share_to_wx_not_installed);
            return false;
        }
    }

    /**
     * 分享内容到微信
     *
     * @param type 类型，支持分享到微信会话、朋友圈、微信收藏三种
     */
    public static void shareToWeiXin(Context activityContext, @ShareType int type, String title, String text, ArrayList<String> images) {
        if (isEmpty(text) && (null == images || images.size() < 1)) {
            ToastHelper.make().showMsg(R.string.ui_base_share_text_share_blank);
            return;
        }
        if (regToWX(activityContext)) {
            switch (type) {
                case TO_WX_SESSION:
                    shareToWeiXinSession(activityContext, title, text, images);
                    break;
                case TO_WX_TIMELINE:
                    shareToWeiXinTimeline(activityContext, title, text, images);
                    break;
                case TO_WX_FAVORITE:
                    break;
                default:
                    break;
            }
        } else {
            ToastHelper.make().showMsg(R.string.ui_base_share_text_share_to_wx_register_failed);
        }
    }

    /**
     * 分享档案内容到微信
     */
    public static void shareToWeiXin(Context context, @ShareType int type, String title, String description, String url, String image) {
        if (isEmpty(title) || isEmpty(url)) {
            ToastHelper.make().showMsg(R.string.ui_base_share_text_share_webpage_blank);
            return;
        }
        if (regToWX(context)) {
            WXWebpageObject webpage = new WXWebpageObject();
            webpage.webpageUrl = url;

            WXMediaMessage msg = new WXMediaMessage(webpage);
            msg.title = title;
            msg.description = description;
            msg.thumbData = getThumb(image);

            switch (type) {
                case TO_WX_SESSION:
                    sendMessage(msg, SendMessageToWX.Req.WXSceneSession);
                    break;
                case TO_WX_TIMELINE:
                    sendMessage(msg, SendMessageToWX.Req.WXSceneTimeline);
                    break;
                case TO_WX_FAVORITE:
                    break;
                default:
                    break;
            }
        } else {
            ToastHelper.make().showMsg(R.string.ui_base_share_text_share_to_wx_register_failed);
        }
    }

    /**
     * 分享到会话
     */
    private static void shareToWeiXinSession(Context context, String title, String text, ArrayList<String> images) {
        if (images.size() > 0) {
            if (images.size() > 1) {
                shareMultipleImageToWeiXinSession(context, images);
            } else {
                // 发送单个图片到聊天对象
                sendMessage(getSingleImageObject(title, text, images.get(0)), SendMessageToWX.Req.WXSceneSession);
            }
        } else if (!isEmpty(text)) {
            // 文字不为空时发送文字到聊天对象
            sendMessage(getTextObject(title, text), SendMessageToWX.Req.WXSceneSession);
        }
    }

    private static void shareMultipleImageToWeiXinSession(Context context, ArrayList<String> images) {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/jpg");
        ArrayList<Uri> imageUris = new ArrayList<>();
        for (String url : images) {
            String ext = Attachment.getExtension(url);
            String local = getLocalPath(url);
            if (!StringHelper.isEmpty(local)) {
                local = compressSources(local, ext);
                Uri uri = FilePreviewHelper.getUriFromFile(local);
                if (null != uri) {
                    imageUris.add(uri);
                }
            }
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        context.startActivity(Intent.createChooser(intent, "分享图片"));
    }

    /**
     * 分享到朋友圈
     */
    private static void shareToWeiXinTimeline(Context context, String title, String text, ArrayList<String> images) {
        if (images.size() > 0) {
            if (images.size() > 1) {
                // 发送多张图片到朋友圈
                shareMultimediaMessageToWeiXinTimeLine(context, text, images);
            } else {
                // 发送单个图片到朋友圈
                sendMessage(getSingleImageObject(title, text, images.get(0)), SendMessageToWX.Req.WXSceneTimeline);
            }
        } else if (!isEmpty(text)) {
            // 文字不为空时发送文字到聊天对象
            sendMessage(getTextObject(title, text), SendMessageToWX.Req.WXSceneTimeline);
        }
    }

    /**
     * 发送图文信息到朋友圈
     */
    private static void shareMultimediaMessageToWeiXinTimeLine(Context context, String title, ArrayList<String> images) {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/jpg");
        intent.putExtra("Kdescription", title);
        ArrayList<Uri> imageUris = new ArrayList<>();
        for (String url : images) {
            String local = getLocalPath(url);
            if (!StringHelper.isEmpty(local)) {
                String ext = Attachment.getExtension(url);
                local = compressSources(local, ext);
                Uri uri = FilePreviewHelper.getUriFromFile(local);
                if (null != uri) {
                    imageUris.add(uri);
                }
            }
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        context.startActivity(intent);
    }

    private static String getScene(int scene) {
        switch (scene) {
            case SendMessageToWX.Req.WXSceneSession:
                return "WX Session";
            case SendMessageToWX.Req.WXSceneTimeline:
                return "WX Timeline";
            default:
                return "WX Favorite";
        }
    }

    private static void sendMessage(WXMediaMessage message, int scene) {
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = message;
        req.scene = scene;
        if (!wxapi.sendReq(req)) {
            log(StringHelper.format("Share to %s failed.", getScene(scene)));
        }
    }

    private static WXMediaMessage getTextObject(String title, String text) {
        WXTextObject object = new WXTextObject();
        object.text = text;

        WXMediaMessage message = new WXMediaMessage();
        message.mediaObject = object;
        message.description = text;
        message.title = title;

        return message;
    }

    private static WXMediaMessage getSingleImageObject(String title, String description, String url) {
        String localPath = url.charAt(0) == '/' ? url : getLocalPath(url);
        localPath = compressSources(localPath, "jpg");
        Bitmap bitmap = BitmapFactory.decodeFile(localPath);
        WXImageObject object = new WXImageObject(bitmap);

        WXMediaMessage message = new WXMediaMessage();
        message.title = title;
        message.description = description;
        message.mediaObject = object;
        message.thumbData = getThumb(url);
        log("WX compressed thumb size = " + Utils.formatSize(message.thumbData.length));
        return message;
    }

}
