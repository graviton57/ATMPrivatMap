package com.havrylyuk.privat.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 *
 * Created by Igor Havrylyuk on 25.01.2017.
 */

public class AcquiringPoint implements Parcelable {

    private  String type;
    private  String cityRU;
    private  String cityUA;
    private  String cityEN;
    private  String fullAddressRu;
    private  String fullAddressUa;
    private  String fullAddressEn;
    private  String placeRu;
    private  String placeUa;
    private  String latitude;
    private  String longitude;
    @SerializedName("tw")
    private TimeWork timeWork;

    public AcquiringPoint() {
    }

    protected AcquiringPoint(Parcel in) {
        type = in.readString();
        cityRU = in.readString();
        cityUA = in.readString();
        cityEN = in.readString();
        fullAddressRu = in.readString();
        fullAddressUa = in.readString();
        fullAddressEn = in.readString();
        placeRu = in.readString();
        placeUa = in.readString();
        latitude = in.readString();
        longitude = in.readString();
    }

    public static final Creator<AcquiringPoint> CREATOR = new Creator<AcquiringPoint>() {
        @Override
        public AcquiringPoint createFromParcel(Parcel in) {
            return new AcquiringPoint(in);
        }

        @Override
        public AcquiringPoint[] newArray(int size) {
            return new AcquiringPoint[size];
        }
    };

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCityRU() {
        return cityRU;
    }

    public void setCityRU(String cityRU) {
        this.cityRU = cityRU;
    }

    public String getCityUA() {
        return cityUA;
    }

    public void setCityUA(String cityUA) {
        this.cityUA = cityUA;
    }

    public String getCityEN() {
        return cityEN;
    }

    public void setCityEN(String cityEN) {
        this.cityEN = cityEN;
    }

    public String getFullAddressRu() {
        return fullAddressRu;
    }

    public void setFullAddressRu(String fullAddressRu) {
        this.fullAddressRu = fullAddressRu;
    }

    public String getFullAddressUa() {
        return fullAddressUa;
    }

    public void setFullAddressUa(String fullAddressUa) {
        this.fullAddressUa = fullAddressUa;
    }

    public String getFullAddressEn() {
        return fullAddressEn;
    }

    public void setFullAddressEn(String fullAddressEn) {
        this.fullAddressEn = fullAddressEn;
    }

    public String getPlaceRu() {
        return placeRu;
    }

    public void setPlaceRu(String placeRu) {
        this.placeRu = placeRu;
    }

    public String getPlaceUa() {
        return placeUa;
    }

    public void setPlaceUa(String placeUa) {
        this.placeUa = placeUa;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public TimeWork getTimeWork() {
        return timeWork;
    }

    public void setTimeWork(TimeWork timeWork) {
        this.timeWork = timeWork;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(cityRU);
        dest.writeString(cityUA);
        dest.writeString(cityEN);
        dest.writeString(fullAddressRu);
        dest.writeString(fullAddressUa);
        dest.writeString(fullAddressEn);
        dest.writeString(placeRu);
        dest.writeString(placeUa);
        dest.writeString(latitude);
        dest.writeString(longitude);
    }

    @Override
    public String toString() {
        return "AcquiringPoint{" +
                "type='" + type + '\'' +
                ", cityRU='" + cityRU + '\'' +
                ", cityUA='" + cityUA + '\'' +
                ", cityEN='" + cityEN + '\'' +
                ", fullAddressRu='" + fullAddressRu + '\'' +
                ", fullAddressUa='" + fullAddressUa + '\'' +
                ", fullAddressEn='" + fullAddressEn + '\'' +
                ", placeRu='" + placeRu + '\'' +
                ", placeUa='" + placeUa + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", timeWork=" + timeWork +
                '}';
    }
}
