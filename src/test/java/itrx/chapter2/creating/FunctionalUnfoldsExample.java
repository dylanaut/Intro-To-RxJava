package itrx.chapter2.creating;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Assert;
import org.junit.Test;

public class FunctionalUnfoldsExample {

    public void exampleRange() {
        Observable<Integer> values = Observable.range(10, 15);
        values.subscribe(System.out::println);

        // 10
        // ...
        // 24
    }


    public void exampleInterval() throws IOException {
        Observable<Long> values = Observable.interval(1000, TimeUnit.MILLISECONDS);
        values.subscribe(
                v -> System.out.println("Received: " + v),
                e -> System.out.println("Error: " + e),
                () -> System.out.println("Completed")
        );
        System.in.read();

        // Received: 0
        // Received: 1
        // Received: 2
        // Received: 3
        // ...
    }


    public void exampleTimer() throws IOException {
        Observable<Long> values = Observable.timer(1, TimeUnit.SECONDS);
        values.subscribe(
                v -> System.out.println("Received: " + v),
                e -> System.out.println("Error: " + e),
                () -> System.out.println("Completed")
        );
        System.in.read();

        // Received: 0
        // Completed
    }


    public void exampleTimerWithRepeat() throws IOException {
        Observable<Long> values = Observable.timer(2, TimeUnit.SECONDS);

        values.subscribe(
                v -> System.out.println("Received: " + v),
                e -> System.out.println("Error: " + e),
                () -> System.out.println("Completed")
        );
        System.in.read();

        // Received: 0
        // Received: 1
        // Received: 2
        // ...
    }


    //
    // Tests
    //


    @Test
    public void testRange() {
        TestObserver<Integer> tester = new TestObserver<Integer>();

        Observable<Integer> values = Observable.range(10, 15);
        values.test();

        tester.assertValues(4);
        tester.assertComplete();
        tester.assertNoErrors();
    }


    @Test
    public void testInterval() {
        TestObserver<Long> tester = new TestObserver<Long>();
        TestScheduler scheduler = new TestScheduler();

        Observable<Long> values = Observable.interval(1000, TimeUnit.MILLISECONDS, scheduler);
        Disposable disposable = values.test();
        scheduler.advanceTimeBy(4500, TimeUnit.MILLISECONDS);

        tester.assertValues(0L, 1L, 2L, 3L);
        tester.assertNoErrors();
        Assert.assertEquals(tester.getEvents().size(), 0);

        disposable.dispose();
    }


    @Test
    public void testTimer() {
        TestObserver<Long> tester = new TestObserver<Long>();
        TestScheduler scheduler = new TestScheduler();

        Observable<Long> values = Observable.timer(1, TimeUnit.SECONDS, scheduler);
        Disposable disposable = values.test();
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);

        tester.assertValues(0L);
        tester.assertNoErrors();
        tester.assertComplete();

        disposable.dispose();
    }


    @Test
    public void testTimerWithRepeat() {
        TestObserver<Long> tester = new TestObserver<Long>();
        TestScheduler scheduler = new TestScheduler();

        Observable<Long> values = Observable.timer(2, TimeUnit.SECONDS, scheduler);
        Disposable disposable = values.test();

        scheduler.advanceTimeBy(6, TimeUnit.SECONDS);

        tester.assertValues(0L, 1L, 2L, 3L, 4L);
        tester.assertNoErrors();
        Assert.assertEquals(tester.getEvents().size(), 0); // Hasn't terminated

        disposable.dispose();
    }
}
