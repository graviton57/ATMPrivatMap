package com.havrylyuk.privat.maps;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.havrylyuk.privat.maps.Distance;
import com.havrylyuk.privat.maps.Duration;


public class Leg {

    @SerializedName("distance")
    @Expose
    private Distance distance;
    @SerializedName("duration")
    @Expose
    private Duration duration;

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

}
