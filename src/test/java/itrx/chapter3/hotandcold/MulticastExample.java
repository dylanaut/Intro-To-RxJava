package itrx.chapter3.hotandcold;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Test;

public class MulticastExample {

    public void exampleMutlicast() throws InterruptedException {
        Observable<Long> cold =
                Observable.interval(200, TimeUnit.MILLISECONDS)
                          .publish()
                          .refCount();

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


    @Test
    public void testRefcount() throws InterruptedException {
        TestScheduler scheduler = new TestScheduler();
        TestObserver<Long> tester1 = TestObserver.create();
        TestObserver<Long> tester2 = TestObserver.create();
        TestObserver<Long> tester3 = TestObserver.create();

        Observable<Long> cold =
                Observable.interval(200, TimeUnit.MILLISECONDS, scheduler)
                          .share();

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

        scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        tester1.assertValues(0L, 1L, 2L, 3L, 4L, 5L, 6L);
        tester2.assertValues(2L, 3L, 4L);
        tester3.assertValues();

        cold.subscribe(tester3);
        scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        tester1.assertValues(0L, 1L, 2L, 3L, 4L, 5L, 6L);
        tester2.assertValues(2L, 3L, 4L);
        tester3.assertValues(0L, 1L);
    }
}
