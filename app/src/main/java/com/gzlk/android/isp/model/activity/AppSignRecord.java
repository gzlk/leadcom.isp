package com.gzlk.android.isp.model.activity;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

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

@Table(Sign.Table.SIGN_RECORD)
public class AppSignRecord extends Sign {

    //签到应用的id(活动-签到应用-签到  三者间是一对多，一对多的关系)
    @Column(Field.SigningId)
    private String setupId;

    //手机设备号
    @Column(Vote.Field.IMSI)
    private String imsi;

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
}
