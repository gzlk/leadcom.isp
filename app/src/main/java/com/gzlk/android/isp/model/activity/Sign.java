package com.gzlk.android.isp.model.activity;

import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.archive.Archive;
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

    public interface Table {
        String SIGNING = "actSign";
        String SIGN_RECORD = "actSignRecord";
    }

    public interface Field {
        String Longitude = "longitude";
        String Latitude = "latitude";
        String Altitude = "altitude";
        String SigningId = "signingId";
        /**
         * 应签到人数
         */
        String ExceptedNum = "exceptedNum";
        /**
         * 实际签到人数
         */
        String RealSignNum = "realSignNum";
        /**
         * 未签到人数
         */
        String NotSignNum = "notSignNum";
    }

    //活动Id
    @Column(Activity.Field.ActivityId)
    private String actId;
    //标题
    @Column(Archive.Field.Title)
    private String title;
    //描述
    @Column(Vote.Field.Description)
    private String desc;
    //地理经度
    @Column(Field.Longitude)
    private String lon;
    //地理纬度
    @Column(Field.Latitude)
    private String lat;
    //海拔
    @Column(Field.Altitude)
    private String alt;
    //创建者的id
    @Column(Model.Field.CreatorId)
    private String creatorId;
    //创建者名称
    @Column(Model.Field.CreatorName)
    private String creatorName;
    //创建时间
    @Column(Model.Field.CreateDate)
    protected String createDate;

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

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

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
