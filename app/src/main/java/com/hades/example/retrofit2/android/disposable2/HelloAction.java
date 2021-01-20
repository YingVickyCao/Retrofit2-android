package com.hades.example.retrofit2.android.disposable2;

import android.util.Log;

import com.hades.example.retrofit2.android.downloadzip.ILocalService;
import com.hades.example.retrofit2.android.downloadzip.ServiceUtils;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class HelloAction {
    private static final String TAG = HelloAction.class.getSimpleName();

    private final String BASE_URL = "http://192.168.8.100:7777/";
    private final ILocalService mService;

    private Disposable mDisposable;

    public HelloAction() {
        mService = ServiceUtils.createService(ILocalService.class, BASE_URL, null);
    }

    public synchronized void hello(IResult result) {
        mService.hello()
                .repeat(2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<String>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull Response<String> response) {
                        Log.d(TAG, "onNext: " + response.body());
                        try {
                            Thread.currentThread().sleep(3000);
                            String s = response.body();
                            if (null != s && !s.isEmpty()) {
                                Log.d(TAG, "onNext:result.onNext " + response.body());
                                result.response(s);
                                if (null != mDisposable && !mDisposable.isDisposed()) {
                                    mDisposable.dispose();
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            if (null != mDisposable && !mDisposable.isDisposed()) {
                                mDisposable.dispose();
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "onError: " + e.toString());
                        if (null != mDisposable && !mDisposable.isDisposed()) {
                            mDisposable.dispose();
                        }
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                        if (null != mDisposable && !mDisposable.isDisposed()) {
                            mDisposable.dispose();
                        }
                    }
                });
    }
}
