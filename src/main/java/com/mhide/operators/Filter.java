package com.mhide.operators;

import com.mhide.core.Observer;
import com.mhide.core.Observable;
import com.mhide.core.Disposable;

import java.util.function.Predicate;

/**
 * Пропуск тех элементов, которые удовлетворяют предикату.
 */
public class Filter {
    public static <T> Observable<T> apply(
            Observable<T> source,
            Predicate<? super T> predicate
    ) {
        return Observable.create(observer -> {
            Disposable disp = source.subscribe(new Observer<T>() {
                @Override
                public void onNext(T item) {
                    if (predicate.test(item)) {
                        observer.onNext(item);
                    }
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
