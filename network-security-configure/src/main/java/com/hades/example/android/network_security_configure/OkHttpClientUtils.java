package com.hades.example.android.network_security_configure;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

public class OkHttpClientUtils {
    public static OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addNetworkInterceptor(createHttpLoggingInterceptor());
        OkHttpClient okHttpClient = builder.build();
        return okHttpClient;
    }

    private static Interceptor createHttpLoggingInterceptor() {
        return new LoggingInterceptor();
    }
}
