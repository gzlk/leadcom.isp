package com.gzlk.android.isp.model.common;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.gzlk.android.isp.model.BaseModel;

import java.util.List;
import java.util.Locale;

/**
 * <b>功能描述：</b>百度地图定位内容<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/23 18:21 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/23 18:21 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class BaiduLocation extends BaseModel implements Parcelable {

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
    private List<Poi> poiList;

    protected BaiduLocation(Parcel in) {
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
        poiList = in.createTypedArrayList(Poi.CREATOR);
    }

    public static final Creator<BaiduLocation> CREATOR = new Creator<BaiduLocation>() {
        @Override
        public BaiduLocation createFromParcel(Parcel in) {
            return new BaiduLocation(in);
        }

        @Override
        public BaiduLocation[] newArray(int size) {
            return new BaiduLocation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
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
        dest.writeList(poiList);
    }

    public BaiduLocation() {
    }

    public BaiduLocation(BDLocation location) {
        setParameters(location);
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%s %s %s %s %s %s %s\n%s",
                country, province, city, district, street,
                streetNumber, describe, address);
    }

    private void setParameters(BDLocation location) {
        latitude = 0.0;
        longitude = 0.0;
        altitude = 0.0;
        if (null == location) return;
        city = location.getCity();
        cityCode = location.getCityCode();
        country = location.getCountry();
        countryCode = location.getCountryCode();
        district = location.getDistrict();
        if (!Double.isNaN(location.getLatitude())) {
            latitude = location.getLatitude();
        }
        if (!Double.isNaN(location.getLongitude())) {
            longitude = location.getLongitude();
        }
        if (!Double.isNaN(location.getAltitude())) {
            altitude = location.getAltitude();
        }
        province = location.getProvince();
        street = location.getStreet();
        streetNumber = location.getStreetNumber();
        address = location.getAddrStr();
        setAddress(location.getAddrStr());
        describe = location.getLocationDescribe().replace("在", "").replace("附近", "");
        setPoiList(location.getPoiList());
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

    public List<Poi> getPoiList() {
        return this.poiList;
    }

    public void setPoiList(List<Poi> var1) {
        this.poiList = var1;
    }

}
