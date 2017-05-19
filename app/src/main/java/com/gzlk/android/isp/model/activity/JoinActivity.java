package com.gzlk.android.isp.model.activity;

import com.gzlk.android.isp.model.Model;

/**
 * <b>功能描述：</b>加入活动邀请<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/19 21:44 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/19 21:44 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class JoinActivity extends Model {
    public static class Field{
        public static final String ActivityId="activityId";
        public static final String ActivityName="activityName";
    }
    //活动
    private String actId;
    private String actName;
    //申请人
    private String appUserId;
    private String appUserName;
    //申请处理人
    private String handleUserId;
    private String handleUserName;
    //申请时间
    private String createTime;

    //申请者发送和审核者回复的消息
    private String msg;
    //处理时间
    private String handleTime;
    //状态
    private String state;
    //uuid 文本消息和申请流程消息公用一个uuid
    private String uuid;
}
