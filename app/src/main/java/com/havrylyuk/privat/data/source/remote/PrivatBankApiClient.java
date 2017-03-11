package com.havrylyuk.privat.data.source.remote;

import com.havrylyuk.privat.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 * Created by Igor Havrylyuk on 26.01.2017.
 */

public class PrivatBankApiClient {


    private static Retrofit sRetrofit;

    private PrivatBankApiClient() {
    }

    public static Retrofit retrofit() {
        if (sRetrofit == null) {
            synchronized (Retrofit.class) {
                if (sRetrofit == null) {
                    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                    interceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
                    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
                    sRetrofit = new Retrofit.Builder()
                            .baseUrl(BuildConfig.BASE_PRIVAT_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(client)
                            .build();
                }
            }
        }
        return sRetrofit;
    }
}
