package com.leadcom.android.isp.nim.constant;

/**
 * <b>功能描述：</b>云信相关状态码描述<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/30 16:13 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/03/30 16:13 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class StatusCode {

    public static String getStatus(int code) {
        switch (code) {
            case 200:
                return "操作成功";
            case 201:
                return "客户端版本不对，需升级sdk";
            case 301:
                return "被封禁";
            case 302:
                return "用户名或密码错误";
            case 315:
                return "IP限制";
            case 403:
                return "非法操作或没有权限";
            case 404:
                return "对象不存在";
            case 405:
                return "参数长度过长";
            case 406:
                return "对象只读";
            case 408:
                return "客户端请求超时";
            case 413:
                return "验证失败(短信服务)";
            case 414:
                return "参数错误";
            case 415:
                return "客户端网络问题";
            case 416:
                return "频率控制";
            case 417:
                return "重复操作";
            case 418:
                return "通道不可用(短信服务)";
            case 419:
                return "数量超过上限";
            case 422:
                return "账号被禁用";
            case 431:
                return "HTTP重复请求";
            case 500:
                return "服务器内部错误";
            case 503:
                return "服务器繁忙";
            case 508:
                return "消息撤回时间超限";
            case 509:
                return "无效协议";
            case 514:
                return "服务不可用";
            case 998:
                return "解包错误";
            case 999:
                return "打包错误";
            case 1000:
                return "本地操作异常";

// 群相关状态码
            case 801:
                return "群人数达到上限";
            case 802:
                return "没有权限";
            case 803:
                return "群不存在";
            case 804:
                return "用户不在群";
            case 805:
                return "群类型不匹配";
            case 806:
                return "创建群数量达到限制";
            case 807:
                return "群成员状态错误";
            case 808:
                return "申请成功";
            case 809:
                return "已经在群内";
            case 810:
                return "邀请成功";

            default:
                return "未定义的状态码";
        }
    }
}
