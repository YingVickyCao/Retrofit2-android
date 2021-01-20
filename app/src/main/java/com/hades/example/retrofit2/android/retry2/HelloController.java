package com.hades.example.retrofit2.android.retry2;

import androidx.appcompat.app.AppCompatActivity;

public class HelloController extends AppCompatActivity {
    private static final String TAG = HelloController.class.getSimpleName();
    private HelloAction mAction = new HelloAction();

    public synchronized void hello(IResult result) {
        mAction.hello(new IResult() {
            @Override
            public void response(String s) {
                cache(s);
                result.response(s);
            }
        });
    }

    private void cache(String s) {

    }
}
