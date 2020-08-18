package com.hades.example.retrofit2.android;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.*;

import javax.annotation.Nullable;
import java.io.IOException;

public class FileResponseBody extends ResponseBody {
    private ResponseBody mResponseBody;
    private IDownloadProgress nDownloadProgress;
    private BufferedSource mBufferedSource;

    public FileResponseBody(ResponseBody mResponseBody, IDownloadProgress downloadProgress) {
        this.mResponseBody = mResponseBody;
        this.nDownloadProgress = downloadProgress;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (mBufferedSource == null) {
            mBufferedSource = Okio.buffer(source(mResponseBody.source()));
        }
        return mBufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            private long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                nDownloadProgress.update(totalBytesRead, mResponseBody.contentLength(), bytesRead == -1);
                return bytesRead;
            }
        };
    }
}
