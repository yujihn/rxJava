package com.mhide.core;

import com.mhide.schedulers.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Основной класс реактивного потока, реализующий паттерн Observer.
 *
 * @param <T> тип элементов, эмитируемых потоком
 */
public class Observable<T> {
    private static final Logger log = LoggerFactory.getLogger(Observable.class);

    private final Subscribe<T> source;

    private Observable(Subscribe<T> source) {
        this.source = source;
    }

    /**
     * Создание нового "холодный" Observable с заданной логикой эмиссии.
     *
     * @param source логика эмиссии элементов
     * @param <T>    тип элементов
     * @return новый экземпляр Observable
     */
    public static <T> Observable<T> create(Subscribe<T> source) {
        log.debug("Создание Observable через create()");
        return new Observable<>(source);
    }

    /**
     * Создание Observable, который эмитит один элемент и сразу завершает поток.
     *
     * @param item элемент для эмиссии
     * @param <T>  тип элемента
     * @return Observable, эмитирующий один элемент и завершающийся
     */
    public static <T> Observable<T> just(T item) {
        return create(observer -> {
            observer.onNext(item);
            observer.onComplete();
        });
    }

    /**
     * Создает Observable, который эмитит переданные элементы и сразу завершает поток.
     *
     * @param items элементы для эмиссии
     * @param <T>   тип элементов
     * @return новый Observable с переданными элементами
     */
    @SafeVarargs
    public static <T> Observable<T> just(T... items) {
        return create(observer -> {
            Arrays.stream(items).forEach(observer::onNext);
            observer.onComplete();
        });
    }

    /**
     * Подписка с обработчиками для onNext, onError и onComplete.
     *
     * @param onNext     действие при получении нового элемента
     * @param onError    действие при возникновении ошибки
     * @param onComplete действие при завершении потока
     * @return Disposable для отмены подписки
     */
    public Disposable subscribe(
            Consumer<? super T> onNext,
            Consumer<Throwable> onError,
            Runnable onComplete
    ) {
        Observer<T> obs = new Observer<>() {
            @Override
            public void onNext(T item) {
                onNext.accept(item);
            }

            @Override
            public void onError(Throwable t) {
                onError.accept(t);
            }

            @Override
            public void onComplete() {
                onComplete.run();
            }
        };
        return subscribe(obs);
    }

    /**
     * Подписка с обработчиком onNext.
     *
     * @param onNext действие при получении нового элемента
     * @return Disposable для отмены подписки
     */
    public Disposable subscribe(Consumer<? super T> onNext) {
        return subscribe(onNext, Throwable::printStackTrace, () -> {
        });
    }

    /**
     * Базовая подписка, возвращает Disposable.
     *
     * @param observer наблюдатель, реализующий интерфейс
     * @return Disposable для отмены подписки
     */
    public Disposable subscribe(Observer<? super T> observer) {
        log.debug("Новая подписка на Observable");
        Disposable disposable = new Disposable();
        try {
            source.subscribe(new Observer<>() {
                @Override
                public void onNext(T item) {
                    if (!disposable.isDisposed()) {
                        observer.onNext(item);
                    }
                }

                @Override
                public void onError(Throwable t) {
                    if (!disposable.isDisposed()) {
                        observer.onError(t);
                    }
                }

                @Override
                public void onComplete() {
                    if (!disposable.isDisposed()) {
                        observer.onComplete();
                    }
                }
            });
        } catch (Throwable t) {
            observer.onError(t);
        }
        return disposable;
    }

    /**
     * Выполнение подписки в указанном планировщике.
     *
     * @param scheduler планировщик для запуска source.subscribe
     * @return новый Observable, подписка которого отложена на scheduler
     */
    public Observable<T> subscribeOn(Scheduler scheduler) {
        return Observable.create(observer ->
                scheduler.schedule(() -> this.subscribe(observer))
        );
    }

    /**
     * Передача события onNext/onError/onComplete в указанном планировщике.
     *
     * @param scheduler планировщик для обработки событий
     * @return новый Observable, события которого переключаются на scheduler
     */
    public Observable<T> observeOn(Scheduler scheduler) {
        return Observable.create(observer ->
                this.subscribe(new Observer<>() {
                    @Override
                    public void onNext(T item) {
                        scheduler.schedule(() -> observer.onNext(item));
                    }

                    @Override
                    public void onError(Throwable t) {
                        scheduler.schedule(() -> observer.onError(t));
                    }

                    @Override
                    public void onComplete() {
                        scheduler.schedule(observer::onComplete);
                    }
                })
        );
    }
}