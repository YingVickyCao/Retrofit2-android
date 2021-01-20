package com.hades.example.retrofit2.android;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class CustomInterceptor implements Interceptor {
    HttpLoggingInterceptor.Logger logger = HttpLoggingInterceptor.Logger.DEFAULT;

    @Override
    public Response intercept(Chain chain) throws IOException {
        logger.log("LoggingInterceptor,intercept,----->");

        Request request = buildRequest(chain.request());

        long t1 = System.nanoTime();
        logger.log(String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));
        Response response = chain.proceed(request);
        logger.log("isSuccessful:" + response.isSuccessful() + ",isRedirect:" + response.isRedirect());

        long t2 = System.nanoTime();
        logger.log(String.format("Received response for %s in %.1fms \nstatus code %s  %n%s", response.request().url(), (t2 - t1) / 1e6d, response.code(), response.headers()));
//        logger.log("Response status code:" + response.code());
        logger.log("LoggingInterceptor,intercept,<-----");
        return response;
    }


    private Request buildRequest(Request request) {
        Request.Builder builder = request.newBuilder();
        builder.addHeader("header_key", "header_value");
        String url = request.url().toString();
        if (url.startsWith("www.localhost/abc/")) {
            builder.addHeader("Cookie", "cookie_value_1");
            builder.addHeader("Cookie", "cookie_value_2");
        }

        String path = request.url().uri().getPath();
        if (null != path && path.contains("/d/e/f")) {
            builder.addHeader("my-token", "value");
        }
        return builder.build();
    }
}
