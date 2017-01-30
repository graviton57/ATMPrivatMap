package com.havrylyuk.privat.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *  Time Work entity class
 * Created by Igor Havrylyuk on 26.01.2017.
 */
public class TimeWork implements Parcelable {

    private String mon;
    private String tue;
    private String wed;
    private String thu;
    private String fri;
    private String sat;
    private String sun;
    private String hol;

    public TimeWork() {
    }

    protected TimeWork(Parcel in) {
        mon = in.readString();
        tue = in.readString();
        wed = in.readString();
        thu = in.readString();
        fri = in.readString();
        sat = in.readString();
        sun = in.readString();
        hol = in.readString();
    }

    public static final Creator<TimeWork> CREATOR = new Creator<TimeWork>() {
        @Override
        public TimeWork createFromParcel(Parcel in) {
            return new TimeWork(in);
        }

        @Override
        public TimeWork[] newArray(int size) {
            return new TimeWork[size];
        }
    };

    public String getMon() {
        return mon;
    }

    public void setMon(String mon) {
        this.mon = mon;
    }

    public String getTue() {
        return tue;
    }

    public void setTue(String tue) {
        this.tue = tue;
    }

    public String getWed() {
        return wed;
    }

    public void setWed(String wed) {
        this.wed = wed;
    }

    public String getThu() {
        return thu;
    }

    public void setThu(String thu) {
        this.thu = thu;
    }

    public String getFri() {
        return fri;
    }

    public void setFri(String fri) {
        this.fri = fri;
    }

    public String getSat() {
        return sat;
    }

    public void setSat(String sat) {
        this.sat = sat;
    }

    public String getSun() {
        return sun;
    }

    public void setSun(String sun) {
        this.sun = sun;
    }

    public String getHol() {
        return hol;
    }

    public void setHol(String hol) {
        this.hol = hol;
    }

    @Override
    public String toString() {
        return
                 mon + ',' +
                 tue + ',' +
                 wed + ',' +
                 thu + ',' +
                 fri + ',' +
                 sat + ',' +
                 sun + ',' +
                 hol ;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mon);
        dest.writeString(tue);
        dest.writeString(wed);
        dest.writeString(thu);
        dest.writeString(fri);
        dest.writeString(sat);
        dest.writeString(sun);
        dest.writeString(hol);
    }
}

/* json
"tw":{
        "mon":"09:00 - 20:00",
        "tue":"09:00 - 20:00",
        "wed":"09:00 - 20:00",
        "thu":"09:00 - 20:00",
        "fri":"09:00 - 20:00",
        "sat":"09:00 - 20:00",
        "sun":"09:00 - 20:00",
        "hol":"09:00 - 20:00"
        }
*/
