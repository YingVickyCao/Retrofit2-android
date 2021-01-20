package com.hades.example.retrofit2.android;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.hades.example.java.lib.FileUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.net.ssl.HttpsURLConnection;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private View mProgressBar;

    // 1.4 MB
    private final String EXAMPLE_URL_1 = "https://github.com/";
    private final String EXAMPLE_FILE_URL_1 = "GameJs/gamejs/archive/master.zip";

    private final String EXAMPLE_URL_2 = "https://github.com/";
    private final String EXAMPLE_FILE_URL_2 = "AtomicGameEngine/AtomicGameEngine/archive/master.zip";

    // 72.3kb
    private final String EXAMPLE_URL_3 = "https://yingvickycao.github.io/";
    private final String EXAMPLE_FILE_URL_3 = "ServerMocker/full.zip";
//    private final String EXAMPLE_URL_3 = "https://gitee.com/YingVickyCao/";
//    private final String EXAMPLE_FILE_URL_3 = "ServerMocker/blob/master/full.zip";

    // 5.4MB
//    private final String EXAMPLE_URL_4 = "https://yingvickycao.github.io/";
//    private final String EXAMPLE_FILE_URL_4 = "ServerMocker/full2.zip";
    private final String EXAMPLE_URL_4 = "https://gitee.com/YingVickyCao/";
    private final String EXAMPLE_FILE_URL_4 = "ServerMocker/blob/master/full2.zip";

    private final String EXAMPLE_URL_5 = "https://gitee.com/YingVickyCao/";
    private final String EXAMPLE_FILE_URL_5 = "YingVickyCao.github.io/raw/master/README.md";

    private final String BASE_URL = EXAMPLE_URL_5;
    private final String FILE_URL = EXAMPLE_FILE_URL_5;

    private final String FILE_NAME = "full.zip";

    private long mTs1;
    private long mTs2;

    private IDownloadProgress mDownloadProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = findViewById(R.id.progressBar);
        findViewById(R.id.download_zip).setOnClickListener(view -> downloadZipFile_NotUseStreaming());
        findViewById(R.id.download_zip_use_streaming).setOnClickListener(view -> downloadZipFile_UseStreaming());
        findViewById(R.id.download_zip_rxjava).setOnClickListener(view -> downloadZipFileRx());
        findViewById(R.id.checkUrl).setOnClickListener(view -> checkUrl());
        findViewById(R.id.checkZipSize).setOnClickListener(view -> checkZipSize());
        findViewById(R.id.printReadme).setOnClickListener(view -> printReadme());
        findViewById(R.id.test).setOnClickListener(view -> test());

        mDownloadProgress = new IDownloadProgress() {
            @Override
            public void update(long bytesRead, long length, boolean done) {
                Log.d(TAG, "Progress update: " + bytesRead + "/" + length + " >>>> " + (float) bytesRead / length + "===>" + ((int) ((float) bytesRead / length * 100)) + "%");
            }
        };
    }

    private void downloadZipFileRx() {
        showProgressBar();
        IDownloadZipService downloadService = createService(IDownloadZipService.class, BASE_URL, mDownloadProgress);
        downloadService.downloadFileByUrlRx(FILE_URL)
                .flatMap(processDownload())
                .flatMap(unpackZip())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<File>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {

                    }

                    @Override
                    public void onNext(@NonNull File file) {
                        Log.d(TAG, "File downloaded and extracted to " + file.getAbsolutePath());
                        hideProgressBar();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        hideProgressBar();
                        e.printStackTrace();
                        Log.d(TAG, "Error " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onCompleted");
                    }
                });
    }

    private Function<Response<ResponseBody>, Observable<File>> processDownload() {
        return new Function<Response<ResponseBody>, Observable<File>>() {
            @Override
            public Observable<File> apply(@NonNull Response<ResponseBody> responseBodyResponse) throws Exception {
                return saveToDiskRx(responseBodyResponse);
            }
        };
    }

    private Function<File, Observable<File>> unpackZip() {
        return new Function<File, Observable<File>>() {
            @Override
            public Observable<File> apply(File file) {
                InputStream is;
                ZipInputStream zis;
                String parentFolder;
                String filename;
                try {
                    parentFolder = file.getParentFile().getPath();

                    is = new FileInputStream(file.getAbsolutePath());
                    zis = new ZipInputStream(new BufferedInputStream(is));
                    ZipEntry ze;
                    byte[] buffer = new byte[1024];
                    int count;

                    while ((ze = zis.getNextEntry()) != null) {
                        filename = ze.getName();

                        if (ze.isDirectory()) {
                            File fmd = new File(parentFolder + "/" + filename);
                            fmd.mkdirs();
                            continue;
                        }

                        FileOutputStream fout = new FileOutputStream(parentFolder + "/" + filename);

                        while ((count = zis.read(buffer)) != -1) {
                            fout.write(buffer, 0, count);
                        }

                        fout.close();
                        zis.closeEntry();
                    }

                    zis.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                File extractedFile = new File(file.getAbsolutePath().replace(".zip", ""));
                if (!file.delete()) Log.d("unpackZip", "Failed to deleted the zip file.");
                return Observable.just(extractedFile);
            }
        };
    }

    private void downloadZipFile_UseStreaming() {
        showProgressBar();
        IDownloadZipService downloadService = createService(IDownloadZipService.class, BASE_URL, mDownloadProgress);
        Call<ResponseBody> call = downloadService.downloadFile_Streaming(FILE_URL);
        downloadZipFile(call);
    }

    private void downloadZipFile_NotUseStreaming() {
        showProgressBar();
        IDownloadZipService downloadService = createService(IDownloadZipService.class, BASE_URL, mDownloadProgress);
        Call<ResponseBody> call = downloadService.downloadFile(BASE_URL + FILE_URL);
        downloadZipFile(call);
    }

    private void downloadZipFile(Call<ResponseBody> call) {
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Got the body for the file");
                    new AsyncTask<Void, Long, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            saveToDisk(response.body(), FILE_NAME);
                            return null;
                        }
                    }.execute();

                } else {
                    Log.d(TAG, "Connection failed " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Log.e(TAG, t.getMessage());
                hideProgressBar();
            }
        });
    }

    private void showProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private File setDestinationFilePath(String filename) {
        new File("/data/data/" + getPackageName() + "/games").mkdir();
        File destinationFile = new File("/data/data/" + getPackageName() + "/games/" + filename);
        return destinationFile;
    }

    private void saveToDisk(ResponseBody body, String filename) {
        mTs1 = System.currentTimeMillis();
        try {
            File destinationFile = setDestinationFilePath(filename);
            InputStream is = null;
            OutputStream os = null;

            try {
                is = body.byteStream();
                long filesize = body.contentLength();
//                long filesize = is.available();
                Log.d(TAG, "File Size=" + filesize);
                os = new FileOutputStream(destinationFile);

                byte data[] = new byte[4096];
                int count;
                int progress = 0;
                while ((count = is.read(data)) != -1) {
                    os.write(data, 0, count);
                    progress += count;
                    mDownloadProgress.update(progress, filesize, count == -1);
                }
                os.flush();
                Log.d(TAG, "File saved successfully!");
                return;
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Failed to save the file!");
                return;
            } finally {
                if (is != null) is.close();
                if (os != null) os.close();
                hideProgressBar();
                mTs2 = System.currentTimeMillis();
                Log.e(TAG, "saveToDisk: ms=" + (mTs2 - mTs1) + "");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Failed to save the file!");
            return;
        }
    }

    private Observable<File> saveToDiskRx(final Response<ResponseBody> response) {
        return Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<File> emitter) throws Exception {
                try {
                    File destinationFile = setDestinationFilePath(FILE_NAME);
                    BufferedSink bufferedSink = Okio.buffer(Okio.sink(destinationFile));
                    bufferedSink.writeAll(response.body().source());
                    bufferedSink.close();
                    emitter.onNext(destinationFile);
                    emitter.onComplete();
                } catch (IOException e) {
                    e.printStackTrace();
                    emitter.onError(e);
                }
            }
        });
    }

    public <T> T createService(Class<T> serviceClass, String baseUrl, final IDownloadProgress callback) {
        return ServiceUtils.createService(serviceClass, baseUrl, callback);
    }

    private void checkUrl() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Tell the URLConnection to use our HostnameVerifier
                try {
                    URL url = new URL("https://blog.csdn.net/");
                    HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.setHostnameVerifier(new MyHostnameVerifier());
                    InputStream in = urlConnection.getInputStream();
                    in.read();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    // Only check zip size, not download zip
    // Gitee cannot get zip size
    // Github can get zip size
    private void checkZipSize() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                okhttp3.Response response = null;
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(BASE_URL + FILE_URL).head().build();
                    response = client.newCall(request).execute();
                    long size = response.body().contentLength();
                    Log.e(TAG, "checkZipSize:size=" + size);

                } catch (Exception ex) {

                } finally {
                    if (null != response) {
                        response.close();
                    }
                }
            }
        }).start();
    }

    private void printReadme() {
        showProgressBar();
        IDownloadZipService downloadService = createService(IDownloadZipService.class, BASE_URL, mDownloadProgress);
        downloadService.printReadme(EXAMPLE_URL_5 + EXAMPLE_FILE_URL_5)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {

                    }

                    @Override
                    public void onNext(@NonNull Response<ResponseBody> responseBodyResponse) {
                        Log.d(TAG, "onNext: " + responseBodyResponse.toString());
                        String result = new FileUtils().convertStreamToStr(responseBodyResponse.body().byteStream());
                        Log.d(TAG, "onNext: ");
                        Log.d(TAG, "printReadme:onNext: " + result);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "onError: ", e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void test() {
    }
}