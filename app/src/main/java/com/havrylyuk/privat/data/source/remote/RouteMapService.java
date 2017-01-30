package com.havrylyuk.privat.data.source.remote;

import com.havrylyuk.privat.maps.RouteResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 *
 * Created by Igor Havrylyuk on 28.01.2017.
 */

public interface RouteMapService {

    String API_KEY ="AIzaSyAX7YCWe-iH_RiQCaSrKSCDNENjVtz1_V4";

    @GET("api/directions/json?key=" + API_KEY)
    Call<RouteResponse> getRoute(
            @Query("units") String units,
            @Query("origin") String origin,
            @Query("destination") String destination,
            @Query("mode") String mode);

}
