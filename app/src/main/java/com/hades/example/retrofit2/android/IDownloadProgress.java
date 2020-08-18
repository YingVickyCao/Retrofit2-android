package com.hades.example.retrofit2.android;

public interface IDownloadProgress {
    void update(long bytesRead, long length, boolean done);
}
