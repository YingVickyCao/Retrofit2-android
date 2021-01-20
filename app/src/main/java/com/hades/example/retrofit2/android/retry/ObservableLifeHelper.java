package com.hades.example.retrofit2.android.retry;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class ObservableLifeHelper {
    private final CompositeDisposable disposables = new CompositeDisposable();

    public void add(Disposable disposable) {
        disposables.add(disposable);
    }

    public void dispose() {
        if (!disposables.isDisposed()) {
            disposables.dispose();
        }
    }
}