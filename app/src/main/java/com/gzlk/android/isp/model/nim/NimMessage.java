package com.gzlk.android.isp.model.nim;

import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;

/**
 * <b>功能描述：</b>自定义网易云信消息<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/18 23:18 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/18 23:18 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class NimMessage implements MsgAttachment {

    /**
     * 自定义消息类型
     */
    public interface Type {
        /**
         * 新成员申请加入组织
         */
        int JOIN_TO_GROUP = 4;
        /**
         * 批准新成员加入
         */
        int APPROVE_JOIN_GROUP = 6;
        /**
         * 不批准新成员加入
         */
        int DISAPPROVE_JOIN_GROUP = 7;
        /**
         * 邀请新成员加入组织
         */
        int INVITE_TO_GROUP = 8;
        /**
         * 新成员同意加入组织
         */
        int AGREE_TO_GROUP = 9;
        /**
         * 新成员不同意加入组织
         */
        int DISAGREE_TO_GROUP = 10;
        /**
         * 小组成员邀请小组外人员加入小组
         */
        int INVITE_TO_SQUAD = 19;
        /**
         * 被邀请者同意加入小组
         */
        int AGREE_TO_SQUAD = 20;
        /**
         * 被邀请者拒绝加入小组
         */
        int DISAGREE_TO_SQUAD = 21;
    }

    // 自定义消息类型
    private int type;
    // 自定义消息标题
    private String msgTitle;
    // 自定义消息内容
    private String msgContent;
    // 自定义消息id
    private String uuid;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMsgTitle() {
        return msgTitle;
    }

    public void setMsgTitle(String msgTitle) {
        this.msgTitle = msgTitle;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toJson(boolean b) {
        return NimMessageParser.packData(this);
    }
}
