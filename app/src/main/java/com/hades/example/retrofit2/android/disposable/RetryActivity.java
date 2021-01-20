package com.hades.example.retrofit2.android.disposable;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hades.example.retrofit2.android.R;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class RetryActivity extends AppCompatActivity {
    private static final String TAG = RetryActivity.class.getSimpleName();

    private View mProgressBar;

    private HelloController helloController = new HelloController();
    private ObservableLifeHelper lifeHelper = new ObservableLifeHelper();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retry);

        mProgressBar = findViewById(R.id.progressBar);

        findViewById(R.id.retry).setOnClickListener(v -> retry());
        findViewById(R.id.retryWhen).setOnClickListener(v -> retryWhen());
    }

    private synchronized void retry() {
        Log.d(TAG, "retry: ");
        showProgressBar();
        helloController.hello()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                        lifeHelper.add(d);
                    }

                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        Log.d(TAG, "retry,onNext: " + aBoolean);
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

    private void retryWhen() {

    }

    private void retryUntil() {

    }

    private void showProgressBar() {
        runOnUiThread(() -> mProgressBar.setVisibility(View.VISIBLE));
    }

    private void hideProgressBar() {
        runOnUiThread(() -> mProgressBar.setVisibility(View.GONE));
    }
}
