package com.gzlk.android.isp.helper.publishable;

import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.activity.Activity;
import com.gzlk.android.isp.model.activity.topic.AppTopic;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.user.Collection;
import com.gzlk.android.isp.model.user.Moment;
import com.gzlk.android.isp.model.user.User;
import com.gzlk.android.isp.nim.session.NimSessionHelper;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;

/**
 * <b>功能描述：</b>收藏相关参数设定<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/11/12 08:54 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/11/12 08:54 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class Collectable {

    public static String creatorId, creatorName, creatorHeader, sourceId, sourceTitle;
    public static int sourceType;

    public static void resetArchiveCollectionParams(Archive archive) {
        sourceType = null == archive.getGroEntity() ? Collection.SourceType.GROUP_ARCHIVE : Collection.SourceType.USER_ARCHIVE;
        sourceTitle = archive.getTitle();
        sourceId = archive.getId();
        creatorId = archive.getUserId();
        creatorName = archive.getUserName();
        creatorHeader = archive.getHeadPhoto();
    }

    public static void resetMomentCollectionParams(Moment moment) {
        sourceId = moment.getId();
        sourceTitle = "";
        sourceType = Collection.SourceType.MOMENT;
        creatorId = moment.getUserId();
        creatorName = moment.getUserName();
        creatorHeader = moment.getHeadPhoto();
    }

    public static void resetSessionCollectionParams(IMMessage message) {
        Model model = NimSessionHelper.getObject(message.getSessionId());
        if (null != model) {
            UserInfoProvider.UserInfo user = NimUIKit.getUserInfoProvider().getUserInfo(message.getFromAccount());
            if (null != user) {
                creatorId = user.getAccount();
                creatorName = user.getName();
                creatorHeader = user.getAvatar();
            }
            if (model instanceof Activity) {
                Activity act = (Activity) model;
                sourceId = act.getId();
                sourceTitle = act.getTitle();
                sourceType = Collection.SourceType.ACTIVITY;
            } else if (model instanceof AppTopic) {
                AppTopic topic = (AppTopic) model;
                sourceId = topic.getId();
                sourceTitle = topic.getTitle();
                sourceType = Collection.SourceType.TOPIC;
            }
        }
    }
}
