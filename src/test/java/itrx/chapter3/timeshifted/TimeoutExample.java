package itrx.chapter3.timeshifted;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

public class TimeoutExample {

    public void exampleTimeout() {
        Observable.concat(
                Observable.interval(100, TimeUnit.MILLISECONDS).take(3),
                Observable.interval(500, TimeUnit.MILLISECONDS).take(3),
                Observable.interval(100, TimeUnit.MILLISECONDS).take(3)
        )
                  .scan(0, (acc, v) -> acc + 1)
                  .timeout(200, TimeUnit.MILLISECONDS)
                  .subscribe(
                          System.out::println,
                          System.out::println);

        // 0
        // 1
        // 2
        // 3
        // java.util.concurrent.TimeoutException
    }


    public void exampleTimeoutWithResume() {
        Observable.concat(
                Observable.interval(100, TimeUnit.MILLISECONDS).take(3),
                Observable.interval(500, TimeUnit.MILLISECONDS).take(3),
                Observable.interval(100, TimeUnit.MILLISECONDS).take(3)
        )
                  .scan(0, (acc, v) -> acc + 1)
                  .timeout(200, TimeUnit.MILLISECONDS, Observable.just(-1))
                  .subscribe(
                          System.out::println,
                          System.out::println);

        // 0
        // 1
        // 2
        // 3
        // -1
    }


    public void exampleTimeoutPerItem() {
        Observable.concat(
                Observable.interval(100, TimeUnit.MILLISECONDS).take(3),
                Observable.interval(500, TimeUnit.MILLISECONDS).take(3),
                Observable.interval(100, TimeUnit.MILLISECONDS).take(3)
        )
                  .scan(0, (acc, v) -> acc + 1)
                  .timeout(i -> Observable.timer(200, TimeUnit.MILLISECONDS))
                  .subscribe(
                          System.out::println,
                          System.out::println);

        // 0
        // 1
        // 2
        // 3
        // java.util.concurrent.TimeoutException
    }


    public void exampleTimeoutPerItemWithResume() {
        Observable.concat(
                Observable.interval(100, TimeUnit.MILLISECONDS).take(3),
                Observable.interval(500, TimeUnit.MILLISECONDS).take(3),
                Observable.interval(100, TimeUnit.MILLISECONDS).take(3)
        )
                  .scan(0, (acc, v) -> acc + 1)
                  .timeout(i -> Observable.timer(200, TimeUnit.MILLISECONDS), Observable.just(-1))
                  .subscribe(
                          System.out::println,
                          System.out::println);

        // 0
        // 1
        // 2
        // 3
        // -1
    }


    //
    // Tests
    //


    @Test
    public void testTimeout() {
        TestScheduler scheduler = new TestScheduler();
        TestObserver<Integer> tester = TestObserver.create();

        Observable.concat(
                Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3),
                Observable.interval(500, TimeUnit.MILLISECONDS, scheduler).take(3),
                Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3)
        )
                  .scan(0, (acc, v) -> acc + 1)
                  .timeout(200, TimeUnit.MILLISECONDS, scheduler).test();

        scheduler.advanceTimeBy(2300, TimeUnit.MILLISECONDS);
        tester.assertValues(3);
        Assert.assertEquals("Observable emits one error",
                1, tester.errorCount());
        assertThat("Observable times out",
                tester.errors().get(0),
                instanceOf(TimeoutException.class));
    }


    @Test
    public void testTimeoutWithResume() {
        TestScheduler scheduler = new TestScheduler();
        TestObserver<Integer> tester = TestObserver.create();

        Observable.concat(
                Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3),
                Observable.interval(500, TimeUnit.MILLISECONDS, scheduler).take(3),
                Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3))
                  .scan(0, (acc, v) -> acc + 1)
                  .timeout(200, TimeUnit.MILLISECONDS, scheduler, Observable.just(-1)).test();

        scheduler.advanceTimeBy(2300, TimeUnit.MILLISECONDS);
        tester.assertValues(1);
    }


    @Test
    public void testTimeoutPerItem() {
        TestScheduler scheduler = new TestScheduler();
        TestObserver<Integer> tester = TestObserver.create();

        Observable.concat(
                Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3),
                Observable.interval(500, TimeUnit.MILLISECONDS, scheduler).take(3),
                Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3)
        )
                  .scan(0, (acc, v) -> acc + 1)
                  .timeout(i -> Observable.timer(200, TimeUnit.MILLISECONDS, scheduler)).test();

        scheduler.advanceTimeBy(2300, TimeUnit.MILLISECONDS);
        tester.assertValues(3);
        Assert.assertEquals("Observable emits one error",
                1, tester.errorCount());
        assertThat("Observable times out",
                tester.errors().get(0),
                instanceOf(TimeoutException.class));
    }


    @Test
    public void testTimeoutPerItemWithResume() {
        TestScheduler scheduler = new TestScheduler();
        TestObserver<Integer> tester = TestObserver.create();

        Observable.concat(
                Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3),
                Observable.interval(500, TimeUnit.MILLISECONDS, scheduler).take(3),
                Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(3)
        )
                  .scan(0, (acc, v) -> acc + 1)
                  .timeout(i -> Observable.timer(200, TimeUnit.MILLISECONDS, scheduler), Observable.just(-1)).test();

        scheduler.advanceTimeBy(2300, TimeUnit.MILLISECONDS);
        tester.assertValues(1);
    }
}
