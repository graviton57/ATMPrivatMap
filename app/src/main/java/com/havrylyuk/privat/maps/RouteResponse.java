package com.havrylyuk.privat.maps;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.havrylyuk.privat.maps.Route;

import java.util.ArrayList;
import java.util.List;

public class RouteResponse {

    @SerializedName("routes")
    @Expose
    private List<Route> routes = new ArrayList<>();

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

}