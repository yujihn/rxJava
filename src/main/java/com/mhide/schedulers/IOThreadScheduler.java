package com.mhide.schedulers;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/**
 * Планировщик, использующий кэшированный пул потоков для операций ввода-вывода.
 * Аналогично Schedulers.io() из RxJava.
 */
public class IOThreadScheduler implements Scheduler {
    private static final ExecutorService EXEC = Executors.newCachedThreadPool();

    @Override
    public void schedule(Runnable task) {
        EXEC.submit(task);
    }
}