package com.havrylyuk.privat.data.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Acquiring Response  class
 * Created by Igor Havrylyuk on 26.01.2017.
 */

public class AcquiringResponse {

    @SerializedName("devices")
    private  List<AcquiringPoint> acquiringPoints;

    public AcquiringResponse(List<AcquiringPoint> acquiringPoints) {
        this.acquiringPoints = acquiringPoints;
    }

    public List<AcquiringPoint> getAcquiringPoints() {
        return acquiringPoints;
    }

}
