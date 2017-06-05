package itrx.chapter2.transforming;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import itrx.helper.PrintObserver;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FlatMapExample {

    public void exampleFlatMap() {
        Observable<Integer> values = Observable.just(2);

        values.flatMap(i -> Observable.range(0, i))
              .subscribe(PrintObserver.printObserver("flatMap"));

        // flatMap: 0
        // flatMap: 1
        // flatMap: Completed
    }


    public void exampleFlatMapMultipleValues() {
        Observable<Integer> values = Observable.range(1, 3);

        values.flatMap(i -> Observable.range(0, i))
              .subscribe(PrintObserver.printObserver("flatMap"));

        // flatMap: 0
        // flatMap: 0
        // flatMap: 1
        // flatMap: 0
        // flatMap: 1
        // flatMap: 2
        // flatMap: Completed
    }


    public void exampleFlatMapNewType() {
        Observable<Integer> values = Observable.just(1);

        values.flatMap(i ->
                Observable.just(
                        Character.valueOf((char) (i + 64))
                ))
              .subscribe(PrintObserver.printObserver("flatMap"));

        // flatMap: A
        // flatMap: Completed
    }


    public void exampleFlatMapFilter() {
        Observable<Integer> values = Observable.range(0, 30);

        values.flatMap(i -> {
            if (0 < i && i <= 26) {
                return Observable.just(Character.valueOf((char) (i + 64)));
            } else {
                return Observable.empty();
            }
        })
              .subscribe(PrintObserver.printObserver("flatMap"));

        // flatMap: A
        // flatMap: B
        // flatMap: C
        // ...
        // flatMap: X
        // flatMap: Y
        // flatMap: Z
        // flatMap: Completed
    }


    public void exampleFlatMapAsynchronous() {
        Observable.just(100, 150)
                  .flatMap(i ->
                          Observable.interval(i, TimeUnit.MILLISECONDS)
                                    .map(v -> i)
                  )
                  .take(10)
                  .subscribe(PrintObserver.printObserver("flatMap"));

        // flatMap: 100
        // flatMap: 150
        // flatMap: 100
        // flatMap: 100
        // flatMap: 150
        // flatMap: 100
        // flatMap: 150
        // flatMap: 100
        // flatMap: 100
        // flatMap: 150
        // flatMap: Completed
    }


    @Test
    public void testFlatMap() {
        Observable<Integer> values = Observable.just(2);

        final TestObserver<Integer> tester = values.flatMap(i -> Observable.range(0, i)).test();

        tester.assertValues(1);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    //
    // Tests
    //


    @Test
    public void testFlatMapMultipleValues() {
        Observable<Integer> values = Observable.range(1, 3);

        final TestObserver<Integer> tester = values.flatMap(i -> Observable.range(0, i)).test();

        tester.assertValues(2);
        tester.assertComplete();
        tester.assertNoErrors();

    }


    @Test
    public void testFlatMapNewType() {
        Observable<Integer> values = Observable.just(1);

        final TestObserver<Character> tester = values.flatMap(i ->
                Observable.just(
                        Character.valueOf((char) (i + 64))
                )).test();

        tester.assertValues('A');
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testFlatMapFilter() {
        Observable<Integer> values = Observable.range(0, 30);

        final TestObserver<Character> tester = values.flatMap(i -> {
            if (0 < i && i <= 26) {
                return Observable.just(Character.valueOf((char) (i + 64)));
            } else {
                return Observable.empty();
            }
        }).test();

        tester.assertValueCount(26);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testFlatMapAsynchronous() {
        TestScheduler scheduler = new TestScheduler();

        final TestObserver<Integer> tester = Observable.just(100, 150)
                                                       .flatMap(i ->
                                                               Observable.interval(i, TimeUnit.MILLISECONDS, scheduler)
                                                                         .map(v -> i)
                                                       )
                                                       .take(10)
                                                       .distinctUntilChanged().test();

        scheduler.advanceTimeBy(2, TimeUnit.SECONDS);

        assertThat(tester.valueCount()).isGreaterThan(2); // 100 and 150 succeeded each other more than once
        tester.assertNoErrors();
    }
}
