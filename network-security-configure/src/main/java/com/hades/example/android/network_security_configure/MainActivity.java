package com.hades.example.android.network_security_configure;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hades.example.java.lib.FileUtils;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    View mLoadingView;
    TextView mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLoadingView = findViewById(R.id.loading);
        mContentView = findViewById(R.id.content);
        findViewById(R.id.requestBtn).setOnClickListener(v -> request());
    }

    private void request() {
        mLoadingView.setVisibility(View.VISIBLE);
        mContentView.setText("");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url("https://publicobject.com/helloworld.txt")
                            .build();
                    Response response = OkHttpClientUtils
                            .createOkHttpClient()
                            .newCall(request).execute();
                    ResponseBody responseBody = response.body();
                    FileUtils fileUtils = new FileUtils();
                    String content = fileUtils.convertStreamToStr(responseBody.byteStream());
                    updateContent(content);
                    if (responseBody != null) {
                        responseBody.close();
                    }
                } catch (Exception exception) {
                    Log.e(TAG, "run: ", exception);
                } finally {
                    hideLoading();
                }
            }
        }).start();
    }

    private void hideLoading() {
        runOnUiThread(() -> mLoadingView.setVisibility(View.GONE));
    }

    private void updateContent(String content) {
        runOnUiThread(() -> mContentView.setText(content));
    }
}