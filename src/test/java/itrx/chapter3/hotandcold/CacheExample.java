package itrx.chapter3.hotandcold;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Test;
import org.reactivestreams.Subscription;

public class CacheExample {

    public void exampleCache() throws InterruptedException {
        Observable<Long> obs = Observable.interval(100, TimeUnit.MILLISECONDS)
                                         .take(5)
                                         .cache();

        Thread.sleep(500);
        obs.subscribe(i -> System.out.println("First: " + i));
        Thread.sleep(300);
        obs.subscribe(i -> System.out.println("Second: " + i));

        // First: 0
        // First: 1
        // First: 2
        // Second: 0
        // Second: 1
        // Second: 2
        // First: 3
        // Second: 3
        // First: 4
        // Second: 4
    }


    public void exampleCacheUnsubscribe() throws InterruptedException {
        Observable<Long> obs = Observable.interval(100, TimeUnit.MILLISECONDS)
                                         .take(5)
                                         .doOnNext(System.out::println)
                                         .cache()
                                         .doOnSubscribe((disp) -> System.out.println("Subscribed"))
                                         .doOnDispose(() -> System.out.println("Unsubscribed"));

        Disposable subscription = obs.subscribe();
        Thread.sleep(150);
        subscription.dispose();

        // Subscribed
        // 0
        // Unsubscribed
        // 1
        // 2
        // 3
        // 4
    }


    //
    // Tests
    //


    @Test
    public void testCache() throws InterruptedException {
        TestObserver<Long> tester1 = new TestObserver<Long>();
        TestObserver<Long> tester2 = new TestObserver<Long>();
        TestScheduler scheduler = new TestScheduler();

        Observable<Long> obs = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
                                         .take(5)
                                         .cache();

        tester1.assertValues();
        tester2.assertValues();

        scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        obs.subscribe(tester1);
        tester1.assertValues();
        tester2.assertValues();

        scheduler.advanceTimeBy(300, TimeUnit.MILLISECONDS);
        tester1.assertValues(0L, 1L, 2L);
        tester2.assertValues();

        obs.subscribe(tester2);
        tester1.assertValues(0L, 1L, 2L);
        tester2.assertValues(0L, 1L, 2L);

        scheduler.advanceTimeBy(200, TimeUnit.MILLISECONDS);
        tester1.assertValues(0L, 1L, 2L, 3L, 4L);
        tester2.assertValues(0L, 1L, 2L, 3L, 4L);
    }


    @Test
    public void testCacheUnsubscribe() throws InterruptedException {
        TestObserver<Long> tester = new TestObserver<Long>();
        TestScheduler scheduler = new TestScheduler();

        Observable<Long> obs = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
                                         .take(5)
                                         .doOnEach(tester)
                                         .cache();

        Disposable subscription = obs.subscribe();
        scheduler.advanceTimeBy(150, TimeUnit.MILLISECONDS);
        tester.assertValues(0L);

        subscription.dispose();
        scheduler.advanceTimeBy(350, TimeUnit.MILLISECONDS);
        tester.assertValues(0L, 1L, 2L, 3L, 4L);
    }
}
