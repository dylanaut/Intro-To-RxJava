package itrx.chapter3.hotandcold;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Test;
import org.reactivestreams.Subscription;

public class ConnectableObservableExample {

    public void exampleConnect() throws InterruptedException {
        ConnectableObservable<Long> cold = Observable.interval(200, TimeUnit.MILLISECONDS).publish();
        cold.connect();

        cold.subscribe(i -> System.out.println("First: " + i));
        Thread.sleep(500);
        cold.subscribe(i -> System.out.println("Second: " + i));

        // First: 0
        // First: 1
        // First: 2
        // Second: 2
        // First: 3
        // Second: 3
        // First: 4
        // Second: 4
        // First: 5
        // Second: 5
    }


    public void exampleDisconnect() throws InterruptedException {
        ConnectableObservable<Long> connectable = Observable.interval(200, TimeUnit.MILLISECONDS).publish();
        Disposable disposable = connectable.connect();

        connectable.subscribe(i -> System.out.println(i));

        Thread.sleep(1000);
        System.out.println("Closing connection");
        disposable.dispose();

        Thread.sleep(1000);
        System.out.println("Reconnecting");
        disposable = connectable.connect();

        // 0
        // 1
        // 2
        // 3
        // 4
        // Closing connection
        // Reconnecting
        // 0
        // 1
        // 2
    }


    public void exampleUnsubscribe() throws InterruptedException {
        ConnectableObservable<Long> connectable = Observable.interval(200, TimeUnit.MILLISECONDS).publish();
        connectable.connect();

        connectable.subscribe(i -> System.out.println("First: " + i));
        Thread.sleep(500);
        Disposable s2 = connectable.subscribe(i -> System.out.println("Seconds: " + i));

        Thread.sleep(500);
        System.out.println("Unsubscribing second");
        s2.dispose();

        // First: 0
        // First: 1
        // First: 2
        // Seconds: 2
        // First: 3
        // Seconds: 3
        // First: 4
        // Seconds: 4
        // Unsubscribing second
        // First: 5
        // First: 6
    }


    public void exampleRefcount() throws InterruptedException {
        Observable<Long> cold = Observable.interval(200, TimeUnit.MILLISECONDS).publish().refCount();

        Disposable s1 = cold.subscribe(i -> System.out.println("First: " + i));
        Thread.sleep(500);
        Disposable s2 = cold.subscribe(i -> System.out.println("Second: " + i));
        Thread.sleep(500);
        System.out.println("Unsubscribe first");
        s2.dispose();
        Thread.sleep(500);
        System.out.println("Unsubscribe first");
        s1.dispose();

        System.out.println("First connection again");
        Thread.sleep(500);
        s1 = cold.subscribe(i -> System.out.println("First: " + i));

        // First: 0
        // First: 1
        // First: 2
        // Second: 2
        // First: 3
        // Second: 3
        // Unsubscribe first
        // First: 4
        // First: 5
        // First: 6
        // Unsubscribe first
        // First connection again
        // First: 0
        // First: 1
        // First: 2
        // First: 3
        // First: 4
    }


    //
    // Test
    //


    @Test
    public void testConnect() throws InterruptedException {
        TestScheduler scheduler = new TestScheduler();
        TestObserver<Long> tester1 = new TestObserver<Long>();
        TestObserver<Long> tester2 = new TestObserver<Long>();

        ConnectableObservable<Long> cold =
                Observable.interval(200, TimeUnit.MILLISECONDS, scheduler)
                          .publish();
        Disposable connection = cold.connect();

        cold.subscribe(tester1);
        scheduler.advanceTimeTo(500, TimeUnit.MILLISECONDS);
        tester1.assertValues(0L, 1L);
        tester2.assertValues();

        cold.subscribe(tester2);
        scheduler.advanceTimeTo(1000, TimeUnit.MILLISECONDS);
        tester1.assertValues(0L, 1L, 2L, 3L, 4L);
        tester2.assertValues(2L, 3L, 4L);

        connection.dispose();
    }


    @Test
    public void testDisconnect() throws InterruptedException {
        TestScheduler scheduler = new TestScheduler();
        TestObserver<Long> tester = new TestObserver<Long>();

        ConnectableObservable<Long> connectable =
                Observable.interval(200, TimeUnit.MILLISECONDS, scheduler)
                          .publish();
        Disposable connection = connectable.connect();
        connectable
.test();

        scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
        tester.assertValues(0L, 1L, 2L, 3L, 4L);

        connection.dispose();
        scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
        tester.assertValues(0L, 1L, 2L, 3L, 4L);

        connection = connectable.connect();
        scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
        tester.assertValues(0L, 1L, 2L, 3L, 4L, 0L, 1L, 2L, 3L, 4L);

        connection.dispose();
    }


    @Test
    public void testUnsubscribe() throws InterruptedException {
        TestScheduler scheduler = new TestScheduler();
        TestObserver<Long> tester1 = new TestObserver<Long>();
        TestObserver<Long> tester2 = new TestObserver<Long>();

        ConnectableObservable<Long> connectable =
                Observable.interval(200, TimeUnit.MILLISECONDS, scheduler)
                          .publish();
        Disposable conSubscription = connectable.connect();

        connectable.subscribe(tester1);
        scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        tester1.assertValues(0L, 1L);
        tester2.assertValues();

        connectable.subscribe(tester2);
        scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        tester1.assertValues(0L, 1L, 2L, 3L, 4L);
        tester2.assertValues(2L, 3L, 4L);

        tester2.dispose();
        scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        tester1.assertValues(0L, 1L, 2L, 3L, 4L, 5L, 6L);
        tester2.assertValues(2L, 3L, 4L);

        conSubscription.dispose();
    }


    @Test
    public void testRefcount() throws InterruptedException {
        TestScheduler scheduler = new TestScheduler();
        TestObserver<Long> tester1 = new TestObserver<Long>();
        TestObserver<Long> tester2 = new TestObserver<Long>();
        TestObserver<Long> tester3 = new TestObserver<Long>();

        Observable<Long> cold =
                Observable.interval(200, TimeUnit.MILLISECONDS, scheduler)
                          .publish()
                          .refCount();

        cold.subscribe(tester1);
        scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        tester1.assertValues(0L, 1L);
        tester2.assertValues();
        tester3.assertValues();

        cold.subscribe(tester2);
        scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        tester1.assertValues(0L, 1L, 2L, 3L, 4L);
        tester2.assertValues(2L, 3L, 4L);
        tester3.assertValues();

        tester2.dispose();
        scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        tester1.assertValues(0L, 1L, 2L, 3L, 4L, 5L, 6L);
        tester2.assertValues(2L, 3L, 4L);
        tester3.assertValues();

        tester1.dispose();

        cold.subscribe(tester3);
        scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        tester1.assertValues(0L, 1L, 2L, 3L, 4L, 5L, 6L);
        tester2.assertValues(2L, 3L, 4L);
        tester3.assertValues(0L, 1L);

        tester3.dispose();
    }
}
