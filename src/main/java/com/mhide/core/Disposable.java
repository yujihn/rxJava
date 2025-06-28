package com.mhide.core;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Механизм отмены подписки.
 */
public class Disposable {
    private final AtomicBoolean disposed = new AtomicBoolean(false);

    /**
     * Отмена подписки и прекращение получения событий.
     */
    public void dispose() {
        disposed.set(true);
    }

    /**
     * Проверка отменены.
     *
     * @return true, если подписка отменена
     */
    public boolean isDisposed() {
        return disposed.get();
    }
}
