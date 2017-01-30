package com.havrylyuk.privat.data.source.remote;

import com.havrylyuk.privat.data.model.AcquiringResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 *
 * Created by Igor Havrylyuk on 25.01.2017.
 */

public interface AcquiringService {

    @GET("/p24api/infrastructure?json&atm")
    Call<AcquiringResponse> getAtms(
            @Query("address") String address,
            @Query("city") String city);

    @GET("p24api/infrastructure?json&tso")
    Call<AcquiringResponse> getTerminals(
            @Query("address") String address,
            @Query("city") String city);


}
