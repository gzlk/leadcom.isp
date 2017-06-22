package com.gzlk.android.isp.api;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/23 01:56 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/23 01:56 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class Api<T> extends BaseApi {

    // code = 000000 表示成功，code=1xxxxx表示失败
    private String code;
    // 成功或失败的消息描述
    private String msg;

    /**
     * 网络调用状态（true=成功，false=失败）
     */
    public boolean success() {
        return SUCCEED.equals(code);
    }

    /**
     * 网络调用状态
     * <p>
     * 000000：表示成功<br/>
     * 1xxxxx: 表示失败
     * </p>
     */
    public String getCode() {
        return code;
    }

    /**
     * 网络调用状态
     * <p>
     * 000000：表示成功<br/>
     * 1xxxxx: 表示失败
     * </p>
     */
    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
