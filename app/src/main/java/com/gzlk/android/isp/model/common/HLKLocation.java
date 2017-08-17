package com.gzlk.android.isp.model.common;

import android.os.Parcel;
import android.os.Parcelable;

import com.amap.api.location.AMapLocation;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.BaseModel;

import java.util.Locale;

/**
 * <b>功能描述：</b>地图定位内容<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/23 18:21 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/23 18:21 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class HLKLocation extends BaseModel implements Parcelable {

    private String time;
    private String address;
    private String country;
    private String countryCode;
    private String province;
    private String city;
    private String cityCode;
    private String district;
    private String street;
    private String streetNumber;
    private String describe;
    private double latitude;
    private double longitude;
    private double altitude;
    private float radius;
    private float direction;

    protected HLKLocation(Parcel in) {
        time = in.readString();
        address = in.readString();
        country = in.readString();
        countryCode = in.readString();
        province = in.readString();
        city = in.readString();
        cityCode = in.readString();
        district = in.readString();
        street = in.readString();
        streetNumber = in.readString();
        describe = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        altitude = in.readDouble();
        radius = in.readFloat();
        direction = in.readFloat();
    }

    public static final Creator<HLKLocation> CREATOR = new Creator<HLKLocation>() {
        @Override
        public HLKLocation createFromParcel(Parcel in) {
            return new HLKLocation(in);
        }

        @Override
        public HLKLocation[] newArray(int size) {
            return new HLKLocation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(time);
        dest.writeString(address);
        dest.writeString(country);
        dest.writeString(countryCode);
        dest.writeString(province);
        dest.writeString(city);
        dest.writeString(cityCode);
        dest.writeString(district);
        dest.writeString(street);
        dest.writeString(streetNumber);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(altitude);
        dest.writeFloat(radius);
        dest.writeDouble(direction);
    }

    public HLKLocation() {
    }

    public HLKLocation(AMapLocation location) {
        latitude = 0.0;
        longitude = 0.0;
        altitude = 0.0;
        if (null == location) return;
        time = Utils.format(StringHelper.getString(R.string.ui_base_text_date_time_format), location.getTime());
        if (!Double.isNaN(location.getLatitude())) {
            latitude = location.getLatitude();
        }
        if (!Double.isNaN(location.getLongitude())) {
            longitude = location.getLongitude();
        }
        if (!Double.isNaN(location.getAltitude())) {
            altitude = location.getAltitude();
        }
        country = location.getCountry();
        province = location.getProvince();
        city = location.getCity();
        cityCode = location.getCityCode();
        district = location.getDistrict();
        street = location.getStreet();
        streetNumber = location.getStreetNum();
        address = location.getAddress();
        describe = location.getDescription();
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%s %s %s %s %s %s %s\n%s",
                country, province, city, district, street,
                streetNumber, describe, address);
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getDescribe() {
        return this.describe;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getDirection() {
        return direction;
    }

    public void setDirection(float direction) {
        this.direction = direction;
    }

}
