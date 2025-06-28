package com.mhide.schedulers;

/**
 * Интерфейс планировщика задач.
 */
public interface Scheduler {
    /**
     * Планирование выполнения задачи.
     *
     * @param task Runnable-задание
     */
    void schedule(Runnable task);
}
