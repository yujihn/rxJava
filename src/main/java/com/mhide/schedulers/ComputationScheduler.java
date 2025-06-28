package com.mhide.schedulers;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/**
 * Планировщик выполнения вычислительных задач с фиксированным числом потоков, равным количеству ядер CPU.
 */
public class ComputationScheduler implements Scheduler {
    private static final int N = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService EXEC = Executors.newFixedThreadPool(N);

    @Override
    public void schedule(Runnable task) {
        EXEC.submit(task);
    }
}
