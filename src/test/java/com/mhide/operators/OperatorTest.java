package com.mhide.operators;

import java.util.List;
import java.util.ArrayList;

import com.mhide.core.Observable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OperatorTest {

    @Test
    void testMapAndFilter() {
        Observable<Integer> src = Observable.create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onNext(3);
            o.onComplete();
        });

        List<Integer> result = new ArrayList<>();
        // сначала map, затем filter
        Observable<Integer> mapped = Map.apply(src, i -> i * 10);
        Observable<Integer> filtered = Filter.apply(mapped, i -> i > 10);
        filtered.subscribe(result::add);

        assertEquals(List.of(20, 30), result);
    }

    @Test
    void testReduce() {
        Observable<Integer> src = Observable.create(o -> {
            o.onNext(2);
            o.onNext(3);
            o.onNext(5);
            o.onComplete();
        });

        List<Integer> out = new ArrayList<>();
        Observable<Integer> reduced = Reduce.apply(src, Integer::sum);
        reduced.subscribe(out::add, Throwable::printStackTrace, () -> {});

        assertEquals(List.of(10), out);
    }

    @Test
    void testMerge() {
        Observable<String> a = Observable.create(o -> {
            o.onNext("A1");
            o.onNext("A2");
            o.onComplete();
        });
        Observable<String> b = Observable.create(o -> {
            o.onNext("B1");
            o.onComplete();
        });

        List<String> merged = new ArrayList<>();
        Observable<String> mergedObs = Merge.apply(a, b);
        mergedObs.subscribe(merged::add, Throwable::printStackTrace, () -> {});

        // Все элементы должны присутствовать
        assertTrue(merged.containsAll(List.of("A1", "A2", "B1")));
        assertEquals(3, merged.size());
    }

    @Test
    void testConcat() {
        Observable<String> a = Observable.create(o -> {
            o.onNext("1");
            o.onComplete();
        });
        Observable<String> b = Observable.create(o -> {
            o.onNext("2");
            o.onComplete();
        });

        List<String> out = new ArrayList<>();
        Observable<String> concatObs = Concatenation.apply(a, b);
        concatObs.subscribe(out::add);

        assertEquals(List.of("1", "2"), out);
    }

    @Test
    void testFlatMap() {
        Observable<Integer> src = Observable.create(o -> {
            o.onNext(1);
            o.onNext(2);
            o.onComplete();
        });

        List<Integer> out = new ArrayList<>();
        // varargs-just:
        Observable<Integer> flat = FlatMap.apply(src, i ->
                Observable.just(i, i * 10)
        );
        flat.subscribe(out::add);

        assertEquals(4, out.size());
        assertTrue(out.containsAll(List.of(1, 10, 2, 20)));
    }
}

