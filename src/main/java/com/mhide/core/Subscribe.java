package com.mhide.core;

/**
 * Функциональный интерфейс, описывающий логику эмиссии элементов при подписке.
 *
 * @param <T> тип элементов, передаваемых наблюдателю
 */
@FunctionalInterface
public interface Subscribe<T> {
    /**
     * Метод, вызываемый при подписке, передающий элементы наблюдателю.
     *
     * @param observer наблюдатель, которому передаются элементы
     */
    void subscribe(Observer<? super T> observer);
}

