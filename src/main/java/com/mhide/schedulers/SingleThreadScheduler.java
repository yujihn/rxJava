package com.mhide.schedulers;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/**
 * Планировщик единственного потока.
 */
public class SingleThreadScheduler implements Scheduler {
    private static final ExecutorService EXEC = Executors.newSingleThreadExecutor();

    @Override
    public void schedule(Runnable task) {
        EXEC.submit(task);
    }
}