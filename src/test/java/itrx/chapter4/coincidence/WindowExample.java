package itrx.chapter4.coincidence;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Test;

public class WindowExample {

    public void exampleParallel() {
        Observable.merge(
                Observable.range(0, 5)
                          .window(3, 1))
                  .subscribe(System.out::println);

        // 0
        // 1
        // 1
        // 2
        // 2
        // 2
        // 3
        // 3
        // 3
        // 4
        // 4
        // 4
    }


    public void exampleByCount() {
        Observable.range(0, 5)
                  .window(3, 1)
                  .flatMap(o -> o.toList().toObservable())
                  .subscribe(System.out::println);

        // [0, 1, 2]
        // [1, 2, 3]
        // [2, 3, 4]
        // [3, 4]
        // [4]

    }


    public void exampleByTime() {
        Observable.interval(100, TimeUnit.MILLISECONDS)
                  .take(5)
                  .window(250, 100, TimeUnit.MILLISECONDS)
                  .flatMap(o -> o.toList().toObservable())
                  .subscribe(System.out::println);

        // [0, 1]
        // [0, 1, 2]
        // [1, 2, 3]
        // [2, 3, 4]
        // [3, 4]
        // [4]
    }


    public void exampleBySignal() {
        Observable.interval(100, TimeUnit.MILLISECONDS)
                  .take(5)
                  .window(
                          Observable.interval(100, TimeUnit.MILLISECONDS),
                          o -> Observable.timer(250, TimeUnit.MILLISECONDS))
                  .flatMap(o -> o.toList().toObservable())
                  .subscribe(System.out::println);

        // [1, 2]
        // [2, 3]
        // [3, 4]
        // [4]
        // []
    }


    //
    // Tests
    //


    @Test
    public void testParallel() {
        final TestObserver<Integer> tester = Observable.merge(
                Observable.range(0, 5)
                          .window(3, 1)).test();

        tester.assertValues(4);
    }


    @Test
    public void testByCount() {
        TestObserver<List<Integer>> tester = TestObserver.create();

        Observable.range(0, 5)
                  .window(3, 1)
                  .flatMap(o -> o.toList().toObservable()).test();

        tester.assertValueSequence(Arrays.asList(
                Arrays.asList(0, 1, 2),
                Arrays.asList(1, 2, 3),
                Arrays.asList(2, 3, 4),
                Arrays.asList(3, 4),
                Arrays.asList(4)
        ));
    }


    @Test
    public void testByTime() {
        TestObserver<List<Long>> tester = TestObserver.create();
        TestScheduler scheduler = new TestScheduler();

        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
                  .take(5)
                  .window(250, 100, TimeUnit.MILLISECONDS, scheduler)
                  .flatMap(o -> o.toList().toObservable()).test();

        scheduler.advanceTimeTo(500, TimeUnit.MILLISECONDS);
        tester.assertValueSequence(Arrays.asList(
                Arrays.asList(0L, 1L),
                Arrays.asList(0L, 1L, 2L),
                Arrays.asList(1L, 2L, 3L),
                Arrays.asList(2L, 3L, 4L),
                Arrays.asList(3L, 4L),
                Arrays.asList(4L)
        ));
    }


    @Test
    public void testBySignal() {
        TestObserver<List<Long>> tester = TestObserver.create();
        TestScheduler scheduler = new TestScheduler();

        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
                  .take(5)
                  .window(Observable.interval(100, TimeUnit.MILLISECONDS, scheduler),
                          o -> Observable.timer(250, TimeUnit.MILLISECONDS, scheduler))
                  .flatMap(o -> o.toList().toObservable()).test();

        scheduler.advanceTimeTo(500, TimeUnit.MILLISECONDS);
        tester.assertValueSequence(Arrays.asList(
                Arrays.asList(0L, 1L, 2L),
                Arrays.asList(1L, 2L, 3L),
                Arrays.asList(2L, 3L, 4L),
                Arrays.asList(3L, 4L),
                Arrays.asList(4L)
        ));
    }
}
