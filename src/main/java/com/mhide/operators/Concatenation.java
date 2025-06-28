package com.mhide.operators;

import com.mhide.core.Observer;
import com.mhide.core.Observable;

/**
 * Последовательная конкатенация двух Observable.
 */
public class Concatenation {
    public static <T> Observable<T> apply(
            Observable<? extends T> first,
            Observable<? extends T> second
    ) {
        return Observable.create(observer -> first.subscribe(new Observer<T>() {
            @Override public void onNext(T item) { observer.onNext(item); }
            @Override public void onError(Throwable t) { observer.onError(t); }
            @Override public void onComplete() {
                second.subscribe(observer);
            }
        }));
    }
}
