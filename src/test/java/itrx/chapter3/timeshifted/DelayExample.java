package itrx.chapter3.timeshifted;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Test;

public class DelayExample {

    public void exampleDelay() {
        Observable.interval(100, TimeUnit.MILLISECONDS)
                  .delay(i -> Observable.timer(i * 100, TimeUnit.MILLISECONDS))
                  .timeInterval()
                  .take(5)
                  .subscribe(System.out::println);

        // TimeInterval [intervalInMilliseconds=152, value=0]
        // TimeInterval [intervalInMilliseconds=173, value=1]
        // TimeInterval [intervalInMilliseconds=199, value=2]
        // TimeInterval [intervalInMilliseconds=201, value=3]
        // TimeInterval [intervalInMilliseconds=199, value=4]
    }


    public void exampleDelaySubscription() {
        Observable.interval(100, TimeUnit.MILLISECONDS)
                  .delaySubscription(1000, TimeUnit.MILLISECONDS)
                  .timeInterval()
                  .take(5)
                  .subscribe(System.out::println);

        // TimeInterval [intervalInMilliseconds=1114, value=0]
        // TimeInterval [intervalInMilliseconds=92, value=1]
        // TimeInterval [intervalInMilliseconds=101, value=2]
        // TimeInterval [intervalInMilliseconds=100, value=3]
        // TimeInterval [intervalInMilliseconds=99, value=4]
    }


    public void exampleDelaySubscriptionWithSignal() {
        Observable.interval(100, TimeUnit.MILLISECONDS)
                  .delaySubscription(1000, TimeUnit.MILLISECONDS)
                  .timeInterval()
                  .take(5)
                  .subscribe(System.out::println);

        // TimeInterval [intervalInMilliseconds=1114, value=0]
        // TimeInterval [intervalInMilliseconds=92, value=1]
        // TimeInterval [intervalInMilliseconds=101, value=2]
        // TimeInterval [intervalInMilliseconds=100, value=3]
        // TimeInterval [intervalInMilliseconds=99, value=4]
    }


    //
    // Tests
    //


    @Test
    public void testDelay() {
        TestScheduler scheduler = new TestScheduler();
        TestObserver<Long> tester = TestObserver.create();

        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
                  .delay(i -> Observable.timer(i * 100, TimeUnit.MILLISECONDS, scheduler))
                  .timeInterval(scheduler)
                  .map(i -> i.time(TimeUnit.MILLISECONDS))
                  .take(5).test();

        scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
        tester.assertValues(100L, 200L, 200L, 200L, 200L);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testDelaySubscription() {
        TestScheduler scheduler = new TestScheduler();
        final TestObserver<Long> test = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
                                                  .delaySubscription(1000, TimeUnit.MILLISECONDS, scheduler)
                                                  .timeInterval(scheduler)
                                                  .take(5)
                                                  .map(i -> i.time(TimeUnit.MILLISECONDS)).test();

        scheduler.advanceTimeBy(1500, TimeUnit.MILLISECONDS);
        test.assertValues(1100L, 100L, 100L, 100L, 100L);
        test.assertComplete();
        test.assertNoErrors();
    }


    @Test
    public void testDelaySubscriptionWithSignal() {
        TestScheduler scheduler = new TestScheduler();

        final TestObserver<Long> tester =
                Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
                          .delaySubscription(1000, TimeUnit.MILLISECONDS, scheduler)
                          .timeInterval(scheduler)
                          .take(5)
                          .map(i -> i.time(TimeUnit.MILLISECONDS)).test();

        scheduler.advanceTimeBy(1500, TimeUnit.MILLISECONDS);
        tester.assertValues(1100L, 100L, 100L, 100L, 100L);
        tester.assertComplete();
        tester.assertNoErrors();
    }
}
