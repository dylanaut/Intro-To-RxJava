package itrx.chapter3.timeshifted;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Test;

public class ThrottleExample {

    public void exampleThrottleFirst() {
        Observable.interval(150, TimeUnit.MILLISECONDS)
                  .throttleFirst(1, TimeUnit.SECONDS)
                  .take(3)
                  .subscribe(System.out::println);

        // 0
        // 7
        // 14
    }


    public void exampleThrottleLast() {
        Observable.interval(150, TimeUnit.MILLISECONDS)
                  .throttleLast(1, TimeUnit.SECONDS)
                  .take(3)
                  .subscribe(System.out::println);

        // 5
        // 12
        // 18
    }


    //
    // Test
    //


    @Test
    public void testThrottleFirst() {
        TestScheduler scheduler = new TestScheduler();
        TestObserver<Long> tester = TestObserver.create();

        Observable.interval(150, TimeUnit.MILLISECONDS, scheduler)
                  .throttleFirst(1, TimeUnit.SECONDS, scheduler)
                  .take(3).test();

        scheduler.advanceTimeBy(3, TimeUnit.SECONDS);
        tester.assertValues(0L, 7L, 14L);
    }


    @Test
    public void testThrottleLast() {
        TestScheduler scheduler = new TestScheduler();
        TestObserver<Long> tester = TestObserver.create();

        Observable.interval(150, TimeUnit.MILLISECONDS, scheduler)
                  .throttleLast(1, TimeUnit.SECONDS, scheduler)
                  .take(3).test();

        scheduler.advanceTimeBy(3, TimeUnit.SECONDS);
        tester.assertValues(5L, 12L, 18L);
    }
}
