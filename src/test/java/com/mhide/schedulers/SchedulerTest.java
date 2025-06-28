package com.mhide.schedulers;

import com.mhide.core.Observable;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class SchedulerTest {

    @Test
    void testIoThreadSchedulerDoesNotBlock() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> threadName = new AtomicReference<>();

        Observable.just("X")
                .subscribeOn(new IOThreadScheduler())
                .subscribe(item -> {
                    threadName.set(Thread.currentThread().getName());
                    latch.countDown();
                });

        assertTrue(latch.await(1, TimeUnit.SECONDS));
        assertTrue(threadName.get().contains("pool"));
    }

    @Test
    void testSingleThreadSchedulerSequential() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);
        AtomicReference<String> first = new AtomicReference<>();
        AtomicReference<String> second = new AtomicReference<>();

        Observable.just("a")
                .observeOn(new SingleThreadScheduler())
                .subscribe(item -> {
                    first.set(Thread.currentThread().getName());
                    latch.countDown();
                });

        Observable.just("b")
                .observeOn(new SingleThreadScheduler())
                .subscribe(item -> {
                    second.set(Thread.currentThread().getName());
                    latch.countDown();
                });

        assertTrue(latch.await(1, TimeUnit.SECONDS));
        assertEquals(first.get(), second.get());
    }
}


