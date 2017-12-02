package com.leadcom.android.isp.model.common;

import com.google.gson.reflect.TypeToken;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.model.Model;

/**
 * <b>功能描述：</b>地址<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/16 21:34 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/16 21:34 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class Address extends Model {

    public static String toJson(Address address) {
        return Json.gson().toJson(address, new TypeToken<Address>() {
        }.getType());
    }

    public static Address fromJson(String json) {
        return Json.gson().fromJson(json, new TypeToken<Address>() {
        }.getType());
    }

    private double longitude;
    private double latitude;
    private double altitude;
    private String address;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
