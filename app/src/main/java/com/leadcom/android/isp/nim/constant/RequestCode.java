package com.leadcom.android.isp.nim.constant;

/**
 * <b>功能描述：</b>各种Action的请求代码<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/15 21:11 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/15 21:11 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class RequestCode {
    private static final int BASE_NIM_REQ = 10;
    /**
     * 到群通知列表
     */
    public static final int REQ_NOTICE_LIST = BASE_NIM_REQ;
    /**
     * 创建新的群通知
     */
    public static final int REQ_NOTICE_NEW = BASE_NIM_REQ + 1;
    /**
     * 到签到列表
     */
    public static final int REQ_SIGN_LIST = BASE_NIM_REQ + 2;
    /**
     * 创建新的签到应用
     */
    public static final int REQ_SIGN_NEW = BASE_NIM_REQ + 3;
    /**
     * 到投票列表
     */
    public static final int REQ_VOTE_LIST = BASE_NIM_REQ + 4;
    /**
     * 创建新的投票应用
     */
    public static final int REQ_VOTE_NEW = BASE_NIM_REQ + 5;
    /**
     * 议题列表
     */
    public static final int REQ_TOPIC_LIST = BASE_NIM_REQ + 6;
    /**
     * 创建新议题
     */
    public static final int REQ_TOPIC_NEW = BASE_NIM_REQ + 7;
    /**
     * 会议纪要详情页
     */
    public static final int REQ_MINUTES_DETAILS = BASE_NIM_REQ + 8;
}
