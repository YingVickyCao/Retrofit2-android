package com.hades.example.retrofit2.android.disposable2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hades.example.retrofit2.android.R;

public class Retry2Activity extends AppCompatActivity {
    private static final String TAG = Retry2Activity.class.getSimpleName();

    private View mProgressBar;

    private HelloController helloController = new HelloController();

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
        helloController.hello(new IResult() {
            @Override
            public void response(String s) {
                Log.d(TAG, "retry: hello()");
                download();
                hideProgressBar();
            }
        });
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
