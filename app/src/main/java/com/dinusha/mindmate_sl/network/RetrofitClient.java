package com.dinusha.mindmate_sl.network;

import android.os.Build;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String EMULATOR_URL =
            "http://10.0.2.2:8000/";

    private static final String REAL_DEVICE_URL =
            "http://192.168.220.119:8000/";

    private static Retrofit retrofit = null;


    private static boolean isEmulator() {

        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("emulator");
    }


    private static String getBaseUrl() {

        if (isEmulator()) {

            return EMULATOR_URL;

        } else {

            return REAL_DEVICE_URL;
        }
    }


    public static Retrofit getClient() {

        if (retrofit == null) {

            retrofit = new Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .addConverterFactory(
                            GsonConverterFactory.create()
                    )
                    .build();
        }

        return retrofit;
    }

    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
}