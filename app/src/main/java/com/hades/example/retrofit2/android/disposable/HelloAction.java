package com.hades.example.retrofit2.android.disposable;

import android.util.Log;

import com.hades.example.retrofit2.android.downloadzip.ILocalService;
import com.hades.example.retrofit2.android.downloadzip.ServiceUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class HelloAction {
    private static final String TAG = HelloAction.class.getSimpleName();

    private final String BASE_URL = "http://10.0.2.2:7777/";
    private final ILocalService mService;

    public HelloAction() {
        mService = ServiceUtils.createService(ILocalService.class, BASE_URL, null);
    }

    public synchronized Observable<String> hello() {
        return mService.hello()
                .subscribeOn(Schedulers.io())
                .delay(3, TimeUnit.SECONDS)
                .flatMap(new Function<Response<String>, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(@NonNull Response<String> response) throws Exception {
                        String s = response.body();
                        Log.d(TAG, "hello,apply:thread name:" + Thread.currentThread().getName() + ",thread id:" + Thread.currentThread().getId());
//                        if (null != s && !s.isEmpty()) {
//                            Log.d(TAG, "hello,apply:result.onNext " + response.body());
//                            return Observable.just(s);
//                        }
                        return Observable.error(new Exception("response is invalid"));
                    }
                });
    }
}
