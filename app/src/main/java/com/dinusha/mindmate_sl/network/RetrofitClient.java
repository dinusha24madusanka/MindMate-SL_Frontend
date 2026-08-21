package com.dinusha.mindmate_sl.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;

public class RetrofitClient {

    private static final String BASE_URL =
            "https://races-assets-saturn-clearance.trycloudflare.com/";

    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {

        if (retrofit == null) {

            OkHttpClient client =
                    new OkHttpClient.Builder()
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(60, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .callTimeout(90, TimeUnit.SECONDS)
                            .build();

            retrofit =
                    new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(client)
                            .addConverterFactory(
                                    GsonConverterFactory.create()
                            )
                            .build();
        }

        return retrofit;
    }

    public static ApiService getApiService() {
        return getRetrofitInstance()
                .create(ApiService.class);
    }
}