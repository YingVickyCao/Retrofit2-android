package com.hades.example.android.network_security_configure;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class LoggingInterceptor implements Interceptor {
    HttpLoggingInterceptor.Logger logger = HttpLoggingInterceptor.Logger.DEFAULT;

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        logger.log("LoggingInterceptor,intercept,----->");

        Request request = chain.request();

        long t1 = System.nanoTime();
        logger.log(String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));
        Response response = chain.proceed(request);
        logger.log("isSuccessful:"+response.isSuccessful() + ",isRedirect:"+response.isRedirect());

        long t2 = System.nanoTime();
        logger.log(String.format("Received response for %s in %.1fms \nstatus code %s  %n%s", response.request().url(), (t2 - t1) / 1e6d, response.code(), response.headers()));
//        logger.log("Response status code:" + response.code());

        logger.log("LoggingInterceptor,intercept,<-----");
        return response;
    }
}
