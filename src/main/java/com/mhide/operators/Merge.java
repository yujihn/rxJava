package com.mhide.operators;

import com.mhide.core.Observer;
import com.mhide.core.Observable;
import com.mhide.core.Disposable;
import com.mhide.core.CompositeDisposable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Объединение нескольких Observable-источников в один поток, эмитирующий все их элементы.
 */
public class Merge {

    /**
     * @param sources массив Observable-источников
     * @param <T> тип элементов
     * @return новый Observable<T>, эмитирующий все элементы sources
     */
    @SafeVarargs
    public static <T> Observable<T> apply(Observable<? extends T>... sources) {
        return Observable.create(observer -> {
            CompositeDisposable composite = new CompositeDisposable();
            AtomicInteger remaining = new AtomicInteger(sources.length);
            ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

            for (Observable<? extends T> src : sources) {
                Disposable disp = src.subscribe(new Observer<T>() {
                    @Override
                    public void onNext(T item) {
                        observer.onNext(item);
                    }

                    @Override
                    public void onError(Throwable t) {
                        errors.add(t);
                        completeIfDone();
                    }

                    @Override
                    public void onComplete() {
                        completeIfDone();
                    }

                    private void completeIfDone() {
                        if (remaining.decrementAndGet() == 0) {
                            Throwable err = errors.poll();
                            if (err != null) {
                                observer.onError(err);
                            } else {
                                observer.onComplete();
                            }
                            composite.dispose();
                        }
                    }
                });
                composite.add(disp);
            }
        });
    }
}
