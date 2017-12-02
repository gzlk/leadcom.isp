package com.leadcom.android.isp.model.common;

/**
 * <b>功能描述：</b>系统版本更新信息<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/25 09:44 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/25 09:44 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SystemUpdate {

    private String version;
    private String patchCode;
    private String resourceURI;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPatchCode() {
        return patchCode;
    }

    public void setPatchCode(String patchCode) {
        this.patchCode = patchCode;
    }

    public String getResourceURI() {
        return resourceURI;
    }

    public void setResourceURI(String resourceURI) {
        this.resourceURI = resourceURI;
    }
}
