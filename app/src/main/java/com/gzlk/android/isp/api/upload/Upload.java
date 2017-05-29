package com.gzlk.android.isp.api.upload;

import com.gzlk.android.isp.api.Api;

/**
 * <b>功能描述：</b>文件上传返回的结果<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/23 16:42 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/23 16:42 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class Upload extends Api {

    private String result;
    private String result2;

    /**
     * 文件上传之后的url
     */
    public String getResult() {
        return result;
    }

    /**
     * 文件上传之后的url
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * Office 文档的pdf预览文件
     */
    public String getResult2() {
        return result2;
    }

    /**
     * Office 文档的pdf预览文件
     */
    public void setResult2(String result2) {
        this.result2 = result2;
    }
}
