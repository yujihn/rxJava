package com.mhide.operators;

import com.mhide.core.Observer;
import com.mhide.core.Disposable;
import com.mhide.core.Observable;
import com.mhide.core.CompositeDisposable;

import java.util.function.Function;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * Для каждого элемента исходного потока создается новый Observable
 * и его элементы объединяются в один результирующий поток.
 */
public class FlatMap {

    /**
     * Применяет оператор flatMap к исходному Observable.
     *
     * @param source исходный поток элементов
     * @param mapper функция, порождающая вложенный Observable для каждого элемента
     * @param <T> тип исходных элементов
     * @param <R> тип элементов результирующего потока
     * @return новый Observable, эмитирующий объединенные элементы вложенных потоков
     */
    public static <T, R> Observable<R> apply(
            Observable<T> source,
            Function<? super T, Observable<? extends R>> mapper
    ) {
        return Observable.create(observer -> {
            // Коллекция для управления подписками вложенных Observable
            CompositeDisposable composite = new CompositeDisposable();
            // счетчик активных потоков (родительский + вложенные)
            AtomicInteger activeCount = new AtomicInteger(1); // 1 — исходный поток
            // поток ошибок
            ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

            // Подписка на исходный поток
            Disposable parentDisp = source.subscribe(new Observer<T>() {
                @Override
                public void onNext(T item) {
                    activeCount.incrementAndGet(); // увеличивание счетчика активных потоков
                    Disposable innerDisp = mapper.apply(item)
                            .subscribe(new Observer<R>() {
                                @Override
                                public void onNext(R inner) {
                                    observer.onNext(inner);
                                }

                                @Override
                                public void onError(Throwable t) {
                                    errors.add(t);
                                    checkComplete();
                                }

                                @Override
                                public void onComplete() {
                                    checkComplete();
                                }

                                private void checkComplete() {
                                    if (activeCount.decrementAndGet() == 0) {
                                        // все потоки завершены
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
                    composite.add(innerDisp);
                }

                @Override
                public void onError(Throwable t) {
                    errors.add(t);
                    checkComplete();
                }

                @Override
                public void onComplete() {
                    checkComplete();
                }

                private void checkComplete() {
                    if (activeCount.decrementAndGet() == 0) {
                        // все потоки завершены
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

            // добавление подписки на исходный поток
            composite.add(parentDisp);
        });
    }
}
