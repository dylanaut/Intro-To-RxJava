package itrx.chapter3.combining;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Assert;
import org.junit.Test;

public class SwitchOnNextExample {

    public void example() {
        Observable.switchOnNext(
                Observable.interval(100, TimeUnit.MILLISECONDS)
                          .map(i ->
                                  Observable.interval(30, TimeUnit.MILLISECONDS)
                                            .map(i2 -> i)
                          )
        )
                  .take(9)
                  .subscribe(System.out::println);

        // 0
        // 0
        // 0
        // 1
        // 1
        // 1
        // 2
        // 2
        // 2
    }


    //
    // Test
    //


    @Test
    public void test() {
        TestScheduler scheduler = new TestScheduler();

        final TestObserver<Long> tester = Observable.switchOnNext(
                Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
                          .map(i ->
                                  Observable.interval(30, TimeUnit.MILLISECONDS, scheduler)
                                            .map(i2 -> i)
                          )
        ).distinctUntilChanged().test();

        scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        tester.assertValues(0L, 1L, 2L, 3L);
        tester.assertNoErrors();
        Assert.assertEquals(tester.getEvents().size(), 0);
    }
}
