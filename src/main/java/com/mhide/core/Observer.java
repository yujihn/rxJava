package com.mhide.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Интерфейс наблюдателя за реактивным потоком данных.
 *
 * @param <T> тип данных, передаваемых в потоке
 */
public interface Observer<T> {
    /**
     * Вызывается при поступлении нового элемента.
     *
     * @param item элемент потока
     */
    void onNext(T item);

    /**
     * Вызывается при возникновении ошибки в потоке.
     *
     * @param t возникшая ошибка
     */
    void onError(Throwable t);

    /**
     * Вызывается при завершении потока.
     */
    void onComplete();
}