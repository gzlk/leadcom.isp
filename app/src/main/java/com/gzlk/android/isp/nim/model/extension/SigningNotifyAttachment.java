package com.gzlk.android.isp.nim.model.extension;

import org.json.JSONObject;

/**
 * <b>功能描述：</b>签到的提醒消息<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/19 15:58 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/19 15:58 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SigningNotifyAttachment extends CustomAttachment {

    public SigningNotifyAttachment() {
        super(AttachmentType.SIGN_NOTIFY);
    }

    /**
     * 提醒类型：
     * <ul>
     * <li>1=准备签到提醒（比如提前5分钟提醒大家签到）</li>
     * <li>2=开始签到提醒</li>
     * <li>3=签到即将结束提醒</li>
     * <li>4=签到已结束（预览签到成果）</li>
     * </ul>
     */
    private int notifyType;
    // 群聊的tid
    private String tid;
    // 签到应用的id
    private String setupId;
    // 提醒内容
    private String content;
    // 开始时间
    private long beginTime;
    // 结束时间
    private long endTime;

    public int getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(int notifyType) {
        this.notifyType = notifyType;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getSetupId() {
        return setupId;
    }

    public void setSetupId(String setupId) {
        this.setupId = setupId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    @Override
    protected void parseData(JSONObject data) {
        super.parseData(data);
        try {
            if (data.has("notifyType")) {
                notifyType = data.getInt("notifyType");
            }
            if (data.has("tid")) {
                tid = data.getString("tid");
            }
            if (data.has("setupId")) {
                setupId = data.getString("setupId");
            }
            if (data.has("content")) {
                content = data.getString("content");
            }
            if (data.has("beginTime")) {
                beginTime = data.getLong("beginTime");
            }
            if (data.has("endTime")) {
                endTime = data.getLong("endTime");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject packData() {
        JSONObject data = super.packData();
        try {
            data.put("notifyType", notifyType)
                    .put("tid", tid)
                    .put("setupId", setupId)
                    .put("content", content)
                    .put("beginTime", beginTime)
                    .put("endTime", endTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

}
