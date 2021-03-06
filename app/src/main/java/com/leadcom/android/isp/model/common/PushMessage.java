package com.leadcom.android.isp.model.common;

import com.google.gson.reflect.TypeToken;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.model.Model;

/**
 * <b>功能描述：</b>推送消息<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/05/22 09:49 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/05/22 09:49 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class PushMessage extends Model {

    public static PushMessage fromJson(String json) {
        return Json.gson().fromJson(isEmpty(json) ? "{}" : json, new TypeToken<PushMessage>() {
        }.getType());
    }

    public static class Extra {

        public static Extra fromJson(String json) {
            return Json.gson().fromJson(isEmpty(json) ? "{}" : json, new TypeToken<Extra>() {
            }.getType());
        }

        private String msgId;
        private String docId;
        private int docType;
        private String docUserId;
        private String groupId;
        private String userMmtId;
        private String messageCode;
        private String groActivityId;
        private String fromGroupId;
        private String h5;
        private String expendFlowerId;

        public String getMsgId() {
            return msgId;
        }

        public void setMsgId(String msgId) {
            this.msgId = msgId;
        }

        public String getDocId() {
            return docId;
        }

        public void setDocId(String docId) {
            this.docId = docId;
        }

        public int getDocType() {
            return docType;
        }

        public void setDocType(int docType) {
            this.docType = docType;
        }

        public String getDocUserId() {
            return docUserId;
        }

        public void setDocUserId(String docUserId) {
            this.docUserId = docUserId;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getFromGroupId() {
            return fromGroupId;
        }

        public void setFromGroupId(String fromGroupId) {
            this.fromGroupId = fromGroupId;
        }

        public String getUserMmtId() {
            return userMmtId;
        }

        public void setUserMmtId(String userMmtId) {
            this.userMmtId = userMmtId;
        }

        public String getMessageCode() {
            return messageCode;
        }

        public void setMessageCode(String messageCode) {
            this.messageCode = messageCode;
        }

        public String getGroActivityId() {
            return groActivityId;
        }

        public void setGroActivityId(String groActivityId) {
            this.groActivityId = groActivityId;
        }

        public String getH5() {
            return h5;
        }

        public void setH5(String h5) {
            this.h5 = h5;
        }

        public String getExpendFlowerId() {
            return expendFlowerId;
        }

        public void setExpendFlowerId(String expendFlowerId) {
            this.expendFlowerId = expendFlowerId;
        }
    }

    /**
     * 消息代码
     */
    public interface MsgCode {
        /**
         * 用户档案被评论
         */
        String USER_DOC_COMMENT = "USERDOC_COMMENT_CODE";
        /**
         * 用户档案被赞
         */
        String USER_DOC_LIKE = "USERDOC_LIKE_CODE";
        /**
         * 用户动态被评论
         */
        String USER_MMT_COMMENT = "USERMMT_COMMENT_CODE";
        /**
         * 用户动态被赞
         */
        String USER_MMT_LIKE = "USERMMT_LIKE_CODE";
        /**
         * 组织档案被评论
         */
        String GROUP_DOC_COMMENT = "GRODOC_COMMENT_CODE";
        /**
         * 组织档案被赞
         */
        String GROUP_DOC_LIKE = "GRODOC_LIKE_CODE";
        /**
         * 组织档案推送
         */
        String GROUP_DOC_TRANSPORT = "GRODOC_TRANSPOND_CODE";
        /**
         * 组织档案草稿分享
         */
        String GROUP_DOC_SHARE = "GRODOC_SHARE_CODE";
        /**
         * 组织流转的档案被回复了
         */
        String GROUP_DOC_REPLY = "GROUP_REPLY_CODE";
        /**
         * 组织被关注
         */
        String GROUP_ATTENTION = "GROUP_ATTENTION_CODE";
        /**
         * 组织被别的组织取消关注了
         */
        String GROUP_ATTENTION_CANCEL = "GROUP_ATTENTION_CANCEL_CODE";
        /**
         * 组织资料授权
         */
        String GROUP_AUTHORIZE = "GROUP_AUTHORIZE_CODE";
        /**
         * 组织被别的组织取消授权了
         */
        String GROUP_AUTHORIZE_CANCEL = "GROUP_AUTHORIZE_CANCEL_CODE";
        /**
         * 活动发布通知
         */
        String GROUP_ACTIVITY_PUBLISH = "GROUP_ACTIVITY_NOTICE_ADD_CODE";
        /**
         * 活动下发通知
         */
        String GROUP_ACTIVITY_DELIVER = "GROUP_ACTIVITY_NOTICE_ISSUE_CODE";
        /**
         * 活动回复通知
         */
        String GROUP_ACTIVITY_REPLY = "GROUP_ACTIVITY_REPLY_CODE";
        /**
         * 财务审批
         */
        String GROUP_FINANCE_EXPEND_CODE = "EXPEND_FLOWER_CODE";
    }

    private String userId, userName, headPhoto, templateCode, title, content, createDate;
    private int status;
    private Extra extras;

    @Override
    public boolean isRead() {
        return status > 0;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHeadPhoto() {
        return headPhoto;
    }

    public void setHeadPhoto(String headPhoto) {
        this.headPhoto = headPhoto;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Extra getExtras() {
        return extras;
    }

    public void setExtras(Extra extras) {
        this.extras = extras;
    }
}
