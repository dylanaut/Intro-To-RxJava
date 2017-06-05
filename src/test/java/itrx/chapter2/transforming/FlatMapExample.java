package itrx.chapter2.transforming;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import itrx.helper.PrintObserver;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

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
        TestObserver<Integer> tester = TestObserver.create();

        Observable<Integer> values = Observable.just(2);

        values.flatMap(i -> Observable.range(0, i)).test();

        tester.assertValues(1);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    //
    // Tests
    //


    @Test
    public void testFlatMapMultipleValues() {
        TestObserver<Integer> tester = TestObserver.create();

        Observable<Integer> values = Observable.range(1, 3);

        values.flatMap(i -> Observable.range(0, i)).test();

        tester.assertValues(2);
        tester.assertComplete();
        tester.assertNoErrors();

    }


    @Test
    public void testFlatMapNewType() {
        TestObserver<Character> tester = TestObserver.create();

        Observable<Integer> values = Observable.just(1);

        values.flatMap(i ->
                Observable.just(
                        Character.valueOf((char) (i + 64))
                )).test();

        tester.assertValues('A');
                tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testFlatMapFilter() {
        TestObserver<Character> tester = TestObserver.create();

        Observable<Integer> values = Observable.range(0, 30);

        values.flatMap(i -> {
            if (0 < i && i <= 26) {
                return Observable.just(Character.valueOf((char) (i + 64)));
            } else {
                return Observable.empty();
            }
        }).test();

        Assert.assertEquals(tester.getEvents().size(), 26);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testFlatMapAsynchronous() {
        TestObserver<Object> tester = TestObserver.create();
        TestScheduler scheduler = new TestScheduler();

        Observable.just(100, 150)
                  .flatMap(i ->
                          Observable.interval(i, TimeUnit.MILLISECONDS, scheduler)
                                    .map(v -> i)
                  )
                  .take(10)
                  .distinctUntilChanged().test();

        scheduler.advanceTimeBy(2, TimeUnit.SECONDS);

        assertTrue(tester.getEvents().size() > 2); // 100 and 150 succeeded each other more than once
        tester.assertNoErrors();
    }
}
