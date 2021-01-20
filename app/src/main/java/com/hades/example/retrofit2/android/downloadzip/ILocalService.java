package com.hades.example.retrofit2.android.downloadzip;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;

public interface ILocalService {

    // http://localhost:7777/hello
    @GET("hello")
    Observable<Response<String>> hello();
}
