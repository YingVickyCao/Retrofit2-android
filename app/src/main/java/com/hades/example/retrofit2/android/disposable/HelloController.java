package com.hades.example.retrofit2.android.disposable;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import kotlin.Unit;

public class HelloController extends AppCompatActivity {
    private static final String TAG = HelloController.class.getSimpleName();
    private HelloAction mAction = new HelloAction();

    public synchronized Observable<Unit> hello() {
        return mAction.hello()
                .flatMap((Function<String, ObservableSource<Unit>>) s -> {
                    cache(s);
                    Log.d(TAG, "hello,apply: thread name:" + Thread.currentThread().getName() + ",thread id:" + Thread.currentThread().getId());
                    return Observable.just(Unit.INSTANCE);
                });
    }

    private void cache(String s) {
        Log.d(TAG, "cache: " + s);
    }
}
