package com.gzlk.android.isp.model.user;

/**
 * <b>功能描述：</b>用户动态中的隐私设置<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/15 19:40 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/15 19:40 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class Privacy {

    private String id;
    //用户名
    private String userId;
    //隐私设置类型  1.公开  2.不公开 3.对某些人公开
    private String type;
    //最后修改时间
    private String lastModifiedTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(String lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }
}
