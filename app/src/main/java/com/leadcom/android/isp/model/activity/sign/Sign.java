package com.leadcom.android.isp.model.activity.sign;

import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.activity.Activity;
import com.litesuits.orm.db.annotation.Column;

/**
 * <b>功能描述：</b>签到应用的基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/26 00:20 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/26 00:20 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class Sign extends Model {

    public interface Field {
        String Longitude = "longitude";
        String Latitude = "latitude";
        String Altitude = "altitude";
        String SetupId = "signingId";
        String SignInId = "signInId";
        /**
         * 签到人数
         */
        String SignInNum = "signInNum";
        String End = "end";
        String Archived = "archived";
    }

    //地理经度
    @Column(Field.Longitude)
    private String lon;
    //地理纬度
    @Column(Field.Latitude)
    private String lat;
    //海拔
    @Column(Field.Altitude)
    private String alt;
    //签到地点的名称
    @Column(Activity.Field.Site)
    private String site;
    //创建时间
    @Column(Model.Field.CreateDate)
    private String createDate;

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
        if (isEmpty(site)) {
            site = "";
        }
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
