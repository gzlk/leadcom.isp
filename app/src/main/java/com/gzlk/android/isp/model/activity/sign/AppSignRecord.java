package com.gzlk.android.isp.model.activity.sign;

import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.activity.Activity;
import com.gzlk.android.isp.model.activity.vote.AppVote;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.List;

/**
 * <b>功能描述：</b>活动应用：签到记录<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/26 00:33 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/26 00:33 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

@Table(Activity.Table.SIGN_RECORD)
public class AppSignRecord extends Sign {

    public static String toJson(AppSignRecord record) {
        return Json.gson().toJson(record, new TypeToken<AppSignRecord>() {
        }.getType());
    }

    public static AppSignRecord fromJson(String json) {
        if (isEmpty(json)) return null;
        return Json.gson().fromJson(json, new TypeToken<AppSignRecord>() {
        }.getType());
    }

    // 查找本地缓存里我在某个签到应用里的签到记录
    public static AppSignRecord getMyRecord(String setupId) {
        QueryBuilder<AppSignRecord> builder = new QueryBuilder<>(AppSignRecord.class)
                .whereEquals(Field.SigningId, setupId)
                .whereAppendAnd()
                .whereEquals(Model.Field.CreatorId, Cache.cache().userId);
        List<AppSignRecord> records = new Dao<>(AppSignRecord.class).query(builder);
        return (null == records || records.size() < 1) ? null : records.get(0);
    }

    //签到应用的id(活动-签到应用-签到  三者间是一对多，一对多的关系)
    @Column(Field.SigningId)
    private String setupId;

    //手机设备号
    @Column(AppVote.Field.IMSI)
    private String imsi;

    @Ignore
    private String time;

    @Column(Field.Distance)
    private String distance;

    public String getSetupId() {
        return setupId;
    }

    public void setSetupId(String setupId) {
        this.setupId = setupId;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
