package com.hades.example.retrofit2.android.downloadzip;

public interface IDownloadProgress {
    void update(long bytesRead, long length, boolean done);
}
