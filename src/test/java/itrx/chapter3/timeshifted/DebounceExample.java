package itrx.chapter3.timeshifted;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Test;

public class DebounceExample {

    public void exampleDebounce() {
        Observable.concat(
                Observable.interval(100, TimeUnit.MILLISECONDS).take(3),
                Observable.interval(500, TimeUnit.MILLISECONDS).take(3),
                Observable.interval(100, TimeUnit.MILLISECONDS).take(3)
        )
                  .scan(0, (acc, v) -> acc + 1)
                  .debounce(150, TimeUnit.MILLISECONDS)
                  .subscribe(System.out::println);

        // 3
        // 4
        // 5
        // 9
    }


    public void exampleDebounceDynamic() {
        Observable.concat(
                Observable.interval(100, TimeUnit.MILLISECONDS).take(3),
                Observable.interval(500, TimeUnit.MILLISECONDS).take(3),
                Observable.interval(100, TimeUnit.MILLISECONDS).take(3)
        )
                  .scan(0, (acc, v) -> acc + 1)
                  .debounce(i -> Observable.timer(i * 50, TimeUnit.MILLISECONDS))
                  .subscribe(System.out::println);

        // 1
        // 3
        // 4
        // 5
        // 9
    }


    public void exampleThrottleWithTimeout() {
        Observable.concat(
                Observable.interval(100, TimeUnit.MILLISECONDS).take(3),
                Observable.interval(500, TimeUnit.MILLISECONDS).take(3),
                Observable.interval(100, TimeUnit.MILLISECONDS).take(3)
        )
                  .scan(0, (acc, v) -> acc + 1)
                  .throttleWithTimeout(150, TimeUnit.MILLISECONDS)
                  .subscribe(System.out::println);

        // 3
        // 4
        // 5
        // 9
    }


    //
    // Test
    //


    @Test
    public void testDebounce() {
        TestScheduler scheduler = new TestScheduler();
        final TestObserver<Integer> tester = Observable.concat(
                Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3),
                Observable.interval(500, TimeUnit.MILLISECONDS, scheduler).take(3),
                Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3)
        )
                                                     .scan(0, (acc, v) -> acc + 1)
                                                     .debounce(150, TimeUnit.MILLISECONDS, scheduler).test();

        scheduler.advanceTimeBy(2100, TimeUnit.MILLISECONDS);
        tester.assertValues(9);
    }


    @Test
    public void testDebounceDynamic() {
        TestScheduler scheduler = new TestScheduler();
        final TestObserver<Integer> tester = Observable.concat(
                Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3),
                Observable.interval(500, TimeUnit.MILLISECONDS, scheduler).take(3),
                Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3)
        )
                                                     .scan(0, (acc, v) -> acc + 1)
                                                     .debounce(i -> Observable.timer(
                                                             i * 50, TimeUnit.MILLISECONDS, scheduler)).test();

        scheduler.advanceTimeBy(2100, TimeUnit.MILLISECONDS);
        tester.assertValues(1, 2, 3, 4, 5, 9);
    }


    @Test
    public void testThrottleWithTimeout() {
        TestScheduler scheduler = new TestScheduler();
        final TestObserver<Integer> tester = Observable.concat(
                Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3),
                Observable.interval(500, TimeUnit.MILLISECONDS, scheduler).take(3),
                Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3)
        )
                                                     .scan(0, (acc, v) -> acc + 1)
                                                     .throttleWithTimeout(150, TimeUnit.MILLISECONDS, scheduler).test();

        scheduler.advanceTimeBy(2100, TimeUnit.MILLISECONDS);
        tester.assertValues(3, 4, 5, 9);
    }
}