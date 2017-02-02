package com.havrylyuk.privat.data.source.remote;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 * Created by Igor Havrylyuk on 28.01.2017.
 */

public class MapApiClient {


    private static final String BASE_MAP_URL = "https://maps.googleapis.com/maps/";

    private static Retrofit sRetrofit;

    private MapApiClient() {
    }

    public static Retrofit retrofit() {
        if (sRetrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
            sRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_MAP_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return sRetrofit;
    }
}
