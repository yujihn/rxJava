package com.mhide.core;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Групповое управление несколькими подписками, позволяет отменить все сразу.
 */
public class CompositeDisposable {
    private final Set<Disposable> disposables = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * Добавление новой подписки в группу.
     *
     * @param d подписка для добавления
     */
    public void add(Disposable d) {
        disposables.add(d);
    }

    /**
     * Удаление подписки из группы.
     *
     * @param d подписка для удаления
     */
    public void remove(Disposable d) {
        disposables.remove(d);
    }

    /**
     * Отмена всех подписок в группе и очистка списка.
     */
    public void dispose() {
        for (Disposable d : disposables) {
            d.dispose();
        }
        disposables.clear();
    }

    /**
     * Проверка, отменены подписок.
     *
     * @return true, если все подписки отменены
     */
    public boolean isDisposed() {
        return disposables.stream().allMatch(Disposable::isDisposed);
    }
}
