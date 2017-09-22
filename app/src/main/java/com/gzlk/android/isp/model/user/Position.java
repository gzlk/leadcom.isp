package com.gzlk.android.isp.model.user;

/**
 * <b>功能描述：</b>用户收藏的位置信息<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/09/22 21:16 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/09/22 21:16 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class Position {
    private String lon;                    //地理经度
    private String lat;                    //地理纬度
    private String alt;                    //海拔
    private String site;                   //位置的名称

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
}
