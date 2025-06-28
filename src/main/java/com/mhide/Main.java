package com.mhide;

import com.mhide.operators.*;
import com.mhide.core.Observable;
import com.mhide.schedulers.IOThreadScheduler;
import com.mhide.schedulers.ComputationScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {

        Observable<Integer> source = Observable.just(1, 2, 3, 4, 5);
        logger.info("Пример map и filter с планировщиками:");

        Filter.apply(
                        Map.apply(source, i -> i * 10),
                        i -> i >= 30
                )
                .subscribeOn(new IOThreadScheduler())
                .observeOn(new ComputationScheduler())
                .subscribe(
                        i -> logger.info("Received: {}", i),
                        throwable -> logger.error("Ошибка в подписке", throwable),
                        () -> logger.info("Completed\n")
                );

        Thread.sleep(500);

        logger.info("Пример flatMap:");
        FlatMap.apply(source, i ->
                Observable.just(i, i * i)
        ).subscribe(i -> logger.info("flatMap: {}", i));

        logger.info("\nПример merge:");
        Merge.apply(
                Observable.just("A", "B"),
                Observable.just("1", "2")
        ).subscribe(s -> logger.info("merge: {}", s));

        logger.info("\nПример concatenation:");
        Concatenation.apply(
                Observable.just("X", "Y"),
                Observable.just("Z")
        ).subscribe(s -> logger.info("concatenation: {}", s));
    }
}