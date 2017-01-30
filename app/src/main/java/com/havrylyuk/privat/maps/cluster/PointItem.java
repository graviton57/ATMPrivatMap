package com.havrylyuk.privat.maps.cluster;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.havrylyuk.privat.data.model.AcquiringPoint;

/**
 *
 * Created by Igor Havrylyuk on 27.01.2017.
 */

public class PointItem extends AcquiringPoint implements ClusterItem {

    private final LatLng position;
    private int icon;
    private long id;
    private boolean isRouteMap;

    public PointItem(LatLng mPosition) {
        this.position = mPosition;
    }

    public PointItem(LatLng mPosition, int icon ) {
        this.position = mPosition;
        this.icon = icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public boolean isRouteMap() {
        return isRouteMap;
    }

    public void setRouteMap(boolean routeMap) {
        isRouteMap = routeMap;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIcon() {
        return icon;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

}
