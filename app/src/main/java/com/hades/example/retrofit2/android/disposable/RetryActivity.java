package com.hades.example.retrofit2.android.disposable;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hades.example.retrofit2.android.R;

import java.security.SecureRandom;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;

public class RetryActivity extends AppCompatActivity {
    private static final String TAG = RetryActivity.class.getSimpleName();

    private View mProgressBar;

    private HelloController helloController = new HelloController();
    private ObservableLifeHelper lifeHelper = new ObservableLifeHelper();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disposable);

        mProgressBar = findViewById(R.id.progressBar);

        findViewById(R.id.disposable).setOnClickListener(v -> disposable());
    }

    private boolean isFlag() {
        int num = new SecureRandom().nextInt();
        return num % 2 == 0;
    }

    private synchronized void disposable() {
        Log.d(TAG, "retry: ");
        showProgressBar();
        Observable.just(isFlag())
                .flatMap(new Function<Boolean, ObservableSource<Unit>>() {
                    @Override
                    public ObservableSource<Unit> apply(@NonNull Boolean flag) throws Exception {
                        if (flag) {
                            return helloController.hello();
                        }
                        return Observable.just(Unit.INSTANCE);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Unit>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                        lifeHelper.add(d);
                    }

                    @Override
                    public void onNext(@NonNull Unit unit) {
                        Log.d(TAG, "retry,onNext: ");
                        download();
                        hideProgressBar();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                        hideProgressBar();
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != lifeHelper) {
            lifeHelper.dispose();
            lifeHelper = null;
        }
    }

    private void download() {
        Log.d(TAG, "download: ");
    }

    private void showProgressBar() {
        runOnUiThread(() -> mProgressBar.setVisibility(View.VISIBLE));
    }

    private void hideProgressBar() {
        runOnUiThread(() -> mProgressBar.setVisibility(View.GONE));
    }
}
