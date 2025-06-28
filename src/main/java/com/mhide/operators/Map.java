package com.mhide.operators;

import com.mhide.core.Observer;
import com.mhide.core.Observable;
import com.mhide.core.Disposable;

import java.util.function.Function;

/**
 * Применение функции к каждому элементу потока.
 */
public class Map {
    public static <T, R> Observable<R> apply(
            Observable<T> source,
            Function<? super T, ? extends R> mapper
    ) {
        return Observable.create(observer -> {
            Disposable disp = source.subscribe(new Observer<T>() {
                @Override
                public void onNext(T item) {
                    observer.onNext(mapper.apply(item));
                }

                @Override
                public void onError(Throwable t) {
                    observer.onError(t);
                }

                @Override
                public void onComplete() {
                    observer.onComplete();
                }
            });
        });
    }
}
