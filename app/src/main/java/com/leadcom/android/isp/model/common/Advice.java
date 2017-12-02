package com.leadcom.android.isp.model.common;

import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.user.User;

/**
 * <b>功能描述：</b>意见和建议<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/07/28 10:26 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/07/28 10:26 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class Advice extends Model {

    private String content;
    private String createDate;
    private User creator;
    private int state;
    private String reply;
    private User replier;
    private String replyDate;

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

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public User getReplier() {
        return replier;
    }

    public void setReplier(User replier) {
        this.replier = replier;
    }

    public String getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(String replyDate) {
        this.replyDate = replyDate;
    }
}
