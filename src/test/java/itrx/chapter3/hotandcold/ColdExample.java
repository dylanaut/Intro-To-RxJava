package itrx.chapter3.hotandcold;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.Test;

public class ColdExample {
    public void example() throws InterruptedException {
        Observable<Long> cold =
                Observable.interval(200, TimeUnit.MILLISECONDS)
                          .take(5);

        cold.subscribe(i -> System.out.println("First: " + i));
        Thread.sleep(500);
        cold.subscribe(i -> System.out.println("Second: " + i));

        // First: 0
        // First: 1
        // First: 2
        // Second: 0
        // First: 3
        // Second: 1
        // First: 4
        // Second: 2
        // Second: 3
        // Second: 4
    }


    @Test
    public void test() {
        TestScheduler scheduler = new TestScheduler();
        TestObserver<Long> tester1 = TestObserver.create();
        TestObserver<Long> tester2 = TestObserver.create();

        Observable<Long> cold =
                Observable.interval(200, TimeUnit.MILLISECONDS, scheduler)
                          .take(5);

        cold.subscribe(tester1);
        tester1.assertValues();
        tester2.assertValues();

        scheduler.advanceTimeTo(500, TimeUnit.MILLISECONDS);
        cold.subscribe(tester2);
        tester1.assertValues(0L, 1L);
        tester2.assertValues();

        scheduler.advanceTimeTo(1000, TimeUnit.MILLISECONDS);
        tester1.assertValues(0L, 1L, 2L, 3L, 4L);
        tester2.assertValues(0L, 1L);

        scheduler.advanceTimeTo(1500, TimeUnit.MILLISECONDS);
        tester1.assertValues(0L, 1L, 2L, 3L, 4L);
        tester1.assertValues(0L, 1L, 2L, 3L, 4L);
    }
}
