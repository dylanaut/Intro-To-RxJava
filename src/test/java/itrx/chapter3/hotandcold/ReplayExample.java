package itrx.chapter3.hotandcold;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Test;

public class ReplayExample {

    public void exampleReplay() throws InterruptedException {
        ConnectableObservable<Long> cold = Observable.interval(200, TimeUnit.MILLISECONDS).replay();
        cold.connect();

        System.out.println("Subscribe first");
        cold.subscribe(i -> System.out.println("First: " + i));
        Thread.sleep(700);
        System.out.println("Subscribe second");
        cold.subscribe(i -> System.out.println("Second: " + i));
        Thread.sleep(500);

        // Subscribe first
        // First: 0
        // First: 1
        // First: 2
        // Subscribe second
        // Second: 0
        // Second: 1
        // Second: 2
        // First: 3
        // Second: 3
    }


    public void exampleReplayWithBufferSize() throws InterruptedException {
        ConnectableObservable<Long> source = Observable.interval(1000, TimeUnit.MILLISECONDS)
                                                       .take(5)
                                                       .replay(2);

        source.connect();
        Thread.sleep(4500);
        source.subscribe(System.out::println);

        // 2
        // 3
        // 4
    }


    public void exampleReplayWithTime() throws InterruptedException {
        ConnectableObservable<Long> source = Observable.interval(1000, TimeUnit.MILLISECONDS)
                                                       .take(5)
                                                       .replay(2000, TimeUnit.MILLISECONDS);

        source.connect();
        Thread.sleep(4500);
        source.subscribe(System.out::println);

        // 2
        // 3
        // 4
    }


    //
    // Test
    //


    @Test
    public void testReplay() throws InterruptedException {
        TestScheduler scheduler = new TestScheduler();
        TestObserver<Long> tester1 = TestObserver.create();
        TestObserver<Long> tester2 = TestObserver.create();

        ConnectableObservable<Long> cold =
                Observable.interval(200, TimeUnit.MILLISECONDS, scheduler)
                          .replay();
        Disposable connection = cold.connect();

        cold.subscribe(tester1);
        scheduler.advanceTimeBy(700, TimeUnit.MILLISECONDS);
        tester1.assertValues(0L, 1L, 2L);
        tester2.assertValues();

        cold.subscribe(tester2);
        tester1.assertValues(0L, 1L, 2L);
        tester2.assertValues(0L, 1L, 2L);
        scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        tester1.assertValues(0L, 1L, 2L, 3L, 4L, 5L);
        tester2.assertValues(0L, 1L, 2L, 3L, 4L, 5L);

        connection.dispose();
    }


    @Test
    public void testReplayWithBufferSize() {
        TestScheduler scheduler = new TestScheduler();
        ConnectableObservable<Long> source = Observable.interval(1000, TimeUnit.MILLISECONDS, scheduler)
                                                       .take(5)
                                                       .replay(2, scheduler);

        source.connect();
        scheduler.advanceTimeBy(4500, TimeUnit.MILLISECONDS);
        final TestObserver<Long> tester = source.test();
        scheduler.triggerActions();
        tester.assertValues(2L, 3L);
        scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        tester.assertValues(2L, 3L, 4L);
    }


    @Test
    public void testReplayWithTime() throws InterruptedException {
        TestScheduler scheduler = new TestScheduler();
        ConnectableObservable<Long> source = Observable.interval(1000, TimeUnit.MILLISECONDS, scheduler)
                                                       .take(5)
                                                       .replay(2000, TimeUnit.MILLISECONDS, scheduler);

        source.connect();
        scheduler.advanceTimeBy(4500, TimeUnit.MILLISECONDS);
        final TestObserver<Long> tester = source.test();
        tester.assertValues(2L, 3L);
        scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        tester.assertValues(2L, 3L, 4L);

//		2
//		3
//		4
    }
}
