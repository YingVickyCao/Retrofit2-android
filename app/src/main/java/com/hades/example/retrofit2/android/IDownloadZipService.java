package com.hades.example.retrofit2.android;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

public interface IDownloadZipService {

    @GET
    @Headers({"Content-Type:application/zip"})
    Call<ResponseBody> downloadFile(@Url String fileUrl);

    @GET
    @Headers({"Content-Type:application/zip"})
    @Streaming
    Call<ResponseBody> downloadFile_Streaming(@Url String fileUrl);

    // Retrofit 2 GET request for rxjava
    @Streaming
    @GET
    Observable<Response<ResponseBody>> downloadFileByUrlRx(@Url String fileUrl);
}
